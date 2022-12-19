package com.github.alenfive.datajellyfish.service.data;

import com.github.alenfive.datajellyfish.entity.*;
import com.github.alenfive.datajellyfish.service.DataOperateService;
import com.github.alenfive.datajellyfish.service.RedisService;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.TextIndexDefinition;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MongoDataOperateService implements DataOperateService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedisService redisService;

    @Override
    public void saveConsumer(Consumer consumer) {
        consumer.setUpdateTime(new Date());
        if (consumer.getId() == null){
            consumer.setCreateTime(new Date());
            consumer.setId(ObjectId.get());
        }
        //为表建立全文检索
        mongoTemplate.indexOps(consumer.getTable()).ensureIndex(TextIndexDefinition.builder().onAllFields().build());
        mongoTemplate.save(consumer);
    }

    @Override
    public Consumer getConsumerById(String id) {
        return mongoTemplate.findOne(Query.query(Criteria.where("_id").is(new ObjectId(id))),Consumer.class);
    }

    @Override
    public Consumer getConsumerByTable(String table) {
        return mongoTemplate.findOne(Query.query(Criteria.where("table").is(table)),Consumer.class);
    }

    @Override
    public void deleteConsumer(String id) {
        mongoTemplate.remove(Query.query(Criteria.where("_id").is(new ObjectId(id))),Consumer.class);
    }

    @Override
    public Pager listConsumer(Pager pager, String topicId) {
        Criteria criteria = new Criteria();

        if (!StringUtils.isEmpty(topicId)){
            criteria.and("topicId").is(new ObjectId(topicId));
        }

        Query query = Query.query(criteria);
        Long count = mongoTemplate.count(query,Consumer.class);
        List<Consumer> list = mongoTemplate.find(query.skip(pager.getIndex()).limit(pager.getPageSize()),Consumer.class);
        for (Consumer consumer : list){
            consumer.setTopicName(getTopic(consumer.getTopicId().toHexString()).getName());
        }
        pager.setTotalRecords(count);
        pager.setData(list);
        return pager;
    }
    @Override
    public List<Consumer> findConsumerByTopic(String topicId) {
        return mongoTemplate.find(Query.query(Criteria.where("topicId").is(new ObjectId(topicId))),Consumer.class);
    }

    @Override
    public List<Consumer> listConsumerAll() {
        return mongoTemplate.findAll(Consumer.class);
    }

    @Override
    public List<Topic> selectTopic() {
        Query query= new Query();
        query.fields().include("_id").include("name");
        return mongoTemplate.find(query,Topic.class);
    }

    @Override
    public Pager listTopic(Pager pager) {
        Criteria criteria = new Criteria();
        Query query = Query.query(criteria);
        Long count = mongoTemplate.count(query,Topic.class);
        List<Topic> list = mongoTemplate.find(query.skip(pager.getIndex()).limit(pager.getPageSize()),Topic.class);
        pager.setTotalRecords(count);
        pager.setData(list);
        return pager;
    }

    @Override
    public List<Topic> listTopicAll() {
        return mongoTemplate.findAll(Topic.class);
    }

    @Override
    public Topic getTopic(String id) {
        return mongoTemplate.findOne(Query.query(Criteria.where("_id").is(new ObjectId(id))),Topic.class);
    }

    @Override
    public void updateTopicStatus(Topic topic) {
        Query query = Query.query(Criteria.where("_id").is(topic.getId()));
        Update update = Update.update("status",topic.getStatus());
        mongoTemplate.upsert(query,update,Topic.class);
    }

    @Override
    public void saveTopic(Topic topic) {
        topic.setUpdateTime(new Date());
        if (topic.getId() == null){
            topic.setCreateTime(new Date());
            topic.setId(ObjectId.get());
        }
        mongoTemplate.save(topic);
    }

    @Override
    public void deleteTopic(String id) {
        mongoTemplate.remove(Query.query(Criteria.where("_id").is(new ObjectId(id))),Topic.class);
    }

    @Override
    public void updateOffset(String topicId, String shardingTopicKey, String oldOffset,String newOffset,String newUId) {
        Query query = Query.query(Criteria.where("_id").is(new ObjectId(topicId)).and("shardings").elemMatch(
                Criteria.where("key").is(shardingTopicKey).and("offset").is(oldOffset)
        ));
        Update update = Update.update("shardings.$.offset",newOffset).set("shardings.$.uId",newUId).set("updateTime",new Date());
        mongoTemplate.updateFirst(query,update,Topic.class);
    }

    @Override
    public Map<String, Object> stats(String table) {
        Map<String,Object> stats = null;
        stats = mongoTemplate.executeCommand("{collStats:\""+table+"\"}");

        Double storageSize = Double.valueOf(stats.get("storageSize").toString());

        String suffix = "";
        if( (storageSize = storageSize/1024.0) <1024 ){
            suffix = " KB";
        }else if( (storageSize = storageSize/1024.0) <1024 ){
            suffix = " MB";
        }else if( (storageSize = storageSize/1024.0) <1024 ){
            suffix = " GB";
        }else if( (storageSize = storageSize/1024.0) <1024 ){
            suffix = " TB";
        }

        String storageSizeStr = (Math.round(storageSize*100)/100)+suffix;

        Map<String,Object> result = new HashMap<>();
        result.put("storageSize",storageSizeStr);
        result.put("recordSize",stats.get("count"));

        Consumer consumer = mongoTemplate.findOne(Query.query(Criteria.where("table").is(table)),Consumer.class);
        result.put("successfulSize",mongoTemplate.count(Query.query(Criteria.where("isSync").is(1)),table));
        result.put("failedSize",mongoTemplate.count(Query.query(Criteria.where("isSync").is(0).and("times").gte(consumer.getMaxTimes())),table));
        result.put("waitSize",mongoTemplate.count(Query.query(Criteria.where("isSync").is(0).and("times").lt(consumer.getMaxTimes())),table));

        return result;
    }

    @Override
    public Map<String, Object> status() {
        Query query = Query.query(Criteria.where("status").is(1));
        query.fields()
                .include("_id")
                .include("table")
                .include("maxTimes")
                .include("shardings");
        List<Consumer> tableList = mongoTemplate.find(query,Consumer.class);

        query = Query.query(Criteria.where("status").is(1));
        query.fields()
                .include("_id")
                .include("shardings");

        List<Topic> topicList = mongoTemplate.find(query,Topic.class);

        Double storageSize = 0D;
        Long recordSize = 0L;
        Long successfulSize = 0L;
        Long failedSize = 0L;
        Long waitSize = 0L;
        Long consumerTaskSize = 0L;
        Long consumerRuningTaskSize = redisTemplate.opsForZSet().size("rocket-ssc:consumer:queue:pending");
        Long topicTaskSize = 0L;
        Long topicRuningTaskSize = redisTemplate.opsForZSet().size("rocket-ssc:producer:queue:pending");

        for (Consumer item : tableList){
            consumerTaskSize += item.getShardings().size();
            String table = item.getTable();
            Map<String,Object> stats = null;
            try {
                stats = mongoTemplate.executeCommand("{collStats:\""+table+"\"}");
            }catch (Exception e){
                continue;
            }
            storageSize += Long.valueOf(stats.get("storageSize").toString());
            recordSize += Long.valueOf(stats.get("count").toString());

            successfulSize += mongoTemplate.count(Query.query(Criteria.where("isSync").is(1)),table);
            failedSize += mongoTemplate.count(Query.query(Criteria.where("isSync").is(0).and("times").gte(item.getMaxTimes())),table);
            waitSize += mongoTemplate.count(Query.query(Criteria.where("isSync").is(0).and("times").lt(item.getMaxTimes())),table);
        }

        for(Topic item : topicList){
            topicTaskSize += item.getShardings().size();
        }

        String suffix = "";
        if( (storageSize = storageSize/1024.0) <1024 ){
            suffix = " KB";
        }else if( (storageSize = storageSize/1024.0) <1024 ){
            suffix = " MB";
        }else if( (storageSize = storageSize/1024.0) <1024 ){
            suffix = " GB";
        }else if( (storageSize = storageSize/1024.0) <1024 ){
            suffix = " TB";
        }

        String storageSizeStr = (Math.round(storageSize*100)/100)+suffix;

        Map<String,Object> result = new HashMap<>();
        result.put("storageSize",storageSizeStr);
        result.put("recordSize",recordSize);
        result.put("successfulSize",successfulSize);
        result.put("failedSize",failedSize);
        result.put("waitSize",waitSize);
        result.put("consumerTaskSize",consumerTaskSize);
        result.put("consumerRuningTaskSize",consumerRuningTaskSize);
        result.put("topicTaskSize",topicTaskSize);
        result.put("topicRuningTaskSize",topicRuningTaskSize);

        return result;
    }

    @Override
    public Map<String, Object> status(String table) {
        Query query = new Query().with(Sort.by(Sort.Order.desc("_id"))).limit(1);
        return mongoTemplate.findOne(query,Map.class,table);
    }

    @Override
    public List<TopicRecord> findTopicRecord(Consumer consumer, ShardingConsumer shardingConsumer) {
        Criteria criteria = Criteria.where("nextTime").lte(new Date()).and("isSync").is(0).and("times").lt(consumer.getMaxTimes());
        Document document = criteria.getCriteriaObject();

        buildContentFilter(document,shardingConsumer.getFilter());

        Query query = new BasicQuery(document).limit(100);
        return mongoTemplate.find(query,TopicRecord.class,consumer.getTable());
    }

    @Override
    public void saveTopicRecord(TopicRecord topicRecord, String table) {
        mongoTemplate.save(topicRecord,table);
    }

    @Override
    public boolean existsTopicRecord(String uId, String table) {
        return mongoTemplate.exists(Query.query(Criteria.where("uId").is(uId)),table);
    }

    @Override
    public Long resetRetryTimes(TableInfoOpenAPIReq openAPIReq) {
        Consumer consumer = this.getConsumerById(openAPIReq.getConsumerId());
        Criteria criteria = new Criteria();

        if (!StringUtils.isEmpty(openAPIReq.getIsSync())){
            criteria.and("isSync").is(openAPIReq.getIsSync());
        }

        if (!StringUtils.isEmpty(openAPIReq.getTimes())){
            criteria.and("times").is(openAPIReq.getTimes());
        }

        if (openAPIReq.getStartTime() != null && openAPIReq.getEndTime() != null){
            criteria.and("createTime").gte(openAPIReq.getStartTime()).lte(openAPIReq.getEndTime());
        }else if(openAPIReq.getStartTime() != null){
            criteria.and("createTime").gte(openAPIReq.getStartTime());
        }else if(openAPIReq.getEndTime() != null){
            criteria.and("createTime").gte(openAPIReq.getEndTime());
        }
        Update update = Update.update("isSync",0).set("times",0).set("nextTime",new Date());
        Query query = Query.query(criteria);
        return mongoTemplate.updateMulti(query,update,consumer.getTable()).getModifiedCount();
    }

    @Override
    public Pager table(TableInfoReq tableInfoReq, String table) {
        Criteria criteria = new Criteria();

        if (!StringUtils.isEmpty(tableInfoReq.getIsSync())){
            criteria.and("isSync").is(tableInfoReq.getIsSync());
        }

        if (!StringUtils.isEmpty(tableInfoReq.getTimes())){
            criteria.and("times").is(tableInfoReq.getTimes());
        }

        if (tableInfoReq.getStartTime() != null && tableInfoReq.getEndTime() != null){
            criteria.and("createTime").gte(tableInfoReq.getStartTime()).lte(tableInfoReq.getEndTime());
        }else if(tableInfoReq.getStartTime() != null){
            criteria.and("createTime").gte(tableInfoReq.getStartTime());
        }else if(tableInfoReq.getEndTime() != null){
            criteria.and("createTime").gte(tableInfoReq.getEndTime());
        }

        Document searchDoc = criteria.getCriteriaObject();

        if (!StringUtils.isEmpty(tableInfoReq.getCustomSearch())){
            buildContentFilter(searchDoc,tableInfoReq.getCustomSearch());
        }
        //全文检索 -- 需要创建全文检索索引
        if (!StringUtils.isEmpty(tableInfoReq.getTextSearch())){
            Document textSearch = new Document("$search",tableInfoReq.getTextSearch());
            searchDoc.put("$text",textSearch);
        }

        Query query = new BasicQuery(searchDoc);
        Long count = mongoTemplate.count(query,table);
        query.with(Sort.by(Sort.Order.desc("_id")))
                .skip(tableInfoReq.getIndex()).limit(tableInfoReq.getPageSize());
        List<Map> list = mongoTemplate.find(query,Map.class,table);
        Pager pager = new Pager();
        pager.setPageNo(tableInfoReq.getPageNo());
        pager.setPageSize(tableInfoReq.getPageSize());
        pager.setTotalRecords(count);
        pager.setData(list);
        return pager;
    }

    @Override
    public List<Consumer> tableList() {
        Query query = new Query();
        query.fields().include("_id").include("table").include("name");
        return mongoTemplate.find(query,Consumer.class);
    }

    public void buildContentFilter(Document document, String filter) {
        Document shardingConsumerFilter = Document.parse(filter);
        for(String key :shardingConsumerFilter.keySet()){
            if(key.equals("_id")){
                document.put(key,shardingConsumerFilter.get(key));
                continue;
            }
            document.put("content."+key,shardingConsumerFilter.get(key));
        }
    }

    @Override
    public DeleteResult deleteData(TableInfoReq tableInfoReq, String table) {
        Criteria criteria = new Criteria();

        if (!StringUtils.isEmpty(tableInfoReq.getIsSync())){
            criteria.and("isSync").is(tableInfoReq.getIsSync());
        }

        if (!StringUtils.isEmpty(tableInfoReq.getTimes())){
            criteria.and("times").is(tableInfoReq.getTimes());
        }

        if (tableInfoReq.getStartTime() != null && tableInfoReq.getEndTime() != null){
            criteria.and("createTime").gte(tableInfoReq.getStartTime()).lte(tableInfoReq.getEndTime());
        }else if(tableInfoReq.getStartTime() != null){
            criteria.and("createTime").gte(tableInfoReq.getStartTime());
        }else if(tableInfoReq.getEndTime() != null){
            criteria.and("createTime").gte(tableInfoReq.getEndTime());
        }

        Document searchDoc = criteria.getCriteriaObject();

        if (!StringUtils.isEmpty(tableInfoReq.getCustomSearch())){
            buildContentFilter(searchDoc,tableInfoReq.getCustomSearch());
        }
        //全文检索 -- 需要创建全文检索索引
        if (!StringUtils.isEmpty(tableInfoReq.getTextSearch())){
            Document textSearch = new Document("$search",tableInfoReq.getTextSearch());
            searchDoc.put("$text",textSearch);
        }

        Query query = new BasicQuery(searchDoc);
        return mongoTemplate.remove(query,table);
    }

    @Override
    public UpdateResult tableReset(TableInfoReq tableInfoReq, String table) {
        Criteria criteria = new Criteria();

        if (!StringUtils.isEmpty(tableInfoReq.getIsSync())){
            criteria.and("isSync").is(tableInfoReq.getIsSync());
        }

        if (!StringUtils.isEmpty(tableInfoReq.getTimes())){
            criteria.and("times").is(tableInfoReq.getTimes());
        }

        if (tableInfoReq.getStartTime() != null && tableInfoReq.getEndTime() != null){
            criteria.and("createTime").gte(tableInfoReq.getStartTime()).lte(tableInfoReq.getEndTime());
        }else if(tableInfoReq.getStartTime() != null){
            criteria.and("createTime").gte(tableInfoReq.getStartTime());
        }else if(tableInfoReq.getEndTime() != null){
            criteria.and("createTime").gte(tableInfoReq.getEndTime());
        }

        Document searchDoc = criteria.getCriteriaObject();

        if (!StringUtils.isEmpty(tableInfoReq.getCustomSearch())){
            buildContentFilter(searchDoc,tableInfoReq.getCustomSearch());
        }
        //全文检索 -- 需要创建全文检索索引
        if (!StringUtils.isEmpty(tableInfoReq.getTextSearch())){
            Document textSearch = new Document("$search",tableInfoReq.getTextSearch());
            searchDoc.put("$text",textSearch);
        }

        Query query = new BasicQuery(searchDoc);
        Update update = Update.update("isSync",0).set("times",0).set("nextTime",new Date());
        return mongoTemplate.updateMulti(query,update,table);
    }

    @Override
    public Map<String, Object> getPushStatus(String table) {
        Map<String,Object> result = new HashMap<>();

        Consumer consumer = mongoTemplate.findOne(Query.query(Criteria.where("table").is(table)),Consumer.class);
        String status = null;
        if (consumer.getStatus() == 0){
            status = "禁用";
        }else{
            status = redisService.getErrMsg(table);
            if (StringUtils.isEmpty(status)){
                status = "正常";
            }
        }
        result.put("status",status);
        return result;
    }
}

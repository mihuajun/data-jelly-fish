package com.github.alenfive.datajellyfish.controller;

import com.github.alenfive.datajellyfish.entity.Consumer;
import com.github.alenfive.datajellyfish.entity.Pager;
import com.github.alenfive.datajellyfish.entity.TableInfoReq;
import com.github.alenfive.datajellyfish.service.ConsumerService;
import com.github.alenfive.datajellyfish.service.RedisService;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 表空间
 */
@RestController
@RequestMapping("/table")
public class TableController {

    @Autowired
    private RedisService redisService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ConsumerService consumerService;

    /**
     * 表空间信息列查询
     */
    @GetMapping("/{table}/cols")
    public Map<String,Object> status(@PathVariable String table) {
        Query query = new Query().with(Sort.by(Sort.Order.desc("_id"))).limit(1);
        return mongoTemplate.findOne(query,Map.class,table);
    }

    /**
     * 表空间信息查询
     */
    @PostMapping("/{table}")
    public Pager table(@RequestBody TableInfoReq tableInfoReq, @PathVariable String table) throws IOException {
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
            consumerService.buildContentFilter(searchDoc,tableInfoReq.getCustomSearch());
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



    /**
     * 表空间列表
     */
    @GetMapping("/table-list")
    public List<Consumer> tableList(){
        Query query = new Query();
        query.fields().include("_id").include("table").include("name");
        return mongoTemplate.find(query,Consumer.class);
    }


    /**
     * 表空间数据删除
     */
    @PostMapping("/{table}/delete")
    public DeleteResult deleteData(@RequestBody TableInfoReq tableInfoReq, @PathVariable String table) throws IOException {
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
            consumerService.buildContentFilter(searchDoc,tableInfoReq.getCustomSearch());
        }
        //全文检索 -- 需要创建全文检索索引
        if (!StringUtils.isEmpty(tableInfoReq.getTextSearch())){
            Document textSearch = new Document("$search",tableInfoReq.getTextSearch());
            searchDoc.put("$text",textSearch);
        }

        Query query = new BasicQuery(searchDoc);
        return mongoTemplate.remove(query,table);
    }

    /**
     * 表空间数据重置
     */
    @PutMapping("/{table}")
    public UpdateResult tableReset(@RequestBody TableInfoReq tableInfoReq, @PathVariable String table) throws IOException {
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
            consumerService.buildContentFilter(searchDoc,tableInfoReq.getCustomSearch());
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

    /**
     * 推送最新状态
     * @return
     */
    @GetMapping("{table}/status")
    public Map<String,Object> pushStatus(@PathVariable String table){
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

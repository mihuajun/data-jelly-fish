package com.github.alenfive.datajellyfish.controller;

import com.github.alenfive.datajellyfish.entity.Consumer;
import com.github.alenfive.datajellyfish.entity.Topic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 统计
 */
@RestController
@RequestMapping("/count")
public class CountController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 表空间信息统计
     * @return
     */
    @GetMapping("/{table}/stats")
    public Map<String,Object> stats(@PathVariable String table){
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

    /**
     * 总概览
     */
    @GetMapping("/stats")
    public Map<String,Object> status() {
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

}

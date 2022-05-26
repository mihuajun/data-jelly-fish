package com.github.alenfive.datajellyfish.controller;

import com.github.alenfive.datajellyfish.entity.Consumer;
import com.github.alenfive.datajellyfish.entity.Pager;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.TextIndexDefinition;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 消费者管理
 */
@RestController
@RequestMapping("/consumer")
public class ConsumerController {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 保存、添加 消费者
     * @param consumer
     */
    @PostMapping()
    public void saveConsumer(@RequestBody Consumer consumer){
        consumer.setUpdateTime(new Date());
        if (consumer.getId() == null){
            consumer.setCreateTime(new Date());
            consumer.setId(ObjectId.get());
        }
        //为表建立全文检索
        mongoTemplate.indexOps(consumer.getTable()).ensureIndex(TextIndexDefinition.builder().onAllFields().build());
        mongoTemplate.save(consumer);
    }

    /**
     * 单个任务获取
     * @param id
     */
    @GetMapping("/{id}")
    public Consumer get(@PathVariable String id){
        return mongoTemplate.findOne(Query.query(Criteria.where("_id").is(new ObjectId(id))),Consumer.class);
    }

    /**
     * 单个任务获取
     * @param table
     */
    @GetMapping("/table/{table}")
    public Consumer getByTable(@PathVariable String table){
        return mongoTemplate.findOne(Query.query(Criteria.where("table").is(table)),Consumer.class);
    }

    /**
     * 删除任务
     * @param id
     */
    @DeleteMapping
    public void delete(String id){
        mongoTemplate.remove(Query.query(Criteria.where("_id").is(new ObjectId(id))),Consumer.class);
    }


    /**
     * consumer 分页查询
     * @param pager
     * @return
     */
    @GetMapping("/list")
    public Pager list(Pager pager, String topicId){
        Criteria criteria = new Criteria();

        if (!StringUtils.isEmpty(topicId)){
            criteria.and("topicId").is(new ObjectId(topicId));
        }

        Query query = Query.query(criteria);
        Long count = mongoTemplate.count(query,Consumer.class);
        List<Consumer> list = mongoTemplate.find(query.skip(pager.getIndex()).limit(pager.getPageSize()),Consumer.class);
        pager.setTotalRecords(count);
        pager.setData(list);
        return pager;
    }
}

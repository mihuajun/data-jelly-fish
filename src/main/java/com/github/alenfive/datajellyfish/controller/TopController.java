package com.github.alenfive.datajellyfish.controller;

import com.github.alenfive.datajellyfish.entity.Pager;
import com.github.alenfive.datajellyfish.entity.Topic;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 消费者管理
 */
@RestController
@RequestMapping("/topic")
public class TopController {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 下拉列表选择
     * @return
     */
    @GetMapping("/select")
    public List<Topic> select(){
        Query query= new Query();
        query.fields().include("_id").include("name");
        return mongoTemplate.find(query,Topic.class);
    }

    /**
     * topic 分页查询
     * @param pager
     * @return
     */
    @GetMapping("/list")
    public Pager list(Pager pager){
        Criteria criteria = new Criteria();
        Query query = Query.query(criteria);
        Long count = mongoTemplate.count(query,Topic.class);
        List<Topic> list = mongoTemplate.find(query.skip(pager.getIndex()).limit(pager.getPageSize()),Topic.class);
        pager.setTotalRecords(count);
        pager.setData(list);
        return pager;
    }

    /**
     * topic 单个查询
     * @param id
     */
    @GetMapping("/{id}")
    public Topic get(@PathVariable String id){
        return mongoTemplate.findOne(Query.query(Criteria.where("_id").is(new ObjectId(id))),Topic.class);
    }

    /**
     * topic 状态修改
     * @param topic
     */
    @PutMapping("/status")
    public void updateStatus(@RequestBody Topic topic){
        Query query = Query.query(Criteria.where("_id").is(topic.getId()));
        Update update = Update.update("status",topic.getStatus());
        mongoTemplate.upsert(query,update,Topic.class);
    }

    /**
     * 保存、添加
     * @param topic
     */
    @PostMapping
    public void save(@RequestBody Topic topic){
        topic.setUpdateTime(new Date());
        if (topic.getId() == null){
            topic.setCreateTime(new Date());
            topic.setId(ObjectId.get());
        }
        mongoTemplate.save(topic);
    }



    /**
     * 删除
     * @param id
     */
    @DeleteMapping
    public void delete(String id){
        mongoTemplate.remove(Query.query(Criteria.where("_id").is(new ObjectId(id))),Topic.class);
    }



}

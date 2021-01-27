package com.github.alenfive.datajellyfish.service;

import com.github.alenfive.datajellyfish.entity.Topic;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Copyright: Copyright (c) 2019  ALL RIGHTS RESERVED.
 * @Author: 米华军
 * @CreateDate: 2020/9/9 15:11
 * @UpdateDate: 2020/9/9 15:11
 * @UpdateRemark: init
 * @Version: 1.0
 * @menu
 */
@Service
public class TopicService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void save(Topic topic) {
        mongoTemplate.save(topic);
    }

    public void update(Topic topic) {
        mongoTemplate.save(topic);
    }

    public List<Topic> listAll(){
        return mongoTemplate.findAll(Topic.class);
    }

    public Topic findOne(String topicId) {
        return mongoTemplate.findOne(Query.query(Criteria.where("_id").is(new ObjectId(topicId))),Topic.class);
    }

    public void updateOffset(String topicId, String shardingTopicKey, String oldOffset,String newOffset,String newUId) {
        Query query = Query.query(Criteria.where("_id").is(new ObjectId(topicId)).and("shardings").elemMatch(
                Criteria.where("key").is(shardingTopicKey).and("offset").is(oldOffset)
        ));
        //Query query = Query.query(Criteria.where("_id").is(new ObjectId(topicId)).and("shardings.key").is(shardingTopicKey).and("shardings.offset").is(oldOffset));
        Update update = Update.update("shardings.$.offset",newOffset).set("shardings.$.uId",newUId).set("updateTime",new Date());
        mongoTemplate.updateFirst(query,update,Topic.class);
    }
}

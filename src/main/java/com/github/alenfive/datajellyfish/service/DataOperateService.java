package com.github.alenfive.datajellyfish.service;

import com.github.alenfive.datajellyfish.entity.*;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

import java.util.List;
import java.util.Map;

public interface DataOperateService {

    /* consumer operate */
    public void saveConsumer(Consumer consumer);

    Consumer getConsumerById(String id);

    Consumer getConsumerByTable(String table);

    void deleteConsumer(String id);

    Pager listConsumer(Pager pager, String topicId);

    List<Consumer> findConsumerByTopic(String topicId);

    List<Consumer> listConsumerAll();


    /* topic operate */
    List<Topic> selectTopic();

    Pager listTopic(Pager pager);

    List<Topic> listTopicAll();

    Topic getTopic(String id);

    void updateTopicStatus(Topic topic);

    void saveTopic(Topic topic);

    void deleteTopic(String id);

    void updateOffset(String topicId, String shardingTopicKey, String offset, String toString, String toString1);

    Map<String, Object> stats(String table);

    Map<String, Object> status();

    Map<String, Object> status(String table);



    List<TopicRecord> findTopicRecord(Consumer consumer, ShardingConsumer shardingConsumer);

    void saveTopicRecord(TopicRecord topicRecord, String table);

    boolean existsTopicRecord(String uId, String table);

    Long resetRetryTimes(TableInfoOpenAPIReq openAPIReq);

    Pager table(TableInfoReq tableInfoReq, String table);

    List<Consumer> tableList();

    DeleteResult deleteData(TableInfoReq tableInfoReq, String table);

    UpdateResult tableReset(TableInfoReq tableInfoReq, String table);

    Map<String, Object> getPushStatus(String table);
}

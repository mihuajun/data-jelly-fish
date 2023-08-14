package com.github.alenfive.datajellyfish.service.data;

import com.github.alenfive.datajellyfish.entity.*;
import com.github.alenfive.datajellyfish.service.DataOperateService;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import java.util.List;
import java.util.Map;

public class MysqlDataOperateService implements DataOperateService {


    @Override
    public void saveConsumer(Consumer consumer) {

    }

    @Override
    public Consumer getConsumerById(String id) {
        return null;
    }

    @Override
    public Consumer getConsumerByTable(String table) {
        return null;
    }

    @Override
    public void deleteConsumer(String id) {

    }

    @Override
    public Pager listConsumer(Pager pager, String topicId) {
        return null;
    }

    @Override
    public List<Consumer> findConsumerByTopic(String topicId) {
        return null;
    }

    @Override
    public List<Consumer> listConsumerAll() {
        return null;
    }

    @Override
    public List<Topic> selectTopic() {
        return null;
    }

    @Override
    public Pager listTopic(Pager pager) {
        return null;
    }

    @Override
    public List<Topic> listTopicAll() {
        return null;
    }

    @Override
    public Topic getTopic(String id) {
        return null;
    }

    @Override
    public void updateTopicStatus(Topic topic) {

    }

    @Override
    public void saveTopic(Topic topic) {

    }

    @Override
    public void deleteTopic(String id) {

    }

    @Override
    public void updateOffset(String topicId, String shardingTopicKey, String offset, String toString, String toString1) {

    }

    @Override
    public Map<String, Object> stats(String table) {
        return null;
    }

    @Override
    public Map<String, Object> status() {
        return null;
    }

    @Override
    public Map<String, Object> status(String table) {
        return null;
    }

    @Override
    public List<TopicRecord> findTopicRecord(Consumer consumer, ShardingConsumer shardingConsumer) {
        return null;
    }

    @Override
    public void saveTopicRecord(TopicRecord topicRecord, String table) {

    }

    @Override
    public boolean existsTopicRecord(String uId, String table) {
        return false;
    }

    @Override
    public Long resetRetryTimes(TableInfoOpenAPIReq openAPIReq) {
        return null;
    }

    @Override
    public Pager table(TableInfoReq tableInfoReq, String table) {
        return null;
    }

    @Override
    public List<Consumer> tableList() {
        return null;
    }

    @Override
    public DeleteResult deleteData(TableInfoReq tableInfoReq, String table) {
        return null;
    }

    @Override
    public UpdateResult tableReset(TableInfoReq tableInfoReq, String table) {
        return null;
    }

    @Override
    public Map<String, Object> getPushStatus(String table) {
        return null;
    }
}

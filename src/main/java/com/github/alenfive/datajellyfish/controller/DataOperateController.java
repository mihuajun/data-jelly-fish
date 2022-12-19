package com.github.alenfive.datajellyfish.controller;

import com.github.alenfive.datajellyfish.entity.*;
import com.github.alenfive.datajellyfish.service.DataJellyFishService;
import com.github.alenfive.datajellyfish.service.DataOperateService;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 消费者管理
 */
@RestController
@RequestMapping("/api")
public class DataOperateController {

    @Autowired
    private DataOperateService dataOperateService;

    @Autowired
    private DataJellyFishService dataJellyFishService;

    /**
     * 保存、添加 消费者
     * @param consumer
     */
    @PostMapping("/consumer")
    public void saveConsumer(@RequestBody Consumer consumer){
        dataOperateService.saveConsumer(consumer);
    }

    /**
     * 单个任务获取
     * @param id
     */
    @GetMapping("/consumer/{id}")
    public Consumer getConsumerById(@PathVariable String id){
        return dataOperateService.getConsumerById(id);
    }

    /**
     * 单个任务获取
     * @param table
     */
    @GetMapping("/consumer/table/{table}")
    public Consumer getByTable(@PathVariable String table){
        return dataOperateService.getConsumerByTable(table);
    }

    /**
     * 删除任务
     * @param id
     */
    @DeleteMapping("/consumer")
    public void delete(String id){
        dataOperateService.deleteConsumer(id);
    }


    /**
     * consumer 分页查询
     * @param pager
     * @return
     */
    @GetMapping("/consumer/list")
    public Pager list(Pager pager, String topicId){
        return dataOperateService.listConsumer(pager,topicId);
    }

    /**
     * 下拉列表选择
     * @return
     */
    @GetMapping("/topic/select")
    public List<Topic> select(){
        return dataOperateService.selectTopic();
    }

    /**
     * topic 分页查询
     * @param pager
     * @return
     */
    @GetMapping("/topic/list")
    public Pager listTopic(Pager pager){
        return dataOperateService.listTopic(pager);
    }

    /**
     * topic 单个查询
     * @param id
     */
    @GetMapping("/topic/{id}")
    public Topic getTopic(@PathVariable String id){
        return dataOperateService.getTopic(id);
    }

    /**
     * topic 状态修改
     * @param topic
     */
    @PutMapping("/topic/status")
    public void updateTopicStatus(@RequestBody Topic topic){
        dataOperateService.updateTopicStatus(topic);
    }

    /**
     * 保存、添加
     * @param topic
     */
    @PostMapping("/topic")
    public void saveTopic(@RequestBody Topic topic){
        dataOperateService.saveTopic(topic);
    }

    /**
     * 删除
     * @param id
     */
    @DeleteMapping("/topic")
    public void deleteTopic(String id){
        dataOperateService.deleteTopic(id);
    }


    /**
     * 表空间信息统计
     * @return
     */
    @GetMapping("/count/{table}/stats")
    public Map<String,Object> stats(@PathVariable String table){
        return dataOperateService.stats(table);
    }

    /**
     * 总概览
     */
    @GetMapping("/count/stats")
    public Map<String,Object> status() {
        return dataOperateService.status();
    }

    /**
     * 任务加载
     */
    @GetMapping("/task-reload")
    public void taskReload() {
        dataJellyFishService.taskReload();
    }

    /**
     * consumerId 消费者标识
     * startTime 数据入库时间 - 开始时间
     * endTime 数据入库时间 - 截止时间
     */
    @PostMapping("/reset-retry-times")
    public Long resetRetryTimes(@RequestBody TableInfoOpenAPIReq openAPIReq) {
        return dataOperateService.resetRetryTimes(openAPIReq);
    }

    /**
     * 表空间信息列查询
     */
    @GetMapping("/table/{table}/cols")
    public Map<String,Object> status(@PathVariable String table) {
        return dataOperateService.status(table);
    }

    /**
     * 表空间信息查询
     */
    @PostMapping("/table/{table}")
    public Pager table(@RequestBody TableInfoReq tableInfoReq, @PathVariable String table) throws IOException {
        return dataOperateService.table(tableInfoReq,table);
    }



    /**
     * 表空间列表
     */
    @GetMapping("/table/table-list")
    public List<Consumer> tableList(){
        return dataOperateService.tableList();
    }


    /**
     * 表空间数据删除
     */
    @PostMapping("/table/{table}/delete")
    public DeleteResult deleteData(@RequestBody TableInfoReq tableInfoReq, @PathVariable String table) throws IOException {
        return dataOperateService.deleteData(tableInfoReq,table);
    }

    /**
     * 表空间数据重置
     */
    @PutMapping("/table/{table}")
    public UpdateResult tableReset(@RequestBody TableInfoReq tableInfoReq, @PathVariable String table) throws IOException {
        return dataOperateService.tableReset(tableInfoReq,table);
    }

    /**
     * 推送最新状态
     * @return
     */
    @GetMapping("/table/{table}/status")
    public Map<String,Object> getPushStatus(@PathVariable String table){
        return dataOperateService.getPushStatus(table);
    }
}

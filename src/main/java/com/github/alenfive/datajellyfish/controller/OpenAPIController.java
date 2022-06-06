package com.github.alenfive.datajellyfish.controller;

import com.github.alenfive.datajellyfish.entity.Consumer;
import com.github.alenfive.datajellyfish.entity.TableInfoOpenAPIReq;
import com.github.alenfive.datajellyfish.service.ConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * 对外开放接口
 */
@RestController
@RequestMapping("/open-api/")
public class OpenAPIController {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ConsumerService consumerService;

    /**
     * consumerId 消费者标识
     * startTime 数据入库时间 - 开始时间
     * endTime 数据入库时间 - 截止时间
     */
    @PostMapping("/reset-retry-times")
    public Long resetRetryTimes(@RequestBody TableInfoOpenAPIReq openAPIReq) {

        Consumer consumer = consumerService.getById(openAPIReq.getConsumerId());

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

}

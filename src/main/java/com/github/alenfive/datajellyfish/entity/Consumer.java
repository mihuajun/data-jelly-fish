package com.github.alenfive.datajellyfish.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Copyright: Copyright (c) 2019  ALL RIGHTS RESERVED.
 * @Author: 米华军
 * @CreateDate: 2020/9/8 19:20
 * @UpdateDate: 2020/9/8 19:20
 * @UpdateRemark: init
 * @Version: 1.0
 * @menu
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document
public class Consumer {

    private ObjectId id;

    /**
     * topic id
     */
    private ObjectId topicId;

    /**
     * 表名
     */
    private String table;
    /**
     * 描述
     */
    private String name;
    /**
     * 三方地址
     */
    private String url;

    /**
     * 订阅过滤
     */
    private String subscribeFilter;

    /**
     * 告警地址
     */
    private String warningUrl;

    /**
     * 唯一规则，逗号分隔多个字段去重
     */
    private String uniqueFields;

    /**
     * 分片规则
     */
    private List<ShardingConsumer> shardings;
    /**
     * 最大重试次数
     */
    private Integer maxTimes;
    /**
     * 是否是延迟消费，体现在首次消费时间
     */
    private Integer isFirstDelay;
    /**
     * 下一次重试时间，单位秒
     */
    private Integer delay;
    /**
     * 延迟周期是否以指数形式增长,0:否，1：是
     */
    private Integer isExponent;

    /**
     * 缓存天数
     */
    private Long cacheDay;

    /**
     * 状态：0:禁用，1：启用
     */
    private Integer status;

    private Date createTime;
    private Date updateTime;

    public ObjectId getId(){
        return id;
    }

    public String getStrId(){
        return id == null?null:id.toHexString();
    }

    /*显示扩展字段*/
    private String topicName;
}

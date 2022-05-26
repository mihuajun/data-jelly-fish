package com.github.alenfive.datajellyfish.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Map;

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
public class TopicRecord {
    private ObjectId id;
    /**
     * 去重唯一键
     */
    private String uId;
    /**
     * 内容实体
     */
    @TextIndexed
    private Map<String,Object> content;
    /**
     * 已重试次数
     */
    private Integer times;
    /**
     * 下一次尝试同步的时间
     */
    private Date nextTime;
    /**
     * 是否同步成功：0:否，1：是
     */
    private Integer isSync;
    /**
     * 最后一次三方的返回
     */
    private Object lastResult;
    /**
     * 分片任务key
     */
    private String shardingKey;

    private Integer dt;
    private Date createTime;
    private Date updateTime;
}

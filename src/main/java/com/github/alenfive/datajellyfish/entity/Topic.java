package com.github.alenfive.datajellyfish.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

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
public class Topic {

    private ObjectId id;
    /**
     * 描述
     */
    private String name;

    private String url;

    /**
     * 请求延迟，单位秒
     */
    private Integer delay;

    /**
     * 偏移量涉及字段
     */
    private OffsetFields offsetFields;

    /**
     * 状态：0:禁用，1：启用
     */
    private Integer status;

    private List<ShardingTopic> shardings;

    /**
     * 告警地址
     */
    private String warningUrl;

    private Date createTime;
    private Date updateTime;

    @JsonIgnore
    public ObjectId getId(){
        return id;
    }

    @JsonProperty("id")
    public String getStrId(){
        return id == null?null:id.toHexString();
    }
}

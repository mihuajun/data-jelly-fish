package com.github.alenfive.datajellyfish.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @Description:
 * @Copyright: Copyright (c) 2019  ALL RIGHTS RESERVED.
 * @Author: 米华军
 * @CreateDate: 2020/10/16 17:51
 * @UpdateDate: 2020/10/16 17:51
 * @UpdateRemark: init
 * @Version: 1.0
 * @menu
 */
public class ShardingTopic {
    /**
     * 分片任务唯一标识，在该topic下唯一
     */
    private String key;
    /**
     * 增量拉取偏移量
     */
    private String offset;
    /**
     * 源数据唯一键
     */
    @JsonProperty("uId")
    private String uId;

    /**
     * 自定义过滤条件，会加入到body请求中
     */
    private String filter;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }
}

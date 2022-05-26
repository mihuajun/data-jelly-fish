package com.github.alenfive.datajellyfish.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @Description:
 * @Copyright: Copyright (c) 2019  ALL RIGHTS RESERVED.
 * @Author: 米华军
 * @CreateDate: 2020/11/11 11:42
 * @UpdateDate: 2020/11/11 11:42
 * @UpdateRemark: init
 * @Version: 1.0
 * @menu 偏移量涉及字段
 */

public class OffsetFields {
    /**
     * 偏移量字段，值为源数据进行增量查询的对应字段
     */
    private String offset;
    /**
     * 值为源数据主键唯一字段
     */
    @JsonProperty("uId")
    private String uId;

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getuId() {
        return uId;
    }
}

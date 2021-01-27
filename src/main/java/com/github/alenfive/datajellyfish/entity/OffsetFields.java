package com.github.alenfive.datajellyfish.entity;

import lombok.Data;

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
@Data
public class OffsetFields {
    /**
     * 偏移量字段，值为源数据进行增量查询的对应字段
     */
    private String offset;
    /**
     * 值为源数据主键唯一字段
     */
    private String uId;
}

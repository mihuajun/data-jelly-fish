package com.github.alenfive.datajellyfish.entity;

import lombok.Data;

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
@Data
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
    private String uId;

    /**
     * 自定义过滤条件，会加入到body请求中
     */
    private String filter;
}

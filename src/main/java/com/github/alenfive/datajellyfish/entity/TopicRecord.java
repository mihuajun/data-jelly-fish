package com.github.alenfive.datajellyfish.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.Map;

/**
 * @Description:
 * @Copyright: Copyright (c) 2019  ALL RIGHTS RESERVED.
 * @Company: 成都国盛天丰技术有限责任公司
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
public class TopicRecord {
    private ObjectId id;
    /**
     * 去重唯一键
     */
    private String uId;
    /**
     * 内容实体
     */
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

    private Date createTime;
    private Date updateTime;
}

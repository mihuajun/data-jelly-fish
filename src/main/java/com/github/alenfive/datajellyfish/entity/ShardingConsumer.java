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
public class ShardingConsumer {
    private String key;
    private String filter;
}

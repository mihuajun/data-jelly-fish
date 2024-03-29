package com.github.alenfive.datajellyfish.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 告警实体
 * @Copyright: Copyright (c) 2019  ALL RIGHTS RESERVED.
 * @Author: 米华军
 * @CreateDate: 2020/12/28 20:08
 * @UpdateDate: 2020/12/28 20:08
 * @UpdateRemark: init
 * @Version: 1.0
 * @menu
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DingTalkTextEntity {
    private String content;
}

package com.github.alenfive.datajellyfish.entity;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Copyright: Copyright (c) 2019  ALL RIGHTS RESERVED.
 * @Author: 米华军
 * @CreateDate: 2020/9/8 16:59
 * @UpdateDate: 2020/9/8 16:59
 * @UpdateRemark: init
 * @Version: 1.0
 * @menu
 */
@Data
public class SSCProducerResult extends SSCResult{
    private List<Map<String,Object>> data;
}

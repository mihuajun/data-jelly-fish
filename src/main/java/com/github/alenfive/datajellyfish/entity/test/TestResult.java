package com.github.alenfive.datajellyfish.entity.test;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class TestResult {
    private String code;
    private List<Map<String,Object>> data;
}

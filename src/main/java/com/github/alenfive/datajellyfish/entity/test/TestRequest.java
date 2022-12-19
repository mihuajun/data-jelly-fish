package com.github.alenfive.datajellyfish.entity.test;

import lombok.Data;

@Data
public class TestRequest {
    private String produceKey;
    private String offset;
    private String uId;
    private String filter;
}

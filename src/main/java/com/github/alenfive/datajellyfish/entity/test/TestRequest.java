package com.github.alenfive.datajellyfish.entity.test;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TestRequest {
    private String producerKey;
    private String offset;

    @JsonProperty("uId")
    private String uId;
    private String filter;
}

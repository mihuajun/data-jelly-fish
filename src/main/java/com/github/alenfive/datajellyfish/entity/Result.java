package com.github.alenfive.datajellyfish.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> {
    private String code;
    private String action;
    private String msg;
    private T data;
    private Date timestamp;
}

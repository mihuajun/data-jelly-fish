package com.github.alenfive.datajellyfish.entity;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class TableInfoOpenAPIReq {

    /**
     * 消费者ID
     */
    private String consumerId;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    private Integer times;

    private Integer isSync;

}

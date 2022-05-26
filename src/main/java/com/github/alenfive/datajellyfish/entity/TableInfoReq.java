package com.github.alenfive.datajellyfish.entity;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class TableInfoReq {

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    private Integer times;

    private Integer isSync;

    private String customSearch;

    /**
     * 全文检索
     */
    private String textSearch;

    /**
     * 页数
     */
    private Integer pageNo = 1;
    /**
     * 每页最大数据量
     */
    private Integer pageSize = 15;

    public Integer getIndex() {
        return (this.pageNo - 1) * this.pageSize;
    }
}

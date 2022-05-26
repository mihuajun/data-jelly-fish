package com.github.alenfive.datajellyfish.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class Pager<T> implements Serializable {
    /**
     * 泛型,参数或返回值
     */
    private T data;
    /**
     * 总记录数
     */
    private Long totalRecords;
    /**
     * 页数
     */
    private Integer pageNo = 1;
    /**
     * 每页最大数据量
     */
    private Integer pageSize = 15;
    /**
     * 是否进行分页，默认分页
     */
    private Boolean pag = true;

    public Integer getIndex() {
        return pag ? (this.pageNo - 1) * this.pageSize : 0;
    }

    public Integer getTotalPages() {
        return pag ? Integer.valueOf((int) ((totalRecords + pageSize - 1) / pageSize)) : 1;
    }

    public Integer getPageSize() {
        int total = totalRecords == null?0:totalRecords.intValue();
        return pag ? pageSize : (total == 0?pageSize:total);
    }
}

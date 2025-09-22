package com.yt.aicode.basic.common;

import lombok.Getter;
import lombok.Setter;
import lombok.AccessLevel;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 通用分页返回对象
 * @author wys17
 * @param <T> 列表数据的类型
 */
@Getter
@Setter
public class PageResult<T> implements Serializable {

    private Integer pageNo;

    @Setter(AccessLevel.NONE)
    private Integer pageSize;

    @Setter(AccessLevel.NONE)
    private Long total = 0L;

    private Integer totalPages = 0;

    private List<T> records = Collections.emptyList();

    /**
     * 设置总记录数，并自动计算总页数
     */
    public void setTotal(Long total) {
        this.total = total;
        calculateTotalPages();
    }
    
    /**
     * 设置页面大小，并重新计算总页数
     */
    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
        calculateTotalPages();
    }
    
    /**
     * 计算总页数
     */
    private void calculateTotalPages() {
        if (this.pageSize != null && this.pageSize > 0 && this.total != null) {
            this.totalPages = (int) ((this.total / this.pageSize) + (this.total % this.pageSize == 0 ? 0 : 1));
        } else {
            this.totalPages = 0;
        }
    }
}
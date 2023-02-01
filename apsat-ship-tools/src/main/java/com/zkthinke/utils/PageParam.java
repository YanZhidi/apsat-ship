package com.zkthinke.utils;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @auther SONGXF
 * @date 2021/3/26 12:48
 */

@Data
@Accessors(chain = true)
public class PageParam<T> implements Serializable {

    /**
     * description = "页码", defaultValue =  1
     */
    private Integer pageNum = 1;

    /**
     * description = "页数", defaultValue = 20
     */
    private Integer pageSize = 20;

    /**
     * description = "排序", example = "id desc"
     */
    private String orderBy;

    /**
     * description = "查询条件参数"
     */
    private T param;

    /**
     * 此处可优化 优化详情且看解析
     */
    public PageParam<T> setOrderBy(String orderBy) {
        this.orderBy = orderBy;
        return this;
    }
}

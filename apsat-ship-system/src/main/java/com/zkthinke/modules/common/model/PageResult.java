package com.zkthinke.modules.common.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author weicb
 * @date 2019/12/29 19:48
 */
@ApiModel(value = "分页数据")
public class PageResult<T> {

    public static final Integer PAGE_DEFAULT = 1;
    public static final Integer PAGE_SIZE_DEFAULT = 10;

    @ApiModelProperty(notes = "总数")
    private int recordCount = 0;
    @ApiModelProperty(notes = "数据集合")
    private List<T> items = null;

    public PageResult() {

    }

    public List<T> getItems() {
        return items;
    }

    public int getRecordCount() {
        return recordCount;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Paged Result: ");

        sb.append("    record count:").append(recordCount);
        sb.append("    items:").append(items);
        return sb.toString();
    }

    public static <E> PageResult<E> create(int count, List<E> items) {
        PageResult<E> result = new PageResult<E>();
        result.items = items;
        result.recordCount = count;
        return result;
    }
}

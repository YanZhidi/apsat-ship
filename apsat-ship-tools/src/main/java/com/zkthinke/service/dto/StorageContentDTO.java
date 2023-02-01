package com.zkthinke.service.dto;

import java.io.Serializable;
import java.sql.Timestamp;
import lombok.Data;


/**
* Created by cjj on 2019-08-31.
*/
@Data
public class StorageContentDTO implements Serializable {

    private String id;

    // 文件的唯一索引
    private String fileKey;

    // 文件名
    private String name;

    // 文件类型
    private String type;

    // 文件大小
    private Integer size;

    // 文件访问链接
    private String url;

    // 创建时间
    private Timestamp addTime;

    // 更新时间
    private Timestamp updateTime;

    // 逻辑删除
    private Integer deleted;
}
package com.zkthinke.domain;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

/**
* Created by cjj on 2019-08-31.
*/
@Entity
@Data
@Table(name="storage_content")
public class StorageContent implements Serializable {

    @Id
    @Column(name = "id")
    private String id;

    // 文件的唯一索引
    @Column(name = "file_key",nullable = false)
    private String fileKey;

    // 文件名
    @Column(name = "name",nullable = false)
    private String name;

    // 文件类型
    @Column(name = "type",nullable = false)
    private String type;

    // 文件大小
    @Column(name = "size",nullable = false)
    private Integer size;

    // 文件访问链接
    @Column(name = "url")
    private String url;

    // 创建时间
    @Column(name = "add_time")
    @CreationTimestamp
    private Timestamp addTime;

    // 更新时间
    @Column(name = "update_time")
    private Timestamp updateTime;

    // 逻辑删除
    @Column(name = "deleted")
    private Integer deleted = 0;

    public void copy(StorageContent source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
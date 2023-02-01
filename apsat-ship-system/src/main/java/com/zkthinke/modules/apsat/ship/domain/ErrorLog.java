package com.zkthinke.modules.apsat.ship.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

/**
    * 错误日志记录表
    */
@Data
public class ErrorLog {
    private Integer id;
    private String type;

    /**
    * 错误文件名称
    */
    private String fileName;

    private String readTime;

    public ErrorLog(String type, String fileName, String readTime) {
        this.type = type;
        this.fileName = fileName;
        this.readTime = readTime;
    }
    public ErrorLog(String type, String fileName) {
        this.type = type;
        this.fileName = fileName;
    }
}
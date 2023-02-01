package com.zkthinke.modules.apsat.ship.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Zhongwl
 * @className ShipLogError
 * @description
 * @date 2022/3/10 15:46
 */
@Entity
@Data
@Table(name="t_ship_log_error")
@ApiModel(value="船舶日志异常log表")
public class ShipLogError implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(notes = "主键id")
    private Integer id;

    @Column(name = "ship_name")
    @ApiModelProperty(notes = "船舶名称")
    private String shipName;

    @Column(name = "error_type")
    @ApiModelProperty(notes = "异常类型（1:Log文件获取异常，2:Log文件解析异常）")
    private String errorType;

    @Column(name = "error_type_name")
    @ApiModelProperty(notes = "异常类型名称")
    private String errorTypeName;

    @Column(name = "remark")
    @ApiModelProperty(notes = "异常备注（异常字头）")
    private String remark;

    @Column(name = "send_flag")
    @ApiModelProperty(notes = "发送标识（邮件发送标识）")
    private Integer sendFlag;

    @Column(name = "create_time")
    @ApiModelProperty(notes = "创建时间")
    private Date createTime;
}

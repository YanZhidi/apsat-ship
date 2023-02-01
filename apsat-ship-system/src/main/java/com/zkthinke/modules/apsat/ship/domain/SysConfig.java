package com.zkthinke.modules.apsat.ship.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Zhongwl
 * @className SysConfig
 * @description
 * @date 2022/3/10 15:58
 */
@Entity
@Data
@Table(name="t_sys_config")
@ApiModel(value="系统配置表")
public class SysConfig implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(notes = "主键id")
    private Integer id;

    @Column(name = "business_code")
    @ApiModelProperty(notes = "业务编码")
    private String businessCode;

    @Column(name = "code")
    @ApiModelProperty(notes = "配置编码")
    private String code;

    @Column(name = "value")
    @ApiModelProperty(notes = "配置值")
    private String value;

    @Column(name = "remark")
    @ApiModelProperty(notes = "备注")
    private String remark;
}

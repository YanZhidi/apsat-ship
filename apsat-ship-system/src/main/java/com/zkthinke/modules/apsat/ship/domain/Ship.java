package com.zkthinke.modules.apsat.ship.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zkthinke.modules.system.domain.Dict;
import com.zkthinke.modules.system.domain.Role;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

/**
* @author weicb
* @date 2020-10-15
*/
@Entity
@Data
@Table(name="t_ship")
@ApiModel(value="船舶基本信息")
public class Ship implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(notes = "船舶 id")
    private Long id;

    // 名称
    @Column(name = "name",nullable = false)
    @ApiModelProperty(notes = "船舶名称")
    private String name;

    // MMSI编号
    @Column(name = "mmsi_number")
    @ApiModelProperty(notes = "MMSI编号")
    private String mmsiNumber;

    // 呼号
    @ApiModelProperty(notes = "呼号")
    @Column(name = "call_sign")
    private String callSign;

    // IMO,唯一编号
    @ApiModelProperty(notes = "IMO,唯一编号")
    @Column(name = "imo_number")
    private String imoNumber;

    // 类型
    @ApiModelProperty(notes = "类型")
    @Column(name = "type")
    private String type;

    // 创建时间
    @Column(name = "create_time")
    private Long createTime;

    // 更新时间
    @Column(name = "update_time")
    private Long updateTime;

    // 最新设备同步时间
    @Column(name = "last_device_stime")
    private String lastDeviceStime;

    // 最新航行数据同步时间
    @Column(name = "last_detail_stime")
    private String lastDetailStime;

    // 最新航行信息 id
    @OneToOne
    @JoinColumn(name = "last_detail_id")
    private ShipDetail lastShipDetail;

//    // 最新设备信息 id
//    @OneToOne
//    @JoinColumn(name = "last_device_id")
//    private ShipDevice lastShipDevice;

    // 拼音名称
    @Column(name = "name_pinyin")
    private String namePinyin;

    @ManyToMany(mappedBy = "ships")
    @JsonIgnore
    private Set<Role> roles;


    public void copy(Ship source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
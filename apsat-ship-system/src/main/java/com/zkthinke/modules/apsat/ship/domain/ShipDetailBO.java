package com.zkthinke.modules.apsat.ship.domain;

import java.io.Serializable;

import lombok.Data;

@Data
public class ShipDetailBO implements Serializable {
//    private String imoNumber;//imo编号
    private Long id;//
    private Long shipId;//船舶Id
    private String sailingStatus;//航行状态
    private String resetVoyage;//复位对地航程(当前航程)
    private String totalVoyage;//累计对地航程(累计航程)
    private String steeringSpeed;//转向速度
    private String groundSpeed;//对地航速
    private String longitude;//当前经度
    private String latitude;//当前纬度
    private String cog;//对地航向
    private String shipHead;//船首向
    private Long departureTime;//出发时间
    private Long eta;//估计到达时间
    private String destination;//目的地
    private String maxStaticDraft;//最大静态吃水
    private String windSpeed;//风速
    private String sensorDepth;//富裕水深
    private String relativeWind;//相对风向
//    private String deviceName;//设备名称
//    private Long createTime;//创建时间
//    private Long updateTime;//更新时间
    private Long collectTime;//数据采集时间
    private String departure;//出发地
//    private Long sourceId;//同步数据id（已废弃）
    private String mmsiNumber;//编号
    private String swsd;//传感器距水面距离
    private String name;//船名
    private String callSign;//呼号
    private String type;//船类型
//    private String dataSyncTime;//数据同步时间
//    private String rerativeWind;//相对风向
    private String hdtHeading;//航向
    private String rotTurnRate;//转向速率
    private String zdaTimeZone;//时区
    private String zdaTime;//时间戳
    private String pstSpeed;//转速
    private String pstTorque;//扭矩
    private String pstThrust;//推力
    private String pstPower;//功率
    //TTM
    private String typeofAcquistition;//获取类型
    private String timeofData;//UTC时间
    private String referenceTarget2;//参考目标
    private String targetStatus1;//目标状态
    private String targetName;//目标名称
    private String speedDidtanceunits;//速度/距离单位
    private String timetoCPA;//最小会遇时间
    private String distanceofClosest;//最小会遇距离
    private String targetCourseDegree;//目标航向，角度
    private String targetSpeed;//目标速度
    private String targetAzimuthAngle;//目标方位，角度
    private String targetDistacefromOwnShip;//目标与本船距离
    private String targetNumber;//目标号
    //OSD
    private String speedUnits;//速度单位
    private String speedReference;//速度参考
    private String vesselSpeed;//船舶速度
    private String courseReference;//航向参考
    private String vesselCourse;//船舶航向
    private String headingStatus;//艏向状态
    private String heading;//艏向
    //RSA
    private String starboardRudderSensor;//舵角
    private String starboardStatus;//状态
    //VBW
    private String longitudinalWaterSpeed;//纵向对水速度
    private String transverseWaterSpeed;//横向对水速度
    private String longitudinalGroundSpeed;//纵向对地速度
    private String transverseGroundSpeed;//横向对地速度
    private String sternTransverseWaterSpeed;//船尾测得横向对水速度
    private String sternTransverseGroundSpeed;//船尾测得横向对地速度
    private String dataValidState;//对水速度状态
    private static final long serialVersionUID = 1L;
}
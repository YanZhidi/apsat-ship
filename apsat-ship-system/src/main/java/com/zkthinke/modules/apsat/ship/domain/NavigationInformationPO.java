package com.zkthinke.modules.apsat.ship.domain;

import java.util.Date;

/**
 * @packageName：com.apstar.bireport.po
 * @className:NavigationInformationPO
 * @description:
 * @author:yeyadan
 * @date:2020/9/16 19:00
 */
public class NavigationInformationPO {
    private Long id;

    private String imoNumber;

    private String name;

    private String deviceName;

    private String rerativeWind;

    private String sensorDepth;

    private String swsd;

    private String sindSpeed;

    private String totalCumulativeGroundDistance;

    private String groundDistanceSinceReset;

    private String mmsiNumber;

    private String navigationalStatus;

    private String steeringSpeed;

    private String groundSpeed;

    private String longitude;

    private String latitude;

    private String cog;

    private String shipHead;

    private String callSign;

    private String type;

    private Date eta;

    private String maximumStaticDraft;

    private String destination;

    private String reserve01;

    private String reserve02;

    private Date dataSyncTime;

    private String departure;

    private Date departureTime;

    private Date inTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImoNumber() {
        return imoNumber;
    }

    public void setImoNumber(String imoNumber) {
        this.imoNumber = imoNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getRerativeWind() {
        return rerativeWind;
    }

    public void setRerativeWind(String rerativeWind) {
        this.rerativeWind = rerativeWind;
    }

    public String getSensorDepth() {
        return sensorDepth;
    }

    public void setSensorDepth(String sensorDepth) {
        this.sensorDepth = sensorDepth;
    }

    public String getSwsd() {
        return swsd;
    }

    public void setSwsd(String swsd) {
        this.swsd = swsd;
    }

    public String getSindSpeed() {
        return sindSpeed;
    }

    public void setSindSpeed(String sindSpeed) {
        this.sindSpeed = sindSpeed;
    }

    public String getTotalCumulativeGroundDistance() {
        return totalCumulativeGroundDistance;
    }

    public void setTotalCumulativeGroundDistance(String totalCumulativeGroundDistance) {
        this.totalCumulativeGroundDistance = totalCumulativeGroundDistance;
    }

    public String getGroundDistanceSinceReset() {
        return groundDistanceSinceReset;
    }

    public void setGroundDistanceSinceReset(String groundDistanceSinceReset) {
        this.groundDistanceSinceReset = groundDistanceSinceReset;
    }

    public String getMmsiNumber() {
        return mmsiNumber;
    }

    public void setMmsiNumber(String mmsiNumber) {
        this.mmsiNumber = mmsiNumber;
    }

    public String getNavigationalStatus() {
        return navigationalStatus;
    }

    public void setNavigationalStatus(String navigationalStatus) {
        this.navigationalStatus = navigationalStatus;
    }

    public String getSteeringSpeed() {
        return steeringSpeed;
    }

    public void setSteeringSpeed(String steeringSpeed) {
        this.steeringSpeed = steeringSpeed;
    }

    public String getGroundSpeed() {
        return groundSpeed;
    }

    public void setGroundSpeed(String groundSpeed) {
        this.groundSpeed = groundSpeed;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getCog() {
        return cog;
    }

    public void setCog(String cog) {
        this.cog = cog;
    }

    public String getShipHead() {
        return shipHead;
    }

    public void setShipHead(String shipHead) {
        this.shipHead = shipHead;
    }

    public String getCallSign() {
        return callSign;
    }

    public void setCallSign(String callSign) {
        this.callSign = callSign;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getEta() {
        return eta;
    }

    public void setEta(Date eta) {
        this.eta = eta;
    }

    public String getMaximumStaticDraft() {
        return maximumStaticDraft;
    }

    public void setMaximumStaticDraft(String maximumStaticDraft) {
        this.maximumStaticDraft = maximumStaticDraft;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getReserve01() {
        return reserve01;
    }

    public void setReserve01(String reserve01) {
        this.reserve01 = reserve01;
    }

    public String getReserve02() {
        return reserve02;
    }

    public void setReserve02(String reserve02) {
        this.reserve02 = reserve02;
    }

    public Date getDataSyncTime() {
        return dataSyncTime;
    }

    public void setDataSyncTime(Date dataSyncTime) {
        this.dataSyncTime = dataSyncTime;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public Date getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(Date departureTime) {
        this.departureTime = departureTime;
    }

    public Date getInTime() {
        return inTime;
    }

    public void setInTime(Date inTime) {
        this.inTime = inTime;
    }

    @Override
    public String toString() {
        return "NavigationInformationPO{" +
                "id=" + id +
                ", imoNumber='" + imoNumber + '\'' +
                ", name='" + name + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", rerativeWind='" + rerativeWind + '\'' +
                ", sensorDepth='" + sensorDepth + '\'' +
                ", swsd='" + swsd + '\'' +
                ", sindSpeed='" + sindSpeed + '\'' +
                ", totalCumulativeGroundDistance='" + totalCumulativeGroundDistance + '\'' +
                ", groundDistanceSinceReset='" + groundDistanceSinceReset + '\'' +
                ", mmsiNumber='" + mmsiNumber + '\'' +
                ", navigationalStatus='" + navigationalStatus + '\'' +
                ", steeringSpeed='" + steeringSpeed + '\'' +
                ", groundSpeed='" + groundSpeed + '\'' +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", cog='" + cog + '\'' +
                ", shipHead='" + shipHead + '\'' +
                ", callSign='" + callSign + '\'' +
                ", type='" + type + '\'' +
                ", eta=" + eta +
                ", maximumStaticDraft='" + maximumStaticDraft + '\'' +
                ", destination='" + destination + '\'' +
                ", reserve01='" + reserve01 + '\'' +
                ", reserve02='" + reserve02 + '\'' +
                ", dataSyncTime=" + dataSyncTime +
                ", departure='" + departure + '\'' +
                ", departureTime=" + departureTime +
                ", inTime=" + inTime +
                '}';
    }
}

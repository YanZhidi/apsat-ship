package com.zkthinke.modules.apsat.sync.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zkthinke.config.sync.SyncDataProperties;
import com.zkthinke.modules.apsat.ship.domain.ShipDevice;
import com.zkthinke.modules.apsat.ship.service.ShipDeviceService;
import com.zkthinke.modules.apsat.ship.service.ShipService;
import com.zkthinke.modules.apsat.sync.constant.OSNConstant;
import com.zkthinke.modules.apsat.sync.domain.SyncShipDevice;
import com.zkthinke.modules.apsat.sync.service.ShipDeviceDataSyncService;
import com.zkthinke.modules.apsat.sync.service.SyncShipDeviceService;
import com.zkthinke.modules.apsat.sync.service.dto.SyncShipDeviceDTO;
import com.zkthinke.modules.apsat.sync.service.dto.SyncShipDeviceQueryCriteria;
import com.zkthinke.modules.common.utils.JsonObjectUtils;
import com.zkthinke.utils.DateUtils;
import com.zkthinke.utils.HttpClientUtils;
import com.zkthinke.utils.PartitionUtil;
import com.zkthinke.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author: weicb
 * @Date: 2020/10/15 12:08
 */
@Service
@Slf4j
public class ShipDeviceDataSyncServiceImpl implements ShipDeviceDataSyncService {

    @Autowired
    private SyncDataProperties syncDataProperties;

    @Autowired
    private ShipDeviceService shipDeviceService;

    @Autowired
    private SyncShipDeviceService syncShipDeviceService;

    @Autowired
    private ShipService shipService;

    /**
     * 同步
     */
    @Override
    public Integer syncData(Long shipId, String imoNumber, String name, Optional<String> lastSuccessTime){
        try {
            String url = syncDataProperties.getShipDeviceUrl();
            JSONObject params = new JSONObject();
            // 公共请求头参数
            params.put(OSNConstant.APSTAR_HEAD, syncDataProperties.genHeaders());
            // 接口参数
            JSONObject businessParams = new JSONObject();
            businessParams.put(OSNConstant.IMO_NUMBER, imoNumber);
            businessParams.put(OSNConstant.NAME, name);
            // 按照上次成功时间拉取数据,避免全量拉取数据过多问题,"2020-11-27 22:00:00"
            lastSuccessTime.ifPresent(l -> businessParams.put(OSNConstant.START_TIME, l));
            params.put(OSNConstant.APSTAR_BODY, businessParams);
            log.info("开始调用船舶设备信息(能效)接口,请求参数 params = "+ params);
            // 记录当前请求时间
            String responseStr = HttpClientUtils.httpPost(url, params);
            log.info("调用船舶设备信息(能效)接口获取的数据:"+ responseStr);
            if(StringUtils.isEmpty(responseStr)) {
                throw new RuntimeException("没有返回结果");
            }
            return this.handleResponseData(shipId, responseStr);
        } catch(Exception e) {
            log.error("调用船舶设备信息(能效)接口 error : " + e.getMessage(), e);
            return 1;
        }
    }

    /**
     * 处理同步接口返回的数据
     * @param responseStr
     */
    private Integer handleResponseData(Long shipId, String responseStr){
        JSONObject jsonObject = JSONObject.parseObject(responseStr);
        String code = jsonObject.getString(OSNConstant.RESP_CODE);
        if (!OSNConstant.SUCCESS_RESP_CODE.equals(code)) {
            log.error("调用船舶设备信息接口请求失败:" + jsonObject.getString(OSNConstant.RESP_DESC));
            return 0;
        }
        JSONArray jsonArray = jsonObject.getJSONArray(OSNConstant.DATA);
        if (jsonArray == null || jsonArray.size() == 0) {
            return 0;
        }
        List<SyncShipDevice> list = new ArrayList<>();
        for(int i = 0, j = jsonArray.size(); i < j; i ++ ){
            SyncShipDevice syncShip = new SyncShipDevice();
            JSONObject obj = jsonArray.getJSONObject(i);
            if(!obj.containsKey("revolutionSpeed")){
                return 0;
            };
            syncShip.setImoNumber(JsonObjectUtils.getStr(obj, "imoNumber"));
            syncShip.setName(JsonObjectUtils.getStr(obj, "name"));
            syncShip.setRevolutionSpeed(JsonObjectUtils.getStr(obj, "revolutionSpeed"));
            syncShip.setStartingAirPressure(JsonObjectUtils.getStr(obj, "startingAirPressure"));
            syncShip.setHostLoad(JsonObjectUtils.getStr(obj, "hostLoad"));
            syncShip.setRunningHours(JsonObjectUtils.getStr(obj, "runningHours"));
            syncShip.setSuperchargerSpeed(JsonObjectUtils.getStr(obj, "superchargerSpeed"));
            syncShip.setTbbt(JsonObjectUtils.getStr(obj, "tbbt"));
            syncShip.setMeav(JsonObjectUtils.getStr(obj, "meav"));
            syncShip.setMeloip(JsonObjectUtils.getStr(obj, "meloip"));
            syncShip.setMefc(JsonObjectUtils.getStr(obj, "mefc"));
            syncShip.setMeloot(JsonObjectUtils.getStr(obj, "meloot"));
            syncShip.setMeloit(JsonObjectUtils.getStr(obj, "meloit"));
            syncShip.setSloip(JsonObjectUtils.getStr(obj, "sloip"));
            syncShip.setSloot(JsonObjectUtils.getStr(obj, "sloot"));
            syncShip.setFip(JsonObjectUtils.getStr(obj, "fip"));
            syncShip.setFit(JsonObjectUtils.getStr(obj, "fit"));
            syncShip.setPcoip(JsonObjectUtils.getStr(obj, "pcoip"));
            syncShip.setCloit(JsonObjectUtils.getStr(obj, "cloit"));
            syncShip.setMejip(JsonObjectUtils.getStr(obj, "mejip"));
            syncShip.setMejit(JsonObjectUtils.getStr(obj, "mejit"));
            syncShip.setSiep(JsonObjectUtils.getStr(obj, "siep"));
            // soet 为增压器出口排气温度
            syncShip.setSoet(JsonObjectUtils.getStr(obj, "soet"));
            syncShip.setSiet(JsonObjectUtils.getStr(obj, "siet"));
            syncShip.setControlAirPressure(JsonObjectUtils.getStr(obj, "controlAirPressure"));
            syncShip.setSmp(JsonObjectUtils.getStr(obj, "smp"));
            syncShip.setSmt(JsonObjectUtils.getStr(obj, "smt"));
            syncShip.setRunstatus1(JsonObjectUtils.getStr(obj, "runstatus1"));
            syncShip.setSae1(JsonObjectUtils.getStr(obj, "sae1"));
            syncShip.setPog1(JsonObjectUtils.getStr(obj, "pog1"));
            syncShip.setAts1(JsonObjectUtils.getStr(obj, "ats1"));
            syncShip.setAlop1(JsonObjectUtils.getStr(obj, "alop1"));
            syncShip.setAlot1(JsonObjectUtils.getStr(obj, "alot1"));
            syncShip.setAfp1(JsonObjectUtils.getStr(obj, "afp1"));
            syncShip.setAft1(JsonObjectUtils.getStr(obj, "aft1"));
            syncShip.setRunstatus2(JsonObjectUtils.getStr(obj, "runstatus2"));
            syncShip.setSae2(JsonObjectUtils.getStr(obj, "sae2"));
            syncShip.setPog2(JsonObjectUtils.getStr(obj, "pog2"));
            syncShip.setAts2(JsonObjectUtils.getStr(obj, "ats2"));
            syncShip.setAlop2(JsonObjectUtils.getStr(obj, "alop2"));
            syncShip.setAlot2(JsonObjectUtils.getStr(obj, "alot2"));
            syncShip.setAfp2(JsonObjectUtils.getStr(obj, "afp2"));
            syncShip.setAft2(JsonObjectUtils.getStr(obj, "aft2"));
            syncShip.setRunstatus3(JsonObjectUtils.getStr(obj, "runstatus3"));
            syncShip.setSae3(JsonObjectUtils.getStr(obj, "sae3"));
            syncShip.setPog3(JsonObjectUtils.getStr(obj, "pog3"));
            syncShip.setAts3(JsonObjectUtils.getStr(obj, "ats3"));
            syncShip.setAlop3(JsonObjectUtils.getStr(obj, "alop3"));
            syncShip.setAlot3(JsonObjectUtils.getStr(obj, "alot3"));
            syncShip.setAfp3(JsonObjectUtils.getStr(obj, "afp3"));
            syncShip.setAft3(JsonObjectUtils.getStr(obj, "aft3"));
            syncShip.setBoilerSteamPressure(JsonObjectUtils.getStr(obj, "boilerSteamPressure"));
            syncShip.setBoilerWaterLevel(JsonObjectUtils.getStr(obj, "boilerWaterLevel"));
            syncShip.setBot(JsonObjectUtils.getStr(obj, "bot"));
            syncShip.setSternDraught(JsonObjectUtils.getStr(obj, "sternDraught"));
            syncShip.setStemDraft(JsonObjectUtils.getStr(obj, "stemDraft"));
            syncShip.setStarboardDraft(JsonObjectUtils.getStr(obj, "starboardDraft"));
            syncShip.setPortDraft(JsonObjectUtils.getStr(obj, "portDraft"));
            syncShip.setTrim(JsonObjectUtils.getStr(obj, "trim"));
            syncShip.setHeel(JsonObjectUtils.getStr(obj, "heel"));
            // 固定值
            syncShip.setMefit("8.00Ton/h");
            syncShip.setMefif("8.00Ton/h");
            syncShip.setMefof(JsonObjectUtils.getStr(obj, "mefof"));
            syncShip.setGfif(JsonObjectUtils.getStr(obj, "gfif"));
            syncShip.setGfof(JsonObjectUtils.getStr(obj, "gfof"));
            syncShip.setBfoif(JsonObjectUtils.getStr(obj, "bfoif"));
            syncShip.setBfoof(JsonObjectUtils.getStr(obj, "bfoof"));
            syncShip.setSefif(JsonObjectUtils.getStr(obj, "sefif"));
            syncShip.setSefof(JsonObjectUtils.getStr(obj, "sefof"));
            syncShip.setDeviceName(JsonObjectUtils.getStr(obj, "deviceName"));
            syncShip.setSourceId(JsonObjectUtils.getStr(obj, "id"));
            syncShip.setDataSyncTime(JsonObjectUtils.getStr(obj, "dataSyncTime"));
            syncShip.setSyncTime(System.currentTimeMillis());
            list.add(syncShip);
        }
        // 写入同步记录
        // 写入同步记录,剔除已同步数据
        List<String> sourceIds = list.stream().map(SyncShipDevice::getSourceId).collect(Collectors.toList());
        SyncShipDeviceQueryCriteria criteria = new SyncShipDeviceQueryCriteria();
        criteria.setSourceIds(sourceIds);
        // 是否已经存在，存在则更新数据
        List<SyncShipDeviceDTO> syncShips = syncShipDeviceService.queryAll(criteria);
        Set<String> existsIds = syncShips.stream().map(SyncShipDeviceDTO::getSourceId).collect(Collectors.toSet());
        List<SyncShipDevice> fullList = list.stream().filter(s -> !existsIds.contains(s.getSourceId())).collect(Collectors.toList());
        if (fullList.size() == 0) {
            return 0;
        }
        // 分批进行入库处理
        PartitionUtil.listPartition(fullList, syncList -> {
            syncShipDeviceService.batchInsertOrUpdate(syncList);
            List<ShipDevice> shipDevices = syncList.stream()
                    .map(e -> handleShipDetail(shipId, e))
                    .collect(Collectors.toList());
            // 写入船舶详细信息
            shipDeviceService.batchInsertOrUpdate(shipDevices);
        });
        // 更新最新同步时间
        SyncShipDevice lastSyncShip = fullList.get(fullList.size() - 1);
        shipService.updateLastDeviceStime(shipId, lastSyncShip.getDataSyncTime());
        return jsonArray.size();
    }


    private ShipDevice handleShipDetail(Long shipId, SyncShipDevice syncShip){
        ShipDevice shipDevice = new ShipDevice();
        BeanUtil.copyProperties(syncShip, shipDevice, CopyOptions.create().setIgnoreNullValue(true));
        shipDevice.setShipId(shipId);
        shipDevice.setCollectTime(DateUtils.str2Long(syncShip.getDataSyncTime()));
        shipDevice.setSourceId(syncShip.getId());
        shipDevice.setCreateTime(System.currentTimeMillis());
        shipDevice.setUpdateTime(System.currentTimeMillis());
        return shipDevice;
    }

}
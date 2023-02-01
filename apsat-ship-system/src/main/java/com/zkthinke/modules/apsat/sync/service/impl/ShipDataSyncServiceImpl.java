package com.zkthinke.modules.apsat.sync.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zkthinke.config.sync.SyncDataProperties;
import com.zkthinke.modules.apsat.ship.domain.Ship;
import com.zkthinke.modules.apsat.ship.domain.ShipDetail;
import com.zkthinke.modules.apsat.ship.service.ShipDetailService;
import com.zkthinke.modules.apsat.ship.service.ShipService;
import com.zkthinke.modules.apsat.sync.constant.OSNConstant;
import com.zkthinke.modules.apsat.sync.domain.SyncShip;
import com.zkthinke.modules.apsat.sync.service.ShipDataSyncService;
import com.zkthinke.modules.apsat.sync.service.SyncShipService;
import com.zkthinke.modules.apsat.sync.service.dto.SyncShipDTO;
import com.zkthinke.modules.apsat.sync.service.dto.SyncShipQueryCriteria;
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
 * @Date: 2019/12/28 13:16
 */
@Service
@Slf4j
public class ShipDataSyncServiceImpl implements ShipDataSyncService {

    @Autowired
    private SyncDataProperties syncDataProperties;

    @Autowired
    private ShipDetailService shipDetailService;

    @Autowired
    private SyncShipService syncShipService;

    @Autowired
    private ShipService shipService;


    /**
     * 同步
     */
    @Override
    public Integer syncShipData(Ship ship, Optional<String> lastSuccessTime){
        try {
            String url = syncDataProperties.getShipNavigationUrl();
            JSONObject params = new JSONObject();
            // 公共请求头参数
            params.put(OSNConstant.APSTAR_HEAD, syncDataProperties.genHeaders());
            // 接口参数
            JSONObject businessParams = new JSONObject();
            businessParams.put(OSNConstant.IMO_NUMBER, ship.getImoNumber());
            businessParams.put(OSNConstant.NAME, ship.getName());
            // 按照上次成功时间拉取数据,避免全量拉取数据过多问题
            lastSuccessTime.ifPresent(l -> businessParams.put(OSNConstant.START_TIME, l));
            params.put(OSNConstant.APSTAR_BODY, businessParams);
            log.info("开始调用船舶航行信息接口,请求参数 params = "+ params);
            // 记录当前请求时间
            String responseStr = HttpClientUtils.httpPost(url, params);
            log.info("调用船舶航行信息接口获取的数据:"+ responseStr);
            if(StringUtils.isEmpty(responseStr)) {
                throw new RuntimeException("没有返回结果");
            }
            return this.handleShipResponseData(ship, responseStr);
        } catch(Exception e) {
            log.error("调用船舶航行信息接口 error: " + e.getMessage(), e);
            return 1;
        }
    }

    /**
     * 处理同步接口返回的数据
     * @param responseStr
     */
    private Integer handleShipResponseData(Ship ship, String responseStr){
        Long shipId = ship.getId();
        JSONObject jsonObject = JSONObject.parseObject(responseStr);
        String code = jsonObject.getString(OSNConstant.RESP_CODE);
        if (!OSNConstant.SUCCESS_RESP_CODE.equals(code)) {
            log.error("调用船舶航行信息接口请求失败:" + jsonObject.getString(OSNConstant.RESP_DESC));
            return 0;
        }
        JSONArray jsonArray = jsonObject.getJSONArray(OSNConstant.DATA);
        if (jsonArray == null || jsonArray.size() == 0) {
            return 0;
        }
        Long syncTime = System.currentTimeMillis();
        List<SyncShip> list = new ArrayList<>();
        for(int i = 0, j = jsonArray.size(); i < j; i ++ ){
            SyncShip syncShip = new SyncShip();
            JSONObject obj = jsonArray.getJSONObject(i);
            if(!obj.containsKey("navigationalstatus")){
                return 0;
            };
            syncShip.setSwsd(JsonObjectUtils.getStr(obj, "swsd"));
            syncShip.setMmsiNumber(JsonObjectUtils.getStr(obj, "mmsiNumber"));
            syncShip.setNavigationalStatus(JsonObjectUtils.getStr(obj, "navigationalstatus"));
            syncShip.setGroundSpeed(JsonObjectUtils.getStr(obj, "groundSpeed"));
            syncShip.setLatitude(JsonObjectUtils.getStr(obj, "latitude"));
            syncShip.setSensorDepth(JsonObjectUtils.getStr(obj, "sensorDepth"));
            syncShip.setDestination(JsonObjectUtils.getStr(obj, "destination"));
            syncShip.setSindSpeed(JsonObjectUtils.getStr(obj, "sindSpeed"));
            syncShip.setSteeringSpeed(JsonObjectUtils.getStr(obj, "steeringSpeed"));
            syncShip.setType(JsonObjectUtils.getStr(obj, "type"));
            syncShip.setDeviceName(JsonObjectUtils.getStr(obj, "deviceName"));
            syncShip.setRerativeWind(JsonObjectUtils.getStr(obj, "rerativeWind"));
            syncShip.setCallSign(JsonObjectUtils.getStr(obj, "callSign"));
            syncShip.setTotalCumulativeGroundDistance(JsonObjectUtils.getStr(obj, "totalCumulativeGroundDistance"));
            syncShip.setMaximumStaticDraft(JsonObjectUtils.getStr(obj, "maximumStaticDraft"));
            syncShip.setEta(JsonObjectUtils.getStr(obj, "eta"));
            syncShip.setShipHead(JsonObjectUtils.getStr(obj, "shipHead"));
            syncShip.setImoNumber(JsonObjectUtils.getStr(obj, "imoNumber"));
            syncShip.setName(JsonObjectUtils.getStr(obj, "name"));
            syncShip.setCog(JsonObjectUtils.getStr(obj, "cog"));
            syncShip.setGroundDistanceSinceReset(JsonObjectUtils.getStr(obj, "groundDistanceSinceReset"));
            syncShip.setLongitude(JsonObjectUtils.getStr(obj, "longitude"));
            syncShip.setDeparture(JsonObjectUtils.getStr(obj, "departure"));
            syncShip.setDeparturetime(JsonObjectUtils.getStr(obj, "departureTime"));
            syncShip.setSourceId(JsonObjectUtils.getStr(obj, "id"));
            syncShip.setDataSyncTime(JsonObjectUtils.getStr(obj, "dataSyncTime"));
            syncShip.setSyncTime(syncTime);
            list.add(syncShip);
        }
        if (list.size() == 0) {
            return 0;
        }
        // 写入同步记录,剔除已同步数据
        List<String> sourceIds = list.stream().map(SyncShip::getSourceId).collect(Collectors.toList());
        SyncShipQueryCriteria criteria = new SyncShipQueryCriteria();
        criteria.setSourceIds(sourceIds);
        // 是否已经存在，存在则更新数据
        List<SyncShipDTO> syncShips = syncShipService.queryAll(criteria);
        Set<String> existsIds = syncShips.stream().map(SyncShipDTO::getSourceId).collect(Collectors.toSet());
        List<SyncShip> fullList = list.stream().filter(s -> !existsIds.contains(s.getSourceId())).collect(Collectors.toList());
        if (fullList.size() == 0) {
            return 0;
        }
        // 最新航行记录
        SyncShip lastSyncShip = fullList.get(fullList.size() - 1);
        ship.setCallSign(lastSyncShip.getCallSign());
        ship.setMmsiNumber(lastSyncShip.getMmsiNumber());
        ship.setType(lastSyncShip.getType());
        ship.setLastDetailStime(lastSyncShip.getDataSyncTime());
        // 分批进行入库处理
        PartitionUtil.listPartition(fullList, syncList -> {
            syncShipService.batchInsertOrUpdate(syncList);
            List<ShipDetail> shipDetails = syncList.stream()
                    .map(e -> handleShipDetail(shipId, e))
                    .collect(Collectors.toList());
            // 写入船舶详细信息
            shipDetailService.batchInsertOrUpdate(shipDetails);
            // 关联最新航行记录
            Optional<ShipDetail> last = shipDetails.stream()
                    .filter(d -> lastSyncShip.getId().equals(d.getSourceId()))
                    .findFirst();
            last.ifPresent(l -> ship.setLastShipDetail(l));
        });
        // 更新最新航行记录 id以及船舶呼号\最新同步时间等信息
        shipService.updateSync(ship);
        return jsonArray.size();
    }


    private ShipDetail handleShipDetail(Long shipId, SyncShip syncShip){
        ShipDetail shipDetail = new ShipDetail();
        shipDetail.setShipId(shipId);
        shipDetail.setSourceId(syncShip.getId());
        shipDetail.setCreateTime(System.currentTimeMillis());
        shipDetail.setUpdateTime(System.currentTimeMillis());
        shipDetail.setCog(syncShip.getCog());
        shipDetail.setDepartureTime(DateUtils.str2Long(syncShip.getDeparturetime()));
        shipDetail.setGroundSpeed(syncShip.getGroundSpeed());
        shipDetail.setDestination(syncShip.getDestination());
        shipDetail.setDeviceName(syncShip.getDeviceName());
        shipDetail.setSailingStatus(syncShip.getNavigationalStatus());
        shipDetail.setResetVoyage(syncShip.getGroundDistanceSinceReset());
        shipDetail.setTotalVoyage(syncShip.getTotalCumulativeGroundDistance());
        shipDetail.setSteeringSpeed(syncShip.getSteeringSpeed());
        shipDetail.setLongitude(syncShip.getLongitude());
        shipDetail.setLatitude(syncShip.getLatitude());
        shipDetail.setShipHead(syncShip.getShipHead());
        shipDetail.setEta(DateUtils.str2Long(syncShip.getEta()));
        shipDetail.setMaxStaticDraft(syncShip.getMaximumStaticDraft());
        shipDetail.setWindSpeed(syncShip.getSindSpeed());
        shipDetail.setRelativeWind(syncShip.getRerativeWind());
        shipDetail.setSensorDepth(syncShip.getSensorDepth());
        shipDetail.setDeparture(syncShip.getDeparture());
        shipDetail.setCollectTime(DateUtils.str2Long(syncShip.getDataSyncTime()));
        return shipDetail;
    }


}
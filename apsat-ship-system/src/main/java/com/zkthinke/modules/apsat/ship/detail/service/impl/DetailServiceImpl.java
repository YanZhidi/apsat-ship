package com.zkthinke.modules.apsat.ship.detail.service.impl;

import com.zkthinke.modules.apsat.ship.detail.BO.DetailVO;
import com.zkthinke.modules.apsat.ship.detail.service.DetailService;
import com.zkthinke.modules.apsat.ship.device.service.DeviceService;
import com.zkthinke.modules.apsat.ship.domain.ShipDetailBO;
import com.zkthinke.modules.apsat.ship.domain.ShipDetailPO;
import com.zkthinke.modules.apsat.ship.mapper.ShipDetailMapper;
import com.zkthinke.modules.apsat.ship.utils.FieldUnitUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DetailServiceImpl implements DetailService {
    @Autowired
    ShipDetailMapper shipDetailMapper;

    @Autowired
    DeviceService deviceService;

    @Override
    public List<DetailVO> findSpeedByIdAndTime(Long shipId, Long collectTimeBegin, Long collectTimeEnd) {
        List<DetailVO> speedByIdAndTime = shipDetailMapper.getSpeedByIdAndTime(shipId, collectTimeBegin, collectTimeEnd);

        return speedByIdAndTime;
    }

    @Override
    public List<Map<String, Object>> findSpendByIdAndTime(Long shipId, Long collectTimeBegin, Long collectTimeEnd) {
        List<DetailVO> list = shipDetailMapper.getSpendByIdAndTime(shipId, collectTimeBegin, collectTimeEnd);
        List<Map<String, Object>> resultList = new ArrayList<>();

        Map<Long, Map<String, Object>> oilMap = deviceService.findShipFuelData(shipId, collectTimeBegin, collectTimeEnd);
        for (int i = 0; i < list.size() - 1; i++) {
            Map<String, Object> resultMap = new HashMap<>();
            DetailVO vo1 = list.get(i);
            DetailVO vo2 = list.get(i + 1);

            String voyage1 = vo1.getTotalVoyage().replace("nm", "");
            String voyage2 = vo2.getTotalVoyage().replace("nm", "");

            Long timeStamp = vo2.getCollectTime();
            if (oilMap.get(timeStamp) == null) {
                continue;
            }
            double totalio = Double.valueOf(oilMap.get(timeStamp).get("totalio").toString());

            double distance = 0;
            if (isSailing(vo1)&&isSailing(vo2)){
                distance = (Double.valueOf(voyage2) - Double.valueOf(voyage1));
            }

            resultMap.put("timeStamp", timeStamp);
            if (voyage1.equals(voyage2) || distance <= 0) {
                resultMap.put("value", 0);
            } else {
                resultMap.put("value", new BigDecimal(totalio / distance/120).setScale(2, RoundingMode.HALF_UP));
            }
            resultList.add(resultMap);
        }

        if (resultList.size() == 0) {
            while (collectTimeBegin < collectTimeEnd) {
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("timeStamp", collectTimeBegin);
                resultMap.put("value", "0");
                resultList.add(resultMap);
                collectTimeBegin += 30000;
            }
            return resultList;
        }

        return resultList;
    }

    public boolean isSailing(DetailVO vo){
        return vo.getSailingStatus().equals("发动机使用中") || vo.getSailingStatus().equals("航行中");
    }

    @Override
    public List<Map<String, Object>> findPstPower(Long shipId, Long collectTimeBegin, Long collectTimeEnd) {
        List<DetailVO> detail = shipDetailMapper.findPstPower(shipId,collectTimeBegin,collectTimeEnd);
        List<Map<String, Object>> collect = detail.stream().map(d -> {
            Map<String, Object> map = new HashMap<>();
            map.put("collectionTime", d.getCollectTime());
            map.put("value", Optional.ofNullable(d.getPstPower()).orElse("0kW"));
            return map;
        }).collect(Collectors.toList());
        return collect;
    }

    @Override
    public List<ShipDetailBO> findShipDetailList(Long shipId, Long collectTimeBegin, Long collectTimeEnd) {
        List<ShipDetailBO> shipDetailList = shipDetailMapper.findShipDetailList(shipId,collectTimeBegin,collectTimeEnd);
        //去掉单位
        for (ShipDetailBO bo : shipDetailList) {
            removeUnit(bo);
        }
        return shipDetailList;
    }


    private void removeUnit(ShipDetailBO bo){
        bo.setResetVoyage(FieldUnitUtil.removeUnit(bo.getResetVoyage()));
        bo.setTotalVoyage(FieldUnitUtil.removeUnit(bo.getTotalVoyage()));
        bo.setSteeringSpeed(FieldUnitUtil.removeUnit(bo.getSteeringSpeed()));
        bo.setGroundSpeed(FieldUnitUtil.removeUnit(bo.getGroundSpeed()));
        bo.setCog(FieldUnitUtil.removeUnit(bo.getCog()));
        bo.setShipHead(FieldUnitUtil.removeUnit(bo.getShipHead()));
        bo.setMaxStaticDraft(FieldUnitUtil.removeUnit(bo.getMaxStaticDraft()));
        bo.setWindSpeed(FieldUnitUtil.removeUnit(bo.getWindSpeed()));
        bo.setSensorDepth(FieldUnitUtil.removeUnit(bo.getSensorDepth()));
        bo.setRelativeWind(FieldUnitUtil.removeUnit(bo.getRelativeWind()));
        bo.setSwsd(FieldUnitUtil.removeUnit(bo.getSwsd()));
        bo.setPstSpeed(FieldUnitUtil.removeUnit(bo.getPstSpeed()));
        bo.setPstTorque(FieldUnitUtil.removeUnit(bo.getPstTorque()));
        bo.setPstThrust(FieldUnitUtil.removeUnit(bo.getPstThrust()));
        bo.setPstPower(FieldUnitUtil.removeUnit(bo.getPstPower()));
    }
}

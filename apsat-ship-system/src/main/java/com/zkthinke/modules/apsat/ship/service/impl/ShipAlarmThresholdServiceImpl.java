package com.zkthinke.modules.apsat.ship.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.zkthinke.modules.apsat.ship.domain.ShipAlarmThresholdPO;
import com.zkthinke.modules.apsat.ship.domain.ShipAlarmThresholdUpsertBO;
import com.zkthinke.modules.apsat.ship.mapper.ShipAlarmThresholdMapper;
import com.zkthinke.modules.apsat.ship.service.ShipAlarmThresholdService;
import com.zkthinke.modules.common.utils.DateUtils;
import com.zkthinke.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ShipAlarmThresholdServiceImpl implements ShipAlarmThresholdService {

    @Autowired
    private ShipAlarmThresholdMapper shipAlarmThresholdMapper;

    @Override
    public List<ShipAlarmThresholdPO> queryShipAlarmThreshold(Integer shipId) {
        log.info("queryShipAlarmThreshold 入参：{}", shipId);
        try {
            List<ShipAlarmThresholdPO> list = shipAlarmThresholdMapper.queryShipAlarmThreshold(shipId);
            List<ShipAlarmThresholdPO> defaultList = shipAlarmThresholdMapper.queryShipAlarmThresholdDefault();
            Map<String, ShipAlarmThresholdPO> map = list.stream().collect(Collectors.toMap(ShipAlarmThresholdPO::getMerit, po -> po));
            for (ShipAlarmThresholdPO po : defaultList) {
                String merit = po.getMerit();
                if (!map.containsKey(merit)) {
                    map.put(merit, po);
                }
            }
            List<ShipAlarmThresholdPO> resultList = new ArrayList<>();
            if (!CollectionUtils.isEmpty(map)) {
                resultList = map.values().stream().sorted((o1, o2) -> {
                    String merit1 = o1.getMerit();
                    String merit2 = o2.getMerit();
                    return merit1.compareTo(merit2);
                }).collect(Collectors.toList());
            }
            return resultList;
        } catch (Exception e) {
            log.error("queryShipAlarmThreshold 异常：", e);
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional
    public void upsertShipAlarmThreshold(ShipAlarmThresholdUpsertBO shipAlarmThresholdUpsertBO) {
        log.info("queryShipAlarmThreshold 入参：{}", JSONObject.toJSONString(shipAlarmThresholdUpsertBO));
        //组装前端传参
        Integer shipId = shipAlarmThresholdUpsertBO.getShipId();
        List<ShipAlarmThresholdPO> shipAlarmThresholdList = shipAlarmThresholdUpsertBO.getShipAlarmThresholdList();
        String createUser = SecurityUtils.getUsername();
        for (ShipAlarmThresholdPO shipAlarmThresholdPO : shipAlarmThresholdList) {
            shipAlarmThresholdPO.setShipId(shipId);
            shipAlarmThresholdPO.setCreateTime(DateUtils.formatDateTime(System.currentTimeMillis()));
            shipAlarmThresholdPO.setUpdateTime(shipAlarmThresholdPO.getCreateTime());
            shipAlarmThresholdPO.setCreateUser(createUser);
            String threshold = shipAlarmThresholdPO.getThreshold();
            if ("".equals(threshold)){
                shipAlarmThresholdPO.setThreshold(null);
            }
        }
        //获取已有的预警阈值
        List<ShipAlarmThresholdPO> list = shipAlarmThresholdMapper.queryShipAlarmThreshold(shipId);
        Map<String, ShipAlarmThresholdPO> map = list.stream().collect(Collectors.toMap(ShipAlarmThresholdPO::getMerit, po -> po));

        for (int i = shipAlarmThresholdList.size() - 1; i >= 0; i--) {
            ShipAlarmThresholdPO po = shipAlarmThresholdList.get(i);
            String merit = po.getMerit();
            if (map.containsKey(merit)) {
                //已有的参数进行更新操作，并移除列表
                shipAlarmThresholdList.remove(i);
                //该shipId下已有的Merit
                ShipAlarmThresholdPO oldPo = map.get(merit);
                if (!oldPo.equals(po)){
                    //与老Merit不同，则更新，相同的不更新
                    shipAlarmThresholdMapper.updateShipAlarmThreshold(po);
                }
            }
        }
        if (!CollectionUtils.isEmpty(shipAlarmThresholdList)) {
            //剩下的批量写入
            shipAlarmThresholdMapper.batchInsertShipAlarmThreshold(shipAlarmThresholdList);
        }
    }

    @Override
    public String getThresholdLimit(String shipId, String merit) {
        String value = shipAlarmThresholdMapper.getThresholdLimit(shipId,merit);
        if (value == null){
            value = shipAlarmThresholdMapper.getThresholdLimitDefault(merit);
        }
        return value;
    }
}

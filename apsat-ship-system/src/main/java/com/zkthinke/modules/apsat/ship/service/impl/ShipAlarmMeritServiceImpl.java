package com.zkthinke.modules.apsat.ship.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.zkthinke.aop.log.Log;
import com.zkthinke.modules.apsat.ship.domain.ShipAlarmMeritDefaultPO;
import com.zkthinke.modules.apsat.ship.domain.ShipAlarmMeritPO;
import com.zkthinke.modules.apsat.ship.domain.ShipAlarmMeritUpsertBO;
import com.zkthinke.modules.apsat.ship.mapper.ShipAlarmMeritMapper;
import com.zkthinke.modules.apsat.ship.service.ShipAlarmMeritService;
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
public class ShipAlarmMeritServiceImpl implements ShipAlarmMeritService {

    @Autowired
    private ShipAlarmMeritMapper shipAlarmMeritMapper;

    @Override
    public List<ShipAlarmMeritPO> queryShipAlarmMerit(Integer shipId) {
        log.info("queryShipAlarmMerit 入参：{}", shipId);
        try {
            List<ShipAlarmMeritPO> list = shipAlarmMeritMapper.queryShipAlarmMerit(shipId);
            List<ShipAlarmMeritPO> defaultList = shipAlarmMeritMapper.queryShipAlarmMeritDefault();
            Map<String, ShipAlarmMeritPO> map = list.stream().collect(Collectors.toMap(ShipAlarmMeritPO::getMerit, po -> po));
            for (ShipAlarmMeritPO po : defaultList) {
                String merit = po.getMerit();
                if (!map.containsKey(merit)) {
                    map.put(merit, po);
                }
            }
            List<ShipAlarmMeritPO> resultList = new ArrayList<>();
            if (!CollectionUtils.isEmpty(map)) {
                resultList = map.values().stream().sorted((o1, o2) -> {
                    String merit1 = o1.getMerit();
                    String merit2 = o2.getMerit();
                    return merit1.compareTo(merit2);
                }).collect(Collectors.toList());
            }
            return resultList;
        } catch (Exception e) {
            log.error("queryShipAlarmMerit 异常：", e);
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional
    public void upsertShipAlarmMerit(ShipAlarmMeritUpsertBO shipAlarmMeritUpsertBO) {
        log.info("queryShipAlarmMerit 入参：{}", JSONObject.toJSONString(shipAlarmMeritUpsertBO));
        //组装前端传参
        Integer shipId = shipAlarmMeritUpsertBO.getShipId();
        List<ShipAlarmMeritPO> shipAlarmMeritList = shipAlarmMeritUpsertBO.getShipAlarmMeritList();
        String createUser = SecurityUtils.getUsername();
        for (ShipAlarmMeritPO shipAlarmMeritPO : shipAlarmMeritList) {
            shipAlarmMeritPO.setShipId(shipId);
            shipAlarmMeritPO.setCreateTime(DateUtils.formatDateTime(System.currentTimeMillis()));
            shipAlarmMeritPO.setUpdateTime(shipAlarmMeritPO.getCreateTime());
            shipAlarmMeritPO.setCreateUser(createUser);
            String low = shipAlarmMeritPO.getLow();
            if ("".equals(low)){
                shipAlarmMeritPO.setLow(null);
            }
            String high = shipAlarmMeritPO.getHigh();
            if ("".equals(high)){
                shipAlarmMeritPO.setHigh(null);
            }
        }
        //获取已有的预警参数
        List<ShipAlarmMeritPO> list = shipAlarmMeritMapper.queryShipAlarmMerit(shipId);
        Map<String, ShipAlarmMeritPO> map = list.stream().collect(Collectors.toMap(ShipAlarmMeritPO::getMerit, po -> po));

        for (int i = shipAlarmMeritList.size() - 1; i >= 0; i--) {
            ShipAlarmMeritPO po = shipAlarmMeritList.get(i);
            String merit = po.getMerit();
            if (map.containsKey(merit)) {
                //已有的参数进行更新操作，并移除列表
                shipAlarmMeritList.remove(i);
                //该shipId下已有的Merit
                ShipAlarmMeritPO oldPo = map.get(merit);
                if (!oldPo.equals(po)) {
                    //与老Merit不同，则更新，相同的不更新
                    shipAlarmMeritMapper.updateShipAlarmMerit(po);
                }
            }
        }
        if (!CollectionUtils.isEmpty(shipAlarmMeritList)) {
            //剩下的批量写入
            shipAlarmMeritMapper.batchInsertShipAlarmMerit(shipAlarmMeritList);
        }
    }

    @Override
    public List<ShipAlarmMeritDefaultPO> queryValidShipAlarmMerit(Integer shipId) {
        log.info("queryValidShipAlarmMerit 入参：{}", shipId);
        try {
            List<ShipAlarmMeritPO> list = queryShipAlarmMerit(shipId);
            return list.stream().filter(po -> po.getState() == 1).map(ShipAlarmMeritDefaultPO::new).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("queryValidShipAlarmMerit 异常：", e);
            return new ArrayList<>();
        }
    }
}

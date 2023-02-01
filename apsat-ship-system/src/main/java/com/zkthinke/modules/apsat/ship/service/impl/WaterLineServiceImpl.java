package com.zkthinke.modules.apsat.ship.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.zkthinke.modules.apsat.ship.domain.WaterDeepLineBO;
import com.zkthinke.modules.apsat.ship.mapper.WaterDeepMapper;
import com.zkthinke.modules.apsat.ship.service.WaterLineService;
import com.zkthinke.modules.apsat.ship.utils.WaterDeepUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class WaterLineServiceImpl implements WaterLineService {

    @Resource
    private WaterDeepMapper waterDeepMapper;

    /**
     * 获取水深
     * @param waterDeep
     * @return
     */
    @Override
    public JSONObject waterService(String waterDeep) {
        JSONObject resultList = null;
        try {
            resultList = WaterDeepUtil.getWatersodiff(waterDeep);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultList;
    }

    @Override
    public List<WaterDeepLineBO> getWaterDeepLineList() {
        log.info("getWaterDeepLineList接口");
        List<WaterDeepLineBO> waterDeepLineList = new ArrayList<>();
        try {
            waterDeepLineList = waterDeepMapper.getWaterDeepLineList();
        } catch (Exception e) {
            log.info("getWaterDeepLineList 异常：", e);
        }
        return waterDeepLineList;
    }
}

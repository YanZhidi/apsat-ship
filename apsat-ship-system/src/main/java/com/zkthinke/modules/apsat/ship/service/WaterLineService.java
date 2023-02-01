package com.zkthinke.modules.apsat.ship.service;

import com.alibaba.fastjson.JSONObject;
import com.zkthinke.modules.apsat.ship.domain.WaterDeepLineBO;

import java.util.List;

/**
 * 水的等位线接口
 */
public interface WaterLineService {
    JSONObject waterService(String waterDeep);

    List<WaterDeepLineBO> getWaterDeepLineList();
}

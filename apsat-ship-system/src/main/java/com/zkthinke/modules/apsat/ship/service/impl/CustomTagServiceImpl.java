package com.zkthinke.modules.apsat.ship.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.zkthinke.modules.apsat.ship.domain.CustomTagPO;
import com.zkthinke.modules.apsat.ship.domain.CustomTagPointPO;
import com.zkthinke.modules.apsat.ship.mapper.CustomTagMapper;
import com.zkthinke.modules.apsat.ship.service.CustomTagService;
import com.zkthinke.utils.SecurityUtils;
import com.zkthinke.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class CustomTagServiceImpl implements CustomTagService {

    @Autowired
    private CustomTagMapper customTagMapper;

    @Override
    public List<CustomTagPO> getCustomTagList() {
        Long userId = SecurityUtils.getUserId();
        log.info("getTyphoonDetailList 入参：{}", userId);
        List<CustomTagPO> list = new ArrayList<>();
        try {
            list = customTagMapper.getCustomTagList(userId);
        } catch (Exception e) {
            log.error("getTyphoonDetailList 异常：", e);
        }
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CustomTagPO addCustomTag(CustomTagPO customTagPO) {
        log.info("addCustomTag 入参：{}", JSONObject.toJSONString(customTagPO));
        CheckParam(customTagPO);

        Long userId = SecurityUtils.getUserId();
        customTagPO.setUserId(userId);
        customTagMapper.addCustomTag(customTagPO);
        customTagMapper.addCustomTagPointList(customTagPO.getPointList(), customTagPO.getId());
        customTagPO.setUserId(null);
        return customTagPO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCustomTag(CustomTagPO customTagPO) {
        log.info("updateCustomTag 入参：{}", JSONObject.toJSONString(customTagPO));
        CheckParam(customTagPO);
        Long id = customTagPO.getId();
        if (id == null) {
            throw new RuntimeException("【id】不能为空");
        }
        Long userId = SecurityUtils.getUserId();
        customTagPO.setUserId(userId);

        int num = customTagMapper.updateCustomTag(customTagPO);
        if (num == 0) {
            throw new RuntimeException("【id=" + id + "】此标注不存在");
        }
        customTagMapper.deleteCustomTagList(customTagPO);
        customTagMapper.addCustomTagPointList(customTagPO.getPointList(), id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCustomTag(CustomTagPO customTagPO) {
        log.info("deleteCustomTag 入参：{}", JSONObject.toJSONString(customTagPO));

        customTagMapper.deleteCustomTag(customTagPO);
        customTagMapper.deleteCustomTagList(customTagPO);
    }

    private void CheckParam(CustomTagPO po) {
        String name = po.getName();
        if (StringUtils.isEmpty(name)) {
            throw new RuntimeException("【name】不能为空");
        }
        String type = po.getType();
        if (StringUtils.isEmpty(type)) {
            throw new RuntimeException("【type】不能为空");
        }
        String radius = po.getRadius();
        if ("4".equals(type) && StringUtils.isEmpty(radius)) {
            throw new RuntimeException("【radius】不能为空");
        }
        List<CustomTagPointPO> pointList = po.getPointList();
        if (CollectionUtils.isEmpty(pointList)) {
            throw new RuntimeException("【pointList】不能为空");
        }
        if (pointList.stream().anyMatch(e -> StringUtils.isEmpty(e.getLongitude()) || StringUtils.isEmpty(e.getLatitude()))) {
            throw new RuntimeException("【经纬度】不能为空");
        }
    }
}

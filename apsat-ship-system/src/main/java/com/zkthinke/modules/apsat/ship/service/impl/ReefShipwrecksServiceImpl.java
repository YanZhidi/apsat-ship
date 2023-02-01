package com.zkthinke.modules.apsat.ship.service.impl;

import com.zkthinke.modules.apsat.ship.domain.PointBO;
import com.zkthinke.modules.apsat.ship.mapper.ReefShipwrecksMapper;
import com.zkthinke.modules.apsat.ship.service.ReefShipwrecksService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ReefShipwrecksServiceImpl implements ReefShipwrecksService {

    @Resource
    private ReefShipwrecksMapper reefShipwrecksMapper;

    @Override
    public List<PointBO> getReefList() {
        log.info("查询暗礁岛屿");
        List<PointBO> resultList = new ArrayList<>();
        try {
            resultList = reefShipwrecksMapper.getReefList();
        } catch (Exception e) {
            log.info("查询暗礁岛屿异常：", e);
        }
        return resultList;
    }

    @Override
    public List<PointBO> getShipwrecksList() {
        log.info("查询沉船点");
        List<PointBO> resultList = new ArrayList<>();
        try {
            resultList = reefShipwrecksMapper.getShipwrecksList();
        } catch (Exception e) {
            log.info("查询沉船点异常：", e);
        }
        return resultList;
    }

}

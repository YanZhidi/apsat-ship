package com.zkthinke.modules.apsat.ship.service.impl;

import com.zkthinke.modules.apsat.ship.domain.ArtificialFenceBO;
import com.zkthinke.modules.apsat.ship.mapper.ArtificialFenceMapper;
import com.zkthinke.modules.apsat.ship.service.ArtificialFenceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ArtificialFenceServiceImpl implements ArtificialFenceService {

    @Autowired
    private ArtificialFenceMapper artificialFenceMapper;

    @Override
    public List<ArtificialFenceBO> getArtificialFenceList() {
        log.info("getArtificialFenceList 接口");
        List<ArtificialFenceBO> artificialFenceList = new ArrayList<>();
        try {
           artificialFenceList = artificialFenceMapper.getArtificialFenceList();
        } catch (Exception e) {
            log.info("getArtificialFenceList 异常：", e);
        }
        return artificialFenceList;
    }
}

package com.zkthinke.modules.apsat.ship.service.impl;

import com.zkthinke.modules.apsat.ship.domain.NavigationWarningMSIbyAreaDTO;
import com.zkthinke.modules.apsat.ship.service.NavigationWarningService;
import com.zkthinke.modules.apsat.ship.utils.NavigationWarningUtil;
import com.zkthinke.response.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j
public class NavigationWarningServiceImpl implements NavigationWarningService {

    @Resource
    private NavigationWarningUtil navigationWarningUtil;

    @Override
    public ResponseResult getNoticeByArea(NavigationWarningMSIbyAreaDTO navigationWarningMSIbyAreaDTO) {
        String area = navigationWarningMSIbyAreaDTO.getArea();
        Integer type = navigationWarningMSIbyAreaDTO.getType();
        String language = navigationWarningMSIbyAreaDTO.getLanguage();
        return navigationWarningUtil.getNoticeByArea(area, type, language);
    }
}

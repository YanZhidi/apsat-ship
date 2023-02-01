package com.zkthinke.modules.apsat.ship.service;

import com.zkthinke.modules.apsat.ship.domain.NavigationWarningMSIbyAreaDTO;
import com.zkthinke.response.ResponseResult;

public interface NavigationWarningService {

    ResponseResult getNoticeByArea(NavigationWarningMSIbyAreaDTO navigationWarningMSIbyAreaDTO);


}

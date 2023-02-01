package com.zkthinke.modules.apsat.ship.service;

import com.zkthinke.modules.apsat.ship.domain.TyphoonBO;
import com.zkthinke.modules.apsat.ship.domain.TyphoonTrackBO;

import java.util.List;

public interface TyphoonService {
    List<TyphoonBO> getTyphoonList(String year);

    List<TyphoonTrackBO> getTyphoonTrackList(String code);

    List<TyphoonBO> getActivatedTyphoonDetailList();
}

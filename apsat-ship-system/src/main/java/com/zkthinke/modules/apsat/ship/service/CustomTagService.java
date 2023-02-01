package com.zkthinke.modules.apsat.ship.service;

import com.zkthinke.modules.apsat.ship.domain.CustomTagPO;

import java.util.List;

public interface CustomTagService {
    List<CustomTagPO> getCustomTagList();

    CustomTagPO addCustomTag(CustomTagPO customTagPO);

    void updateCustomTag(CustomTagPO customTagPO);

    void deleteCustomTag(CustomTagPO customTagPO);
}

package com.zkthinke.modules.apsat.ship.mapper;

import com.zkthinke.modules.apsat.ship.domain.CustomTagPO;
import com.zkthinke.modules.apsat.ship.domain.CustomTagPointPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CustomTagMapper {
    List<CustomTagPO> getCustomTagList(Long userId);

    List<CustomTagPointPO> getCustomTagPointList(Long tagId);

    void addCustomTag(CustomTagPO customTagPO);

    void addCustomTagPointList(@Param("pointList") List<CustomTagPointPO> pointList,@Param("tagId") Long tagId);

    int updateCustomTag(CustomTagPO customTagPO);

    void deleteCustomTag(CustomTagPO customTagPO);

    void deleteCustomTagList(CustomTagPO customTagPO);
}

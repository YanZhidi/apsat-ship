package com.zkthinke.modules.apsat.ship.mapper;

import com.zkthinke.modules.apsat.ship.domain.NavigationInformationHistoryPO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface NavigationInformationHistoryMapper {
    int deleteByPrimaryKey(Long id);

    int insert(NavigationInformationHistoryPO record);

    int insertSelective(NavigationInformationHistoryPO record);

    NavigationInformationHistoryPO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(NavigationInformationHistoryPO record);

    int updateByPrimaryKey(NavigationInformationHistoryPO record);

    Integer getCount(NavigationInformationHistoryPO navigationInformationHistoryPO);

    NavigationInformationHistoryPO getNavigationHistory(@Param("mmsi") String mmsi, @Param("dataSyncTime") Date dataSyncTime);
}
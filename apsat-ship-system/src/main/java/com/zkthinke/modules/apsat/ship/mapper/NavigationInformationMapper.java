package com.zkthinke.modules.apsat.ship.mapper;

import com.zkthinke.modules.apsat.ship.domain.NavigationInformationPO;
import com.zkthinke.modules.apsat.ship.domain.ShipDetailPO;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 航行最新表
 */
public interface NavigationInformationMapper {
    int insert(ShipDetailPO record);
    int updateByPrimaryKey(ShipDetailPO record);
    ShipDetailPO getNavigation(@Param("mmsi")String mmsi);
    int getCount(@Param("mmsi") String mmsi);
    String getZdaTime();
}

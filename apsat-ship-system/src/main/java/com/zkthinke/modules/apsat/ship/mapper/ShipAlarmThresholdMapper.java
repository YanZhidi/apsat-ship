package com.zkthinke.modules.apsat.ship.mapper;

import com.zkthinke.modules.apsat.ship.domain.ShipAlarmThresholdPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShipAlarmThresholdMapper {

    List<ShipAlarmThresholdPO> queryShipAlarmThreshold(Integer shipId);

    List<ShipAlarmThresholdPO> queryShipAlarmThresholdDefault();

    void updateShipAlarmThreshold(ShipAlarmThresholdPO po);

    void batchInsertShipAlarmThreshold(List<ShipAlarmThresholdPO> shipAlarmThresholdList);

    String getThresholdLimit(@Param("shipId") String shipId,@Param("merit") String merit);

    String getThresholdLimitDefault(String merit);
}

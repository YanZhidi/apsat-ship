package com.zkthinke.modules.apsat.ship.mapper;

import com.zkthinke.modules.apsat.ship.domain.ShipRoutePlanAlarm;
import com.zkthinke.modules.apsat.ship.service.dto.ShipRoutePlanAlarmQueryCriteria;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShipRoutePlanAlarmDao {

    List<ShipRoutePlanAlarm> findAlarmByParam(@Param("criteria") ShipRoutePlanAlarmQueryCriteria criteria, @Param("shipIds") List<Long> shipIds);

    List<ShipRoutePlanAlarm> findAlarmLimit(String shipId);

    void deleteAlarm(String shipId);

    void insertAlarm(ShipRoutePlanAlarm alarm);

    void batchInsertAlarm(List<ShipRoutePlanAlarm> alarmList);
}





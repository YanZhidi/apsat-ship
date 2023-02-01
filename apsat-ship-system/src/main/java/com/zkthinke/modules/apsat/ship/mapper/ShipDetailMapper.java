package com.zkthinke.modules.apsat.ship.mapper;

import com.zkthinke.modules.apsat.ship.detail.BO.DetailVO;
import com.zkthinke.modules.apsat.ship.domain.ShipDetailBO;
import com.zkthinke.modules.apsat.ship.domain.ShipDetailHistoryPO;
import com.zkthinke.modules.apsat.ship.domain.ShipDetailPO;
import com.zkthinke.modules.apsat.ship.domain.ShipDetailPO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @Entity com.zkthinke.modules.apsat.ship.domain.ShipDetailPO
 */
public interface ShipDetailMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ShipDetailHistoryPO record);

    ShipDetailPO selectByPrimaryKey(Long id);

    int updateByPrimaryKey(ShipDetailHistoryPO record);

    Integer getCount(ShipDetailPO ShipDetailPO);

    ShipDetailHistoryPO getNavigationHistory(@Param("mmsi") String mmsi, @Param("collectTime") Long collectTime);

    List<DetailVO> getSpeedByIdAndTime(@Param("shipId") Long shipId,@Param("collectTimeBegin") Long collectTimeBegin,@Param("collectTimeEnd") Long collectTimeEnd);

    List<DetailVO> getSpendByIdAndTime(Long shipId, Long collectTimeBegin, Long collectTimeEnd);

    List<DetailVO> findPstPower(@Param("shipId") Long shipId,@Param("collectTimeBegin") Long collectTimeBegin,@Param("collectTimeEnd") Long collectTimeEnd);

    List<ShipDetailBO> findShipDetailList(@Param("shipId") Long shipId, @Param("collectTimeBegin") Long collectTimeBegin, @Param("collectTimeEnd") Long collectTimeEnd);
}
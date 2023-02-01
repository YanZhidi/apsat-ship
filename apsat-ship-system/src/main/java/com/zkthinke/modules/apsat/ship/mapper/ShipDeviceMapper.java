package com.zkthinke.modules.apsat.ship.mapper;

import com.zkthinke.modules.apsat.ship.device.domain.*;
import com.zkthinke.modules.apsat.ship.domain.ShipDeviceBO;
import com.zkthinke.modules.apsat.ship.domain.ShipDeviceModPO;
import com.zkthinke.modules.apsat.ship.domain.ShipDevicePO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface ShipDeviceMapper {
    //查询设备历史
//     List<ShipDevicePO> getDeviceInformation(QryDeviceReqBO qryDeviceReqBO);
    ShipDevicePO getDeviceInfoByImoNumber(@Param("name") String name, @Param("imoNumber") String imoNumber, @Param("dataSyncTime") Date dataSyncTime);
//    int deleteByPrimaryKey(Long id);

    Integer insert(ShipDevicePO record);

//    int insertSelective(ShipDevicePO record);

//    ShipDevicePO selectByPrimaryKey(Long id);

//    int updateByPrimaryKeySelective(ShipDevicePO record);

    int updateByPrimaryKey(ShipDevicePO record);
    int getCount(@Param("name") String name,@Param("imoNumber") String imoNumber,@Param("dataSyncTime") Date dataSyncTime);


    List<ShipDeviceModPO> getFuelConmuseList(@Param("shipId") Long shipId, @Param("begin") Long collectTimeBegin, @Param("end") Long collectTimeEnd);

    DeviceRealTimeBO getRealTimeFuelConsumptionData(Long shipId);

    ShipDevicePO realTimeEquipmentStatus(Long shipId);

    List<ShipDevicePO> getAll();

    List<SmartCabinStatusPO> findSmartCabinStatusList(@Param("shipId") Long shipId,@Param("begin") Long collectTimeBegin,@Param("end") Long collectTimeEnd);

    List<RevolutionGroundSpeedBO> findRotationGroundSpeedList(@Param("shipId") Long shipId,@Param("begin") Long collectTimeBegin,@Param("end") Long collectTimeEnd);

    List<AlopStatusPO> findAlotList(@Param("shipId") Long shipId, @Param("begin") Long collectTimeBegin, @Param("end") Long collectTimeEnd);

    HostDetailsBO findHostDetails(Long shipId);

    List<ShipDeviceBO> findShipDeviceList(@Param("shipId") Long shipId, @Param("begin") Long collectTimeBegin, @Param("end") Long collectTimeEnd);
}

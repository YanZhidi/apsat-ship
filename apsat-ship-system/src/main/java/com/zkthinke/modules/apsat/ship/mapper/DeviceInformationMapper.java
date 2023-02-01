package com.zkthinke.modules.apsat.ship.mapper;

import com.zkthinke.modules.apsat.ship.domain.DeviceInformationPO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface DeviceInformationMapper {
    //查询设备历史
//     List<DeviceInformationPO> getDeviceInformation(QryDeviceReqBO qryDeviceReqBO);
    DeviceInformationPO getDeviceInfoByImoNumber(@Param("name") String name,@Param("imoNumber") String imoNumber,@Param("dataSyncTime") Date dataSyncTime);
//    int deleteByPrimaryKey(Long id);

    int insert(DeviceInformationPO record);

//    int insertSelective(DeviceInformationPO record);

//    DeviceInformationPO selectByPrimaryKey(Long id);

//    int updateByPrimaryKeySelective(DeviceInformationPO record);

    int updateByPrimaryKey(DeviceInformationPO record);
    int getCount(@Param("name") String name,@Param("shipId") Long shipId,@Param("dataSyncTime") Date dataSyncTime);
}

package com.zkthinke.modules.apsat.ship.mapper;

import com.zkthinke.modules.apsat.ship.domain.ShipPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;

@Mapper
public interface ShipMapper {
    /**
     * @mbg.generated
     */
    int deleteByPrimaryKey(Long id);

    /**
     * @mbg.generated
     */
    int insert(ShipPO record);

    /**
     * @mbg.generated
     */
    int insertSelective(ShipPO record);

    /**
     * @mbg.generated
     */
    ShipPO selectByPrimaryKey(Long id);

    /**
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(ShipPO record);

    /**
     * @mbg.generated
     */
    int updateByPrimaryKey(ShipPO record);



    Long selectShipByMmsi(@Param("mmsiNumber") String mmsiNumber);

    ShipPO selectShipInfoByMmsi(@Param("mmsiNumber") String mmsiNumber);

    int updateLastDetailId(@Param("id")Long id,@Param("lastDetailId") Long lastDetailId,@Param("name") String name);

    /**
     * 根据userID和角色ID联表查出对应船舶信息
     * @param userId
     * @return
     */
    List<ShipPO> selectShipByRoleAndUserId(Long userId);
}

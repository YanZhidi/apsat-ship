package com.zkthinke.modules.apsat.ship.mapper;

import com.zkthinke.modules.apsat.ship.domain.ShipVdmPO;

import java.util.List;

public interface ShipVdmMapper {

    List<ShipVdmPO> getShipVdmListByShipId(String shipId);

    void upsertVdm1List(List<ShipVdmPO> list);

    void upsertVdm2List(List<ShipVdmPO> list);

    void upsertVdm3List(List<ShipVdmPO> list);

    void upsertVdm5List(List<ShipVdmPO> list);

    void upsertVdm18List(List<ShipVdmPO> list);

    void upsertVdm19List(List<ShipVdmPO> list);

    void upsertVdm24AList(List<ShipVdmPO> list);

    void upsertVdm24BList(List<ShipVdmPO> list);

    void upsertVdm27List(List<ShipVdmPO> list);

}

package com.zkthinke.modules.apsat.ship.domain;

import lombok.Data;

import java.util.List;

@Data
public class ShipDeviceModDTO {
    List<ShipDeviceModPO> steamList;
    List<ShipDeviceModPO> hostImList;
    List<ShipDeviceModPO> hostOutList;
    List<ShipDeviceModPO> generImList;
    List<ShipDeviceModPO> generOutList;
}

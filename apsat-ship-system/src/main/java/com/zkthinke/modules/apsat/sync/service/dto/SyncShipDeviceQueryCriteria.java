package com.zkthinke.modules.apsat.sync.service.dto;

import lombok.Data;
import com.zkthinke.annotation.Query;

import java.util.List;

/**
* @author weicb
* @date 2020-10-28
*/
@Data
public class SyncShipDeviceQueryCriteria{

    @Query
    private  String sourceId;

    @Query(propName="sourceId", type= Query.Type.IN)
    private List<String> sourceIds;
}
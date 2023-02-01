package com.zkthinke.modules.apsat.sync.service.dto;

import lombok.Data;
import com.zkthinke.annotation.Query;

import java.util.List;

/**
* @author weicb
* @date 2020-10-19
*/
@Data
public class SyncShipQueryCriteria{

    @Query
    private  String sourceId;

    @Query(propName="sourceId", type= Query.Type.IN)
    private List<String> sourceIds;
}
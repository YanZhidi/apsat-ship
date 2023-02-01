package com.zkthinke.modules.apsat.ship.service.impl;

import cn.hutool.core.util.StrUtil;
import com.zkthinke.modules.apsat.ship.domain.ShipVdmBO;
import com.zkthinke.modules.apsat.ship.domain.ShipVdmPO;
import com.zkthinke.modules.apsat.ship.mapper.ShipVdmMapper;
import com.zkthinke.modules.apsat.ship.service.ShipVdmService;
import com.zkthinke.modules.apsat.ship.utils.FieldUnitUtil;
import com.zkthinke.modules.common.utils.DateUtils;
import com.zkthinke.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ShipVdmServiceImpl implements ShipVdmService {

    @Autowired
    private ShipVdmMapper shipVdmMapper;

    @Override
    public List<ShipVdmBO> getShipVdmListByShipId(String shipId){
        log.info("getShipVdmListByShipId入参：{}",shipId);

        List<ShipVdmBO> list = new ArrayList<>();
        try {
            List<ShipVdmPO> shipVdmPOList = shipVdmMapper.getShipVdmListByShipId(shipId);
            list = shipVdmPOList.stream().map(po->{
                ShipVdmBO bo = new ShipVdmBO(po);
                String shipDimensions = po.getShipDimensions();
                try {
                    //计算船长和船宽
                    String[] sp = shipDimensions.split(",");
                    String a = sp[0].split("=")[1];
                    String b = sp[1].split("=")[1];
                    String c = sp[2].split("=")[1];
                    String d = sp[3].split("=")[1];
                    String shipLength = Long.valueOf(a)+Long.valueOf(b)+"";
                    String shipWidth = Long.valueOf(c)+Long.valueOf(d)+"";
                    bo.setShipLength(shipLength);
                    bo.setShipWidth(shipWidth);
                } catch (Exception e) {
                    //设置长宽都为0
                    bo.setShipLength("0");
                    bo.setShipWidth("0");
                }
                String eta = bo.getEta();
                if (!StringUtils.isEmpty(eta)){
                    bo.setEta(DateUtils.formatDateTime(Long.valueOf(eta)));
                }
                removeUnit(bo);
                return bo;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("getShipVdmListByShipId异常：",e);
        }
        return list;
    }

    public void removeUnit(ShipVdmBO bo){
        bo.setCog(FieldUnitUtil.removeUnit(bo.getCog()));
        bo.setSog(FieldUnitUtil.removeUnit(bo.getSog()));
        bo.setTrueHeading(FieldUnitUtil.removeUnit(bo.getTrueHeading()));
        bo.setMaxStaticDraft(FieldUnitUtil.removeUnit(bo.getMaxStaticDraft()));

    }
}

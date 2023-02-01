package com.zkthinke.modules.apsat.ship.device.service.impl;

import com.zkthinke.modules.apsat.ship.device.domain.*;
import com.zkthinke.modules.apsat.ship.device.service.DeviceService;
import com.zkthinke.modules.apsat.ship.domain.ShipDeviceBO;
import com.zkthinke.modules.apsat.ship.domain.ShipDeviceModBO;
import com.zkthinke.modules.apsat.ship.domain.ShipDeviceModPO;
import com.zkthinke.modules.apsat.ship.domain.ShipDevicePO;
import com.zkthinke.modules.apsat.ship.mapper.ShipDeviceMapper;
import com.zkthinke.modules.apsat.ship.mapper.ShipDeviceModMapper;
import com.zkthinke.modules.apsat.ship.utils.FieldUnitUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DeviceServiceImpl implements DeviceService {

    @Autowired
    private ShipDeviceMapper shipDeviceMapper;
    @Autowired
    private ShipDeviceModMapper shipDeviceModMapper;

    @Override
    public Map<Long, Map<String, Object>> findShipFuelData(Long shipId, Long collectTimeBegin, Long collectTimeEnd) {
        List<ShipDeviceModPO> deviceMefcPOList = shipDeviceMapper.getFuelConmuseList(shipId, collectTimeBegin, collectTimeEnd);
        Map<Long, Map<String, Object>> map = new HashMap<>();
        for (ShipDeviceModPO po : deviceMefcPOList) {
            Map<String, Object> resultMap = new HashMap<>();

            BigDecimal bd0 = new BigDecimal(0);
            //主机燃油消耗
            BigDecimal mefioBD = new BigDecimal(po.getZjjkll()).subtract(new BigDecimal(po.getZjckll()));
            if (mefioBD.compareTo(bd0) < 0) {
                mefioBD = bd0;
            }
            //辅机燃油消耗
            BigDecimal sefioBD = new BigDecimal(po.getFdjjkll()).subtract(new BigDecimal(po.getFdjckll()));
            if (sefioBD.compareTo(bd0) < 0) {
                sefioBD = bd0;
            }
            //锅炉燃油消耗
            BigDecimal bfoioBD = new BigDecimal(po.getGlll()).subtract(new BigDecimal(po.getGlckll()));
            if (bfoioBD.compareTo(bd0) < 0) {
                bfoioBD = bd0;
            }
            //总燃油消耗
            BigDecimal totalio = mefioBD.add(sefioBD).add(bfoioBD);

            resultMap.put("mefio", mefioBD.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
            resultMap.put("sefio", sefioBD.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
            resultMap.put("bfoio", bfoioBD.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
            resultMap.put("totalio", totalio.setScale(2, BigDecimal.ROUND_HALF_UP).toString());

            Long dataSyncTime = Long.parseLong(po.getDataSyncTime());
            resultMap.put("collectTime", dataSyncTime);
            map.put(dataSyncTime, resultMap);
        }
        return map;
    }

    @Override
    public DeviceRealTimeBO realTimeFuelConsumptionData(Long shipId) {
        DeviceRealTimeBO po = shipDeviceMapper.getRealTimeFuelConsumptionData(shipId);

        BigDecimal bd0 = new BigDecimal(0);
        //主机燃油消耗
        BigDecimal mefioBD = new BigDecimal(po.getZjjkll()).subtract(new BigDecimal(po.getZjckll()));
        if (mefioBD.compareTo(bd0) < 0) {
            mefioBD = bd0;
        }
        //辅机燃油消耗
        BigDecimal sefioBD = new BigDecimal(po.getFdjjkll()).subtract(new BigDecimal(po.getFdjckll()));
        if (sefioBD.compareTo(bd0) < 0) {
            sefioBD = bd0;
        }
        //锅炉燃油消耗
        BigDecimal bfoioBD = new BigDecimal(po.getGlll()).subtract(new BigDecimal(po.getGlckll()));
        if (bfoioBD.compareTo(bd0) < 0) {
            bfoioBD = bd0;
        }
        //总燃油消耗
        BigDecimal totalio = mefioBD.add(sefioBD).add(bfoioBD);

        po.setMefio(mefioBD.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        po.setSefio(sefioBD.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        po.setBfoio(bfoioBD.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        po.setTotalio(totalio.setScale(2, BigDecimal.ROUND_HALF_UP).toString());

        if (shipId == 190 || shipId == 193){
            //神华536和神华515没有锅炉出口
            po.setGlckll("-");
            po.setGlckmd("-");
            po.setGlckwd("-");
        }

        return po;
    }

    @Override
    public ShipDevicePO realTimeEquipmentStatus(Long shipId) {
        ShipDevicePO shipDevicePO = shipDeviceMapper.realTimeEquipmentStatus(shipId);

        return shipDevicePO;
    }

    @Override
    public SmartCabinBO smartCabin(Long shipId, Long collectTimeBegin, Long collectTimeEnd) {
        SmartCabinBO smartCabinBO = new SmartCabinBO();
        List<SmartCabinStatusPO> smartCabinStatusPOList = shipDeviceMapper.findSmartCabinStatusList(shipId, collectTimeBegin, collectTimeEnd);
        List<SmartCabinStatusBO> list = smartCabinStatusPOList.stream().map(SmartCabinStatusBO::new).collect(Collectors.toList());
        smartCabinBO.setSmartCabinStatusBOList(list);

        //获取最后一个PO的字段
        if (!CollectionUtils.isEmpty(smartCabinStatusPOList)) {
            SmartCabinStatusPO smartCabinStatusPO = smartCabinStatusPOList.get(smartCabinStatusPOList.size() - 1);
            smartCabinBO.setMefc(smartCabinStatusPO.getMefc());
            smartCabinBO.setRevolutionSpeed(smartCabinStatusPO.getRevolutionSpeed());
            smartCabinBO.setCoolSeaWPress(smartCabinStatusPO.getCoolSeaWPress());
            smartCabinBO.setMejip(smartCabinStatusPO.getMejip());
            smartCabinBO.setMejit(smartCabinStatusPO.getMejit());
            smartCabinBO.setGeneratorRunning1(smartCabinStatusPO.getGeneratorRunning1());
            smartCabinBO.setGeneratorRunning2(smartCabinStatusPO.getGeneratorRunning2());
            smartCabinBO.setGeneratorRunning3(smartCabinStatusPO.getGeneratorRunning3());
        }

        return smartCabinBO;
    }

    @Override
    public List<RevolutionGroundSpeedBO> findRotationGroundSpeedList(Long shipId, Long collectTimeBegin, Long collectTimeEnd) {
        List<RevolutionGroundSpeedBO> list = shipDeviceMapper.findRotationGroundSpeedList(shipId, collectTimeBegin, collectTimeEnd);

        return list;
    }

    @Override
    public AlopBO findAlopList(Long shipId, Long collectTimeBegin, Long collectTimeEnd) {
        AlopBO alopBO = new AlopBO();
        List<AlopStatusPO> alopPOList = shipDeviceMapper.findAlotList(shipId, collectTimeBegin, collectTimeEnd);
        List<String> alop1List = new ArrayList<>();
        List<String> alop2List = new ArrayList<>();
        List<String> alop3List = new ArrayList<>();
        List<Long> collectTimeList = new ArrayList<>();

        alopPOList.forEach(po -> {
            alop1List.add(po.getAlop1().replace("Mpa", ""));
            alop2List.add(po.getAlop2().replace("Mpa", ""));
            alop3List.add(po.getAlop3().replace("Mpa", ""));
            collectTimeList.add(po.getCollectTime());
        });
        alopBO.setAlop1List(alop1List);
        alopBO.setAlop2List(alop2List);
        alopBO.setAlop3List(alop3List);
        alopBO.setCollectTime(collectTimeList);

        if (!CollectionUtils.isEmpty(alopPOList)){
            AlopStatusPO alopStatusPO = alopPOList.get(alopPOList.size() - 1);
            alopBO.setGeneratorRunning1(alopStatusPO.getGeneratorRunning1());
            alopBO.setGeneratorRunning2(alopStatusPO.getGeneratorRunning2());
            alopBO.setGeneratorRunning3(alopStatusPO.getGeneratorRunning3());
        }

        return alopBO;
    }

    @Override
    public HostDetailsBO hostDetails(Long shipId) {
        HostDetailsBO hostDetailsBO = shipDeviceMapper.findHostDetails(shipId);
        return hostDetailsBO;
    }
    @Override
    public List<ShipDeviceBO> findShipDeviceList(Long shipId, Long collectTimeBegin, Long collectTimeEnd) {
        List<ShipDeviceBO> list = shipDeviceMapper.findShipDeviceList(shipId,collectTimeBegin,collectTimeEnd);
        //去掉单位
        for (ShipDeviceBO bo : list) {
            removeUnit(bo);
        }
        return list;
    }

    @Override
    public List<ShipDeviceModBO> findShipDeviceModList(Long shipId, Long collectTimeBegin, Long collectTimeEnd) {
        List<ShipDeviceModBO> list = shipDeviceModMapper.findShipDeviceModList(shipId,collectTimeBegin,collectTimeEnd);
        //shipDeviceMod数据没有单位
        return list;
    }

    private void removeUnit(ShipDeviceBO bo) {
        bo.setRevolutionSpeed(FieldUnitUtil.removeUnit(bo.getRevolutionSpeed()));
        bo.setStartingAirPressure(FieldUnitUtil.removeUnit(bo.getStartingAirPressure()));
        bo.setMeav(FieldUnitUtil.removeUnit(bo.getMeav()));
        bo.setMeloip(FieldUnitUtil.removeUnit(bo.getMeloip()));
        bo.setMefc(FieldUnitUtil.removeUnit(bo.getMefc()));
        bo.setMeloit(FieldUnitUtil.removeUnit(bo.getMeloit()));
        bo.setSloip(FieldUnitUtil.removeUnit(bo.getSloip()));
        bo.setSloot(FieldUnitUtil.removeUnit(bo.getSloot()));
        bo.setFip(FieldUnitUtil.removeUnit(bo.getFip()));
        bo.setFit(FieldUnitUtil.removeUnit(bo.getFit()));
        bo.setCloit(FieldUnitUtil.removeUnit(bo.getCloit()));
        bo.setMejip(FieldUnitUtil.removeUnit(bo.getMejip()));
        bo.setMejit(FieldUnitUtil.removeUnit(bo.getMejit()));
        bo.setSiet(FieldUnitUtil.removeUnit(bo.getSiet()));
        bo.setSoet(FieldUnitUtil.removeUnit(bo.getSoet()));
        bo.setControlAirPressure(FieldUnitUtil.removeUnit(bo.getControlAirPressure()));
        bo.setAlop1(FieldUnitUtil.removeUnit(bo.getAlop1()));
        bo.setAlot1(FieldUnitUtil.removeUnit(bo.getAlot1()));
        bo.setAfp1(FieldUnitUtil.removeUnit(bo.getAfp1()));
        bo.setAft1(FieldUnitUtil.removeUnit(bo.getAft1()));
        bo.setAlop2(FieldUnitUtil.removeUnit(bo.getAlop2()));
        bo.setAlot2(FieldUnitUtil.removeUnit(bo.getAlot2()));
        bo.setAfp2(FieldUnitUtil.removeUnit(bo.getAfp2()));
        bo.setAft2(FieldUnitUtil.removeUnit(bo.getAft2()));
        bo.setAlop3(FieldUnitUtil.removeUnit(bo.getAlop3()));
        bo.setAlot3(FieldUnitUtil.removeUnit(bo.getAlot3()));
        bo.setAfp3(FieldUnitUtil.removeUnit(bo.getAfp3()));
        bo.setAft3(FieldUnitUtil.removeUnit(bo.getAft3()));
        bo.setSternDraught(FieldUnitUtil.removeUnit(bo.getSternDraught()));
        bo.setStemDraft(FieldUnitUtil.removeUnit(bo.getStemDraft()));
        bo.setStarboardDraft(FieldUnitUtil.removeUnit(bo.getStarboardDraft()));
        bo.setPortDraft(FieldUnitUtil.removeUnit(bo.getPortDraft()));
        bo.setTrim(FieldUnitUtil.removeUnit(bo.getTrim()));
        bo.setHeel(FieldUnitUtil.removeUnit(bo.getHeel()));
        bo.setShaftBearingTempFore(FieldUnitUtil.removeUnit(bo.getShaftBearingTempFore()));
        bo.setShaftBearingTempAft(FieldUnitUtil.removeUnit(bo.getShaftBearingTempAft()));
        bo.setShaftBearingTempInter(FieldUnitUtil.removeUnit(bo.getShaftBearingTempInter()));
        bo.setCoolSeaWPress(FieldUnitUtil.removeUnit(bo.getCoolSeaWPress()));
        bo.setThrustBearingTemp(FieldUnitUtil.removeUnit(bo.getThrustBearingTemp()));
        bo.setOilBunkerTempL(FieldUnitUtil.removeUnit(bo.getOilBunkerTempL()));
        bo.setOilBunkerTempR(FieldUnitUtil.removeUnit(bo.getOilBunkerTempR()));
        bo.setOilBunkerLevL(FieldUnitUtil.removeUnit(bo.getOilBunkerLevL()));
        bo.setOilBunkerLevR(FieldUnitUtil.removeUnit(bo.getOilBunkerLevR()));
        bo.setDieselFuelTankLev(FieldUnitUtil.removeUnit(bo.getDieselFuelTankLev()));
        bo.setGasOutletTemp1(FieldUnitUtil.removeUnit(bo.getGasOutletTemp1()));
        bo.setAirReceiverTemp(FieldUnitUtil.removeUnit(bo.getAirReceiverTemp()));
        bo.setAirMainFoldPress(FieldUnitUtil.removeUnit(bo.getAirMainFoldPress()));
        bo.setFstartingAirPressure1(FieldUnitUtil.removeUnit(bo.getFstartingAirPressure1()));
        bo.setFstartingAirPressure2(FieldUnitUtil.removeUnit(bo.getFstartingAirPressure2()));
        bo.setFstartingAirPressure3(FieldUnitUtil.removeUnit(bo.getFstartingAirPressure3()));
        bo.setFcontrolAirPressure1(FieldUnitUtil.removeUnit(bo.getFcontrolAirPressure1()));
        bo.setFcontrolAirPressure2(FieldUnitUtil.removeUnit(bo.getFcontrolAirPressure2()));
        bo.setFcontrolAirPressure3(FieldUnitUtil.removeUnit(bo.getFcontrolAirPressure3()));
        bo.setGasOutletTemp2(FieldUnitUtil.removeUnit(bo.getGasOutletTemp2()));
        bo.setGasOutletTemp3(FieldUnitUtil.removeUnit(bo.getGasOutletTemp3()));
        bo.setOneGwdit(FieldUnitUtil.removeUnit(bo.getOneGwdit()));
        bo.setOneGwdot(FieldUnitUtil.removeUnit(bo.getOneGwdot()));
        bo.setOneDwdit(FieldUnitUtil.removeUnit(bo.getOneDwdit()));
        bo.setOneGwdip(FieldUnitUtil.removeUnit(bo.getOneGwdip()));
        bo.setOneDwdip(FieldUnitUtil.removeUnit(bo.getOneDwdip()));
        bo.setOneoneOpot(FieldUnitUtil.removeUnit(bo.getOneoneOpot()));
        bo.setOnetwoOpot(FieldUnitUtil.removeUnit(bo.getOnetwoOpot()));
        bo.setOnethreeOpot(FieldUnitUtil.removeUnit(bo.getOnethreeOpot()));
        bo.setOnefourOpot(FieldUnitUtil.removeUnit(bo.getOnefourOpot()));
        bo.setTwofourTpot(FieldUnitUtil.removeUnit(bo.getTwofourTpot()));
        bo.setTwothreeTpot(FieldUnitUtil.removeUnit(bo.getTwothreeTpot()));
        bo.setTwotwoTpot(FieldUnitUtil.removeUnit(bo.getTwotwoTpot()));
        bo.setTwooneTpot(FieldUnitUtil.removeUnit(bo.getTwooneTpot()));
        bo.setTwoGwdip(FieldUnitUtil.removeUnit(bo.getTwoGwdip()));
        bo.setTwoDwdip(FieldUnitUtil.removeUnit(bo.getTwoDwdip()));
        bo.setThreefourTpot(FieldUnitUtil.removeUnit(bo.getThreefourTpot()));
        bo.setThreethreeTpot(FieldUnitUtil.removeUnit(bo.getThreethreeTpot()));
        bo.setThreetwoTpot(FieldUnitUtil.removeUnit(bo.getThreetwoTpot()));
        bo.setThreeoneTpot(FieldUnitUtil.removeUnit(bo.getThreeoneTpot()));
        bo.setThreeGwdip(FieldUnitUtil.removeUnit(bo.getThreeGwdip()));
        bo.setThreeDwdip(FieldUnitUtil.removeUnit(bo.getThreeDwdip()));
        bo.setOneZpt(FieldUnitUtil.removeUnit(bo.getOneZpt()));
        bo.setTwoZpt(FieldUnitUtil.removeUnit(bo.getTwoZpt()));
        bo.setThreeZpt(FieldUnitUtil.removeUnit(bo.getThreeZpt()));
        bo.setFourZpt(FieldUnitUtil.removeUnit(bo.getFourZpt()));
        bo.setFiveZpt(FieldUnitUtil.removeUnit(bo.getFiveZpt()));
        bo.setSixZpt(FieldUnitUtil.removeUnit(bo.getSixZpt()));
        bo.setOneQot(FieldUnitUtil.removeUnit(bo.getOneQot()));
        bo.setTwoQot(FieldUnitUtil.removeUnit(bo.getTwoQot()));
        bo.setThreeQot(FieldUnitUtil.removeUnit(bo.getThreeQot()));
        bo.setFourQot(FieldUnitUtil.removeUnit(bo.getFourQot()));
        bo.setFiveQot(FieldUnitUtil.removeUnit(bo.getFiveQot()));
        bo.setSixQot(FieldUnitUtil.removeUnit(bo.getSixQot()));
        bo.setZjlqSt(FieldUnitUtil.removeUnit(bo.getZjlqSt()));
        bo.setZjlhSt(FieldUnitUtil.removeUnit(bo.getZjlhSt()));
        bo.setZjlqsJit(FieldUnitUtil.removeUnit(bo.getZjlqsJit()));
        bo.setZjlqsJot(FieldUnitUtil.removeUnit(bo.getZjlqsJot()));
        bo.setZjpAip(FieldUnitUtil.removeUnit(bo.getZjpAip()));
        bo.setZjlqJip(FieldUnitUtil.removeUnit(bo.getZjlqJip()));
        bo.setOneQhlot(FieldUnitUtil.removeUnit(bo.getOneQhlot()));
        bo.setTwoQhlot(FieldUnitUtil.removeUnit(bo.getTwoQhlot()));
        bo.setThreeQhlot(FieldUnitUtil.removeUnit(bo.getThreeQhlot()));
        bo.setFourQhlot(FieldUnitUtil.removeUnit(bo.getFourQhlot()));
        bo.setFiveQhlot(FieldUnitUtil.removeUnit(bo.getFiveQhlot()));
        bo.setSixQhlot(FieldUnitUtil.removeUnit(bo.getSixQhlot()));
        bo.setOneRct(FieldUnitUtil.removeUnit(bo.getOneRct()));
        bo.setOneRrt(FieldUnitUtil.removeUnit(bo.getOneRrt()));
        bo.setTwoRct(FieldUnitUtil.removeUnit(bo.getTwoRct()));
        bo.setTwoRrt(FieldUnitUtil.removeUnit(bo.getTwoRrt()));
        bo.setOneRcl(FieldUnitUtil.removeUnit(bo.getOneRcl()));
        bo.setOneRrl(FieldUnitUtil.removeUnit(bo.getOneRrl()));
        bo.setTwoRcl(FieldUnitUtil.removeUnit(bo.getTwoRcl()));
        bo.setTwoRrl(FieldUnitUtil.removeUnit(bo.getTwoRrl()));
    }

}

package com.zkthinke.modules.apsat.ship.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSONObject;
import com.zkthinke.domain.StorageContent;
import com.zkthinke.modules.apsat.ship.domain.*;
import com.zkthinke.modules.apsat.ship.mapper.ShipRoutePlanDao;
import com.zkthinke.modules.apsat.ship.service.ShipAlarmThresholdService;
import com.zkthinke.modules.apsat.ship.service.ShipRoutePlanService;
import com.zkthinke.modules.apsat.ship.service.dto.ShipRoutePlanQueryCriteria;
import com.zkthinke.modules.common.utils.LatitudeLongitideUtils;
import com.zkthinke.service.impl.StorageService;
import com.zkthinke.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author weicb
 * @date 2020-10-15
 */
@Service
@Slf4j
public class ShipRoutePlanServiceImpl implements ShipRoutePlanService {

    @Autowired
    private ShipAlarmThresholdService shipAlarmThresholdService;

    @Autowired
    private ShipRoutePlanDao shipRoutePlanDao;

    @Autowired
    private StorageService storageService;

    @Autowired
    private ShipRoutePlanService shipRoutePlanService;

    @Value("${tempPath}")
    private String tempPath;

    @Override
    public List<ShipRoutePlan> findAll(ShipRoutePlanQueryCriteria criteria) {
        // ??????????????????????????????
        String createUser = SecurityUtils.getUsername();
        criteria.setCreateUser(createUser);
        List<ShipRoutePlan> shipRoutePlanList = shipRoutePlanDao.findAll(criteria);
        return shipRoutePlanList;
    }

    @Override
    @Transactional
    public void updateStateById(ShipRoutePlanQueryCriteria criteria) {
        if ("1".equals(criteria.getState())) {
            //?????????????????????,???????????????????????????????????????,???????????????????????????
            shipRoutePlanDao.updateStateToClose(criteria);
        }
        shipRoutePlanDao.updateStateById(criteria);
    }

    @Override
    @Transactional
    public String parseFileById(String id, String dataId,String planId,String createUser) throws Exception {

        //??????id???qiniu_content????????????url
        String url = shipRoutePlanDao.queryUrlById(dataId);
        log.info("??????????????????????????????url==[{}]", url);

        //?????????????????????
        //url = "E:/???????????????/??????????????????????????????/??????????????????.xls";
        //??????url????????????
        ShipRoutePlan shipRoutePlan = readFile(url);
        String radius = shipAlarmThresholdService.getThresholdLimit(id,"??????????????????????????????");
        //1?????? = 1852???
        double radiusD = Double.parseDouble(radius) * 1852;
        shipRoutePlan.setShipId(id);
        shipRoutePlan.setCreateTime(new Date());
        shipRoutePlan.setUpdateTime(new Date());
        shipRoutePlan.setRadius(String.valueOf(radiusD));
        shipRoutePlan.setCreateUser(createUser);
        //????????????????????????t_ship_route_plan
        if (StringUtils.isEmpty(planId)){
            int num = shipRoutePlanDao.addPlan(shipRoutePlan);
            if (num == 0) {
                throw new RuntimeException("??????Excel??????");
            }
            planId = shipRoutePlan.getId().toString();
        }else {
            //??????????????????
            shipRoutePlan.setId(Long.parseLong(planId));
            int num = shipRoutePlanDao.updatePlan(shipRoutePlan);
            if (num == 0) {
                throw new RuntimeException("????????????????????????");
            }
            //????????????????????????????????????
            shipRoutePlanDao.deletePlanDetail(shipRoutePlan.getId());
            shipRoutePlanDao.deletePlanEnclosure(shipRoutePlan.getId());
        }

        List<ShipRoutePlanDetail> list = shipRoutePlan.getList();
        //????????????????????????
        List<ShipRoutePlanEnclosure> shipRoutePlanEnclosureList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            ShipRoutePlanEnclosure shipRoutePlanEnclosure = new ShipRoutePlanEnclosure();
            shipRoutePlanEnclosure.setId(shipRoutePlan.getId());

            list.get(i).setId(shipRoutePlan.getId());
            ShipRoutePlanDetail shipRoutePlanDetail = list.get(i);

            String longitude = shipRoutePlanDetail.getLongitude();
            String latitude = shipRoutePlanDetail.getLatitude();
            String true_course = shipRoutePlanDetail.getTrueCourse();
            String orderNum = shipRoutePlanDetail.getOrderNum();
            shipRoutePlanEnclosure.setOrderNum(orderNum);
            shipRoutePlanEnclosure.setTrueCourse(true_course);

            if (i > 0) {
                //??????????????????
                ShipRoutePlanDetail shipRoutePlanDetailBefore = list.get(i - 1);
                String true_courseBefore = shipRoutePlanDetailBefore.getTrueCourse();

                Map<String, String> mapBegin = LatitudeLongitideUtils.mathLeftRight(Double.valueOf(longitude), Double.valueOf(latitude), Double.valueOf(true_courseBefore), radiusD);
                shipRoutePlanEnclosure.setBeginLeftLongitude(mapBegin.get("leftLongitude"));
                shipRoutePlanEnclosure.setBeginLeftLatitude(mapBegin.get("leftLatitude"));
                shipRoutePlanEnclosure.setBeginRightLongitude(mapBegin.get("rightLongitude"));
                shipRoutePlanEnclosure.setBeginRightLatitude(mapBegin.get("rightLatitude"));
            }
            if (i < list.size() - 1) {
                Map<String, String> mapEnd = LatitudeLongitideUtils.mathLeftRight(Double.valueOf(longitude), Double.valueOf(latitude), Double.valueOf(true_course), radiusD);
                //?????????????????????
                shipRoutePlanEnclosure.setEndLeftLongitude(mapEnd.get("leftLongitude"));
                shipRoutePlanEnclosure.setEndLeftLatitude(mapEnd.get("leftLatitude"));
                shipRoutePlanEnclosure.setEndRightLongitude(mapEnd.get("rightLongitude"));
                shipRoutePlanEnclosure.setEndRightLatitude(mapEnd.get("rightLatitude"));
            }
            shipRoutePlanEnclosureList.add(shipRoutePlanEnclosure);
        }
        //????????????????????????t_ship_route_plan_detail
        shipRoutePlanDao.addPlanDetail(list);

        shipRoutePlanDao.addPlanEnclosure(shipRoutePlanEnclosureList);
        return planId;
    }

    @Override
    public String drawShipRoutePlanState(DrawShipRoutePlanBO reqBo) throws Exception {
        log.info("drawShipRoutePlanState ?????????{}", JSONObject.toJSONString(reqBo));
        // ??????????????????????????????
        String createUser = SecurityUtils.getUsername();

        String planId;
        InputStream stream = null;
        ExcelWriter excelWriter = null;
        InputStream is = null;
        File file = null;
        try {
            String shipId = reqBo.getShipId();
            if (StringUtils.isEmpty(shipId)) {
                throw new RuntimeException("???shipId???????????????");
            }
            String voyageNumber = reqBo.getVoyageNumber();
            if (StringUtils.isEmpty(voyageNumber)) {
                throw new RuntimeException("????????????????????????");
            }
            String planName = reqBo.getPlanName().replaceAll(" ","");
            if (StringUtils.isEmpty(planName)) {
                throw new RuntimeException("??????????????????????????????");
            }
            String legBegin = reqBo.getLegBegin();
            if (StringUtils.isEmpty(legBegin)) {
                throw new RuntimeException("???????????????????????????");
            }
            String legEnd = reqBo.getLegEnd();
            if (StringUtils.isEmpty(legEnd)) {
                throw new RuntimeException("???????????????????????????");
            }
            String etd = reqBo.getEtd();
            if (StringUtils.isEmpty(etd)) {
                throw new RuntimeException("?????????????????????????????????");
            }
            String eta = reqBo.getEta();
            if (StringUtils.isEmpty(eta)) {
                throw new RuntimeException("?????????????????????????????????");
            }
            List<PointBO> pointList = reqBo.getPointList();
            if (CollectionUtils.isEmpty(pointList)) {
                throw new RuntimeException("???pointList???????????????");
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            //??????????????????
            String shipName = shipRoutePlanDao.queryShipNameById(shipId);

            // 1 ?????????????????????????????????????????????
            stream = new ClassPathResource("template/excel/??????????????????.xls").getStream();
            String fileName = planName + "-????????????.xls";
            String absFileName = tempPath + fileName;
            FileUtil.writeFromStream(stream, absFileName);

            // 2 ????????????????????????
            excelWriter = new ExcelWriter(absFileName, "Data");
            List<Double> rangeList = new ArrayList<>();
            for (int i = 0; i < pointList.size(); i++) {
                //??????
                String latitude = pointList.get(i).getLatitude();
                //??????
                String longitude = pointList.get(i).getLongitude();

                if (i == pointList.size() - 1) {
                    //???????????????
                    Cell cell2 = excelWriter.getCell(2, pointList.size() - 1 + 3);
                    cell2.setCellValue(Double.parseDouble(LatitudeLongitideUtils.angleConvertReverse(latitude)));
                    Cell cell3 = excelWriter.getCell(3, pointList.size() - 1 + 3);
                    cell3.setCellValue(Double.parseDouble(LatitudeLongitideUtils.angleConvertReverse(longitude)));
                    break;
                }

                String nextLatitude = pointList.get(i + 1).getLatitude();
                String nextLongitude = pointList.get(i + 1).getLongitude();

                Double mp = LatitudeLongitideUtils.calculateMP(Double.parseDouble(latitude));
                Double nextMp = LatitudeLongitideUtils.calculateMP(Double.parseDouble(nextLatitude));
                Double dmp = nextMp - mp;
                Double dLat = Double.parseDouble(nextLatitude) - Double.parseDouble(latitude);
                Double dLong = LatitudeLongitideUtils.calculateDLong(Double.parseDouble(nextLongitude), Double.parseDouble(longitude));
                //?????????
                Double trueCourse = LatitudeLongitideUtils.calculateTrueCourse(dLong, dmp);
                //??????
                Double range = LatitudeLongitideUtils.calculateRange(trueCourse, dLong, Double.parseDouble(latitude), dLat);
                rangeList.add(range);

                //????????????3??????
                Cell cell2 = excelWriter.getCell(2, i + 3);
                cell2.setCellValue(Double.parseDouble(LatitudeLongitideUtils.angleConvertReverse(latitude)));
                Cell cell3 = excelWriter.getCell(3, i + 3);
                cell3.setCellValue(Double.parseDouble(LatitudeLongitideUtils.angleConvertReverse(longitude)));

                Cell cell4 = excelWriter.getCell(4, i + 3);
                cell4.setCellFormula(null);
                cell4.setCellValue(trueCourse);
                Cell cell5 = excelWriter.getCell(5, i + 3);
                cell5.setCellFormula(null);
                cell5.setCellValue(range);
            }

            //??????????????????
            for (int i = 0; i < pointList.size() - 1; i++) {
                double sum = rangeList.stream().skip(i).mapToDouble(Double::doubleValue).sum();
                Cell cell6 = excelWriter.getCell(6, i + 3);
                cell6.setCellFormula(null);
                cell6.setCellValue(sum);
            }
            //??????
            Cell cell_24_3 = excelWriter.getCell(24, 3);
            cell_24_3.setCellValue(shipName);
            //??????
            Cell cell_24_4 = excelWriter.getCell(24, 4);
            cell_24_4.setCellValue(voyageNumber);
            //????????????
            Cell cell_27_5 = excelWriter.getCell(27, 5);
            cell_27_5.setCellValue(planName);
            //?????????
            Cell cell_24_6 = excelWriter.getCell(24, 6);
            cell_24_6.setCellValue(legBegin);
            //?????????
            Cell cell_24_8 = excelWriter.getCell(24, 8);
            cell_24_8.setCellValue(legEnd);
            //???????????????
            Cell cell_24_9 = excelWriter.getCell(24, 10);
            cell_24_9.setCellValue(HSSFDateUtil.getExcelDate(sdf.parse(etd)));
            //???????????????
            Cell cell_24_10 = excelWriter.getCell(24, 12);
            cell_24_10.setCellValue(HSSFDateUtil.getExcelDate(sdf.parse(eta)));
            excelWriter.flush();

            // 3 ??????????????????
            file = new File(absFileName);
            is = new FileInputStream(file);

            StorageContent store = storageService.store(is, file.length(), "application/vnd.ms-excel", fileName);

            // 4 ?????? ????????????bean??????????????????
            planId = shipRoutePlanService.parseFileById(shipId,store.getId(),reqBo.getPlanId(),createUser);
        } finally {
            if (is !=null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (excelWriter != null) {
                excelWriter.close();
            }
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (file != null){
                // ??????????????????
                file.delete();
            }
        }
        return planId;
    }

    @Override
    @Transactional
    public void deleteShipRoutePlan(DrawShipRoutePlanBO reqBo) {
        log.info("deleteShipRoutePlan ?????????{}",reqBo);
        shipRoutePlanDao.deletePlan(Long.parseLong(reqBo.getPlanId()));
        shipRoutePlanDao.deletePlanDetail(Long.parseLong(reqBo.getPlanId()));
        shipRoutePlanDao.deletePlanEnclosure(Long.parseLong(reqBo.getPlanId()));
    }

    private ShipRoutePlan readFile(String url) throws Exception {
        ShipRoutePlan shipRoutePlan = new ShipRoutePlan();
        List<ShipRoutePlanDetail> list = new ArrayList<>();
        String voyageLeg = "";//??????
        //???????????????????????????
        InputStream input = new URL(url).openStream();
        ExcelReader excelReader = new ExcelReader(input, "Data", true);
        List<List<Object>> read = excelReader.read();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //????????????
        for (int i = 0; i < read.size(); i++) {
            //log.info("i==" + i);
            if (i > 1) {
                ShipRoutePlanDetail detail = new ShipRoutePlanDetail();
                List<Object> raw = read.get(i);
                if (i == 2) {//??????
                    try {
                        String voyageName = String.valueOf(raw.get(24));
                        if (StringUtils.isEmpty(voyageName)) {
                            throw new RuntimeException("????????????????????????");
                        }
                        shipRoutePlan.setVoyageName(voyageName);
                    } catch (RuntimeException e) {
                        throw e;
                    } catch (Exception e) {
                        throw new RuntimeException("????????????????????????");
                    }
                }
                if (i == 3) {//??????
                    try {
                        String voyageNumber = String.valueOf(raw.get(24));
                        if (StringUtils.isEmpty(voyageNumber)) {
                            throw new RuntimeException("????????????????????????");
                        }
                        shipRoutePlan.setVoyageNumber(voyageNumber);
                    } catch (RuntimeException e) {
                        throw e;
                    } catch (Exception e) {
                        throw new RuntimeException("????????????????????????");
                    }
                }
                if (i == 4) {//????????????
                    try {
                        String planName = String.valueOf(raw.get(27));
                        if (StringUtils.isEmpty(planName)) {
                            throw new RuntimeException("??????????????????????????????");
                        }
                        shipRoutePlan.setPlanName(planName);
                    } catch (RuntimeException e) {
                        throw e;
                    } catch (Exception e) {
                        throw new RuntimeException("??????????????????????????????");
                    }
                }
                if (i == 5) {//???????????????
                    try {
                        String begin = String.valueOf(raw.get(24));
                        if (StringUtils.isEmpty(begin)) {
                            throw new RuntimeException("?????????????????????????????????");
                        }
                        shipRoutePlan.setLegBegin(begin);
                    } catch (RuntimeException e) {
                        throw e;
                    } catch (Exception e) {
                        throw new RuntimeException("?????????????????????????????????");
                    }
                }
                if (i == 7) {//???????????????
                    try {
                        String end = String.valueOf(raw.get(24));
                        if (StringUtils.isEmpty(end)) {
                            throw new RuntimeException("?????????????????????????????????");
                        }
                        shipRoutePlan.setLegEnd(end);
                    } catch (RuntimeException e) {
                        throw e;
                    } catch (Exception e) {
                        throw new RuntimeException("?????????????????????????????????");
                    }
                }
                if (i == 9) {//???????????????(ETD)
                    try {
                        String etd = String.valueOf(raw.get(24));
                        if (StringUtils.isEmpty(etd)) {
                            throw new RuntimeException("?????????????????????????????????");
                        }
                        Date javaDate = HSSFDateUtil.getJavaDate(Double.parseDouble(etd));
                        shipRoutePlan.setEtd(sdf.format(javaDate));
                    } catch (RuntimeException e) {
                        throw e;
                    } catch (Exception e) {
                        throw new RuntimeException("?????????????????????????????????");
                    }
                }
                if (i == 11) {//???????????????(ETA)
                    try {
                        String eta = String.valueOf(raw.get(24));
                        if (StringUtils.isEmpty(eta)) {
                            throw new RuntimeException("?????????????????????????????????");
                        }
                        Date javaDate = HSSFDateUtil.getJavaDate(Double.parseDouble(eta));
                        shipRoutePlan.setEta(sdf.format(javaDate));
                    } catch (RuntimeException e) {
                        throw e;
                    } catch (Exception e) {
                        throw new RuntimeException("?????????????????????????????????");
                    }
                }
                shipRoutePlan.setState("0");//??????????????????
                shipRoutePlan.setUrl(url);
                //???????????????
                Object s = raw.get(2);
                if (raw.get(2) != "" && raw.get(2) != null) {
                    try {
                        String orderNum = String.valueOf(raw.get(1));
                        String latitude = String.valueOf(raw.get(2));
                        String longitide = String.valueOf(raw.get(3));
                        String true_course = String.valueOf(raw.get(4));
                        String range = String.valueOf(raw.get(5));
                        String distance_todo = String.valueOf(raw.get(6));
                        String wp_remark = String.valueOf(raw.get(7));

                        detail.setOrderNum(orderNum);
                        detail.setLatitude(LatitudeLongitideUtils.parse(latitude));
                        detail.setLongitude(LatitudeLongitideUtils.parse(longitide));
                        detail.setTrueCourse(true_course);
                        detail.setRange(range);
                        detail.setDistanceTodo(distance_todo);
                        detail.setWpRemark(wp_remark);
                        list.add(detail);
                    } catch (Exception e) {
                        throw new RuntimeException("???????????????????????????");
                    }
                }
            }
        }
        shipRoutePlan.setList(list);
        return shipRoutePlan;
    }
}
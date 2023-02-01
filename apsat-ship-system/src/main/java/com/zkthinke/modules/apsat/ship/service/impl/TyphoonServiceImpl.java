package com.zkthinke.modules.apsat.ship.service.impl;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.zkthinke.modules.apsat.ship.domain.TyphoonBO;
import com.zkthinke.modules.apsat.ship.domain.TyphoonTrackBO;
import com.zkthinke.modules.apsat.ship.service.TyphoonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.ArrayList;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Slf4j
@Service
public class TyphoonServiceImpl implements TyphoonService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${typhoon.token}")
    private String token;

    private final String typhoonListUrl = "https://api.foreocean.com/typhoon/list";
    private final String trackListUrl = "https://api.foreocean.com/typhoon/track";
    private final String forecastListUrl = "https://api.foreocean.com/typhoon/forecast";

    @Override
    public List<TyphoonBO> getTyphoonList(String year) {
        log.info("getTyphoonList 入参：{}", year);
        String redisKey = "TyphoonList:" + year;
        try {
            //从Redis取缓存
            Object redisValue = redisTemplate.opsForValue().get(redisKey);
            if (redisValue != null) {
                log.info("Redis 获取台风列表" + year);
                List<TyphoonBO> list = JSONObject.parseArray(redisValue.toString(), TyphoonBO.class);
                return list;
            }
        } catch (Exception e) {
            log.error("Redis 获取台风列表异常：", e);
        }

        List<TyphoonBO> resultList = new ArrayList<>();
        try {
            String typhoonListStr = HttpRequest.get(typhoonListUrl + "?year=" + year)
                    .header("Token", token)
                    .execute()
                    .body();
            JSONObject typhoonListJson = JSONObject.parseObject(typhoonListStr);
            String resultCode = typhoonListJson.get("code").toString();
            String resultMsg = typhoonListJson.get("msg").toString();
            if (!"1000".equals(resultCode)) {
                //响应码错误
                throw new RuntimeException("getTyphoonList 响应码错误，code=" + resultCode + "，msg=" + resultMsg);
            }
            JSONArray typhoonListData = typhoonListJson.getJSONArray("data");
            if (typhoonListData == null) {
                //没有台风列表数据
                return resultList;
            }
            for (int i = 0; i < typhoonListData.size(); i++) {
                JSONObject typhoonJson = typhoonListData.getJSONObject(i);
                String status = typhoonJson.get("status").toString();
                String code = typhoonJson.get("code").toString();
                String name = typhoonJson.get("name").toString();
                String updateTime = typhoonJson.get("updatetime").toString();
                String enName = typhoonJson.get("enname").toString();

                TyphoonBO typhoonBO = new TyphoonBO();
                typhoonBO.setCode(code);
                typhoonBO.setName(name);
                typhoonBO.setUpdateTime(updateTime);
                typhoonBO.setEnName(enName);
                typhoonBO.setStatus(status);
                resultList.add(typhoonBO);
            }

            //缓存到Redis里 1小时过期
            redisTemplate.opsForValue().set(redisKey, JSONObject.toJSONString(resultList), 1, TimeUnit.HOURS);
        } catch (RuntimeException e) {
            log.error("getTyphoonList 异常：", e);
        }

        return resultList;
    }

    @Override
    public List<TyphoonTrackBO> getTyphoonTrackList(String code) {
        log.info("getTyphoonTrackList 入参：{}", code);
        String redisKey = "TyphoonTrackList:" + code;
        try {
            //从Redis取缓存
            Object redisValue = redisTemplate.opsForValue().get(redisKey);
            if (redisValue != null) {
                log.info("Redis 获取台风轨迹列表" + code);
                List<TyphoonTrackBO> list = JSONObject.parseObject(redisValue.toString(), List.class);
                return list;
            }
        } catch (Exception e) {
            log.error("Redis 获取台风轨迹列表：", e);
        }

        List<TyphoonTrackBO> trackList = new ArrayList<>();

        try {
            String trackListStr = HttpRequest.get(forecastListUrl + "?code=" + code)
                    .header("Token", token)
                    .execute()
                    .body();
            JSONObject trackListJson = JSONObject.parseObject(trackListStr);
            String resultCode = trackListJson.get("code").toString();
            String resultMsg = trackListJson.get("msg").toString();
            if (!"1000".equals(resultCode)) {
                //响应码错误
                throw new RuntimeException("getTyphoonTrackList 响应码错误，code=" + resultCode + "，msg=" + resultMsg);
            }
            JSONArray trackListData = trackListJson.getJSONArray("data");
            if (trackListData == null) {
                //没有台风轨迹数据
                return trackList;
            }
            for (int i = 0; i < trackListData.size(); i++) {
                JSONObject trackJson = trackListData.getJSONObject(i);
                String bizDate = trackJson.getOrDefault("bizDate", "").toString();
                String lng = trackJson.getOrDefault("lng", "").toString();
                String lat = trackJson.getOrDefault("lat", "").toString();
                String centerSpeed = trackJson.getOrDefault("centerSpeed", "").toString();
                String centerPressure = trackJson.getOrDefault("centerPressure", "").toString();
                String moveSpeed = trackJson.getOrDefault("moveSpeed", "").toString();
                String moveDirection = trackJson.getOrDefault("moveDirection", "").toString().trim();
                String r7Se = trackJson.getOrDefault("r7Se", "").toString();
                String r7Sw = trackJson.getOrDefault("r7Sw", "").toString();
                String r7Ne = trackJson.getOrDefault("r7Ne", "").toString();
                String r7Nw = trackJson.getOrDefault("r7Nw", "").toString();

                TyphoonTrackBO trackBo = new TyphoonTrackBO();
                List<TyphoonTrackBO> forecastList = new ArrayList<>();
                trackBo.setForecastList(forecastList);
                trackBo.setBizDate(bizDate);
                trackBo.setLng(lng);
                trackBo.setLat(lat);
                trackBo.setCenterSpeed(centerSpeed);
                trackBo.setCenterPressure(centerPressure);
                trackBo.setMoveSpeed(moveSpeed);
                trackBo.setMoveDirection(moveDirection);
                trackBo.setR7Se(r7Se);
                trackBo.setR7Sw(r7Sw);
                trackBo.setR7Ne(r7Ne);
                trackBo.setR7Nw(r7Nw);
                trackList.add(trackBo);

                if (i == trackListData.size() -1){
                    //获取当前台风的状态
                    List<TyphoonBO> typhoonList = getTyphoonList(code.substring(0, 4));
                    List<TyphoonBO> matchOne = typhoonList.stream().filter(e -> e.getCode().equals(code) && e.getStatus().equals("active") ).collect(Collectors.toList());
                    if (CollectionUtils.isEmpty(matchOne)){
                        //此台风不是激活的
                        continue;
                    }
                    //台风的最后一个点，带有预测轨迹
                    JSONArray forecastListJson = trackJson.getJSONArray("forecastList");
                    if (forecastListJson != null && forecastListJson.size() > 0) {
                        for (int j = 0; j < forecastListJson.size(); j++) {
                            JSONObject forecast = forecastListJson.getJSONObject(j);
                            String forecastOrg = forecast.getOrDefault("forecastOrg","").toString();
                            if (!"中国".equals(forecastOrg)){
                                continue;
                            }
                            String bizDateForecast = forecast.getOrDefault("bizDate", "").toString();
                            String lngForecast = forecast.getOrDefault("lng", "").toString();
                            String latForecast = forecast.getOrDefault("lat", "").toString();
                            String centerSpeedForecast = forecast.getOrDefault("centerSpeed", "").toString();
                            String centerPressureForecast = forecast.getOrDefault("centerPressure", "").toString();
                            String moveSpeedForecast = forecast.getOrDefault("moveSpeed", "").toString();
                            String moveDirectionForecast = forecast.getOrDefault("moveDirection", "").toString().trim();

                            TyphoonTrackBO forecastBo = new TyphoonTrackBO();
                            forecastBo.setBizDate(bizDateForecast);
                            forecastBo.setLng(lngForecast);
                            forecastBo.setLat(latForecast);
                            forecastBo.setCenterSpeed(centerSpeedForecast);
                            forecastBo.setCenterPressure(centerPressureForecast);
                            forecastBo.setMoveSpeed(moveSpeedForecast);
                            forecastBo.setMoveDirection(moveDirectionForecast);
                            forecastList.add(forecastBo);
                        }
                        forecastList.sort(Comparator.comparing(TyphoonTrackBO::getBizDate));
                    }
                }
            }

            //缓存到Redis里 1小时过期
            redisTemplate.opsForValue().set(redisKey, JSONObject.toJSONString(trackList), 1,TimeUnit.HOURS);
        } catch (RuntimeException e) {
            log.error("getTyphoonTrackList 异常：", e);
        }
        return trackList;
    }

    @Override
    public List<TyphoonBO> getActivatedTyphoonDetailList() {
        log.info("getActivatedTyphoonDetailList 接口");
        String redisKey = "ActivatedTyphoonDetailList";
        try {
            //从Redis取缓存
            Object redisValue = redisTemplate.opsForValue().get(redisKey);
            if (redisValue != null) {
                log.info("Redis里获取台风信息列表");
                List<TyphoonBO> list = JSONObject.parseArray(redisValue.toString(), TyphoonBO.class);
                return list;
            }
        } catch (Exception e) {
            log.error("Redis里获取台风信息列表异常：", e);
        }

        List<TyphoonBO> typhoonList = new ArrayList<>();
        try {
            int year = LocalDate.now().getYear();

            for (int x = 0; x < 2; x++) {
                //近两年的，防止跨年时查不到去年的
                String typhoonListStr = HttpRequest.get(typhoonListUrl + "?year=" + (year - x))
                        .header("Token", token)
                        .execute()
                        .body();
                JSONObject typhoonListJson = JSONObject.parseObject(typhoonListStr);
                String resultCode = typhoonListJson.get("code").toString();
                String resultMsg = typhoonListJson.get("msg").toString();
                if (!"1000".equals(resultCode)) {
                    //响应码错误
                    throw new RuntimeException("台风列表响应码错误，code=" + resultCode + "，msg=" + resultMsg);
                }
                JSONArray typhoonListData = typhoonListJson.getJSONArray("data");
                if (typhoonListData == null) {
                    //没有台风列表数据
                    return typhoonList;
                }
                for (int i = 0; i < typhoonListData.size(); i++) {
                    JSONObject typhoonJson = typhoonListData.getJSONObject(i);
                    String status = typhoonJson.get("status").toString();
                    if (!"active".equals(status)) {
                        //非活动状态
                        continue;
                    }
                    String code = typhoonJson.get("code").toString();
                    String name = typhoonJson.get("name").toString();
                    String updateTime = typhoonJson.get("updatetime").toString();
                    String enName = typhoonJson.get("enname").toString();

                    TyphoonBO typhoonBO = new TyphoonBO();
                    List<TyphoonTrackBO> typhoonTrackList = new ArrayList<>();
                    typhoonBO.setCode(code);
                    typhoonBO.setName(name);
                    typhoonBO.setUpdateTime(updateTime);
                    typhoonBO.setEnName(enName);
                    typhoonBO.setStatus(status);
                    typhoonBO.setTrackList(typhoonTrackList);
                    typhoonList.add(typhoonBO);

                    String trackListStr = HttpRequest.get(trackListUrl + "?code=" + code)
                            .header("Token", token)
                            .execute()
                            .body();

                    JSONObject trackListJson = JSONObject.parseObject(trackListStr);
                    String resultCode2 = typhoonListJson.get("code").toString();
                    String resultMsg2 = typhoonListJson.get("msg").toString();
                    if (!"1000".equals(resultCode2)) {
                        //响应码错误
                        throw new RuntimeException("台风实况路径响应码错误，code=" + resultCode2 + "，msg=" + resultMsg2);
                    }
                    JSONArray trackListData = trackListJson.getJSONArray("data");
                    if (trackListData == null) {
                        //没有台风实况路径数据
                        continue;
                    }

                    for (int j = 0; j < trackListData.size(); j++) {
                        JSONObject trackJson = trackListData.getJSONObject(j);
                        String bizDate = trackJson.getOrDefault("bizDate", "").toString();
                        String lng = trackJson.getOrDefault("lng", "").toString();
                        String lat = trackJson.getOrDefault("lat", "").toString();
                        String centerSpeed = trackJson.getOrDefault("centerSpeed", "").toString();
                        String centerPressure = trackJson.getOrDefault("centerPressure", "").toString();
                        String moveSpeed = trackJson.getOrDefault("moveSpeed", "").toString();
                        String moveDirection = trackJson.getOrDefault("moveDirection", "").toString().trim();

                        TyphoonTrackBO typhoonTrackBO = new TyphoonTrackBO();
                        typhoonTrackBO.setBizDate(bizDate);
                        typhoonTrackBO.setLng(lng);
                        typhoonTrackBO.setLat(lat);
                        typhoonTrackBO.setCenterSpeed(centerSpeed);
                        typhoonTrackBO.setCenterPressure(centerPressure);
                        typhoonTrackBO.setMoveSpeed(moveSpeed);
                        typhoonTrackBO.setMoveDirection(moveDirection);
                        typhoonTrackList.add(typhoonTrackBO);
                    }
                }
            }
            //缓存到Redis里 1小时过期
            redisTemplate.opsForValue().set(redisKey, JSONObject.toJSONString(typhoonList), 1, TimeUnit.HOURS);
        } catch (Exception e) {
            log.error("getActivatedTyphoonDetailList 异常：", e);
        }
        return typhoonList;
    }
}

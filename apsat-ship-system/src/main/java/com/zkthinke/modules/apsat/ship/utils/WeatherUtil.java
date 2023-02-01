package com.zkthinke.modules.apsat.ship.utils;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSONObject;
import com.zkthinke.modules.apsat.ship.domain.ShipWeatherDTO;
import com.zkthinke.response.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 获取气象信息工具类
 *
 * @author dww
 * @since 1.0
 */
@Component
@Slf4j
public class WeatherUtil {

    //获取气象信息接口 url
    @Value("${weather.setting.weatherValueUrl}")
    private String weatherValueUrl;


    /**
     * 获取气象信息接口
     *
     * @param shipWeatherDto 获取气象信息数据传输对象
     * @return 统一视图对象
     */
    public ResponseResult getWeatherValue(ShipWeatherDTO shipWeatherDto) {
        String time = shipWeatherDto.getTime();
        String latitude = shipWeatherDto.getLatitude();
        String longitude = shipWeatherDto.getLongitude();

        String requestWeatherValueUrl = String.format(weatherValueUrl, time, latitude, longitude);
        log.info("获取气象信息接口,请求参数:[{}].", requestWeatherValueUrl);
        JSONObject jsonObject;
        try {
            String result = HttpRequest.get(requestWeatherValueUrl)
                    .execute()
                    .body();
            log.info("获取气象信息接口,响应报文:[{}]", result);
            jsonObject = JSONObject.parseObject(result);

        } catch (Exception e) {
            log.error("获取气象信息接口异常:[{}].", e.getMessage(), e);
            return ResponseResult.fail(e.getMessage());
        }
        return ResponseResult.ok(jsonObject);
    }
}

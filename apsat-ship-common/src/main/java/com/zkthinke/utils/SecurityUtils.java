package com.zkthinke.utils;

import cn.hutool.json.JSONObject;
import com.alibaba.fastjson.JSON;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.zkthinke.exception.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取当前登录的用户
 * Created by kellen on 2019/8/24.
 */
public class SecurityUtils {

    public static UserDetails getUserDetails() {
        UserDetails userDetails = null;
        try {
            userDetails = (UserDetails) org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Exception e) {
            throw new BadRequestException(HttpStatus.UNAUTHORIZED, "登录状态过期");
        }
        return userDetails;
    }

    /**
     * 获取系统用户名称
     * @return 系统用户名称
     */
    public static String getUsername(){
        Object obj = getUserDetails();
        JSONObject json = new JSONObject(obj);
        return json.get("username", String.class);
    }

    /**
     * 获取系统用户id
     * @return 系统用户id
     */
    public static Long getUserId(){
        Object obj = getUserDetails();
        JSONObject json = new JSONObject(obj);
        return json.get("id", Long.class);
    }

    /**
     * 获取用户角色
     * @return
     */
    public static List<Object> getRole(){
        Object obj = getUserDetails();
        JSONObject json = new JSONObject(obj);
        return json.get("userRoles", List.class);
    }

    /**
     * 判断用户的角色是否是超级管理员
     * @return
     */
    public static boolean getAdminRole(){
        Object obj = getUserDetails();
        JSONObject json = new JSONObject(obj);
        List<JSONObject> userRoles = json.get("userRoles", List.class);

        if(userRoles != null && userRoles.size()>0 ) {
            for (JSONObject o : userRoles ) {
                JsonParser jp = new JsonParser();
                JsonObject asJsonObject = jp.parse(o.toString()).getAsJsonObject();
                JsonElement name = asJsonObject.get("name");
                if(name.getAsString().equals("超级管理员")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取当前用户的船舶信息
     * @return
     */
    public static List<Object> getShips() {
        Object obj = getUserDetails();
        JSONObject json = new JSONObject(obj);
        return json.get("ships", List.class);
    }

    /**
     * 获取当前用户拥有的船舶id
     * @return
     */
    public static List<Long> getShipIds() {
        Object obj = getUserDetails();
        JSONObject json = new JSONObject(obj);
        return json.get("shipIds", List.class);
    }

}

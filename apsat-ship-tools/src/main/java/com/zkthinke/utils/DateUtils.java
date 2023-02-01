package com.zkthinke.utils;

import org.springframework.util.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author: huqijun
 * @Date: 2020/1/13 23:13
 */
public class DateUtils {

    private static final String YYYY_MM_DD = "yyyy-MM-dd";
    private static final String YYYY_MM_DD_HH_MM_SS  = "yyyy-MM-dd HH:mm:ss";
    private static final String YYYY_MM_DD_HHmmss  = "yyyy-MM-dd HHmmss";
    private static final String DATE_FORMAT_ALARM_LOG  = "yyyy-MM-dd HH:mm:ss";
    private static final String DATE_FORMAT_ALARM_MSG  = "yyyy年M月d日 HH时mm分ss秒";

    /**
     * 时间字符串转换成long
     * @param dateStr
     * @return
     */
    public static Long strDate2Long(String dateStr){
        if(StringUtils.isEmpty(dateStr)){
            return null;
        }
        DateFormat df = new SimpleDateFormat(YYYY_MM_DD);
        try {
            return df.parse(dateStr).getTime();
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        return null;
    }

    /**
     * 时间字符串转换成long yyyy-MM-dd HH:MM:SS
     * @param dateStr
     * @return
     */
    public static Long str2Long(String dateStr){
        if(StringUtils.isEmpty(dateStr)){
            return null;
        }
        DateFormat df = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
        try {
            return df.parse(dateStr).getTime();
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        return null;
    }

    /**
     * long转换成时间字符串 yyyy-MM-dd HH:mm:ss
     * @param time
     * @return
     */
    public static String long2Str(Long time){
        if(time == null){
            return null;
        }
        DateFormat df = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
        return df.format(new Date(time));
    }

    public static String long2StrForAlarmLog(Long time){
        if(time == null){
            return null;
        }
        DateFormat df = new SimpleDateFormat(DATE_FORMAT_ALARM_LOG);
        return df.format(new Date(time));
    }

    public static String long2StrForAlarmMsg(Long time){
        if(time == null){
            return null;
        }
        DateFormat df = new SimpleDateFormat(DATE_FORMAT_ALARM_MSG);
        return df.format(new Date(time));
    }

    public static Long Date2Long(Date dateStr){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(dateStr);
        if(StringUtils.isEmpty(dateString)){
            return null;
        }
        DateFormat df = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
        try {
            return df.parse(dateString).getTime();
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        return null;
    }

    public static Long str2Longss(String dateStr){
        if(StringUtils.isEmpty(dateStr)){
            return null;
        }
        DateFormat df = new SimpleDateFormat(YYYY_MM_DD_HHmmss);
        try {
            return df.parse(dateStr).getTime();
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        return null;
    }
}
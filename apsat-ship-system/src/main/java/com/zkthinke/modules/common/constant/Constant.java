package com.zkthinke.modules.common.constant;

/**
 * @Author: qijun
 * @Date: 2019/11/16 19:46
 */
public class Constant {

    public static final Double ARC = 6371.393*1000;
    public static final Integer RADIUS = 300;

    // 偏航短信模板
    public static final String SMS_TEMPLATECODE_YAW = "SMS_215121362";
    public static final String SMS_TEMPLATECODE_NOT_YAW = "SMS_215065770";
    public static final String SMS_TEMPLATECODE_CHANGE_TARGET = "SMS_215065772";
    public static final String SMS_TEMPLATECODE_NO_POWER = "SMS_215120749";
    public static final String SMS_TEMPLATECODE_NOT_NO_POWER = "SMS_215065776";
    public static final String SMS_TEMPLATECODE_DEPTH_STRANDING = "SMS_232916536";
    public static final String SMS_TEMPLATECODE_ARTIFICIAL_FENCE_STRANDING = "SMS_232891591";

    //预警类型
    public static final String ALARM_TYPE_YAW = "0";
    public static final String ALARM_TYPE_NOT_YAW = "1";
    public static final String ALARM_TYPE_CHANGE_TARGET = "2";
    public static final String ALARM_TYPE_NO_POWER = "3";
    public static final String ALARM_TYPE_NOT_NO_POWER = "4";
    public static final String ALARM_TYPE_HIT_ROCK = "5";
    public static final String ALARM_TYPE_ANCHOR_WALK = "6";
    public static final String ALARM_TYPE_NOT_ANCHOR_WALK = "7";
    public static final String ALARM_TYPE_TYPHOON = "8";
    public static final String ALARM_TYPE_STRANDING = "9";

    //预警类型描述
    public static final String ALARM_TYPE_YAW_DESC = "偏离航线.";
    public static final String ALARM_TYPE_NOT_YAW_DESC = "恢复航线.";
    public static final String ALARM_TYPE_CHANGE_TARGET_DESC = "目的地变更.";
    public static final String ALARM_TYPE_NO_POWER_DESC = "失去动力.";
    public static final String ALARM_TYPE_NOT_NO_POWER_DESC = "恢复动力.";
    public static final String ALARM_TYPE_HIT_ROCK_DESC = "有触礁风险";
    public static final String ALARM_TYPE_ANCHOR_WALK_DESC = "船舶有走锚风险";
    public static final String ALARM_TYPE_NOT_ANCHOR_WALK_DESC = "船舶走锚风险消除";
    public static final String ALARM_TYPE_TYPHOON_DESC = "台风预警";
    public static final String ALARM_TYPE_STRANDING_DESC = "搁浅风险";


    public static final String WATERDEEP_API_LOGIN_USER = "ytxk_visit";
    public static final String WATERDEEP_API_LOGIN_PASS = "97dec13d007cdbdea68a8e7eca199718";

    /*RabbitMQ*/
    /**通道*/
    public final static String EXCHANGE_TOPIC="exchange_topic";
    /**通道*/
    public final static String EXCHANGE_DIRECT="exchange_direct";
    /**通道*/
    public static final String EXCHANGE_FANOUT = "exchange_fanout";

    /**短信队列*/
    public final static String QUEUE_SMS="queue_sms";
    /**邮件队列*/
    public final static String QUEUE_EMAIL="queue_email";
    /**系统消息队列*/
    public final static String QUEUE_SYS="queue_sys";
    public static final String QUEUE_FANOUT_A = "fanout_a";
    public static final String QUEUE_FANOUT_B = "fanout_b";
    /**消息队列*/
    public final static String QUEUE_TOPIC_A="queue_topic_A";
    public final static String QUEUE_TOPIC_B="queue_topic_B";

    /**key*/
    public final static String DIRECT_KEY_SMS="sms";
    public final static String DIRECT_KEY_EMAIL="email";
    public final static String DIRECT_KEY_SYS="sys";
    public final static String KEY_TOPIC_A="send.A";
    public final static String KEY_TOPIC_B="send.B";
    public final static String KEY_TOPIC="send.*";


}

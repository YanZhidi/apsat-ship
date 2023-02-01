package com.zkthinke.modules.security.utils;

/**
 * @author weicb
 * @date 7/9/20 2:14 PM
 */
public class PwdSecurityConfig {

    /**
     * 密码最小长度，默认为8
     */
    public static int MIN_LENGTH = 8;
    /**
     * 密码最大长度，默认为20
     */
    public static int MAX_LENGTH = 20;

    /**
     * 默认的特殊符号集合
     */
    public static String SPECIAL_CHAR="!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";

    /**
     * 键盘横向与连续字符方向规则
     */
    public static String[] KEYBOARD_HORIZONTAL_ARR = {
            "!@#$%^&*()_+",
            "qwertyuiop",
            "asdfghjkl",
            "zxcvbnm",
    };

}

package com.zkthinke.modules.security.utils;

/**
 * 密码安全策略，杜绝弱口令
 * @author weicb
 * @date 7/9/20 2:07 PM
 */
public class CheckPwdUtils {

    /**
     * @brief   检测密码中字符长度
     * @param[in] password            密码字符串
     * @return  符合长度要求 返回true
     */
    public static boolean checkPasswordLength(String password) {
        if (password.length() >= PwdSecurityConfig.MIN_LENGTH &&
                password.length() <= PwdSecurityConfig.MAX_LENGTH) {
            return true;
        }
        return false;
    }

    /**
     * @brief   检测密码中是否包含数字
     * @param[in] password            密码字符串
     * @return  包含数字 返回true
     */
    public static boolean checkContainDigit(String password) {
        char[] chPass = password.toCharArray();
        boolean flag = false;

        for (int i = 0; i < chPass.length; i++) {
            if (Character.isDigit(chPass[i])) {
                flag = true;
                break;
            }
        }

        return flag;
    }



    /**
     * @brief   检测密码中是否包含小写字母
     * @param[in] password            密码字符串
     * @return  包含小写字母 返回true
     */
    public static boolean checkContainLowerCase(String password) {
        char[] chPass = password.toCharArray();
        boolean flag = false;

        for (int i = 0; i < chPass.length; i++) {
            if (Character.isLowerCase(chPass[i])) {
                flag = true;
                break;
            }
        }

        return flag;
    }

    /**
     * @brief   检测密码中是否包含大写字母
     * @param[in] password            密码字符串
     * @return  包含大写字母 返回true
     */
    public static boolean checkContainUpperCase(String password) {
        char[] chPass = password.toCharArray();
        boolean flag = false;

        for (int i = 0; i < chPass.length; i++) {
            if (Character.isUpperCase(chPass[i])) {
                flag = true;
                break;
            }
        }

        return flag;
    }

    /**
     * @brief   检测密码中是否包含特殊符号
     * @param[in] password            密码字符串
     * @return  包含特殊符号 返回true
     */
    public static boolean checkContainSpecialChar(String password) {
        char[] chPass = password.toCharArray();
        boolean flag = false;

        for (int i = 0; i < chPass.length; i++) {
            if (PwdSecurityConfig.SPECIAL_CHAR.indexOf(chPass[i]) != -1) {
                flag = true;
                break;
            }
        }

        return flag;
    }

    /**
     * @brief   键盘规则匹配器 横向连续检测
     * @param[in] password            密码字符串
     * @return  含有横向连续字符串 返回true
     */
    public static boolean checkLateralKeyboardSite(String password) {
        String t_password = new String(password);
        //将所有输入字符转为小写
        t_password = t_password.toLowerCase();
        int n = t_password.length();
        /**
         * 键盘横向规则检测
         */
        boolean flag = false;
        int arrLen = PwdSecurityConfig.KEYBOARD_HORIZONTAL_ARR.length;
        int limit_num = 6;

        for(int i=0; i+limit_num<=n; i++) {
            String str = t_password.substring(i, i+limit_num);
            String distinguishStr = password.substring(i, i+limit_num);

            for(int j=0; j<arrLen; j++) {
                String PwdSecurityConfigStr = PwdSecurityConfig.KEYBOARD_HORIZONTAL_ARR[j];
                String revOrderStr = new StringBuffer(PwdSecurityConfig.KEYBOARD_HORIZONTAL_ARR[j]).reverse().toString();

                //考虑 大写键盘匹配的情况
                String UpperStr = PwdSecurityConfig.KEYBOARD_HORIZONTAL_ARR[j].toUpperCase();
                if((PwdSecurityConfigStr.indexOf(distinguishStr) != -1) || (UpperStr.indexOf(distinguishStr) != -1)) {
                    flag = true;
                    return flag;
                }
                //考虑逆序输入情况下 连续输入
                String revUpperStr = new StringBuffer(UpperStr).reverse().toString();
                if((revOrderStr.indexOf(distinguishStr) != -1) || (revUpperStr.indexOf(distinguishStr) != -1)) {
                    flag = true;
                    return flag;
                }

            }
        }
        return flag;
    }

    /**
     * @brief   评估a-z,z-a,0-9这样的连续字符
     * @param[in] password 密码字符串
     * @return  含有a-z,z-a,0-9连续字符串 返回true
     */
    public static boolean checkSequentialChars(String password) {
        String t_password = new String(password);
        boolean flag = false;
        int limit_num = 6;
        int normal_count = 0;
        int reversed_count = 0;

        //检测包含字母(区分大小写)
        int n = t_password.length();
        char[] pwdCharArr = t_password.toCharArray();

        for (int i=0; i+limit_num<=n; i++) {
            normal_count = 0;
            reversed_count = 0;
            for (int j=0; j<limit_num-1; j++) {
                if (pwdCharArr[i+j+1]-pwdCharArr[i+j]==1) {
                    normal_count++;
                    if(normal_count == limit_num -1){
                        return true;
                    }
                }

                if (pwdCharArr[i+j]-pwdCharArr[i+j+1]==1) {
                    reversed_count++;
                    if(reversed_count == limit_num -1){
                        return true;
                    }
                }
            }
        }
        return flag;
    }

    /**
     * @brief   评估aaaa,1111这样的相同连续字符
     * @param[in] password            密码字符串
     * @return  含有aaaa,1111等连续字符串 返回true
     */
    public static boolean checkSequentialSameChars(String password) {
        String t_password = new String(password);
        int n = t_password.length();
        char[] pwdCharArr = t_password.toCharArray();
        boolean flag = false;
        int limit_num = 6;
        int count = 0;
        for (int i=0; i+limit_num<=n; i++) {
            count=0;
            for (int j=0; j<limit_num-1; j++) {
                if(pwdCharArr[i+j] == pwdCharArr[i+j+1]) {
                    count++;
                    if (count == limit_num -1){
                        return true;
                    }
                }
            }
        }
        return flag;
    }

    /**
     * @brief   评估密码中包含的字符类型是否符合要求
     * 1、长度在八位以上；
     * 2、含大、小写字母、数字及特殊字符中的三种类型以上；
     * 3、不含 6 位及以上规律序列（如123456/abcdef/!@#¥%^等）;
     * 4、不含 6 位及以上重复序列（如111111/aaaaaa等）;
     * @param[in] password            密码字符串
     * @return  符合要求 返回true
     */
    public static boolean EvalPWD(String password) {
        if (password == null || "".equals(password)) {
            return false;
        }
        boolean flag = false;

        /**
         * 检测长度
         */
        flag = checkPasswordLength(password);
        if (!flag) {
            return false;
        }
        /**
         * 大小写，数字，特殊符号，满足其中三种即可
         */
        int i = 0;
        /**
         * 检测包含数字
         */
        flag = checkContainDigit(password);
        if (flag) {
            i++;
        }
        /**
         * 检测包含字母(区分大小写)
         */
        flag = checkContainLowerCase(password);
        if (flag) {
            i++;
        }
        flag = checkContainUpperCase(password);
        if (flag) {
            i++;
        }

        /**
         * 检测包含特殊符号
         */
        flag = checkContainSpecialChar(password);
        if (flag) {
            i++;
        }
        if(i<3){
            return false;
        }

        /**
         * 检测键盘横向连续
         */
        flag = checkLateralKeyboardSite(password);
        if (flag) {
            return false;
        }


        /**
         * 检测逻辑位置连续
         */
        flag = checkSequentialChars(password);
        if (flag) {
            return false;
        }

        /**
         * 检测相邻字符是否相同
         */
        flag = checkSequentialSameChars(password);
        if (flag) {
            return false;
        }
        return true;
    }

}

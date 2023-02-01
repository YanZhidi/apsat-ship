package com.zkthinke.modules.common.utils;

/**
 * 二进制字符串转换工具类
 */
public class BinaryStringConverteUtil {

    private static final String BIN_SEPARATOR = " ";


    /**
     * 字符串转换为二进制字符串
     * @param str 普通字符串
     * @return String 二进制字符串
     */
    public static String toBinaryString(String str) {

        if (str == null) return null;

        StringBuffer sb = new StringBuffer();

        byte[] bytes = str.getBytes();
        for (byte aByte : bytes) {
            sb.append(Integer.toBinaryString(aByte) + BIN_SEPARATOR);
        }
        return sb.toString();
    }


    /**
     * 二进制字符串转换为普通字符串
     * @param binaryStr 二进制字符串
     * @return String 普通字符串
     */
    public static String toString(String binaryStr) {

        if (binaryStr == null) return null;

        String[] binArrays = binaryStr.split(BIN_SEPARATOR);


        StringBuffer sb = new StringBuffer();
        for (String binStr : binArrays) {
            char c = binstrToChar(binStr);
            sb.append(c);
        }
        return sb.toString();
    }

    /**
     * 统计二进制字符串中1的个数
     * @param binaryStr 二进制字符，如:1101
     * @return int
     */
    public static int countBitOne(String binaryStr) {
        int cnt = 0;
        if(binaryStr != null) {
            byte[] bytes = binaryStr.getBytes();
            for (byte aByte : bytes) {
                if (aByte==49) {
                    cnt++;
                }
            }
        }
        return cnt;
    }

    /**
     * 二进制字符转换为int数组
     * @param binStr 二进制字符串
     * @return int[]
     */
    private static int[] binstrToIntArray(String binStr) {
        char[] temp=binStr.toCharArray();
        int[] result=new int[temp.length];
        for(int i=0;i<temp.length;i++) {
            result[i]=temp[i]-48;
        }
        return result;
    }

    /**
     * 将二进制转换成字符
     * @param binStr 二进制字符串
     * @return char
     */
    public static char binstrToChar(String binStr){
        int[] temp=binstrToIntArray(binStr);
        int sum=0;
        for(int i=0; i<temp.length;i++){
            sum +=temp[temp.length-1-i]<<i;
        }
        return (char)sum;
    }

    public static String StrToBinstr(String str) {
        char[] strChar = str.toCharArray();
        String result = "";
        for (int i = 0; i < strChar.length; i++) {
            result += Integer.toBinaryString(strChar[i]) + " ";
        }
        return result;
    }

    //字符串转Ascill
    public static String stringToAscii(String value)
    {
        StringBuffer sbu = new StringBuffer();
        char[] chars = value.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if(i != chars.length - 1)
            {
                sbu.append((int)chars[i]).append(",");
            }
            else {
                sbu.append((int)chars[i]);
            }
        }
        return sbu.toString();
    }

    public static void main(String[] args) {
        String vod1Acsii = stringToAscii("13u?etPv2;0n:dDPwUM1U1Cb069D");
        String[] vod1Split = vod1Acsii.split(",");
        StringBuffer vdo1bitTOmessage = new StringBuffer();
        for (String s2 : vod1Split) {
            String bit6 = BinaryStringConverteUtil.to6Binary(Integer.valueOf(s2));
            vdo1bitTOmessage.append(bit6);
        }
        System.out.println(vdo1bitTOmessage);
    }

    public static String toBinary(int num) {
        int value = 1 << 8 | num;
        String bs = Integer.toBinaryString(value); //0x20 | 这个是为了保证这个string长度是6位数
        return  bs.substring(3,9);
    }

    //将数字转为六位二进制
    public static String to6Binary(Integer num){
        int value = num + 40;
        if(value>128){
            value+=32;
        }else {
            value+=40;
        }
        int to = 1 << 8 | value;
        String bs = Integer.toBinaryString(to);
        return bs.substring(3,9);
    }

    //将六位二进制转化为十进制数据，二进制转为十进制也可使用该方法
    public static Integer bitToinfo(String value){
        return Integer.parseInt(value,2);
    }

    //信息五中的二进制转ACSll值
    public static String convert(int[] paramArr) {
        StringBuffer sb = new StringBuffer();
        for (int i : paramArr) {
            if (i>=32) {
                sb.append((char)i);
            } else {
                sb.append((char)(i+64));
            }
        }
        return sb.toString();
    }

}


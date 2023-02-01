package com.zkthinke.modules.security.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author weicb
 * @date 2020/6/18 14:08
 */
public class SecurityUtil {


    public static String signGenerator(String secret, String... params) throws NoSuchAlgorithmException {
        StringBuffer sb = new StringBuffer(secret);
        Arrays.stream(params).peek(Objects::requireNonNull).reduce(null, (acc, item) -> {
            if (acc == null) {
                return item;
            }
            sb.append(acc).append(item);
            return null;
        });
        sb.append(secret);
        return md5(sb.toString());
    }

    public static String md5(String message) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.reset();
        messageDigest.update(message.getBytes(StandardCharsets.UTF_8));

        StringBuilder md5StrBuff = new StringBuilder();
        byte[] byteArray = messageDigest.digest();
        for (byte aByteArray : byteArray) {
            if (Integer.toHexString(0xFF & aByteArray).length() == 1)
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & aByteArray));
            else
                md5StrBuff.append(Integer.toHexString(0xFF & aByteArray));
        }
        return md5StrBuff.toString().toUpperCase();
    }


    public static void main(String[] args) {

    }
}

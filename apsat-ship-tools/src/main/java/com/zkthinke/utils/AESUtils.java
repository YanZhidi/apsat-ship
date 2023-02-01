package com.zkthinke.utils;

import org.springframework.util.StringUtils;
import sun.misc.BASE64Decoder;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

/**
 * @Author: huqijun
 * @Date: 2020/1/9 20:16
 */
public class AESUtils {

    /**
     *  aes解密
     * @param encryptStr 待解密字符串
     * @param decryptKey 解密密钥
     * @return 解密后的string
     */
    public static String aesDecrypt(String encryptStr, String decryptKey) {
        if(StringUtils.isEmpty(encryptStr) || StringUtils.isEmpty(decryptKey)){
            return "";
        }
        try {
            byte[] bEncryptStr = new BASE64Decoder().decodeBuffer(encryptStr);
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(128);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(decryptKey.getBytes(), "AES"));
            byte[] decryptBytes = cipher.doFinal(bEncryptStr);
            String sDecryptBytes = new String(decryptBytes);
            return sDecryptBytes;
        } catch (Exception e) {
            return "";
        }
    }
}
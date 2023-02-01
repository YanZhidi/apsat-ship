package com.zkthinke.modules.security.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Decoder;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * @author weicb
 * @date 2020/6/19 12:29
 */
public class EncryptUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(EncryptUtil.class);

    private static final String ALGORITHM = "AES";
    private static final String KEY_GCM_AES = "AES/GCM/NoPadding";
    private static final int AES_KEY_SIZE = 128;

    /**
     * 生成length字节的偏移量IV
     * createIV的功能<br>
     *
     * @param length
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String createIV(int length) throws NoSuchAlgorithmException {
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[length];
        random.nextBytes(salt);
        return encodeToBase64(salt);
    }

    /**
     * 加密——使用自定义的加密key
     *
     * @param data
     * @param key
     * @param ivStr
     * @return
     * @throws Exception
     */
    public static String encryptByGcm(String data, String key, String ivStr) throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(KEY_GCM_AES);

        byte[] iv = decodeFromBase64(ivStr);
        SecretKeySpec keySpec = getSecretKeySpec(key);
        cipher.init(1, keySpec, new GCMParameterSpec(AES_KEY_SIZE, iv));
        byte[] content = data.getBytes(StandardCharsets.UTF_8);
        byte[] result = cipher.doFinal(content);
        return encodeToBase64(result);
    }

    /**
     * 解密——使用自定义的加密key
     *
     * @param data
     * @param key
     * @param ivStr
     * @return
     * @throws Exception
     */
    public static String decryptByGcm(String data, String key, String ivStr) throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(KEY_GCM_AES);

        byte[] iv = decodeFromBase64(ivStr);
        SecretKeySpec keySpec = getSecretKeySpec(key);
        cipher.init(2, keySpec, new GCMParameterSpec(AES_KEY_SIZE, iv));
        byte[] content = decodeFromBase64(data);
        byte[] result = cipher.doFinal(content);
        return new String(result, StandardCharsets.UTF_8);
    }

    private static byte[] decodeFromBase64(String data) {
        return Base64.getDecoder().decode(data);
    }

    private static String encodeToBase64(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    /**
     * 公共使用，获取SecretKeySpec
     *
     * @param key
     * @return
     */
    private static SecretKeySpec getSecretKeySpec(String key) {
        SecretKeySpec keySpec = null;
        try {
            //1.构造密钥生成器，指定为AES算法,不区分大小写
            KeyGenerator kgen = KeyGenerator.getInstance(ALGORITHM);
            //2.根据ecnodeRules规则初始化密钥生成器
            //生成一个128位的随机源,根据传入的字节数组
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(key.getBytes(StandardCharsets.UTF_8));
            kgen.init(AES_KEY_SIZE, secureRandom);
            //3.产生原始对称密钥
            SecretKey secretKey = kgen.generateKey();
            //4.获得原始对称密钥的字节数组
            byte[] enCodeFormat = secretKey.getEncoded();
            //5.根据字节数组生成AES密钥
            keySpec = new SecretKeySpec(enCodeFormat, ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("To do get SecretKeySpec exception!",e);
        }
        return keySpec;
    }

}

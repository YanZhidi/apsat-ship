package com.zkthinke.utils;

import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;
import static com.zkthinke.utils.EncryptUtils.*;

public class EncryptUtilsTest {

    /**
     * 对称加密
     */
    @Test
    public void testDesEncrypt() {
        try {
            assertEquals("7772841DC6099402", desEncrypt("123456"));
            assertEquals("F17Q+/RfTI/1F8ivA2VWPw==", aesEncrypt("123456", "r73IYsjMHh33LQ0Y"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 对称解密
     */
    @Test
    public void testDesDecrypt() {
        try {
            assertEquals("123456", desDecrypt("7772841DC6099402"));
            assertEquals("123456", aesDecrypt("F17Q+/RfTI/1F8ivA2VWPw==", "r73IYsjMHh33LQ0Y"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

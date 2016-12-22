package com.ruihai.xingka.utils;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES加解密工具类
 * <p/>
 * Created by zecker on 15/8/12.
 */
public class Security {

    final static String AES_KEY = "Ce^dkT*dz1!O1Gc=";
    final static String AES_KEY1 = "Ar*vlH^dz1)O8Gj=";

    /**
     * AES加密
     *
     * @param str 待加密字符串
     * @return 加密后字符串
     */
    public static String aesEncrypt(String str) {
        try {
            String password = AES_KEY;
            SecretKeySpec skeySpec = new SecretKeySpec(password.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            String strTmp = Base64.encodeToString(cipher.doFinal(str.getBytes()), Base64.DEFAULT);
            return strTmp;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * AES解密
     *
     * @param str 待解密字符串
     * @return 解密后字符串
     */
    public static String aesDecrypt(String str) {
        try {
            String password = AES_KEY;
            SecretKeySpec skeySpec = new SecretKeySpec(password.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            String strTmp = new String(cipher.doFinal(Base64.decode(str, Base64.DEFAULT)));
            return strTmp;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return str;
    }

    /**
     * AES加密
     *
     * @param str 待加密字符串
     * @return 加密后字符串
     */
    public static String aesEncrypt1(String str) {
        try {
            String password = AES_KEY1;
            SecretKeySpec skeySpec = new SecretKeySpec(password.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            String strTmp = Base64.encodeToString(cipher.doFinal(str.getBytes()), Base64.NO_WRAP);
            return strTmp;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }
}

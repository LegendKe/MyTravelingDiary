package com.ruihai.xingka.utils;


import android.util.Base64;

import com.qiniu.android.utils.UrlSafeBase64;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by zecker on 15/8/25.
 */
public class QiniuHelper {
    // 七牛密钥 https://portal.qiniu.com
    private static final String QINIU_AK = "AEmkHhQAbG8sOUox3kuYmiWhMY-eTXbx_cSoerFr";
    private static final String QINIU_SK = "TCLdq8DZIidEI6cw2NGAxKkvyjkw533YQhASUI12";
    private static final String QINIU_BUCKNAME = "images";

    public final static String FILE_BASE_URL = "http://file.xingka.cc/";

    public final static String STYLE_THUMBNAIL_96 = "thumbnail96";
    public final static String STYLE_THUMBNAIL_200 = "thumbnail200";

    private static final String THUMBNAIL_IMAGE_SUFFIX = "_thumbnail";
    private static final String MEDIUM_IMAGE_SUFFIX = "_bmiddle";
    private static final String LARGE_IMAGE_SUFFIX = "_large";
    private static final String TOPIC_COVER_SUFFIX = "_topic.cover";

    /**
     * 获取缩略图宽高度为96px的缩略图
     *
     * @param imgName 图片Key
     * @return
     */
    public static String getThumbnail96Url(String imgName) {
        return String.format("%s%s_%s", FILE_BASE_URL, imgName, STYLE_THUMBNAIL_96);
    }

    /**
     * 获取缩略图宽高度为200px的缩略图
     *
     * @param imgName 图片Key
     * @return
     */
    public static String getThumbnail200Url(String imgName) {
        return String.format("%s%s_%s", FILE_BASE_URL, imgName, STYLE_THUMBNAIL_200);
    }

    /**
     * 获取小尺寸缩略图
     *
     * @param imgKey 图片Key
     * @return
     */
    public static String getThumbnailWithKey(String imgKey) {
        return String.format("%s%s%s", FILE_BASE_URL, imgKey, THUMBNAIL_IMAGE_SUFFIX);
    }

    /**
     * 根据url获取压缩小尺寸缩略图
     *
     * @param imgUrl 图片原地址
     * @return
     */
    public static String getThumbnailWithUrl(String imgUrl) {
        return String.format("%s%s", imgUrl, THUMBNAIL_IMAGE_SUFFIX);
    }

    /**
     * 获取中尺寸图片
     *
     * @param imgKey 图片Key
     * @return
     */
    public static String getMediumWithKey(String imgKey) {
        return String.format("%s%s%s", FILE_BASE_URL, imgKey, MEDIUM_IMAGE_SUFFIX);
    }

    /**
     * 根据url获取压缩中尺寸图片
     *
     * @param imgUrl 图片原地址
     * @return
     */
    public static String getMediumWithUrl(String imgUrl) {
        return String.format("%s%s", imgUrl, MEDIUM_IMAGE_SUFFIX);
    }

    /**
     * 获取大尺寸图片
     *
     * @param imgKey 图片Key
     * @return
     */
    public static String getLargeWithKey(String imgKey) {
        return String.format("%s%s%s", FILE_BASE_URL, imgKey, LARGE_IMAGE_SUFFIX);
    }

    /**
     * 根据url获取压缩大尺寸图片
     *
     * @param imgUrl
     * @return
     */
    public static String getLargeWithUrl(String imgUrl) {
        return String.format("%s%s", imgUrl, LARGE_IMAGE_SUFFIX);
    }

    /**
     * 根据url获取水印图片
     * @param imageUrl
     * @param userNick
     * @return
     */
    public static String getLargeWatermarkWithUrl(String imageUrl, String userNick) {
        try {
            String base64Nick = Base64.encodeToString(userNick.getBytes(), Base64.URL_SAFE);
            String urlNick = URLEncoder.encode(base64Nick, "UTF-8");
            return String.format("%s?imageView2/1/w/1280/q/100/format/jpg|watermark/3/image/aHR0cDovL2ZpbGUueGluZ2thLmNjL3hrX3dhdGVybWFya19sb2dvXzI4LnBuZw==/dissolve/100/gravity/SouthWest/dx/10/dy/10/text/UEhPVE8gQlk=/font/ZnJhbmtsaW4gZ290aGljIG1lZGl1bQ==/fontsize/400/fill/I0ZGRkZGRg==/dissolve/100/gravity/SouthWest/dx/43/dy/12/text/%s/font/5b6u6L2v6ZuF6buR/fontsize/300/fill/I0ZGRkZGRg==/dissolve/100/gravity/SouthWest/dx/136/dy/15", imageUrl, urlNick);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return imageUrl;
        }
    }

    /**
     * 获取图说列表浏览图片
     *
     * @param imgKey
     * @return
     */
    public static String getTopicCoverWithKey(String imgKey) {
        return String.format("%s%s%s", FILE_BASE_URL, imgKey, TOPIC_COVER_SUFFIX);
    }

    /**
     * 为了优化程序图片加载速度, 不提供使用原图加载显示, 请根据需要选择合适尺寸的图片地址
     *
     * @param imgKey 图片Key
     * @return 获取原图url地址
     */
    public static String getOriginalWithKey(String imgKey) {
        return String.format("%s%s", FILE_BASE_URL, imgKey);
    }

    // 简单上传，使用默认策略
    public static String getUpToken() {
        try {
            // 1.确定上传策略
            // 2.将上传策略序列化称为json格式
            JSONObject jsonObj = new JSONObject();
            long deadline = System.currentTimeMillis() / 1000 + 3600;
            jsonObj.put("deadline", deadline);
            jsonObj.put("scope", QINIU_BUCKNAME);
            // 3.对json序列化后的上传策略进行URL安全的Base64编码,得到如下encoded
            String encodedPutPolicy = UrlSafeBase64.encodeToString(jsonObj.toString().getBytes());
            // 4.用SecretKey对编码后的上传策略进行HMAC-SHA1加密，并且做URL安全的Base64编码,得到如下的encodedSigned
            byte[] sign = HmacSHA1Encrypt(encodedPutPolicy, QINIU_SK);
            String encodedSigned = UrlSafeBase64.encodeToString(sign);
            // 5.将 AccessKey、encodeSigned 和 encoded 用 “:” 连接起来，得到如下的UploadToken
            String uploadToken = QINIU_AK + ":" + encodedSigned + ":" + encodedPutPolicy;
            return uploadToken;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static final String MAC_NAME = "HmacSHA1";
    private static final String ENCODING = "UTF-8";

    /**
     * 使用 HMAC-SHA1 签名方法对对encryptText进行签名
     *
     * @param encryptText
     * @param encryptKey
     * @return
     * @throws Exception
     */
    public static byte[] HmacSHA1Encrypt(String encryptText, String encryptKey)
            throws Exception {
        byte[] data = encryptKey.getBytes(ENCODING);
        // 根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
        SecretKey secretKey = new SecretKeySpec(data, MAC_NAME);
        // 生成一个指定 Mac 算法 的 Mac 对象
        Mac mac = Mac.getInstance(MAC_NAME);
        // 用给定密钥初始化 Mac 对象
        mac.init(secretKey);
        byte[] text = encryptText.getBytes(ENCODING);
        // 完成 Mac 操作
        return mac.doFinal(text);
    }
}

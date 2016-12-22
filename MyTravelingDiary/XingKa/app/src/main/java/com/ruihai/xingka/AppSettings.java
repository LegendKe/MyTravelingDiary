package com.ruihai.xingka;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.orhanobut.hawk.Hawk;

/**
 * Created by zecker on 15/10/2.
 */
public class AppSettings {

    /**
     * 发送成功后的震动反馈
     *
     * @return
     */
    public static boolean isSendVibrate() {
        return Hawk.get("pSendVibrate", true);
    }

    /**
     * 上传图片质量设置
     * @return
     */
    public static int getUploadSetting() {
        int value = Hawk.get("pUploadSetting", 3);
        return value;
    }

    /**
     * 设置投诉举报类型
     * @param value
     */
    public static void setReportTypeSetting(int value) {
        Hawk.put("pReportTypeSetting", value);
    }

    /**
     * 投诉举报类型设置
     * @return
     */
    public static int getReportTypeSetting() {
        int value = Hawk.get("pReportTypeSetting", 5);
        return value;
    }

    /**
     * 设置上传图片质量
     * @param value
     */
    public static void setUploadSetting(int value) {
        Hawk.put("pUploadSetting", value);
    }

    /**
     * 1.被关注的推送提醒
     *
     * @return
     */
    public static boolean isNotifyFollower() {
        return Hawk.get("pFollower", true);
    }

    /**
     * 2.被评论的推送提醒
     *
     * @return
     */
    public static boolean isNotifyComment() {
        return Hawk.get("pComment", true);
    }

    /**
     * 3.被赞的推送提醒
     * @return
     */
    public static boolean isNotifyPraise() {
        return Hawk.get("pPraise", true);
    }

    /**
     * 4.被@的推送提醒
     * @return
     */
    public static boolean isNotifyMention() {
        return Hawk.get("pMention", true);
    }

    /**
     * 5.系统消息推送提醒
     * @return
     */
    public static boolean isNotifySystem() {
        return Hawk.get("pSystem", true);
    }

    /**
     * 响铃提醒
     *
     * @return
     */
    public static boolean isNotifySound() {
        return Hawk.get("pNotifySound", true);
    }

    /**
     * 震动提醒
     *
     * @return
     */
    public static boolean isNotifyVibrate() {
        return Hawk.put("pNotifyVibrate", true);
    }

    /**
     * LED提醒
     *
     * @return
     */
    public static boolean isNotifyLED() {
        return Hawk.put("pNotifyLED", true);
    }

    /**
     * 获取JPush Registration ID
     * @return
     */
    public static String getJPushRegId() {
        return Hawk.get("deviceToken");
    }


    public static String getImageSavePath() {
        return Hawk.get("com.ruhai.xingka.Images", "XKImages");
    }

    public static void setImageSavePath(String path) {
        Hawk.put("com.ruhai.xingka.Images", path);
    }

}

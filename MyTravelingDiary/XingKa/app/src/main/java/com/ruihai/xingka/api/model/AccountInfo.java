package com.ruihai.xingka.api.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.orhanobut.hawk.Hawk;
import com.ruihai.xingka.XKCache;

import java.security.Key;
import java.util.List;

/**
 * Created by zecker on 15/9/5.
 */
public class AccountInfo {
    private static final String KEY_ACCOUNT = "user_account";
    private static final String KEY_PASSWORD = "user_password";//密码
    private static final String KEY_USER_TAG = "user_tag";//标签
    private static final String KEY_USER_CAR_INFO = "user_car_info";//用户车型
    private static final String KEY_CARBRAND = "carbrand"; //车品牌
    private static final String KEY_USER_DRAFTBOX = "user_draftbox";//图说草稿
    private static final String KEY_TRAVELTOGETHER_DRAFTBOX = "travel_together";//旅拼草稿

    private static final String KEY_USER_REPORTTYPE = "user_reporttype";//举报类型
    private static final String KEY_OFFICIALNUM = "officialNum";//系统通知

    private static final String KEY_FIRST_TIME = "first_time"; //第一次登录应用
    private static final String KEY_FRIEND_LIST = "friend_list"; //好友列表信息

    private static final String KEY_NOTDISTURBED_TIME = "notdisturbed_time"; //免打扰时间
    private static final String KEY_SWITCHBUTTON = "switchbutton"; //免打扰设置状态

    private static final String KEY_IM_ACCOUNT = "account"; //IM账号和token保存和获取
    private static final String KEY_IM_TOKEN = "token";

    private static final String KEY_IM_ISREG = "isReg"; //是否注册云信的保存和获取

    /* 持有私有静态实例，防止被引用，此处赋值为null，目的是实现延迟加载 */
    private static AccountInfo instance = null;

    /* 私有构造方法，防止被实例化 */
    private AccountInfo() {

    }

    /* 静态工程方法，创建实例 */
    public static AccountInfo getInstance() {
        if (instance == null) {
            instance = new AccountInfo();
        }
        return instance;
    }

    /**
     * 保存用户账户信息
     *
     * @param user
     */
    public void saveAccount(User user) {
        Hawk.put(KEY_ACCOUNT, user); // save an object
    }

    /**
     * 保存用户密码信息
     *
     * @param password
     */
    public void savePassWord(String password) {
        Hawk.put(KEY_PASSWORD, password); // save an object
    }

    //判断是否第一次进应用
    public void setFirstTime(boolean isFirstTime) {
        Hawk.put(KEY_FIRST_TIME, isFirstTime); // save an object
    }

    public Boolean isFirstTimeLogin() {
        if (Hawk.get(KEY_FIRST_TIME) != null) {
            return Hawk.get(KEY_FIRST_TIME);
        } else {
            return true;
        }

    }

    /**
     * IM账号和token保存和获取
     */
    public static void saveUserAccount(String account) {
        Hawk.put(KEY_IM_ACCOUNT, account);
    }

    public static String getUserAccount() {
        return Hawk.get(KEY_IM_ACCOUNT);
    }

    public static void saveUserToken(String token) {
        Hawk.put( KEY_IM_TOKEN, token);
    }

    public static String getUserToken() {
        return Hawk.get( KEY_IM_TOKEN);
    }

    /**
     * IM保存免打扰时间设置
     */
    public void saveNotDisturbedTime(String[] time) {
        Hawk.put(KEY_NOTDISTURBED_TIME, time);
    }
    public String[] loadNotDisturbedTime() {
        return Hawk.get(KEY_NOTDISTURBED_TIME);
    }

    /**
     * IM保存免打扰开关设置状态
     */
    public void saveState(boolean on) {
        Hawk.put(KEY_SWITCHBUTTON, on);
    }
    public Boolean getState() {
        return Hawk.get(KEY_SWITCHBUTTON);
    }

    /**
     * 判断是否注册IM
     */
    public void saveIsIMReg(boolean isIMReg) {
        Hawk.put(KEY_IM_ISREG, isIMReg);
    }
    public Boolean getIsIMReg() {
        return Hawk.get(KEY_IM_ISREG);
    }

    /**
     * 加载用户密码
     *
     * @return
     */
    public String loadPassWord() {
        return Hawk.get(KEY_PASSWORD);
    }

    /**
     * 加载用户账号信息
     *
     * @return
     */
    public User loadAccount() {
        return Hawk.get(KEY_ACCOUNT);
    }

    /**
     * 保存系统通知
     *
     * @param officialNum
     */
    public void saveOfficialNum(int officialNum) {
        Hawk.put(loadAccount().getAccount() + KEY_OFFICIALNUM, officialNum);
    }

    /**
     * 获取系统通知
     *
     * @return
     */
    public int getOfficialNum() {
        return Hawk.get(loadAccount().getAccount() + KEY_OFFICIALNUM, 0);
    }

    /**
     * 清除系统通知
     *
     * @return
     */
    public void clearOfficialNum() {
        Hawk.remove(KEY_OFFICIALNUM);
    }

    /**
     * 存储用户标签
     *
     * @param userTags
     */
    public void saveUserTags(List<UserTag> userTags) {
        Hawk.put(KEY_USER_TAG, userTags);
    }

    /**
     * 获取用户标签
     *
     * @return
     */
    public List<UserTag> getUserTags() {
        return Hawk.get(KEY_USER_TAG);
    }

    /**
     * 存储车品牌信息
     *
     * @param carBrand
     */
    public void saveCarBrand(List<CarBrandRepo.CarBrand> carBrand) {
        Hawk.put(KEY_CARBRAND, carBrand);
    }

    /**
     * 获取车品牌信息
     *
     * @return
     */
    public List<CarBrandRepo.CarBrand> getCarBrand() {
        return Hawk.get(KEY_CARBRAND);
    }

    /**
     * 存储我的车型信息
     *
     * @param userCarInfo
     */
    public void saveUserCarInfo(UserCarInfo userCarInfo) {
        Hawk.put(KEY_USER_CAR_INFO, userCarInfo);
    }

    /**
     * 获取我的车型信息
     *
     * @return
     */
    public UserCarInfo getUserCarInfo() {
        return Hawk.get(KEY_USER_CAR_INFO);
    }

    /**
     * 清除我的车型信息
     */
    public void clearUserCarInfo() {
        Hawk.remove(KEY_USER_CAR_INFO);
    }

    /**
     * 存储用户好友列表信息
     *
     * @param myFriendInfos
     */
    public void saveFriendList(List<MyFriendInfoRepo.MyFriendInfo> myFriendInfos) {
        Hawk.put(KEY_FRIEND_LIST + loadAccount().getAccount(), myFriendInfos);
    }

    /**
     * 获取用户好友列表信息
     */
    public List<MyFriendInfoRepo.MyFriendInfo> getFriendList() {
        return Hawk.get(KEY_FRIEND_LIST + loadAccount().getAccount(), null);
    }

    /**
     * 存储用户图说草稿信息
     *
     * @param draftBoxItem
     */
    public void saveUserDraftBoxInfo(DraftBoxItem draftBoxItem) {
        Hawk.put(KEY_USER_DRAFTBOX + loadAccount().getAccount(), draftBoxItem);
    }

    /**
     * 获取用户图说草稿信息
     *
     * @return
     */
    public DraftBoxItem getUserDraftBoxInfo() {
        return Hawk.get(KEY_USER_DRAFTBOX + loadAccount().getAccount());
    }

    /**
     * 清除用户草稿信息
     *
     * @return
     */
    public void clearUserDraftBoxInfo() {
        Hawk.remove(KEY_USER_DRAFTBOX + loadAccount().getAccount());
    }

    /**
     * 存储用户图说草稿信息
     *
     * @param travelTogetherInfo
     */
    public void saveTravelTogetherDraftBoxInfo(TravelTogetherInfo travelTogetherInfo) {
        Hawk.put(KEY_TRAVELTOGETHER_DRAFTBOX + loadAccount().getAccount(), travelTogetherInfo);
    }

    /**
     * 获取用户图说草稿信息
     *
     * @return
     */
    public TravelTogetherInfo getTravelTogetherDraftBoxInfo() {
        return Hawk.get(KEY_TRAVELTOGETHER_DRAFTBOX + loadAccount().getAccount());
    }

    /**
     * 清除用户草稿信息
     *
     * @return
     */
    public void clearTravelTogetherDraftBoxInfo() {
        Hawk.remove(KEY_TRAVELTOGETHER_DRAFTBOX + loadAccount().getAccount());
    }

    /**
     * 清除用户账户信息
     */
    public void clearAccount() {
        Hawk.remove(KEY_ACCOUNT);
    }

    /**
     * 保存举报类型
     *
     * @param type
     */
    public void saveReportType(List<ReportType> type) {
        Hawk.put(KEY_USER_REPORTTYPE, type);
    }

    /**
     * 获取举报类型
     *
     * @return
     */
    public List<ReportType> getReportType() {
        return Hawk.get(KEY_USER_REPORTTYPE);
    }

    /**
     * 判断用户是否登录
     *
     * @return
     */
    public boolean isLogin() {
        if (loadAccount() != null)
            return true;
        return false;
    }
}

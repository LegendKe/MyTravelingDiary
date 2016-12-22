package com.ruihai.xingka.api.model;

import com.google.gson.annotations.SerializedName;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import org.aisen.orm.annotation.PrimaryKey;

import java.io.Serializable;
import java.util.List;

/**
 * 用户信息实体类
 * <p/>
 * Created by zecker on 15/8/12.
 */
public class User implements Serializable {
    @PrimaryKey(column = "idStr")
    private String idstr;
    @SerializedName("U_Account")
    private int account; // 行账号
    @SerializedName("U_Pwd")
    private String password; //密码
    @SerializedName("C_Id")
    private String carBrand; //车品牌
    @SerializedName("U_Phone")
    private String phone; // 手机号码
    @SerializedName("U_Email")
    private String email; // 邮件
    @SerializedName("U_Nick")
    private String nickname; // 昵称
    @SerializedName("U_Sex")
    private int sex; // 性别 1-男 2-女 0-未选择
    @SerializedName("U_Img")
    private String avatar; // 图片Key字符串，到七牛获取
    @SerializedName("U_Adress")
    private String address; // 当前地理位置
    @SerializedName("U_Location")
    private String location; // 定位(经度|纬度)

    @SerializedName("U_BGImg")
    private String backgroud;//名片页面背景图

    @SerializedName("U_Talk")
    private String talk; // 一句话说说
    @SerializedName("U_KXB")
    private int xCoin; // 行币
    @SerializedName("U_Level")
    private int level; // 等级
    @SerializedName("U_Points")
    private int points; // 积分
    @SerializedName("U_AddTime")
    private String addTime; // 注册时间
    @SerializedName("U_LastLoginTime")
    private String lastLoginTime; // 最后登录时间
    @SerializedName("U_LoginCount")
    private String loginCount; // 登录次数
    @SerializedName("isOfficial")
    private boolean isOfficial;//是否官方账号
    @SerializedName("U_State")
    private int loginState;//登录状态  1禁止登录， 2发布的图说仅自己可见， 0正常
    @SerializedName("U_IsIM")
    private boolean isIM;

    public boolean isIM() {
        return isIM;
    }

    public void setIM(boolean IM) {
        isIM = IM;
    }

    public int getLoginState() {
        return loginState;
    }

    public void setLoginState(int loginState) {
        this.loginState = loginState;
    }


    private String pinYin;

    public String version = "0";
    private List<TagInfo> tagInfo;

    public List<TagInfo> getTagInfo() {
        return tagInfo;
    }

    public void setTagInfo(List<TagInfo> tagInfo) {
        this.tagInfo = tagInfo;
    }

    public String getBackgroud() {
        return backgroud;
    }

    public void setBackgroud(String backgroud) {
        this.backgroud = backgroud;
    }

    public boolean isOfficial() {
        return isOfficial;
    }

    public void setOfficial(boolean official) {
        isOfficial = official;
    }

    public int getAccount() {
        return account;
    }

    public void setAccount(int account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCarBrand() {
        return carBrand;
    }

    public void setCarBrand(String carBrand) {
        this.carBrand = carBrand;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTalk() {
        return talk;
    }

    public void setTalk(String talk) {
        this.talk = talk;
    }

    public int getxCoin() {
        return xCoin;
    }

    public void setxCoin(int xCoin) {
        this.xCoin = xCoin;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public String getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(String lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getLoginCount() {
        return loginCount;
    }

    public void setLoginCount(String loginCount) {
        this.loginCount = loginCount;
    }

    public String getPinYin() {
        return pinYin;
    }

    public void setPinYin(String pinYin) {
        this.pinYin = getFirstLetters(getNickname()).toUpperCase();
    }

    public String getIdstr() {
        return String.valueOf(this.account);
    }

    public static String getFirstLetters(String chinese) {
        StringBuffer pybf = new StringBuffer();
        char[] arr = chinese.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > 128) {
                try {
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(arr[i], defaultFormat);
                    if (temp != null) {
                        pybf.append(temp[0].charAt(0));
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                pybf.append(arr[i]);
            }
        }
        return pybf.toString().replaceAll("\\W", "").trim();
    }

    public String getFirstLetter() {
        String letter = pinYin.substring(0, 1).toUpperCase();
        if (0 <= letter.compareTo("A") && letter.compareTo("Z") <= 0) {
            return letter;
        }
        return "#";
    }
}

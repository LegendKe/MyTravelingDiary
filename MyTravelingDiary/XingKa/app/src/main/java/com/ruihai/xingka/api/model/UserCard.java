package com.ruihai.xingka.api.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by zecker on 15/10/8.
 */
public class UserCard extends XKRepo implements Serializable {
    @SerializedName("Img")
    private String avatar;
    @SerializedName("Sex")
    private int sex;
    @SerializedName("Account")
    private String account;
    @SerializedName("Nick")
    private String nick;
    @SerializedName("Talk")
    private String talk;
    @SerializedName("Address")
    private String address;
    @SerializedName("CarImg")
    private String carImage;
    @SerializedName("CarName")
    private String carName;
    @SerializedName("userCounts")
    private UserCounts userCounts;
    @SerializedName("Remark")
    private String remark;
    @SerializedName("Nexus")
    private int nexus;  //被查看人是查看人的什么关系
    @SerializedName("BGImg")
    private String bgImg;//名片背景墙图片
    @SerializedName("isOfficial")
    private boolean isOfficial;//是否官方账号
    @SerializedName("isSignIn")
    private boolean isSignIn;//是否签到
    @SerializedName("Integral")
    private int integral;  //总积分
    @SerializedName("TravelTogetherNum")
    private int travelTogetherNum; //旅拼数量

    @SerializedName("isIMReg")
    private boolean isIMReg;  //是否注册过云信
    @SerializedName("isIMFriend")
    private boolean isIMFriend; //是否是云信好友
    @SerializedName("isIMBlack")
    private boolean isIMBlack; //是否云信黑名单

    public boolean isIMReg() {
        return isIMReg;
    }

    public void setIMReg(boolean IMReg) {
        isIMReg = IMReg;
    }

    public boolean isIMFriend() {
        return isIMFriend;
    }

    public void setIMFriend(boolean IMFriend) {
        isIMFriend = IMFriend;
    }

    public boolean isIMBlack() {
        return isIMBlack;
    }

    public void setIMBlack(boolean IMBlack) {
        isIMBlack = IMBlack;
    }

    public boolean isSignIn() {
        return isSignIn;
    }

    public void setSignIn(boolean signIn) {
        isSignIn = signIn;
    }

    public int getIntegral() {
        return integral;
    }

    public void setIntegral(int integral) {
        this.integral = integral;
    }


    public String getBgImg() {
        return bgImg;
    }

    public void setBgImg(String bgImg) {
        this.bgImg = bgImg;
    }

    public int getNexus() {
        return nexus;
    }

    public void setNexus(int nexus) {
        this.nexus = nexus;
    }

    public boolean isOfficial() {
        return isOfficial;
    }

    public void setOfficial(boolean official) {
        isOfficial = official;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getTalk() {
        return talk;
    }

    public void setTalk(String talk) {
        this.talk = talk;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCarImage() {
        return carImage;
    }

    public void setCarImage(String carImage) {
        this.carImage = carImage;
    }

    public UserCounts getUserCounts() {
        return userCounts;
    }

    public void setUserCounts(UserCounts userCounts) {
        this.userCounts = userCounts;
    }

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public int getTravelTogetherNum() {
        return travelTogetherNum;
    }

    public void setTravelTogetherNum(int travelTogetherNum) {
        this.travelTogetherNum = travelTogetherNum;
    }


}

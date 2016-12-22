package com.ruihai.xingka.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by apple on 15/9/8.
 */
public class MaybeInfo {

    @SerializedName("Account")
    private int account; // 行账号
    @SerializedName("Nick")
    private String nick; // 昵称
    @SerializedName("Img")
    private String img; // 头像
    @SerializedName("Phone")
    private String phone; // 手机号码
    @SerializedName("CommonFriend")
    private String commonFriend; // 共同好友

    public String getCommonFriend() {
        return commonFriend;
    }

    public void setCommonFriend(String commonFriend) {
        this.commonFriend = commonFriend;
    }

    public int getAccount() {
        return account;
    }

    public void setAccount(int account) {
        this.account = account;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

}

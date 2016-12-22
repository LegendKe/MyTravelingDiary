package com.ruihai.xingka.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mac on 15/9/25.
 */
public class SearchUser {
    @SerializedName("account")
    private int account;
    @SerializedName("nick")
    private String nick;
    @SerializedName("headImg")
    private String avatar;
    @SerializedName("isFriend")
    private boolean isFriend;
    @SerializedName("isAdmin")
    private boolean isAdmin;//是否官方账号

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public boolean isFriend() {
        return isFriend;
    }

    public void setIsFriend(boolean isFriend) {
        this.isFriend = isFriend;
    }
}

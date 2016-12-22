package com.ruihai.xingka.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lqfang on 15/10/8.
 */
public class MessageInfo {
    @SerializedName("account")
    private int account;
    @SerializedName("nick")
    private String nick;
    @SerializedName("headImg")
    private String avatar;
    @SerializedName("isFriend")
    private boolean isFriend;

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

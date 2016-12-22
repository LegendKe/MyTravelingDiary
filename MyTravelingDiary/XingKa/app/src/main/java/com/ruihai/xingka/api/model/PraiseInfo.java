package com.ruihai.xingka.api.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 *
 * Created by lqfang on 16/1/21.
 */
public class PraiseInfo implements Parcelable {
    @SerializedName("counter")
    private int counter;
    @SerializedName("userAccount")
    private int account;
    @SerializedName("imgSrc")
    private String avatar;
    @SerializedName("nick")
    private String nick;
    @SerializedName("remark")
    private String remark;
    @SerializedName("isFriend")
    private boolean isFriend;//关系 1是好友，2是关注，3是粉丝，如果查看的是自己的列表则是0
    @SerializedName("address")
    private String address;
    @SerializedName("isAdmin")
    private boolean isAdmin;//是否官方账号

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public boolean isFriend() {
        return isFriend;
    }

    public void setFriend(boolean friend) {
        isFriend = friend;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getAccount() {
        return account;
    }

    public void setAccount(int account) {
        this.account = account;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.counter);
        dest.writeInt(this.account);
        dest.writeString(this.avatar);
        dest.writeString(this.nick);
        dest.writeString(this.remark);
        dest.writeString(this.address);
        dest.writeByte(isAdmin ? (byte) 1 : (byte) 0);
    }

    public PraiseInfo() {
    }

    protected PraiseInfo(Parcel in) {
        this.counter = in.readInt();
        this.account = in.readInt();
        this.avatar = in.readString();
        this.nick = in.readString();
        this.remark = in.readString();
        this.address = in.readString();
        this.isAdmin = in.readByte() != 0;
    }

    public static final Parcelable.Creator<PraiseItem> CREATOR = new Parcelable.Creator<PraiseItem>() {
        public PraiseItem createFromParcel(Parcel source) {
            return new PraiseItem(source);
        }

        public PraiseItem[] newArray(int size) {
            return new PraiseItem[size];
        }
    };
}

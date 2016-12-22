package com.ruihai.xingka.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by apple on 15/9/11.
 */
public class RecommendInfo {

    @SerializedName("r_Account")  // 行账号
    private int account;
    @SerializedName("r_Nick")   //  昵称
    private String nickName;
    @SerializedName("r_Img")    //  头像
    private String img;
    @SerializedName("r_Phone")   // 手机号
    private String phone;
    @SerializedName("r_Tid")
    private String tid;
    @SerializedName("r_Isfriend")
    private boolean isfriend;
/*
"r_Account":121287,
"r_Nick":"大脚妹的章鱼哥",
"r_Img":"9fe39cee-2360-4414-8c9a-a020ae86a1d5",
"r_Phone":"18055152052",
"r_Tid":3,
"r_Isfriend":false
* */


    public boolean getIsfriend() {
        return isfriend;
    }

    public void setIsfriend(boolean isfriend) {
        this.isfriend = isfriend;
    }


    public int getAccount() {
        return account;
    }

    public void setAccount(int account) {
        this.account = account;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
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

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }
}

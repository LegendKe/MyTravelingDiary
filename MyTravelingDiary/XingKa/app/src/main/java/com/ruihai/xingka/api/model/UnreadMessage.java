package com.ruihai.xingka.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by zecker on 15/10/31.
 */
public class UnreadMessage {

    @SerializedName("PP_Guid")
    private String ppGuid;
    @SerializedName("P_Guid")
    private String pGuid;
    @SerializedName("PushType")
    private int pushType;
    @SerializedName("P_Img")
    private String pImg;
    @SerializedName("P_Content")
    private String pContent;
    @SerializedName("PC_Content")
    private String pcContent;
    @SerializedName("headImg")
    private String headImg;
    @SerializedName("nick")
    private String nick;
    @SerializedName("time")
    private String time;

    public String getPpGuid() {
        return ppGuid;
    }

    public void setPpGuid(String ppGuid) {
        this.ppGuid = ppGuid;
    }

    public String getpGuid() {
        return pGuid;
    }

    public void setpGuid(String pGuid) {
        this.pGuid = pGuid;
    }

    public int getPushType() {
        return pushType;
    }

    public void setPushType(int pushType) {
        this.pushType = pushType;
    }

    public String getpImg() {
        return pImg;
    }

    public void setpImg(String pImg) {
        this.pImg = pImg;
    }

    public String getpContent() {
        return pContent;
    }

    public void setpContent(String pContent) {
        this.pContent = pContent;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPcContent() {
        return pcContent;
    }

    public void setPcContent(String pcContent) {
        this.pcContent = pcContent;
    }
}

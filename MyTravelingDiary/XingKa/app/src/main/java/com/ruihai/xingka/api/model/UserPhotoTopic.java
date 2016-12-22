package com.ruihai.xingka.api.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by apple on 15/9/3.
 */
public class UserPhotoTopic implements Serializable {

    @SerializedName("P_Guid")
    private String pGuid;
    @SerializedName("readNum")
    private String readNum;
    @SerializedName("addTime")
    private String addTime;
    @SerializedName("imagesMessage")
    private List<ImagesMessage> imagesMessage;

    @SerializedName("type")
    private int type;
    @SerializedName("title")
    private String title;
    @SerializedName("color")
    private String color;
    @SerializedName("content")
    private String content;
    private String account;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getpGuid() {
        return pGuid;
    }

    public void setpGuid(String pGuid) {
        this.pGuid = pGuid;
    }

    public String getReadNum() {
        return readNum;
    }

    public void setReadNum(String readNum) {
        this.readNum = readNum;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public List<ImagesMessage> getImagesMessage() {
        return imagesMessage;
    }

    public void setImagesMessage(List<ImagesMessage> imagesMessage) {
        this.imagesMessage = imagesMessage;
    }
}

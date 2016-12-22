package com.ruihai.xingka.api.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mac on 16/5/19.
 */
public class MyTrackway implements Serializable {

    @SerializedName("T_Guid")
    private String tGuid;
    @SerializedName("readNum")
    private String readNum;
    @SerializedName("addTime")
    private String addTime;
    @SerializedName("imagesMessage")
    private List<ImagesMessage> imagesMessage;

    public String gettGuid() {
        return tGuid;
    }

    public void settGuid(String tGuid) {
        this.tGuid = tGuid;
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

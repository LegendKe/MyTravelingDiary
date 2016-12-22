package com.ruihai.xingka.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by zecker on 15/9/11.
 */
public class PhotoTopicListRepo extends XKRepo {
    @SerializedName("recordCount")
    private int recordCount;
    @SerializedName("mainMessage")
    private List<PhotoTopic> photoTopicList;

    public int getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
    }

    public List<PhotoTopic> getPhotoTopicList() {
        return photoTopicList;
    }

    public void setPhotoTopicList(List<PhotoTopic> photoTopicList) {
        this.photoTopicList = photoTopicList;
    }
}

package com.ruihai.xingka.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by apple on 15/9/3.
 */
public class UserPhotoTopicRepo extends XKRepo {

    @SerializedName("myPhotoTopicMessages")
    private List<UserPhotoTopic> userPhotoTopicList;
    @SerializedName("recordCount")
    private int recordCount;

    public int getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
    }

    public List<UserPhotoTopic> getUserPhotoTopicList() {
        return userPhotoTopicList;
    }

    public void setUserPhotoTopicList(List<UserPhotoTopic> userPhotoTopicList) {
        this.userPhotoTopicList = userPhotoTopicList;
    }
}

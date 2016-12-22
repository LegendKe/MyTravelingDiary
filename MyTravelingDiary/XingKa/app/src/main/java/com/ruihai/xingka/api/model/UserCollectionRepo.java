package com.ruihai.xingka.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by apple on 15/9/19.
 */
public class UserCollectionRepo extends XKRepo{

    @SerializedName("recordCount")
    private int recordCount;
    @SerializedName("myPhotoTopicCollectionMessages")
    private List<UserCollection> userCollectionList;

    public int getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
    }

    public List<UserCollection> getUserCollectionList() {
        return userCollectionList;
    }

    public void setUserCollectionList(List<UserCollection> userCollectionList) {
        this.userCollectionList = userCollectionList;
    }
}

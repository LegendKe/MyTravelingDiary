package com.ruihai.xingka.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by mac on 16/5/14.
 */
public class MyTrackwayRepo extends XKRepo{

    @SerializedName("myTravelTogetherMessages")
    private List<MyTrackway> userTrackwayInfoList;
    @SerializedName("recordCount")
    private int recordCount;

    public int getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
    }

    public List<MyTrackway> getUserTrackwayInfoList() {
        return userTrackwayInfoList;
    }

    public void setUserPhotoTopicList(List<MyTrackway> userTrackwayInfoList) {
        this.userTrackwayInfoList = userTrackwayInfoList;
    }
}

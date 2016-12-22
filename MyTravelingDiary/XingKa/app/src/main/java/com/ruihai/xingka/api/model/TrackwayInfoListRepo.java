package com.ruihai.xingka.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by mac on 16/5/5.
 */
public class TrackwayInfoListRepo extends XKRepo{
    @SerializedName("recordCount")
    private int recordCount;
    @SerializedName("mainMessage")
    private List<TrackwayInfo> trackwayInfoList;

    public int getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
    }

    public List<TrackwayInfo> getTrackwayInfoList() {
        return trackwayInfoList;
    }

    public void setTrackwayInfoList(List<TrackwayInfo> trackwayInfoList) {
        this.trackwayInfoList = trackwayInfoList;
    }
}

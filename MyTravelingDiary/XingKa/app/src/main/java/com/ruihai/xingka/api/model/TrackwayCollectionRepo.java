package com.ruihai.xingka.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by mac on 16/5/19.
 */
public class TrackwayCollectionRepo extends XKRepo{

    @SerializedName("recordCount")
    private int recordCount;
    @SerializedName("myTravelTogetherCollectionMessages")
    private List<TrackwayCollection> trackwayCollectList;

    public int getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
    }

    public List<TrackwayCollection> getTrackwayCollectionList() {
        return trackwayCollectList;
    }

    public void setTrackwayCollectionList(List<TrackwayCollection> trackwayCollectList) {
        this.trackwayCollectList = trackwayCollectList;
    }
}

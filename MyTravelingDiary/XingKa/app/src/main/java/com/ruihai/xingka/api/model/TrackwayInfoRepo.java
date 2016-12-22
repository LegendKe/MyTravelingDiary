package com.ruihai.xingka.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mac on 16/5/9.
 */
public class TrackwayInfoRepo extends XKRepo{

    @SerializedName("contentDetailMessage")
    private TrackwayInfo trackwayInfo;

    public TrackwayInfo getTrackwayInfo() {
        return trackwayInfo;
    }

    public void setTrackwayInfo(TrackwayInfo trackwayInfo) {
        this.trackwayInfo = trackwayInfo;
    }
}

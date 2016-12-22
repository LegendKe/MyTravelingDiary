package com.ruihai.xingka.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by gjzhang on 15/11/19.
 */
public class OfficialMessageRepo extends XKRepo {
    @SerializedName("recordCount")
    private int recordCount;
    @SerializedName("officialMessages")
    private List<OfficialMessageDetail> officialMessages;

    public int getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
    }

    public List<OfficialMessageDetail> getOfficialMessages() {
        return officialMessages;
    }

    public void setOfficialMessages(List<OfficialMessageDetail> officialMessages) {
        this.officialMessages = officialMessages;
    }
}

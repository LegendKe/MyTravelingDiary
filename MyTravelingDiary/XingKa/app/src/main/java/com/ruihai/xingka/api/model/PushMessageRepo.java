package com.ruihai.xingka.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by zecker on 15/10/29.
 */
public class PushMessageRepo extends XKRepo {
    @SerializedName("recordCount")
    private int recordCount;
    @SerializedName("PushMessages")
    private List<PushMessage> pushMessages;

    public int getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
    }

    public List<PushMessage> getPushMessages() {
        return pushMessages;
    }

    public void setPushMessages(List<PushMessage> pushMessages) {
        this.pushMessages = pushMessages;
    }
}

package com.ruihai.xingka.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by gjzhang on 16/1/29.
 */
public class UnReadMesageInfo extends XKRepo {
    @SerializedName("recordCount")//未读消息数量
    private int unRecordCount;

    public int getUnRecordCount() {
        return unRecordCount;
    }

    public void setUnRecordCount(int unRecordCount) {
        this.unRecordCount = unRecordCount;
    }
}

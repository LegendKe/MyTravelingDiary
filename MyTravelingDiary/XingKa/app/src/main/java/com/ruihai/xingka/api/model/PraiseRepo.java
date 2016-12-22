package com.ruihai.xingka.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by gjzhang on 16/1/4.
 */
public class PraiseRepo extends XKRepo {
    @SerializedName("recordCount")
    private int recordCount;

    @SerializedName("praiseMessage")
    private List<PraiseItem> praiseItemItemList;

    public int getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
    }

    public List<PraiseItem> getPraiseItemItemList() {
        return praiseItemItemList;
    }

    public void setPraiseItemItemList(List<PraiseItem> praiseItemItemList) {
        this.praiseItemItemList = praiseItemItemList;
    }
}

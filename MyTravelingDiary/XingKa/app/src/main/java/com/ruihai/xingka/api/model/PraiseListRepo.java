package com.ruihai.xingka.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by lqfang on 16/1/21.
 */
public class PraiseListRepo extends XKRepo{
    @SerializedName("recordCount")
    private int recordCount;

    @SerializedName("praiseMessage")
    private List<PraiseInfo> praiseInfos;

    public int getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
    }

    public List<PraiseInfo> getPraiseInfos() {
        return praiseInfos;
    }

    public void setPraiseInfos(List<PraiseInfo> praiseInfos) {
        this.praiseInfos = praiseInfos;
    }
}

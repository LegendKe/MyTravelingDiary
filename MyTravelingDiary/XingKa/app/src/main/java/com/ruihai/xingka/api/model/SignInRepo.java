package com.ruihai.xingka.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mac on 16/4/19.
 */
public class SignInRepo extends XKRepo {
    @SerializedName("continueDays")
    private int continueDays;//连续签到天数
    @SerializedName("totalIntegral")
    private int totalIntegral;//总积分
    @SerializedName("thisIntegral")
    private int thisIntegral;//当前签到积分

    public int getContinueDays() {
        return continueDays;
    }

    public void setContinueDays(int continueDays) {
        this.continueDays = continueDays;
    }

    public int getTotalIntegral() {
        return totalIntegral;
    }

    public void setTotalIntegral(int totalIntegral) {
        this.totalIntegral = totalIntegral;
    }

    public int getThisIntegral() {
        return thisIntegral;
    }

    public void setThisIntegral(int thisIntegral) {
        this.thisIntegral = thisIntegral;
    }
}

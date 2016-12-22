package com.ruihai.xingka.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mac on 16/5/19.
 */
public class TrackwayCollection {

    @SerializedName("imgNum")
    private int imgNum;
    @SerializedName("firstImg")
    private String firstImg;
    @SerializedName("T_Guid")
    private String tGuid;
    @SerializedName("authorAccount")
    private int authorAccount;

    public int getImgNum() {
        return imgNum;
    }

    public void setImgNum(int imgNum) {
        this.imgNum = imgNum;
    }

    public String getFirstImg() {
        return firstImg;
    }

    public void setFirstImg(String firstImg) {
        this.firstImg = firstImg;
    }

    public String gettGuid() {
        return tGuid;
    }

    public void settGuid(String tGuid) {
        this.tGuid = tGuid;
    }

    public int getAuthorAccount() {
        return authorAccount;
    }

    public void setAuthorAccount(int authorAccount) {
        this.authorAccount = authorAccount;
    }
}

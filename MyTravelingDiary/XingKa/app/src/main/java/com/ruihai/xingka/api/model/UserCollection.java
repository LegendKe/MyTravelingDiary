package com.ruihai.xingka.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by apple on 15/9/18.
 */
public class UserCollection {

    @SerializedName("imgNum")
    private int imgNum;
    @SerializedName("firstImg")
    private String firstImg;
    @SerializedName("P_Guid")
    private String pGuid;
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

    public String getpGuid() {
        return pGuid;
    }

    public void setpGuid(String pGuid) {
        this.pGuid = pGuid;
    }

    public int getAuthorAccount() {
        return authorAccount;
    }

    public void setAuthorAccount(int authorAccount) {
        this.authorAccount = authorAccount;
    }
}

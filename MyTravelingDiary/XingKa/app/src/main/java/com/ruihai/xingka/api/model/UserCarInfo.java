package com.ruihai.xingka.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by zecker on 15/11/2.
 */
public class UserCarInfo {
    @SerializedName("C_ImgKey")
    private String imgkey;
    @SerializedName("C_Name")
    private String name;

    public String getImgkey() {
        return imgkey;
    }

    public void setImgkey(String imgkey) {
        this.imgkey = imgkey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

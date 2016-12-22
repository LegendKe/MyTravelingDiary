package com.ruihai.xingka.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by apple on 15/9/5.
 */
public class UserTag {

    @SerializedName("M_Guid")
    private String guid;   // 标签id
    @SerializedName("M_Name")
    private String name; // 标签名称

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

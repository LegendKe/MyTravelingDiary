package com.ruihai.xingka.api.model;

import com.google.gson.annotations.SerializedName;
import com.orhanobut.hawk.Hawk;

import java.io.Serializable;
import java.util.List;

/**
 * Created by apple on 15/8/29.
 */
public class TagInfo implements Serializable{

    @SerializedName("T_Id")
    private int id;
    @SerializedName("T_Type")
    private String type;
    @SerializedName("T_Name")
    private String name;
    @SerializedName("T_ImgKey")
    private String imgKey;
    @SerializedName("T_Summary")
    private String summary;
    @SerializedName("T_Order")
    private String order;
    @SerializedName("T_FirstLetters")
    private String firstLetters;
    @SerializedName("T_AllLetter")
    private String allLetter;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgKey() {
        return imgKey;
    }

    public void setImgKey(String imgKey) {
        this.imgKey = imgKey;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getFirstLetters() {
        return firstLetters;
    }

    public void setFirstLetters(String firstLetters) {
        this.firstLetters = firstLetters;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getAllLetter() {
        return allLetter;
    }

    public void setAllLetter(String allLetter) {
        this.allLetter = allLetter;
    }

    // Cache
    public static int getCacheSysTagInfoVersion() {
        return Hawk.get("sysTagInfoVersion", 0);
    }

    public static void setCacheSysTagInfoVersion(int version) {
        Hawk.put("sysTagInfoVersion", version);
    }

    public static List<TagInfo> getCacheSysTagInfos() {
        return Hawk.get("sysTagInfos");
    }

    public static void setCacheSysTagInfos(List<TagInfo> tagInfos) {
        Hawk.put("sysTagInfos", tagInfos);
    }
}

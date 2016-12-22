package com.ruihai.xingka.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by zecker on 15/8/11.
 */
public class XKRepo {
    @SerializedName("success")
    private boolean success;
    @SerializedName("code")
    private int code;
    @SerializedName("msg")
    private String msg;
    @SerializedName("tagInfo")
    private List<TagInfo> tagInfo;
    @SerializedName("addInfo")
    private List<AddInfo> addInfo;
    @SerializedName("maybeInfo")
    private List<MaybeInfo> maybeInfo;
    @SerializedName("version")
    private int version;
    @SerializedName("recommendInfo")
    private List<RecommendInfo> recommendInfo;
    @SerializedName("returnGuid")
    private String returnGuid;
    @SerializedName("userSearchList")
    private List<SearchUser> searchUserList;
    @SerializedName("messageInfo")
    private List<MessageInfo> messageInfos;
    @SerializedName("type")
    private String type;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public List<MessageInfo> getMessageInfos() {
        return messageInfos;
    }

    public void setMessageInfos(List<MessageInfo> messageInfos) {
        this.messageInfos = messageInfos;
    }

    public List<RecommendInfo> getRecommendInfo() {
        return recommendInfo;
    }

    public void setRecommendInfo(List<RecommendInfo> recommendInfo) {
        this.recommendInfo = recommendInfo;
    }

    public List<MaybeInfo> getMaybeInfo() {
        return maybeInfo;
    }

    public void setMaybeInfo(List<MaybeInfo> maybeInfo) {
        this.maybeInfo = maybeInfo;
    }

    public List<AddInfo> getAddInfo() {
        return addInfo;
    }

    public void setAddInfo(List<AddInfo> addInfo) {
        this.addInfo = addInfo;
    }

    public List<TagInfo> getTagInfo() {
        return tagInfo;
    }

    public void setTagInfo(List<TagInfo> tagInfo) {
        this.tagInfo = tagInfo;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getReturnGuid() {
        return returnGuid;
    }

    public void setReturnGuid(String returnGuid) {
        this.returnGuid = returnGuid;
    }

    public List<SearchUser> getSearchUserList() {
        return searchUserList;
    }

    public void setSearchUserList(List<SearchUser> searchUserList) {
        this.searchUserList = searchUserList;
    }
}

package com.ruihai.xingka.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * 官方系统消息详情数据类
 * Created by mac on 15/11/19.
 */
public class OfficialMessageDetail {
    @SerializedName("id")
    private int id;//系统消息ID
    @SerializedName("title")
    private String title;//系统消息标题
    @SerializedName("type")
    private int type;//消息类型
    @SerializedName("url")
    private String url;//外部链接
    @SerializedName("P_Guid")
    private String captionId;//图说Guid
    @SerializedName("P_AuthorAccount")
    private String authorAccount;//图说作者行咖号
    @SerializedName("time")
    private String time;//系统消息发布时间

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setCaptionId(String captionId) {
        this.captionId = captionId;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    public String getCaptionId() {
        return captionId;
    }

    public String getTime() {
        return time;
    }

    public String getAuthorAccount() {
        return authorAccount;
    }

    public void setAuthorAccount(String authorAccount) {
        this.authorAccount = authorAccount;
    }
}

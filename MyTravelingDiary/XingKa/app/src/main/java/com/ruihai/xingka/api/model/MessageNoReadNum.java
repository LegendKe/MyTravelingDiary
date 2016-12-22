package com.ruihai.xingka.api.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mac on 16/4/6.
 */
public class MessageNoReadNum implements Serializable {

    @SerializedName("success")
    private boolean success;
    @SerializedName("code")
    private int code;
    @SerializedName("msg")
    private String msg;
    @SerializedName("atNum")
    private int atNum;
    @SerializedName("commentNum")
    private int commentNum;
    @SerializedName("praiseNum")
    private int praiseNum;
    @SerializedName("collectNum")
    private int collectNum;
    @SerializedName("focusNum")
    private int focusNum;
    @SerializedName("officialNum")
    private int officialNum;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getAtNum() {
        return atNum;
    }

    public void setAtNum(int atNum) {
        this.atNum = atNum;
    }

    public int getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(int commentNum) {
        this.commentNum = commentNum;
    }

    public int getPraiseNum() {
        return praiseNum;
    }

    public void setPraiseNum(int praiseNum) {
        this.praiseNum = praiseNum;
    }

    public int getCollectNum() {
        return collectNum;
    }

    public void setCollectNum(int collectNum) {
        this.collectNum = collectNum;
    }

    public int getFocusNum() {
        return focusNum;
    }

    public void setFocusNum(int focusNum) {
        this.focusNum = focusNum;
    }

    public int getOfficialNum() {
        return officialNum;
    }

    public void setOfficialNum(int officialNum) {
        this.officialNum = officialNum;
    }
}

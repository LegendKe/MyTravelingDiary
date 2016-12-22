package com.ruihai.xingka.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by zecker on 16/5/26.
 */

public class SMSStatus {

    @SerializedName("success")
    private boolean success; // 是否成功
    @SerializedName("bl")
    private String code;     // 状态代码
    @SerializedName("id")
    private String msg;      // 辅助信息


    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}

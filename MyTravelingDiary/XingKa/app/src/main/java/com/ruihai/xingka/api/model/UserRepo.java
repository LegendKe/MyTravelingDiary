package com.ruihai.xingka.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by mac on 15/10/29.
 */
public class UserRepo extends XKRepo {
    @SerializedName("userInfo")
    private User userInfo;
    @SerializedName("userTag")
    private List<UserTag> userTags;
    @SerializedName("carInfo")
    private UserCarInfo userCarInfo;

    public User getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(User userInfo) {
        this.userInfo = userInfo;
    }

    public List<UserTag> getUserTags() {
        return userTags;
    }

    public void setUserTags(List<UserTag> userTags) {
        this.userTags = userTags;
    }

    public UserCarInfo getUserCarInfo() {
        return userCarInfo;
    }

    public void setUserCarInfo(UserCarInfo userCarInfo) {
        this.userCarInfo = userCarInfo;
    }
}

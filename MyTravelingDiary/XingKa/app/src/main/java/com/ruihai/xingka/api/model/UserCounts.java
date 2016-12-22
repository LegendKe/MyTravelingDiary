package com.ruihai.xingka.api.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by apple on 15/9/9.
 */
public class UserCounts implements Serializable {

    @SerializedName("A_ID")
    private int id;
    @SerializedName("U_Account")
    private int account; // 行账号
    @SerializedName("A_FansNum")
    private int fansNum; // 粉丝数量
    @SerializedName("A_FriendsNum")
    private int friendsNum; // 关注数量
    @SerializedName("A_GoodFriendsNum")
    private int goodfriendNum;
    @SerializedName("A_CollectionNum")
    private int collectionNum; // 收藏数量
    @SerializedName("A_PhotoTopicNum")
    private int photoTopicNum; // 图说数量
    @SerializedName("A_GoInNum")
    private int goInNum; // 咖行数量
    @SerializedName("A_PraiseNum")
    private int praiseNum; // 被赞的数量
    @SerializedName("A_ThemeNum")
    private int themeNum; // 话题数量

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAccount() {
        return account;
    }

    public void setAccount(int account) {
        this.account = account;
    }

    public int getFansNum() {
        return fansNum;
    }

    public void setFansNum(int fansNum) {
        this.fansNum = fansNum;
    }

    public int getCollectionNum() {
        return collectionNum;
    }

    public void setCollectionNum(int collectionNum) {
        this.collectionNum = collectionNum;
    }

    public int getPhotoTopicNum() {
        return photoTopicNum;
    }

    public void setPhotoTopicNum(int photoTopicNum) {
        this.photoTopicNum = photoTopicNum;
    }

    public int getGoInNum() {
        return goInNum;
    }

    public void setGoInNum(int goInNum) {
        this.goInNum = goInNum;
    }

    public int getPraiseNum() {
        return praiseNum;
    }

    public void setPraiseNum(int praiseNum) {
        this.praiseNum = praiseNum;
    }

    public int getThemeNum() {
        return themeNum;
    }

    public void setThemeNum(int themeNum) {
        this.themeNum = themeNum;
    }

    public int getFriendsNum() {
        return friendsNum;
    }

    public void setFriendsNum(int friendsNum) {
        this.friendsNum = friendsNum;
    }

    public int getGoodfriendNum() {
        return goodfriendNum;
    }

    public void setGoodfriendNum(int goodfriendNum) {
        this.goodfriendNum = goodfriendNum;
    }
}

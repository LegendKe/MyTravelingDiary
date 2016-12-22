package com.ruihai.xingka.api.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 草稿箱数据模型类
 * Created by gjzhang on 16/1/5.
 */
public class DraftBoxItem implements Serializable {
    private int account;//用户行咖号
    private String title;//标题
    private ArrayList<MyFriendInfoRepo.MyFriendInfo> selectedFriends;//@的好友
    private ArrayList<String> mSelectedPath;//图片路径
    private String type;//类型:图说,咖行
    private long saveTime;//保存时间
    private int onlyVisible;//是否仅自己可见 0,所有人可见 1,自己可见
    private String address;//地理位置
    private double x;//经纬度
    private double y;

    public DraftBoxItem() {

    }


    public DraftBoxItem(int account, String title, ArrayList<MyFriendInfoRepo.MyFriendInfo> selectedFriends, ArrayList<String> mSelectedPath, String type, long saveTime, int onlyVisible, String address, double x, double y) {
        this.account = account;
        this.title = title;
        this.selectedFriends = selectedFriends;
        this.mSelectedPath = mSelectedPath;
        this.type = type;
        this.saveTime = saveTime;
        this.onlyVisible = onlyVisible;
        this.address = address;
        this.x = x;
        this.y = y;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public int getAccount() {
        return account;
    }

    public void setAccount(int account) {
        this.account = account;
    }

    public int getOnlyVisible() {
        return onlyVisible;
    }

    public void setOnlyVisible(int onlyVisible) {
        this.onlyVisible = onlyVisible;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<MyFriendInfoRepo.MyFriendInfo> getSelectedFriends() {
        return selectedFriends;
    }

    public void setSelectedFriends(ArrayList<MyFriendInfoRepo.MyFriendInfo> selectedFriends) {
        this.selectedFriends = selectedFriends;
    }

    public ArrayList<String> getmSelectedPath() {
        return mSelectedPath;
    }

    public void setmSelectedPath(ArrayList<String> mSelectedPath) {
        this.mSelectedPath = mSelectedPath;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getSaveTime() {
        return saveTime;
    }

    public void setSaveTime(long saveTime) {
        this.saveTime = saveTime;
    }
}

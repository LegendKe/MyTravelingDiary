package com.ruihai.xingka.ui.caption.publisher.bean;

import com.ruihai.android.network.http.Params;
import com.ruihai.xingka.api.model.CaptionInfo;
import com.ruihai.xingka.api.model.MyFriendInfoRepo;
import com.ruihai.xingka.api.model.TravelLineMoudle;
import com.ruihai.xingka.api.model.TravelTogetherImgMoudle;
import com.ruihai.xingka.api.model.TravelTogetherInfo;

import org.aisen.orm.annotation.PrimaryKey;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by zecker on 15/10/2.
 */
public class PublishBean implements Serializable {

    public enum PublishStatus {
        // 新建
        create,
        // 发布失败
        faild,
        // 草稿
        draft,
        // 正在发布
        sending,
        // 等待发布
        waiting
    }

    @PrimaryKey(column = "id")
    private String id = UUID.randomUUID().toString();
    private PublishStatus status;
    private PublishType type;
    private CaptionInfo captionInfo;
    private String[] pics;
    private String[] atUsers;
    private ArrayList<MyFriendInfoRepo.MyFriendInfo> selectedFriends;
    private String text;
    private long delay = 0;
    private long timing = 0;
    private String errorMsg;
    //经纬度
    private double x, y;
    //定位地址
    private String address;
    Params params;
    Params extras = new Params();

    private TravelTogetherInfo travelTogetherInfo;

    private String beginTime;//行程开始时间
    private String endTime;//行程结束时间
    private int costType;//费用类型
    private int personNum;//旅伴人数
    private String partnerContent;//旅伴要求
    private String content;//旅拼描述
    private String url;
    private ArrayList<TravelTogetherImgMoudle> imgModule;//旅拼图片
    private ArrayList<TravelLineMoudle> lineModule;//旅拼路线

    public TravelTogetherInfo getTravelTogetherInfo() {
        return travelTogetherInfo;
    }

    public void setTravelTogetherInfo(TravelTogetherInfo travelTogetherInfo) {
        this.travelTogetherInfo = travelTogetherInfo;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getCostType() {
        return costType;
    }

    public void setCostType(int costType) {
        this.costType = costType;
    }

    public int getPersonNum() {
        return personNum;
    }

    public void setPersonNum(int personNum) {
        this.personNum = personNum;
    }

    public String getPartnerContent() {
        return partnerContent;
    }

    public void setPartnerContent(String partnerContent) {
        this.partnerContent = partnerContent;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ArrayList<TravelTogetherImgMoudle> getImgModule() {
        return imgModule;
    }

    public void setImgModule(ArrayList<TravelTogetherImgMoudle> imgModule) {
        this.imgModule = imgModule;
    }

    public ArrayList<TravelLineMoudle> getLineModule() {
        return lineModule;
    }

    public void setLineModule(ArrayList<TravelLineMoudle> lineModule) {
        this.lineModule = lineModule;
    }

    public ArrayList<MyFriendInfoRepo.MyFriendInfo> getSelectedFriends() {
        return selectedFriends;
    }

    public void setSelectedFriends(ArrayList<MyFriendInfoRepo.MyFriendInfo> selectedFriends) {
        this.selectedFriends = selectedFriends;
    }

    public PublishStatus getStatus() {
        return status;
    }

    public void setStatus(PublishStatus status) {
        this.status = status;
    }

    public CaptionInfo getCaptionInfo() {
        return captionInfo;
    }

    public void setCaptionInfo(CaptionInfo captionInfo) {
        this.captionInfo = captionInfo;
    }

    public String[] getPics() {
        return pics;
    }

    public void setPics(String[] pics) {
        this.pics = pics;
    }

    public PublishType getType() {
        return type;
    }

    public void setType(PublishType type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTiming() {
        return timing;
    }

    public void setTiming(long timing) {
        this.timing = timing;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public Params getParams() {
        return params;
    }

    public void setParams(Params params) {
        this.params = params;
    }

    public Params getExtras() {
        return extras;
    }

    public void setExtras(Params extras) {
        this.extras = extras;
    }

    public String[] getAtUsers() {
        return atUsers;
    }

    public void setAtUsers(String[] atUsers) {
        this.atUsers = atUsers;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}

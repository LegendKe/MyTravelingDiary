package com.ruihai.xingka.api.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zecker on 15/9/9.
 */
public class PhotoTopic implements Parcelable {
    @SerializedName("P_Guid")
    private String pGuid;
    @SerializedName("img")
    private String avatar;
    @SerializedName("account")
    private int account;
    @SerializedName("nick")
    private String nick;
    @SerializedName("remark")
    private String remark;
    @SerializedName("addTime")
    private String addTime;
    @SerializedName("content")
    private String content;
    @SerializedName("address")
    private String address;
    @SerializedName("isPraise")
    private boolean isPraise;
    @SerializedName("isCollect")
    private boolean isCollect;
    @SerializedName("isFriend")
    private boolean isFriend;
    @SerializedName("praiseNum")
    private int praiseNum;//点赞数量
    @SerializedName("isAdmin")
    private boolean isOffical;//是否官方账号
    @SerializedName("url")
    private String webUrl;//官方图说的web页面地址

    private boolean hasMore;//图说内容是否展开
    private int detailHasMore;//详情页是否展开

    public boolean isHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    public int getDetailHasMore() {
        return detailHasMore;
    }

    public void setDetailHasMore(int detailHasMore) {
        this.detailHasMore = detailHasMore;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public boolean isOffical() {
        return isOffical;
    }

    public void setOffical(boolean offical) {
        isOffical = offical;
    }

    public int getPraiseNum() {
        return praiseNum;
    }

    public void setPraiseNum(int praiseNum) {
        this.praiseNum = praiseNum;
    }

    // 图说评论列表
    @SerializedName("commentMessage")
    private List<CommentItem> commentList;
    // 图片列表
    @SerializedName("imagesMessage")
    private List<ImageItem> imageList;
    // 点赞人员列表
    @SerializedName("praiseMessage")
    private List<PraiseItem> praiseList;

    public String getpGuid() {
        return pGuid;
    }

    public void setpGuid(String pGuid) {
        this.pGuid = pGuid;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getAccount() {
        return account;
    }

    public void setAccount(int account) {
        this.account = account;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getAddTime() {

        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isPraise() {
        return isPraise;
    }

    public void setIsPraise(boolean isPraise) {
        this.isPraise = isPraise;
    }

    public boolean isCollect() {
        return isCollect;
    }

    public void setIsCollect(boolean isCollect) {
        this.isCollect = isCollect;
    }

    public List<CommentItem> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<CommentItem> commentList) {
        this.commentList = commentList;
    }

    public List<ImageItem> getImageList() {
        return imageList;
    }

    public void setImageList(List<ImageItem> imageList) {
        this.imageList = imageList;
    }

    public List<PraiseItem> getPraiseList() {
        return praiseList;
    }

    public void setPraiseList(List<PraiseItem> praiseList) {
        this.praiseList = praiseList;
    }

    public boolean isFriend() {
        return isFriend;
    }

    public void setIsFriend(boolean isFriend) {
        this.isFriend = isFriend;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.praiseNum);
        dest.writeString(this.pGuid);
        dest.writeString(this.avatar);
        dest.writeInt(this.account);
        dest.writeString(this.nick);
        dest.writeString(this.remark);
        dest.writeString(this.addTime);
        dest.writeString(this.content);
        dest.writeString(this.webUrl);
        dest.writeString(this.address);
        dest.writeByte(isPraise ? (byte) 1 : (byte) 0);
        dest.writeByte(isCollect ? (byte) 1 : (byte) 0);
        dest.writeByte(isFriend ? (byte) 1 : (byte) 0);
        dest.writeByte(isOffical ? (byte) 1 : (byte) 0);
        dest.writeTypedList(commentList);
        dest.writeTypedList(imageList);
        dest.writeTypedList(praiseList);
    }

    public PhotoTopic() {

    }

    protected PhotoTopic(Parcel in) {
        this.praiseNum = in.readInt();
        this.pGuid = in.readString();
        this.avatar = in.readString();
        this.account = in.readInt();
        this.nick = in.readString();
        this.remark = in.readString();
        this.addTime = in.readString();
        this.content = in.readString();
        this.webUrl = in.readString();
        this.address = in.readString();
        this.isPraise = in.readByte() != 0;
        this.isCollect = in.readByte() != 0;
        this.isFriend = in.readByte() != 0;
        this.isOffical = in.readByte() != 0;
        this.commentList = in.createTypedArrayList(CommentItem.CREATOR);
        this.imageList = in.createTypedArrayList(ImageItem.CREATOR);
        this.praiseList = in.createTypedArrayList(PraiseItem.CREATOR);
    }

    public static final Creator<PhotoTopic> CREATOR = new Creator<PhotoTopic>() {
        public PhotoTopic createFromParcel(Parcel source) {
            return new PhotoTopic(source);
        }

        public PhotoTopic[] newArray(int size) {
            return new PhotoTopic[size];
        }
    };
}

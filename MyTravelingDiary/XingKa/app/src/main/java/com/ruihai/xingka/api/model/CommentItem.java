package com.ruihai.xingka.api.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.ruihai.xingka.Constant;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 评论列表对象
 **/
public class CommentItem implements Parcelable {
    // 评论GUID
    @SerializedName("guid")
    private String guid;
    // 评论用户ID
    @SerializedName("account")
    private String reviewUid;
    // 评论用户昵称
    @SerializedName("nick")
    private String reviewUName;
    // 评论用户备注
    @SerializedName("remark")
    private String reviewURemark;
    // 被回复GUID
    @SerializedName("toGuid")
    private String replyGuid;
    // 被回复用户ID 如果是0  就不是回复
    @SerializedName("toWho")
    private String replyUid;
    // 被回复用户昵称
    @SerializedName("toNick")
    private String replyUName;
    // 被回复用户备注
    @SerializedName("toRemark")
    private String replyURemark;
    // 评论内容
    @SerializedName("content")
    private String reviewContent;
    // 是否是回复
    @SerializedName("isReply")
    private boolean isReply;
    // 是否已点赞
    @SerializedName("isPraise")
    private boolean isPraise;
    // 点赞数
    @SerializedName("praiseNum")
    private int praiseNum;
    // 评论者头像
    @SerializedName("headimg")
    private String avatar;
    // 评论时间
    @SerializedName("addTime")
    private String addTime;
    // 评论回复
    @SerializedName("commentsMessage")
    private List<CommentItem> subCommentItems;
    @SerializedName("isAdmin")
    private boolean isAdmin;
    @SerializedName("toisAdmin")
    private boolean toisAdmin;

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public boolean isToisAdmin() {
        return toisAdmin;
    }

    public void setToisAdmin(boolean toisAdmin) {
        this.toisAdmin = toisAdmin;
    }

    public CommentItem() {

    }

    /**
     * 评论
     *
     * @param guid          评论GUID
     * @param reviewUid     评论者行账号
     * @param reviewUName   评论者昵称
     * @param avatar        评论者头像
     * @param reviewContent 评论内容
     */
    public CommentItem(String guid, String reviewUid, String reviewUName, String avatar, String reviewContent) {
        this.guid = guid;
        this.reviewUid = reviewUid;
        this.reviewUName = reviewUName;
        this.reviewURemark = "";
        this.replyGuid = Constant.DEFAULT_UUID;
        this.replyUid = "0";
        this.replyUName = "";
        this.replyURemark = "";
        this.reviewContent = reviewContent;
        this.isReply = false;
        this.isPraise = false;
        this.praiseNum = 0;
        this.avatar = avatar;
        this.subCommentItems = null;
        this.setAddTime(String.valueOf(System.currentTimeMillis()));
    }


    /**
     * 评论的回复/
     *
     * @param guid          评论GUID
     * @param reviewUid     评论者行账号
     * @param reviewUName   评论者昵称
     * @param avatar        评论者头像
     * @param reviewContent 评论内容
     */
    public CommentItem(String guid, String reviewUid, String reviewUName, String avatar, String reviewContent, String replyGuid, String toWho, String replyUName) {
        this.guid = guid;
        this.reviewUid = reviewUid;
        this.reviewUName = reviewUName;
        this.reviewURemark = "";
        this.replyGuid = replyGuid;
        this.replyUid = toWho;
        this.replyUName = replyUName;
        this.replyURemark = "";
        this.reviewContent = reviewContent;
        this.isReply = true;
        this.isPraise = false;
        this.praiseNum = 0;
        this.avatar = avatar;
        this.subCommentItems = null;
        this.setAddTime(String.valueOf(System.currentTimeMillis()));
    }


    public String getReviewUid() {
        return reviewUid;
    }

    public void setReviewUid(String reviewUid) {
        this.reviewUid = reviewUid;
    }

    public String getReviewUName() {
        return reviewUName;
    }

    public void setReviewUName(String reviewUName) {
        this.reviewUName = reviewUName;
    }

    public String getReplyUid() {
        return replyUid;
    }

    public void setReplyUid(String replyUid) {
        this.replyUid = replyUid;
    }

    public String getReplyUName() {
        return replyUName;
    }

    public void setReplyUName(String replyUName) {
        this.replyUName = replyUName;
    }

    public String getReviewContent() {
        return reviewContent;
    }

    public void setReviewContent(String reviewContent) {
        this.reviewContent = reviewContent;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getReviewURemark() {
        return reviewURemark;
    }

    public void setReviewURemark(String reviewURemark) {
        this.reviewURemark = reviewURemark;
    }

    public String getReplyGuid() {
        return replyGuid;
    }

    public void setReplyGuid(String replyGuid) {
        this.replyGuid = replyGuid;
    }

    public String getReplyURemark() {
        return replyURemark;
    }

    public void setReplyURemark(String replyURemark) {
        this.replyURemark = replyURemark;
    }

    public boolean isReply() {
        return isReply;
    }

    public void setIsReply(boolean isReply) {
        this.isReply = isReply;
    }

    public boolean isPraise() {
        return isPraise;
    }

    public void setIsPraise(boolean isPraise) {
        this.isPraise = isPraise;
    }

    public int getPraiseNum() {
        return praiseNum;
    }

    public void setPraiseNum(int praiseNum) {
        this.praiseNum = praiseNum;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public List<CommentItem> getSubCommentItems() {
        return subCommentItems;
    }

    public void setSubCommentItems(List<CommentItem> subCommentItems) {
        this.subCommentItems = subCommentItems;
    }

    public String getAddTime() {
        return addTime.substring(6, 19);
    }

    public void setAddTime(String addTime) {
        this.addTime = String.format("/Date(%s)/", addTime);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.guid);
        dest.writeString(this.reviewUid);
        dest.writeString(this.reviewUName);
        dest.writeString(this.reviewURemark);
        dest.writeString(this.replyGuid);
        dest.writeString(this.replyUid);
        dest.writeString(this.replyUName);
        dest.writeString(this.replyURemark);
        dest.writeString(this.reviewContent);
        dest.writeByte(isReply ? (byte) 1 : (byte) 0);
        dest.writeByte(isPraise ? (byte) 1 : (byte) 0);
        dest.writeInt(this.praiseNum);
        dest.writeString(this.avatar);
        dest.writeString(this.addTime);
    }

    protected CommentItem(Parcel in) {
        this.guid = in.readString();
        this.reviewUid = in.readString();
        this.reviewUName = in.readString();
        this.reviewURemark = in.readString();
        this.replyGuid = in.readString();
        this.replyUid = in.readString();
        this.replyUName = in.readString();
        this.replyURemark = in.readString();
        this.reviewContent = in.readString();
        this.isReply = in.readByte() != 0;
        this.isPraise = in.readByte() != 0;
        this.praiseNum = in.readInt();
        this.avatar = in.readString();
        this.addTime = in.readString();
    }

    public static final Parcelable.Creator<CommentItem> CREATOR = new Parcelable.Creator<CommentItem>() {
        public CommentItem createFromParcel(Parcel source) {
            return new CommentItem(source);
        }

        public CommentItem[] newArray(int size) {
            return new CommentItem[size];
        }
    };
}

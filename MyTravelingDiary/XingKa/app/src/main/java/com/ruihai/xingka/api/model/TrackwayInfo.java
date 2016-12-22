package com.ruihai.xingka.api.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by lqfang on 16/5/4.
 */
public class TrackwayInfo implements Parcelable {
    @SerializedName("T_Guid")
    private String tGuid;
    @SerializedName("beginDate")
    private String beginDate; //开始日期
    @SerializedName("endDate")
    private String endDate; //结束日期
    @SerializedName("costType")
    private String costType; //费用说明
    @SerializedName("personNum")
    private String personNum; //旅拼人数
    @SerializedName("partnerContent")
    private String partnerContent; //对旅伴的要求
    @SerializedName("img")
    private String avatar;
    @SerializedName("sex")
    private int sex;
    @SerializedName("account")
    private int account;  //旅拼作者
    @SerializedName("nick")
    private String nick;
    @SerializedName("remark")
    private String remark;
    @SerializedName("addTime")
    private String addTime;
    @SerializedName("title")
    private String title;
    @SerializedName("content")
    private String content; //旅拼描述
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
    private String webUrl;//官方旅拼的web页面地址

    private boolean hasMore;//旅拼内容是否展开
    private int detailHasMore;//详情页是否展开

    //活动路线信息
    @SerializedName("lineMessage")
    private List<LineInfo> lineInfoList;
    // 评论列表
    @SerializedName("commentMessage")
    private List<CommentItem> commentList;
    // 图片列表
    @SerializedName("imagesMessage")
    private List<ImageItem> imageList;
    // 点赞人员列表
    @SerializedName("praiseMessage")
    private List<PraiseItem> praiseList;


    public String gettGuid() {
        return tGuid;
    }

    public void settGuid(String tGuid) {
        this.tGuid = tGuid;
    }

    public String getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getCostType() {
        return costType;
    }

    public void setCostType(String costType) {
        this.costType = costType;
    }

    public String getPersonNum() {
        return personNum;
    }

    public void setPersonNum(String personNum) {
        this.personNum = personNum;
    }

    public String getPartnerContent() {
        return partnerContent;
    }

    public void setPartnerContent(String partnerContent) {
        this.partnerContent = partnerContent;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isPraise() {
        return isPraise;
    }

    public void setPraise(boolean praise) {
        isPraise = praise;
    }

    public boolean isCollect() {
        return isCollect;
    }

    public void setCollect(boolean collect) {
        isCollect = collect;
    }

    public boolean isFriend() {
        return isFriend;
    }

    public void setFriend(boolean friend) {
        isFriend = friend;
    }

    public int getPraiseNum() {
        return praiseNum;
    }

    public void setPraiseNum(int praiseNum) {
        this.praiseNum = praiseNum;
    }

    public boolean isOffical() {
        return isOffical;
    }

    public void setOffical(boolean offical) {
        isOffical = offical;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

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

    public List<LineInfo> getLineInfoList() {
        return lineInfoList;
    }

    public void setLineInfoList(List<LineInfo> lineInfoList) {
        this.lineInfoList = lineInfoList;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(tGuid);
        parcel.writeString(beginDate);
        parcel.writeString(endDate);
        parcel.writeString(costType);
        parcel.writeString(personNum);
        parcel.writeString(partnerContent);
        parcel.writeString(avatar);
        parcel.writeInt(account);
        parcel.writeInt(sex);
        parcel.writeString(nick);
        parcel.writeString(remark);
        parcel.writeString(addTime);
        parcel.writeString(title);
        parcel.writeString(content);
        parcel.writeByte((byte) (isPraise ? 1 : 0));
        parcel.writeByte((byte) (isCollect ? 1 : 0));
        parcel.writeByte((byte) (isFriend ? 1 : 0));
        parcel.writeInt(praiseNum);
        parcel.writeByte((byte) (isOffical ? 1 : 0));
        parcel.writeString(webUrl);
        parcel.writeByte((byte) (hasMore ? 1 : 0));
        parcel.writeInt(detailHasMore);
        parcel.writeTypedList(lineInfoList);
        parcel.writeTypedList(commentList);
        parcel.writeTypedList(imageList);
        parcel.writeTypedList(praiseList);
    }

    protected TrackwayInfo(Parcel in) {
        tGuid = in.readString();
        beginDate = in.readString();
        endDate = in.readString();
        costType = in.readString();
        personNum = in.readString();
        partnerContent = in.readString();
        avatar = in.readString();
        account = in.readInt();
        sex = in.readInt();
        nick = in.readString();
        remark = in.readString();
        addTime = in.readString();
        title = in.readString();
        content = in.readString();
        isPraise = in.readByte() != 0;
        isCollect = in.readByte() != 0;
        isFriend = in.readByte() != 0;
        praiseNum = in.readInt();
        isOffical = in.readByte() != 0;
        webUrl = in.readString();
        hasMore = in.readByte() != 0;
        detailHasMore = in.readInt();
        lineInfoList = in.createTypedArrayList(LineInfo.CREATOR);
        commentList = in.createTypedArrayList(CommentItem.CREATOR);
        imageList = in.createTypedArrayList(ImageItem.CREATOR);
        praiseList = in.createTypedArrayList(PraiseItem.CREATOR);
    }

    public static final Creator<TrackwayInfo> CREATOR = new Creator<TrackwayInfo>() {
        @Override
        public TrackwayInfo createFromParcel(Parcel in) {
            return new TrackwayInfo(in);
        }

        @Override
        public TrackwayInfo[] newArray(int size) {
            return new TrackwayInfo[size];
        }
    };

}

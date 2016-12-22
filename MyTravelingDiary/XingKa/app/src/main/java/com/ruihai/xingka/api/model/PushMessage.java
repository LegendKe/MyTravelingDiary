package com.ruihai.xingka.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by zecker on 15/10/29.
 */
public class PushMessage {
    @SerializedName("pp_Guid")
    private String ppGuid;
    @SerializedName("pushType")
    private int pushType; // 消息类型，0表示全部；1：发布图说@；2：评论图说@；3：评论图说；4：评论图说下的评论；5：图说点赞；6：图说评论点赞；7：图说收藏；8：关注。
    @SerializedName("sender")
    private String sender;
    @SerializedName("receiver")
    private String receiver;
    @SerializedName("p_Guid")
    private String pGuid;
    @SerializedName("authorAccount")
    private String authorAccount;
    @SerializedName("pc_Guid")
    private String pcGuid;
    @SerializedName("senderNick")
    private String senderNick;
    @SerializedName("senderHeadImg")
    private String senderAvatar;
    @SerializedName("p_Img")
    private String pImage;
    @SerializedName("p_Content")
    private String pContent;
    @SerializedName("pc_Content")
    private String pcContent;
    @SerializedName("isFriend")
    private boolean isFriend;
    @SerializedName("msgContent")
    private String msgContent;
    @SerializedName("sendTime")
    private String sendTime;
    @SerializedName("isSee")
    private boolean isSee;
    @SerializedName("isAdmin")
    private boolean isAdmin;

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getPpGuid() {
        return ppGuid;
    }

    public void setPpGuid(String ppGuid) {
        this.ppGuid = ppGuid;
    }

    public int getPushType() {
        return pushType;
    }

    public void setPushType(int pushType) {
        this.pushType = pushType;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getpGuid() {
        return pGuid;
    }

    public void setpGuid(String pGuid) {
        this.pGuid = pGuid;
    }

    public String getPcGuid() {
        return pcGuid;
    }

    public void setPcGuid(String pcGuid) {
        this.pcGuid = pcGuid;
    }

    public String getSenderNick() {
        return senderNick;
    }

    public void setSenderNick(String senderNick) {
        this.senderNick = senderNick;
    }

    public String getSenderAvatar() {
        return senderAvatar;
    }

    public void setSenderAvatar(String senderAvatar) {
        this.senderAvatar = senderAvatar;
    }

    public String getpImage() {
        return pImage;
    }

    public void setpImage(String pImage) {
        this.pImage = pImage;
    }

    public String getpContent() {
        return pContent;
    }

    public void setpContent(String pContent) {
        this.pContent = pContent;
    }

    public String getPcContent() {
        return pcContent;
    }

    public void setPcContent(String pcContent) {
        this.pcContent = pcContent;
    }

    public boolean isFriend() {
        return isFriend;
    }

    public void setIsFriend(boolean isFriend) {
        this.isFriend = isFriend;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public boolean isSee() {
        return isSee;
    }

    public void setIsSee(boolean isSee) {
        this.isSee = isSee;
    }

    public String getAuthorAccount() {
        return authorAccount;
    }

    public void setAuthorAccount(String authorAccount) {
        this.authorAccount = authorAccount;
    }
}

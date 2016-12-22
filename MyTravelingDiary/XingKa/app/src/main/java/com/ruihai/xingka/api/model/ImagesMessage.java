package com.ruihai.xingka.api.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by apple on 15/9/15.
 */
public class ImagesMessage implements Serializable {

    @SerializedName("imgSrc")
    private String imgSrc;
    @SerializedName("moodMessage")
    private MoodMessage moodMessage;
    @SerializedName("brandMessage")
    private String brandMessage;
    @SerializedName("addressMessage")
    private String addressMessage;


    public String getImgSrc() {
        return imgSrc;
    }

    public void setImgSrc(String imgSrc) {
        this.imgSrc = imgSrc;
    }

    public MoodMessage getMoodMessage() {
        return moodMessage;
    }

    public void setMoodMessage(MoodMessage moodMessage) {
        this.moodMessage = moodMessage;
    }

    public String getBrandMessage() {
        return brandMessage;
    }

    public void setBrandMessage(String brandMessage) {
        this.brandMessage = brandMessage;
    }

    public String getAddressMessage() {
        return addressMessage;
    }

    public void setAddressMessage(String addressMessage) {
        this.addressMessage = addressMessage;
    }

    public class MoodMessage implements Serializable {

        @SerializedName("imgSrc")
        private String account;
        @SerializedName("moodMessage")
        private String nick;

        public String getAccount() {
            return account;
        }

        public void setAccount(String account) {
            this.account = account;
        }

        public String getNick() {
            return nick;
        }

        public void setNick(String nick) {
            this.nick = nick;
        }
    }
}

package com.ruihai.xingka.api.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zecker on 16/1/16.
 */
public class ShareInfo implements Parcelable {
    private String title;
    private String content;
    private String imageUrl;
    private String targetUrl;

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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.content);
        dest.writeString(this.imageUrl);
        dest.writeString(this.targetUrl);
    }

    public ShareInfo() {
    }

    protected ShareInfo(Parcel in) {
        this.title = in.readString();
        this.content = in.readString();
        this.imageUrl = in.readString();
        this.targetUrl = in.readString();
    }

    public static final Parcelable.Creator<ShareInfo> CREATOR = new Parcelable.Creator<ShareInfo>() {
        public ShareInfo createFromParcel(Parcel source) {
            return new ShareInfo(source);
        }

        public ShareInfo[] newArray(int size) {
            return new ShareInfo[size];
        }
    };

    @Override
    public String toString() {
        return "ShareInfo: " + title + " / " + content + " / " + imageUrl +" / " + targetUrl;
    }
}

package com.ruihai.xingka.api.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by zecker on 15/9/24.
 */
public class ImageItem implements Parcelable {

    // 图片GUID
    @SerializedName("imgSrc")
    public String imgSrc;
    // 心情标签，为空则输入null
    @SerializedName("moodMessage")
    public ImageTag moodTag;
    // 品牌标签，为空则输入null
    @SerializedName("brandMessage")
    public ImageTag brandTag;
    // 地理标签，为空则输入null
    @SerializedName("addressMessage")
    public ImageTag addressTag;

    public class ImageTag implements Parcelable {
        // 标签文字
        public String tagName;
        public String x;
        public String y;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.tagName);
            dest.writeString(this.x);
            dest.writeString(this.y);
        }

        public ImageTag() {
        }

        protected ImageTag(Parcel in) {
            this.tagName = in.readString();
            this.x = in.readString();
            this.y = in.readString();
        }


        public Creator<ImageTag> CREATOR = new Creator<ImageTag>() {
            public ImageTag createFromParcel(Parcel source) {
                ImageTag imageTag = new ImageTag(source);
                return imageTag;
            }

            public ImageTag[] newArray(int size) {
                return new ImageTag[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.imgSrc);
        dest.writeParcelable(this.moodTag, 0);
        dest.writeParcelable(this.brandTag, 0);
        dest.writeParcelable(this.addressTag, 0);
    }

    public ImageItem() {
    }

    protected ImageItem(Parcel in) {
        this.imgSrc = in.readString();
        this.moodTag = in.readParcelable(ImageTag.class.getClassLoader());
        this.brandTag = in.readParcelable(ImageTag.class.getClassLoader());
        this.addressTag = in.readParcelable(ImageTag.class.getClassLoader());
    }

    public static final Creator<ImageItem> CREATOR = new Creator<ImageItem>() {
        public ImageItem createFromParcel(Parcel source) {
            return new ImageItem(source);
        }

        public ImageItem[] newArray(int size) {
            return new ImageItem[size];
        }
    };
}

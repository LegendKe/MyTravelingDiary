package com.ruihai.xingka.api.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mac on 16/5/4.
 */
public class LineInfo implements Parcelable{
    @SerializedName("x")
    private double x;
    @SerializedName("y")
    private double y;
    @SerializedName("address")
    private String address;


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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(x);
        parcel.writeDouble(y);
        parcel.writeString(address);
    }

    protected LineInfo(Parcel in) {
        x = in.readDouble();
        y = in.readDouble();
        address = in.readString();
    }

    public static final Creator<LineInfo> CREATOR = new Creator<LineInfo>() {
        @Override
        public LineInfo createFromParcel(Parcel in) {
            return new LineInfo(in);
        }

        @Override
        public LineInfo[] newArray(int size) {
            return new LineInfo[size];
        }
    };
}

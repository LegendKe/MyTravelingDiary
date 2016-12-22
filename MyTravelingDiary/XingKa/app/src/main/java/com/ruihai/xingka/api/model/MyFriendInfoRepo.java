package com.ruihai.xingka.api.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.orhanobut.hawk.Hawk;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zecker on 15/9/14.
 */
public class MyFriendInfoRepo extends XKRepo {
    @SerializedName("recordCount")
    private int recordCount;
    @SerializedName("myFriendInfo")
    private List<MyFriendInfo> myFriendInfoList;

    public List<MyFriendInfo> getMyFriendInfoList() {
        return myFriendInfoList;
    }

    public void setMyFriendInfoList(List<MyFriendInfo> myFriendInfoList) {
        this.myFriendInfoList = myFriendInfoList;
    }

    public int getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
    }

    public static class MyFriendInfo implements Parcelable, Serializable {

        //
//        myMessage":[
//        {
//            "P_Guid":"b4cb0890-0a2d-4546-bb53-1ea910bf55ee",
//                "readNum":26,
//                "addTime":"\/Date(1447327193653)\/",
//                "imagesMessage":[
//            {
//                "imgSrc":"e9df798f-e7dd-40b7-a232-47878a4a0dbe",
//                    "moodMessage":null,
//                    "brandMessage":null,
//                    "addressMessage":null
//            }
//            ]
//        }
//        ]
//@SerializedName("myMessage")
//private List
        @SerializedName("f_Account")
        private int account; //行帐号
        @SerializedName("f_Nick")
        private String nick; //昵称
        @SerializedName("f_Remark")
        private String remark; //备注
        @SerializedName("f_Img")
        private String avatar; //头像
        @SerializedName("f_Address")
        private String address; //地址
        @SerializedName("f_Nexus")
        private int isFriend; //关系 1是好友，2是关注，3是粉丝，如果查看的是自己的列表则是0
        @SerializedName("isEach")
        private boolean isEach;
        @SerializedName("isAdmin")
        private boolean isAdmin;

        public boolean isAdmin() {
            return isAdmin;
        }

        public void setAdmin(boolean admin) {
            isAdmin = admin;
        }

        private boolean isSelected;

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

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public int isFriend() {
            return isFriend;
        }

        public void setIsFriend(int isFriend) {
            this.isFriend = isFriend;
        }

        public boolean isEach() {return isEach;}

        public void setEach(boolean each) {isEach = each;}

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean isSelected) {
            this.isSelected = isSelected;
        }

        public int getIsFriend() {
            return isFriend;
        }

        @Override
        public int describeContents() {
            return 0;
        }


        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.account);
            dest.writeString(this.nick);
            dest.writeString(this.remark);
            dest.writeString(this.avatar);
            dest.writeString(this.address);
            dest.writeInt(this.isFriend);
            dest.writeByte(isSelected ? (byte) 1 : (byte) 0);
            dest.writeByte(isAdmin ? (byte) 1 : (byte) 0);
        }

        public MyFriendInfo() {

        }

        protected MyFriendInfo(Parcel in) {
            this.account = in.readInt();
            this.nick = in.readString();
            this.remark = in.readString();
            this.avatar = in.readString();
            this.address = in.readString();
            this.isFriend = in.readInt();
            this.isSelected = in.readByte() != 0;
            this.isAdmin = in.readByte() != 0;
        }

        public static final Creator<MyFriendInfo> CREATOR = new Creator<MyFriendInfo>() {
            public MyFriendInfo createFromParcel(Parcel source) {
                return new MyFriendInfo(source);
            }

            public MyFriendInfo[] newArray(int size) {
                return new MyFriendInfo[size];
            }
        };
    }

}

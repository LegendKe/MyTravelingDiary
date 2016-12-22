package com.ruihai.xingka.api.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by gjzhang on 15/12/12.
 */
public class BannerInfoRepo extends XKRepo {
    @SerializedName("bannerMessages")
    private List<BannerInfo> bannerInfoList;

    public List<BannerInfo> getBannerInfoList() {
        return bannerInfoList;
    }

    public void setBannerInfoList(List<BannerInfo> bannerInfoList) {
        this.bannerInfoList = bannerInfoList;
    }

    public class BannerInfo implements Serializable {
        /* {
"id":5,
"type":1,
"bclass":1,
"platform":1,
"begintime":"\/Date(1449590400000)\/",
"endtime":"\/Date(1451491200000)\/",
"title":"标题",
"description":"内容描述",
"content1":"http://www.xingka.cc",
"content2":"http://www.xingka.cc",
"img":"62015922-aa7a-4e07-9a92-a742e9cf874b",
"thumbnailImg":"62015922-aa7a-4e07-9a92-a742e9cf874b"
}*/
        @SerializedName("id")
        private int id;
        @SerializedName("type")
        private int type;//大的类型 1:活动 2.广告
        @SerializedName("platform")
        private int platform;//平台类型 1:手机 2.PC 9.所有平台
        @SerializedName("bclass")
        private int bclass;//判定内容类型 1:外部链接 2.图说GUID
        @SerializedName("title")
        private String title;//标题
        @SerializedName("content1")
        private String content1;//内容(链接或GUID)
        @SerializedName("content2")
        private String content2;//分享的内容或者图说发布者行咖号
        @SerializedName("description")
        private String description;//分享内容描述
        @SerializedName("img")
        private String img;//图片GUID
        @SerializedName("thumbnailImg")
        private String thumbnailImg;//缩略图GUID
        @SerializedName("begintime")
        private String begintime;
        @SerializedName("endtime")
        private String endtime;
        @SerializedName("isShareSelf")
        private boolean isShareSelf;//是否分享自身链接

        public boolean isShareSelf() {
            return isShareSelf;
        }

        public void setShareSelf(boolean shareSelf) {
            isShareSelf = shareSelf;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getPlatform() {
            return platform;
        }

        public void setPlatform(int platform) {
            this.platform = platform;
        }

        public int getBclass() {
            return bclass;
        }

        public void setBclass(int bclass) {
            this.bclass = bclass;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent1() {
            return content1;
        }

        public void setContent1(String content1) {
            this.content1 = content1;
        }

        public String getContent2() {
            return content2;
        }

        public void setContent2(String content2) {
            this.content2 = content2;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }

        public String getThumbnailImg() {
            return thumbnailImg;
        }

        public void setThumbnailImg(String thumbnailImg) {
            this.thumbnailImg = thumbnailImg;
        }

        public String getEndtime() {
            return endtime;
        }

        public void setEndtime(String endtime) {
            this.endtime = endtime;
        }

        public String getBegintime() {
            return begintime;
        }

        public void setBegintime(String begintime) {
            this.begintime = begintime;
        }
    }
}

package com.ruihai.xingka.api.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mac on 16/7/12.
 */
public class OfficialPhotoTopicTypeRepo extends XKRepo {
    private List<OfficialPhotoTopicType> listMessage;

    public List<OfficialPhotoTopicType> getListMessage() {
        return listMessage;
    }

    public void setListMessage(List<OfficialPhotoTopicType> listMessage) {
        this.listMessage = listMessage;
    }

    public class OfficialPhotoTopicType implements Serializable {
        //{"type":1,"title":"行咖FM","descrip":"官方实时动态与公告","color":"#95D867","icon":"图标"}
        private int type;
        private String title;
        private String descrip;
        private String color;
        private String icon;
        private String cover;
        private String sharecode;

        public String getSharecode() {
            return sharecode;
        }

        public void setSharecode(String sharecode) {
            this.sharecode = sharecode;
        }

        public String getCover() {
            return cover;
        }

        public void setCover(String cover) {
            this.cover = cover;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescrip() {
            return descrip;
        }

        public void setDescrip(String descrip) {
            this.descrip = descrip;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }
    }
}

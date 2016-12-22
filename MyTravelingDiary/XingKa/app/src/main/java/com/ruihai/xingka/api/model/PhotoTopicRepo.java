package com.ruihai.xingka.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by zecker on 15/10/22.
 */
public class PhotoTopicRepo extends XKRepo {

    @SerializedName("contentDetailMessage")
    private PhotoTopic photoTopic;

    public PhotoTopic getPhotoTopic() {
        return photoTopic;
    }

    public void setPhotoTopic(PhotoTopic photoTopic) {
        this.photoTopic = photoTopic;
    }
}

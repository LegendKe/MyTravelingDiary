package com.ruihai.xingka.api.model;

/**
 * Created by zecker on 15/9/28.
 */
public class ImageModule {

    public String imgSrc;
    public ImageTag moodTag;
    public ImageTag brandTag;
    public ImageTag addressTag;

    public class ImageTag {
        // 标签文字
        public String tagName;
        public String x;
        public String y;
    }

}

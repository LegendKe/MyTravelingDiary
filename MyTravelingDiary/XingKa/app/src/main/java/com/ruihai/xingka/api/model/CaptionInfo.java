package com.ruihai.xingka.api.model;

import java.util.List;

/**
 * 发布图说封装类
 * <p>
 * Created by zecker on 15/9/18.
 */
public class CaptionInfo {
    // 图说内容
    public String content;
    //经纬度
    public double x, y;
    //定位地址
    public String address;
    // 仅自己可见，0-否，1-是
    public int isHidden;
    // 图说图片
    public List<ImageModule> imgModule;
    // @谁
    public List<AtUser> pushModule;
    // 发布人
    public int account;

    public class AtUser {
        public int account;
    }
}

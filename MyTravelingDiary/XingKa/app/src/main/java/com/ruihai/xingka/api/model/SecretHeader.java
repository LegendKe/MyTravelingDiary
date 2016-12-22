package com.ruihai.xingka.api.model;

import com.ruihai.xingka.api.ApiModule;

/**
 * 加密请求头
 * Created by gjzhang on 16/3/7.
 */
public class SecretHeader {
    private String timer;
    private String guid;

    public SecretHeader(String timer, String guid) {
        this.timer = timer;
        this.guid = guid;
    }
}

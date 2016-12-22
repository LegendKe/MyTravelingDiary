package com.ruihai.xingka.base.htmltext;

import android.os.Parcel;
import android.text.style.URLSpan;

/**
 * 用来解析 url 以跳转到不同的界面
 * Created by zecker on 15/10/17.
 */
public class URLSpanNoUnderline extends URLSpan {
    private int color;

    public URLSpanNoUnderline(String url, int color) {
        super(url);
        this.color = color;
    }

    public URLSpanNoUnderline(String url) {
        super(url);
    }

    public URLSpanNoUnderline(Parcel src) {
        super(src);
    }
}

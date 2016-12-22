package com.ruihai.xingka.ui.common.enter;

import android.graphics.drawable.Drawable;

import com.ruihai.xingka.XKApplication;

public class DrawableTool {
    public static void zoomDrwable(Drawable drawable, boolean isMonkey) {
        int width = isMonkey ? XKApplication.sEmojiMonkey : XKApplication.sEmojiNormal;
        drawable.setBounds(0, 0, width, width);
    }
}

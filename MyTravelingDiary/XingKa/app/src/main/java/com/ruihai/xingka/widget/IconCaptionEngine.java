package com.ruihai.xingka.widget;

import android.graphics.Typeface;

import com.ruihai.iconicfontengine.IconicFontEngine;
import com.ruihai.iconicfontengine.IconicFontMap;

/**
 * Created by zecker on 15/11/17.
 */
public class IconCaptionEngine extends IconicFontEngine {

    public final static IconicFontMap FONT_MAP = new IconicFontMap() {{
        put("xk-praise-normal", '\ue900');
        put("xk-praise-selected", '\ue904');
        put("xk-collect-normal", '\ue901');
        put("xk-collect-selected", '\ue905');
        put("xk-comment-normal", '\ue902');
        put("xk-comment-pressed", '\ue906');
        put("xk-share-new", '\ue903');
        put("xk-praise-more", '\ue907');
    }};

    public IconCaptionEngine(Typeface typeface) {
        super(typeface, FONT_MAP);
    }
}

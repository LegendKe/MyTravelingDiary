package com.ruihai.xingka.widget;

import android.graphics.Typeface;

import com.ruihai.iconicfontengine.IconicFontEngine;
import com.ruihai.iconicfontengine.IconicFontMap;

public class IconCommonEngine extends IconicFontEngine {
    public final static IconicFontMap FONT_MAP = new IconicFontMap() {{
        put("xk-a-tail", '\ue920');
        put("xk-next", '\ue92b');
        put("xk-back", '\ue91a');
        put("xk-right", '\ue91e');
        put("xk-add-friend", '\ue97e');
        put("xk-crop", '\ue93d');
        put("xk-tag", '\ue92d');
        put("xk-close", '\ue91f');
        put("xk-trash", '\ue92e');
        put("xk-add", '\ue96b');
        put("xk-crop-9_16", '\ue93e');
        put("xk-crop-3_4", '\ue93f');
        put("xk-crop-1_1", '\ue940');
        put("xk-crop-4_3", '\ue942');
        put("xk-crop-16_9", '\ue941');
        put("xk-rotate-90", '\ue90e');
        put("xk-praise", '\ue970');
        put("xk-praise-selected", '\ue971');
        put("xk-collect", '\ue972');
        put("xk-collect-selected", '\ue973');
        put("xk-comment", '\ue974');
        put("xk-attent", '\ue91c');
        put("xk-share", '\ue975');
        put("xk-female", '\ue951');
        put("xk-male", '\ue950');
        put("xk-setting", '\ue92c');
        put("xk-report", '\ue918');
        put("xk-edit", '\ue92f');
        put("xk-location", '\ue944');
        put("xk-head-portrait", '\ue97e');
        put("xk-collection", '\ue96c');
        put("xk-eye", '\ue946');
        put("xk-message", '\ue91d');
        put("xk-card", '\ue934');
        put("xk-set-back", '\ue97d');
        put("xk-remark", '\ue96c');
        put("xk-change", '\ue90f');
        put("xk-mydata", '\ue97c');
        put("xk-draftbox", '\ue97b');
        put("xk-feedback", '\ue97a');
        put("xk-mycaption", '\ue976');
        put("xk-addfriend", '\ue97e');
        put("xk—sign-in", '\ue97f');
        put("xk-integral", '\ue977');
        put("xk-praise-more", '\ue96f');
        put("xk-destination", '\ue953');
        put("xk-traveltogether_title", '\ue981');
        put("xk-traveltogether-time", '\ue982');
        put("xk-traveltogether-route", '\ue983');
        put("xk-traveltogether-charge", '\ue984');
        put("xk-traveltogether-number", '\ue985');
        put("xk-traveltogether-require", '\ue986');
    }};

    public IconCommonEngine(Typeface typeface) {
        super(typeface, FONT_MAP);
    }
}

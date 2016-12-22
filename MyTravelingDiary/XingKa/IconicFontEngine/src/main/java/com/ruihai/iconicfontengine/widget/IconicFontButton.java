package com.ruihai.iconicfontengine.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.TextView;

import com.ruihai.iconicfontengine.IconicFontEngine;

import java.util.ArrayList;

public class IconicFontButton extends Button {
    private ArrayList<IconicFontEngine> iconicFontEngines;
    public IconicFontButton(Context context) {
        super(context);
    }
    public IconicFontButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public IconicFontButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public IconicFontButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(IconicFontEngine.apply(text), BufferType.SPANNABLE);
    }

    public void setTextWithEngines(CharSequence text, BufferType type, ArrayList<IconicFontEngine> engines) {
        super.setText(IconicFontEngine.apply(text, engines), BufferType.SPANNABLE);
    }
}

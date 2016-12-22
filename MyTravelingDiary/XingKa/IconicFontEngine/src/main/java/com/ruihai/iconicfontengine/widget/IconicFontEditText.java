package com.ruihai.iconicfontengine.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;


import com.ruihai.iconicfontengine.IconicFontEngine;

import java.util.ArrayList;

public class IconicFontEditText extends EditText {
    public IconicFontEditText(Context context) {
        super(context);
    }

    public IconicFontEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IconicFontEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(IconicFontEngine.apply(text), type);
    }

    public void setText(CharSequence text, BufferType type, ArrayList<IconicFontEngine> engines) {
        super.setText(IconicFontEngine.apply(text, engines), type);
    }

    public void setHintWithEngines(CharSequence hint) {
        super.setHint(IconicFontEngine.apply(hint));
    }

    public void setHintWithEngines(CharSequence hint, ArrayList<IconicFontEngine> engines) {
        super.setHint(IconicFontEngine.apply(hint, engines));
    }
}

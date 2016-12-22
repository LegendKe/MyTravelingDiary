package com.ruihai.xingka.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ruihai.iconicfontengine.widget.IconicFontTextView;
import com.ruihai.xingka.R;
import com.ruihai.xingka.utils.AppUtility;

/**
 * Created by mac on 16/6/6.
 */
public class MoreContentPopWindow extends PopupWindow {
    private Context context;
    private LayoutInflater inflater;
    private TextView titleTv;
    private ScrollView contentScrollView;
    private TextView contentTv;
    private IconicFontTextView backTv;


    public MoreContentPopWindow(Context context) {
        this.context = context;
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        int mScreenHeight = AppUtility.getScreenHeight();
        this.setHeight((int) (3.0F * (mScreenHeight / 4.0F)));
        //this.setBackgroundDrawable(new ColorDrawable(0xb0000000));// 这样设置才能点击屏幕外dismiss窗口
        this.setOutsideTouchable(true);
        this.setFocusable(true);
        this.setAnimationStyle(R.style.timepopwindow_anim_style);
        inflater = LayoutInflater.from(context);
        View contentView = inflater.inflate(R.layout.layout_show_more_content, null);

        titleTv = (TextView) contentView.findViewById(R.id.title_tv);
        contentScrollView = (ScrollView) contentView.findViewById(R.id.content_scrollview);
        backTv = (IconicFontTextView) contentView.findViewById(R.id.back_tv);
        contentTv = (TextView) contentView.findViewById(R.id.content_tv);
        backTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                return;
            }
        });
        setContentView(contentView);
    }

    public void setTitle(CharSequence title) {
        titleTv.setText(title);
    }

    public void setContent(CharSequence content) {
        contentTv.setText(content);
    }


}


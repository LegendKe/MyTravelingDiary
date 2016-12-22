package com.ruihai.xingka.event;

import android.view.View;

import java.util.Calendar;

/**
 * 处理快速在某个控件上双击2次(或多次)会导致onClick被触发2次(或多次)的问题
 * 通过判断2次click事件的时间间隔进行过滤
 * <p>
 * 子类通过实现{@link #onSingleClick}响应click事件
 * <p>
 * Created by zecker on 15/10/31.
 */
public abstract class OnSingleClickListener implements View.OnClickListener {
    /**
     * 最短click事件的时间间隔
     */
    private static final long MIN_CLICK_INTERVAL = 1 * 1000;
    /**
     * 上次click的时间
     */
    private long mLastClickTime = 0;

    /**
     * click响应函数
     *
     * @param v The view that was clicked.
     */
    public abstract void onSingleClick(View v);

    @Override
    public final void onClick(View v) {
        // 有可能2次连击，也有可能3连击，保证mLastClickTime记录的总是上次click的时间
        long currentClickTime = Calendar.getInstance().getTimeInMillis();
        long elapsedTime = currentClickTime - mLastClickTime;
        if (elapsedTime > MIN_CLICK_INTERVAL) {
            mLastClickTime = currentClickTime;
            onSingleClick(v);
        }
    }
}

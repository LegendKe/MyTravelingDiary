package com.ruihai.xingka.widget.swiperefreshload;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

/**
 * Created by mac on 15/11/21.
 */
public class VetricalSwipeRefreshLoad extends SwipeRefreshLoadLayout {
    private int mTouchSlop;
    // 上一次触摸时的X坐标
    private float mPrevX;

    public VetricalSwipeRefreshLoad(Context context) {
        super(context);
    }

    public VetricalSwipeRefreshLoad(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 触发移动事件的最短距离，如果小于这个距离就不触发移动控件
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPrevX = ev.getX();
                break;

            case MotionEvent.ACTION_MOVE:
                final float eventX = ev.getX();
                float xDiff = Math.abs(eventX - mPrevX);
                // 增加60的容差，让下拉刷新在竖直滑动时就可以触发
                if (xDiff > mTouchSlop + 60) {
                    return false;
                }
        }
        return super.onInterceptTouchEvent(ev);
    }
}

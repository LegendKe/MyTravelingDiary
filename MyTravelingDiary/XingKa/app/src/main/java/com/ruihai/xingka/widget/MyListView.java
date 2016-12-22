package com.ruihai.xingka.widget;

/**
 * 重写ListView,以解决ListView与水平listView滑动时冲突
 * Created by gjzhang on 15/11/18.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

public class MyListView extends ListView {
    //    private GestureDetector mGestureDetector;
//    View.OnTouchListener mGestureListener;
//
//    public MyListView(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        mGestureDetector = new GestureDetector(new YScrollDetector());
//        setFadingEdgeLength(0);
//    }
//
//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        return super.onInterceptTouchEvent(ev)
//                && mGestureDetector.onTouchEvent(ev);
//    }
//
//    class YScrollDetector extends GestureDetector.SimpleOnGestureListener {
//        @Override
//        public boolean onScroll(MotionEvent e1, MotionEvent e2,
//                                float distanceX, float distanceY) {
//            if (Math.abs(distanceY) > Math.abs(distanceX)) {
//                return true;
//            }
//            return false;
//        }
//    }
    private float mDX;
    private float mDY;
    private float mLX;
    private float mLY;
    private int mLastAct;
    private boolean mIntercept;

    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDX = mDY = 0f;
                mLX = ev.getX();
                mLY = ev.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                final float X = ev.getX();
                final float Y = ev.getY();
                mDX += Math.abs(X - mLX);
                mDY += Math.abs(Y - mLY);
                mLX = X;
                mLY = Y;

                if (mIntercept && mLastAct == MotionEvent.ACTION_MOVE) {
                    return false;
                }

                if (mDX > mDY) {

                    mIntercept = true;
                    mLastAct = MotionEvent.ACTION_MOVE;
                    return false;
                }

        }
        mLastAct = ev.getAction();
        mIntercept = false;
        return super.onInterceptTouchEvent(ev);
    }
}

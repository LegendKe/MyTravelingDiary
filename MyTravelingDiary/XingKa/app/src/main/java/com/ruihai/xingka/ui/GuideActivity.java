package com.ruihai.xingka.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ruihai.xingka.R;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.ui.login.LoginActivity;

import java.util.ArrayList;

/**
 * 引导页界面的实现.
 */
public class GuideActivity extends Activity implements ViewPager.OnPageChangeListener, View.OnClickListener {
    ImageButton button, close;
    ArrayList mViewList = new ArrayList();
    LayoutInflater mLayoutInflater;
    MyPagerAdapter mPagerAdapter;
    // 底部小点图片
    private ImageView[] dots;
    // 记录当前选中位置
    private int currentIndex;
    private boolean callHappened = false;
    private boolean mPageEnd = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_main);
        //设置竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        SharedPreferences preferences = getSharedPreferences("first_pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isFirstIn", false);
        editor.commit();

        initView();
        // 初始化底部小点
        initDots();
    }

    /**
     * 点击跳转到主界面.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_close:
                jumpToMain();
                break;
        }
    }

    private void initView() {
        close = (ImageButton) findViewById(R.id.img_close);
        close.setOnClickListener(this);

        mLayoutInflater = getLayoutInflater();
        //可以按照需求进行动态创建Layout,这里暂用静态的xml layout
        mViewList.add(mLayoutInflater.inflate(R.layout.activity_guide_01, null));
        mViewList.add(mLayoutInflater.inflate(R.layout.activity_guide_02, null));
        mViewList.add(mLayoutInflater.inflate(R.layout.activity_guide_03, null));
        mViewList.add(mLayoutInflater.inflate(R.layout.activity_guide_04, null));

        // 初始化Adapter
        mPagerAdapter = new MyPagerAdapter(mViewList, this);
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(mPagerAdapter);
        viewPager.setOnPageChangeListener(this);
    }

    private void initDots() {
        LinearLayout ll = (LinearLayout) findViewById(R.id.ll_point);

        dots = new ImageView[mViewList.size()];
        // 循环取得小点图片
        for (int i = 0; i < mViewList.size(); i++) {
            dots[i] = (ImageView) ll.getChildAt(i);
            dots[i].setEnabled(true);// 都设为灰色
        }
        currentIndex = 0;
        dots[currentIndex].setEnabled(false); // 设置为橙色，即选中状态
    }

    private void setCurrentDot(int position) {
        if (position < 0 || position > mViewList.size() - 1
                || currentIndex == position) {
            return;
        }
        dots[position].setEnabled(false);
        dots[currentIndex].setEnabled(true);

        currentIndex = position;
    }

    // 滑动到最后一页点击跳到主界面
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mPageEnd && position == currentIndex && !callHappened) { // 向右继续滑动直接进入主页或注册登录页
            jumpToMain();
            mPageEnd = false;
            callHappened = true;
        } else {
            mPageEnd = false;
        }

        if (position == mViewList.size() - 1) {
            button = (ImageButton) findViewById(R.id.img_btn);
            button.setVisibility(View.GONE);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    jumpToMain();
                }
            });
        }
    }

    private void jumpToMain() {
        if (AccountInfo.getInstance().isLogin()) { // 判断用户是否登录
            startActivity(new Intent(this, MainActivity.class));
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }
        finish();
    }

    @Override
    public void onPageSelected(int position) {
        // 设置底部小点选中状态
        setCurrentDot(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (currentIndex == mPagerAdapter.getCount() - 1) {
            mPageEnd = true;
        }
    }


    class MyPagerAdapter extends PagerAdapter {
        private Context context;

        public MyPagerAdapter(ArrayList mViewList, GuideActivity guideActivity) {

        }

        @Override
        public int getCount() {
            return mViewList.size();
        }

        @Override
        public Object instantiateItem(View container, int position) {
            Log.i("INFO", "instantiate item:" + position);
            ((ViewPager) container).addView((View) mViewList.get(position), 0);
            return mViewList.get(position);
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            Log.i("INFO", "destroy item:" + position);
            ((ViewPager) container).removeView((View) mViewList.get(position));
        }

        @Override
        public boolean isViewFromObject(View v, Object obj) {
            return v == obj;
        }

    }
}

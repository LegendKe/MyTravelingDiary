package com.ruihai.xingka.ui.mine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.ruihai.xingka.R;
import com.ruihai.xingka.entity.TabEntity;
import com.ruihai.xingka.ui.BaseActivity;
import com.ruihai.xingka.ui.mine.fragment.UserCaptionFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bingoogolapple.bgabanner.BGAViewPager;

/**
 * 用户发布图说列表
 * Created by mac on 16/4/6.
 */
public class UserCaptionActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    public static void launch(Activity from, String account) {
        Intent intent = new Intent(from, UserCaptionActivity.class);
        intent.putExtra("userAccount", account);
        from.startActivity(intent);
    }

    @BindView(R.id.bga_viewpager)
    BGAViewPager mViewPager;
    @BindView(R.id.tab_layout)
    CommonTabLayout mTabLayout;
    @BindView(R.id.tv_right)
    TextView mRight;

    protected String mUserAccount;
    private List<Fragment> mFragmentList;
    private int flag;
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    private String[] mTitles = {"图说", "旅拼"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_caption);
        ButterKnife.bind(this);

        mRight.setVisibility(View.INVISIBLE);
        setUpViewPager();
        setUpSegmentedView();
        mViewPager.addOnPageChangeListener(this);

//        if (savedInstanceState == null && getIntent() != null) {
//            mUserAccount = getIntent().getStringExtra("userAccount");
//        } else {
//            if (savedInstanceState != null) {
//                mUserAccount = savedInstanceState.getString("userAccount");
//            }
//        }
//
//        UserCaptionFragment fragment = UserCaptionFragment.newInstance(mUserAccount);
//        getSupportFragmentManager().beginTransaction()
//                .add(R.id.fragment_container, fragment).commit();
    }

    private void setUpViewPager() {
        mFragmentList = new ArrayList<>();
        mFragmentList.add(UserCaptionFragment.newInstance(mUserAccount, 0));
        mFragmentList.add(UserCaptionFragment.newInstance(mUserAccount, 1));
        mViewPager.setAllowUserScrollable(true);
        mViewPager.setAdapter(new FriendViewPagerAdapter(getSupportFragmentManager(), mFragmentList));
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mTabLayout.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setUpSegmentedView() {
        for (int i = 0; i < mTitles.length; i++) {
            mTabEntities.add(new TabEntity(mTitles[i], 0, 0));
        }
        // 设置tabs
        mTabLayout.setTabData(mTabEntities);
        // 设置点击事件
        mTabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                if (position == 0) {
                    mViewPager.setCurrentItem(0, false);
                } else if (position == 1) {
                    mViewPager.setCurrentItem(1, false);
                }
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
    }

    @OnClick(R.id.tv_left)
    void onBack() {
        finish();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private static class FriendViewPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragmentList;

        public FriendViewPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragmentList = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
    }

}

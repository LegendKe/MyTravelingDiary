package com.ruihai.xingka.ui.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.ruihai.iconicfontengine.widget.IconicFontTextView;
import com.ruihai.xingka.R;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.entity.TabEntity;
import com.ruihai.xingka.ui.BaseActivity;
import com.ruihai.xingka.ui.mine.fragment.FriendFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bingoogolapple.bgabanner.BGAViewPager;

/**
 * 关注-粉丝模块
 * Created by lqfang on 16/1/20.
 */
public class FollowAndFansActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    public static void launch(Context from, String userAccount, int mflag) {
        Intent intent = new Intent(from, FollowAndFansActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        intent.putExtra("userAccount", userAccount);
        intent.putExtra("flag", mflag);
        from.startActivity(intent);
    }

    @BindView(R.id.tab_layout)
    CommonTabLayout mTabLayout;
    @BindView(R.id.tv_left)
    IconicFontTextView mBackView;
    @BindView(R.id.tv_right)
    IconicFontTextView mAddFriend;
    @BindView(R.id.bga_viewpager)
    BGAViewPager mViewPager;

    private String myAccount;//我的行咖号
    private String mUserAccount;//当前名片的行咖号
    private List<Fragment> mFragmentList;
    private int flag;
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    private String[] mTitles = {"关注", "粉丝"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_funs);
        ButterKnife.bind(this);

        myAccount = String.valueOf(AccountInfo.getInstance().loadAccount().getAccount());
        mUserAccount = getIntent().getStringExtra("userAccount");
        flag = getIntent().getIntExtra("flag", 1);
        initView();
        setUpViewPager();
        setUpSegmentedView();
        mViewPager.addOnPageChangeListener(this);
    }

    @OnClick(R.id.tv_left)
    void onBack() {
        finish();
    }

//    @OnClick(R.id.tv_right)
//    void onAddFriend() {
//        if (AccountInfo.getInstance().isLogin()) { // 判断用户是否登录
//            startActivity(new Intent(this, AddFriendsActivity.class));
//        } else { // 进入登录页面
//            startActivity(new Intent(this, LoginActivity.class));
//        }
//    }

    private void initView() {
        mBackView.setText("{xk-back}");
        mAddFriend.setVisibility(View.INVISIBLE);
        //  mAddFriend.setText("{xk-head-portrait}");
    }

    private void setUpViewPager() {
        mFragmentList = new ArrayList<>();
        if (myAccount.endsWith(mUserAccount)) { //我的主页关注为好友和关注
            //1:好友 2:关注 3:粉丝 4:好友—关注
            mFragmentList.add(FriendFragment.newInstance(4, mUserAccount, 2));
//            mFragmentList.add(FriendFragment.newInstance(1, mUserAccount));
            mFragmentList.add(FriendFragment.newInstance(3, mUserAccount, 2));
            mViewPager.setOffscreenPageLimit(1);
        } else { //他人主页关注仅为关注
            mFragmentList.add(FriendFragment.newInstance(2, mUserAccount, 2));
            mFragmentList.add(FriendFragment.newInstance(3, mUserAccount, 2));
            mViewPager.setOffscreenPageLimit(1);
        }
        mViewPager.setAllowUserScrollable(true);//启用ViewPager的滑动事件
        mViewPager.setAdapter(new FriendViewPagerAdapter(getSupportFragmentManager(), mFragmentList));
    }

    private void setUpSegmentedView() {
        for (int i = 0; i < mTitles.length; i++) {
            mTabEntities.add(new TabEntity(mTitles[i], 0, 0));
        }
        // 设置tabs
        mTabLayout.setTabData(mTabEntities);
        mViewPager.setCurrentItem(flag - 1);
        mTabLayout.setCurrentTab(flag - 1);

        // 设置点击事件
        mTabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                mViewPager.setCurrentItem(position, false);
            }

            @Override
            public void onTabReselect(int position) {

            }
        });

    }


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

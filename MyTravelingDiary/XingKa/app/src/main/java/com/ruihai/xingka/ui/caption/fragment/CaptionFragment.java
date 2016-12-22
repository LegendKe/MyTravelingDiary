package com.ruihai.xingka.ui.caption.fragment;

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
import com.ruihai.xingka.XKApplication;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.entity.TabEntity;
import com.ruihai.xingka.ui.BaseFragment;
import com.ruihai.xingka.ui.login.LoginActivity;
import com.ruihai.xingka.ui.mine.AddFriendsActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bingoogolapple.bgabanner.BGAViewPager;

/**
 * 图说主页(包含发现和关注)
 * <p/>
 * A simple {@link Fragment} subclass.
 */
public class CaptionFragment extends BaseFragment {

    @BindView(R.id.viewPager)
    BGAViewPager mViewPager;
    @BindView(R.id.tab_layout)
    CommonTabLayout mTabLayout;
    @BindView(R.id.tv_left)
    IconicFontTextView leftBtn;
    @BindView(R.id.tv_right)
    IconicFontTextView rightBtn;

    private List<Fragment> fragmentList;
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    private String[] mTitles = {"发现", "关注", "推荐"};

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_caption);
        ButterKnife.bind(this, mContentView);
        setUpSegmentedView();
        setUpViewPager();

    }

    @Override
    protected void setListener() {

    }


    @Override
    protected void processLogic(Bundle savedInstanceState) {
        leftBtn.setVisibility(View.INVISIBLE);
        rightBtn.setVisibility(View.VISIBLE);
        rightBtn.setText("{xk-add-friend}");
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
                } else if (position == 2) {
                    mViewPager.setCurrentItem(2, false);
                }
            }

            @Override
            public void onTabReselect(int position) {

            }
        });

    }

    private void setUpViewPager() {
        fragmentList = new ArrayList<>();
        fragmentList.add(CaptionListFragment.newInstance(0));
        fragmentList.add(CaptionListFragment.newInstance(1));
        fragmentList.add(CaptionListFragment.newInstance(2));
        mViewPager.setAllowUserScrollable(true);
        mViewPager.setAdapter(new ContentViewPagerAdapter(getChildFragmentManager(), fragmentList));
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

    @Override
    protected void onUserVisible() {

    }

    @OnClick(R.id.tv_right)
    void onLeftClicked() {
        if (AccountInfo.getInstance().isLogin()) { // 判断用户是否登录
            startActivity(new Intent(getActivity(), AddFriendsActivity.class));
        } else { // 进入登录页面
            logout();
        }
    }


    private void logout() {
        AccountInfo.getInstance().clearAccount();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        XKApplication.getInstance().exit();
    }

    private static class ContentViewPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragmentList;

        public ContentViewPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
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

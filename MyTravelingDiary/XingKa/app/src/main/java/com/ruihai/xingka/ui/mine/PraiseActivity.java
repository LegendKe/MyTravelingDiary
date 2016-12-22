package com.ruihai.xingka.ui.mine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.ruihai.iconicfontengine.widget.IconicFontTextView;
import com.ruihai.xingka.R;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.api.model.PraiseInfo;
import com.ruihai.xingka.entity.TabEntity;
import com.ruihai.xingka.ui.BaseActivity;
import com.ruihai.xingka.ui.mine.adapter.PraiseAdapter;
import com.ruihai.xingka.ui.mine.fragment.FriendFragment;
import com.shizhefei.mvc.MVCHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bingoogolapple.bgabanner.BGAViewPager;

/**
 * 图说-旅拼
 * 我的点赞人列表
 * Created by lqfang on 16/1/21.
 */
public class PraiseActivity extends BaseActivity implements ViewPager.OnPageChangeListener {
    public final static String KEY_AUTHOR_ACCOUNT = "KEY_AUTHOR";
//    @Bind(R.id.tv_title)
//    TextView titleView;
//    @Bind(R.id.tv_right)
//    IconicFontTextView rightView;
//    @Bind(R.id.rl_recyclerview_refresh)
//    PtrClassicFrameLayout mRefreshLayout;

    @BindView(R.id.tab_layout)
    CommonTabLayout mTabLayout;
    @BindView(R.id.tv_left)
    IconicFontTextView mBackView;
    @BindView(R.id.tv_right)
    IconicFontTextView mAddFriend;
    @BindView(R.id.bga_viewpager)
    BGAViewPager mViewPager;

    private PraiseAdapter mAdapter;
    private MVCHelper<List<PraiseInfo>> listViewHelper;
    private String mMyAccount;
    private String mUserAccount;
    private List<Fragment> mFragmentList;
    private int flag;
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    private String[] mTitles = {"图说", "旅拼"};

    public static void launch(Activity from, String author) {
        Intent intent = new Intent(from, PraiseActivity.class);
        intent.putExtra(KEY_AUTHOR_ACCOUNT, author);
//        intent.putExtra("flag", mflag);
        from.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_praise);
        ButterKnife.bind(this);
        mMyAccount = String.valueOf(AccountInfo.getInstance().loadAccount().getAccount());
        mUserAccount = getIntent().getStringExtra(KEY_AUTHOR_ACCOUNT);
        initView();
        setUpViewPager();
        setUpSegmentedView();
        mViewPager.addOnPageChangeListener(this);
    }

    private void initView() {
//        mAddFriend.setVisibility(View.GONE);
//        titleView.setText("我的赞");
//        rightView.setVisibility(View.INVISIBLE);
//        listViewHelper.setLoadViewFractory(new ProfileLoadViewFactory("想为你点赞，可是没有发现图说/(ㄒoㄒ)/~~"));
//        listViewHelper = new MVCUltraHelper<>(mRefreshLayout);
//        // 设置数据源
//        listViewHelper.setDataSource(new PraiseDataSourse(mMyAccount, mUserAccount));
//        // 设置适配器
//        mAdapter = new PraiseAdapter(this);
//        mAdapter.setOnItemClickListener(this);

//        listViewHelper.setAdapter(mAdapter);
//        // 加载数据
//        listViewHelper.refresh();
    }

    private void setUpViewPager() {
        mFragmentList = new ArrayList<>();
        mFragmentList.add(FriendFragment.newInstance(0, mUserAccount, 1));
        mFragmentList.add(FriendFragment.newInstance(1, mUserAccount, 1));
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

//        mFragmentList = new ArrayList<>();
//        if (mMyAccount.endsWith(mUserAccount)) {
//            mFragmentList.add(FriendFragment.newInstance(2, mUserAccount));
////            mFragmentList.add(FriendFragment.newInstance(1, mUserAccount));
//            mFragmentList.add(FriendFragment.newInstance(3, mUserAccount));
//            mViewPager.setOffscreenPageLimit(2);
//        } else {
//            mFragmentList.add(FriendFragment.newInstance(2, mUserAccount));
//            mFragmentList.add(FriendFragment.newInstance(3, mUserAccount));
//            mViewPager.setOffscreenPageLimit(1);
//        }
//        mViewPager.setAllowUserScrollable(true);//启用ViewPager的滑动事件
//        mViewPager.setAdapter(new FriendViewPagerAdapter(getSupportFragmentManager(), mFragmentList));
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

//    @Override
//    public void onItemClick(View view, int position) {
//        String account = String.valueOf(mAdapter.getData().get(position).getAccount());
//        if(mMyAccount.equals(account)){
////            MainActivity.launch(this, account,1);
//        }else {
//            UserProfileActivity.launch(this, account);
//        }
//    }
//
//    @Override
//    public void onItemLongClick(View view, int position) {
//
//    }
//
//    @Override
//    public void onItemChildClick(View childView, int position) {
//
//    }

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

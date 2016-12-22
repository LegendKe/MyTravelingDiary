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
import com.ruihai.xingka.ui.trackway.fragment.MyCollectFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bingoogolapple.bgabanner.BGAViewPager;


/**
 * 图说-旅拼收藏
 * Created by lqfang on 16/1/19.
 */
public class UserCollectionActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    private static final String ARG_USER_ACCOUNT = "user_account";

    public static void launch(Activity from) {
        Intent intent = new Intent(from, UserCollectionActivity.class);
//        intent.putExtra("account", account);
        from.startActivity(intent);
    }

    @BindView(R.id.bga_viewpager)
    BGAViewPager mViewPager;
    @BindView(R.id.tab_layout)
    CommonTabLayout mTabLayout;
    @BindView(R.id.tv_right)
    TextView mRight;

    private List<Fragment> mFragmentList;
    private int flag;
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    private String[] mTitles = {"图说", "旅拼"};
//    private String mUserAccount;
//    private UserCollectionGridAdapter mAdapter;
//    private MVCHelper<List<UserCollection>> listViewHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        ButterKnife.bind(this);
//        mUserAccount = getIntent().getStringExtra("account");
        mRight.setVisibility(View.INVISIBLE);
        setUpViewPager();
        setUpSegmentedView();
        mViewPager.addOnPageChangeListener(this);
    }

    private void setUpViewPager() {
        mFragmentList = new ArrayList<>();
        mFragmentList.add(MyCollectFragment.newInstance(0));
        mFragmentList.add(MyCollectFragment.newInstance(1));
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

//    private void showCancelDialog(final UserCollection photoTopic, final int position) {
//        // 1. 布局文件转换为View对象
//        LayoutInflater inflater = LayoutInflater.from(mContext);
//        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.dialog_communal, null);
//        final Dialog dialog = new AlertDialog.Builder(mContext).create();
//
//        dialog.setCancelable(false);
//        dialog.show();
//        dialog.getWindow().setContentView(layout);
//
//        // 3. 消息内容
//        TextView dialog_msg = (TextView) layout.findViewById(R.id.update_content);
//        dialog_msg.setText("确定取消收藏该图说吗?");
//
//        // 4. 确定按钮
//        Button btnOK = (Button) layout.findViewById(R.id.umeng_update_id_ok);
//        btnOK.setText("确定");
//        btnOK.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                cancelCollect(photoTopic, position, true);
//                dialog.dismiss();
//            }
//        });
//
//        // 5. 取消按钮
//        Button btnCancel = (Button) layout.findViewById(R.id.umeng_update_id_cancel);
//        btnCancel.setText("取消");
//        btnCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//    }
//
//
//    /**
//     * 执行取消收藏图说操作
//     *
//     * @param photoTopic
//     * @param position
//     */
//    private void cancelCollect(UserCollection photoTopic, final int position, final Boolean showDialog) {
//        // 整理取消图说收藏参数并加密
//        User currentAccount = AccountInfo.getInstance().loadAccount();
//        String account = Security.aesEncrypt(String.valueOf(currentAccount.getAccount()));
//        String pGuid = Security.aesEncrypt(photoTopic.getpGuid());
//        String isCollect = Security.aesEncrypt("0");
//        String toAccount = Security.aesEncrypt(String.valueOf(photoTopic.getAuthorAccount()));
//        if (showDialog) {
//            ProgressHUD.showLoadingMessage(mContext, "正在取消收藏...", false);
//        }
//        ApiModule.apiService().photoTopicCollectionAdd(account, pGuid, isCollect, toAccount).enqueue(new Callback<XKRepo>() {
//            @Override
//            public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
//                XKRepo xkRepo = response.body();
//                updateCountEvent();
//
//                ProgressHUD.dismiss();
//                if (xkRepo.isSuccess()) {
//                    mAdapter.getData().remove(position);
//                    mAdapter.notifyDataSetChanged();
//                    if (showDialog) {
//                        ProgressHUD.showSuccessMessage(mContext, "取消收藏成功");
//                    }
//                } else {
//                    if (showDialog) {
//                        ProgressHUD.showInfoMessage(mContext, xkRepo.getMsg());
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<XKRepo> call, Throwable t) {
//                ProgressHUD.showErrorMessage(mContext, getString(R.string.common_network_error));
//            }
//        });
//    }
//
//    private void updateCountEvent() {
//        EventBus.getDefault().post(new UpdateCountEvent());
//    }

    @OnClick(R.id.tv_left)
    void onBack() {
        finish();
    }

//    @Override
//    public boolean canScrollVertically(int direction) {
//        return mGridView != null && mGridView.canScrollVertically(direction);
//    }


//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        listViewHelper.destory();
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

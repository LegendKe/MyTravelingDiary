package com.ruihai.xingka.ui.mine.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ruihai.xingka.R;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.api.model.OfficialMessageDetail;
import com.ruihai.xingka.api.model.User;
import com.ruihai.xingka.ui.BaseFragment;
import com.ruihai.xingka.ui.caption.CaptionDetailActivity;
import com.ruihai.xingka.ui.common.WebActivity;
import com.ruihai.xingka.ui.mine.adapter.NoticeAdapter;
import com.ruihai.xingka.ui.mine.datasource.NoticeDataSource;
import com.ruihai.xingka.ui.mine.impl.ProfileLoadViewFactory;
import com.shizhefei.mvc.MVCHelper;
import com.shizhefei.mvc.MVCUltraHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;

/**
 * 系统通知模块
 * Created by apple on 15/9/11.
 */
@SuppressLint("ValidFragment")
public class NoticeFragment extends BaseFragment {

    private static final String ARG_USER_ACCOUNT = "user_account";

    @BindView(R.id.list_notice)
    ListView mListNotice;
    @BindView(R.id.rl_recyclerview_refresh)
    PtrClassicFrameLayout mRefreshLayout;

    private NoticeAdapter mNoticeAdapter;
    private MVCHelper<List<OfficialMessageDetail>> listViewHelper;
    Context context;
    private User mCurrentUser;

//    public NoticeFragment(Context context) {
//        this.context = context;
//    }

    public static NoticeFragment newInstance() {
        NoticeFragment fragment = new NoticeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentUser = AccountInfo.getInstance().loadAccount();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_notice);
        ButterKnife.bind(this, mContentView);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        mNoticeAdapter = new NoticeAdapter(getActivity());
        listViewHelper = new MVCUltraHelper<>(mRefreshLayout);
        listViewHelper.setLoadViewFractory(new ProfileLoadViewFactory("所有的美景，都将如约而至"));
        // 设置数据源
        listViewHelper.setDataSource(new NoticeDataSource(String.valueOf(mCurrentUser.getAccount())));
        // 设置适配器
        //mAdapter.setOnItemClickListener(this);
        listViewHelper.setAdapter(mNoticeAdapter);
        // 加载数据
        listViewHelper.refresh();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 释放资源
        listViewHelper.destory();
    }

    @Override
    protected void onUserVisible() {
        if (listViewHelper != null) {
            listViewHelper.refresh();
        }
    }


}

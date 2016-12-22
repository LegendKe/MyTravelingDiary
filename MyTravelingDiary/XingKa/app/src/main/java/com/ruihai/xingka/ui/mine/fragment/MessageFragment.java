package com.ruihai.xingka.ui.mine.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.ruihai.xingka.R;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.api.model.PushMessage;
import com.ruihai.xingka.api.model.User;
import com.ruihai.xingka.ui.BaseFragment;
import com.ruihai.xingka.ui.caption.CaptionDetailActivity;
import com.ruihai.xingka.ui.mine.adapter.MessageAdapter;
import com.ruihai.xingka.ui.mine.datasource.CaptionCollectDataSource;
import com.ruihai.xingka.ui.mine.datasource.CaptionPriaiseDataSource;
import com.ruihai.xingka.ui.mine.datasource.CommentDataSource;
import com.ruihai.xingka.ui.mine.datasource.FollowDataSource;
import com.ruihai.xingka.ui.mine.datasource.AtmineDataSource;
import com.ruihai.xingka.ui.mine.impl.ProfileLoadViewFactory;
import com.ruihai.xingka.ui.trackway.TrackwayDetailActivity;
import com.shizhefei.mvc.MVCHelper;
import com.shizhefei.mvc.MVCUltraHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;

/**
 * 消息模块
 * Created by apple on 15/9/11.
 */
@SuppressLint("ValidFragment")
public class MessageFragment extends BaseFragment {
    @BindView(R.id.rl_recyclerview_refresh)
    PtrClassicFrameLayout mRefreshLayout;

    private MessageAdapter mAdapter;
    private User mCurrentUser;
    private MVCHelper<List<PushMessage>> listViewHelper;
    private int mFlag = 0;


    public static MessageFragment newInstance(int flag) {
        MessageFragment fragment = new MessageFragment();
        Bundle args = new Bundle();
        args.putInt("FLAG", flag);//1:@我的 2:评论 3:点赞 4:收藏 5:关注
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
        setContentView(R.layout.fragment_message);
        ButterKnife.bind(this, mContentView);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        mFlag = getArguments().getInt("FLAG", 0);
//        mFlag = getArguments().getInt("FLAG", 2);
//        mFlag = getArguments().getInt("FLAG", 3);
//        mFlag = getArguments().getInt("FLAG", 4);
//        mFlag = getArguments().getInt("FLAG", 5);

        mAdapter = new MessageAdapter(getActivity());
        listViewHelper.setLoadViewFractory(new ProfileLoadViewFactory("东方有火红的希望，南方有温暖的巢床，向西逐退残阳，向北唤醒芬芳"));
        listViewHelper = new MVCUltraHelper<>(mRefreshLayout);
        // 设置数据源
        if (mFlag == 1) { //@我的
            listViewHelper.setDataSource(new AtmineDataSource(String.valueOf(mCurrentUser.getAccount())));
        } else if (mFlag == 2) { //评论
            listViewHelper.setDataSource(new CommentDataSource(String.valueOf(mCurrentUser.getAccount())));
        } else if (mFlag == 3) { //点赞
            listViewHelper.setDataSource(new CaptionPriaiseDataSource(String.valueOf(mCurrentUser.getAccount())));
        } else if (mFlag == 4) { //收藏
            listViewHelper.setDataSource(new CaptionCollectDataSource(String.valueOf(mCurrentUser.getAccount())));
        } else if (mFlag == 5) { //关注
            listViewHelper.setDataSource(new FollowDataSource(String.valueOf(mCurrentUser.getAccount())));
        }
        // 设置适配器
        //mAdapter.setOnItemClickListener(this);
        listViewHelper.setAdapter(mAdapter);
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

//    private void getPushMessageList() {
//        String sAccount = Security.aesEncrypt(String.valueOf(mCurrentUser.getAccount()));
//        String sPushType = Security.aesEncrypt("0");
//        String sReadType = Security.aesEncrypt("-1");
//        String sPage = Security.aesEncrypt("1");
//        String sPerpage = Security.aesEncrypt("100");
//        ApiModule.apiService().photoTopicPushList(sAccount, sPushType, sReadType, sPage, sPerpage, new Callback<PushMessageRepo>() {
//            @Override
//            public void success(PushMessageRepo pushMessageRepo, Response response) {
//                if (pushMessageRepo.isSuccess()) {
//                    mPushMessageList.clear();
//                    mPushMessageList.addAll(pushMessageRepo.getPushMessages());
//                    mAdapter.notifyDataSetChanged();
//                } else {
//
//                }
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                ProgressHUD.showErrorMessage(getActivity(), getString(R.string.common_network_error));
//            }
//        });
//    }

    @OnItemClick(R.id.list_message)
    void onItemClick(int position) {
        PushMessage item = mAdapter.getData().get(position);
        int pushType = item.getPushType();
        if (pushType == 1 || pushType == 2 || pushType == 3 || pushType == 4 || pushType == 5 || pushType == 6 || pushType == 7) { //图说详情
            CaptionDetailActivity.launch(getActivity(), item.getpGuid(), item.getAuthorAccount());
        } else if (pushType == 21 || pushType == 22 || pushType == 23 || pushType == 24 || pushType == 25 || pushType == 26 || pushType == 27) { //旅拼详情
            TrackwayDetailActivity.launch(getActivity(), item.getpGuid(), item.getAuthorAccount());
        }
    }
}

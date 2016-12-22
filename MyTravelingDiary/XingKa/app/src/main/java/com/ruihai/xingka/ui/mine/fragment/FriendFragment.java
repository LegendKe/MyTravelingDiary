package com.ruihai.xingka.ui.mine.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.ruihai.xingka.R;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.api.model.MyFriendInfoRepo;
import com.ruihai.xingka.api.model.PraiseInfo;
import com.ruihai.xingka.event.OnItemClickListener;
import com.ruihai.xingka.ui.MainActivity;
import com.ruihai.xingka.ui.mine.UserProfileActivity;
import com.ruihai.xingka.ui.mine.adapter.AttentionFriendAdapter;
import com.ruihai.xingka.ui.mine.adapter.PraiseAdapter;
import com.ruihai.xingka.ui.mine.datasource.MyAttentionDataSource;
import com.ruihai.xingka.ui.mine.datasource.PraiseDataSourse;
import com.ruihai.xingka.ui.mine.impl.ProfileLoadViewFactory;
import com.ruihai.xingka.ui.trackway.datasource.MyPraiseListDataSource;
import com.shizhefei.mvc.MVCHelper;
import com.shizhefei.mvc.MVCUltraHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;

/**
 * 关注-好友-粉丝模块
 * Created by gjzhang on 15/10/30.
 */
public class FriendFragment extends Fragment implements OnItemClickListener {
    @BindView(R.id.rl_recyclerview_refresh)
    PtrClassicFrameLayout mRefreshLayout;
    @BindView(R.id.rv_list)
    ListView mListView;

    private AttentionFriendAdapter mAdapter;
    private int mType, mFlag;//类型
    private String mUserAccount;//被查看人行咖号
    private String myAccount;//我的行咖号
    private MVCHelper<List<MyFriendInfoRepo.MyFriendInfo>> listViewHelper;
    private MVCHelper<List<PraiseInfo>> listViewHelper1;
    PraiseAdapter mPraiseAdapter;

    public static FriendFragment newInstance(int type, String userAccount, int flag) {
        FriendFragment fragment = new FriendFragment();
        Bundle bundle = new Bundle();
        bundle.putString("userAccount", userAccount);
        bundle.putInt("type", type);
        bundle.putInt("flag", flag);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mType = getArguments().getInt("type");
        mFlag = getArguments().getInt("flag");
        mUserAccount = getArguments().getString("userAccount");
        //获取我的行咖号
        myAccount = String.valueOf(AccountInfo.getInstance().loadAccount().getAccount());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attention_friend, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mFlag == 2) { //关注-粉丝点赞人列表
            switch (mType) {
                case 1:
                    listViewHelper.setLoadViewFractory(new ProfileLoadViewFactory("你还没有好友哦,快去互粉吧"));
                    break;
                case 2:
                    //无关注列表的提示：
                    listViewHelper.setLoadViewFractory(new ProfileLoadViewFactory("每一次相遇都是奇迹"));
                    break;
                case 3:
                    //无粉丝列表的提示：
                    listViewHelper.setLoadViewFractory(new ProfileLoadViewFactory("你笑，全世界便与你同声笑"));
                    break;
                default:
                    break;
            }
            listViewHelper = new MVCUltraHelper<>(mRefreshLayout);
            // 设置数据源
            listViewHelper.setDataSource(new MyAttentionDataSource(myAccount, mUserAccount, String.valueOf(mType)));
            // 设置适配器
            mAdapter = new AttentionFriendAdapter(getActivity(), mType, mUserAccount);
            mAdapter.setOnItemClickListener(this);
            listViewHelper.setAdapter(mAdapter);
            // 加载数据
            listViewHelper.refresh();
        } else if (mFlag == 1) {  //图说-旅拼点赞人列表
            switch (mType) {
                case 0:
                    listViewHelper1.setLoadViewFractory(new ProfileLoadViewFactory("想为你点赞，可是没有发现图说/(ㄒoㄒ)/~~"));
                    listViewHelper1 = new MVCUltraHelper<>(mRefreshLayout);
                    // 设置数据源
                    listViewHelper1.setDataSource(new PraiseDataSourse(myAccount, mUserAccount));
                    break;
                case 1:
                    listViewHelper1.setLoadViewFractory(new ProfileLoadViewFactory("想为你点赞，可是没有发现旅拼/(ㄒoㄒ)/~~"));
                    listViewHelper1 = new MVCUltraHelper<>(mRefreshLayout);
                    // 设置数据源
                    listViewHelper1.setDataSource(new MyPraiseListDataSource(myAccount, mUserAccount));
                    break;
                default:
                    break;
            }

            // 设置适配器
            mPraiseAdapter = new PraiseAdapter(getActivity());
            mPraiseAdapter.setOnItemClickListener(this);
            listViewHelper1.setAdapter(mPraiseAdapter);
            // 加载数据
            listViewHelper1.refresh();

        }

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && listViewHelper != null) {
            listViewHelper.refresh();
        } else if (isVisibleToUser && listViewHelper1 != null) {
            listViewHelper1.refresh();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 释放资源
        if (mFlag == 1) {
            listViewHelper1.destory();
        } else if (mFlag == 2) {
            listViewHelper.destory();
        }
    }


    @Override
    public void onItemClick(View view, int position) {
        String account = "";
        boolean isAdamin = false;
        if (mFlag == 1) { //关注-粉丝列表
            account = String.valueOf(mPraiseAdapter.getData().get(position).getAccount());
            isAdamin = mPraiseAdapter.getData().get(position).isAdmin();
        } else if (mFlag == 2) { //图说-旅拼点赞人列表
            account = String.valueOf(mAdapter.getData().get(position).getAccount());
            isAdamin = mAdapter.getData().get(position).isAdmin();
        }
//        UserProfileActivity.launch(getActivity(), account);
        if (myAccount.equals(account)) {
//            MainActivity.launch(getActivity(), account,1);
        } else if (isAdamin) {
            UserProfileActivity.launch(getActivity(), account, 1, 1);
        } else {
            UserProfileActivity.launch(getActivity(), account, 1, 0);
        }
    }

    @Override
    public void onItemLongClick(View view, int position) {

    }

    @Override
    public void onItemChildClick(View childView, int position) {

    }
}

package com.ruihai.xingka.ui.mine.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ruihai.xingka.R;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.api.model.MyFriendInfoRepo;
import com.ruihai.xingka.event.OnItemClickListener;
import com.ruihai.xingka.ui.MainActivity;
import com.ruihai.xingka.ui.mine.UserProfileActivity;
import com.ruihai.xingka.ui.mine.adapter.AttentionFriendAdapter;
import com.ruihai.xingka.ui.mine.datasource.MyAttentionDataSource;
import com.ruihai.xingka.ui.mine.impl.ProfileLoadViewFactory;
import com.shizhefei.mvc.MVCHelper;
import com.shizhefei.mvc.MVCUltraHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;

/**
 * 关注-粉丝模块
 * Created by mac on 16/1/20.
 */
public class FollowAndFansFrament extends FriendFragment implements OnItemClickListener {

    @BindView(R.id.rl_recyclerview_refresh)
    PtrClassicFrameLayout mRefreshLayout;

    private AttentionFriendAdapter mAdapter;
    private int mType;//类型
    private String mUserAccount;//被查看人行咖号
    private String myAccount;//我的行咖号
    private MVCHelper<List<MyFriendInfoRepo.MyFriendInfo>> listViewHelper;


    public static FriendFragment newInstance(int type, String userAccount) {
        FriendFragment fragment = new FriendFragment();
        Bundle bundle = new Bundle();
        bundle.putString("userAccount", userAccount);
        bundle.putInt("type", type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mType = getArguments().getInt("type");
        mUserAccount = getArguments().getString("userAccount");
        //获取我的行咖号
        myAccount = String.valueOf(AccountInfo.getInstance().loadAccount().getAccount());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_follow_fans, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        switch (mType) {
//            case 1:
//                listViewHelper.setLoadViewFractory(new ProfileLoadViewFactory("你还没有好友哦,快去互粉吧"));
//                break;
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
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && listViewHelper != null) {
            listViewHelper.refresh();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 释放资源
        listViewHelper.destory();
    }


    @Override
    public void onItemClick(View view, int position) {
        String account = String.valueOf(mAdapter.getData().get(position).getAccount());
        if (myAccount.equals(account)) {
//            if (MainActivity.currentTabIndex != 3) {
//                MainActivity.launch(getActivity(), 1);
//            }
        } else if (mAdapter.getData().get(position).isAdmin()) {
            UserProfileActivity.launch(getActivity(), account, 1, 1);
        } else {
            UserProfileActivity.launch(getActivity(), account, 1, 0);
        }
//        UserProfileActivity.launch(getActivity(), account);
    }

    @Override
    public void onItemLongClick(View view, int position) {

    }

    @Override
    public void onItemChildClick(View childView, int position) {

    }

}

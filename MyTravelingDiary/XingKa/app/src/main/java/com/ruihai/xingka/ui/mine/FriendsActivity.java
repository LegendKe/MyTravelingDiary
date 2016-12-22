package com.ruihai.xingka.ui.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ruihai.iconicfontengine.widget.IconicFontTextView;
import com.ruihai.xingka.R;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.api.model.MyFriendInfoRepo;
import com.ruihai.xingka.event.OnItemClickListener;
import com.ruihai.xingka.ui.BaseActivity;
import com.ruihai.xingka.ui.mine.adapter.AttentionFriendAdapter;
import com.ruihai.xingka.ui.mine.datasource.MyAttentionDataSource;
import com.ruihai.xingka.ui.mine.impl.ProfileLoadViewFactory;
import com.shizhefei.mvc.MVCHelper;
import com.shizhefei.mvc.MVCUltraHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;

/**
 * Created by lqfang on 16/1/22.
 */
public class FriendsActivity extends BaseActivity implements OnItemClickListener {

    public static void launch(Context from, String userAccount) {
        Intent intent = new Intent(from, FriendsActivity.class);
        intent.putExtra("userAccount", userAccount);
        from.startActivity(intent);
    }

    @BindView(R.id.tv_title)
    TextView titleView;
    @BindView(R.id.tv_right)
    IconicFontTextView rightView;
    @BindView(R.id.rl_recyclerview_refresh)
    PtrClassicFrameLayout mRefreshLayout;

    private AttentionFriendAdapter mAdapter;
    private int mType = 1;//类型
    private String mUserAccount;//被查看人行咖号
    private String myAccount;//我的行咖号
    private MVCHelper<List<MyFriendInfoRepo.MyFriendInfo>> listViewHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_praise_list);
        ButterKnife.bind(this);
        myAccount = String.valueOf(AccountInfo.getInstance().loadAccount().getAccount());
        mUserAccount = getIntent().getStringExtra("userAccount");

        initView();
    }

    private void initView() {
        titleView.setText("我的好友");
        //加好友图标
        rightView.setVisibility(View.GONE);
        rightView.setText("{xk-add-friend}");

        mAdapter = new AttentionFriendAdapter(mContext, 1, mUserAccount);
        listViewHelper = new MVCUltraHelper<>(mRefreshLayout);
        listViewHelper.setLoadViewFractory(new ProfileLoadViewFactory("你还没有好友哦,快去互粉吧"));
        // 设置数据源
        listViewHelper.setDataSource(new MyAttentionDataSource(myAccount, mUserAccount, String.valueOf(mType)));
        // 设置适配器
        mAdapter = new AttentionFriendAdapter(mContext, mType, mUserAccount);
        mAdapter.setOnItemClickListener(this);
        listViewHelper.setAdapter(mAdapter);
        // 加载数据
        listViewHelper.refresh();
    }

    @OnClick(R.id.tv_back)
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

    @Override
    public void onItemClick(View view, int position) {
        String account = String.valueOf(mAdapter.getData().get(position).getAccount());
//        UserProfileActivity.launch(this, account);
        if (myAccount.equals(account)) {
//            MainActivity.launch(this, account,1);
        } else if (mAdapter.getData().get(position).isAdmin()) {
            UserProfileActivity.launch(this, account, 1, 1);
        } else {
            UserProfileActivity.launch(this, account, 1, 0);
        }
    }

    @Override
    public void onItemLongClick(View view, int position) {

    }

    @Override
    public void onItemChildClick(View childView, int position) {

    }
}

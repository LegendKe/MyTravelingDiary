package com.ruihai.xingka.ui.caption;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.ruihai.xingka.R;
import com.ruihai.xingka.api.ApiModule;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.api.model.MyFriendInfoRepo;
import com.ruihai.xingka.api.model.MyFriendInfoRepo.MyFriendInfo;
import com.ruihai.xingka.api.model.TagInfo;
import com.ruihai.xingka.api.model.User;
import com.ruihai.xingka.ui.BaseActivity;
import com.ruihai.xingka.ui.caption.adapter.MyFriendInfoAdapter;
import com.ruihai.xingka.utils.AppUtility;
import com.ruihai.xingka.utils.Security;
import com.ruihai.xingka.widget.ClearableEditText;
import com.ruihai.xingka.widget.DividerItemDecoration;
import com.ruihai.xingka.widget.ProgressHUD;
import com.ruihai.xingka.widget.ProgressLayout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * 好友选择页
 */
public class ChooseFriendsActivity extends BaseActivity {

    public final static String KEY_SELECTED_FRIENDS = "SELECTED_FRIENDS";


    @BindView(R.id.et_search_key)
    ClearableEditText mSearchKeyEditText;
    @BindView(R.id.rv_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.progress_layout)
    ProgressLayout progressLayout;
    @BindView(R.id.tv_right)
    TextView mRight;
    @BindView(R.id.tv_title)
    TextView mTitle;


    private List<MyFriendInfo> mData = new ArrayList<>();
    private List<MyFriendInfo> mSearchData = new ArrayList<>();
    private MyFriendInfoAdapter mAdapter;
    private ArrayList<MyFriendInfo> mSelectedDatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_friends);
        ButterKnife.bind(this);
        initViews();
        initListener();
        if (AccountInfo.getInstance().getFriendList() != null) {
            mData = AccountInfo.getInstance().getFriendList();
            mAdapter.update(mData);
            reloadData();
        } else {
            loadData();
        }


    }

    private void initListener() {
        mSearchKeyEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString())) {
                    searchFriend(s.toString());
                } else {
                    mAdapter.update(mData);
                }
            }
        });
    }

    private void searchFriend(String keyWord) {
        mSearchData.clear();
        for (MyFriendInfo myFriendInfo : mData) {
            if (myFriendInfo.getNick().contains(keyWord)) {
                mSearchData.add(myFriendInfo);
            }
        }
        if (mSearchData.size() == 0) {
            AppUtility.showToast("无结果");
        }
        mAdapter.update(mSearchData);
    }

    private void initViews() {
//      mSearchKeyEditText.setHint(R.string.caption_input_friend_name);
        mTitle.setText("@好友");
        mRight.setVisibility(View.VISIBLE);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        //添加分割线
//        mRecyclerView.addItemDecoration(new DividerItemDecoration(
//                this, DividerItemDecoration.VERTICAL_LIST));
        mSelectedDatas = getIntent().getParcelableArrayListExtra(KEY_SELECTED_FRIENDS);
        //mData.addAll(myDatas);
        mAdapter = new MyFriendInfoAdapter(mData);
        mRecyclerView.setAdapter(mAdapter);
    }

    @OnClick(R.id.tv_back)
    void onBack() {
        finish();
    }

    @OnClick(R.id.tv_right)
    void onRight() {
        List<MyFriendInfo> myFriendInfos = ((MyFriendInfoAdapter) mAdapter).getMyFriendInfos();
        List<MyFriendInfo> selectedFriends = new ArrayList<>();
        for (int i = 0; i < myFriendInfos.size(); i++) {
            MyFriendInfo info = myFriendInfos.get(i);
            if (info.isSelected() == true) {
                selectedFriends.add(info);
            }
        }
        //if (selectedFriends.size() > 0) {
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(KEY_SELECTED_FRIENDS, (ArrayList<? extends Parcelable>) selectedFriends);
        setResult(RESULT_OK, intent);
        finish();
//        } else {
//            AppUtility.showToast("请选择好友");
//        }
    }

    void reloadData() {
        User user = AccountInfo.getInstance().loadAccount();
        String account = Security.aesEncrypt(String.valueOf(user.getAccount()));
        ApiModule.apiService().myFriendList(account).enqueue(new Callback<MyFriendInfoRepo>() {
            @Override
            public void onResponse(Call<MyFriendInfoRepo> call, Response<MyFriendInfoRepo> response) {
                MyFriendInfoRepo myFriendInfoRepo = response.body();
                String msg = myFriendInfoRepo.getMsg();
                if (myFriendInfoRepo.isSuccess()) {
                    List<MyFriendInfo> myFriendInfos = myFriendInfoRepo.getMyFriendInfoList();
                    AccountInfo.getInstance().saveFriendList(myFriendInfos);
                    Logger.i(myFriendInfos.size() + " <-- SIZE");
                    for (MyFriendInfo myFriendInfo : myFriendInfos) {
                        for (int i = 0; i < mSelectedDatas.size(); i++) {
                            if (myFriendInfo.getAccount() == mSelectedDatas.get(i).getAccount()) {
                                myFriendInfo.setSelected(true);
                            }
                        }
                    }
                    mAdapter.update(myFriendInfos);
                }
            }

            @Override
            public void onFailure(Call<MyFriendInfoRepo> call, Throwable t) {
            }
        });
    }

    void loadData() {
        progressLayout.showLoading();
        User user = AccountInfo.getInstance().loadAccount();
        String account = Security.aesEncrypt(String.valueOf(user.getAccount()));
        ApiModule.apiService().myFriendList(account).enqueue(new Callback<MyFriendInfoRepo>() {
            @Override
            public void onResponse(Call<MyFriendInfoRepo> call, Response<MyFriendInfoRepo> response) {
                MyFriendInfoRepo myFriendInfoRepo = response.body();
                progressLayout.showContent();
                String msg = myFriendInfoRepo.getMsg();
                if (myFriendInfoRepo.isSuccess()) {
                    List<MyFriendInfo> myFriendInfos = myFriendInfoRepo.getMyFriendInfoList();
                    AccountInfo.getInstance().saveFriendList(myFriendInfos);

                    Logger.i(myFriendInfos.size() + " <-- SIZE");
                    if (myFriendInfos.size() == 0) {
                        ProgressHUD.showInfoMessage(mContext, "你还没好友哦!");
                    }
                    for (MyFriendInfo myFriendInfo : myFriendInfos) {
                        for (int i = 0; i < mSelectedDatas.size(); i++) {
                            if (myFriendInfo.getAccount() == mSelectedDatas.get(i).getAccount()) {
                                myFriendInfo.setSelected(true);
                            }
                        }
                    }
                    mAdapter.update(myFriendInfos);
                }
            }

            @Override
            public void onFailure(Call<MyFriendInfoRepo> call, Throwable t) {
                progressLayout.showError(null,
                        "No Connection",
                        "We could not establish a connection with our servers. Please try again when you are connected to the internet.",
                        "Try Again", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                loadData();
                            }
                        });
            }
        });
    }


}

package com.ruihai.xingka.ui.mine;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.ruihai.xingka.R;
import com.ruihai.xingka.api.ApiModule;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.api.model.SearchUser;
import com.ruihai.xingka.api.model.XKRepo;
import com.ruihai.xingka.ui.BaseActivity;
import com.ruihai.xingka.ui.MainActivity;
import com.ruihai.xingka.ui.login.LoginActivity;
import com.ruihai.xingka.ui.mine.adapter.FriendAdapter;
import com.ruihai.xingka.utils.Security;
import com.ruihai.xingka.widget.ClearableEditText;
import com.ruihai.xingka.widget.ProgressHUD;
import com.ruihai.xingka.widget.PullListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by lqfang on 16/1/16.
 */
public class SearchUsersActivity extends BaseActivity {

    @BindView(R.id.lv_addfriend_fouth)
    PullListView mLvFouth;
    @BindView(R.id.et_search_key)
    ClearableEditText mSearchKeyEditText;
    @BindView(R.id.tv_search1)
    TextView searchBtn;

    private int mPage = 1;
    private int mPerPage = 20;

    private String mMyAccount;
    private String mUserAccount;
    private List<SearchUser> mSearchUserList = new ArrayList<>();
    private FriendAdapter mFriendAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_users);
        ButterKnife.bind(this);

        mMyAccount = String.valueOf(AccountInfo.getInstance().loadAccount().getAccount());

//        View emptyLayout = findViewById(R.id.empty_layout);
//        TextView emptyText = (TextView) findViewById(R.id.textView1);
//        emptyText.setText("没有用户哦");
//        mLvFouth.setEmptyView(emptyLayout);

        searchBtn.setVisibility(View.VISIBLE);
        mLvFouth.setDivider(null);
        mSearchKeyEditText.setHint("搜索用户昵称或者行咖号");
        mSearchKeyEditText.setFocusable(true);
        mSearchKeyEditText.setFocusableInTouchMode(true);
        mSearchKeyEditText.requestFocus();
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mSearchKeyEditText.getText().toString())) {
                    ProgressHUD.showInfoMessage(mContext, "请输入搜索关键词");
                } else {
                    mPage = 1;
                    submitSearch(mSearchKeyEditText.getText().toString());
                    // 先隐藏键盘
                    hideSoftInput(v.getWindowToken());
                }
            }
        });

        mLvFouth.setOnGetMoreListener(new PullListView.OnGetMoreListener() {
            @Override
            public void onGetMore() {
                mPage = mPage + 1;
                String searchKey = mSearchKeyEditText.getText().toString().trim();
//                submitSearch(searchKey);
                String account = Security.aesEncrypt(String.valueOf(currentUser.getAccount()));
                String searchStr = Security.aesEncrypt(searchKey);
                String page = Security.aesEncrypt(String.valueOf(mPage));
                String perPage = Security.aesEncrypt(String.valueOf(mPerPage));

                ApiModule.apiService().returnSearchUser(account, searchStr, page, perPage).enqueue(new Callback<XKRepo>() {
                    @Override
                    public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                        XKRepo xkRepo = response.body();
                        mLvFouth.getMoreComplete();
                        if (xkRepo.isSuccess()) {
                            if (mPage == 1) {
                                mSearchUserList.clear();
                            }
                            mSearchUserList.addAll(xkRepo.getSearchUserList());
                            mFriendAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<XKRepo> call, Throwable t) {
                        ProgressHUD.showErrorMessage(mContext, getString(R.string.common_network_error));
                    }

                });

            }
        });

        mFriendAdapter = new FriendAdapter(mContext, mSearchUserList);
        mLvFouth.setAdapter(mFriendAdapter);

        //对好友昵称或行帐号进行搜索
        mSearchKeyEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int keyCode, KeyEvent event) {
                //修改回车键功能
                if (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    String searchKey = v.getText().toString().trim();
                    if (TextUtils.isEmpty(searchKey)) {
                        ProgressHUD.showInfoMessage(mContext, "请输入搜索关键词");
                        return true;
                    } else {
                        mPage = 1;
                        submitSearch(searchKey);
                        // 先隐藏键盘
                        hideSoftInput(v.getWindowToken());
                    }
                    return true;
                }
                return false;
            }
        });
        //跳转到搜索当前行咖号的主页
        mLvFouth.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mUserAccount = String.valueOf(mSearchUserList.get(i - 1).getAccount());
//                UserProfileActivity.launch(SearchUsersActivity.this, mUserAccount);
                if (mMyAccount.equals(mUserAccount)) {
                    MainActivity.launch(SearchUsersActivity.this, 1);
                } else if (mSearchUserList.get(i - 1).isAdmin()) {
                    UserProfileActivity.launch(SearchUsersActivity.this, mUserAccount, 1, 1);
                } else {
                    UserProfileActivity.launch(SearchUsersActivity.this, mUserAccount, 1, 0);
                }
            }
        });
    }

    private void submitSearch(String searchKey) {
        // 判断用户是否登录,如果没有则跳转至登录页
        if (currentUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }

        String account = Security.aesEncrypt(String.valueOf(currentUser.getAccount()));
        String searchStr = Security.aesEncrypt(searchKey);
        String page = Security.aesEncrypt(String.valueOf(mPage));
        String perPage = Security.aesEncrypt(String.valueOf(mPerPage));

        ApiModule.apiService().returnSearchUser(account, searchStr, page, perPage).enqueue(new Callback<XKRepo>() {
            @Override
            public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                XKRepo xkRepo = response.body();
                //                mLvFouth.getMoreComplete();
                //判断若集合中没有数据则隐藏已经加载完毕
                if (xkRepo.getSearchUserList() != null) {
                    mLvFouth.getMoreComplete();
                } else {
                    mLvFouth.initNoMore();
                }

                if (xkRepo.isSuccess()) {
                    if (mPage == 1) {
                        mSearchUserList.clear();
                    }
                    mSearchUserList.addAll(xkRepo.getSearchUserList());
                } else {
                    ProgressHUD.showInfoMessage(mContext, "对不起,没有搜索到相关用户");
                    mSearchUserList.clear();
                    mLvFouth.initNoMore();
                }
                mFriendAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<XKRepo> call, Throwable t) {
                ProgressHUD.showErrorMessage(mContext, getString(R.string.common_network_error));
            }

        });
    }

    @OnClick(R.id.tv_back)
    void onBack() {
        finish();
    }
}

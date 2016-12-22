package com.ruihai.xingka.ui.talking;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.friend.FriendServiceObserve;
import com.netease.nimlib.sdk.friend.model.Friend;
import com.netease.nimlib.sdk.friend.model.FriendChangedNotify;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.ruihai.iconicfontengine.widget.IconicFontTextView;
import com.ruihai.xingka.R;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.api.model.User;
import com.ruihai.xingka.ui.BaseActivity;
import com.ruihai.xingka.ui.talking.adapter.FirstFriendAdapter;
import com.ruihai.xingka.ui.talking.adapter.FriendAdapter;
import com.ruihai.xingka.utils.AppUtility;
import com.ruihai.xingka.widget.AppNoScrollerListView;
import com.ruihai.xingka.widget.ClearableEditText;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

/**
 * 好友
 * Created by lqfang on 16/8/5.
 */
public class FriendActivity extends BaseActivity {

    public static void launch(Context context) {
        Intent intent = new Intent(context, FriendActivity.class);
//        intent.putExtra("userAccount", userAccount);
        context.startActivity(intent);
    }


    @BindView(R.id.tv_back)
    IconicFontTextView mLeft;
    @BindView(R.id.tv_title)
    TextView mTitle;
    @BindView(R.id.tv_right)
    IconicFontTextView mRight;
    @BindView(R.id.et_search_key)
    ClearableEditText editText;
    @BindView(R.id.ans_list)
    AppNoScrollerListView appNoScrollerListView;
    @BindView(R.id.rv_list)
    RecyclerView recyclerView;

    String[] friend_data = {"新的好友", "好友群聊"};
    int[] pic_friend = {R.mipmap.icon_new_friend, R.mipmap.icon_team_talking};

    private FirstFriendAdapter firstFriendAdapter;
    private FriendAdapter friendAdapter;

    private String mAccount;
    private User currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        ButterKnife.bind(this);

        initViews();
        setListener();
    }

    private void initViews() {
        mRight.setText("{xk-add-friend}");
        mTitle.setText("好友");

        currentUser = AccountInfo.getInstance().loadAccount();
        if (currentUser != null) {
            mAccount = String.valueOf(currentUser.getAccount());
        }


        //监听好友关系的变化
        Observer<FriendChangedNotify> friendChangedNotifyObserver = new Observer<FriendChangedNotify>() {
            @Override
            public void onEvent(FriendChangedNotify friendChangedNotify) {
                List<Friend> addedOrUpdatedFriends = friendChangedNotify.getAddedOrUpdatedFriends(); // 新增的好友
                List<String> deletedFriendAccounts = friendChangedNotify.getDeletedFriends(); // 删除好友或者被解除好友
                Log.e("TAG", "新增的好友-->" + addedOrUpdatedFriends.size());
                if (addedOrUpdatedFriends != null) {
                    for (int i = 1; i < addedOrUpdatedFriends.size(); i++) {
                        Friend friend = addedOrUpdatedFriends.get(i);
                        String account = friend.getAccount();
                        Log.e("TAG", "用户资料111-->" + friend.getAlias() + "\n" + account);
                    }
                }
            }
        };

        NIMClient.getService(FriendServiceObserve.class).observeFriendChangedNotify(friendChangedNotifyObserver, true);


        //根据用户账号获取好友关系
//        Friend friend = NIMClient.getService(FriendService.class).getFriendByAccount("account");

        //判断用户是否为我的好友
//        boolean isMyFriend = NIMClient.getService(FriendService.class).isMyFriend(account);

        //获取好友列表 该方法是同步方法，返回我的好友帐号集合，可以根据帐号来获取对应的用户资料来构建自己的通讯录,见构建通讯录。
        List<String> accounts = NIMClient.getService(FriendService.class).getFriendAccounts(); // 获取所有好友帐号
        List<NimUserInfo> users = NIMClient.getService(UserService.class).getUserInfoList(accounts); // 获取所有好友用户资料
        //获取服务器用户资料
        NIMClient.getService(UserService.class).fetchUserInfo(accounts).setCallback(new RequestCallback<List<NimUserInfo>>() {
               @Override
               public void onSuccess(List<NimUserInfo> userInfos) {
                      for (int i = 1; i < userInfos.size(); i++) {
                           NimUserInfo userInfo = userInfos.get(i);
                           Log.e("TAG", "用户资料-->" + userInfo.getName() + "\n" + userInfo.getAccount());
                           recyclerView.setLayoutManager(new LinearLayoutManager(FriendActivity.this));
                           friendAdapter = new FriendAdapter(FriendActivity.this, userInfos);
                           recyclerView.setAdapter(friendAdapter);
                      }

               }

               @Override
               public void onFailed(int i) {

               }

               @Override
               public void onException(Throwable throwable) {

               }
        });


        firstFriendAdapter = new FirstFriendAdapter(this, friend_data, pic_friend);
        appNoScrollerListView.setAdapter(firstFriendAdapter);
        firstFriendAdapter.notifyDataSetChanged();

    }


    /****
     * 添加监听
     */

    private void setListener() {

        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String searchContent = editText.getText().toString();
//                if (searchContent.equals(""))
//                {
//                    country_iv_clearText.setVisibility(View.INVISIBLE);
//                }
//                else
//                {
//                    country_iv_clearText.setVisibility(View.VISIBLE);
//                }
//
//                if (searchContent.length() > 0)
//                {
//                    // 按照输入内容进行匹配
//                    ArrayList<CountrySortModel> fileterList = (ArrayList<CountrySortModel>) countryChangeUtil
//                            .search(searchContent, mAllCountryList);
//
//                    adapter.updateListView(fileterList);
//                }
//                else
//                {
//                    adapter.updateListView(mAllCountryList);
//                }
//                country_lv_countryList.setSelection(0);
            }
        });

//		// 右侧sideBar监听
//		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener()
//		{
//
//			@Override
//			public void onTouchingLetterChanged(String s)
//			{
//				// 该字母首次出现的位置
//				int position = adapter.getPositionForSection(s.charAt(0));
//				if (position != -1)
//				{
//					country_lv_countryList.setSelection(position);
//				}
//			}
//		});

//        country_lv_countryList.setOnItemClickListener(new AdapterView.OnItemClickListener()
//        {
//
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3)
//            {
//                String countryName = null;
//                String countryNumber = null;
//                String searchContent = editText.getText().toString();
//
//                if (searchContent.length() > 0)
//                {
//                    // 按照输入内容进行匹配
//                    ArrayList<CountrySortModel> fileterList = (ArrayList<CountrySortModel>) countryChangeUtil
//                            .search(searchContent, mAllCountryList);
//                    countryName = fileterList.get(position).countryName;
//                    countryNumber = fileterList.get(position).countryNumber;
//                }
//                else
//                {
//                    // 点击后返回
//                    countryName = mAllCountryList.get(position).countryName;
//                    countryNumber = mAllCountryList.get(position).countryNumber;
//                }
//
//                Intent intent = new Intent();
//                //intent.setClass(CountryActivity.this, RegisterPhoneNumActivity.class);
//                intent.putExtra("countryName", countryName);
//                intent.putExtra("countryNumber", countryNumber);
//                setResult(RESULT_OK, intent);
//                Log.e("TAG", "countryName: + " + countryName + "countryNumber: " + countryNumber);
//                finish();
//
//            }
//        });

    }


    @OnItemClick(R.id.ans_list)
    void onItemFirstClick(int position) {
        if (position == 0) { //新的好友
            NewFriendsActivity.launch(this);
        } else if (position == 1) { //好友群聊
            TeamListActivity.launch(this);
        }

    }

    @OnClick(R.id.tv_back)
    void onBack(View view) {
        finish();
    }

    @OnClick(R.id.tv_right)
    void onAddFriend() {
        AddFriendActivity.launch(this);
    }

    /**
     * 解决：点击空白区域，自动隐藏软键盘
     *
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            // 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体按键会移动焦点）
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                hideSoftInput(v.getWindowToken());
//                mCommonEnterRoot.setVisibility(View.GONE);
//                mBottomLayout.setVisibility(View.VISIBLE);
                return true;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
     *
     * @param v
     * @param event
     * @return
     */
    public boolean isShouldHideInput(View v, MotionEvent event) {

        if (v != null && (v instanceof EditText)) {

            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (event.getX() > (left - AppUtility.dip2px(70)) && event.getX() < (right + AppUtility.dip2px(70))
                    && event.getY() > top - AppUtility.dip2px(5) && event.getY() < bottom + AppUtility.dip2px(210)) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return false;
    }


}

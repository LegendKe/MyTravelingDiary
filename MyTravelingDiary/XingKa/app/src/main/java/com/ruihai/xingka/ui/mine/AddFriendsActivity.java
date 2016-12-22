package com.ruihai.xingka.ui.mine;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ruihai.iconicfontengine.widget.IconicFontTextView;
import com.ruihai.xingka.R;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.api.model.SearchUser;
import com.ruihai.xingka.ui.BaseActivity;
import com.ruihai.xingka.ui.mine.adapter.AddFriendsAdapter;
import com.ruihai.xingka.ui.mine.adapter.FriendAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;


/**
 * Created by lqfang on 15/9/25.
 */

public class AddFriendsActivity extends BaseActivity {

    public static void launch(Activity from) {
        Intent intent = new Intent(from, AddFriendsActivity.class);
        from.startActivity(intent);
    }

    @BindView(R.id.tv_title)
    TextView mTitle;
    @BindView(R.id.tv_right)
    IconicFontTextView mRight;
    @BindView(R.id.tv_xingka_number)
    TextView mNumber;
    @BindView(R.id.lv_addfriend_first)
    ListView mLvFirst;
    @BindView(R.id.lv_addfriend_second)
    ListView mLvSecond;
    @BindView(R.id.lv_addfriend_third)
    ListView mLvThird;
    @BindView(R.id.lv_addfriend_fouth)
    ListView mLvFouth;

    private String mAccount;

    String[] data_first = {"咖友推荐"};
    String[] data_second = {"邀请好友", "手机通讯录邀请"};
    String[] data_third = {"扫一扫"};


    int[] pic_first = {R.mipmap.search_recommend};

    int[] pic_second = {R.mipmap.search_invite, R.mipmap.search_iphone};
    int[] pic_third = {R.mipmap.search_ss};

    private ArrayList<HashMap<String, Object>> friendsInApp;
    private ArrayList<HashMap<String, Object>> contactsInMobile;
    private Dialog pd;

    private List<SearchUser> mSearchUserList = new ArrayList<>();
    private FriendAdapter mFriendAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfriends);
        ButterKnife.bind(this);
        initViews();

    }

    private void initViews() {
        mAccount = String.valueOf(AccountInfo.getInstance().loadAccount().getAccount());
        mTitle.setText("添加好友");
        mRight.setVisibility(View.INVISIBLE);
        mNumber.setText(mAccount);

        AddFriendsAdapter adapter_first = new AddFriendsAdapter(this, data_first, pic_first);
        AddFriendsAdapter adapter_second = new AddFriendsAdapter(this, data_second, pic_second);
        AddFriendsAdapter adapter_third = new AddFriendsAdapter(this, data_third, pic_third);
        mLvFirst.setAdapter(adapter_first);
        mLvSecond.setAdapter(adapter_second);
        mLvThird.setAdapter(adapter_third);
    }

    //进入到搜索用户页面
    @OnClick(R.id.tv_search_key)
    void onSearch() {
        startActivity(new Intent(this, SearchUsersActivity.class));
    }

    // 行咖推荐界面
    @OnItemClick(R.id.lv_addfriend_first)
    void onFirstItemClik(int position) {
        Intent intent = new Intent();
        intent.setClass(this, RecommendFriendActivity.class);
        startActivity(intent);

    }

    @OnItemClick(R.id.lv_addfriend_second)
    void onSecondItemClik(int position) {
        if (position == 0) {
            //邀请好友
            Intent intent = new Intent();
            intent.setClass(this, RecommendActivity.class);
            startActivity(intent);
        } else if (position == 1) {
            //打开通信录好友列表页面
            Intent intent = new Intent();
            intent.setClass(this, MailListActivity.class);
            startActivity(intent);
        }

    }

    //打开扫一扫页面
    @OnItemClick(R.id.lv_addfriend_third)
    void onThirdItemClik(int position) {
        if (position == 0) {
            Intent intent = new Intent();
            intent.setClass(this, MipcaActivityCapture.class);
            startActivity(intent);
        }
    }

    //打开二维码扫描页面
    @OnClick(R.id.ll_qr)
    void onQR(){
        Intent intent = new Intent();
        intent.setClass(this, PictureDisplayActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("show", 2);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @OnClick(R.id.tv_back)
    void onBack() {
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        finish();
        return true;
    }
}

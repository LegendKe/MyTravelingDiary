package com.ruihai.xingka.ui.talking;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.netease.nim.uikit.common.activity.UI;
import com.ruihai.iconicfontengine.widget.IconicFontTextView;
import com.ruihai.xingka.R;
import com.ruihai.xingka.widget.AppNoScrollerListView;
import com.ruihai.xingka.widget.ClearableEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 群聊
 * Created by lqfang on 16/8/9.
 */
public class TeamListActivity extends UI{

    public static void launch(Context context) {
        Intent intent = new Intent(context, TeamListActivity.class);
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
    @BindView(R.id.start_group_chat_text)
    TextView start_group_chat_text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        ButterKnife.bind(this);
        initViews();
    }

    private void initViews() {
        mTitle.setText("群聊");
        mRight.setVisibility(View.INVISIBLE);
        appNoScrollerListView.setVisibility(View.GONE);
        start_group_chat_text.setVisibility(View.VISIBLE);


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
    }

    @OnClick(R.id.tv_back)
    void onBack(){
        finish();
    }

}

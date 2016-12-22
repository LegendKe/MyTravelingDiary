package com.ruihai.xingka.ui.talking;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.ruihai.iconicfontengine.widget.IconicFontTextView;
import com.ruihai.xingka.R;
import com.ruihai.xingka.ui.BaseActivity;
import com.ruihai.xingka.widget.AppNoScrollerListView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 聊天信息
 * Created by lqfang on 16/8/9.
 */
public class MessageInfoActivity extends BaseActivity{

    public static void launch(Context context) {
        Intent intent = new Intent(context, MessageInfoActivity.class);
//        intent.putExtra("userAccount", userAccount);
        context.startActivity(intent);
    }

    @BindView(R.id.tv_title)
    TextView mTitle;
    @BindView(R.id.tv_right)
    IconicFontTextView mRight;
    @BindView(R.id.sdv_avatar)
    SimpleDraweeView mAvatar;
    @BindView(R.id.appns_list1)
    AppNoScrollerListView appns_list1;
    @BindView(R.id.appns_list2)
    AppNoScrollerListView appns_list2;
    @BindView(R.id.appns_list3)
    AppNoScrollerListView appns_list3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_info);
        ButterKnife.bind(this);
        initViews();
    }

    private void initViews() {
        mTitle.setText("聊天信息");
        mRight.setVisibility(View.INVISIBLE);

    }
}

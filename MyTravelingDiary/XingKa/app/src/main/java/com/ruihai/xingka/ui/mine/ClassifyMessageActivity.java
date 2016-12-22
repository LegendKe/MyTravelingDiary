package com.ruihai.xingka.ui.mine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ruihai.xingka.R;
import com.ruihai.xingka.ui.BaseActivity;
import com.ruihai.xingka.ui.mine.fragment.MessageFragment;
import com.ruihai.xingka.ui.mine.fragment.NoticeFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mac on 16/4/6.
 */
public class ClassifyMessageActivity extends BaseActivity {

    public static void launch(Activity from, int flag) {
        Intent intent = new Intent(from, ClassifyMessageActivity.class);
        intent.putExtra("flag", flag);
        from.startActivity(intent);
    }

    @BindView(R.id.tv_title)
    TextView mTitle;
    @BindView(R.id.tv_right)
    TextView mRight;
    @BindView(R.id.ll_message)
    LinearLayout messageLayout;

    protected String mUserAccount;
    private int mFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classify_message);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        messageLayout.setVisibility(View.VISIBLE);
        mRight.setVisibility(View.INVISIBLE);
        mFlag = getIntent().getIntExtra("flag", 0);
//        mFlag = getIntent().getIntExtra("flag", 2);
//        mFlag = getIntent().getIntExtra("flag", 3);
//        mFlag = getIntent().getIntExtra("flag", 4);
//        mFlag = getIntent().getIntExtra("flag", 5);
//        mFlag = getIntent().getIntExtra("flag", 6);
//        AppUtility.showToast(String.valueOf(mFlag));
        if (mFlag == 1) {
            mTitle.setText("@我的");
            MessageFragment fragment = MessageFragment.newInstance(1);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment).commit();
        } else if (mFlag == 2) {
            mTitle.setText("评论");
            MessageFragment fragment = MessageFragment.newInstance(2);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment).commit();
        } else if (mFlag == 3) {
            mTitle.setText("点赞");
            MessageFragment fragment = MessageFragment.newInstance(3);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment).commit();
        } else if (mFlag == 4) {
            mTitle.setText("收藏");
            MessageFragment fragment = MessageFragment.newInstance(4);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment).commit();
        } else if (mFlag == 5) {
            mTitle.setText("关注");
            MessageFragment fragment = MessageFragment.newInstance(5);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment).commit();
        } else if (mFlag == 6) {
            mTitle.setText("系统通知");
            NoticeFragment fragment = NoticeFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment).commit();
        }

    }


    @OnClick(R.id.tv_back)
    void onBack() {
        finish();
    }
}

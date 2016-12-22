package com.ruihai.xingka.ui.mine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.ruihai.xingka.R;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.api.model.MessageNoReadNum;
import com.ruihai.xingka.ui.BaseActivity;
import com.ruihai.xingka.ui.mine.adapter.MyFirstMessageAdapter;
import com.ruihai.xingka.ui.mine.adapter.MySecondMessageAdapter;
import com.ruihai.xingka.ui.mine.adapter.MyThirdMessageAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

/**
 * 消息中心
 * Created by mac on 16/4/5.
 */
public class MyMessageActivity extends BaseActivity {

    public static void launch(Activity from, int atNum, int commentNum, int praiseNum, int collectNum, int focusNum, int officialNum) {
        Intent intent = new Intent(from, MyMessageActivity.class);
//        intent.putExtra("message",  messageNoReadNum);
        intent.putExtra("atNum", atNum);
        intent.putExtra("commentNum", commentNum);
        intent.putExtra("praiseNum", praiseNum);
        intent.putExtra("collectNum", collectNum);
        intent.putExtra("focusNum", focusNum);
        intent.putExtra("officialNum", officialNum);
//        intent.putExtra("localOfficialNum", localOfficialNum);
        from.startActivity(intent);
    }

    @BindView(R.id.tv_right)
    TextView mRightView;
    @BindView(R.id.tv_title)
    TextView mTitle;
    @BindView(R.id.list1)
    ListView mList1;
    @BindView(R.id.list2)
    ListView mList2;
    @BindView(R.id.list3)
    ListView mList3;

    String[] data_first = {"@我的"};
    String[] data_second = {"评论", "点赞", "收藏", "关注"};
    String[] data_third = {"系统通知"};

    int[] pic_first = {R.mipmap.icon_mention};
    int[] pic_second = {R.mipmap.icon_comment, R.mipmap.icon_praise, R.mipmap.icon_collect, R.mipmap.icon_attent};
    int[] pic_third = {R.mipmap.icon_notice};

    MyFirstMessageAdapter adapter_first;
    MySecondMessageAdapter adapter_second;
    MyThirdMessageAdapter adapter_third;
    private MessageNoReadNum messageNum;
    private int atNum, commentNum, praiseNum, collectNum, focusNum, officialNum, localOfficialNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mymessage);
        ButterKnife.bind(this);
        //获取本地系统消息
        localOfficialNum = AccountInfo.getInstance().getOfficialNum();

        mTitle.setText("消息中心");
        mRightView.setVisibility(View.GONE);
//        messageNum = (MessageNoReadNum) getIntent().getSerializableExtra("message");
        atNum = getIntent().getIntExtra("atNum", atNum);
        commentNum = getIntent().getIntExtra("commentNum", commentNum);
        praiseNum = getIntent().getIntExtra("praiseNum", praiseNum);
        collectNum = getIntent().getIntExtra("collectNum", collectNum);
        focusNum = getIntent().getIntExtra("focusNum", focusNum);
        officialNum = getIntent().getIntExtra("officialNum", officialNum);
        //本地消息
        localOfficialNum = getIntent().getIntExtra("localOfficialNum", localOfficialNum);


        adapter_first = new MyFirstMessageAdapter(this, data_first, pic_first, atNum);
        adapter_second = new MySecondMessageAdapter(this, data_second, pic_second, commentNum, praiseNum, collectNum, focusNum);
        adapter_third = new MyThirdMessageAdapter(this, data_third, pic_third, officialNum);
        mList1.setAdapter(adapter_first);
        mList2.setAdapter(adapter_second);
        mList3.setAdapter(adapter_third);

        adapter_first.notifyDataSetChanged();
        adapter_second.notifyDataSetChanged();
        adapter_third.notifyDataSetChanged();

    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter_first = new MyFirstMessageAdapter(this, data_first, pic_first, atNum);
        adapter_second = new MySecondMessageAdapter(this, data_second, pic_second, commentNum, praiseNum, collectNum, focusNum);
        adapter_third = new MyThirdMessageAdapter(this, data_third, pic_third, officialNum);
        mList1.setAdapter(adapter_first);
        mList2.setAdapter(adapter_second);
        mList3.setAdapter(adapter_third);
        //刷新listview
        adapter_first.notifyDataSetChanged();
        adapter_second.notifyDataSetChanged();
        adapter_third.notifyDataSetChanged();
    }

    @OnClick(R.id.tv_back)
    void onBack() {
        finish();
    }

    @OnItemClick(R.id.list1)
    void onFirstItemClick(int position) {
        //@我的
        ClassifyMessageActivity.launch(this, 1);
        atNum = 0;
    }

    @OnItemClick(R.id.list2)
    void onSecondItemClick(int position) {
        if (position == 0) {
            //评论
            ClassifyMessageActivity.launch(this, 2);
            commentNum = 0;
        } else if (position == 1) {
            //点赞
            ClassifyMessageActivity.launch(this, 3);
            praiseNum = 0;
        } else if (position == 2) {
            //收藏
            ClassifyMessageActivity.launch(this, 4);
            collectNum = 0;
        } else if (position == 3) {
            //关注
            ClassifyMessageActivity.launch(this, 5);
            focusNum = 0;
        }
    }

    @OnItemClick(R.id.list3)
    void onThirdItemClick(int position) {
        if (position == 0) {
            //系统通知
            ClassifyMessageActivity.launch(this, 6);
            //保存系统通知
//            AccountInfo.getInstance().saveOfficialNum(officialNum);
        }
    }

}

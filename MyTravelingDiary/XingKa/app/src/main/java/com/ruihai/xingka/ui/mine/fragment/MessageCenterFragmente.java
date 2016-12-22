package com.ruihai.xingka.ui.mine.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.ruihai.xingka.R;
import com.ruihai.xingka.api.ApiModule;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.api.model.MessageNoReadNum;
import com.ruihai.xingka.api.model.MsgType;
import com.ruihai.xingka.api.model.ReadMsg;
import com.ruihai.xingka.ui.BaseFragment;
import com.ruihai.xingka.ui.mine.ClassifyMessageActivity;
import com.ruihai.xingka.ui.mine.adapter.MyFirstMessageAdapter;
import com.ruihai.xingka.ui.mine.adapter.MySecondMessageAdapter;
import com.ruihai.xingka.ui.mine.adapter.MyThirdMessageAdapter;
import com.ruihai.xingka.utils.Security;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import de.greenrobot.event.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessageCenterFragmente extends BaseFragment {
    @BindView(R.id.tv_right)
    TextView mRightView;
    @BindView(R.id.tv_back)
    TextView mLeftView;
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
    private String mAccount;
    MyFirstMessageAdapter adapter_first;
    MySecondMessageAdapter adapter_second;
    MyThirdMessageAdapter adapter_third;
    private MessageNoReadNum messageNum;
    private int atNum, commentNum, praiseNum, collectNum, focusNum, officialNum, localOfficialNum;

    public static MessageCenterFragmente newInstance() {
        MessageCenterFragmente fragment = new MessageCenterFragmente();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_mymessage);
        ButterKnife.bind(this, mContentView);
        mTitle.setText(R.string.module_message);
        mRightView.setVisibility(View.INVISIBLE);
        mLeftView.setVisibility(View.VISIBLE);
        mAccount = String.valueOf(currentUser.getAccount());
        //获取本地系统消息
        localOfficialNum = AccountInfo.getInstance().getOfficialNum();
        EventBus.getDefault().register(this);

//        messageNum = (MessageNoReadNum) getIntent().getSerializableExtra("message");
//        atNum = getIntent().getIntExtra("atNum", atNum);
//        commentNum = getIntent().getIntExtra("commentNum", commentNum);
//        praiseNum = getIntent().getIntExtra("praiseNum", praiseNum);
//        collectNum = getIntent().getIntExtra("collectNum", collectNum);
//        focusNum = getIntent().getIntExtra("focusNum", focusNum);
//        officialNum = getIntent().getIntExtra("officialNum", officialNum);
        //本地消息
        // localOfficialNum = getIntent().getIntExtra("localOfficialNum", localOfficialNum);


        adapter_first = new MyFirstMessageAdapter(getContext(), data_first, pic_first, atNum);
        adapter_second = new MySecondMessageAdapter(getContext(), data_second, pic_second, commentNum, praiseNum, collectNum, focusNum);
        adapter_third = new MyThirdMessageAdapter(getContext(), data_third, pic_third, officialNum);
        mList1.setAdapter(adapter_first);
        mList2.setAdapter(adapter_second);
        mList3.setAdapter(adapter_third);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        loadData();
    }

    private void loadData() {
//请求消息接口

        final String account = Security.aesEncrypt(mAccount);
        String OS = Security.aesEncrypt("1");
        ApiModule.apiService_1().getPushNumMessageNoRead(account, OS).enqueue(new Callback<MessageNoReadNum>() {
            @Override
            public void onResponse(Call<MessageNoReadNum> call, Response<MessageNoReadNum> response) {
                messageNum = response.body();
                String msg = messageNum.getMsg();
                if (messageNum.isSuccess()) {
                    atNum = messageNum.getAtNum();
                    commentNum = messageNum.getCommentNum();
                    praiseNum = messageNum.getPraiseNum();
                    collectNum = messageNum.getCollectNum();
                    focusNum = messageNum.getFocusNum();
                    officialNum = messageNum.getOfficialNum();

                    adapter_first.notifyData(atNum);
                    adapter_second.notifyData(commentNum, praiseNum, collectNum, focusNum);
                    adapter_third.notifyData(officialNum);
                }
            }

            @Override
            public void onFailure(Call<MessageNoReadNum> call, Throwable t) {
                //ProgressHUD.showErrorMessage(getActivity(), getString(R.string.common_network_error));
            }
        });


    }

    @Override
    protected void onUserVisible() {

    }

    @Override
    public void onResume() {
        super.onResume();
        adapter_first.notifyData(atNum);
        adapter_second.notifyData(commentNum, praiseNum, collectNum, focusNum);
        adapter_third.notifyData(officialNum);
    }

    @OnItemClick(R.id.list1)
    void onFirstItemClick(int position) {
        //@我的
        ClassifyMessageActivity.launch(getActivity(), 1);
//        atNum = 0;
//        EventBus.getDefault().post(1);
    }

    @OnItemClick(R.id.list2)
    void onSecondItemClick(int position) {
        if (position == 0) {
            //评论
            ClassifyMessageActivity.launch(getActivity(), 2);
//            commentNum = 0;
//            EventBus.getDefault().post(2);
        } else if (position == 1) {
            //点赞
            ClassifyMessageActivity.launch(getActivity(), 3);
//            EventBus.getDefault().post(3);
//            praiseNum = 0;
        } else if (position == 2) {
            //收藏
            ClassifyMessageActivity.launch(getActivity(), 4);
//            EventBus.getDefault().post(4);
//            collectNum = 0;
        } else if (position == 3) {
            //关注
            ClassifyMessageActivity.launch(getActivity(), 5);
//            EventBus.getDefault().post(5);
//            focusNum = 0;
        }
    }

    @OnItemClick(R.id.list3)
    void onThirdItemClick(int position) {
        if (position == 0) {
            //系统通知
            ClassifyMessageActivity.launch(getActivity(), 6);
            //保存系统通知
//            AccountInfo.getInstance().saveOfficialNum(officialNum);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    public void onEvent(ReadMsg readMsg) {
        int type = readMsg.type;
        Log.i("TAG", "------->进入");
        if (type == 1) {
            atNum = 0;
        } else if (type == 2) {
            commentNum = 0;
        } else if (type == 3) {
            praiseNum = 0;
        } else if (type == 4) {
            collectNum = 0;
        } else if (type == 5) {
            focusNum = 0;
        }
        localOfficialNum = AccountInfo.getInstance().getOfficialNum();
        adapter_first.notifyData(atNum);
        adapter_second.notifyData(commentNum, praiseNum, collectNum, focusNum);
        adapter_third.notifyData(officialNum);

    }

    public void onEvent(MsgType msgType) {
        if (msgType.type == 1) {
            atNum++;
        } else if (msgType.type == 2) {
            commentNum++;
        } else if (msgType.type == 3) {
            praiseNum++;

        } else if (msgType.type == 4) {
            collectNum++;
        } else if (msgType.type == 5) {
            focusNum++;
        } else if (msgType.type == 6) {
            officialNum++;
        }
        localOfficialNum = AccountInfo.getInstance().getOfficialNum();
        adapter_first.notifyData(atNum);
        adapter_second.notifyData(commentNum, praiseNum, collectNum, focusNum);
        adapter_third.notifyData(officialNum);
    }
}

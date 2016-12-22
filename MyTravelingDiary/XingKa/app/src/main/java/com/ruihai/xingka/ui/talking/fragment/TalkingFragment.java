package com.ruihai.xingka.ui.talking.fragment;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.ruihai.iconicfontengine.widget.IconicFontTextView;
import com.ruihai.xingka.R;
import com.ruihai.xingka.api.ApiModule;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.api.model.Index;
import com.ruihai.xingka.api.model.MessageNoReadNum;
import com.ruihai.xingka.api.model.User;
import com.ruihai.xingka.api.model.XKRepo;
import com.ruihai.xingka.ui.mine.MipcaActivityCapture;
import com.ruihai.xingka.ui.mine.MyMessageActivity;
import com.ruihai.xingka.ui.talking.AddFriendActivity;
import com.ruihai.xingka.ui.talking.FriendActivity;
import com.ruihai.xingka.ui.talking.adapter.MessageCenterAdapter;
import com.ruihai.xingka.ui.talking.adapter.SessionAdapter;
import com.ruihai.xingka.utils.Security;
import com.ruihai.xingka.widget.AppNoScrollerListView;
import com.ruihai.xingka.widget.ProgressHUD;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import de.greenrobot.event.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 咖聊主页
 * Created by lqfang on 16/8/2.
 */
public class TalkingFragment extends Fragment {


    @BindView(R.id.tv_back)
    IconicFontTextView mConctacts;
    @BindView(R.id.tv_title)
    TextView mTitle;
    @BindView(R.id.tv_right)
    IconicFontTextView mAddFriend;
    @BindView(R.id.list1)
    AppNoScrollerListView mList1;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;

    private int mType; //1:获得点击事件标识
    private boolean isIM; //是否注册云信

    private String mAccount;
    private User currentUser;

    private MessageCenterAdapter mMessage; //消息通知
    private SessionAdapter sessionAdapter; //最近会话

    String[] message = {"消息中心"};
    String[] pic_message = {"{xk-message}"};

    private MessageNoReadNum messageNoReadNum;
    private int atNum, commentNum, praiseNum, collectNum, focusNum, officialNum, localOfficialNum;

    List<RecentContact> recentslist;  //最近会话列表

    public static TalkingFragment newInstance() {
        TalkingFragment fragment = new TalkingFragment();
        Bundle args = new Bundle();
//        args.putInt("type", type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        currentUser = AccountInfo.getInstance().loadAccount();
        if (currentUser != null) {
            mAccount = String.valueOf(currentUser.getAccount());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_talking, container, false);
        ButterKnife.bind(this, view);

        getData();
        initViews();
        return view;
    }

    private void initViews() {
        mConctacts.setText("{xk-card}");
        mAddFriend.setText("{xk-attent}");
        mTitle.setText("咖聊");

//        //消息中心
//        mMessage = new MessageCenterAdapter(getActivity(), message, pic_message);
//        mList1.setAdapter(mMessage);
//        mMessage.notifyDataSetChanged();


//        NIMClient.getService(MsgService.class).queryRecentContacts()
//                .setCallback(new RequestCallbackWrapper<List<RecentContact>>() {
//                    @Override
//                    public void onResult(int code, List<RecentContact> recents, Throwable e) {
//                        // recents参数即为最近联系人列表（最近会话列表）
//                        if(code == 200){
//                            recentslist = recents;
//                            Log.e("TAG","recents-->"+recents.size());
////                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
////                            sessionAdapter = new SessionAdapter(recents);
////                            recyclerView.setAdapter(sessionAdapter);
////                            sessionAdapter.notifyDataSetChanged();
//                        }
//                    }
//                });


    }

    public void onEvent(Index index) {
        if (index.index == 2) {
            isIM = AccountInfo.getInstance().getIsIMReg();
            if (!isIM) {
                mConctacts.setVisibility(View.GONE);
                mAddFriend.setVisibility(View.GONE);
                showCreateIMUser();
            }
        }
    }


    //请求消息接口
    private void getData() {
        String account = Security.aesEncrypt(mAccount);
        String OS = Security.aesEncrypt("1");
        ApiModule.apiService_1().getPushNumMessageNoRead(account, OS).enqueue(new Callback<MessageNoReadNum>() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onResponse(Call<MessageNoReadNum> call, Response<MessageNoReadNum> response) {
                Log.i("TAG", "--------->消息请求成功" + response.message());
                messageNoReadNum = response.body();
                String msg = messageNoReadNum.getMsg();
                if (messageNoReadNum.isSuccess()) {
                    atNum = messageNoReadNum.getAtNum();
                    commentNum = messageNoReadNum.getCommentNum();
                    praiseNum = messageNoReadNum.getPraiseNum();
                    collectNum = messageNoReadNum.getCollectNum();
                    focusNum = messageNoReadNum.getFocusNum();
                    officialNum = messageNoReadNum.getOfficialNum();

                    //消息中心
                    mMessage = new MessageCenterAdapter(getActivity(), message, pic_message,
                            atNum, commentNum, praiseNum, collectNum, focusNum, officialNum);
                    mList1.setAdapter(mMessage);
                    mMessage.notifyDataSetChanged();
                    //系统消息存储到本地
                    AccountInfo.getInstance().saveOfficialNum(officialNum);

                }
            }

            @Override
            public void onFailure(Call<MessageNoReadNum> call, Throwable t) {
                ProgressHUD.showErrorMessage(getActivity(), getString(R.string.common_network_error));
            }
        });
    }

    //注册云信
    public void createIMUser() {
        String account = Security.aesEncrypt(mAccount);
        ApiModule.apiService_1().createIMUser(account).enqueue(new Callback<XKRepo>() {
            @Override
            public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                XKRepo xkRepo = response.body();
                String msg = xkRepo.getMsg();
                if (xkRepo.isSuccess()) {
                    ProgressHUD.showSuccessMessage(getActivity(), msg);
                    mConctacts.setVisibility(View.VISIBLE);
                    mAddFriend.setVisibility(View.VISIBLE);
                    AccountInfo.getInstance().saveIsIMReg(true);
                } else {
                    ProgressHUD.showInfoMessage(getActivity(), "已经注册过云信");
                    mConctacts.setVisibility(View.VISIBLE);
                    mAddFriend.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<XKRepo> call, Throwable t) {
                ProgressHUD.showErrorMessage(getActivity(), getString(R.string.common_network_error));
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
//        mMessage.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @OnItemClick(R.id.list1)
    void onItemFirstClick(int position) {
        //消息中心
        MyMessageActivity.launch(getActivity(), atNum, commentNum, praiseNum, collectNum, focusNum, officialNum);
    }

    @OnClick(R.id.tv_back)
    void onBack() {
        //好友联系人
        FriendActivity.launch(getActivity());
    }

    @OnClick(R.id.tv_right)
    void onAddFriend() {
        showChangeBGDialog();
    }

    /**
     * 云信注册弹出框
     */
    private void showCreateIMUser() {
//            final NormalDialog dialog = new NormalDialog(this);
//            dialog.setCancelable(false); // 设置点击返回键Dialog不消失
//            dialog.setCanceledOnTouchOutside(false); // 设置点击屏幕Dialog不消失
//            dialog.isTitleShow(true)
//                    .style(NormalDialog.STYLE_TWO)//
//                    .bgColor(Color.parseColor("#ffffff"))
//                    .cornerRadius(5)
//                    .title("想聊就聊,想约就约")
//                    .titleTextSize(15.0f)
//                    .titleTextColor(Color.parseColor("#333333"))
//                    .content("欢迎咖主来到「咖聊」哦，在这里可以聊天")
//                    .contentTextSize(12.0f)
//                    .btnNum(1)
//                    .btnText("免费开通")
//                    .contentTextColor(Color.parseColor("#67676f"))
//                    .dividerColor(Color.parseColor("#dcdce4"))
//                    .btnTextSize(15f)
//                    .btnTextColor(Color.parseColor("#ff7800"))
//                    .widthScale(0.85f)
//                    .show();
//
//            dialog.setOnBtnClickL(new OnBtnClickL() {
//                @Override
//                public void onBtnClick() {
//                    dialog.dismiss();
//                    createIMUser();
//                }
//            });

        // 1. 布局文件转换为View对象
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.dialog_choose_sex, null);
        final Dialog dialog = new AlertDialog.Builder(getActivity()).create();
        dialog.setCanceledOnTouchOutside(false); // 设置点击屏幕Dialog不消失
        dialog.setCancelable(false);
        dialog.show();
        dialog.getWindow().setContentView(layout);
        //2.标题
        TextView dialog_title = (TextView) layout.findViewById(R.id.tv_title);
        dialog_title.setVisibility(View.VISIBLE);
        dialog_title.setText("想聊就聊,想约就约");
        // 3. 消息内容
        //显示内容
        TextView dialog_msg = (TextView) layout.findViewById(R.id.umeng_update_content);
        dialog_msg.setText("欢迎咖主来到「咖聊」哦，在这里可以聊天");

        // 4. 确定按钮
        Button btnOK = (Button) layout.findViewById(R.id.umeng_update_id_ok);
        btnOK.setText("免费开通");
        btnOK.setTextColor(Color.parseColor("#ff7800"));
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createIMUser();
                dialog.dismiss();
            }
        });

        // 5. 取消按钮
        Button btnCancel = (Button) layout.findViewById(R.id.umeng_update_id_cancel);
        btnCancel.setText("取消");
        btnCancel.setTextColor(Color.parseColor("#ff7800"));
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }


    private void showChangeBGDialog() {//添加好友
        final String[] stringItems = {"添加好友", "发起群聊", "扫一扫"};
        final ActionSheetDialog dialog = new ActionSheetDialog(getActivity(), stringItems, null);
        //弹出框背景色为白色
        dialog.titleBgColor(Color.parseColor("#ffffff"))
                .lvBgColor(Color.parseColor("#ffffff"));
        dialog.title("添加好友")
                .titleTextSize_SP(13f)
                .titleHeight(25f)
                .titleTextColor(Color.parseColor("#aeaeb3"))
                .dividerColor(Color.parseColor("#ffffff"))
                .isTitleShow(false);
        dialog.itemTextColor(Color.parseColor("#46464c"))
                .itemTextSize(15.5f)
                .itemHeight(40f)
                .dividerColor(Color.parseColor("#bcbcc4"));
        dialog.cancelText(Color.parseColor("#46464c"))
                .cancelTextSize(15.5f)
                .show();
        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (position == 0) {
                    //添加好友
                    AddFriendActivity.launch(getActivity());
                } else if (position == 1) {
                    //发起群聊

                } else if (position == 2) {
                    //扫一扫
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), MipcaActivityCapture.class);
                    startActivity(intent);
                }
                dialog.dismiss();
            }
        });
    }
}

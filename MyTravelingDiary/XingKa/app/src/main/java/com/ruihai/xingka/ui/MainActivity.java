package com.ruihai.xingka.ui;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.orhanobut.logger.Logger;
import com.ruihai.xingka.R;
import com.ruihai.xingka.XKApplication;
import com.ruihai.xingka.api.ApiModule;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.api.model.DoubleClickModel;
import com.ruihai.xingka.api.model.Index;
import com.ruihai.xingka.api.model.MessageNoReadNum;
import com.ruihai.xingka.api.model.MsgType;
import com.ruihai.xingka.api.model.ReadMsg;
import com.ruihai.xingka.api.model.User;
import com.ruihai.xingka.api.model.UserCarInfo;
import com.ruihai.xingka.api.model.UserRepo;
import com.ruihai.xingka.api.model.UserTag;
import com.ruihai.xingka.api.model.XKRepo;
import com.ruihai.xingka.event.DisableUser;
import com.ruihai.xingka.event.OnDoubleClickListener;
import com.ruihai.xingka.event.OnSingleClickListener;
import com.ruihai.xingka.receiver.MyReceiver;
import com.ruihai.xingka.ui.caption.fragment.CaptionFragment;
import com.ruihai.xingka.ui.caption.publisher.PublishManager;
import com.ruihai.xingka.ui.login.LoginActivity;
import com.ruihai.xingka.ui.mine.ClassifyMessageActivity;
import com.ruihai.xingka.ui.mine.fragment.MessageCenterFragmente;
import com.ruihai.xingka.ui.mine.fragment.MyUserFragment;
import com.ruihai.xingka.ui.mine.fragment.UserProfileFragment;
import com.ruihai.xingka.ui.piazza.PiazzaFragment;
import com.ruihai.xingka.ui.talking.fragment.TalkingFragment;
import com.ruihai.xingka.ui.trackway.fragment.TrackwayFragment;
import com.ruihai.xingka.utils.AppUtility;
import com.ruihai.xingka.utils.Security;
import com.ruihai.xingka.widget.ProgressHUD;
import com.umeng.onlineconfig.OnlineConfigAgent;
import com.umeng.update.UmengUpdateAgent;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bingoogolapple.badgeview.BGABadgeRadioButton;
import de.greenrobot.event.EventBus;
import jp.wasabeef.blurry.Blurry;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity implements MoreDialogFragment.OnFragmentInteractionListener {
    public static void launch(Context from, int flag) {
        Intent intent = new Intent(from, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("flag", flag);
        from.startActivity(intent);
    }

    public static final String ACTION_NOTIFICATION = "com.ruihai.xingka.ACTION_NOTIFICATION";

    @BindView(R.id.rg_main_tabbar)
    RadioGroup mTabRadioGroup;
    @BindView(R.id.iv_main_plus)
    ImageView mPlusImageView;
    @BindView(R.id.main_layout)
    FrameLayout rootView;
    // 成员（实例）变量名称前加m前缀
    private CaptionFragment mCaptionFragment;
    private TrackwayFragment mTrackwayFragment;
    private PiazzaFragment mPiazzaFragment;
    private MessageCenterFragmente mMessageFragment;
    private TalkingFragment mTalkingFragment;
    private UserProfileFragment mMineFragment;
    private MyUserFragment mMyFragment;

    private Fragment[] mFragments;
    private String mUserAccount;
    private int mFlag;
    private int index;
    public static int currentTabIndex; // 当前fragment的index
    private User mCurrentUser;
    public static boolean isForeground = false;
    private MyReceiver myReceiver;
    private IntentFilter intentFilter;
    private Timer mTimer;
    private EventBus eventBus = EventBus.getDefault();
    private int mFlog;
    private MessageNoReadNum messageNoReadNum;
    private int atNum, commentNum, praiseNum, collectNum, focusNum, officialNum, localOfficialNum;

    @Nullable
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // 请求在线参数
        OnlineConfigAgent.getInstance().updateOnlineConfig(mContext);
        // 默认在wifi环境下才进行自动更新提醒，如要在任意网络下都进行更新提醒，则加入如下代码。
        // UmengUpdateAgent.setUpdateOnlyWifi(false);
        // 调用更新接口，如果处于wifi环境则检测更新，如果有更新，弹出对话框提示有新版本，用户点选更新开始下载更新。
        UmengUpdateAgent.update(MainActivity.this);
        if (currentUser != null) {
            mUserAccount = String.valueOf(currentUser.getAccount());
        }
        // 判断用户是否登录,如果没有则跳转至登录页
        if (!AccountInfo.getInstance().isLogin()) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        } else {
            // 是否展示开屏页,默认开启,当从登录页进入不做显示
            boolean isLaunchSplash = getIntent().getBooleanExtra("launchSplash", true);
            if (isLaunchSplash) {
                showSplashDialog();
                updateUserAccount();
            }
            mCurrentUser = AccountInfo.getInstance().loadAccount();
            addChildFragments();
            // init timer
            mTimer = new Timer();
            // start timer task
            setTimerTask();
        }

        eventBus.register(this);
        myReceiver = new MyReceiver();
        intentFilter = new IntentFilter(PublishManager.ACTION_PUBLISH_FAILED);
        registerReceiver(myReceiver, intentFilter); // 注册广播

        String action = getIntent() != null ? getIntent().getAction() : null;
        mFlog = getIntent().getIntExtra("type", 0);
        if (ACTION_NOTIFICATION.equals(action)) {
            if (mCurrentUser != null) {
                ClassifyMessageActivity.launch(this, mFlog);
            }
        }

        final View child = findViewById(R.id.rl_add_click);
        final View parent = findViewById(R.id.rg_main_tabbar);

        parent.post(new Runnable() {
            @Override
            public void run() {
                Rect rc = new Rect();
                child.getHitRect(rc); //如果直接在onCreate函数中执行本函数，会获取rect失败，因为此时UI界面尚未开始绘制，无法获得正确的坐标
                rc.left -= 20;
                rc.top -= 50;
                rc.right += 20;
                rc.bottom += 50;
                parent.setTouchDelegate(new TouchDelegate(rc, child));

            }
        });

        child.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        if (!TextUtils.isEmpty(mUserAccount)) {
            getData();
        }


    }

    private void addChildFragments() {
        mCaptionFragment = new CaptionFragment();
        mTrackwayFragment = TrackwayFragment.newInstance(mUserAccount);
//        mMessageFragment = MessageCenterFragmente.newInstance();
        mTalkingFragment = TalkingFragment.newInstance();
        mMyFragment = MyUserFragment.newInstance(String.valueOf(mCurrentUser.getAccount()), 1);
        mFragments = new Fragment[]{
                mCaptionFragment,
                mTrackwayFragment,
                mTalkingFragment,
//                mMessageFragment,
                mMyFragment
        };

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, mCaptionFragment)
                .add(R.id.fragment_container, mTrackwayFragment)
                .add(R.id.fragment_container, mTalkingFragment)
                .add(R.id.fragment_container, mMyFragment)
                .show(mCaptionFragment)
                .hide(mTrackwayFragment)
                .hide(mTalkingFragment)
                .hide(mMyFragment)
                .commit();


        currentTabIndex = 0;
//        BGABadgeRadioButton rab = (BGABadgeRadioButton) mTabRadioGroup.getChildAt(0);
//        rab.setChecked(true);
        initListener();
    }

    private void showSplashDialog() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        SplashDialogFragment fragment = SplashDialogFragment.newInstance();
//        fragment.show(fm, "fragment_splash");
        ft.add(fragment, "fragment_splash");
        ft.commitAllowingStateLoss();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mFlag = intent.getIntExtra("flag", 0);
        if (mFlag == 1) {//我的
            BGABadgeRadioButton rab = (BGABadgeRadioButton) mTabRadioGroup.getChildAt(4);
            rab.setChecked(true);
        }
        if (mFlag == 2) { // 旅拼
            BGABadgeRadioButton rab = (BGABadgeRadioButton) mTabRadioGroup.getChildAt(1);
            rab.setChecked(true);
        }
        if (mFlag == 3) { //咖聊
            BGABadgeRadioButton rab = (BGABadgeRadioButton) mTabRadioGroup.getChildAt(0);
            rab.setChecked(true);
        }

        String action = intent != null ? intent.getAction() : null;
        mFlog = intent.getIntExtra("type", 0);
        if (ACTION_NOTIFICATION.equals(action)) {
            ClassifyMessageActivity.launch(this, mFlog);
        }
    }

    private void initListener() {
        mTabRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Resources r = getBaseContext().getResources();
                localOfficialNum = AccountInfo.getInstance().getOfficialNum();
                Log.i("TAG", "--------->消息请求成功" + atNum + "--" + commentNum + "---" + praiseNum + "----" + collectNum + "---" + focusNum + "---" + officialNum + "---" + localOfficialNum);
                if (checkedId == R.id.brb_main_message) {
                    Drawable drawableTop = r.getDrawable(R.mipmap.tab_message_checked);
                    BGABadgeRadioButton rab = (BGABadgeRadioButton) mTabRadioGroup.getChildAt(3);
                    rab.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop, null, null);
                } else {
                    if (atNum > 0 || commentNum > 0 || praiseNum > 0 ||
                            collectNum > 0 || focusNum > 0 || officialNum - localOfficialNum > 0) {
                        Drawable drawableTop = r.getDrawable(R.mipmap.tab_message_noread);
                        BGABadgeRadioButton rab = (BGABadgeRadioButton) mTabRadioGroup.getChildAt(3);
                        rab.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop, null, null);
                    } else if (atNum == 0 || commentNum == 0 || praiseNum == 0 ||
                            collectNum == 0 || focusNum == 0 || officialNum - localOfficialNum == 0) {
                        Drawable drawableTop = r.getDrawable(R.mipmap.tab_message_normal);
                        BGABadgeRadioButton rab = (BGABadgeRadioButton) mTabRadioGroup.getChildAt(3);
                        rab.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop, null, null);
                    }
                }

                if (checkedId == R.id.brb_main_caption) {
                    index = 0;
                } else if (checkedId == R.id.brb_main_trackway) {
                    index = 1;
                } else if (checkedId == R.id.brb_main_message) {
                    index = 2;
                } else if (checkedId == R.id.brb_main_mine) {
                    index = 3;

                }

                Index index1 = new Index(index);
                EventBus.getDefault().post(index1);

                if (currentTabIndex != index) {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.hide(mFragments[currentTabIndex]);
                    if (!mFragments[index].isAdded()) {
                        transaction.add(R.id.fragment_container, mFragments[index]);
                    }
                    transaction.show(mFragments[index]).commit();
                }
                currentTabIndex = index;
            }
        });

        BGABadgeRadioButton captionRab = (BGABadgeRadioButton) mTabRadioGroup.getChildAt(0);
        captionRab.setOnClickListener(new OnDoubleClickListener() {
            @Override
            public void onDoubleClick(View v) {
                DoubleClickModel data = new DoubleClickModel(1, true);
                EventBus.getDefault().post(data);
            }
        });
        BGABadgeRadioButton travelRab = (BGABadgeRadioButton) mTabRadioGroup.getChildAt(1);
        travelRab.setOnClickListener(new OnDoubleClickListener() {
            @Override
            public void onDoubleClick(View v) {
                DoubleClickModel data = new DoubleClickModel(2, true);
                EventBus.getDefault().post(data);
            }
        });
        /**
         * @功能描述 : 监听+号点击事件
         */
        mPlusImageView.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Blurry.with(mContext).radius(10).sampling(8).animate(0).onto(rootView);
                showDialog();
            }
        });
    }


    /**
     * 点击返回按钮键时
     */
    private boolean backPressedToExitOnce = false;

    @Override
    public void onBackPressed() {
        if (mTrackwayFragment.moreContentPW != null && mTrackwayFragment.moreContentPW.isShowing()) {
            mTrackwayFragment.moreContentPW.dismiss();
            return;
        }
        if (backPressedToExitOnce) {
            super.onBackPressed();
            // moveTaskToBack(true);
        } else {
            this.backPressedToExitOnce = true;
            AppUtility.showToast("再次点击退出应用");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    backPressedToExitOnce = false;
                }
            }, 2000);
        }

    }

    void showDialog() {
        MoreDialogFragment newFragment = MoreDialogFragment.newInstance();
        newFragment.show(getSupportFragmentManager(), "dialog");
    }

    @Override
    public void onFragmentInteraction() {
        Blurry.delete(rootView);
    }

    @Override
    protected void onResume() {
        isForeground = true;
        super.onResume();
    }

    @Override
    protected void onPause() {
        isForeground = true;
        super.onPause();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myReceiver); // 取消注册
        eventBus.unregister(this);
        if (mTimer != null) {
            mTimer.cancel();
        }
    }

    private void setTimerTask() {
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateLastAliveTime();
            }
        }, 1000, 1000 * 60 * 10  /* 表示1000毫秒之後，每隔10分钟執行一次 */);
    }

    public void updateLastAliveTime() {
        String aesAccount = Security.aesEncrypt(String.valueOf(mCurrentUser.getAccount()));
        ApiModule.apiService().editLastAliveTime(aesAccount).enqueue(new Callback<XKRepo>() {
            @Override
            public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                Logger.d("---> 更新成功");
            }

            @Override
            public void onFailure(Call<XKRepo> call, Throwable t) {
                Logger.d("---> 更新失败");
            }

        });
    }

    public void onEvent(DisableUser event) {
        Logger.d("...我要被退出了...");
        AccountInfo.getInstance().clearAccount();

        final NormalDialog dialog = new NormalDialog(mContext);
        dialog.setCancelable(false); // 设置点击返回键Dialog不消失
        dialog.setCanceledOnTouchOutside(false); // 设置点击屏幕Dialog不消失
        dialog.isTitleShow(false)
                .bgColor(Color.parseColor("#ffffff"))
                .cornerRadius(5)
                .content("您的账号涉嫌违规操作,已被后台禁用,如有问题请联系客服QQ:2015686190")
                .btnNum(1)
                .btnText("确定")
                .contentGravity(Gravity.CENTER)
                .contentTextColor(Color.parseColor("#33333d"))
                .dividerColor(Color.parseColor("#dcdce4"))
                .btnTextSize(15.5f, 15.5f)
                .btnTextColor(Color.parseColor("#ff2814"), Color.parseColor("#0077fe"))
                .widthScale(0.85f)
                .show();
          dialog.setOnBtnClickL(new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                logout();
                dialog.dismiss();
            }
        });
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
        BGABadgeRadioButton rab = (BGABadgeRadioButton) mTabRadioGroup.getChildAt(3);
        if (!rab.isChecked()) {
            if (atNum > 0 || commentNum > 0 || praiseNum > 0 ||
                    collectNum > 0 || focusNum > 0 || officialNum - localOfficialNum > 0) {
                Resources r = getBaseContext().getResources();
                Drawable drawableTop = r.getDrawable(R.mipmap.tab_message_noread);
                rab.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop, null, null);
            } else if (atNum == 0 || commentNum == 0 || praiseNum == 0 ||
                    collectNum == 0 || focusNum == 0 || officialNum - localOfficialNum == 0) {
                Resources r = getBaseContext().getResources();
                Drawable drawableTop = r.getDrawable(R.mipmap.tab_message_normal);
                rab.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop, null, null);
            }
        }

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
        BGABadgeRadioButton rab = (BGABadgeRadioButton) mTabRadioGroup.getChildAt(3);
        if (!rab.isChecked()) {
            if (atNum > 0 || commentNum > 0 || praiseNum > 0 ||
                    collectNum > 0 || focusNum > 0 || officialNum - localOfficialNum > 0) {
                Resources r = getBaseContext().getResources();
                Drawable drawableTop = r.getDrawable(R.mipmap.tab_message_noread);
                rab.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop, null, null);
            } else if (atNum == 0 || commentNum == 0 || praiseNum == 0 ||
                    collectNum == 0 || focusNum == 0 || officialNum - localOfficialNum == 0) {
                Resources r = getBaseContext().getResources();
                Drawable drawableTop = r.getDrawable(R.mipmap.tab_message_normal);
                rab.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop, null, null);
            }
        }
    }

    private void logout() {
        AccountInfo.getInstance().clearAccount();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        XKApplication.getInstance().exit();
    }

    //请求消息接口
    private void getData() {
        String account = Security.aesEncrypt(mUserAccount);
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
                    localOfficialNum = AccountInfo.getInstance().getOfficialNum();

                    if (atNum > 0 || commentNum > 0 || praiseNum > 0 ||
                            collectNum > 0 || focusNum > 0 || officialNum - localOfficialNum > 0) {
                        BGABadgeRadioButton rab = (BGABadgeRadioButton) mTabRadioGroup.getChildAt(3);
                        Resources r = getBaseContext().getResources();
                        Drawable drawableTop = r.getDrawable(R.mipmap.tab_message_noread);
                        rab.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop, null, null);
                    } else if (atNum == 0 || commentNum == 0 || praiseNum == 0 ||
                            collectNum == 0 || focusNum == 0 || officialNum - localOfficialNum == 0) {

                    }

                }
            }

            @Override
            public void onFailure(Call<MessageNoReadNum> call, Throwable t) {
                ProgressHUD.showErrorMessage(MainActivity.this, getString(R.string.common_network_error));
            }
        });
    }

    private void updateUserAccount() {
        String equipment = android.os.Build.MODEL;
        String password = AccountInfo.getInstance().loadPassWord();
        String version = Build.VERSION.RELEASE;
        String appVersion = AppUtility.getAppVersionName();
        String sAccount = Security.aesEncrypt(mUserAccount);
        String sOS = Security.aesEncrypt("1");
//        Log.e("系统", sOS);
        String sEquipment = Security.aesEncrypt(equipment);
//        Log.e("设备", equipment);
        // 行咖号登录默认+86
        String sCountryNum = Security.aesEncrypt("");
        String sVersion = Security.aesEncrypt(version);
        String sAppVersion = Security.aesEncrypt(appVersion);


        Call<UserRepo> call = ApiModule.apiService_1().userLogin4(sAccount, password, sOS, sEquipment, sCountryNum, sVersion, sAppVersion);
        call.enqueue(new Callback<UserRepo>() {
            @Override
            public void onResponse(Call<UserRepo> call, Response<UserRepo> response) {
                UserRepo userRepo = response.body();
                String msg = userRepo.getMsg();
                if (userRepo.isSuccess()) { // 登录成功
                    User user = userRepo.getUserInfo();
                    List<UserTag> userTags = userRepo.getUserTags();
                    UserCarInfo userCarInfo = userRepo.getUserCarInfo();
                    // 保存用户基本数据到本地
                    AccountInfo.getInstance().saveAccount(user);
                    AccountInfo.getInstance().saveUserTags(userTags);
                    AccountInfo.getInstance().saveUserCarInfo(userCarInfo);

                } else { // 登录失败
                    ProgressHUD.showErrorMessage(MainActivity.this, msg, new ProgressHUD.SimpleHUDCallback() {
                        @Override
                        public void onSimpleHUDDismissed() {
                            logout();
                        }
                    });
                    //startActivity(new Intent(getActivity(), LoginActivity.class));
                    //getActivity().finish();
                }
            }

            @Override
            public void onFailure(Call<UserRepo> call, Throwable t) {
                //if (!CaptionListFragment.this.isDetached())
                //ProgressHUD.showErrorMessage(getContext(), getString(R.string.common_network_error));
            }
        });
    }


}
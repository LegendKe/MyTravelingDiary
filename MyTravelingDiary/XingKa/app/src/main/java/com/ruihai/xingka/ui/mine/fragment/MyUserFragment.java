package com.ruihai.xingka.ui.mine.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.alibaba.sdk.android.feedback.util.IWxCallback;
import com.bumptech.glide.Glide;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.orhanobut.logger.Logger;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.ruihai.iconicfontengine.widget.IconicFontTextView;
import com.ruihai.xingka.Global;
import com.ruihai.xingka.R;
import com.ruihai.xingka.XKApplication;
import com.ruihai.xingka.api.ApiModule;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.api.model.SignInRepo;
import com.ruihai.xingka.api.model.User;
import com.ruihai.xingka.api.model.UserCard;
import com.ruihai.xingka.api.model.UserCounts;
import com.ruihai.xingka.api.model.XKRepo;
import com.ruihai.xingka.event.UpdateCountEvent;
import com.ruihai.xingka.ui.common.enter.EmojiUtils;
import com.ruihai.xingka.ui.login.LoginActivity;
import com.ruihai.xingka.ui.mine.AddFriendsActivity;
import com.ruihai.xingka.ui.mine.CropBgImageActivity;
import com.ruihai.xingka.ui.mine.EditUserDataActivity;
import com.ruihai.xingka.ui.mine.FollowAndFansActivity;
import com.ruihai.xingka.ui.mine.FriendsActivity;
import com.ruihai.xingka.ui.mine.IntegralWebActivity;
import com.ruihai.xingka.ui.mine.PraiseActivity;
import com.ruihai.xingka.ui.mine.SettingActivity;
import com.ruihai.xingka.ui.mine.ShareCardActivity;
import com.ruihai.xingka.ui.mine.UserCaptionActivity;
import com.ruihai.xingka.ui.mine.UserCollectionActivity;
import com.ruihai.xingka.ui.mine.adapter.MyUserFivthAdapter;
import com.ruihai.xingka.ui.mine.adapter.MyUserFourthAdapter;
import com.ruihai.xingka.ui.mine.adapter.MyUserSecondAdapter;
import com.ruihai.xingka.ui.mine.adapter.MyUserThirdAdapter;
import com.ruihai.xingka.ui.mine.adapter.UserProfilePagerAdapter;
import com.ruihai.xingka.utils.AppUtility;
import com.ruihai.xingka.utils.QiniuHelper;
import com.ruihai.xingka.utils.Security;
import com.ruihai.xingka.widget.AppNoScrollerListView;
import com.ruihai.xingka.widget.JavaBlurProcess;
import com.ruihai.xingka.widget.ProgressHUD;
import com.ruihai.xingka.widget.SharePopWindow;
import com.ruihai.xingka.widget.pulltozoomview.PullToZoomBase;
import com.ruihai.xingka.widget.pulltozoomview.PullToZoomScrollViewEx;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mac on 16/4/1.
 */
public class MyUserFragment extends Fragment implements View.OnClickListener {
    private static final String BLURRED_IMG_PATH = "blurred_image.png";
    private static final String ARG_LAST_SCROLL_Y = "arg.LastScrollY";
    public final int RESULT_REQUEST_PHOTO = 0x1005;
    public final int RESULT_REQUEST_PHOTO_CROP = 0x1006;
    public final int RESULT_REQUEST_BACKGROUD = 0x1007;
    public final int RESULT_REQUEST_BACKGROUD_CROP = 0x1008;
    public static final String DEFAULT_BACKGROUND_KEY = "00000000-0000-0000-0000-000000000000";
    public static final String INTEGRAL_GUID = "33333333-3333-3333-3333-333333333333";
    @BindView(R.id.scroll_view)
    PullToZoomScrollViewEx mZoomRecyclerview;
    // @Bind(R.id.user_info_layout)
    RelativeLayout userInfoLayout;
    @BindView(R.id.head_tv_share)
    IconicFontTextView mShareButton;
    @BindView(R.id.head_tv_sign_in)
    IconicFontTextView mLeftButton;
    @BindView(R.id.ll_sing_in)
    LinearLayout mSignInLayout;
    @BindView(R.id.tv_sing_in)
    TextView mSignInText;
    @BindView(R.id.img_btn2)
    RelativeLayout bglayout;
    @BindView(R.id.root_layout)
    RelativeLayout rootLayout;

    //背景墙图片
    // @Bind(R.id.bg_image)
    ImageView bgImageview;
    // 用户信息展示控件
    //@Bind(R.id.sdv_avatar)
    ImageView mAvatarView;
    //// @Bind(R.id.iv_sex)
    ImageView mSex;
    // @Bind(R.id.tv_note)
    TextView mNote;
    // @Bind(R.id.tv_nickname)
    TextView mNickName;
    //  @Bind(R.id.tv_xingka_number)
    TextView mXingkaNumber;
    // @Bind(R.id.tv_xingka_numbername)
    TextView mXingKaNoTv;
    // @Bind(R.id.tv_officalaccount)
    TextView officalBtn;
    // @Bind(R.id.tv_myfriend)
    TextView mFriend;
    // @Bind(R.id.zan_layout)
//    LinearLayout zanLayout;
    // @Bind(R.id.tv_zan)
    TextView zanTextview;
    // @Bind(R.id.tv_collection)
    TextView collectTextview;
    // @Bind(R.id.tv_fans)
    TextView fansTextview;
    // @Bind(R.id.tv_follow)
    TextView followTextview;
    // @Bind(R.id.menu_layout)
    FrameLayout menuLayout;
    // @Bind(R.id.iv_logo)
    FrameLayout avatarLayout;
    // @Bind(R.id.fans_follow_layout)
    LinearLayout fansfollowLayout;
    View mFirstView;

    public SharePopWindow sharePopWindow;

    AppNoScrollerListView mList1;
    AppNoScrollerListView mList2;
    AppNoScrollerListView mList3;
    AppNoScrollerListView mList4;
    AppNoScrollerListView mList5;

    //默认的一句话说说的内容
    private final String DEFAULT_MYTALK = "有点害羞，不知道说什么O(∩_∩)O~";
    private String avatar; // 当前名片头像
    private int sex; // 当前名片性别
    private String nick; // 当前名片昵称
    private String address; // 当前名片用户地址
    private String remark; // 当前名片用户备注名
    private int nexus; // 被查看人是查看人的什么关系
    private boolean isOfficial;//是否官方账号
    private boolean isSignIn;//今日是否签到
    private int totalIntegral;//总积分

    private boolean isIMReg; //是否注册云信

    private User mCurrentUser; // 我的信息类
    private String mMyAccount; // 我的行咖号
    private String mUserAccount; // 名片行咖号
    private boolean isMine = false; // 当前是否为自己的主页
    private LayoutInflater inflater;
    private UserCard mUserCard;//用户名片数据
    private int mFlag;
    private UserProfilePagerAdapter mAdapter;
    private List<BaseScrollFragment> mFragments = new ArrayList<>();
    private Uri fileUri;
    private Uri fileCropUri;
    private UploadManager uploadManager;

    private AnimationDrawable animationDrawable;

    private int feedNum; //意见反馈未读数

    Bitmap overlay;
    JavaBlurProcess process = new JavaBlurProcess();

    String[] data_first = {"消息中心"};
    //    String[] data_second = {"我的发布", "我的收藏", "草稿箱"};
    String[] data_second = {"我的发布", "我的收藏"};
    String[] data_third = {"我的资料"};
    String[] data_fourth = {"积分乐园"};
    //    String[] data_fiveth = {"找咖友", "意见反馈", "设置"};
    String[] data_fiveth = {"意见反馈", "找咖友", "设置"};

    String[] pic_first = {"{xk-message}"};
    //    String[] pic_second = {"{xk-mycaption}", "{xk-collect}", "{xk-draftbox}"};
    String[] pic_second = {"{xk-mycaption}", "{xk-collect}"};
    String[] pic_third = {"{xk-mydata}"};
    String[] pic_fourth = {"{xk-integral}"};
    //    String[] pic_fiveth = {"{xk-addfriend}", "{xk-feedback}", "{xk-setting}"};
    String[] pic_fiveth = {"{xk-feedback}", "{xk-addfriend}", "{xk-setting}"};
    private MyUserFourthAdapter adapter_fourth;
    private MyUserFivthAdapter adapter_fivth;

    private Handler handler = new Handler(Looper.getMainLooper());

    public static MyUserFragment newInstance(String userAccount, int flag) {
        Bundle args = new Bundle();
        MyUserFragment fragment = new MyUserFragment();
        args.putInt("FLAG", flag);//0:MyUserFragment  1:MainActivity
        args.putString("userAccount", userAccount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserAccount = getArguments().getString("userAccount");
        mFlag = getArguments().getInt("FLAG", 0);
        mCurrentUser = AccountInfo.getInstance().loadAccount();
        mMyAccount = String.valueOf(mCurrentUser.getAccount());
        if (mMyAccount.equals(mUserAccount)) {
            isMine = true;
        } else {
            isMine = false;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        final View view = inflater.inflate(R.layout.fragment_myuser_profile, container, false);
        ButterKnife.bind(this, view);

        final View headView = LayoutInflater.from(getActivity()).inflate(R.layout.user_header, null, false);
        final View zoomView = LayoutInflater.from(getActivity()).inflate(R.layout.user_zoom, null, false);
        final View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.user_content, null, false);
        //给可缩放头部设置属性
        mZoomRecyclerview.setParallax(false);
        final int mScreenHeight = AppUtility.getScreenHeight();
        final int mScreenWidth = AppUtility.getScreenWidth();
        LinearLayout.LayoutParams localObject = new LinearLayout.LayoutParams(mScreenWidth, (int) (6.8F * (mScreenHeight / 16.0F)));
        mZoomRecyclerview.setHeaderLayoutParams(localObject);

        mZoomRecyclerview.setHeaderView(headView);
        mZoomRecyclerview.setZoomView(zoomView);
        mZoomRecyclerview.setScrollContentView(contentView);

        // 获取组件
        userInfoLayout = (RelativeLayout) headView.findViewById(R.id.user_info_layout);
        mAvatarView = (ImageView) headView.findViewById(R.id.sdv_avatar);
        mSex = (ImageView) headView.findViewById(R.id.iv_sex);
        mNote = (TextView) headView.findViewById(R.id.tv_note);
        mNickName = (TextView) headView.findViewById(R.id.tv_nickname);
        mXingkaNumber = (TextView) headView.findViewById(R.id.tv_xingka_number);
        mXingKaNoTv = (TextView) headView.findViewById(R.id.tv_xingka_numbername);
        officalBtn = (TextView) headView.findViewById(R.id.tv_officalaccount);
        mFriend = (TextView) headView.findViewById(R.id.tv_fans);
        fansfollowLayout = (LinearLayout) headView.findViewById(R.id.fans_follow_layout);
        zanTextview = (TextView) headView.findViewById(R.id.tv_zan);
        fansTextview = (TextView) headView.findViewById(R.id.tv_follow);
        followTextview = (TextView) headView.findViewById(R.id.tv_trackway);
        avatarLayout = (FrameLayout) headView.findViewById(R.id.iv_logo);
        View view1 = (View) headView.findViewById(R.id.view1);

        mShareButton.setVisibility(View.VISIBLE);
        mLeftButton.setVisibility(View.VISIBLE);
        mSignInLayout.setVisibility(View.VISIBLE);
        mFriend.setVisibility(View.GONE);
        view1.setVisibility(View.GONE);

        bgImageview = (ImageView) zoomView.findViewById(R.id.bg_image);

        mList1 = (AppNoScrollerListView) contentView.findViewById(R.id.list1);
        mList2 = (AppNoScrollerListView) contentView.findViewById(R.id.list2);
        mList3 = (AppNoScrollerListView) contentView.findViewById(R.id.list3);
        mList4 = (AppNoScrollerListView) contentView.findViewById(R.id.list4);
        mList5 = (AppNoScrollerListView) contentView.findViewById(R.id.list5);
        mFirstView = (View) contentView.findViewById(R.id.view_first);

        userInfoLayout.setOnClickListener(this);
        mAvatarView.setOnClickListener(this);
        mShareButton.setOnClickListener(this);
        mFriend.setOnClickListener(this);
        zanTextview.setOnClickListener(this);
//        collectTextview.setOnClickListener(this);
        fansTextview.setOnClickListener(this);
        followTextview.setOnClickListener(this);
        mSignInLayout.setOnClickListener(this);

        //我的发布,收藏,草稿箱
        mList2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (position == 0) { //图说和旅拼的发布
                    UserCaptionActivity.launch(getActivity(), mMyAccount);
                } else if (position == 1) { //图说和旅拼的收藏
                    UserCollectionActivity.launch(getActivity());
                }
            }
        });
        //我的资料
        mList3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                EditUserDataActivity.launch(getActivity());
            }
        });
        //积分乐园
        mList4.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                try {
                    String sAccount = URLEncoder.encode(Security.aesEncrypt(mMyAccount).trim(), "utf-8");
                    String webUrl = String.format(Global.INTEGRAL_URL, sAccount);
                    IntegralWebActivity.launch(getActivity(), webUrl, INTEGRAL_GUID);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }
        });
        //加好友,意见反馈,设置
        mList5.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (position == 0) { //意见反馈
//                    FeedbackActivity.launch(getActivity());

                    FeedbackAPI.openFeedbackActivity(getActivity()); //进入意见反馈

                    //可选功能，第二个参数是当前登录的openim账号，如果是匿名账号方式使用，则可以传空的。返回的未读数在onsuccess接口数组中第一个元素，直接转成Integer就可以。
                    FeedbackAPI.getFeedbackUnreadCount(getActivity(), "", new IWxCallback() {
                        @Override
                        public void onSuccess(final Object... result) {
                            if (result != null && result.length == 1 && result[0] instanceof Integer) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        int count = (Integer) result[0];
                                        feedNum = count;
                                        Log.e("TAG", "意见反馈未读数-->" + feedNum);
                                        adapter_fivth = new MyUserFivthAdapter(getActivity(), data_fiveth, pic_fiveth, feedNum);
                                        mList5.setAdapter(adapter_fivth);
                                    }
                                });
                            }
                        }

                        @Override
                        public void onError(int i, String s) {
                        }

                        @Override
                        public void onProgress(int i) {

                        }
                    });

                    String avatar1 = QiniuHelper.getThumbnail96Url(avatar);//获取当前登录用户头像

                    //可以设置反馈消息自定义参数，方便在反馈后台查看自定义数据，参数是json对象，里面所有的数据都可以由开发者自定义
                    Map<String, String> uiCustomInfoMap = new HashMap<>();
                    uiCustomInfoMap.put("avatar", avatar1); //当前登录账号的头像
                    uiCustomInfoMap.put("bgColor", "#45d75e"); //消息气泡背景色
                    uiCustomInfoMap.put("color", "#ffffff");  //消息内容文字颜色
                    uiCustomInfoMap.put("pageTitle", "意见反馈"); //Web容器标题
//                    uiCustomInfoMap.put("hideLoginSuccess", true);
                    FeedbackAPI.setUICustomInfo(uiCustomInfoMap);

                    //设置自定义联系方式
                    //@param customContact  自定义联系方式
                    //@param hideContactView 是否隐藏联系人设置界面
                    FeedbackAPI.setCustomContact("", false);

//                    //自定义参数演示
//                    JSONObject jsonObject = new JSONObject();
//                    try {
//                        jsonObject.put("hideLoginSuccess", true); //隐藏登录成功的toast
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    FeedbackAPI.setAppExtInfo(jsonObject);

                } else if (position == 1) { //加好友
                    AddFriendsActivity.launch(getActivity());
                } else if (position == 2) { //设置
                    SettingActivity.launch(getActivity());
                }
            }
        });

        if (isMine && mFlag == 1) {
            bglayout.setVisibility(View.VISIBLE);
            mShareButton.setVisibility(View.VISIBLE);
            mLeftButton.setVisibility(View.VISIBLE);
            mSignInLayout.setVisibility(View.VISIBLE);

            MyUserSecondAdapter adapter_second = new MyUserSecondAdapter(getActivity(), data_second, pic_second, 2);
            MyUserThirdAdapter adapter_third = new MyUserThirdAdapter(getActivity(), data_third, pic_third);
            adapter_fourth = new MyUserFourthAdapter(getActivity(), data_fourth, pic_fourth, String.valueOf(totalIntegral));
            adapter_fivth = new MyUserFivthAdapter(getActivity(), data_fiveth, pic_fiveth, feedNum);
            mList2.setAdapter(adapter_second);
            mList3.setAdapter(adapter_third);
            mList4.setAdapter(adapter_fourth);
            mList5.setAdapter(adapter_fivth);
            adapter_second.notifyDataSetChanged();
            adapter_third.notifyDataSetChanged();
            adapter_fourth.notifyDataSetChanged();
//            adapter_fivth.notifyDataSetChanged();
        }

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.user_info_layout: {//更换背景
                showChangeBGDialog();
                break;
            }
            case R.id.tv_zan: {//赞
                PraiseActivity.launch(getActivity(), mMyAccount);
                break;
            }
            case R.id.tv_follow: {//粉丝
                FollowAndFansActivity.launch(getActivity(), mUserAccount, 2);
                break;
            }
            case R.id.tv_trackway: {//关注
                FollowAndFansActivity.launch(getActivity(), mUserAccount, 1);
                break;
            }
            case R.id.tv_fans: {//好友
                FriendsActivity.launch(getActivity(), mMyAccount);
                break;
            }
            case R.id.ll_sing_in: { //签到
                ProgressHUD.showLoadingMessage(getContext(), "正在签到...", false);
                String sAccount = Security.aesEncrypt(String.valueOf(AccountInfo.getInstance().loadAccount().getAccount()));
                ApiModule.apiService_1().signIn(sAccount).enqueue(new Callback<SignInRepo>() {
                    @Override
                    public void onResponse(Call<SignInRepo> call, Response<SignInRepo> response) {
                        SignInRepo signInRepo = response.body();
                        String msg = signInRepo.getMsg();
                        int continueDays = signInRepo.getContinueDays();
                        int thisIntegral = signInRepo.getThisIntegral();
                        totalIntegral = signInRepo.getTotalIntegral();
                        if (signInRepo.isSuccess()) {
                            adapter_fourth.updateIntegral(totalIntegral);
                            mLeftButton.setTextColor(getResources().getColor(R.color.white));
                            mSignInText.setTextColor(getResources().getColor(R.color.white));
                            mSignInText.setText("已签");
                            mSignInLayout.setEnabled(false);
                            signIn(thisIntegral, continueDays);
                        } else {
                            ProgressHUD.dismiss();
                            ProgressHUD.showErrorMessage(getActivity(), msg);
                        }
                    }

                    @Override
                    public void onFailure(Call<SignInRepo> call, Throwable t) {
                        ProgressHUD.dismiss();
                        ProgressHUD.showErrorMessage(getActivity(), getString(R.string.common_network_error));
                    }
                });

                break;
            }
            case R.id.head_tv_share: {//分享
                //分享名片
                ShareCardActivity.launch(getActivity(), mMyAccount, avatar, nick);

//                backgroundAlpha(0.6f); // 设置背景颜色变暗
//                sharePopWindow.showAtLocation(rootLayout, Gravity.BOTTOM, 0, 0);
                break;
            }

        }
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = bgAlpha; // 0.0-1.0
        getActivity().getWindow().setAttributes(lp);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getUserProfile();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();

        getUserProfile();
        //意见反馈即时刷新
        adapter_fivth.notifyDataSetChanged();

    }

    private void getUserProfile() {
        // 获取名片的信息
        String sMyAccount = Security.aesEncrypt(mMyAccount);
        String sAccount = Security.aesEncrypt(mUserAccount);
        ApiModule.apiService_1().getUserBusinessCard(sMyAccount, sAccount).enqueue(new Callback<UserCard>() {
            @Override
            public void onResponse(Call<UserCard> call, Response<UserCard> response) {
                UserCard userCard = response.body();
                if (userCard.isSuccess()) {
                    //获取到当前用户的头像,性别,昵称,地址
                    mUserCard = userCard;
                    avatar = userCard.getAvatar();
                    sex = userCard.getSex();
                    nick = userCard.getNick();
                    address = userCard.getAddress();
                    remark = userCard.getRemark();
                    nexus = userCard.getNexus();//被查看人与自己的关系
                    isOfficial = userCard.isOfficial();
                    isSignIn = userCard.isSignIn();
                    totalIntegral = userCard.getIntegral();
                    adapter_fourth.updateIntegral(totalIntegral);
                    isIMReg = userCard.isIMReg(); //是否注册云信
                    initUserProfile(userCard);

//                    adapter_fivth.notifyDataSetChanged();
                } else {
                    if (userCard.getCode() == 100) {
                        logout();
                    } else {
                        if (!MyUserFragment.this.isDetached())
                            ProgressHUD.showInfoMessage(getContext(), userCard.getMsg());
                    }
                }
            }

            @Override
            public void onFailure(Call<UserCard> call, Throwable t) {
                //if (!UserProfileFragment.this.isDetached())
                //ProgressHUD.showErrorMessage(getContext(), getString(R.string.common_network_error));
            }
        });
    }

    private void logout() {
        AccountInfo.getInstance().clearAccount();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        XKApplication.getInstance().exit();
    }

    private void blur(Bitmap bkg, final ImageView view, float radius) {
        int scaleFactor = 3;
        if (overlay != null) {
            overlay.recycle();
        }
        overlay = Bitmap.createScaledBitmap(bkg, bkg.getWidth() / scaleFactor, bkg.getHeight() / scaleFactor, false);
        overlay = process.blur(overlay, radius);//高斯模糊
        view.setImageBitmap(overlay);
    }

    public Bitmap returnBitMap(String url, final String bgImg) {
        URL myFileUrl = null;
        Bitmap bitmap = null;
        try {
            myFileUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            final HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
            //--------------- 背景墙 --------------------------
            if (!DEFAULT_BACKGROUND_KEY.equals(bgImg)) {
                int x = (int) bgImageview.getX();
                int y = (int) bgImageview.getY();
                int bitmapX = bitmap.getWidth();
                int bitmapY = bitmap.getHeight();
                final Bitmap bitmap1 = Bitmap.createBitmap(bitmap, x, y, bitmapX - x, bitmapY - y);

                if (bitmap != null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            XKApplication.runOnUIThread(new Runnable() {
                                @Override
                                public void run() {
                                    //视图更新要在主线程进行
                                    blur(bitmap1, bgImageview, 3);//模糊处理
                                }
                            });
                        }
                    }).start();
                }

            } else {
                Resources res = getResources();
                Bitmap scaledBitmap = BitmapFactory.decodeResource(res, R.mipmap.userbg);
                int x = (int) bgImageview.getX();
                int y = (int) bgImageview.getY();
                int bitmapX = scaledBitmap.getWidth();
                int bitmapY = scaledBitmap.getHeight();
                final Bitmap bitmap1 = Bitmap.createBitmap(scaledBitmap, x, y, bitmapX - x, bitmapY - y);

                if (scaledBitmap != null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            XKApplication.runOnUIThread(new Runnable() {
                                @Override
                                public void run() {
                                    //视图更新要在主线程进行
                                    blur(bitmap1, bgImageview, 3);//模糊处理
                                }
                            });
                        }
                    }).start();
                }
            }
            // --------------------背景墙毛玻璃效果 ----------------
            final Bitmap finalBitmap = bitmap;
            final Bitmap finalBitmap1 = bitmap;
            mZoomRecyclerview.setOnPullZoomListener(new PullToZoomBase.OnPullZoomListener() {
                @Override
                public void onPullZooming(final int newScrollValue) {
                    final int scaleRatio;
                    int scale = bgImageview.getHeight();

                    if (scale > 0 && scale < 550) {
                        scaleRatio = 3;
                    } else if (scale > 550 && scale < 650) {
                        scaleRatio = 2;
                    } else if (scale > 650 && scale < 750) {
                        scaleRatio = 1;
                    } else {
                        scaleRatio = 0;
                    }

                    if (DEFAULT_BACKGROUND_KEY.equals(bgImg)) {
                        Resources res = getResources();
                        Bitmap scaledBitmap = BitmapFactory.decodeResource(res, R.mipmap.userbg);
                        int x = (int) bgImageview.getX();
                        int y = (int) bgImageview.getY();
                        int bitmapX = scaledBitmap.getWidth();
                        int bitmapY = scaledBitmap.getHeight();
                        final Bitmap bitmap1 = Bitmap.createBitmap(scaledBitmap, x, y, bitmapX - x, bitmapY - y);

                        if (scaledBitmap != null) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    XKApplication.runOnUIThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            //视图更新要在主线程进行
                                            blur(bitmap1, bgImageview, scaleRatio);//模糊处理
                                        }
                                    });
                                }
                            }).start();
                        }

                    } else {
                        int x = (int) bgImageview.getX();
                        int y = (int) bgImageview.getY();
                        int bitmapX = finalBitmap.getWidth();
                        int bitmapY = finalBitmap.getHeight();
                        final Bitmap bitmap1 = Bitmap.createBitmap(finalBitmap, x, y, bitmapX - x, bitmapY - y);

                        if (finalBitmap != null) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    XKApplication.runOnUIThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            //视图更新要在主线程进行
                                            blur(bitmap1, bgImageview, scaleRatio);//模糊处理
                                        }
                                    });
                                }
                            }).start();
                        }
                    }
                }

                @Override
                public void onPullZoomEnd() {
                    if (DEFAULT_BACKGROUND_KEY.equals(bgImg)) {
                        Resources res = getResources();
                        Bitmap scaledBitmap = BitmapFactory.decodeResource(res, R.mipmap.userbg);
                        int x = (int) bgImageview.getX();
                        int y = (int) bgImageview.getY();
                        int bitmapX = scaledBitmap.getWidth();
                        int bitmapY = scaledBitmap.getHeight();
                        final Bitmap bitmap1 = Bitmap.createBitmap(scaledBitmap, x, y, bitmapX - x, bitmapY - y);

                        if (scaledBitmap != null) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    XKApplication.runOnUIThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            //视图更新要在主线程进行
                                            blur(bitmap1, bgImageview, 3);//模糊处理
                                        }
                                    });
                                }
                            }).start();
                        }
                    } else {
                        int x = (int) bgImageview.getX();
                        int y = (int) bgImageview.getY();
                        int bitmapX = finalBitmap1.getWidth();
                        int bitmapY = finalBitmap1.getHeight();
                        final Bitmap bitmap1 = Bitmap.createBitmap(finalBitmap1, x, y, bitmapX - x, bitmapY - y);

                        if (finalBitmap1 != null) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    XKApplication.runOnUIThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            //视图更新要在主线程进行
                                            blur(bitmap1, bgImageview, 3);//模糊处理
                                        }
                                    });
                                }
                            }).start();
                        }
                    }
                }
            });

            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void initUserProfile(final UserCard userCard) {//初始化用户信息
        Uri imageUri = Uri.parse(QiniuHelper.getThumbnail200Url(userCard.getAvatar()));
        if (!DEFAULT_BACKGROUND_KEY.equals(userCard.getAvatar())) {
            mAvatarView.setImageURI(imageUri);
        }
        // 赞、关注、粉丝数量显示
        UserCounts userCounts = userCard.getUserCounts();
        followTextview.setText(String.format("%d\n关注", userCounts.getFriendsNum() + userCounts.getGoodfriendNum()));
//        followTextview.setText(String.format("%d\n关注", userCounts.getFriendsNum()));
        zanTextview.setText(String.format("%d\n赞", userCounts.getPraiseNum()));
        fansTextview.setText(String.format("%d\n粉丝", userCounts.getFansNum()));
        mFriend.setText(String.format("%d\n好友", userCounts.getGoodfriendNum()));

        // 点击进入我的名片
        mAvatarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isMine) {
                    showEditAvatarDialog(); //修改头像
                }
            }
        });
        final String bgImg = userCard.getBgImg();
        final String url = QiniuHelper.getOriginalWithKey(bgImg);

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                // 下面的这个方法必须在子线程中执行
//                returnBitMap(url, bgImg);
//            }
//        }).start();

        Resources res = getResources();
        Bitmap scaledBitmap = BitmapFactory.decodeResource(res, R.mipmap.userbg);
        int x = (int) bgImageview.getX();
        int y = (int) bgImageview.getY();
        int bitmapX = scaledBitmap.getWidth();
        int bitmapY = scaledBitmap.getHeight();
        final Bitmap bitmap1 = Bitmap.createBitmap(scaledBitmap, x, y, bitmapX - x, bitmapY - y);

        if (scaledBitmap != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    XKApplication.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            //视图更新要在主线程进行
                            blur(bitmap1, bgImageview, 3);//模糊处理
                        }
                    });
                }
            }).start();
        }

        mZoomRecyclerview.setOnPullZoomListener(new PullToZoomBase.OnPullZoomListener() {
            @Override
            public void onPullZooming(final int newScrollValue) {
                final int scaleRatio;
                final int scale = bgImageview.getHeight();

                if (scale > 0 && scale < 550) {
                    scaleRatio = 3;
                } else if (scale > 550 && scale < 650) {
                    scaleRatio = 2;
                } else if (scale > 650 && scale < 750) {
                    scaleRatio = 1;
                } else {
                    scaleRatio = 0;
                }

                Resources res = getResources();
                Bitmap scaledBitmap = BitmapFactory.decodeResource(res, R.mipmap.userbg);
                int x = (int) bgImageview.getX();
                int y = (int) bgImageview.getY();
                int bitmapX = scaledBitmap.getWidth();
                int bitmapY = scaledBitmap.getHeight();
                final Bitmap bitmap1 = Bitmap.createBitmap(scaledBitmap, x, y, bitmapX - x, bitmapY - y);

                if (scaledBitmap != null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            XKApplication.runOnUIThread(new Runnable() {
                                @Override
                                public void run() {
                                    //视图更新要在主线程进行
                                    blur(bitmap1, bgImageview, scaleRatio);//模糊处理
                                }
                            });
                        }
                    }).start();
                }
            }

            @Override
            public void onPullZoomEnd() {
                Resources res = getResources();
                Bitmap scaledBitmap = BitmapFactory.decodeResource(res, R.mipmap.userbg);
                int x = (int) bgImageview.getX();
                int y = (int) bgImageview.getY();
                int bitmapX = scaledBitmap.getWidth();
                int bitmapY = scaledBitmap.getHeight();
                final Bitmap bitmap1 = Bitmap.createBitmap(scaledBitmap, x, y, bitmapX - x, bitmapY - y);

                if (scaledBitmap != null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            XKApplication.runOnUIThread(new Runnable() {
                                @Override
                                public void run() {
                                    //视图更新要在主线程进行
                                    blur(bitmap1, bgImageview, 3);//模糊处理
                                }
                            });
                        }
                    }).start();
                }
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                // 下面的这个方法必须在子线程中执行
                returnBitMap(url, bgImg);
            }
        }).start();

        //--------------- 背景墙 --------------------------
//        if (!DEFAULT_BACKGROUND_KEY.equals(bgImg)) {
//            GlideHelper.loadFullImageWithUrl(QiniuHelper.getOriginalWithKey(bgImg), bgImageview, new GlideHelper.ImageLoadingListener() {
//                @Override
//                public void onLoaded() {
//                    GlideHelper.loadResource(R.mipmap.userbg, bgImageview);
//                }
//
//                @Override
//                public void onFailed() {
//                    GlideHelper.loadResource(R.mipmap.userbg, bgImageview);
//                }
//            });
////            Glide.with(XKApplication.getInstance())
////                    .load(QiniuHelper.getOriginalWithKey(bgImg))
////                    .error(R.mipmap.userbg)
////                    .placeholder(R.mipmap.userbg)
////                    .into(bgImageview);
//
//        } else {
//            GlideHelper.loadResource(R.mipmap.userbg, bgImageview);
//        }

        if (userCard.isSignIn()) {//已签到
            mLeftButton.setTextColor(getResources().getColor(R.color.white));
            mSignInText.setTextColor(getResources().getColor(R.color.white));
            mSignInText.setText("已签");
            mSignInLayout.setEnabled(false);

        }
        if (userCard.getSex() == 1) {
            mSex.setImageResource(R.mipmap.icon_boy);
        } else if (userCard.getSex() == 2) {
            mSex.setImageResource(R.mipmap.icon_girl);
        } else {
            mSex.setVisibility(View.GONE);
        }
        String myTalk = DEFAULT_MYTALK;
        if (!TextUtils.isEmpty(userCard.getTalk())) {
            myTalk = userCard.getTalk();
        }
        mNote.setText(String.format("“ %s ”", myTalk));
        if (!TextUtils.isEmpty(userCard.getRemark())) {//判断是否有备注名
            mNickName.setText(userCard.getRemark());
        } else {
            //mNickName.setText(userCard.getNick());
            mNickName.setText(EmojiUtils.fromStringToEmoji1(userCard.getNick(), getContext()));
        }

        if (isOfficial) {
            mXingkaNumber.setVisibility(View.GONE);
            mXingKaNoTv.setVisibility(View.GONE);
            officalBtn.setVisibility(View.VISIBLE);
        } else {
            mXingkaNumber.setText(String.valueOf(userCard.getAccount()));
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(UpdateCountEvent event) { // get update message
        getUserProfile();
//        getData(); //获取分类未读消息数
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_REQUEST_BACKGROUD) {//选择背景墙图片
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    fileUri = data.getData();
                }
                fileCropUri = getOutputMediaFileUri();
                Intent intent = new Intent(getActivity(), CropBgImageActivity.class);
                // 获取这个图片的URI
                String filePath = AppUtility.getFilePath(getActivity(), fileUri);
                intent.putExtra(CropBgImageActivity.ARG_CROP_IMAGE_PATH, filePath);
                startActivityForResult(intent, RESULT_REQUEST_BACKGROUD_CROP);
//                cropImageUri(fileUri, fileCropUri, AppUtility.getScreenWidth(), AppUtility.getScreenWidth(), RESULT_REQUEST_BACKGROUD_CROP);
            }
        } else if (requestCode == RESULT_REQUEST_BACKGROUD_CROP) {
            if (resultCode == Activity.RESULT_OK) {
//                String filePath = AppUtility.getFilePath(getActivity(), fileCropUri);
                Bundle bundle = data.getExtras();
                String cropImagePath = bundle.getString("cropImagePath");
                File avatarFile = new File(cropImagePath);
                Logger.e("filePath:" + cropImagePath);
                // Todo: 将背景图片文件上传到指定服务器上
                updateAvatarToQiniu(avatarFile, 1);
            }
        }
        if (requestCode == RESULT_REQUEST_PHOTO) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    fileUri = data.getData();
                }
                fileCropUri = getOutputMediaFileUri();
                cropImageUri(fileUri, fileCropUri, 560, 560, RESULT_REQUEST_PHOTO_CROP);
            }
        } else if (requestCode == RESULT_REQUEST_PHOTO_CROP) {
            if (resultCode == Activity.RESULT_OK) {
                String filePath = AppUtility.getFilePath(getActivity(), fileCropUri);
                File avatarFile = new File(filePath);
                Logger.d("filePath:" + filePath);
                // Todo: 将头像文件上传到指定服务器上
                updateAvatarToQiniu(avatarFile, 0);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 显示头像大图
     */
    private void showAvatarDialog(String avatarKey) {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ViewAvatarFragment avatarFragment = ViewAvatarFragment.newInstance(avatarKey);
        avatarFragment.show(ft, "dialog");
    }

    private void showChangeBGDialog() {//修改背景
        //final String[] stringItems = {"去拍一张美图", "去我的相册中选择", "去背景墙选择"};
        final String[] stringItems = {"去拍一张美图", "去我的相册中选择"};
        final ActionSheetDialog dialog = new ActionSheetDialog(getActivity(), stringItems, null);
        //dialog.title("分享名片").isTitleShow(false).show();
        //弹出框背景色为白色
        dialog.titleBgColor(Color.parseColor("#ffffff"))
                .lvBgColor(Color.parseColor("#ffffff"));
//            dialog.itemTextColor(Color.parseColor("#ff2814"));
        dialog.title("更换背景墙")
                .titleTextSize_SP(13f)
                .titleHeight(35f)
                .titleTextColor(Color.parseColor("#aeaeb3"))
                .dividerColor(Color.parseColor("#bcbcc4"))
                .isTitleShow(true);
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
                    //去拍一张美图
                    backgroupFromCamera();
                } else if (position == 1) {
                    //去我的相册中选择
                    backgroupFromPhoto();
                }
                dialog.dismiss();
            }
        });
    }

    public void backgroupFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, RESULT_REQUEST_BACKGROUD);
    }

    public void backgroupFromPhoto() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_REQUEST_BACKGROUD);
    }

    public Uri getOutputMediaFileUri() {
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "XKFile");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Logger.d("XKFile", "failed to create directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp
                + ".jpg");
        return Uri.fromFile(mediaFile);
    }

    protected void cropImageUri(Uri uri, Uri outputUri, int outputX, int outputY, int requestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, requestCode);
    }

    private void updateAvatarToQiniu(final File avatarFile, final int type) {
        ProgressHUD.showLoadingMessage(getActivity(), "正在更新", true);
        String randomStr = Security.aesEncrypt("android");
        ApiModule.apiService().getQiniuToken(randomStr).enqueue(new Callback<XKRepo>() {
            @Override
            public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                XKRepo xkRepo = response.body();
                if (xkRepo.isSuccess()) {
                    String token = xkRepo.getMsg();
                    upload(token, avatarFile, type);
                } else {
                    ProgressHUD.dismiss();
                    ProgressHUD.showInfoMessage(getActivity(), xkRepo.getMsg());
                }
            }

            @Override
            public void onFailure(Call<XKRepo> call, Throwable t) {
                ProgressHUD.showErrorMessage(getActivity(), getString(R.string.common_network_error));
            }

        });
    }

    private void upload(String uploadToken, final File avatarFile, final int type) {
        if (this.uploadManager == null) {
            this.uploadManager = new UploadManager();
        }
        String uploadFilKey = AppUtility.generateUUID();
        this.uploadManager.put(avatarFile, uploadFilKey, uploadToken, new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                if (info.isOK()) {
                    String fileKey = response.optString("key");
                    Logger.d(fileKey);
                    if (type == 0) {//修改头像
                        updateAvatarKey(fileKey, avatarFile);
                    } else if (type == 1) {//修改背景墙
                        updateBackGroud(fileKey, avatarFile);
                    }

                }
            }
        }, null);
    }

    //修改头像
    private void updateAvatarKey(final String key, final File avatarFile) {
        String account = Security.aesEncrypt(String.valueOf(mCurrentUser.getAccount()));
        String type = Security.aesEncrypt("img");
        String value = Security.aesEncrypt(key);
        ApiModule.apiService().editUserInfo(account,
                type, value).enqueue(new Callback<XKRepo>() {
            @Override
            public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                XKRepo xkRepo = response.body();
                Logger.i(xkRepo.getMsg());
                ProgressHUD.dismiss();
                if (xkRepo.isSuccess()) {
                    ProgressHUD.showSuccessMessage(getActivity(), "已完成头像更新!");
                    // 更新本地头像信息
                    mCurrentUser.setAvatar(key);
                    // mAvatarView.setImageBitmap(BitmapFactory.decodeFile(avatarFile.getPath()));
                    mAvatarView.setImageURI(Uri.parse(QiniuHelper.getThumbnail200Url(key)));

                    AccountInfo.getInstance().saveAccount(mCurrentUser);
                } else {
                    ProgressHUD.showSuccessMessage(getActivity(), xkRepo.getMsg());
                }
            }

            @Override
            public void onFailure(Call<XKRepo> call, Throwable t) {
                ProgressHUD.showErrorMessage(getActivity(), getString(R.string.common_network_error));
            }

        });
    }

    //修改背景墙
    private void updateBackGroud(final String key, final File imageFile) {
        String account = Security.aesEncrypt(String.valueOf(mCurrentUser.getAccount()));
        String type = Security.aesEncrypt("bgimg");
        String value = Security.aesEncrypt(key);
        ApiModule.apiService().editUserInfo(account,
                type, value).enqueue(new Callback<XKRepo>() {
            @Override
            public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                XKRepo xkRepo = response.body();
                Logger.i(xkRepo.getMsg());
                ProgressHUD.dismiss();
                if (xkRepo.isSuccess()) {
                    ProgressHUD.showSuccessMessage(getActivity(), "修改背景墙成功!");
                    // 更新本地头像信息
                    mCurrentUser.setBackgroud(key);
                    Glide.with(getActivity())
                            .load(imageFile.getPath())
                            .placeholder(R.mipmap.userbg)
                            .error(R.mipmap.userbg)
                            .into(bgImageview);
                    //Drawable drawable = new BitmapDrawable(BitmapFactory.decodeFile(imageFile.getPath()));
                    //userInfoLayout.setBackground(drawable);
                    AccountInfo.getInstance().saveAccount(mCurrentUser);
                } else {
                    ProgressHUD.showSuccessMessage(getActivity(), xkRepo.getMsg());
                }
            }

            @Override
            public void onFailure(Call<XKRepo> call, Throwable t) {
                ProgressHUD.showErrorMessage(getActivity(), getString(R.string.common_network_error));
            }

        });
    }

    private void reportUser() {
        String sAccount = Security.aesEncrypt(mMyAccount);
        String sUserAcount = Security.aesEncrypt(mUserAccount);
        String sType = Security.aesEncrypt("0");
        ApiModule.apiService_1().reportUser(sAccount,
                sUserAcount, sType).enqueue(new Callback<XKRepo>() {
            @Override
            public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                XKRepo xkRepo = response.body();
                Logger.i(xkRepo.getMsg());
                ProgressHUD.dismiss();
                if (xkRepo.isSuccess()) {
                    ProgressHUD.showSuccessMessage(getActivity(), "举报成功,感谢您的反馈");
                } else {
                    ProgressHUD.showSuccessMessage(getActivity(), xkRepo.getMsg());
                }
            }

            @Override
            public void onFailure(Call<XKRepo> call, Throwable t) {
                ProgressHUD.showErrorMessage(getActivity(), getString(R.string.common_network_error));
            }
        });
    }

    /**
     * 显示修改头像选择对话框
     */
    private void showEditAvatarDialog() {
        final String[] actionItems = {"拍照", "从手机相册选择"};
        final ActionSheetDialog dialog = new ActionSheetDialog(getActivity(), actionItems, null);
        //弹出框背景色为白色
        dialog.titleBgColor(Color.parseColor("#ffffff"))
                .lvBgColor(Color.parseColor("#ffffff"));
//            dialog.itemTextColor(Color.parseColor("#ff2814"));
        dialog.title("更换头像")
                .titleTextSize_SP(13f)
                .titleHeight(35f)
                .titleTextColor(Color.parseColor("#aeaeb3"))
                .dividerColor(Color.parseColor("#bcbcc4"))
                .isTitleShow(true);
        dialog.itemTextColor(Color.parseColor("#46464c"))
                .itemTextSize(15.5f)
                .itemHeight(40f)
                .dividerColor(Color.parseColor("#bcbcc4"));
        dialog.cancelText(Color.parseColor("#46464c"))
                .cancelTextSize(15.5f)
                .show();

        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    camera();
                } else if (position == 1) {
                    photo();
                }
                dialog.dismiss();
            }
        });
    }

    public void camera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, RESULT_REQUEST_PHOTO);
    }

    public void photo() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_REQUEST_PHOTO);
    }

    private void signIn(int thisIntegral, int continueDays) {

        RelativeLayout layout = (RelativeLayout) LayoutInflater.from(getActivity()).inflate(R.layout.layout_signin_animotion, null);
        final Dialog dialog = new AlertDialog.Builder(getActivity()).create();

        dialog.setCancelable(false); // 设置点击返回键Dialog不消失
        dialog.setCanceledOnTouchOutside(false); // 设置点击屏幕Dialog不消失
//             /*
//         * 将对话框的大小按屏幕大小的百分比设置
//         */
//        Window dialogWindow = dialog.getWindow();
//        WindowManager m = getActivity().getWindowManager();
//        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
//        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
//        p.height = (int) (d.getHeight() * 0.5); // 高度设置为屏幕的0.3
//        p.width = (int) (d.getWidth() * 0.5); // 宽度设置为屏幕的0.6
//        dialogWindow.setAttributes(p);
        ProgressHUD.dismiss();
        dialog.show();
        dialog.getWindow().setContentView(layout);
        TextView message = (TextView) layout.findViewById(R.id.update_content);
        message.setGravity(Gravity.CENTER);
        String content = "本次获得%d积分,已连续签到%d天";
        message.setText(String.format(content, thisIntegral, continueDays));
        ImageView animotionIv = (ImageView) layout.findViewById(R.id.update_icon);
        animotionIv.setImageResource(R.drawable.animation_signin);
        animationDrawable = (AnimationDrawable) animotionIv.getDrawable();
        animationDrawable.start();
        // 4. 确定按钮
        Button btnOK = (Button) layout.findViewById(R.id.umeng_update_id_ok);
        btnOK.setText("确定");
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }
}

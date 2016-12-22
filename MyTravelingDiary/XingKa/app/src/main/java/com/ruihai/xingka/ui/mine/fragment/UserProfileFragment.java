package com.ruihai.xingka.ui.mine.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.flyco.dialog.widget.NormalDialog;
import com.melnykov.fab.FloatingActionButton;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.friend.constant.VerifyType;
import com.netease.nimlib.sdk.friend.model.AddFriendData;
import com.orhanobut.logger.Logger;
import com.qiniu.android.storage.UploadManager;
import com.ruihai.iconicfontengine.widget.IconicFontTextView;
import com.ruihai.xingka.Global;
import com.ruihai.xingka.R;
import com.ruihai.xingka.XKApplication;
import com.ruihai.xingka.api.ApiModule;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.api.model.ImagesMessage;
import com.ruihai.xingka.api.model.MyTrackway;
import com.ruihai.xingka.api.model.MyTrackwayRepo;
import com.ruihai.xingka.api.model.User;
import com.ruihai.xingka.api.model.UserCard;
import com.ruihai.xingka.api.model.UserCounts;
import com.ruihai.xingka.api.model.UserPhotoTopic;
import com.ruihai.xingka.api.model.UserPhotoTopicRepo;
import com.ruihai.xingka.api.model.XKRepo;
import com.ruihai.xingka.event.OnItemClickListener;
import com.ruihai.xingka.event.UpdateCountEvent;
import com.ruihai.xingka.ui.caption.CaptionDetailActivity;
import com.ruihai.xingka.ui.common.enter.EmojiUtils;
import com.ruihai.xingka.ui.login.LoginActivity;
import com.ruihai.xingka.ui.mine.FollowAndFansActivity;
import com.ruihai.xingka.ui.talking.FriendActivity;
import com.ruihai.xingka.ui.talking.RemarkActivity;
import com.ruihai.xingka.ui.trackway.TrackwayDetailActivity;
import com.ruihai.xingka.utils.AppUtility;
import com.ruihai.xingka.utils.QiniuHelper;
import com.ruihai.xingka.utils.Security;
import com.ruihai.xingka.widget.CustomImageView;
import com.ruihai.xingka.widget.JavaBlurProcess;
import com.ruihai.xingka.widget.NineGridlayout;
import com.ruihai.xingka.widget.ProgressHUD;
import com.ruihai.xingka.widget.pulltozoomview.PullToZoomBase;
import com.ruihai.xingka.widget.pulltozoomview.PullToZoomRecyclerViewEx;
import com.ruihai.xingka.widget.pulltozoomview.RecyclerViewHeaderAdapter;
import com.shizhefei.mvc.IDataAdapter;
import com.shizhefei.mvc.MVCHelper;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by zecker on 15/11/6.
 */
public class UserProfileFragment extends Fragment implements View.OnClickListener, OnItemClickListener {

    private static final String ARG_LAST_SCROLL_Y = "arg.LastScrollY";
    public final int RESULT_REQUEST_PHOTO = 0x1005;
    public final int RESULT_REQUEST_PHOTO_CROP = 0x1006;
    public final int RESULT_REQUEST_BACKGROUD = 0x1007;
    public final int RESULT_REQUEST_BACKGROUD_CROP = 0x1008;
    public static final String DEFAULT_BACKGROUND_KEY = "00000000-0000-0000-0000-000000000000";

    @BindView(R.id.rl_toolbar)
    RelativeLayout mToolbarRelative;
    @BindView(R.id.user_profile_toolbar)
    RelativeLayout mToolbar;
    @BindView(R.id.tv_left)
    IconicFontTextView mLeft;
    @BindView(R.id.tv_right)
    IconicFontTextView mRight;
    @BindView(R.id.img_btn1)
    RelativeLayout img_btn1;
    @BindView(R.id.img_btn2)
    RelativeLayout img_btn2;
    @BindView(R.id.recyclerview)
    PullToZoomRecyclerViewEx mZoomRecyclerview;
    @BindView(R.id.floatButton)
    FloatingActionButton floatButton;
    @BindView(R.id.ll_empty)
    LinearLayout mEmpty;
    @BindView(R.id.ll_error)
    LinearLayout mError;
    @BindView(R.id.head_tv_left)
    IconicFontTextView mLeftButton;
    @BindView(R.id.head_tv_share)
    TextView mReportTv;

    RelativeLayout mBgToolBarrelative;
    //    @BindView(R.id.user_info_layout)
    RelativeLayout userInfoLayout;
    //    @BindView(R.id.bg_image)
    ImageView bgImageview;

    // 用户信息展示控件
//    @Bind(R.id.sdv_avatar)
    ImageView mAvatarView;
    //    @Bind(R.id.iv_sex)
    ImageView mSex;
    //    @Bind(R.id.tv_note)
    TextView mNote;
    //    @Bind(R.id.tv_nickname)
    TextView mNickName;
    //    @Bind(R.id.tv_xingka_number)
    TextView mXingkaNumber;
    //    @Bind(R.id.tv_xingka_numbername)
    TextView mXingkaNoTv;
    //    @Bind(R.id.tv_officalaccount)
    TextView officalBtn;
    //    @Bind(R.id.tv_addfriend)
    RelativeLayout isIMFriendLayout;
    CheckedTextView mAttentionFriend;
    CheckedTextView mIsIMFriend;
    //    @Bind(R.id.zan_layout)
//    LinearLayout zanLayout;
    //    @Bind(R.id.tv_zan)
    TextView zanTextview;
    TextView trackwayTextview;
    //        @Bind(R.id.tv_fans)
    TextView fansTextview;
    //    @Bind(R.id.tv_follow)
    TextView followTextview;
    //    @Bind(R.id.iv_logo)
    FrameLayout avatarLayout;
    //    @Bind(R.id.fans_follow_layout)
    LinearLayout fansfollowLayout;
    TextView mFriend;
    View mView;

    //默认的一句话说说的内容
    private final String DEFAULT_MYTALK = "有点害羞，不知道说什么O(∩_∩)O~";
    private String avatar; // 当前名片头像
    private int sex; // 当前名片性别
    private String nick; // 当前名片昵称
    private String address; // 当前名片用户地址
    private String remark; // 当前名片用户备注名
    private int nexus; // 被查看人是查看人的什么关系
    private boolean isOfficial;//是否官方账号

    private boolean isIMReg; //是否注册云信
    private boolean isIMFriend; //是否云信好友
    private boolean isIMBlack; //是否云信黑名单

    private User mCurrentUser; // 我的信息类
    private String mMyAccount; // 我的行咖号
    private String mUserAccount; // 名片行咖号
    private boolean isMine = false; // 当前是否为自己的主页
    private LayoutInflater inflater;
    private UserCard mUserCard;//用户名片数据

    private List<BaseScrollFragment> mFragments = new ArrayList<>();
    private Uri fileUri;
    private Uri fileCropUri;
    private UploadManager uploadManager;
    private UserCaptionAdapter userCaptionAdapter;
    private UserTrackwayAdapter userTrackwayAdapter;
    private int mPage = 1;
    private int mPage1 = 1;
    private int mMaxPage = 0;
    private int mPerPage = 10;
    private RecyclerView mRecycleview;
    private List<UserPhotoTopic> mPhotoTopics = new ArrayList<UserPhotoTopic>();
    private List<MyTrackway> myTrackwayList = new ArrayList<MyTrackway>();
    ;
    private boolean isLoadingMore = false;
    private MVCHelper<List<UserPhotoTopic>> listViewHelper;
    private int mPhotoCount;
    private int totalDy = 0;

    private int mType = 1; // 1:图说 2:旅拼
    private int trackwayCount, photoTopicCount;

    Bitmap overlay;
    JavaBlurProcess process = new JavaBlurProcess();

    //    private TranslateAnimation mShowAction;
    //    private TranslateAnimation mHiddenAction;
    public static UserProfileFragment newInstance(String userAccount) {
        Bundle args = new Bundle();
        UserProfileFragment fragment = new UserProfileFragment();
        args.putString("userAccount", userAccount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserAccount = getArguments().getString("userAccount");

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
        final View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        ButterKnife.bind(this, view);
        ProgressHUD.showLoadingMessage(getActivity(), "正在加载中...", true);
        //请求接口获取数据
//         getCaptionData(); //图说数据
//         getTrackwayData(); //旅拼数据

        mRecycleview = mZoomRecyclerview.getPullRootView();

        View headView = LayoutInflater.from(getActivity()).inflate(R.layout.user_header, null, false);
        View zoomView = LayoutInflater.from(getActivity()).inflate(R.layout.user_zoom, null, false);
        mZoomRecyclerview.setHeaderView(headView);
        mZoomRecyclerview.setZoomView(zoomView);

        // 获取组件
        userInfoLayout = (RelativeLayout) headView.findViewById(R.id.user_info_layout);
        mAvatarView = (ImageView) headView.findViewById(R.id.sdv_avatar);
        mSex = (ImageView) headView.findViewById(R.id.iv_sex);
        mNote = (TextView) headView.findViewById(R.id.tv_note);
        mNickName = (TextView) headView.findViewById(R.id.tv_nickname);
        mXingkaNumber = (TextView) headView.findViewById(R.id.tv_xingka_number);
        mXingkaNoTv = (TextView) headView.findViewById(R.id.tv_xingka_numbername);
        officalBtn = (TextView) headView.findViewById(R.id.tv_officalaccount);
        zanTextview = (TextView) headView.findViewById(R.id.tv_zan);
        trackwayTextview = (TextView) headView.findViewById(R.id.tv_trackway);
        fansTextview = (TextView) headView.findViewById(R.id.tv_fans);
        followTextview = (TextView) headView.findViewById(R.id.tv_follow);
        fansfollowLayout = (LinearLayout) headView.findViewById(R.id.fans_follow_layout);
        avatarLayout = (FrameLayout) headView.findViewById(R.id.iv_logo);
//        mLeftButton = (IconicFontTextView) headView.findViewById(R.id.head_tv_left);
//        mReportTv = (IconicFontTextView) headView.findViewById(R.id.head_tv_share);
        isIMFriendLayout = (RelativeLayout) headView.findViewById(R.id.ll_isIMFriend);
        mAttentionFriend = (CheckedTextView) headView.findViewById(R.id.tv_addfriend);
        mIsIMFriend = (CheckedTextView) headView.findViewById(R.id.tv_isIMFriend);
//        mFriend = (TextView) headView.findViewById(R.id.tv_myfriend);
//        mView = (View) headView.findViewById(R.id.view);
        mBgToolBarrelative = (RelativeLayout) headView.findViewById(R.id.zan_fans_follow_collection_layout);
        bgImageview = (ImageView) zoomView.findViewById(R.id.bg_image);

        //给可缩放头部设置属性
        mZoomRecyclerview.setParallax(true);
        int mScreenHeight = AppUtility.getScreenHeight();
        int mScreenWidth = AppUtility.getScreenWidth();
        AbsListView.LayoutParams localObject = new AbsListView.LayoutParams(mScreenWidth, (int) (7.4F * (mScreenHeight / 16.0F)));
        mZoomRecyclerview.setHeaderLayoutParams(localObject);

        userInfoLayout.setOnClickListener(this);
        mAvatarView.setOnClickListener(this);
        mReportTv.setOnClickListener(this);
        mLeftButton.setOnClickListener(this);
        zanTextview.setOnClickListener(this);
        trackwayTextview.setOnClickListener(this);
        fansTextview.setOnClickListener(this);
        followTextview.setOnClickListener(this);
        mAttentionFriend.setOnClickListener(this);
        mIsIMFriend.setOnClickListener(this);
        mRight.setOnClickListener(this);
        mLeft.setOnClickListener(this);

        if (!isMine) {//他人
//            mFriend.setVisibility(View.GONE);
//            mView.setVisibility(View.GONE);
            mReportTv.setVisibility(View.VISIBLE);
            mLeftButton.setVisibility(View.VISIBLE);
            mLeftButton.setText("{xk-back}");//返回键
            mReportTv.setText("{xk-report}"); //举报
//            mToolbar.setVisibility(View.GONE);
            mLeft.setVisibility(View.VISIBLE);
            mRight.setVisibility(View.VISIBLE);
            img_btn1.setVisibility(View.VISIBLE);
            img_btn2.setVisibility(View.VISIBLE);
        }

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getUserProfile();  // 获取名片的信息
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_isIMFriend: { //云信添加好友
                final VerifyType verifyType = VerifyType.VERIFY_REQUEST; // 发起好友验证请求
                String msg = "跪求通过";
                NIMClient.getService(FriendService.class).addFriend(new AddFriendData(mUserAccount, verifyType, msg))
                        .setCallback(new RequestCallback<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                ProgressHUD.showSuccessMessage(getActivity(), "添加好友请求发送成功");
                                mIsIMFriend.setChecked(true);
                                mIsIMFriend.setVisibility(View.VISIBLE);
                                mIsIMFriend.setText("发消息");
                            }

                            @Override
                            public void onFailed(int code) {
                                ProgressHUD.showInfoMessage(getActivity(), "请求失败");
//                                Log.e("TAG", "添加失败-->" + code);
                                mIsIMFriend.setChecked(true);
                                mIsIMFriend.setVisibility(View.VISIBLE);
                                mIsIMFriend.setText("加好友");
                            }

                            @Override
                            public void onException(Throwable throwable) {
                                ProgressHUD.showSuccessMessage(getActivity(), "抛出异常");
//                                Log.e("TAG", "抛出异常-->" + throwable.getMessage());
                            }

                        });
                break;
            }
            case R.id.tv_fans: {//粉丝
                FollowAndFansActivity.launch(getActivity(), mUserAccount, 2);
                break;
            }
            case R.id.tv_follow: {//关注
                FollowAndFansActivity.launch(getActivity(), mUserAccount, 1);
                break;
            }

            case R.id.tv_left:
            case R.id.head_tv_left: { // 返回
                getActivity().finish();
                break;
            }
            case R.id.tv_right:
            case R.id.head_tv_share: { //设置备注名,删除好友,举报等操作
                showUserDialog();
                break;
            }

            case R.id.tv_trackway://旅拼
                mType = 2;

                if (trackwayCount == 0) {
                    mEmpty.setVisibility(View.VISIBLE);
                } else {
                    mEmpty.setVisibility(View.GONE);
                }

                trackwayTextview.setTextColor(Color.parseColor("#ff7800"));
                zanTextview.setTextColor(Color.parseColor("#ffffff"));

                final GridLayoutManager manager2 = new GridLayoutManager(getActivity(), 1);
                manager2.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return userTrackwayAdapter.getItemViewType(position) == RecyclerViewHeaderAdapter.INT_TYPE_HEADER ? 1 : 1;
                    }
                });
                if (userTrackwayAdapter == null) {
                    userTrackwayAdapter = new UserTrackwayAdapter(getActivity());
                    userTrackwayAdapter.setOnItemClickListener(this);
                    getTrackwayData(); //旅拼数据
                }
                mZoomRecyclerview.setAdapterAndLayoutManager(userTrackwayAdapter, manager2);

                onScolledTracker(manager2); //旅拼上拉滑动分页

                break;

            case R.id.tv_zan://图说
                mType = 1;

                if (photoTopicCount == 0) {
                    mEmpty.setVisibility(View.VISIBLE);
                } else {
                    mEmpty.setVisibility(View.GONE);
                }

                zanTextview.setTextColor(Color.parseColor("#ff7800"));
                trackwayTextview.setTextColor(Color.parseColor("#ffffff"));

                final GridLayoutManager manager1 = new GridLayoutManager(getActivity(), 1);
                manager1.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return userCaptionAdapter.getItemViewType(position) == RecyclerViewHeaderAdapter.INT_TYPE_HEADER ? 1 : 1;
                    }
                });
                if (userCaptionAdapter == null) {
                    userCaptionAdapter = new UserCaptionAdapter(getActivity());
                    userCaptionAdapter.setOnItemClickListener(this);
                    getCaptionData(); //图说数据
                }
                mZoomRecyclerview.setAdapterAndLayoutManager(userCaptionAdapter, manager1);

                onScolledCaption(manager1); //图说上拉滑动分页
                break;
            case R.id.tv_addfriend: {//加关注
                showFollowed();
                break;
//                ProgressHUD.showPraiseOrCollectSuccessMessage(getActivity(),"谢谢你的关注");
//                mAttentionFriend.setVisibility(View.VISIBLE);
//                mAttentionFriend.setText("已关注");
//                mAttentionFriend.setClickable(false);
//                ApiModule.apiService().addFriend(Security.aesEncrypt(mMyAccount),
//                        Security.aesEncrypt(mUserAccount)).enqueue(new Callback<XKRepo>() {
//                    @Override
//                    public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
//                        XKRepo xkRepo = response.body();
////                        ProgressHUD.dismiss();
//                        String msg = xkRepo.getMsg();
//                        if (xkRepo.isSuccess()) { // 关注成功
////                            mAttentionFriend.setVisibility(View.VISIBLE);
////                            mAttentionFriend.setText("已关注");
////                            mAttentionFriend.setClickable(false);
//                            //mRemark.setImageResource(R.mipmap.icon_remark);//改为备注图标
//                            nexus = 1;//已关注
//                        } else { // 关注失败
//                            ProgressHUD.showErrorMessage(getActivity(), msg);
//                            mAttentionFriend.setText("关注TA");
//                            mAttentionFriend.setClickable(true);
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<XKRepo> call, Throwable t) {
//                        ProgressHUD.showErrorMessage(getActivity(), t.getLocalizedMessage());
////                        ProgressHUD.dismiss();
//                    }
//
//                });


            }
        }
    }

    /**
     * 举报等操作
     */
    private void showUserDialog() {//举报等操作
        //注: 1:若不是好友则只有举报; 2:若是好友,判断如果删除操作后回到1; 3拉黑后改为移出黑名单;
        String[] stringItems = new String[0];
        if (isIMFriend) { //判断是否是好友
            if (isIMBlack) { //判断是否拉入黑名单
                stringItems = new String[]{"设置备注名", "移出黑名单", "删除好友", "举报"};
            } else {
                stringItems = new String[]{"设置备注名", "列入黑名单", "删除好友", "举报"};
            }
        } else {
            stringItems = new String[]{"举报"};
        }
//        final String[] stringItems = {"设置备注名", "列入黑名单", "删除好友", "举报"};

        final ActionSheetDialog dialog = new ActionSheetDialog(getActivity(), stringItems, null);
        //弹出框背景色为白色
        dialog.titleBgColor(Color.parseColor("#ffffff"))
                .lvBgColor(Color.parseColor("#ffffff"));
        dialog.title("设置备注名")
                .titleTextSize_SP(13f)
                .titleHeight(35f)
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
                    //设置备注名
                    Intent intent = new Intent(getActivity(), RemarkActivity.class);
                    intent.putExtra("account", mUserAccount);
                    intent.putExtra("remark", remark);
                    startActivityForResult(intent, 200);
//                    RemarkActivity.launch(getActivity(), mUserAccount, remark);
                } else if (position == 1) {
                    //列入黑名单
                    showIMBlack();
                } else if (position == 2) {
                    //删除好友
                    showDeleteIMFriend();
                } else if (position == 3) {
                    //举报
                    final NormalDialog dialog = new NormalDialog(getActivity());
                    dialog.isTitleShow(false)
                            .bgColor(Color.parseColor("#ffffff"))
                            .cornerRadius(5)
                            .content("是否举报该用户?")
                            .contentGravity(Gravity.CENTER)
                            .contentTextColor(Color.parseColor("#33333d"))
                            .dividerColor(Color.parseColor("#dcdce4"))
                            .btnTextSize(15.5f, 15.5f)
                            .btnTextColor(Color.parseColor("#ff2814"), Color.parseColor("#0077fe"))
                            .btnText("取消", "确定")
                            .widthScale(0.85f)
                            .show();
                    dialog.setOnBtnClickL(new OnBtnClickL() {
                        @Override
                        public void onBtnClick() {
                            dialog.dismiss();
                        }
                    }, new OnBtnClickL() {
                        @Override
                        public void onBtnClick() {

                            reportUser();
                            dialog.dismiss();
                        }
                    });
                }
                dialog.dismiss();
            }
        });
    }

    //拉黑云信好友
    private void showIMBlack() {
        String[] stringItems = {"确定"};
        final ActionSheetDialog dialog = new ActionSheetDialog(getActivity(), stringItems, null);
        String title;
        if (isIMBlack) {
            title = "确定移出黑名单吗";
        } else {
            title = "列入黑名单后,你将不再收到对方的消息";
        }
        //弹出框背景色为白色
        dialog.titleBgColor(Color.parseColor("#ffffff"))
                .lvBgColor(Color.parseColor("#ffffff"));
        dialog.title(title)
                .titleTextSize_SP(13f)
                .titleHeight(35f)
                .titleTextColor(Color.parseColor("#aeaeb3"))
                .dividerColor(Color.parseColor("#ffffff"))
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
                    //确定
                    String sAccid = Security.aesEncrypt(mMyAccount);
                    String sTaccid = Security.aesEncrypt(mUserAccount);
                    if (isIMBlack) { //判断是否在黑名单
                        ApiModule.apiService_1().deleteBlacklist(sAccid, sTaccid).enqueue(new Callback<XKRepo>() {
                            @Override
                            public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                                XKRepo xkRepo = response.body();
                                String msg = xkRepo.getMsg();
                                if (xkRepo.isSuccess()) { //
                                    ProgressHUD.showSuccessMessage(getActivity(), "移出黑名单成功");
                                    mIsIMFriend.setChecked(true);
                                    mIsIMFriend.setVisibility(View.VISIBLE);
                                    mIsIMFriend.setText("发消息");
                                } else {
                                    ProgressHUD.showErrorMessage(getActivity(), msg);
                                }
                            }

                            @Override
                            public void onFailure(Call<XKRepo> call, Throwable t) {
                                ProgressHUD.showErrorMessage(getActivity(), t.getLocalizedMessage());
                            }

                        });
                    } else {
                        ApiModule.apiService_1().addBlacklist(sAccid, sTaccid).enqueue(new Callback<XKRepo>() {
                            @Override
                            public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                                XKRepo xkRepo = response.body();
                                String msg = xkRepo.getMsg();
                                if (xkRepo.isSuccess()) { //
                                    ProgressHUD.showSuccessMessage(getActivity(), "列入黑名单成功");
                                    mIsIMFriend.setChecked(true);
                                    mIsIMFriend.setVisibility(View.VISIBLE);
                                    mIsIMFriend.setText("发消息");
                                } else {
                                    ProgressHUD.showErrorMessage(getActivity(), msg);
                                }
                            }

                            @Override
                            public void onFailure(Call<XKRepo> call, Throwable t) {
                                ProgressHUD.showErrorMessage(getActivity(), t.getLocalizedMessage());
                                ProgressHUD.dismiss();
                            }

                        });
                    }

                }
                dialog.dismiss();
            }
        });
    }


    // *****************************  删除云信好友 ***************************
    private void showDeleteIMFriend() {
        String[] stringItems = {"确定"};
        final ActionSheetDialog dialog = new ActionSheetDialog(getActivity(), stringItems, null);
        //弹出框背景色为白色
        dialog.titleBgColor(Color.parseColor("#ffffff"))
                .lvBgColor(Color.parseColor("#ffffff"));
        dialog.title("确定删除好友吗")
                .titleTextSize_SP(13f)
                .titleHeight(35f)
                .titleTextColor(Color.parseColor("#aeaeb3"))
                .dividerColor(Color.parseColor("#ffffff"))
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
                    //确定
                    String sAccid = Security.aesEncrypt(mMyAccount);
                    String sFaccid = Security.aesEncrypt(mUserAccount);
                    ApiModule.apiService_1().delIMFriend(sAccid, sFaccid).enqueue(new Callback<XKRepo>() {
                        @Override
                        public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                            XKRepo xkRepo = response.body();
                            String msg = xkRepo.getMsg();
                            if (xkRepo.isSuccess()) { //
                                ProgressHUD.showSuccessMessage(getActivity(), "删除好友成功");
                                mIsIMFriend.setChecked(true);
                                mIsIMFriend.setVisibility(View.VISIBLE);
                                mIsIMFriend.setText("加好友");
                                //mRemark.setImageResource(R.mipmap.icon_remark);//改为备注图标
                            } else {
                                ProgressHUD.showErrorMessage(getActivity(), msg);
                            }
                        }

                        @Override
                        public void onFailure(Call<XKRepo> call, Throwable t) {
                            ProgressHUD.showErrorMessage(getActivity(), t.getLocalizedMessage());
                            ProgressHUD.dismiss();
                        }

                    });


                }
                dialog.dismiss();
            }
        });
    }

    // *****************************  加关注  *****************************
    private void showFollowed() {
        final NormalDialog dialog = new NormalDialog(getActivity());
        dialog.isTitleShow(false)
                .bgColor(Color.parseColor("#ffffff"))
                .cornerRadius(5)
                .content("是否关注" + nick + "?")
                .contentGravity(Gravity.CENTER)
                .contentTextColor(Color.parseColor("#33333d"))
                .dividerColor(Color.parseColor("#dcdce4"))
                .btnTextSize(15.5f, 15.5f)
                .btnTextColor(Color.parseColor("#ff2814"), Color.parseColor("#0077fe"))
                .btnText("取消", "确定")
                .widthScale(0.85f)
                .show();

        dialog.setOnBtnClickL(new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                dialog.dismiss();
            }
        }, new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                dialog.dismiss();
                ProgressHUD.showLoadingMessage(getActivity(), "正在关注...", false);
                ApiModule.apiService().addFriend(Security.aesEncrypt(mMyAccount),
                        Security.aesEncrypt(mUserAccount)).enqueue(new Callback<XKRepo>() {
                    @Override
                    public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                        XKRepo xkRepo = response.body();
                        ProgressHUD.dismiss();
                        String msg = xkRepo.getMsg();
                        if (xkRepo.isSuccess()) { // 关注成功
                            mAttentionFriend.setChecked(true);
                            mAttentionFriend.setVisibility(View.VISIBLE);
                            mAttentionFriend.setText("已关注");
                            mAttentionFriend.setClickable(false);
                            //mRemark.setImageResource(R.mipmap.icon_remark);//改为备注图标
                            nexus = 1;//已关注
                        } else { // 关注失败
                            ProgressHUD.showErrorMessage(getActivity(), msg);
                        }
                    }

                    @Override
                    public void onFailure(Call<XKRepo> call, Throwable t) {
                        ProgressHUD.showErrorMessage(getActivity(), t.getLocalizedMessage());
                        ProgressHUD.dismiss();
                    }

                });
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
//        getUserProfile();
//        getCaptionData(); //图说数据
//        getTrackwayData(); //旅拼数据
    }

    // ----------------------- 请求数据获取名片信息 -----------------------
    private void getUserProfile() {
        String sMyAccount = Security.aesEncrypt(mMyAccount);
        String sAccount = Security.aesEncrypt(mUserAccount);
        ApiModule.apiService_1().getUserBusinessCard(sMyAccount, sAccount).enqueue(new Callback<UserCard>() {
            @Override
            public void onResponse(Call<UserCard> call, Response<UserCard> response) {
                ProgressHUD.dismiss();
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
                    isIMReg = userCard.isIMReg();
                    isIMFriend = userCard.isIMFriend();
                    isIMBlack = userCard.isIMBlack();
                    initUserProfile(userCard);
//                    Log.e("TAG","nick-->"+nick);
//                    Log.e("TAG","是否注册云信-->"+isIMReg);
                } else {
                    if (userCard.getCode() == 100) {
                        logout();
                    } else {
                        if (!UserProfileFragment.this.isDetached())
                            ProgressHUD.showInfoMessage(getContext(), userCard.getMsg());
                    }
                }
            }

            @Override
            public void onFailure(Call<UserCard> call, Throwable t) {
                ProgressHUD.dismiss();
                //if (!UserProfileFragment.this.isDetached())
                //ProgressHUD.showErrorMessage(getContext(), getString(R.string.common_network_error));
            }
        });
    }

    // ********************************* 初始化用户信息 *********************************
    public void initUserProfile(final UserCard userCard) {
        photoTopicCount = userCard.getUserCounts().getPhotoTopicNum(); //获取图说数量
        trackwayCount = userCard.getTravelTogetherNum(); //获取旅拼数量

        Uri imageUri = Uri.parse(QiniuHelper.getThumbnail200Url(userCard.getAvatar()));
        if (!DEFAULT_BACKGROUND_KEY.equals(userCard.getAvatar())) {
            mAvatarView.setImageURI(imageUri);
        } else {
            mAvatarView.setImageURI(Uri.parse("res:///" + R.mipmap.default_avatar));
        }

        if (isOfficial) {
            mReportTv.setVisibility(View.GONE);
            img_btn2.setVisibility(View.GONE);
            mRight.setVisibility(View.GONE);
        }
        // 赞、关注、粉丝数量显示
        UserCounts userCounts = userCard.getUserCounts();
        // 更新图说数值显示
        int captionCount = userCard.getUserCounts().getPhotoTopicNum();
        int trackwayCount = userCard.getTravelTogetherNum();

        zanTextview.setText(String.format("%d\n图说", captionCount));
        trackwayTextview.setText(String.format("%d\n旅拼", trackwayCount));
        followTextview.setText(String.format("%d\n关注", userCounts.getFriendsNum()));
        fansTextview.setText(String.format("%d\n粉丝", userCounts.getFansNum()));

        // 点击查看用户图像
        mAvatarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isMine) {
                    showAvatarDialog(userCard.getAvatar());//查看用户图像
                } else {//弹出编辑和设置按钮
//                    onAvatarClick(view);
                }
            }
        });


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
//      mNickName.setText(userCard.getNick());
        if (!TextUtils.isEmpty(userCard.getRemark())) {//判断是否有备注名
            mNickName.setText(userCard.getRemark());
        } else {
            //mNickName.setText(userCard.getNick());
            mNickName.setText(EmojiUtils.fromStringToEmoji1(userCard.getNick(), getContext()));
        }

        if (isOfficial) {
            mXingkaNumber.setVisibility(View.GONE);
            mXingkaNoTv.setVisibility(View.GONE);
            officalBtn.setVisibility(View.VISIBLE);
            mBgToolBarrelative.setVisibility(View.GONE);
        } else
            mXingkaNumber.setText(String.valueOf(userCard.getAccount()));

        if (!isMine) {
            if (isOfficial) {
                fansfollowLayout.setVisibility(View.GONE);
                mBgToolBarrelative.setVisibility(View.GONE);
            }
            if (!isIMReg) { //如果没有注册云信,隐藏加好友
                if (isOfficial) {
                    mAttentionFriend.setVisibility(View.GONE);
                } else {
                    mAttentionFriend.setVisibility(View.VISIBLE);
                }
                mIsIMFriend.setVisibility(View.GONE);

                return;
            } else {
                mAttentionFriend.setVisibility(View.VISIBLE);
                mIsIMFriend.setVisibility(View.VISIBLE);
                if (isIMFriend) {
                    mIsIMFriend.setChecked(true);
                    mIsIMFriend.setText("发消息");
                    mIsIMFriend.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) { //进入好友聊天
                            FriendActivity.launch(getActivity());
                        }
                    });
                }
            }

            if (nexus == 1 || nexus == 2) { //已经关注的
                if (isOfficial) {
                    mAttentionFriend.setVisibility(View.GONE);
                } else {
                    mAttentionFriend.setChecked(true);
                    mAttentionFriend.setVisibility(View.VISIBLE);
                    mAttentionFriend.setText("已关注");
                    //设置已关注后按钮没有点击效果
                    mAttentionFriend.setClickable(false);
                }
                //mRemark.setImageResource(R.mipmap.icon_remark);
            } else { //未关注
                mAttentionFriend.setVisibility(View.VISIBLE);
            }

//            if(isIMFriend){
//                mIsIMFriend.setVisibility(View.VISIBLE);
//                mIsIMFriend.setChecked(true);
//                mIsIMFriend.setText("发消息");
//                mIsIMFriend.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) { //进入好友聊天
//                        FriendActivity.launch(getActivity());
//                    }
//                });
//            }
        }

        //******************  图说和旅拼的滑动处理  *******************
        final GridLayoutManager manager = new GridLayoutManager(getActivity(), 1);
        manager.setOrientation(GridLayoutManager.VERTICAL);

        if (photoTopicCount > 0 || photoTopicCount == trackwayCount) {
            manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return userCaptionAdapter.getItemViewType(position) == RecyclerViewHeaderAdapter.INT_TYPE_HEADER ? 1 : 1;
                }
            });
            userCaptionAdapter = new UserCaptionAdapter(getActivity());
            userCaptionAdapter.setOnItemClickListener(this);
            getCaptionData();
            mZoomRecyclerview.setAdapterAndLayoutManager(userCaptionAdapter, manager);
            zanTextview.setTextColor(Color.parseColor("#ff7800"));
            onScolledCaption(manager); //图说上拉滑动分页

        } else if (photoTopicCount == 0 && trackwayCount > 0) {
            manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return userTrackwayAdapter.getItemViewType(position) == RecyclerViewHeaderAdapter.INT_TYPE_HEADER ? 1 : 1;
                }
            });
            userTrackwayAdapter = new UserTrackwayAdapter(getActivity());
            userTrackwayAdapter.setOnItemClickListener(this);
            getTrackwayData();
            mZoomRecyclerview.setAdapterAndLayoutManager(userTrackwayAdapter, manager);
            trackwayTextview.setTextColor(Color.parseColor("#ff7800"));
            onScolledTracker(manager); //旅拼上拉滑动分页
        }

        // *********************  背景墙的毛玻璃处理  *****************
        final String bgImg = userCard.getBgImg();
        final String url = QiniuHelper.getOriginalWithKey(bgImg);

        //默认背景墙毛玻璃实现
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

        //----------------------------------- 背景墙 --------------------------------------
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
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == RESULT_REQUEST_BACKGROUD) {//选择背景墙图片
//            if (resultCode == Activity.RESULT_OK) {
//                if (data != null) {
//                    fileUri = data.getData();
//                }
//                fileCropUri = getOutputMediaFileUri();
//                Intent intent = new Intent(getActivity(), CropBgImageActivity.class);
//                // 获取这个图片的URI
//                String filePath = AppUtility.getFilePath(getActivity(), fileUri);
//                intent.putExtra(CropBgImageActivity.ARG_CROP_IMAGE_PATH, filePath);
//                startActivityForResult(intent, RESULT_REQUEST_BACKGROUD_CROP);
////                cropImageUri(fileUri, fileCropUri, AppUtility.getScreenWidth(), AppUtility.getScreenWidth(), RESULT_REQUEST_BACKGROUD_CROP);
//            }
//        } else if (requestCode == RESULT_REQUEST_BACKGROUD_CROP) {
//            if (resultCode == Activity.RESULT_OK) {
////                String filePath = AppUtility.getFilePath(getActivity(), fileCropUri);
//                Bundle bundle = data.getExtras();
//                String cropImagePath = bundle.getString("cropImagePath");
//                File avatarFile = new File(cropImagePath);
//                Logger.e("filePath:" + cropImagePath);
//                // Todo: 将背景图片文件上传到指定服务器上
//                updateAvatarToQiniu(avatarFile, 1);
//            }
//        }else
        if (requestCode == 200 && resultCode == Activity.RESULT_OK) {
            Bundle bundle = data.getExtras();
            remark = bundle.getString("remarkIM");
//            Log.e("TAG", "remarkIM->" + remark);
            mNickName.setText(remark);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    // 退出登录
    private void logout() {
        AccountInfo.getInstance().clearAccount();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        XKApplication.getInstance().exit();
    }

    // ******************************* 获取用户的图说  ******************************
    private void getCaptionData() {
        String sAccount = Security.aesEncrypt(mUserAccount);
        String sIsHidden = Security.aesEncrypt(String.valueOf(0));
        String sPage = Security.aesEncrypt(String.valueOf(mPage));
        String sPerPage = Security.aesEncrypt(String.valueOf(mPerPage));
        ApiModule.apiService_1().myPhotoTopic(sAccount, sIsHidden, sPage, sPerPage).enqueue(new Callback<UserPhotoTopicRepo>() {
            @Override
            public void onResponse(Call<UserPhotoTopicRepo> call, Response<UserPhotoTopicRepo> response) {
                ProgressHUD.dismiss();
                UserPhotoTopicRepo repo = response.body();
                if (repo.isSuccess()) {
                    isLoadingMore = true;
                    mMaxPage = (repo.getRecordCount() + mPerPage - 1) / mPerPage;
                    mPhotoTopics.addAll(repo.getUserPhotoTopicList());
                    userCaptionAdapter.notifyDataChanged(mPhotoTopics, true);
                } else {
                    isLoadingMore = true;
                    mPage = mPage - 1;
                    //图说为空时
                    if (repo.getRecordCount() == 0) {
                        mEmpty.setVisibility(View.VISIBLE);
                    } else {
                        mEmpty.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<UserPhotoTopicRepo> call, Throwable t) {
                ProgressHUD.dismiss();
                isLoadingMore = true;
                mPage = mPage - 1;
                mError.setVisibility(View.VISIBLE);
                //点击重试按钮重新网络请求
                mError.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getCaptionData();
                    }
                });
                ProgressHUD.showErrorMessage(getActivity(), getString(R.string.common_network_error));
            }
        });

    }

    // ********************************  获取用户的旅拼  ******************************
    private void getTrackwayData() {
        String sAccount = Security.aesEncrypt(mUserAccount);
        String sIsHidden = Security.aesEncrypt(String.valueOf(0));
        String sPage = Security.aesEncrypt(String.valueOf(mPage1));
        String sPerPage = Security.aesEncrypt(String.valueOf(mPerPage));
        ApiModule.apiService_1().myTravelTogether(sAccount, sIsHidden, sPage, sPerPage).enqueue(new Callback<MyTrackwayRepo>() {
            @Override
            public void onResponse(Call<MyTrackwayRepo> call, Response<MyTrackwayRepo> response) {
                ProgressHUD.dismiss();
                MyTrackwayRepo repo = response.body();
                if (repo.isSuccess()) {
                    isLoadingMore = true;
                    mMaxPage = (repo.getRecordCount() + mPerPage - 1) / mPerPage;
                    myTrackwayList.addAll(repo.getUserTrackwayInfoList());
                    userTrackwayAdapter.notifyDataChanged(myTrackwayList, true);
                } else {
                    isLoadingMore = true;
                    mPage1 = mPage1 - 1;
                    //旅拼为空时
                    if (repo.getRecordCount() == 0) {
                        mEmpty.setVisibility(View.VISIBLE);
                    } else {
                        mEmpty.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<MyTrackwayRepo> call, Throwable t) {
                ProgressHUD.dismiss();
                isLoadingMore = true;
                mPage1 = mPage1 - 1;
                mError.setVisibility(View.VISIBLE);
                //点击重试按钮重新网络请求
                mError.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getTrackwayData();
                    }
                });
                ProgressHUD.showErrorMessage(getActivity(), getString(R.string.common_network_error));
            }
        });

    }

    @Override
    public void onItemClick(View view, int position) {
        if (photoTopicCount > 0 || photoTopicCount == trackwayCount) {
            if (mType == 1) { //图说
                UserPhotoTopic photoTopic = userCaptionAdapter.getData().get(position);
                String pGuid = photoTopic.getpGuid();
                CaptionDetailActivity.launch(getActivity(), pGuid, mUserAccount);
            } else if (mType == 2) { //旅拼
                MyTrackway myTrackway = userTrackwayAdapter.getData().get(position);
                String tGuid = myTrackway.gettGuid();
                TrackwayDetailActivity.launch(getActivity(), tGuid, mUserAccount);
            }

        } else if (photoTopicCount == 0 && trackwayCount > 0) { //旅拼
            MyTrackway myTrackway = userTrackwayAdapter.getData().get(position);
            String tGuid = myTrackway.gettGuid();
            TrackwayDetailActivity.launch(getActivity(), tGuid, mUserAccount);
        }

    }

    @Override
    public void onItemLongClick(View view, int position) {
    }

    @Override
    public void onItemChildClick(View childView, int position) {
    }


    /**
     * 显示头像大图
     */
    private void showAvatarDialog(String avatarKey) {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ViewAvatarFragment avatarFragment = ViewAvatarFragment.newInstance(avatarKey);
        avatarFragment.show(ft, "dialog");
    }

    //***************************  举报成功数据接口  *******************************
    private void reportUser() {
        String sAccount = Security.aesEncrypt(mMyAccount);
        String sUserAcount = Security.aesEncrypt(mUserAccount);
        String sType = Security.aesEncrypt("0");
        ApiModule.apiService().reportUser(sAccount,
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

    //**************************************************  毛玻璃效果 ****************************************************
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

                    if (DEFAULT_BACKGROUND_KEY.equals(bgImg)) { //默认背景
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
                    } else { //自定义背景
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

    //**************************************** 图说上拉滑动分页  *************************************
    private void onScolledCaption(final GridLayoutManager manager1) {
        mRecycleview.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItem = manager1.findLastVisibleItemPosition();
                int totalItemCount = 0;
                totalItemCount = userCaptionAdapter.getItemCount();
                totalDy += dy;
                if (totalDy > AppUtility.dip2px(250)) {
                    mToolbar.setVisibility(View.VISIBLE);
                    mToolbarRelative.setVisibility(View.GONE);
                } else {
                    mToolbarRelative.setVisibility(View.VISIBLE);
                    mToolbar.setVisibility(View.GONE);
                }
                // dy>0 表示向上滑动
                if (lastVisibleItem >= totalItemCount - 1 && dy > 0 && mPage < mMaxPage) {
                    if (isLoadingMore) {
                        if (lastVisibleItem == totalItemCount - 1 && mPage < mMaxPage) {
                            ProgressHUD.showLoadingMessage(getActivity(), "正在加载中...", true);
                        }
                        mPage++;
                        String sAccount = Security.aesEncrypt(mUserAccount);
                        String sIsHidden = Security.aesEncrypt(String.valueOf(0));
                        String sPage = Security.aesEncrypt(String.valueOf(mPage));
                        String sPerPage = Security.aesEncrypt(String.valueOf(mPerPage));
                        ApiModule.apiService_1().myPhotoTopic(sAccount, sIsHidden, sPage, sPerPage).enqueue(new Callback<UserPhotoTopicRepo>() {
                            @Override
                            public void onResponse(Call<UserPhotoTopicRepo> call, Response<UserPhotoTopicRepo> response) {
                                ProgressHUD.dismiss();
                                UserPhotoTopicRepo repo = response.body();
                                if (repo.isSuccess()) {
                                    isLoadingMore = true;
                                    mMaxPage = (repo.getRecordCount() + mPerPage - 1) / mPerPage;
                                    mPhotoTopics.addAll(repo.getUserPhotoTopicList());
                                    userCaptionAdapter.notifyDataChanged(mPhotoTopics, true);
                                }
                            }

                            @Override
                            public void onFailure(Call<UserPhotoTopicRepo> call, Throwable t) {
                                ProgressHUD.dismiss();
                            }
                        });
                        isLoadingMore = false;
                    }
                }
            }
        });
    }

    //******************************  旅拼上拉滑动分页  *****************************************
    private void onScolledTracker(final GridLayoutManager manager2) {

        mRecycleview.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItem = manager2.findLastVisibleItemPosition();
                int totalItemCount = 0;
                totalItemCount = userTrackwayAdapter.getItemCount();

                totalDy += dy;
                if (totalDy > AppUtility.dip2px(250)) {
                    mToolbar.setVisibility(View.VISIBLE);
                    mToolbarRelative.setVisibility(View.GONE);
                } else {
                    mToolbarRelative.setVisibility(View.VISIBLE);
                    mToolbar.setVisibility(View.GONE);
                }
                // dy>0 表示向上滑动
                if (lastVisibleItem >= totalItemCount - 1 && dy > 0 && mPage1 < mMaxPage) {
                    if (isLoadingMore) {
                        if (lastVisibleItem == totalItemCount - 1 && mPage1 < mMaxPage) {
                            ProgressHUD.showLoadingMessage(getActivity(), "正在加载中...", true);
                        }
                        mPage1++;
                        String sAccount = Security.aesEncrypt(mUserAccount);
                        String sIsHidden = Security.aesEncrypt(String.valueOf(0));
                        String sPage = Security.aesEncrypt(String.valueOf(mPage1));
                        String sPerPage = Security.aesEncrypt(String.valueOf(mPerPage));
                        ApiModule.apiService_1().myTravelTogether(sAccount, sIsHidden, sPage, sPerPage).enqueue(new Callback<MyTrackwayRepo>() {
                            @Override
                            public void onResponse(Call<MyTrackwayRepo> call, Response<MyTrackwayRepo> response) {
                                ProgressHUD.dismiss();
                                MyTrackwayRepo repo = response.body();
                                if (repo.isSuccess()) {
                                    isLoadingMore = true;
                                    mMaxPage = (repo.getRecordCount() + mPerPage - 1) / mPerPage;
                                    myTrackwayList.addAll(repo.getUserTrackwayInfoList());
                                    userTrackwayAdapter.notifyDataChanged(myTrackwayList, true);
                                }
                            }

                            @Override
                            public void onFailure(Call<MyTrackwayRepo> call, Throwable t) {
                                ProgressHUD.dismiss();
                            }
                        });
                        isLoadingMore = false;
                    }
                }
            }
        });
    }


    //******************************************************** 好友旅拼适配器  **************************************************************
    public class UserTrackwayAdapter extends RecyclerViewHeaderAdapter<ViewHolderRecyclerPullToZoom> implements IDataAdapter<List<MyTrackway>> {
        public OnItemClickListener mOnItemClickListener;
        private List<MyTrackway> myTrackwayList = new ArrayList<>();

        public void setOnItemClickListener(OnItemClickListener listener) {
            this.mOnItemClickListener = listener;
        }

        public UserTrackwayAdapter(Context context) {
            super(context);
        }

        @Override
        public int getCount() {
            return myTrackwayList.size();
        }

        @Override
        public void notifyDataChanged(List<MyTrackway> data, boolean isRefresh) {
            if (isRefresh) {
                myTrackwayList.clear();
            }
            myTrackwayList.addAll(data);
            notifyDataSetChanged();
        }

        @Override
        public List<MyTrackway> getData() {
            return myTrackwayList;
        }

        @Override
        public boolean isEmpty() {
            return myTrackwayList.isEmpty();
        }

        @Override
        public ViewHolderRecyclerPullToZoom onCreateContentView(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_user_caption, parent, false);
            return new ViewHolderRecyclerPullToZoom(itemView);
        }

        @Override
        public void onBindView(ViewHolderRecyclerPullToZoom view, final int position) {
            MyTrackway myTrackway = myTrackwayList.get(position);
            view.num.setText(myTrackway.getReadNum());

            if (position == 0) {
                view.timelineTop.setVisibility(View.INVISIBLE);
            } else {
                view.timelineTop.setVisibility(View.VISIBLE);
            }
            if (position == myTrackwayList.size() - 1) {
                view.timelineBottom.setVisibility(View.INVISIBLE);
                view.mComplete.setVisibility(View.VISIBLE);
            } else {
                view.mComplete.setVisibility(View.GONE);
                view.timelineBottom.setVisibility(View.VISIBLE);
            }

            List<ImagesMessage> imagesList = myTrackway.getImagesMessage();
            if (imagesList.isEmpty()) {
                view.moreImage.setVisibility(View.GONE);
                view.oneImage.setVisibility(View.GONE);
            } else if (imagesList.size() == 1) {
                view.moreImage.setVisibility(View.GONE);
                view.oneImage.setVisibility(View.VISIBLE);

                ImagesMessage image = imagesList.get(0);
                String imageUrl = QiniuHelper.getOriginalWithKey(image.getImgSrc());
                view.oneImage.setImageUrl(imageUrl);
            } else {
                view.moreImage.setVisibility(View.VISIBLE);
                view.oneImage.setVisibility(View.GONE);

                List<String> imageUrls = new ArrayList<>();
                for (ImagesMessage image : imagesList) {
                    imageUrls.add(QiniuHelper.getOriginalWithKey(image.getImgSrc()));
                }
                view.moreImage.setImagesData(imageUrls);
            }

            // 月份/日期显示
            String datetime = myTrackway.getAddTime().substring(6, 19);
            long timestamp = Long.valueOf(datetime);
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTimeInMillis(timestamp);
            int month = calendar.get(GregorianCalendar.MONTH);
            int day = calendar.get(GregorianCalendar.DAY_OF_MONTH);

            view.month.setText(Global.getMonth(month + 1));
            //判断日期显示
            if (day < 10) {
                view.day.setText(0 + String.valueOf(day));
            } else {
                view.day.setText(String.valueOf(day));
            }

            // 判断图说列表作者是否是当前登录用户,如果不是则不显示删除按钮
            if (mUserAccount.equals(String.valueOf(mCurrentUser.getAccount()))) {
                view.deleteButton.setVisibility(View.VISIBLE);
            } else {
                view.deleteButton.setVisibility(View.GONE);
            }

            // 添加点击和长按事件监听
            if (mOnItemClickListener != null) {
                view.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickListener.onItemClick(v, position);
                    }
                });
                view.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        mOnItemClickListener.onItemLongClick(v, position);
                        return true;
                    }
                });

                // 添加删除旅拼按钮
                view.deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickListener.onItemChildClick(v, position);
                    }
                });
            }
        }
    }

    //********************************************************  好友图说适配器  ************************************************
    public class UserCaptionAdapter extends RecyclerViewHeaderAdapter<ViewHolderRecyclerPullToZoom> implements IDataAdapter<List<UserPhotoTopic>> {
        public OnItemClickListener mOnItemClickListener;
        private List<UserPhotoTopic> photoTopics = new ArrayList<>();

        public void setOnItemClickListener(OnItemClickListener listener) {
            this.mOnItemClickListener = listener;
        }

        public UserCaptionAdapter(Context context) {
            super(context);
        }

        @Override
        public int getCount() {
            return photoTopics.size();
        }

        @Override
        public void notifyDataChanged(List<UserPhotoTopic> data, boolean isRefresh) {
            if (isRefresh) {
                photoTopics.clear();
            }
            photoTopics.addAll(data);
            notifyDataSetChanged();
        }

        @Override
        public List<UserPhotoTopic> getData() {
            return photoTopics;
        }

        @Override
        public boolean isEmpty() {
            return photoTopics.isEmpty();
        }

        @Override
        public ViewHolderRecyclerPullToZoom onCreateContentView(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_user_caption, parent, false);
            return new ViewHolderRecyclerPullToZoom(itemView);
        }

        @Override
        public void onBindView(ViewHolderRecyclerPullToZoom view, final int position) {
            UserPhotoTopic photoTopic = photoTopics.get(position);
            view.num.setText(photoTopic.getReadNum());

            if (position == 0) {
                view.timelineTop.setVisibility(View.INVISIBLE);
            } else {
                view.timelineTop.setVisibility(View.VISIBLE);
            }
            if (position == photoTopics.size() - 1) {
                view.timelineBottom.setVisibility(View.INVISIBLE);
                view.mComplete.setVisibility(View.VISIBLE);
            } else {
                view.mComplete.setVisibility(View.GONE);
                view.timelineBottom.setVisibility(View.VISIBLE);
            }

            List<ImagesMessage> imagesList = photoTopic.getImagesMessage();
            if (imagesList.isEmpty()) {
                view.moreImage.setVisibility(View.GONE);
                view.oneImage.setVisibility(View.GONE);
            } else if (imagesList.size() == 1) {
                view.moreImage.setVisibility(View.GONE);
                view.oneImage.setVisibility(View.VISIBLE);

                ImagesMessage image = imagesList.get(0);
                String imageUrl = QiniuHelper.getOriginalWithKey(image.getImgSrc());
                view.oneImage.setImageUrl(imageUrl);
            } else {
                view.moreImage.setVisibility(View.VISIBLE);
                view.oneImage.setVisibility(View.GONE);

                List<String> imageUrls = new ArrayList<>();
                for (ImagesMessage image : imagesList) {
                    imageUrls.add(QiniuHelper.getOriginalWithKey(image.getImgSrc()));
                }
                view.moreImage.setImagesData(imageUrls);
            }

            // 月份/日期显示
            String datetime = photoTopic.getAddTime().substring(6, 19);
            long timestamp = Long.valueOf(datetime);
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTimeInMillis(timestamp);
            int month = calendar.get(GregorianCalendar.MONTH);
            int day = calendar.get(GregorianCalendar.DAY_OF_MONTH);

            view.month.setText(Global.getMonth(month + 1));
            //判断日期显示
            if (day < 10) {
                view.day.setText(0 + String.valueOf(day));
            } else {
                view.day.setText(String.valueOf(day));
            }

            // 判断图说列表作者是否是当前登录用户,如果不是则不显示删除按钮
            if (mUserAccount.equals(String.valueOf(mCurrentUser.getAccount()))) {
                view.deleteButton.setVisibility(View.VISIBLE);
            } else {
                view.deleteButton.setVisibility(View.GONE);
            }

            // 添加点击和长按事件监听
            if (mOnItemClickListener != null) {
                view.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickListener.onItemClick(v, position);
                    }
                });
                view.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        mOnItemClickListener.onItemLongClick(v, position);
                        return true;
                    }
                });

                // 添加删除图说按钮
                view.deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickListener.onItemChildClick(v, position);
                    }
                });
            }
        }
    }

    public static class ViewHolderRecyclerPullToZoom extends RecyclerView.ViewHolder {
        @BindView(R.id.num)
        TextView num;
        @BindView(R.id.iv_ngrid_layout)
        NineGridlayout moreImage;
        @BindView(R.id.iv_one_image)
        CustomImageView oneImage;
        @BindView(R.id.timeline_top)
        View timelineTop;
        @BindView(R.id.timeline_bottom)
        View timelineBottom;
        @BindView(R.id.tv_month)
        TextView month;
        @BindView(R.id.tv_day)
        TextView day;
        @BindView(R.id.tv_delete)
        IconicFontTextView deleteButton;
        @BindView(R.id.tv_complete)
        TextView mComplete;
//        @Bind(R.id.ll_loding)
//        LinearLayout mLoding;

        @Override
        public String toString() {
            return super.toString();
        }

        public ViewHolderRecyclerPullToZoom(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


//    private void showChangeBGDialog() {//修改背景
//        //final String[] stringItems = {"去拍一张美图", "去我的相册中选择", "去背景墙选择"};
//        final String[] stringItems = {"去拍一张美图", "去我的相册中选择"};
//        final ActionSheetDialog dialog = new ActionSheetDialog(getActivity(), stringItems, null);
//        dialog.isTitleShow(false).show();
//        //dialog.title("分享名片").isTitleShow(false).show();
//        //弹出框背景色为白色
//        dialog.lvBgColor(Color.parseColor("#ffffff"));
////            dialog.itemTextColor(Color.parseColor("#ff2814"));
//        dialog.setOnOperItemClickL(new OnOperItemClickL() {
//            @Override
//            public void onOperItemClick(AdapterView<?> adapterView, View view, int position, long id) {
//                if (position == 0) {
//                    //去拍一张美图
//                    backgroupFromCamera();
//                } else if (position == 1) {
//                    //去我的相册中选择
//                    backgroupFromPhoto();
//                }
//                dialog.dismiss();
//            }
//        });
//
//    }
//
//    public void backgroupFromCamera() {
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        fileUri = getOutputMediaFileUri();
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
//        startActivityForResult(intent, RESULT_REQUEST_BACKGROUD);
//    }
//
//    public void backgroupFromPhoto() {
//        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(i, RESULT_REQUEST_BACKGROUD);
//    }
//
//    public Uri getOutputMediaFileUri() {
//        File mediaStorageDir = new File(
//                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
//                "XKFile");
//        if (!mediaStorageDir.exists()) {
//            if (!mediaStorageDir.mkdirs()) {
//                Logger.d("XKFile", "failed to create directory");
//                return null;
//            }
//        }
//
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp
//                + ".jpg");
//        return Uri.fromFile(mediaFile);
//    }
//
//    protected void cropImageUri(Uri uri, Uri outputUri, int outputX, int outputY, int requestCode) {
//        Intent intent = new Intent("com.android.camera.action.CROP");
//        intent.setDataAndType(uri, "image/*");
//        intent.putExtra("crop", "true");
//        intent.putExtra("aspectX", 1);
//        intent.putExtra("aspectY", 1);
//        intent.putExtra("outputX", outputX);
//        intent.putExtra("outputY", outputY);
//        intent.putExtra("scale", true);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
//        intent.putExtra("return-data", false);
//        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
//        intent.putExtra("noFaceDetection", true); // no face detection
//        startActivityForResult(intent, requestCode);
//    }
//
//    // *********************************  头像文件上传至七牛接口  ********************************
//    private void updateAvatarToQiniu(final File avatarFile, final int type) {
//        ProgressHUD.showLoadingMessage(getActivity(), "正在更新", true);
//        String randomStr = Security.aesEncrypt("android");
//        ApiModule.apiService().getQiniuToken(randomStr).enqueue(new Callback<XKRepo>() {
//            @Override
//            public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
//                XKRepo xkRepo = response.body();
//                if (xkRepo.isSuccess()) {
//                    String token = xkRepo.getMsg();
//                    upload(token, avatarFile, type);
//                } else {
//                    ProgressHUD.dismiss();
//                    ProgressHUD.showInfoMessage(getActivity(), xkRepo.getMsg());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<XKRepo> call, Throwable t) {
//                ProgressHUD.showErrorMessage(getActivity(), getString(R.string.common_network_error));
//            }
//
//        });
//    }
//
//    private void upload(String uploadToken, final File avatarFile, final int type) {
//        if (this.uploadManager == null) {
//            this.uploadManager = new UploadManager();
//        }
//        String uploadFilKey = AppUtility.generateUUID();
//        this.uploadManager.put(avatarFile, uploadFilKey, uploadToken, new UpCompletionHandler() {
//            @Override
//            public void complete(String key, ResponseInfo info, JSONObject response) {
//                if (info.isOK()) {
//                    String fileKey = response.optString("key");
//                    Logger.d(fileKey);
//                    if (type == 0) {//修改头像
////                        updateAvatarKey(fileKey, avatarFile);
//                    } else if (type == 1) {//修改背景墙
//                        updateBackGroud(fileKey, avatarFile);
//                    }
//
//                }
//            }
//        }, null);
//    }
//
//    //************************************  修改背景墙  ***********************************
//    private void updateBackGroud(final String key, final File imageFile) {
//        String account = Security.aesEncrypt(String.valueOf(mCurrentUser.getAccount()));
//        String type = Security.aesEncrypt("bgimg");
//        String value = Security.aesEncrypt(key);
//        ApiModule.apiService().editUserInfo(account,
//                type, value).enqueue(new Callback<XKRepo>() {
//            @Override
//            public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
//                XKRepo xkRepo = response.body();
//                Logger.i(xkRepo.getMsg());
//                ProgressHUD.dismiss();
//                if (xkRepo.isSuccess()) {
//                    ProgressHUD.showSuccessMessage(getActivity(), "修改背景墙成功!");
//                    // 更新本地头像信息
//                    mCurrentUser.setBackgroud(key);
//                    Glide.with(getActivity())
//                            .load(imageFile.getPath())
//                            .placeholder(R.mipmap.userbg)
//                            .error(R.mipmap.userbg)
//                            .into(bgImageview);
//                    //Drawable drawable = new BitmapDrawable(BitmapFactory.decodeFile(imageFile.getPath()));
//                    //userInfoLayout.setBackground(drawable);
//                    AccountInfo.getInstance().saveAccount(mCurrentUser);
//                } else {
//                    ProgressHUD.showSuccessMessage(getActivity(), xkRepo.getMsg());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<XKRepo> call, Throwable t) {
//                ProgressHUD.showErrorMessage(getActivity(), getString(R.string.common_network_error));
//            }
//
//        });
//    }


//    private void onAvatarClick(View v) {
//        if (v.isSelected()) {
//            hideMenu();
//        } else {
//            showMenu();
//        }
//        v.setSelected(!v.isSelected());
//    }
//
//    @SuppressWarnings("NewApi")
//    private void hideMenu() {
//
//        List<Animator> animList = new ArrayList<>();
//
//        for (int i = arcLayout.getChildCount() - 1; i >= 0; i--) {
//            animList.add(createHideItemAnimator(arcLayout.getChildAt(i)));
//        }
//
//        AnimatorSet animSet = new AnimatorSet();
//        animSet.setDuration(400);
//        animSet.setInterpolator(new AnticipateInterpolator());
//        animSet.playTogether(animList);
//        animSet.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                super.onAnimationEnd(animation);
//                menuLayout.setVisibility(View.INVISIBLE);
//            }
//        });
//        animSet.start();
//
//    }
//
//    @SuppressWarnings("NewApi")
//    private void showMenu() {
//        menuLayout.setVisibility(View.VISIBLE);
//
//        List<Animator> animList = new ArrayList<>();
//
//        for (int i = 0, len = arcLayout.getChildCount(); i < len; i++) {
//            animList.add(createShowItemAnimator(arcLayout.getChildAt(i)));
//        }
//
//        AnimatorSet animSet = new AnimatorSet();
//        animSet.setDuration(400);
//        animSet.setInterpolator(new OvershootInterpolator());
//        animSet.playTogether(animList);
//        animSet.start();
//    }
//
//    private Animator createShowItemAnimator(View item) {
//
//        float dx = avatarLayout.getX() - item.getX();
//        float dy = avatarLayout.getY() - item.getY();
//
//        item.setRotation(0f);
//        item.setTranslationX(dx);
//        item.setTranslationY(dy);
//
//        Animator anim = ObjectAnimator.ofPropertyValuesHolder(
//                item,
//                AnimatorUtils.rotation(0f, 720f),
//                AnimatorUtils.translationX(dx, 0f),
//                AnimatorUtils.translationY(dy, 0f)
//        );
//
//        return anim;
//    }
//
//    private Animator createHideItemAnimator(final View item) {
//        float dx = avatarLayout.getX() - item.getX();
//        float dy = avatarLayout.getY() - item.getY();
//
//        Animator anim = ObjectAnimator.ofPropertyValuesHolder(
//                item,
//                AnimatorUtils.rotation(720f, 0f),
//                AnimatorUtils.translationX(0f, dx),
//                AnimatorUtils.translationY(0f, dy)
//        );
//
//        anim.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                super.onAnimationEnd(animation);
//                item.setTranslationX(0f);
//                item.setTranslationY(0f);
//            }
//        });
//
//        return anim;
//    }
}

package com.ruihai.xingka.ui.mine.fragment;


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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;
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
import com.ruihai.xingka.api.model.OfficialPhotoTopicTypeRepo;
import com.ruihai.xingka.api.model.User;
import com.ruihai.xingka.api.model.UserCard;
import com.ruihai.xingka.api.model.UserPhotoTopic;
import com.ruihai.xingka.api.model.UserPhotoTopicRepo;
import com.ruihai.xingka.event.OnItemClickListener;
import com.ruihai.xingka.event.UpdateCountEvent;
import com.ruihai.xingka.ui.caption.CaptionDetailActivity;
import com.ruihai.xingka.ui.caption.ShareCaptionActivity;
import com.ruihai.xingka.ui.common.enter.EmojiUtils;
import com.ruihai.xingka.ui.login.LoginActivity;
import com.ruihai.xingka.ui.mine.OfficalPhotoTopicListActivity;
import com.ruihai.xingka.ui.mine.adapter.OfficalPhotoTopicTypeAdapter;
import com.ruihai.xingka.utils.AppUtility;
import com.ruihai.xingka.utils.QiniuHelper;
import com.ruihai.xingka.utils.Security;
import com.ruihai.xingka.widget.CustomImageView;
import com.ruihai.xingka.widget.HorizontalListView;
import com.ruihai.xingka.widget.JavaBlurProcess;
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
 * A simple {@link Fragment} subclass.
 */
public class OfficalAccountFragment extends Fragment implements View.OnClickListener, OnItemClickListener, AdapterView.OnItemClickListener {

    private static final String ARG_LAST_SCROLL_Y = "arg.LastScrollY";
    public final int RESULT_REQUEST_PHOTO = 0x1005;
    public final int RESULT_REQUEST_PHOTO_CROP = 0x1006;
    public final int RESULT_REQUEST_BACKGROUD = 0x1007;
    public final int RESULT_REQUEST_BACKGROUD_CROP = 0x1008;
    public static final String DEFAULT_BACKGROUND_KEY = "00000000-0000-0000-0000-000000000000";
    //    @BindView(R.id.id_swiperefreshlayout)
//    SwipeRefreshLayout mSwipeRefreshLayout;
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

    //    @BindView(R.id.user_info_layout)
    RelativeLayout userInfoLayout;
    //    @BindView(R.id.bg_image)
    ImageView bgImageview;

    // 用户信息展示控件
//    @BindView(R.id.sdv_avatar)
    ImageView mAvatarView;
    //    @BindView(R.id.iv_sex)
    ImageView mSex;
    //    @BindView(R.id.tv_note)
    TextView mNote;
    //    @BindView(R.id.tv_nickname)
    TextView mNickName;


    FrameLayout avatarLayout;
    HorizontalListView horizontalListView;

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

    private User mCurrentUser; // 我的信息类
    private String mMyAccount; // 我的行咖号
    private String mUserAccount; // 名片行咖号
    private boolean isMine = false; // 当前是否为自己的主页
    private LayoutInflater inflater;
    private UserCard mUserCard;//用户名片数据
    private int mFlag;

    private List<BaseScrollFragment> mFragments = new ArrayList<>();
    private Uri fileUri;
    private Uri fileCropUri;
    private UploadManager uploadManager;
    private OfficalCaptionAdapter userCaptionAdapter;
    private int mPage = 1;
    private int mMaxPage = 0;
    private int mPerPage = 10;
    private RecyclerView mRecycleview, mRecycleView1;
    private List<UserPhotoTopic> mPhotoTopics = new ArrayList<UserPhotoTopic>();
    private List<MyTrackway> myTrackwayList = new ArrayList<MyTrackway>();
    ;
    private boolean isLoadingMore = false;
    private MVCHelper<List<UserPhotoTopic>> listViewHelper;
    private int mPhotoCount;
    private int totalDy = 0;

    Bitmap overlay;
    JavaBlurProcess process = new JavaBlurProcess();

    private int mType; // 1:图说 2:旅拼
    private int trackwayCount, photoTopicCount;
    private List<OfficialPhotoTopicTypeRepo.OfficialPhotoTopicType> mListMessage = new ArrayList<>();

    //    private TranslateAnimation mShowAction;
    //    private TranslateAnimation mHiddenAction;
    public static OfficalAccountFragment newInstance(String userAccount, int flag, int type) {
        Bundle args = new Bundle();
        OfficalAccountFragment fragment = new OfficalAccountFragment();
        args.putInt("FLAG", flag);//0:UserProfileActivity  1:MainActivity
        args.putString("userAccount", userAccount);
        args.putInt("type", type); //1:图说 2:旅拼
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserAccount = getArguments().getString("userAccount");
        mFlag = getArguments().getInt("FLAG", 0);
        mType = getArguments().getInt("type", 1);
        mMyAccount = String.valueOf(AccountInfo.getInstance().loadAccount().getAccount());
        mCurrentUser = AccountInfo.getInstance().loadAccount();
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
//        final View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        final View view = inflater.inflate(R.layout.fragment_offical_account, container, false);
        ButterKnife.bind(this, view);
        ProgressHUD.showLoadingMessage(getActivity(), "正在加载中...", true);
        //请求接口获取数据
//         getCaptionData(); //图说数据
//         getTrackwayData(); //旅拼数据
        mRecycleview = mZoomRecyclerview.getPullRootView();

        View headView = LayoutInflater.from(getActivity()).inflate(R.layout.offical_account_header, null, false);
        View zoomView = LayoutInflater.from(getActivity()).inflate(R.layout.user_zoom, null, false);
        mZoomRecyclerview.setHeaderView(headView);
        mZoomRecyclerview.setZoomView(zoomView);

        // 获取组件
        userInfoLayout = (RelativeLayout) headView.findViewById(R.id.user_info_layout);
        mAvatarView = (ImageView) headView.findViewById(R.id.sdv_avatar);
        mSex = (ImageView) headView.findViewById(R.id.iv_sex);
        mNote = (TextView) headView.findViewById(R.id.tv_note);
        mNickName = (TextView) headView.findViewById(R.id.tv_nickname);

        avatarLayout = (FrameLayout) headView.findViewById(R.id.iv_logo);
        horizontalListView = (HorizontalListView) headView.findViewById(R.id.horizontal_listview);

        bgImageview = (ImageView) zoomView.findViewById(R.id.bg_image);


        //给可缩放头部设置属性
        mZoomRecyclerview.setParallax(true);
        int mScreenHeight = AppUtility.getScreenHeight();
        int mScreenWidth = AppUtility.getScreenWidth();
        AbsListView.LayoutParams localObject = new AbsListView.LayoutParams(mScreenWidth, (int) (7.0F * (mScreenHeight / 16.0F)));
        mZoomRecyclerview.setHeaderLayoutParams(localObject);

        userInfoLayout.setOnClickListener(this);
        mAvatarView.setOnClickListener(this);
        mReportTv.setOnClickListener(this);
        mLeftButton.setOnClickListener(this);
        mRight.setOnClickListener(this);
        mLeft.setOnClickListener(this);
        horizontalListView.setOnItemClickListener(this);

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


    //获取用户的图说
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
//                Log.e("TAG", "----->" +repo.getMsg());
                if (repo.isSuccess()) {
                    isLoadingMore = true;
                    mMaxPage = (repo.getRecordCount() + mPerPage - 1) / mPerPage;
//                    Log.e("图说页数", "---" + mMaxPage);
                    mPhotoTopics.addAll(repo.getUserPhotoTopicList());
                    userCaptionAdapter.notifyDataChanged(mPhotoTopics, true);
                } else {
                    isLoadingMore = true;
                    mPage = mPage - 1;
                    //好友图说为空时
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
                mEmpty.setVisibility(View.GONE);
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


    @Override
    public void onItemClick(View view, int position) {
        UserPhotoTopic photoTopic = userCaptionAdapter.getData().get(position);
        String pGuid = photoTopic.getpGuid();
        CaptionDetailActivity.launch(getActivity(), pGuid, mUserAccount);


    }

    @Override
    public void onItemLongClick(View view, int position) {
    }

    @Override
    public void onItemChildClick(View childView, int position) {

        UserPhotoTopic item = userCaptionAdapter.getData().get(position);
        item.setAccount(mUserAccount);
        switch (childView.getId()) {
            case R.id.tv_delete:
                ShareCaptionActivity.launch(getContext(), item, 1);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        OfficalPhotoTopicListActivity.launch(getContext(), mUserAccount, mListMessage.get(position));

    }


    //---------------------  好友图说适配器  -------------------------
    public class OfficalCaptionAdapter extends RecyclerViewHeaderAdapter<ViewHolderRecyclerPullToZoom> implements IDataAdapter<List<UserPhotoTopic>> {
        public OnItemClickListener mOnItemClickListener;
        private List<UserPhotoTopic> photoTopics = new ArrayList<>();

        public void setOnItemClickListener(OnItemClickListener listener) {
            this.mOnItemClickListener = listener;
        }

        public OfficalCaptionAdapter(Context context) {
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
                    .inflate(R.layout.item_offical_caption, parent, false);
            return new ViewHolderRecyclerPullToZoom(itemView);
        }

        @Override
        public void onBindView(ViewHolderRecyclerPullToZoom view, final int position) {
            UserPhotoTopic photoTopic = photoTopics.get(position);
            view.num.setText(photoTopic.getReadNum());

//            if (position == 0) {
//                view.timelineTop.setVisibility(View.INVISIBLE);
//            } else {
//                view.timelineTop.setVisibility(View.VISIBLE);
//            }
            if (position == photoTopics.size() - 1) {
                //  view.timelineBottom.setVisibility(View.INVISIBLE);
                view.mComplete.setVisibility(View.VISIBLE);
            } else {
                view.mComplete.setVisibility(View.GONE);
                //  view.timelineBottom.setVisibility(View.VISIBLE);
            }

            List<ImagesMessage> imagesList = photoTopic.getImagesMessage();
            if (imagesList.isEmpty()) {
                view.oneImage.setVisibility(View.GONE);
            } else {
                view.oneImage.setVisibility(View.VISIBLE);
                ImagesMessage image = imagesList.get(0);
                String imageUrl = QiniuHelper.getOriginalWithKey(image.getImgSrc());
                view.oneImage.setImageUrl(imageUrl);
            }
            // 月份/日期显示
            String datetime = photoTopic.getAddTime().substring(6, 19);
            long timestamp = Long.valueOf(datetime);
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTimeInMillis(timestamp);
            int month = calendar.get(GregorianCalendar.MONTH);
            int day = calendar.get(GregorianCalendar.DAY_OF_MONTH);

            view.monthTv.setText(Global.getMonth(month + 1));
            //判断日期显示
            if (day < 10) {
                view.dayTv.setText(0 + String.valueOf(day));
            } else {
                view.dayTv.setText(String.valueOf(day));
            }

            // 判断图说列表作者是否是当前登录用户,如果不是则不显示删除按钮
            view.deleteButton.setVisibility(View.VISIBLE);

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
        @BindView(R.id.iv_one_image)
        CustomImageView oneImage;
        @BindView(R.id.tv_month)
        TextView monthTv;
        @BindView(R.id.tv_day)
        TextView dayTv;
        @BindView(R.id.tv_delete)
        IconicFontTextView deleteButton;
        @BindView(R.id.tv_complete)
        TextView mComplete;

        @Override
        public String toString() {
            return super.toString();
        }

        public ViewHolderRecyclerPullToZoom(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_left:
            case R.id.head_tv_left: { // 返回
                getActivity().finish();
                break;
            }

        }

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getUserProfile();
        getOfficialPhotoTopicTypeList();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        horizontalListView.setBackgroundColor(Color.parseColor("#ffffff"));

    }

    private void getUserProfile() {
        // 获取名片的信息
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
                    initUserProfile(userCard);

                } else {
                    if (userCard.getCode() == 100) {
                        logout();
                    } else {
                        if (!OfficalAccountFragment.this.isDetached())
                            ProgressHUD.showInfoMessage(getContext(), userCard.getMsg());
                    }
                }
            }

            @Override
            public void onFailure(Call<UserCard> call, Throwable t) {
                ProgressHUD.dismiss();
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

    public void initUserProfile(final UserCard userCard) {//初始化用户信息
        final GridLayoutManager manager = new GridLayoutManager(getActivity(), 1);
        manager.setOrientation(GridLayoutManager.VERTICAL);
        photoTopicCount = userCard.getUserCounts().getPhotoTopicNum();

        userCaptionAdapter = new OfficalCaptionAdapter(getActivity());

        getCaptionData();
        userCaptionAdapter.setOnItemClickListener(this);

        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return userCaptionAdapter.getItemViewType(position) == RecyclerViewHeaderAdapter.INT_TYPE_HEADER ? 1 : 1;
            }
        });
        mZoomRecyclerview.setAdapterAndLayoutManager(userCaptionAdapter, manager);

        //---------------------  好友图说上拉滑动分页   --------------------------
        mRecycleview.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItem = manager.findLastVisibleItemPosition();
                int totalItemCount = 0;
                totalItemCount = userCaptionAdapter.getItemCount();
                //int totalItemCount = userTrackwayAdapter.getItemCount();
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
                        getCaptionData();
                        isLoadingMore = false;
                    }
                }
            }
        });


        Uri imageUri = Uri.parse(QiniuHelper.getThumbnail200Url(userCard.getAvatar()));
        if (!DEFAULT_BACKGROUND_KEY.equals(userCard.getAvatar())) {
            mAvatarView.setImageURI(imageUri);
        } else {
            mAvatarView.setImageURI(Uri.parse("res:///" + R.mipmap.default_avatar));
        }
        Logger.i(QiniuHelper.getThumbnail200Url(userCard.getAvatar()));
        if (isOfficial) {
            mReportTv.setVisibility(View.GONE);
            img_btn2.setVisibility(View.GONE);
            mRight.setVisibility(View.GONE);
        }

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

        // ---------------------------- 默认背景 -------------------------
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


        // Log.i("TAG", "----名片背景-->" + bgImg);
//        if (!DEFAULT_BACKGROUND_KEY.equals(bgImg)) {
//            GlideHelper.loadFullImageWithUrl(QiniuHelper.getOriginalWithKey(bgImg), bgImageview, new GlideHelper.ImageLoadingListener() {
//                @Override
//                public void onLoaded() {
//                    bgImageview.setImageResource(R.mipmap.userbg);
//                }
//
//                @Override
//                public void onFailed() {
//                    bgImageview.setImageResource(R.mipmap.userbg);
//                }
//            });
//        } else {
////                bgImageview.setBackground(XKApplication.getInstance().getResources().getDrawable(R.mipmap.userbg));
//            GlideHelper.loadResource(R.mipmap.userbg, bgImageview);
//        }

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

    private void getOfficialPhotoTopicTypeList() {
        // 获取分类的信息
        String version = AppUtility.getAppVersionName();
        String sVersion = Security.aesEncrypt(version);
        ApiModule.apiService_1().officialPhotoTopicTypeList(sVersion).enqueue(new Callback<OfficialPhotoTopicTypeRepo>() {
            @Override
            public void onResponse(Call<OfficialPhotoTopicTypeRepo> call, Response<OfficialPhotoTopicTypeRepo> response) {
                OfficialPhotoTopicTypeRepo officialPhotoTopicTypeRepo = response.body();
                if (officialPhotoTopicTypeRepo.isSuccess()) {
                    List<OfficialPhotoTopicTypeRepo.OfficialPhotoTopicType> listMessage = officialPhotoTopicTypeRepo.getListMessage();
                    mListMessage.addAll(listMessage);
                    OfficalPhotoTopicTypeAdapter adapter = new OfficalPhotoTopicTypeAdapter(getContext(), listMessage);
                    horizontalListView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<OfficialPhotoTopicTypeRepo> call, Throwable t) {

            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    /**
     * 显示头像大图
     */
    private void showAvatarDialog(String avatarKey) {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ViewAvatarFragment avatarFragment = ViewAvatarFragment.newInstance(avatarKey);
        avatarFragment.show(ft, "dialog");
    }

    // ---------------------------毛玻璃效果 ---------------------------------
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

}

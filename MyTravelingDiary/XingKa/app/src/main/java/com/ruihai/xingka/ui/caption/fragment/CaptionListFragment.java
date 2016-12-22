package com.ruihai.xingka.ui.caption.fragment;


import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.flyco.dialog.entity.DialogMenuItem;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.NormalListDialog;
import com.orhanobut.logger.Logger;
import com.ruihai.xingka.R;
import com.ruihai.xingka.XKApplication;
import com.ruihai.xingka.api.ApiModule;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.api.model.BannerInfoRepo;
import com.ruihai.xingka.api.model.DoubleClickModel;
import com.ruihai.xingka.api.model.ImageItem;
import com.ruihai.xingka.api.model.PhotoTopic;
import com.ruihai.xingka.api.model.PraiseItem;
import com.ruihai.xingka.api.model.User;
import com.ruihai.xingka.api.model.UserCarInfo;
import com.ruihai.xingka.api.model.UserRepo;
import com.ruihai.xingka.api.model.UserTag;
import com.ruihai.xingka.api.model.XKRepo;
import com.ruihai.xingka.event.OnItemClickListener;
import com.ruihai.xingka.ui.MainActivity;
import com.ruihai.xingka.ui.caption.CaptionDetailActivity;
import com.ruihai.xingka.ui.caption.OfficalCaptionInfoActivity;
import com.ruihai.xingka.ui.caption.ShareCaptionActivity;
import com.ruihai.xingka.ui.caption.adapter.CaptionListAdapter;
import com.ruihai.xingka.ui.caption.datasource.BannerDataProvider;
import com.ruihai.xingka.ui.caption.datasource.CaptionListDataSource;
import com.ruihai.xingka.ui.common.ListModify;
import com.ruihai.xingka.ui.common.PhotoPagerActivity;
import com.ruihai.xingka.ui.login.LoginActivity;
import com.ruihai.xingka.ui.mine.UserProfileActivity;
import com.ruihai.xingka.ui.mine.impl.ProfileLoadViewFactory;
import com.ruihai.xingka.utils.AppUtility;
import com.ruihai.xingka.utils.QiniuHelper;
import com.ruihai.xingka.utils.Security;
import com.ruihai.xingka.widget.ProgressHUD;
import com.ruihai.xingka.widget.goodview.GoodView;
import com.shizhefei.mvc.MVCHelper;
import com.shizhefei.mvc.MVCUltraHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CaptionListFragment extends Fragment implements OnItemClickListener {
    private final static String ARG_CAPTION_TYPE = "catpion_type";

    public final static String KEY_CAPTION_ITEM = "caption_item";
    public final static String KEY_COMMENT_ITEM = "comment_item";
    public final static String KEY_POP_COMMENT = "pop_comment";

    public final static int RESULT_EDIT_CAPTION = 100;

    @BindView(R.id.rotate_header_list_view_frame)
    PtrClassicFrameLayout mPtrFrameLayout;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    private LinearLayout bannerLayout;

    private String mTimeStamp;
    private int mType;
    private User mCurrentUser;
    private String mCurrentAccount; //本人行咖号
    private MVCHelper<List<PhotoTopic>> listViewHelper;
    private CaptionListAdapter mAdapter;
    private View mHeaderView;
    private ClipboardManager clipboard;//粘贴板
    //广告栏数据集合
    private List<BannerInfoRepo.BannerInfo> bannerInfoList = new ArrayList<>();
    private GoodView mGoodView;

    public static Fragment newInstance(int type) {
        CaptionListFragment fragment = new CaptionListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CAPTION_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        if (getArguments() != null) {
            mType = getArguments().getInt(ARG_CAPTION_TYPE, 0);
        }
        mCurrentUser = AccountInfo.getInstance().loadAccount();
        if (mCurrentUser != null) {
            mCurrentAccount = String.valueOf(mCurrentUser.getAccount());
        } else {
            mCurrentAccount = "0";
        }
        mTimeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());//获取当前时间戳
        mGoodView = new GoodView(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_caption_list, container, false);
        //mHeaderView = inflater.inflate(R.layout.head_banner, container, false);
        // banner = (SimpleImageBanner) mHeaderView.findViewById(R.id.banner);
        //bannerLayout = (LinearLayout) mHeaderView.findViewById(R.id.head_banner_layout);
        ButterKnife.bind(this, view);
        return view;
    };

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mRecyclerView.setLayoutM.llanager(new LinearLayoutManager(getActivity()));

        listViewHelper = new MVCUltraHelper<>(mPtrFrameLayout);
        // 设置数据源
        listViewHelper.setDataSource(new CaptionListDataSource(mCurrentAccount, mType, mTimelStamp, mType));
        // 设置适配器
        mAdapter = new CaptionListAdapter(getActivity(), getActivity(), mCurrentAccount);
        listViewHelper.setLoadViewFractory(new ProfileLoadViewFactory("每一次相遇都是奇迹"));
        mAdapter.setOnItemClickListener(CaptionListFragment.this);
        listViewHelper.setAdapter(mAdapter);
        //加载数据
        listViewHelper.refresh();
    }

    public void onEvent(DoubleClickModel data) {
        if (data.type == 1 && data.isDoubleClick) {
            //双击事件
            if (!listViewHelper.isLoading()) {
                mRecyclerView.scrollToPosition(0);//点击快速回到顶部
                listViewHelper.refresh();
            }
            //Toast.makeText(getContext(), "我被双击了", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUserAccount() {
        String equipment = android.os.Build.MODEL;
        String password = AccountInfo.getInstance().loadPassWord();
        String version = Build.VERSION.RELEASE;
        String appVersion = AppUtility.getAppVersionName();
        String sAccount = Security.aesEncrypt(mCurrentAccount);
        Log.e("密码", password);
        String sOS = Security.aesEncrypt("1");
        Log.e("系统", sOS);
        String sEquipment = Security.aesEncrypt(equipment);
        Log.e("设备", equipment);
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
                    ProgressHUD.showErrorMessage(getActivity(), msg, new ProgressHUD.SimpleHUDCallback() {
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


    @Override
    public void onDestroy() {
        super.onDestroy();
        // 释放资源
        if (listViewHelper != null) {
            listViewHelper.destory();
        }
        EventBus.getDefault().unregister(this);
    }

    private void logout() {
        AccountInfo.getInstance().clearAccount();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        XKApplication.getInstance().exit();
    }

    @Override
    public void onItemClick(View view, int position) {
        PhotoTopic item;
        Intent intent;
        if (mAdapter.hasHeader()) {
            if (position > 0) {
                item = mAdapter.getData().get(position - 1);
                if (item.isOffical() && !TextUtils.isEmpty(item.getWebUrl())) {
                    OfficalCaptionInfoActivity.launch(getActivity(), item.getWebUrl(), item.getpGuid());
                } else {
                    intent = new Intent(getActivity(), CaptionDetailActivity.class);
                    intent.putExtra("position", position - 1);
                    intent.putExtra(KEY_CAPTION_ITEM, item);
                    startActivityForResult(intent, RESULT_EDIT_CAPTION);
                }
            }
        } else {
            item = mAdapter.getData().get(position);
            if (item.isOffical() && !TextUtils.isEmpty(item.getWebUrl())) {
                OfficalCaptionInfoActivity.launch(getActivity(), item.getWebUrl(), item.getpGuid());
            } else {
                intent = new Intent(getActivity(), CaptionDetailActivity.class);
                intent.putExtra("position", position);
                intent.putExtra(KEY_CAPTION_ITEM, item);
                startActivityForResult(intent, RESULT_EDIT_CAPTION);
            }
        }
    }

    @Override
    public void onItemLongClick(View view, int position) {
//        PhotoTopic item;
//        if (mAdapter.hasHeader()) {
//            item = mAdapter.getData().get(position - 1);
//        } else {
//            item = mAdapter.getData().get(position);
//        }
//        switch (view.getId()) {
//            case R.id.tv_content://图说内容
//                normalListDialogNoTitle(item.getContent());
//                break;
//        }
    }

    @Override
    public void onItemChildClick(View childView, int position) {
        if (AppUtility.isFastClick()) {//判断两次点击时间是否太接近
            return;
        }
        PhotoTopic item;
        if (mAdapter.hasHeader()) {
            item = mAdapter.getData().get(position - 1);
        } else {
            item = mAdapter.getData().get(position);
        }
        switch (childView.getId()) {
            case R.id.ll_praise: { // 点赞
                addReadingRecord(item.getpGuid(), String.valueOf(item.getAccount()));
                praiseOrCancel(childView, item);
                break;
            }
            case R.id.ll_collect: { // 收藏
                addReadingRecord(item.getpGuid(), String.valueOf(item.getAccount()));
                collectOrCancel(childView, item);
                break;
            }
            case R.id.ll_comment: { // 评论
                addReadingRecord(item.getpGuid(), String.valueOf(item.getAccount()));
                comment(item);
                break;
            }
            case R.id.ll_share: { // 分享
                addReadingRecord(item.getpGuid(), String.valueOf(item.getAccount()));
                ShareCaptionActivity.launch(getActivity(), item, 1);
                break;
            }
            case R.id.btn_add_follow: { // 加关注
                executeAddFollow(item);
                break;
            }
            case R.id.iv_image: { // 浏览图片
                addReadingRecord(item.getpGuid(), String.valueOf(item.getAccount()));
                if (item.isOffical() && !TextUtils.isEmpty(item.getWebUrl())) {//官方账号
                    OfficalCaptionInfoActivity.launch(getActivity(), item.getWebUrl(), item.getpGuid());
                } else
                    browsePhotos(item);
                break;
            }
            case R.id.sdv_avatar: { // 点击头像
                viewUserInfo(item);
                break;
            }

        }
    }

    /**
     * 执行删除图说操作
     *
     * @param photoTopic
     */
    private void deletePhotoTopic(PhotoTopic photoTopic, final int position) {
        ProgressHUD.showLoadingMessage(getActivity(), "正在删除...", false);
        String securityAccount = Security.aesEncrypt(mCurrentAccount); //行账号
        String securityPGuid = Security.aesEncrypt(photoTopic.getpGuid());
        String sAccount = Security.aesEncrypt(String.valueOf(photoTopic.getAccount())); //图说作者

        ApiModule.apiService().deletePhotoTopic(securityAccount, securityPGuid, sAccount).enqueue(new Callback<XKRepo>() {
            @Override
            public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                XKRepo xkRepo = response.body();
                ProgressHUD.dismiss();
                String message = xkRepo.getMsg();
                if (xkRepo.isSuccess()) {
                    //updateCountEvent();
                    mAdapter.getData().remove(position);
                    mAdapter.notifyDataSetChanged();
                    ProgressHUD.showSuccessMessage(getActivity(), "删除成功");
                } else {
                    ProgressHUD.showInfoMessage(getActivity(), message);
                }
            }

            @Override
            public void onFailure(Call<XKRepo> call, Throwable t) {
                ProgressHUD.showErrorMessage(getActivity(), "音信全无？嗯，可能需要检查一下网络哟");
            }
        });
    }

    /**
     * @param item
     * @功能描述 : 进行取消或者点赞操作
     */
    private void praiseOrCancel(View view, final PhotoTopic item) {
        if (!AccountInfo.getInstance().isLogin()) {
            logout();
            return;
        }

        final String isPraiseStr = item.isPraise() ? "0" : "1";
        String account = Security.aesEncrypt(String.valueOf(mCurrentAccount));
        String pGuid = Security.aesEncrypt(item.getpGuid());
        String toAccount = Security.aesEncrypt(String.valueOf(item.getAccount()));
        final String isPraise = Security.aesEncrypt(isPraiseStr); // 1-点赞，0-取消赞
        //if (AppUtility.isNetWorkAvilable()) {
        if (isPraiseStr.equals("0")) {
            // 设置数据
            item.setIsPraise(false);
            List<PraiseItem> praiseItems = item.getPraiseList();
            for (Iterator<PraiseItem> iterator = praiseItems.iterator(); iterator.hasNext(); ) {
                PraiseItem praise = iterator.next();
                if (praise.getAccount() == mCurrentUser.getAccount()) {
                    iterator.remove();
                    item.setPraiseNum(item.getPraiseNum() - 1);
                }
            }
            mAdapter.notifyDataSetChanged();
        } else {
            // 设置数据
            mGoodView.setTextInfo("谢谢喜欢", Color.parseColor("#ff941A"), 10);
            mGoodView.show(view);
            // ProgressHUD.showPraiseOrCollectSuccessMessage(getContext(), "谢谢你的喜欢");
            item.setIsPraise(true);
            mCurrentUser = AccountInfo.getInstance().loadAccount();
            List<PraiseItem> praiseItems = item.getPraiseList();
            PraiseItem praise = new PraiseItem();
            praise.setAccount(mCurrentUser.getAccount());
            praise.setAvatar(mCurrentUser.getAvatar());
            praise.setNick(mCurrentUser.getNickname());
            praiseItems.add(0, praise);
            item.setPraiseNum(item.getPraiseNum() + 1);
            mAdapter.notifyDataSetChanged();
        }
        //}
        Call<XKRepo> call = ApiModule.apiService_1().photoTopicPraiseAdd(account, pGuid, isPraise, toAccount);
        call.enqueue(new Callback<XKRepo>() {
            @Override
            public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                XKRepo xkRepo = response.body();
                if (xkRepo.isSuccess()) {
//                    if (isPraiseStr.equals("0")) {
//                        // 设置数据
//                        item.setIsPraise(false);
//                        List<PraiseItem> praiseItems = item.getPraiseList();
//                        for (Iterator<PraiseItem> iterator = praiseItems.iterator(); iterator.hasNext(); ) {
//                            PraiseItem praise = iterator.next();
//                            if (praise.getAccount() == mCurrentUser.getAccount()) {
//                                iterator.remove();
//                                item.setPraiseNum(item.getPraiseNum() - 1);
//                            }
//                        }
//                        mAdapter.notifyDataSetChanged();
//                    } else {
//                        // 设置数据
//                        item.setIsPraise(true);
//                        List<PraiseItem> praiseItems = item.getPraiseList();
//                        PraiseItem praise = new PraiseItem();
//                        praise.setAccount(mCurrentUser.getAccount());
//                        praise.setAvatar(mCurrentUser.getAvatar());
//                        praise.setNick(mCurrentUser.getNickname());
//                        praiseItems.add(0, praise);
//                        item.setPraiseNum(item.getPraiseNum() + 1);
//                        mAdapter.notifyDataSetChanged();
//                    }
                } else {
                    //ProgressHUD.showInfoMessage(getActivity(), xkRepo.getMsg());
                }
            }

            @Override
            public void onFailure(Call<XKRepo> call, Throwable t) {
                //ProgressHUD.showInfoMessage(getActivity(), getString(R.string.common_network_error));
            }

        });
    }

    /**
     * 进行收藏或取消收藏
     *
     * @param item
     */
    private void collectOrCancel(View view, final PhotoTopic item) {
        if (!AccountInfo.getInstance().isLogin()) {
            logout();
            return;
        }
        final String isCollectStr = item.isCollect() ? "0" : "1";
        String account = Security.aesEncrypt(String.valueOf(mCurrentAccount));
        String pGuid = Security.aesEncrypt(item.getpGuid());
        String toAccount = Security.aesEncrypt(String.valueOf(item.getAccount()));
        String isCollect = Security.aesEncrypt(isCollectStr); // 1-收藏 0-取消收藏
        if (isCollectStr.equals("0")) {
            // 设置数据
            item.setIsCollect(false);
            mAdapter.notifyDataSetChanged();
        } else {
            // 设置数据
            // ProgressHUD.showPraiseOrCollectSuccessMessage(getContext(), "谢谢你的收藏");
            mGoodView.setTextInfo("谢谢收藏", Color.parseColor("#ff941A"), 10);
            mGoodView.show(view);

            item.setIsCollect(true);
            mAdapter.notifyDataSetChanged();
        }
        Call<XKRepo> call = ApiModule.apiService_1().photoTopicCollectionAdd(account, pGuid, isCollect, toAccount);
        call.enqueue(new Callback<XKRepo>() {
            @Override
            public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                XKRepo xkRepo = response.body();
                if (xkRepo.isSuccess()) {
//                    if (isCollectStr.equals("0")) {
//                        // 设置数据
//                        item.setIsCollect(false);
//                        mAdapter.notifyDataSetChanged();
//                    } else {
//                        // 设置数据
//                        item.setIsCollect(true);
//                        mAdapter.notifyDataSetChanged();
//                    }
                } else {
                    ProgressHUD.showInfoMessage(getActivity(), xkRepo.getMsg());
                }
            }

            @Override
            public void onFailure(Call<XKRepo> call, Throwable t) {
                ProgressHUD.showInfoMessage(getActivity(), getString(R.string.common_network_error));
                if (isCollectStr.equals("0")) {
                    // 设置数据
                    item.setIsCollect(true);
                    mAdapter.notifyDataSetChanged();
                } else {
                    // 设置数据
                    item.setIsCollect(false);
                    mAdapter.notifyDataSetChanged();
                }
            }

        });
    }

    // --------------------------- 加关注 ---------------------------
    private void executeAddFollow(final PhotoTopic item) {
        if (!AccountInfo.getInstance().isLogin()) {
            logout();
            return;
        }
        // ProgressHUD.showPraiseOrCollectSuccessMessage(getContext(), "谢谢你的关注");
        item.setIsFriend(true);
        mAdapter.notifyDataSetChanged();
        String account = Security.aesEncrypt(String.valueOf(mCurrentAccount));
        String friendAccount = Security.aesEncrypt(String.valueOf(item.getAccount()));
        Call<XKRepo> call = ApiModule.apiService_1().addFriend(account, friendAccount);
        call.enqueue(new Callback<XKRepo>() {
            @Override
            public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                XKRepo xkRepo = response.body();
                String message = xkRepo.getMsg();
                if (xkRepo.isSuccess()) {

                } else {
                    ProgressHUD.showInfoMessage(getActivity(), message);
                }
            }

            @Override
            public void onFailure(Call<XKRepo> call, Throwable t) {
                ProgressHUD.showInfoMessage(getActivity(), getString(R.string.common_network_error));
            }

        });
    }

    // 查看用户信息
    private void viewUserInfo(PhotoTopic item) {
        String account = String.valueOf(item.getAccount());
        if (mCurrentAccount.equals(account)) {
            if (MainActivity.currentTabIndex != 3) {
                MainActivity.launch(getActivity(), 1);
            }
        } else if (item.isOffical()) {
            UserProfileActivity.launch(getActivity(), account, 1, 1);
        } else {
            UserProfileActivity.launch(getActivity(), account, 1, 0);
        }

    }

    // --------------------------- 浏览图片 ---------------------------
    private void browsePhotos(PhotoTopic item) {
        ArrayList<String> imageUrls = new ArrayList<>();
        for (ImageItem image : item.getImageList()) {
            imageUrls.add(QiniuHelper.getOriginalWithKey(image.imgSrc));
        }
        Intent intent = new Intent(getActivity(), PhotoPagerActivity.class);
        intent.putExtra(PhotoPagerActivity.EXTRA_CURRENT_ITEM, 0);
        intent.putExtra(PhotoPagerActivity.EXTRA_PHOTOS, imageUrls);
        intent.putExtra(PhotoPagerActivity.EXTRA_TYPE, 1);
        intent.putExtra(PhotoPagerActivity.EXTRA_USERNICK, item.getNick());
        Log.i("TAG", "------CaptionFragment------>" + item.getNick());
        startActivity(intent);
    }

    /**
     * 执行评论操作
     *
     * @param item
     */
    private void comment(PhotoTopic item) {
        Intent intent = new Intent(getActivity(), CaptionDetailActivity.class);
        intent.putExtra(KEY_CAPTION_ITEM, item);
        intent.putExtra(KEY_POP_COMMENT, 1);
        startActivityForResult(intent, RESULT_EDIT_CAPTION);
    }

    public void onEvent(PhotoTopic photoTopic) {
        List<PhotoTopic> photoTopics = mAdapter.getData();
        for (int i = 0; i < photoTopics.size(); i++) {
            PhotoTopic item = photoTopics.get(i);
            if (item.getpGuid().equals(photoTopic.getpGuid())) {
                photoTopics.remove(i);
                photoTopics.add(i, photoTopic);
                mAdapter.notifyDataSetChanged();
                break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_EDIT_CAPTION) {
            if (resultCode == Activity.RESULT_OK || data != null) {
                int type = data.getIntExtra(ListModify.TYPE, 0);
                if (type == ListModify.Edit) {
                    PhotoTopic photoTopic = data.getParcelableExtra(ListModify.DATA);
                    List<PhotoTopic> photoTopics = mAdapter.getData();
                    for (int i = 0; i < photoTopics.size(); i++) {
                        PhotoTopic item = photoTopics.get(i);
                        if (item.getpGuid().equals(photoTopic.getpGuid())) {
                            photoTopics.remove(i);
                            photoTopics.add(i, photoTopic);
                            mAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }
            }
            if (resultCode == 200 || data != null) {
                int position = data.getIntExtra("position", -100);
                if (position != -100) {
                    mAdapter.getData().remove(position);
                    mAdapter.notifyDataSetChanged();
                }

            }

        }
    }

    //获取广告位信息
    private void loadBannerData() {
        String sAccount = Security.aesEncrypt(mCurrentAccount);
        String sType = Security.aesEncrypt("0");//类型 1:活动 2:广告
        String sPlatform = Security.aesEncrypt("0");//平台类型 1:手机 2:PC 9:所有
        Call<BannerInfoRepo> call = ApiModule.apiService_1().bannerList(sAccount, sType, sPlatform);
        call.enqueue(new Callback<BannerInfoRepo>() {
            @Override
            public void onResponse(Call<BannerInfoRepo> call, Response<BannerInfoRepo> response) {
                BannerInfoRepo bannerInfoRepo = response.body();
                if (bannerInfoRepo.isSuccess()) {
                    List<BannerInfoRepo.BannerInfo> bannerInfos = bannerInfoRepo.getBannerInfoList();
                    if (bannerInfos != null && bannerInfos.size() > 0) {
                        //mAdapter.setHeaderVisibility(true);
                        //bannerLayout.setVisibility(View.VISIBLE);
                        bannerInfoList.addAll(bannerInfos);
                        initHeaderBanner();
                        mAdapter.addHeader(mHeaderView);
                    }
                }
                listViewHelper.setAdapter(mAdapter);
                //加载数据
                listViewHelper.refresh();
            }

            @Override
            public void onFailure(Call<BannerInfoRepo> call, Throwable t) {
                listViewHelper.setAdapter(mAdapter);
                //加载数据
                listViewHelper.refresh();
            }
        });
    }

    //初始化头部设置
    private void initHeaderBanner() {
        BannerDataProvider bannerDataProvider = new BannerDataProvider(bannerInfoList);
//        SimpleImageBanner sib = ViewFindUtils.find(mHeaderView, R.id.banner);
//        sib
        /** methods in BaseIndicatorBanner */
//              .setIndicatorStyle(BaseIndicaorBanner.STYLE_CORNER_RECTANGLE)//set indicator style
//              .setIndicatorWidth(6)                               //set indicator width
//              .setIndicatorHeight(6)                              //set indicator height
//              .setIndicatorGap(8)                                 //set gap btween two indicators
//              .setIndicatorCornerRadius(3)                        //set indicator corner raduis
//              .setSelectAnimClass(ZoomInEnter.class)              //set indicator select anim
        /** methods in BaseBanner */
//              .setBarColor(Color.parseColor("#88000000"))         //set bootom bar color
//              .barPadding(5, 2, 5, 2)                             //set bottom bar padding
//              .setBarShowWhenLast(true)                           //set bottom bar show or not when the position is the last
//              .setTextColor(Color.parseColor("#ffffff"))          //set title text color
//              .setTextSize(12.5f)                                 //set title text size
//              .setTitleShow(true)                                 //set title show or not
//              .setIndicatorShow(true)                             //set indicator show or not
//              .setDelay(2)                                        //setDelay before start scroll
//              .setPeriod(10)                                      //scroll setPeriod
//                .setSource(bannerDataProvider.getList())            //data source list
//                .setTransformerClass(ZoomOutSlideTransformer.class) //set page transformer
//                .startScroll();                                  //start scroll,the last method to call

//        sib.setOnItemClickL(new SimpleImageBanner.OnItemClickL() {
//            @Override
//            public void onItemClick(int position) {
//                if (bannerInfoList.get(position).getBclass() == 1) {//外部链接
//                    BannerInfoActivity.launch(getActivity(), bannerInfoList.get(position));
//                } else if (bannerInfoList.get(position).getBclass() == 2) {//图说ID
//                    CaptionDetailActivity.launch(getActivity(), bannerInfoList.get(position).getContent1(), bannerInfoList.get(position).getContent2());
//                }
//            }
//        });
    }

    /**
     * @功能描述 : 点击阅读记录
     */
    private void addReadingRecord(String pGuid, String author) {
        String sPGuid = Security.aesEncrypt(pGuid);
        String sAuthor = Security.aesEncrypt(author);
        Call<XKRepo> call = ApiModule.apiService_1().photoTopicReadAdd(sPGuid, sAuthor);
        call.enqueue(new Callback<XKRepo>() {
            @Override
            public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                XKRepo xkRepo = response.body();
                if (xkRepo.isSuccess()) {
                    Logger.d("阅读记录添加成功!");
                } else {
                    Logger.d("阅读记录添加失败!");
                }
            }

            @Override
            public void onFailure(Call<XKRepo> call, Throwable t) {
                Logger.d(getString(R.string.common_network_error));
            }
        });
    }

    //长按显示复制的弹框
    private void normalListDialogNoTitle(final String content) {
        final ArrayList<DialogMenuItem> testItems = new ArrayList<>();
//        testItems.add(new DialogMenuItem("复制", R.mipmap.ic_winstyle_copy));
//        testItems.add(new DialogMenuItem("删除", R.mipmap.ic_winstyle_delete));
        testItems.add(new DialogMenuItem("复制", R.mipmap.ic_winstyle_copy));
        final NormalListDialog dialog = new NormalListDialog(getActivity(), testItems);
        dialog.title("请选择")//
                .isTitleShow(false)//
                .itemPressColor(Color.parseColor("#85D3EF"))//
                .itemTextColor(Color.parseColor("#303030"))//
                .itemTextSize(15)//
                .cornerRadius(2)//
                .widthScale(0.75f)//
                .show();
        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0://复制
                        copyFromComment(content);
                        break;
                }
                dialog.dismiss();
            }
        });
    }

    //复制内容到粘贴板
    private void copyFromComment(String content) {
        // Gets a handle to the clipboard service.
        if (null == clipboard) {
            //获取粘贴板服务
            clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        }
        // Creates a new text clip to put on the clipboard
        ClipData clip = ClipData.newPlainText("simple text",
                content);
        // Set the clipboard's primary clip.
        clipboard.setPrimaryClip(clip);
    }

}

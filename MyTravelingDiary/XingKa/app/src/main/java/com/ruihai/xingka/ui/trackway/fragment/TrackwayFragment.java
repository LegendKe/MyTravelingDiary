package com.ruihai.xingka.ui.trackway.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.orhanobut.logger.Logger;
import com.ruihai.xingka.R;
import com.ruihai.xingka.XKApplication;
import com.ruihai.xingka.api.ApiModule;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.api.model.DoubleClickModel;
import com.ruihai.xingka.api.model.ImageItem;
import com.ruihai.xingka.api.model.PraiseItem;
import com.ruihai.xingka.api.model.TrackwayInfo;
import com.ruihai.xingka.api.model.User;
import com.ruihai.xingka.api.model.XKRepo;
import com.ruihai.xingka.event.OnItemClickListener;
import com.ruihai.xingka.ui.MainActivity;
import com.ruihai.xingka.ui.caption.OfficalCaptionInfoActivity;
import com.ruihai.xingka.ui.common.ListModify;
import com.ruihai.xingka.ui.common.PhotoPagerActivity;
import com.ruihai.xingka.ui.common.enter.EmojiUtils;
import com.ruihai.xingka.ui.login.LoginActivity;
import com.ruihai.xingka.ui.mine.UserProfileActivity;
import com.ruihai.xingka.ui.mine.impl.ProfileLoadViewFactory;
import com.ruihai.xingka.ui.trackway.ShareTrackwayActivity;
import com.ruihai.xingka.ui.trackway.TrackwayDetailActivity;
import com.ruihai.xingka.ui.trackway.adapter.TrackwayAdapter;
import com.ruihai.xingka.ui.trackway.datasource.TrackwayMainDataSource;
import com.ruihai.xingka.utils.AppUtility;
import com.ruihai.xingka.utils.QiniuHelper;
import com.ruihai.xingka.utils.Security;
import com.ruihai.xingka.widget.MoreContentPopWindow;
import com.ruihai.xingka.widget.ProgressHUD;
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


/**
 * 旅拼主页
 * Created by lqfang on 16/5/3.
 */
public class TrackwayFragment extends Fragment implements OnItemClickListener {

    public final static String KEY_TRACKWAY_ITEM = "trackway_item";
    public final static String KEY_COMMENT_ITEM = "comment_item";
    public final static String KEY_POP_COMMENT = "pop_comment";

    public final static int RESULT_EDIT_TRACKWAY = 100;

    @BindView(R.id.rotate_header_list_view_frame)
    PtrClassicFrameLayout mPtrFrameLayout;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.root_layout)
    RelativeLayout rootLayout;


    private String mTimeStamp;
    private User mCurrentUser;
    private String mCurrentAccount;
    private MVCHelper<List<TrackwayInfo>> listViewHelper;
    TrackwayAdapter mTrackwayAdapter;

    private float startY;
    private float startX;    // 记录viewPager是否拖拽的标记
    private boolean mIsVpDragger;

    private AbsListView.OnScrollListener mOnScrollListener;
    public MoreContentPopWindow moreContentPW;

    public static TrackwayFragment newInstance(String userAccount) {
        Bundle args = new Bundle();
        TrackwayFragment fragment = new TrackwayFragment();
        args.putString("userAccount", userAccount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        mCurrentUser = AccountInfo.getInstance().loadAccount();
        if (mCurrentUser != null) {
            mCurrentAccount = String.valueOf(mCurrentUser.getAccount());
        } else {
            mCurrentAccount = "0";
        }
        mTimeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());//获取当前时间戳

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trackway, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        listViewHelper = new MVCUltraHelper<>(mPtrFrameLayout);
        // 设置适配器
        mTrackwayAdapter = new TrackwayAdapter(getActivity(), getActivity(), mCurrentAccount);
        listViewHelper.setLoadViewFractory(new ProfileLoadViewFactory("每一次相遇都是奇迹"));
        // 设置数据源
        listViewHelper.setDataSource(new TrackwayMainDataSource(mCurrentAccount, mTimeStamp));
        mTrackwayAdapter.setOnItemClickListener(TrackwayFragment.this);
        listViewHelper.setAdapter(mTrackwayAdapter);
        //加载数据
        listViewHelper.refresh();

        initView();

    }

    private void initView() {
        moreContentPW = new MoreContentPopWindow(getActivity());
        moreContentPW.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1f);
            }
        });
    }

    public void onEvent(DoubleClickModel data) {
        if (data.type == 2 && data.isDoubleClick) {
            //双击事件
            if (!listViewHelper.isLoading()) {
                mRecyclerView.scrollToPosition(0);//点击快速回到顶部
                listViewHelper.refresh();
            }
        }
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
        TrackwayInfo item;
        Intent intent;
        item = mTrackwayAdapter.getData().get(position);
        if (item.isOffical() && !TextUtils.isEmpty(item.getWebUrl())) {
            OfficalCaptionInfoActivity.launch(getActivity(), item.getWebUrl(), item.gettGuid());
        } else {
            intent = new Intent(getActivity(), TrackwayDetailActivity.class);
            intent.putExtra("position", position);
            intent.putExtra(KEY_TRACKWAY_ITEM, item);
            startActivityForResult(intent, RESULT_EDIT_TRACKWAY);
        }
    }

    @Override
    public void onItemLongClick(View view, int position) {

    }

    @Override
    public void onItemChildClick(View childView, int position) {
        if (AppUtility.isFastClick()) {//判断两次点击时间是否太接近
            return;
        }
        TrackwayInfo item;
        item = mTrackwayAdapter.getData().get(position);
        switch (childView.getId()) {
            case R.id.ll_praise: { // 点赞
                addReadingRecord(item.gettGuid(), String.valueOf(item.getAccount()));
                praiseOrCancel(item);
                break;
            }
            case R.id.ll_collect: { // 收藏
                addReadingRecord(item.gettGuid(), String.valueOf(item.getAccount()));
                collectOrCancel(item);
                break;
            }
            case R.id.ll_comment: { // 评论
                addReadingRecord(item.gettGuid(), String.valueOf(item.getAccount()));
                comment(item);
                break;
            }
            case R.id.ll_share: { // 分享
                addReadingRecord(item.gettGuid(), String.valueOf(item.getAccount()));
                ShareTrackwayActivity.launch(getActivity(), item, 1);
                break;
            }
            case R.id.btn_add_follow: { // 加关注
                executeAddFollow(item);
                break;
            }
            case R.id.iv_image: { // 浏览图片
                addReadingRecord(item.gettGuid(), String.valueOf(item.getAccount()));
                if (item.isOffical() && !TextUtils.isEmpty(item.getWebUrl())) {//官方账号
                    OfficalCaptionInfoActivity.launch(getActivity(), item.getWebUrl(), item.gettGuid());
                } else
                    browsePhotos(item);
                break;
            }
            case R.id.sdv_avatar: { // 点击头像
                viewUserInfo(item);
                break;
            }
            case R.id.btn_content_more: {//更多内容
                addReadingRecord(item.gettGuid(), String.valueOf(item.getAccount()));
                moreContentPW.setTitle(EmojiUtils.fromStringToEmoji1(item.getTitle(), getContext()));
                moreContentPW.setContent(EmojiUtils.fromStringToEmoji1(item.getContent(), getContext()));
                backgroundAlpha(0.6f); // 设置背景颜色变暗
                moreContentPW.showAtLocation(rootLayout, Gravity.BOTTOM, 0, 0);
                break;
            }
        }
    }

    /**
     * @param item
     * @功能描述 : 进行取消或者点赞操作
     */
    private void praiseOrCancel(final TrackwayInfo item) {
        if (!AccountInfo.getInstance().isLogin()) {
            logout();
            return;
        }

        final String isPraiseStr = item.isPraise() ? "0" : "1";
        String account = Security.aesEncrypt(String.valueOf(mCurrentAccount));
        String pGuid = Security.aesEncrypt(item.gettGuid());
        String toAccount = Security.aesEncrypt(String.valueOf(item.getAccount()));
        final String isPraise = Security.aesEncrypt(isPraiseStr); // 1-点赞，0-取消赞
        //if (AppUtility.isNetWorkAvilable()) {
        if (isPraiseStr.equals("0")) {
            // 设置数据
            item.setPraise(false);
            List<PraiseItem> praiseItems = item.getPraiseList();
            for (Iterator<PraiseItem> iterator = praiseItems.iterator(); iterator.hasNext(); ) {
                PraiseItem praise = iterator.next();
                if (praise.getAccount() == mCurrentUser.getAccount()) {
                    iterator.remove();
                    item.setPraiseNum(item.getPraiseNum() - 1);
                }
            }
            mTrackwayAdapter.notifyDataSetChanged();
        } else {
            // 设置数据
            // ProgressHUD.showPraiseOrCollectSuccessMessage(getContext(), "谢谢你的喜欢");
            item.setPraise(true);
            mCurrentUser = AccountInfo.getInstance().loadAccount();
            List<PraiseItem> praiseItems = item.getPraiseList();
            PraiseItem praise = new PraiseItem();
            praise.setAccount(mCurrentUser.getAccount());
            praise.setAvatar(mCurrentUser.getAvatar());
            praise.setNick(mCurrentUser.getNickname());
            praiseItems.add(0, praise);
            item.setPraiseNum(item.getPraiseNum() + 1);
            mTrackwayAdapter.notifyDataSetChanged();
        }
        //}
        Call<XKRepo> call = ApiModule.apiService_1().travelTogetherPraiseAdd(account, pGuid, isPraise, toAccount);
        call.enqueue(new Callback<XKRepo>() {
            @Override
            public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                XKRepo xkRepo = response.body();
                if (xkRepo.isSuccess()) {

                } else {
                    //ProgressHUD.showInfoMessage(getActivity(), xkRepo.getMsg());
                }
            }

            @Override
            public void onFailure(Call<XKRepo> call, Throwable t) {
                ProgressHUD.showInfoMessage(getActivity(), getString(R.string.common_network_error));
            }

        });
    }


    /**
     * 进行收藏或取消收藏
     *
     * @param item
     */
    private void collectOrCancel(final TrackwayInfo item) {
        if (!AccountInfo.getInstance().isLogin()) {
            logout();
            return;
        }
        final String isCollectStr = item.isCollect() ? "0" : "1";
        String account = Security.aesEncrypt(String.valueOf(mCurrentAccount));
        String pGuid = Security.aesEncrypt(item.gettGuid());
        String toAccount = Security.aesEncrypt(String.valueOf(item.getAccount()));
        String isCollect = Security.aesEncrypt(isCollectStr); // 1-收藏 0-取消收藏
        if (isCollectStr.equals("0")) {
            // 设置数据
            item.setCollect(false);
            mTrackwayAdapter.notifyDataSetChanged();
        } else {
            // 设置数据
            // ProgressHUD.showPraiseOrCollectSuccessMessage(getContext(), "谢谢你的收藏");
            item.setCollect(true);
            mTrackwayAdapter.notifyDataSetChanged();
        }
        Call<XKRepo> call = ApiModule.apiService_1().travelTogetherCollectionAdd(account, pGuid, isCollect, toAccount);
        call.enqueue(new Callback<XKRepo>() {
            @Override
            public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                XKRepo xkRepo = response.body();
                if (xkRepo.isSuccess()) {

                } else {
                    ProgressHUD.showInfoMessage(getActivity(), xkRepo.getMsg());
                }
            }

            @Override
            public void onFailure(Call<XKRepo> call, Throwable t) {
                ProgressHUD.showInfoMessage(getActivity(), getString(R.string.common_network_error));
                if (isCollectStr.equals("0")) {
                    // 设置数据
                    item.setCollect(true);
                    mTrackwayAdapter.notifyDataSetChanged();
                } else {
                    // 设置数据
                    item.setCollect(false);
                    mTrackwayAdapter.notifyDataSetChanged();
                }
            }

        });
    }

    // 查看用户信息
    private void viewUserInfo(TrackwayInfo item) {
        String account = String.valueOf(item.getAccount());
        if (mCurrentAccount.equals(account)) {
            if (MainActivity.currentTabIndex != 3) {
                MainActivity.launch(getActivity(), 1);
            }
        } else if (item.isOffical()) {
            UserProfileActivity.launch(getActivity(), account, 2, 1);
        } else {
            UserProfileActivity.launch(getActivity(), account, 2, 0);
        }

    }


    // --------------------------- 浏览图片 ---------------------------
    private void browsePhotos(TrackwayInfo item) {
        ArrayList<String> imageUrls = new ArrayList<>();
        for (ImageItem image : item.getImageList()) {
            imageUrls.add(QiniuHelper.getOriginalWithKey(image.imgSrc));
        }
        Intent intent = new Intent(getActivity(), PhotoPagerActivity.class);
        intent.putExtra(PhotoPagerActivity.EXTRA_CURRENT_ITEM, 0);
        intent.putExtra(PhotoPagerActivity.EXTRA_PHOTOS, imageUrls);
        intent.putExtra(PhotoPagerActivity.EXTRA_TYPE, 1);
        intent.putExtra(PhotoPagerActivity.EXTRA_USERNICK, item.getNick());
        startActivity(intent);
    }


    // --------------------------- 加关注 ---------------------------
    private void executeAddFollow(final TrackwayInfo item) {
        if (!AccountInfo.getInstance().isLogin()) {
            logout();
            return;
        }
        // ProgressHUD.showPraiseOrCollectSuccessMessage(getContext(), "谢谢你的关注");
        item.setFriend(true);
        mTrackwayAdapter.notifyDataSetChanged();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_EDIT_TRACKWAY) {
            if (resultCode == Activity.RESULT_OK || data != null) {
                int type = data.getIntExtra(ListModify.TYPE, 0);
                if (type == ListModify.Edit) {
                    TrackwayInfo trackwayInfo = data.getParcelableExtra(ListModify.DATA);
                    List<TrackwayInfo> trackwayInfoList = mTrackwayAdapter.getData();
                    for (int i = 0; i < trackwayInfoList.size(); i++) {
                        TrackwayInfo item = trackwayInfoList.get(i);
                        if (item.gettGuid().equals(trackwayInfo.gettGuid())) {
                            trackwayInfoList.remove(i);
                            trackwayInfoList.add(i, trackwayInfo);
                            mTrackwayAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }
            }
            if (resultCode == 200 || data != null) {
                int position = data.getIntExtra("position", -100);
                if (position != -100) {
                    mTrackwayAdapter.getData().remove(position);
                    mTrackwayAdapter.notifyDataSetChanged();
                }

            }

        }
    }


    /**
     * 执行评论操作
     *
     * @param item
     */
    private void comment(TrackwayInfo item) {
        Intent intent = new Intent(getActivity(), TrackwayDetailActivity.class);
        intent.putExtra(KEY_TRACKWAY_ITEM, item);
        intent.putExtra(KEY_POP_COMMENT, 1);
        startActivityForResult(intent, RESULT_EDIT_TRACKWAY);
    }

    public void onEvent(TrackwayInfo trackwayInfo) {
        Log.i("TAG", "----点过赞了------->");
        List<TrackwayInfo> trackwayInfos = mTrackwayAdapter.getData();
        for (int i = 0; i < trackwayInfos.size(); i++) {
            TrackwayInfo item = trackwayInfos.get(i);
            if (item.gettGuid().equals(trackwayInfo.gettGuid())) {
                trackwayInfos.remove(i);
                trackwayInfos.add(i, trackwayInfo);
                mTrackwayAdapter.notifyDataSetChanged();
                break;
            }
        }
    }


    /**
     * @功能描述 : 点击阅读记录
     */
    private void addReadingRecord(String tGuid, String author) {
        String sTGuid = Security.aesEncrypt(tGuid);
        String sAuthor = Security.aesEncrypt(author);
        Call<XKRepo> call = ApiModule.apiService_1().travelTogetherReadAdd(sTGuid, sAuthor);
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
                //  Logger.d(getString(R.string.common_network_error));
            }
        });
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

//    @Override
//    public void onScrollStateChanged(AbsListView absListView, int i) {
//
//    }
//
//    @Override
//    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//        View firstView = absListView.getChildAt(firstVisibleItem);
//
//        // 当firstVisibleItem是第0位。如果firstView==null说明列表为空，需要刷新;或者top==0说明已经到达列表顶部, 也需要刷新
////        if (firstVisibleItem == 0 && (firstView == null || firstView.getTop() == 0)) {
//        if (firstVisibleItem == 0){
//            mPtrFrameLayout.setEnabled(true);
//
//            mPtrFrameLayout.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View view, MotionEvent event) {
//                    mPtrFrameLayout.getParent().requestDisallowInterceptTouchEvent(true);
////                    int action = event.getAction();
////                    switch (action) {
////                        case MotionEvent.ACTION_DOWN:
////                            // 记录手指按下的位置
////                            startY = event.getY();
////                            startX = event.getX();
////                            // 初始化标记
////                            mIsVpDragger = false;
////                            break;
////                        case MotionEvent.ACTION_MOVE:
////                            // 如果viewpager正在拖拽中，那么不拦截它的事件，直接return false；
////                            if (mIsVpDragger) {
////                                return false;
////                            }
////                            // 获取当前手指位置
////                            float endY = event.getY();
////                            float endX = event.getX();
////                            float distanceX = Math.abs(endX - startX);
////                            float distanceY = Math.abs(endY - startY);
////                            // 如果X轴位移大于Y轴位移，那么将事件交给viewPager处理。
////                            if (distanceX >= distanceY) {
////                            mIsVpDragger = true;
//////                                mPtrFrameLayout.getParent().requestDisallowInterceptTouchEvent(true);
////                                return false;
////                            }
////                            break;
////                        case MotionEvent.ACTION_UP:
////
////                        case MotionEvent.ACTION_CANCEL:
////                            // 初始化标记
////                            mIsVpDragger = false;
////                            break;
////                    }
//                    // 如果是Y轴位移大于X轴.
//                    return false;
//                }
//            });
//
//        } else {
//            mPtrFrameLayout.setEnabled(false);
//        }
//        if (null != mOnScrollListener) {
//            mOnScrollListener.onScroll(absListView, firstVisibleItem,
//                    visibleItemCount, totalItemCount);
//        }
//
//    }

}

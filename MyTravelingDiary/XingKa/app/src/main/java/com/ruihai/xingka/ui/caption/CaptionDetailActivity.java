package com.ruihai.xingka.ui.caption;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.flyco.dialog.entity.DialogMenuItem;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.flyco.dialog.widget.NormalDialog;
import com.flyco.dialog.widget.NormalListDialog;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orhanobut.logger.Logger;
import com.ruihai.iconicfontengine.widget.IconicFontTextView;
import com.ruihai.xingka.Constant;
import com.ruihai.xingka.Global;
import com.ruihai.xingka.R;
import com.ruihai.xingka.api.ApiModule;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.api.model.CommentItem;
import com.ruihai.xingka.api.model.CommentRepo;
import com.ruihai.xingka.api.model.ImageItem;
import com.ruihai.xingka.api.model.PhotoTopic;
import com.ruihai.xingka.api.model.PhotoTopicRepo;
import com.ruihai.xingka.api.model.PraiseItem;
import com.ruihai.xingka.api.model.ReportInfo;
import com.ruihai.xingka.api.model.ReportType;
import com.ruihai.xingka.api.model.User;
import com.ruihai.xingka.api.model.XKRepo;
import com.ruihai.xingka.ui.BaseActivity;
import com.ruihai.xingka.ui.MainActivity;
import com.ruihai.xingka.ui.caption.adapter.CommentListAdapter;
import com.ruihai.xingka.ui.caption.adapter.ImageFlingAdapter;
import com.ruihai.xingka.ui.caption.fragment.CaptionListFragment;
import com.ruihai.xingka.ui.common.PhotoPagerActivity;
import com.ruihai.xingka.ui.common.enter.EmojiFilter;
import com.ruihai.xingka.ui.common.enter.EmojiUtils;
import com.ruihai.xingka.ui.common.enter.EnterEmojiLayout;
import com.ruihai.xingka.ui.common.enter.EnterLayout;
import com.ruihai.xingka.ui.login.LoginActivity;
import com.ruihai.xingka.ui.mine.UserProfileActivity;
import com.ruihai.xingka.utils.AppUtility;
import com.ruihai.xingka.utils.QiniuHelper;
import com.ruihai.xingka.utils.Security;
import com.ruihai.xingka.utils.glide.GlideHelper;
import com.ruihai.xingka.widget.EnterLayoutAnimSupportContainer;
import com.ruihai.xingka.widget.ProgressHUD;
import com.ruihai.xingka.widget.flingswipe.SwipeFlingAdapterView;
import com.ruihai.xingka.widget.goodview.GoodView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * 图说详情页
 *
 * @author Zecker
 */
public class CaptionDetailActivity extends BaseActivity implements CommentListAdapter.OnItemClickListener, PullToRefreshBase.OnRefreshListener2 {


    public final static String KEY_PHOTOTOPIC_GUID = "KEY_PGUID";
    public final static String KEY_AUTHOR_ACCOUNT = "KEY_AUTHOR";
    public static final String DEFAULT_AVATAR_KEY = "00000000-0000-0000-0000-000000000000";
    private static final int DEFAULT_PER_PAGE = 20;
    @BindView(R.id.commonEnterRoot)
    FrameLayout mCommonEnterRoot;
    EnterLayoutAnimSupportContainer enterRootView;
    @BindView(R.id.tv_title)
    TextView mTitleView;
    @BindView(R.id.tv_right)
    IconicFontTextView mRightView;
    @BindView(R.id.elv_comment_list)
    PullToRefreshListView mRefreshListview;
    @BindView(R.id.bottom_layout)
    LinearLayout mBottomLayout;
    @BindView(R.id.rl_toolbar)
    RelativeLayout mToolbar;
    @BindView(R.id.ifb_praise)
    IconicFontTextView mPraiseButton;
    // private TextView mPraiseText;
    @BindView(R.id.ll_praise)
    LinearLayout paiseLayout;
    @BindView(R.id.ll_collect)
    LinearLayout collectLayout;
    @BindView(R.id.ifb_collect)
    IconicFontTextView mCollectButton;
    // private TextView mCollectText;
    @BindView(R.id.ifb_comment)
    IconicFontTextView mCommentButton;
    @BindView(R.id.ll_comment)
    LinearLayout commentLayout;
    @BindView(R.id.ifb_share)
    IconicFontTextView mShareButton;
    private SwipeFlingAdapterView mSwipeView;
    @BindView(R.id.ll_share)
    LinearLayout mShareLayout;

    //PullToRefreshExpandableListView mRefreshCommentExLV;
    //private ExpandableListView mCommentExLV;
    private ListView mCommentLV;
    private ClipboardManager clipboard;//粘贴板
    private EnterEmojiLayout mEnterLayout;
    private PhotoTopic mPhotoTopic;
    private List<CommentItem> mCommentItemList = new ArrayList<>();
    private CommentListAdapter mCommentAdapter;
    private View mListHeaderView;

    // 图说内容Views
    private TextView mAddFollowButton; //关注按钮
    private LinearLayout mPraiseLayout;
    private LinearLayout mPraiseUsersLayout; //
    private TextView mPraiseCountText;
    private ImageView hasMoreBtn;
    private TextView contentView;

    // 评论的position
    private int replyPosition = 0;
    private int mPopComment = 0;

    private String mPGuid;
    private String mAuthor;
    private String mAccount;
    private int mPage = 1;//页数
    private int mMaxPage = 0;//最大页数
    private int mPerPage = DEFAULT_PER_PAGE;//每页数量
    private boolean mLoadMore;
    private CommentItem mCommentItem;
    private boolean hasMore;
    private float mLastY = 0;
    private ActionSheetDialog dialog;
    private List<ReportType> reportInfoList = new ArrayList<>();
    private boolean isDown;
    private boolean hasMoreContentShow = true;
    // private String commentContent;
    private GoodView mGoodView;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            int lineCount = msg.what;
            if (lineCount > 4) {
                hasMoreBtn.setVisibility(View.VISIBLE);
                contentView.setMaxLines(4);
                hasMoreBtn.setImageResource(R.mipmap.icon_content_down);

            } else {
                hasMoreBtn.setVisibility(View.GONE);
            }
            return false;
        }
    });

    public static void launch(Activity from, String pGuid, String author) {
        Intent intent = new Intent(from, CaptionDetailActivity.class);
        intent.putExtra(KEY_PHOTOTOPIC_GUID, pGuid);
        intent.putExtra(KEY_AUTHOR_ACCOUNT, author);
        from.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caption_detail);
        ButterKnife.bind(this);
        // mTitleView.setText(R.string.module_caption);
        mTitleView.setVisibility(View.INVISIBLE);
        mRightView.setText("{xk-report}");
        mRightView.setVisibility(View.VISIBLE);
        mAccount = String.valueOf(AccountInfo.getInstance().loadAccount().getAccount());
        Log.e("本账户账号", mAccount);
        mPhotoTopic = getIntent().getParcelableExtra(CaptionListFragment.KEY_CAPTION_ITEM);
        mPopComment = getIntent().getIntExtra(CaptionListFragment.KEY_POP_COMMENT, 0);
        mCommentItem = getIntent().getParcelableExtra(CaptionListFragment.KEY_COMMENT_ITEM);
        mPGuid = getIntent().getStringExtra(KEY_PHOTOTOPIC_GUID);
        mAuthor = getIntent().getStringExtra(KEY_AUTHOR_ACCOUNT);
        mCommentLV = mRefreshListview.getRefreshableView();
        mRefreshListview.setMode(PullToRefreshBase.Mode.BOTH);
        mGoodView = new GoodView(this);

        if (!TextUtils.isEmpty(mPGuid) && !TextUtils.isEmpty(mAuthor)) {
            addReadingRecord(mPGuid, mAuthor);
            getCaptionData(mPGuid, mAuthor);
        } else if (mPhotoTopic != null) {
            mPGuid = mPhotoTopic.getpGuid();
            mAuthor = String.valueOf(mPhotoTopic.getAccount());
            addReadingRecord(mPGuid, mAuthor);
            initHeader();
            initViews();
            loadCommentData();
        }
        mCommentLV.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent ev) {
                final int action = ev.getAction();
                switch (action & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_MOVE:
                        final float y = ev.getY();
                        if (y > mLastY) // 向下
                        {
                            isDown = true;
                        } else // 向上
                        {
                            isDown = false;
                        }
                        mLastY = y;
                        break;
                }
                return false;
            }
        });
        mCommentLV.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case SCROLL_STATE_FLING:
                        if (isDown) {
                            mBottomLayout.setVisibility(View.GONE);
                            mToolbar.setVisibility(View.GONE);
                            mCommentLV.setMinimumHeight(AppUtility.getScreenHeight());
                        } else {
                            mBottomLayout.setVisibility(View.VISIBLE);
                            mToolbar.setVisibility(View.VISIBLE);
                            mCommentLV.setMinimumHeight(AppUtility.getScreenHeight() - AppUtility.dip2px(96));
                        }
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0 && getScrollY() == 0) {
                    mRefreshListview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                    mToolbar.setVisibility(View.VISIBLE);
                    mBottomLayout.setVisibility(View.VISIBLE);
                } else if (hasMore) {
                    mRefreshListview.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
                } else {
                    mRefreshListview.setMode(PullToRefreshBase.Mode.DISABLED);
                }


            }
        });
        Log.e("图说作者账号", "" + mAuthor);
        //mCommonEnterRoot.setTag(1);

//        if (mEnterLayout == null)
//            mEnterLayout = new EnterEmojiLayout(CaptionDetailActivity.this, onSendClicked, EnterLayout.Type.TextOnly, EnterEmojiLayout.EmojiType.SmallOnly);
//        mEnterLayout.content.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                commentContent = s.toString();
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                //   commentContent = s.toString();
//            }
//        });
    }


    /**
     * @功能描述 : 初始化图说详情内容
     */
    private void initHeader() {
        if (mListHeaderView == null) {
            //mListHeaderView = mInflater.inflate(R.layout.layout_caption_info, null, false);
            mListHeaderView = mInflater.inflate(R.layout.layout_caption_detail_info, null, false);
            mCommentLV.addHeaderView(mListHeaderView);
        }
        // 头像
        SimpleDraweeView avatar = (SimpleDraweeView) mListHeaderView.findViewById(R.id.sdv_avatar);
        Uri avatarUri = Uri.parse(QiniuHelper.getThumbnail96Url(mPhotoTopic.getAvatar()));
        if (!DEFAULT_AVATAR_KEY.equals(mPhotoTopic.getAvatar())) {
            avatar.setImageURI(avatarUri);
        }
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAccount.equals(String.valueOf(mPhotoTopic.getAccount()))) {
                    if (MainActivity.currentTabIndex != 3) {
                        MainActivity.launch(CaptionDetailActivity.this, 1);
                    }
                } else if (mPhotoTopic.isOffical()) {

                    UserProfileActivity.launch(CaptionDetailActivity.this, String.valueOf(mPhotoTopic.getAccount()), 1, 1);
                } else {
                    UserProfileActivity.launch(CaptionDetailActivity.this, String.valueOf(mPhotoTopic.getAccount()), 1, 0);
                }
            }
        });
        TextView name = (TextView) mListHeaderView.findViewById(R.id.tv_username);
        ImageView isOfficalAccount = (ImageView) mListHeaderView.findViewById(R.id.tv_officalaccount);
        // 用户名，判断备注是否为空，如果为空，显示nick，如果不为空，则显示remark。
        if (!TextUtils.isEmpty(mPhotoTopic.getRemark())) {
            name.setText(mPhotoTopic.getRemark());
        } else if (!TextUtils.isEmpty(mPhotoTopic.getNick())) {
            // name.setText(mPhotoTopic.getNick());
            name.setText(EmojiUtils.fromStringToEmoji1(mPhotoTopic.getNick(), CaptionDetailActivity.this));
        } else { // 显示行咖号
            name.setText(String.valueOf(mPhotoTopic.getAccount()));
        }
        if (mPhotoTopic.isOffical()) {
            isOfficalAccount.setVisibility(View.VISIBLE);
        }

        // 发布时间
        TextView afterTime = (TextView) mListHeaderView.findViewById(R.id.tv_aftertime);
        String datetime = mPhotoTopic.getAddTime().substring(6, 19);
        long timestamp = Long.valueOf(datetime);
        afterTime.setText(Global.dayToNow(timestamp));
        // 加关注
        mAddFollowButton = (TextView) mListHeaderView.findViewById(R.id.tv_attention);
        if (mPhotoTopic.isFriend()) {
            mAddFollowButton.setSelected(true);
            mAddFollowButton.setText("已关注");
            mAddFollowButton.setEnabled(false);
        } else {
            if (currentUser != null) {
                if (mPhotoTopic.getAccount() == currentUser.getAccount()) {
                    mAddFollowButton.setVisibility(View.GONE);
                } else {
                    mAddFollowButton.setVisibility(View.VISIBLE);
                }
            } else {
                mAddFollowButton.setVisibility(View.VISIBLE);
            }
        }
        mAddFollowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeAddFollow();
            }
        });
        // 图说内容
        contentView = (TextView) mListHeaderView.findViewById(R.id.tv_content);
        hasMoreBtn = (ImageView) mListHeaderView.findViewById(R.id.btn_content_more);
        if (!TextUtils.isEmpty(mPhotoTopic.getContent())) {
            contentView.setVisibility(View.VISIBLE);

            // String content = String.format(Constant.FORMAT_CAPTION_CONTENT, mPhotoTopic.getContent());
            //contentView.setText(EmojiUtils.fromStringToEmoji(Html.fromHtml(content), this));
            contentView.setText(EmojiUtils.fromStringToEmoji1(mPhotoTopic.getContent(), this));
            contentView.post(new Runnable() {

                @Override
                public void run() {
                    int lineCount = contentView.getLineCount();
                    mHandler.sendEmptyMessage(lineCount);
                }
            });
        } else {
            contentView.setVisibility(View.GONE);
        }


        hasMoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasMoreContentShow) {
                    contentView.setMaxLines(contentView.getLineCount());
                    hasMoreBtn.setImageResource(R.mipmap.icon_content_up);
                    hasMoreContentShow = false;
                } else {
                    contentView.setMaxLines(4);
                    hasMoreBtn.setImageResource(R.mipmap.icon_content_down);
                    hasMoreContentShow = true;
                }
            }
        });

        //官号点击内容进入web页
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPhotoTopic.isOffical() && !TextUtils.isEmpty(mPhotoTopic.getWebUrl())) {
                    OfficalCaptionInfoActivity.launch(CaptionDetailActivity.this, mPhotoTopic.getWebUrl(), mPhotoTopic.getpGuid());
                }
            }
        });


        //  int screenWidth = AppUtility.getScreenWidth();

//        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams((int) (7F * (screenWidth / 10F)), LinearLayout.LayoutParams.WRAP_CONTENT);
//        params1.setMargins(0, 0, 0, AppUtility.dip2px(10));
//        contentView.setLayoutParams(params1);
//        contentView.setGravity(Gravity.CENTER_HORIZONTAL);
        // contentView.setWidth((int) (7F * (screenWidth / 10F)));
        contentView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                normalListDialogNoTitle(mPhotoTopic.getContent());
                return false;
            }
        });


        // 图说地址
        LinearLayout ll_address = (LinearLayout) mListHeaderView.findViewById(R.id.at_address_layout);
        TextView mAddress = (TextView) mListHeaderView.findViewById(R.id.tv_address);
        if (!TextUtils.isEmpty(mPhotoTopic.getAddress())) {
            ll_address.setVisibility(View.VISIBLE);
            mAddress.setText(mPhotoTopic.getAddress());
        } else {
            ll_address.setVisibility(View.GONE);
        }

        // 图片列表
        LinearLayout imagesLayout = (LinearLayout) mListHeaderView.findViewById(R.id.images_layout);
        //LinearLayout swipeviewLayout = (LinearLayout) mListHeaderView.findViewById(R.id.swipeview_layout);
        //imagesLayout.setVisibility(View.GONE);
        //mSwipeView = (SwipeFlingAdapterView) mListHeaderView.findViewById(R.id.swipeview);
        //ImageView coverImage = (ImageView) mListHeaderView.findViewById(R.id.iv_image1);
        final List<ImageItem> imageItems = mPhotoTopic.getImageList();
        final List<ImageView> coverImageViews = new ArrayList<ImageView>();
        coverImageViews.add((ImageView) mListHeaderView.findViewById(R.id.iv_image1));
        coverImageViews.add((ImageView) mListHeaderView.findViewById(R.id.iv_image2));
        coverImageViews.add((ImageView) mListHeaderView.findViewById(R.id.iv_image3));
        coverImageViews.add((ImageView) mListHeaderView.findViewById(R.id.iv_image4));
        coverImageViews.add((ImageView) mListHeaderView.findViewById(R.id.iv_image5));
        coverImageViews.add((ImageView) mListHeaderView.findViewById(R.id.iv_image6));
        coverImageViews.add((ImageView) mListHeaderView.findViewById(R.id.iv_image7));
        coverImageViews.add((ImageView) mListHeaderView.findViewById(R.id.iv_image8));
        coverImageViews.add((ImageView) mListHeaderView.findViewById(R.id.iv_image9));
        final ArrayList<String> imageUrls = new ArrayList<>();
        if (imageItems.size() > 0) {
            imagesLayout.setVisibility(View.VISIBLE);
            for (int i = 0; i < imageItems.size(); i++) {
                coverImageViews.get(i).setVisibility(View.VISIBLE);
                int screemWidth = AppUtility.getScreenWidth();
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(screemWidth, (int) (3F * (screemWidth / 4F)));
                params.setMargins(0, AppUtility.dip2px(30), 0, 0);
                coverImageViews.get(i).setLayoutParams(params);
                GlideHelper.loadTopicCoverWithKey(imageItems.get(i).imgSrc, coverImageViews.get(i), null);
                imageUrls.add(QiniuHelper.getOriginalWithKey(imageItems.get(i).imgSrc));
                coverImageViews.get(i).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = coverImageViews.indexOf(v);
                        addReadingRecord(mPGuid, mAuthor);
                        if (mPhotoTopic.isOffical() && !TextUtils.isEmpty(mPhotoTopic.getWebUrl())) {
                            OfficalCaptionInfoActivity.launch(CaptionDetailActivity.this, mPhotoTopic.getWebUrl(), mPhotoTopic.getpGuid());
                        } else {
                            Intent intent = new Intent(mContext, PhotoPagerActivity.class);
                            intent.putExtra(PhotoPagerActivity.EXTRA_CURRENT_ITEM, position);
                            intent.putExtra(PhotoPagerActivity.EXTRA_PHOTOS, imageUrls);
                            intent.putExtra(PhotoPagerActivity.EXTRA_TYPE, 1);
                            intent.putExtra(PhotoPagerActivity.EXTRA_USERNICK, mPhotoTopic.getNick());
                            startActivity(intent);
                        }
                    }
                });
            }

        } else {
            imagesLayout.setVisibility(View.GONE);
        }
//        if (imageItems.size() == 1) {//单张图片
//
//            imagesLayout.setVisibility(View.VISIBLE);
//            swipeviewLayout.setVisibility(View.GONE);
//            GlideHelper.loadTopicCoverWithKey(imageItems.get(0).imgSrc, coverImage, null);
//            coverImage.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    addReadingRecord(mPGuid, mAuthor);
//                    if (mPhotoTopic.isOffical() && !TextUtils.isEmpty(mPhotoTopic.getWebUrl())) {
//                        OfficalCaptionInfoActivity.launch(CaptionDetailActivity.this, mPhotoTopic.getWebUrl(), mPhotoTopic.getpGuid());
//                    } else {
//                        ArrayList<String> imageUrls = new ArrayList<>();
//                        imageUrls.add(QiniuHelper.getOriginalWithKey(imageItems.get(0).imgSrc));
//                        Intent intent = new Intent(mContext, PhotoPagerActivity.class);
//                        intent.putExtra(PhotoPagerActivity.EXTRA_CURRENT_ITEM, 0);
//                        intent.putExtra(PhotoPagerActivity.EXTRA_PHOTOS, imageUrls);
//                        intent.putExtra(PhotoPagerActivity.EXTRA_TYPE, 1);
//                        intent.putExtra(PhotoPagerActivity.EXTRA_USERNICK, mPhotoTopic.getNick());
//                        startActivity(intent);
//                    }
//                }
//            });
//        } else if (imageItems.size() > 1) {//多张图片
//            imagesLayout.setVisibility(View.GONE);
//            swipeviewLayout.setVisibility(View.VISIBLE);
//            List<String> imageKeys = new ArrayList<>();
//            for (ImageItem imageItem : imageItems) {
//                imageKeys.add(imageItem.imgSrc);
//            }
//            initSwipeView(imageKeys);
//        } else {
//            imagesLayout.setVisibility(View.GONE);
//            swipeviewLayout.setVisibility(View.GONE);
//        }

        // 点赞操作
//        mPraiseButton = (IconicFontTextView) mListHeaderView.findViewById(R.id.ifb_praise);
//        mPraiseText = (TextView) mListHeaderView.findViewById(R.id.ift_praise);
        if (mPhotoTopic.isPraise()) {
            mPraiseButton.setSelected(true);
            mPraiseButton.setText("{xk-praise-selected}");
//            mPraiseText.setSelected(true);
            //       mPraiseText.setText("已赞");
        } else {
            mPraiseButton.setSelected(false);
            mPraiseButton.setText("{xk-praise}");
        }
//        LinearLayout praiseLayout = (LinearLayout) mListHeaderView.findViewById(R.id.ll_praise);
        paiseLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppUtility.isFastClick()) {
                    return;
                }
                if (mPhotoTopic == null) {
                    AppUtility.showToast("图说载入失败，不能点赞");
                    return;
                }
                executePraise();

            }
        });
//
//        // 收藏操作
//        mCollectButton = (IconicFontTextView) mListHeaderView.findViewById(R.id.ifb_collect);
//        mCollectText = (TextView) mListHeaderView.findViewById(R.id.ift_collect);
        if (mPhotoTopic.isCollect()) {
            mCollectButton.setSelected(true);
            mCollectButton.setText("{xk-collect-selected}");
//            mCollectText.setSelected(true);
            //      mCollectText.setText("已收藏");
        }
//        LinearLayout collectLayout = (LinearLayout) mListHeaderView.findViewById(R.id.ll_collect);
        collectLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppUtility.isFastClick()) {
                    return;
                }
                if (mPhotoTopic == null) {
                    AppUtility.showToast("图说载入失败，不能收藏");
                    return;
                }
                execureCollection();
            }
        });
//
//        // 评论
//        LinearLayout commentLayout = (LinearLayout) mListHeaderView.findViewById(R.id.ll_comment);
        commentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AccountInfo.getInstance().isLogin()) {
                    startActivity(new Intent(mContext, LoginActivity.class));
                    return;
                }
                prepareAddComment(mPhotoTopic, true);
            }
        });
//
//        //分享
//        mShareLayout = (LinearLayout) mListHeaderView.findViewById(R.id.ll_share);
//        //mShareButton = (IconicFontTextView) mListHeaderView.findViewById(R.id.ifb_share);
        mShareLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addReadingRecord(mPGuid, mAuthor);
                ShareCaptionActivity.launch(CaptionDetailActivity.this, mPhotoTopic, 1);
            }
        });
        mPraiseLayout = (LinearLayout) mListHeaderView.findViewById(R.id.layout_praise);
        mPraiseCountText = (TextView) mListHeaderView.findViewById(R.id.text_praise_count);
        // 显示点赞好友头像
        mPraiseUsersLayout = (LinearLayout) mListHeaderView.findViewById(R.id.layout_praise_users);
        displayPraiseUser();

    }


    private void initViews() {
        TextView textview = (TextView) mListHeaderView.findViewById(R.id.tv_more);
        mCommentAdapter = new CommentListAdapter(mContext, mCommentItemList, mAuthor, mAccount, textview, 1);
        mCommentAdapter.setOnItemClickListener(this);
        mRefreshListview.setOnRefreshListener(this);
        //去掉评论回复列表的分割线
        mCommentLV.setDivider(null);
        mCommentLV.setAdapter(mCommentAdapter);
        //mCommentExLV.setGroupIndicator(null);
        mCommentLV.setSelector(new ColorDrawable(Color.WHITE));
        // expandAllGroup();
        //点击expandablelistview不收缩
//        mCommentExLV.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
//            @Override
//            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
//                return true;
//            }
//        });

        mEnterLayout = new EnterEmojiLayout(this, onSendClicked, EnterLayout.Type.TextOnly, EnterEmojiLayout.EmojiType.SmallOnly);

        if (mPopComment == 1) {
            if (mCommentItem != null) {
                prepareAddComment(mCommentItem, true);
            } else {
                prepareAddComment(mPhotoTopic, true);
            }
        } else {
            // prepareAddComment(mPhotoTopic, false);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        notifyData(mPGuid, mAuthor);
        //getCaptionData(mPGuid, mAuthor);
        Log.i("TAG", "-------onResume------->");
//        EditText editText = mEnterLayout.content;l
//        if (!TextUtils.isEmpty(editText.getText().toString())) {
//            editText.setText(editText.getText().toString());
//        }
    }
//    private void expandAllGroup() {
//        // 默认展开每一个分组
//        for (int i = 0; i < mCommentAdapter.getGroupCount(); i++) {
//            mCommentExLV.expandGroup(i);
//        }
//    }

    @OnClick(R.id.tv_back)
    void backClicked() {
        finish();
    }

    /**
     * @功能描述 : 点击阅读记录
     */
    private void addReadingRecord(String pGuid, String author) {
        String sPGuid = Security.aesEncrypt(pGuid);
        String sAuthor = Security.aesEncrypt(author);
        ApiModule.apiService_1().photoTopicReadAdd(sPGuid, sAuthor).enqueue(new Callback<XKRepo>() {
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

    /**
     * @param pGuid  图说GUID
     * @param author 图说作者行账号
     * @功能描述 : 获取图说详情
     */
    private void getCaptionData(String pGuid, String author) {
        User user = AccountInfo.getInstance().loadAccount();
        String account = Security.aesEncrypt(String.valueOf(user.getAccount()));
        String sPGuid = Security.aesEncrypt(pGuid);
        String sAuthor = Security.aesEncrypt(author);
        // Log.i("TAG", "---account---->" + account + "----sPGuid---->" + sPGuid + "---sAuthor----->" + sAuthor);
        ApiModule.apiService().PhotoTopicDetailNoC2_1(account, sPGuid, sAuthor).enqueue(new Callback<PhotoTopicRepo>() {
            @Override
            public void onResponse(Call<PhotoTopicRepo> call, Response<PhotoTopicRepo> response) {
                PhotoTopicRepo photoTopicRepo = response.body();
                String msg = photoTopicRepo.getMsg();
                if (photoTopicRepo.isSuccess()) {
                    mPhotoTopic = photoTopicRepo.getPhotoTopic();
                    initHeader();
                    initViews();
                    loadCommentData();
                } else {
                    if ("暂无数据".equals(msg)) {
                        final NormalDialog dialog = new NormalDialog(mContext);
                        dialog.isTitleShow(false)
                                .bgColor(Color.parseColor("#ffffff"))
                                .cornerRadius(5)
                                .content("该图说已被删除")
                                .btnNum(1)
                                .btnText("确定")
                                .contentGravity(Gravity.CENTER)
                                .contentTextColor(Color.parseColor("#33333d"))
                                .dividerColor(Color.parseColor("#dcdce4"))
                                .btnTextSize(15.5f, 15.5f)
                                .btnTextColor(Color.parseColor("#ff2814"), Color.parseColor("#0077fe"))
                                .widthScale(0.85f)
                                .show();
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.setOnBtnClickL(new OnBtnClickL() {
                            @Override
                            public void onBtnClick() {
                                Intent intent = new Intent();
                                intent.putExtra("position", getIntent().getIntExtra("position", -100));
                                setResult(200, intent);
                                finish();
                                dialog.dismiss();
                            }
                        });
                    } else
                        ProgressHUD.showInfoMessage(mContext, photoTopicRepo.getMsg());
                }
            }

            @Override
            public void onFailure(Call<PhotoTopicRepo> call, Throwable t) {
                Log.i("TAG", "-----错误提示----->" + t.getMessage());
                //ProgressHUD.showInfoMessage(mContext, getString(R.string.common_network_error));
            }

        });
    }

    /**
     * @功能描述 : 获取评论数据列表
     **/
    private void loadCommentData() {
        int currentAccount = 0;
        if (currentUser != null) {
            currentAccount = currentUser.getAccount();
        }
        String account = Security.aesEncrypt(String.valueOf(currentAccount));
        String pGuid = Security.aesEncrypt(mPGuid);
        String author = Security.aesEncrypt(String.valueOf(mAuthor));
        final String page = Security.aesEncrypt(String.valueOf(mPage));
        String pageSize = Security.aesEncrypt(String.valueOf(mPerPage));
        //Log.e("TAG", "------页数------>" + mLoadMore + mPage);
        ApiModule.apiService().photoTopicDetailOnlyCPages(account, pGuid, author, page, pageSize).enqueue(new Callback<CommentRepo>() {
            @Override
            public void onResponse(Call<CommentRepo> call, Response<CommentRepo> response) {
                CommentRepo commentRepo = response.body();
                mRefreshListview.onRefreshComplete();
                if (commentRepo.isSuccess()) {
                    mMaxPage = (commentRepo.getRecordCount() + mPerPage - 1) / mPerPage;
                    if (mPage < mMaxPage) {
                        mRefreshListview.setMode(PullToRefreshBase.Mode.BOTH);
                        hasMore = true;
                    } else {
                        mRefreshListview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                        hasMore = false;
                    }
                    // Log.e("TAG", "------最大页数------>" + mLoadMore + mPage);
                    if (mPage == 1) {
                        mCommentItemList.clear();
                    }
                    List<CommentItem> commentItemList = commentRepo.getCommentItemList();
                    mCommentItemList.addAll(commentItemList);
                    View view = (View) mListHeaderView.findViewById(R.id.view);
                    View view1 = (View) mListHeaderView.findViewById(R.id.view1);
                    TextView textView = (TextView) mListHeaderView.findViewById(R.id.tv_more);

                    //如果评论为空,则评论区域不显示
                    if (mCommentItemList.size() > 0) {
                        //最新评论
                        textView.setVisibility(View.VISIBLE);
                    } else {
                        //最新评论
                        textView.setVisibility(View.GONE);
                        //mRefreshCommentExLV.setMode(PullToRefreshBase.Mode.MANUAL_REFRESH_ONLY);
                    }
                    // mCommentAdapter.notifyDataSetChanged();
                    if (mPage == 1) {
                        mCommentLV.setAdapter(mCommentAdapter);
                    } else {
                        mCommentAdapter.notifyDataSetChanged();
                    }
                    //mCommentLV.setSelection(0);
                    //expandAllGroup();
                }
            }

            @Override
            public void onFailure(Call<CommentRepo> call, Throwable t) {
                mRefreshListview.onRefreshComplete();
                ProgressHUD.showInfoMessage(mContext, getString(R.string.common_network_error));
            }
        });
    }

    private void prepareAddComment(Object data, boolean popKeyboard) {


        CommentItem comment = null;
        EditText content = mEnterLayout.content;
//        if (!TextUtils.isEmpty(commentContent)) {
//            content.setText(commentContent);
//            content.setSelection(commentContent.length());
//        }
        if (data instanceof CommentItem) {
            comment = (CommentItem) data;
            content.setHint("回复" + comment.getReviewUName());
            content.setTag(comment);
        } else if (data instanceof PhotoTopic) {
            comment = new CommentItem();
            content.setHint("你的评论会让咖主更有动力");
            content.setTag(comment);
        }
        mEnterLayout.restoreLoad(comment);
        Log.i("TAG", "-------是否谈起评论框------->" + mPopComment + popKeyboard);
        mCommonEnterRoot.setVisibility(View.VISIBLE);
        mBottomLayout.setVisibility(View.GONE);
        AppUtility.popSoftkeyboard(this, content, true);
//        if (popKeyboard) {
//            content.requestFocus();
//            //AppUtility.showSoftInput(this, content);
//            AppUtility.popSoftkeyboard(this, content, true);
//        } else {
//            content.clearFocus();
//            //AppUtility.hideSoftInput(this, content);
//            AppUtility.popSoftkeyboard(this, content, false);
//        }

    }

    //    private View.OnClickListener onSendClicked = new View.OnClickListener() {
//
//        @Override
//        public void onClick(View v) {
//            mCommonEnterRoot.setSelected(true);
//            if (mPhotoTopic == null) {
//                AppUtility.showToast("图说载入失败，不能发送给评论");
//
//            }
//
//            if (mEnterLayout != null) {
//                mEnterLayout.hideKeyboard();
//                mEnterLayout.getEnterLayoutAnimSupportContainer().getPanelLayout().setVisibility(View.GONE);
//                mEnterLayout.getEnterLayoutAnimSupportContainer().setEnterLayoutBottomMargin(AppUtility.dip2px(-200));
//            }
//
//            EditText content = mEnterLayout.content;
//            String input = content.getText().toString();
//            if (EmojiFilter.containsEmptyEmoji(v.getContext(), input)) {
//
//            }
//            CommentItem comment = (CommentItem) content.getTag();
//            sendComment(input, comment);
//        }
//
//
//    };
    private View.OnTouchListener onSendClicked = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (AppUtility.isFastClick()) {
                return false;
            }
            if (mPhotoTopic == null) {
                AppUtility.showToast("图说载入失败，不能发送给评论");
                return false;
            }

            if (mEnterLayout != null) {
                mEnterLayout.hideKeyboard();
                mEnterLayout.getEnterLayoutAnimSupportContainer().getPanelLayout().setVisibility(View.GONE);
                mEnterLayout.getEnterLayoutAnimSupportContainer().setEnterLayoutBottomMargin(AppUtility.dip2px(-200));
            }

            EditText content = mEnterLayout.content;

            String input = content.getText().toString();
            if (EmojiFilter.containsEmptyEmoji(v.getContext(), input)) {
                return false;
            }

            CommentItem comment = (CommentItem) content.getTag();
            sendComment(input, comment);
            return false;
        }


    };


    /**
     * @param contentStr 评论&回复内容
     * @param comment
     * @功能描述 : 提交评论 & 回复评论 & 回复评论的回复
     **/
    private void sendComment(final String contentStr, final CommentItem comment) {
        ProgressHUD.showLoadingMessage(mContext, "正在发送中...", false);
        String account = Security.aesEncrypt(String.valueOf(currentUser.getAccount())); // 行账号
        String pGuid = Security.aesEncrypt(mPhotoTopic.getpGuid()); // 图说主键
        String toAccount = Security.aesEncrypt(String.valueOf(mPhotoTopic.getAccount())); // 图说发布人行账号
        String content = Security.aesEncrypt(contentStr); // 评论内容
        String isReply = ""; // 1-是回复 0-不是回复
        String toUser = ""; // 回复谁
        String toGuid = ""; // 回复评论顶级主键
        String pushAccount = ""; // @的人的行账号,多个行账号之间用英文逗号(,)隔开,不需要@传0
        if (comment.getGuid() == null) { // 评论
            isReply = Security.aesEncrypt("0");
            toUser = Security.aesEncrypt("0");
            toGuid = Security.aesEncrypt(Constant.DEFAULT_UUID);
            pushAccount = Security.aesEncrypt("0");
        } else { // 回复评论
            if (comment.getReplyGuid().equals(Constant.DEFAULT_UUID)) { // 评论的回复
                toUser = Security.aesEncrypt("0");
                toGuid = Security.aesEncrypt(comment.getGuid());
            } else { // 评论回复的回复
                toUser = Security.aesEncrypt(comment.getReviewUid());
                toGuid = Security.aesEncrypt(comment.getReplyGuid());
            }
            isReply = Security.aesEncrypt("1");
            pushAccount = Security.aesEncrypt("0");
        }
        for (int i = 0; i < mCommentItemList.size(); i++) {
            if (mCommentItemList.get(i).getGuid().equals(comment.getGuid())) {
                replyPosition = i;
            }
        }
        ApiModule.apiService().photoTopicCommentAdd(account, pGuid, content, isReply, toUser, toGuid, toAccount, pushAccount).enqueue(new Callback<XKRepo>() {


            @Override
            public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                XKRepo xkRepo = response.body();
                ProgressHUD.dismiss();
                if (xkRepo.isSuccess()) {
                    addReadingRecord(mPGuid, mAuthor);
                    ProgressHUD.showSuccessMessage(mContext, "发送成功!");
                    mCommonEnterRoot.setVisibility(View.GONE);
                    mBottomLayout.setVisibility(View.VISIBLE);

                    if (comment.getGuid() == null) { // 评论
                        CommentItem commentItem = new CommentItem(
                                xkRepo.getReturnGuid(), String.valueOf(currentUser.getAccount()),
                                currentUser.getNickname(),
                                currentUser.getAvatar(),
                                contentStr
                        );
                        //显示最新评论
                        //间距
                        View view = (View) findViewById(R.id.view);
                        view.setVisibility(View.VISIBLE);

                        View view1 = (View) findViewById(R.id.view1);
                        view1.setVisibility(View.VISIBLE);
                        //最新评论
                        TextView textView = (TextView) findViewById(R.id.tv_more);
                        textView.setVisibility(View.VISIBLE);
                        mCommentItemList.add(0, commentItem);
                        mPhotoTopic.setCommentList(mCommentItemList);

//                        Intent intent = new Intent();
//                        intent.putExtra(ListModify.TYPE, ListModify.Edit);
//                        intent.putExtra(ListModify.DATA, mPhotoTopic);
//                        setResult(Activity.RESULT_OK, intent);
                        EventBus.getDefault().post(mPhotoTopic);
                    } else { // 评论的回复/评论回复的回复
                        String toWho = "";
                        String replyGuid = "";
                        String replyUName = "";
                        if (comment.getReplyGuid().equals(Constant.DEFAULT_UUID)) { // 评论的回复
                            toWho = "0";
                            replyUName = "";
                            replyGuid = comment.getGuid();

                        } else { // 评论回复的回复
                            toWho = comment.getReviewUid();
                            replyUName = comment.getReviewUName();
                            replyGuid = comment.getReplyGuid();
                        }
                        CommentItem commentItem = new CommentItem(
                                xkRepo.getReturnGuid(),
                                String.valueOf(currentUser.getAccount()),
                                currentUser.getNickname(),
                                currentUser.getAvatar(),
                                contentStr,
                                replyGuid,
                                toWho,
                                replyUName
                        );
                        CommentItem commentItemTemp = mCommentItemList.get(replyPosition);
                        if (commentItemTemp.getSubCommentItems() == null) {
                            List<CommentItem> commentItems = new ArrayList<>();
                            commentItems.add(commentItem);
                            commentItemTemp.setSubCommentItems(commentItems);
                        } else {
                            commentItemTemp.getSubCommentItems().add(commentItem);
                        }
                    }
                    mEnterLayout.clearContent();
                    //mEnterLayout.hideKeyboard();
                    mCommentAdapter.notifyDataSetChanged();
                    //expandAllGroup();
                } else {
                    //mEnterLayout.hideKeyboard();
                    ProgressHUD.showInfoMessage(mContext, xkRepo.getMsg());
                }
            }

            @Override
            public void onFailure(Call<XKRepo> call, Throwable t) {
                ProgressHUD.showInfoMessage(mContext, getString(R.string.common_network_error));
            }
        });
    }

    // --------------------------- 点赞 ---------------------------

    /**
     * @功能描述 : 提交点赞操作
     */
    private void executePraise() {
        if (!AccountInfo.getInstance().isLogin()) {
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }
        addReadingRecord(mPGuid, mAuthor);
        final String isPraiseStr = mPhotoTopic.isPraise() ? "0" : "1";
        String account = Security.aesEncrypt(String.valueOf(currentUser.getAccount()));
        String pGuid = Security.aesEncrypt(mPhotoTopic.getpGuid());
        String toAccount = Security.aesEncrypt(String.valueOf(mPhotoTopic.getAccount()));
        final String isPraise = Security.aesEncrypt(isPraiseStr); // 1-点赞，0-取消赞
        ApiModule.apiService().photoTopicPraiseAdd(account, pGuid, isPraise, toAccount).enqueue(new Callback<XKRepo>() {
            @Override
            public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                XKRepo xkRepo = response.body();
                if (xkRepo.isSuccess()) {
                    if (isPraiseStr.equals("0")) {
                        mPraiseButton.setSelected(false);
                        mPraiseButton.setText("{xk-praise}");
                        // 设置数据
                        mPhotoTopic.setIsPraise(false);
                        List<PraiseItem> praiseItems = mPhotoTopic.getPraiseList();
                        for (Iterator<PraiseItem> iterator = praiseItems.iterator(); iterator.hasNext(); ) {
                            PraiseItem praise = iterator.next();
                            if (praise.getAccount() == currentUser.getAccount()) {
                                iterator.remove();
                                mPhotoTopic.setPraiseNum(mPhotoTopic.getPraiseNum() - 1);
                            }
                        }
                    } else {
                        // ProgressHUD.showPraiseOrCollectSuccessMessage(CaptionDetailActivity.this, "谢谢你的喜欢");
                        mGoodView.setTextInfo("谢谢喜欢", Color.parseColor("#ff941A"), 10);
                        mGoodView.show(mPraiseButton);

                        mPraiseButton.setSelected(true);
                        mPraiseButton.setText("{xk-praise-selected}");
//                        mPraiseText.setSelected(true);
                        //       mPraiseText.setText("已赞");
                        // 设置数据
                        mPhotoTopic.setIsPraise(true);

                        List<PraiseItem> praiseItems = mPhotoTopic.getPraiseList();
                        PraiseItem item = new PraiseItem();
                        currentUser = AccountInfo.getInstance().loadAccount();
                        item.setAccount(currentUser.getAccount());
                        item.setAvatar(currentUser.getAvatar());
                        item.setNick(currentUser.getNickname());
                        praiseItems.add(0, item);
                        mPhotoTopic.setPraiseNum(mPhotoTopic.getPraiseNum() + 1);

                    }
                    displayPraiseUser();

//                    Intent intent = new Intent();
//                    intent.putExtra(ListModify.TYPE, ListModify.Edit);
//                    intent.putExtra(ListModify.DATA, mPhotoTopic);
//                    setResult(Activity.RESULT_OK, intent);
                    EventBus.getDefault().post(mPhotoTopic);
                } else {
                    ProgressHUD.showInfoMessage(mContext, xkRepo.getMsg());
                }
            }

            @Override
            public void onFailure(Call<XKRepo> call, Throwable t) {

//                if (isPraiseStr.equals("0")) {
//                    mPraiseButton.setSelected(true);
//                    mPraiseButton.setText("{xk-praise-selected}");
//                    // 设置数据
//                    mPhotoTopic.setIsPraise(true);
//                    List<PraiseItem> praiseItems = mPhotoTopic.getPraiseList();
//                    PraiseItem item = new PraiseItem();
//                    currentUser = AccountInfo.getInstance().loadAccount();
//                    item.setAccount(currentUser.getAccount());
//                    item.setAvatar(currentUser.getAvatar());
//                    item.setNick(currentUser.getNickname());
//                    praiseItems.add(0, item);
//                    mPhotoTopic.setPraiseNum(mPhotoTopic.getPraiseNum() + 1);
//
//                } else {
//                    mPraiseButton.setSelected(false);
//                    mPraiseButton.setText("{xk-praise}");
////                        mPraiseText.setSelected(true);
//                    //       mPraiseText.setText("已赞");
//                    // 设置数据
//                    mPhotoTopic.setIsPraise(false);
//
//                    List<PraiseItem> praiseItems = mPhotoTopic.getPraiseList();
//                    for (Iterator<PraiseItem> iterator = praiseItems.iterator(); iterator.hasNext(); ) {
//                        PraiseItem praise = iterator.next();
//                        if (praise.getAccount() == currentUser.getAccount()) {
//                            iterator.remove();
//                            mPhotoTopic.setPraiseNum(mPhotoTopic.getPraiseNum() - 1);
//                        }
//                    }
//
//                }
//                displayPraiseUser();
//                Intent intent = new Intent();
//                intent.putExtra(ListModify.TYPE, ListModify.Edit);
//                intent.putExtra(ListModify.DATA, mPhotoTopic);
//                setResult(Activity.RESULT_OK, intent);
            }


        });
    }

    // --------------------------- 收藏 ---------------------------

    /**
     * @功能描述 : 提交收藏操作
     */
    private void execureCollection() {
        if (!AccountInfo.getInstance().isLogin()) {
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }
        final String isCollectStr = mCollectButton.isSelected() ? "0" : "1";
        String account = Security.aesEncrypt(String.valueOf(currentUser.getAccount()));
        String pGuid = Security.aesEncrypt(mPhotoTopic.getpGuid());
        String toAccount = Security.aesEncrypt(String.valueOf(mPhotoTopic.getAccount()));
        String isCollect = Security.aesEncrypt(isCollectStr); // 1-收藏 0-取消收藏
        addReadingRecord(mPGuid, mAuthor);
        ApiModule.apiService().photoTopicCollectionAdd(account, pGuid, isCollect, toAccount).enqueue(new Callback<XKRepo>() {
            @Override
            public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                XKRepo xkRepo = response.body();
                if (xkRepo.isSuccess()) {

                    if (isCollectStr.equals("0")) {
                        mCollectButton.setSelected(false);
                        mCollectButton.setText("{xk-collect}");
//                        mCollectText.setSelected(false);
                        //   mCollectText.setText("收藏");
                        // 设置数据
                        mPhotoTopic.setIsCollect(false);
                    } else {
                        mGoodView.setTextInfo("谢谢收藏", Color.parseColor("#ff941A"), 10);
                        mGoodView.show(mCollectButton);
                        // ProgressHUD.showPraiseOrCollectSuccessMessage(CaptionDetailActivity.this, "谢谢你的收藏");
                        mCollectButton.setSelected(true);
                        mCollectButton.setText("{xk-collect-selected}");
//                        mCollectText.setSelected(true);
                        //      mCollectText.setText("已收藏");
                        // 设置数据
                        mPhotoTopic.setIsCollect(true);
                    }
//                    Intent intent = new Intent();
//                    intent.putExtra(ListModify.TYPE, ListModify.Edit);
//                    intent.putExtra(ListModify.DATA, mPhotoTopic);
//                    setResult(Activity.RESULT_OK, intent);
                    EventBus.getDefault().post(mPhotoTopic);
                } else {
                    ProgressHUD.showInfoMessage(mContext, xkRepo.getMsg());
                }
            }

            @Override
            public void onFailure(Call<XKRepo> call, Throwable t) {
//                if (isCollectStr.equals("0")) {
//                    mCollectButton.setSelected(true);
//                    mCollectButton.setText("{xk-collect-selected}");
//                    // 设置数据
//                    mPhotoTopic.setIsCollect(true);
//                } else {
//                    mCollectButton.setSelected(false);
//                    mCollectButton.setText("{xk-collect}");
//                    // 设置数据
//                    mPhotoTopic.setIsCollect(false);
//                }
//                Intent intent = new Intent();
//                intent.putExtra(ListModify.TYPE, ListModify.Edit);
//                intent.putExtra(ListModify.DATA, mPhotoTopic);
//                setResult(Activity.RESULT_OK, intent);
                ProgressHUD.showInfoMessage(mContext, getString(R.string.common_network_error));
            }
        });
    }

    // --------------------------- 加关注 ---------------------------

    /**
     * @功能描述 : 提交关注操作
     */
    private void executeAddFollow() {
        if (!AccountInfo.getInstance().isLogin()) {
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }
        // ProgressHUD.showPraiseOrCollectSuccessMessage(mContext, "谢谢你的关注");
        mAddFollowButton.setSelected(true);
        mAddFollowButton.setText("已关注");
        mAddFollowButton.setClickable(false);
        mPhotoTopic.setIsFriend(true);
        mPhotoTopic.setIsFriend(true);
        String account = Security.aesEncrypt(String.valueOf(currentUser.getAccount()));
        String friendAccount = Security.aesEncrypt(String.valueOf(mPhotoTopic.getAccount()));

        ApiModule.apiService().addFriend(account, friendAccount).enqueue(new Callback<XKRepo>() {


            @Override
            public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                XKRepo xkRepo = response.body();
                String message = xkRepo.getMsg();
                if (xkRepo.isSuccess()) {
//                    mAddFollowButton.setSelected(true);
//                    mAddFollowButton.setText("已关注");
//                    mAddFollowButton.setClickable(false);
                    //                   mPhotoTopic.setIsFriend(true);
//                    Intent intent = new Intent();
//                    intent.putExtra(ListModify.TYPE, ListModify.Edit);
//                    intent.putExtra(ListModify.DATA, mPhotoTopic);
//                    setResult(Activity.RESULT_OK, intent);
                    EventBus.getDefault().post(mPhotoTopic);
                } else {
                    ProgressHUD.showInfoMessage(mContext, message);
                }
            }

            @Override
            public void onFailure(Call<XKRepo> call, Throwable t) {
                ProgressHUD.showInfoMessage(mContext, getString(R.string.common_network_error));

            }

        });
    }

    // ------------------------- 显示 -----------------------------

    /**
     * @功能描述 : 显示点赞用户头像
     */
    private void displayPraiseUser() {

        mPraiseCountText.setText(String.valueOf(mPhotoTopic.getPraiseNum()));
        List<PraiseItem> praiseList = mPhotoTopic.getPraiseList();
        if (praiseList.size() == 0) {
            mPraiseLayout.setVisibility(View.GONE);
        } else {
            mPraiseLayout.setVisibility(View.VISIBLE);
        }
        mPraiseUsersLayout.removeAllViews();
        for (int i = 0; i < praiseList.size(); i++) {
            if (i > 7) {
                IconicFontTextView praiseCountView = new IconicFontTextView(CaptionDetailActivity.this);
                praiseCountView.setTextColor(Color.parseColor("#dcdce0"));
                praiseCountView.setTextSize(25);
                //TextView praiseCountView = (TextView) LayoutInflater.from(getApplicationContext()).inflate(R.layout.praise_count_view, null, false);
//                praiseCountView.setText(mPhotoTopic.getPraiseNum() + "");
                praiseCountView.setText("{xk-praise-more}");
                praiseCountView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PraiseListActivity.launch(CaptionDetailActivity.this, mPhotoTopic.getpGuid(), String.valueOf(mPhotoTopic.getAccount()), 1);
                    }
                });
                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(AppUtility.dip2px(26), AppUtility.dip2px(26));
                params1.setMargins(0, 0, 0, 0);
                mPraiseUsersLayout.addView(praiseCountView, params1);
                break;
            }
            PraiseItem item = praiseList.get(i);
            SimpleDraweeView avatarView = (SimpleDraweeView) mInflater.inflate(R.layout.item_caption_praise_user, null, false);
            avatarView.setTag(item);
            Uri avatarUri = Uri.parse(QiniuHelper.getThumbnail96Url(item.getAvatar()));
            if (!DEFAULT_AVATAR_KEY.equals(item.getAvatar())) {
                avatarView.setImageURI(avatarUri);
            }

            avatarView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PraiseItem praiseItem = (PraiseItem) v.getTag();
                    if (mAccount.equals(praiseItem.getAccount())) {
                        if (MainActivity.currentTabIndex != 3) {
                            MainActivity.launch(CaptionDetailActivity.this, 1);
                        }
                    } else {
                        if (praiseItem.isAdmin()) {
                            UserProfileActivity.launch(CaptionDetailActivity.this, String.valueOf(praiseItem.getAccount()), 1, 1);
                        } else {
                            UserProfileActivity.launch(CaptionDetailActivity.this, String.valueOf(praiseItem.getAccount()), 1, 0);
                        }
                    }
//                    UserProfileActivity.launch(CaptionDetailActivity.this, String.valueOf(v.getTag()));
                }
            });
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(AppUtility.dip2px(26), AppUtility.dip2px(26));
            params.setMargins(0, 0, AppUtility.dip2px(7), 0);
            mPraiseUsersLayout.addView(avatarView, params);
        }
    }

    // ------------------------- 举报 -----------------------------

    @OnClick(R.id.tv_right)
    void onRightClick() {
        addReadingRecord(mPGuid, mAuthor);
        ShareCaptionActivity.launch(CaptionDetailActivity.this, mPhotoTopic, 2);
//        final String[] stringItems;
//        if (!TextUtils.isEmpty(mPhotoTopic.getWebUrl())) {
//            stringItems = new String[]{"分享"};
//
//        } else {
//            stringItems = new String[]{"分享", "举报"};
//        }
//        dialog = new ActionSheetDialog(this, stringItems, null);
//        dialog.isTitleShow(false);
//        dialog.itemTextColor(Color.parseColor("#363640"))
//                .itemTextSize(15.5f)
//                .itemHeight(36f)
//                .dividerColor(Color.parseColor("#d8d9e2"))
//                .show();
//        dialog.cancelText(Color.parseColor("#363640"))
//                .cancelTextSize(15.5f)
//                .show();
//        //弹出框透明度设为1
//        dialog.titleBgColor(Color.parseColor("#ffffff"));
//        dialog.lvBgColor(Color.parseColor("#ffffff"));
//        dialog.setOnOperItemClickL(new OnOperItemClickL() {
//            @Override
//            public void onOperItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                switch (i) {
//                    case 0://分享
//                        dialog.dismiss();
//                        addReadingRecord(mPGuid, mAuthor);
//                        ShareCaptionActivity.launch(CaptionDetailActivity.this, mPhotoTopic, 2);
//                        break;
//                    case 1://举报
//                        dialog.dismiss();
//                        report();
//                        break;
//                }
//
//            }
//        });
    }

    // ------------------------- 显示图片 -----------------------------

    /**
     * @param imageKeys
     * @功能描述 : 初始化显示图说图片集合操作
     */
    private void initSwipeView(final List<String> imageKeys) {
        final ImageFlingAdapter mFlingAdapter = new ImageFlingAdapter(this, imageKeys);
        final ArrayList<String> imageList = new ArrayList<>();
        imageList.addAll(imageKeys);
        mSwipeView.setAdapter(mFlingAdapter);
        mSwipeView.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                imageKeys.remove(0);
                mFlingAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                imageKeys.add(imageKeys.size(), (String) dataObject);
                mFlingAdapter.notifyDataSetChanged();
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                imageKeys.add(imageKeys.size(), (String) dataObject);
                mFlingAdapter.notifyDataSetChanged();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {

            }

            @Override
            public void onScroll(float scrollProgressPercent) {

            }
        });

        mSwipeView.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                addReadingRecord(mPGuid, mAuthor);
                int position = imageList.indexOf(dataObject);
                ArrayList<String> imageUrls = new ArrayList<>();
                for (String imageKey : imageList) {
                    imageUrls.add(QiniuHelper.getOriginalWithKey(imageKey));
                }
                Intent intent = new Intent(mContext, PhotoPagerActivity.class);
                intent.putExtra(PhotoPagerActivity.EXTRA_CURRENT_ITEM, position);
                intent.putExtra(PhotoPagerActivity.EXTRA_PHOTOS, imageUrls);
                intent.putExtra(PhotoPagerActivity.EXTRA_TYPE, 1);
                intent.putExtra(PhotoPagerActivity.EXTRA_USERNICK, mPhotoTopic.getNick());
                startActivity(intent);
            }
        });
    }

    /**
     * @param view
     * @param position
     * @功能描述 : 处理图说评论回复
     */
    @Override
    public void onItemReplyClick(View view, int position) {
        Logger.i("点击回复" + position);
        //计算被点击的评论所处的位置
//        mCommentLV.setSelection(position + 2);
//      mCommentExLV.setDivider(null);
        CommentItem commentItem = mCommentItemList.get(position);
        replyPosition = position;
        Logger.d(commentItem.getGuid() + " : " + commentItem.getReplyGuid());
        prepareAddComment(commentItem, true);
        measure(position + 2);

    }

    private int mSelectCircleItemH;
    private int mSelectCommentItemBottom;
    private int mCommentPosition;
    int softkeyboardHeight = 550;
    int inputBoxHeight = AppUtility.dip2px(48);
    int screenHeight = AppUtility.getScreenHeightWithStatusBar();
    int statusBarH = AppUtility.getStatusBarHeight();

    private void measure(int mCirclePosition) {
        if (mCommentLV != null) {
            int firstPosition = mCommentLV.getFirstVisiblePosition();
            View selectCircleItem = mCommentLV.getChildAt(mCirclePosition - firstPosition);
            Log.i("TAG", "-----当前位置------>" + (mCirclePosition - firstPosition));
            if (selectCircleItem != null) {
                mSelectCircleItemH = selectCircleItem.getHeight();
            }
            Log.i("TAG", "-----选中这一项的高度------>" + mSelectCircleItemH);
            if (mEnterLayout != null) {
                softkeyboardHeight = mEnterLayout.softkeyboardHeight;
            }
            if (softkeyboardHeight == 0) {
                softkeyboardHeight = 550;
            }
            Log.i("TAG", "-----选中这一项的高度------>" + mSelectCircleItemH + "-----软键盘的高度------>" + softkeyboardHeight);
            int listviewOffset;
            if (mToolbar.getVisibility() == View.GONE) {
                listviewOffset = screenHeight - mSelectCircleItemH - softkeyboardHeight - inputBoxHeight - inputBoxHeight - statusBarH + mToolbar.getHeight();
            } else {
                listviewOffset = screenHeight - mSelectCircleItemH - softkeyboardHeight - inputBoxHeight - inputBoxHeight - statusBarH;
            }

            mCommentLV.setSelectionFromTop(mCirclePosition, listviewOffset);
            //mCommentLV.scrollTo(0, listviewOffset);
            // if (commentType == ICircleViewUpdate.TYPE_REPLY_COMMENT) {//回复评论的情况
//            AppNoScrollerListView commentLv = (AppNoScrollerListView) selectCircleItem.findViewById(R.id.commentList);
//            if (commentLv != null) {
//                int firstCommentPosition = commentLv.getFirstVisiblePosition();
//                //找到要回复的评论view,计算出该view距离所属动态底部的距离
//                //View selectCommentItem = commentLv.getChildAt(mCommentPosition - firstCommentPosition);
//                View selectCommentItem = mCommentLV.getChildAt(mCirclePosition);
//                if (selectCommentItem != null) {
//                    mSelectCommentItemBottom = 0;
//                    View parentView = selectCommentItem;
//                    do {
//                        int subItemBottom = parentView.getBottom();
//                        parentView = (View) parentView.getParent();

//                        if (parentView != null) {
//                            mSelectCommentItemBottom += (parentView.getHeight() - subItemBottom);
//                        }
//                    } while (parentView != null && parentView != selectCircleItem);
//                }
//            }
            //  }
        }
    }


    /**
     * @param view
     * @param position
     * @功能描述 : 处理评论点赞
     **/
    @Override
    public void onItemPraiseClick(View view, final int position) {
        CommentItem commentItem = mCommentItemList.get(position);

        final String isPraiseStr = commentItem.isPraise() ? "0" : "1";
        String account = Security.aesEncrypt(String.valueOf(currentUser.getAccount())); // 行账号
        String pGuid = Security.aesEncrypt(mPhotoTopic.getpGuid()); // 图说主键
        String toAccount = Security.aesEncrypt(String.valueOf(mPhotoTopic.getAccount())); // 图说作者行账号
        String pcGuid = Security.aesEncrypt(commentItem.getGuid()); // 评论主键
        final String isPraise = Security.aesEncrypt(isPraiseStr); // 1-点赞，0-取消赞

        ApiModule.apiService().photoTopicCommentPraiseAdd(account, pGuid, pcGuid, toAccount, isPraise).enqueue(new Callback<XKRepo>() {


            @Override
            public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                XKRepo xkRepo = response.body();
                if (xkRepo.isSuccess()) {
                    if (isPraiseStr.equals("0")) {
                        mCommentItemList.get(position).setIsPraise(false);
                    } else {
                        mCommentItemList.get(position).setIsPraise(true);
                    }
                    mCommentAdapter.notifyDataSetChanged();
                } else {
                    ProgressHUD.showInfoMessage(mContext, xkRepo.getMsg());
                }
            }

            @Override
            public void onFailure(Call<XKRepo> call, Throwable t) {
                ProgressHUD.showInfoMessage(mContext, getString(R.string.common_network_error));
            }
        });
    }

    @Override
    public void onItemReplyToClick(View view, int groupPosition, int childPosition) {
        CommentItem commentItem = mCommentItemList.get(groupPosition);
        CommentItem subCommentItem = commentItem.getSubCommentItems().get(childPosition);

        // 判断是否是当前登录用户自己针对评论的回复
        if (!subCommentItem.getReviewUid().equals(String.valueOf(currentUser.getAccount()))) {
            replyPosition = groupPosition;
            prepareAddComment(subCommentItem, true);
            measure(groupPosition + 2);
        }
    }


    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        mPage = 1;
        loadCommentData();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        mPage++;
        loadCommentData();
    }

    //长按显示复制的弹框
    private void normalListDialogNoTitle(final String content) {
        final ArrayList<DialogMenuItem> testItems = new ArrayList<>();
//        testItems.add(new DialogMenuItem("复制", R.mipmap.ic_winstyle_copy));
//        testItems.add(new DialogMenuItem("删除", R.mipmap.ic_winstyle_delete));
        testItems.add(new DialogMenuItem("复制", R.mipmap.ic_winstyle_copy));
        final NormalListDialog dialog = new NormalListDialog(CaptionDetailActivity.this, testItems);
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
            clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        }
        // Creates a new text clip to put on the clipboard
        ClipData clip = ClipData.newPlainText("simple text",
                content);
        // Set the clipboard's primary clip.
        clipboard.setPrimaryClip(clip);
    }

    public boolean isListViewReachTopEdge(final ExpandableListView listView) {
        boolean result = false;
        if (listView.getFirstVisiblePosition() == 0) {
            final View topChildView = listView.getChildAt(0);
            result = topChildView.getTop() == 0;

        }
        return result;
    }

    public int getScrollY() {
        View c = mCommentLV.getChildAt(0);
        if (c == null) {
            return 0;
        }
        int firstVisiblePosition = mCommentLV.getFirstVisiblePosition();
        int top = c.getTop();
        return -top + firstVisiblePosition * c.getHeight();
    }


    /**
     * @param pGuid  图说GUID
     * @param author 图说作者行账号
     * @功能描述 : 获取图说详情
     */
    private void notifyData(String pGuid, String author) {
        User user = AccountInfo.getInstance().loadAccount();
        String account = Security.aesEncrypt(String.valueOf(user.getAccount()));
        String sPGuid = Security.aesEncrypt(pGuid);
        String sAuthor = Security.aesEncrypt(author);
        // Log.i("TAG", "---account---->" + account + "----sPGuid---->" + sPGuid + "---sAuthor----->" + sAuthor);
        ApiModule.apiService().PhotoTopicDetailNoC2_1(account, sPGuid, sAuthor).enqueue(new Callback<PhotoTopicRepo>() {


            @Override
            public void onResponse(Call<PhotoTopicRepo> call, Response<PhotoTopicRepo> response) {
                PhotoTopicRepo photoTopicRepo = response.body();
                String msg = photoTopicRepo.getMsg();
                if (photoTopicRepo.isSuccess()) {
                    mPhotoTopic = photoTopicRepo.getPhotoTopic();
                    //initHeader();
                    //initViews();
                    //loadCommentData();
                    //mEnterLayout = new EnterEmojiLayout(this, onSendClicked, EnterLayout.Type.TextOnly, EnterEmojiLayout.EmojiType.SmallOnly);
                    if (mEnterLayout == null)
                        mEnterLayout = new EnterEmojiLayout(CaptionDetailActivity.this, onSendClicked, EnterLayout.Type.TextOnly, EnterEmojiLayout.EmojiType.SmallOnly);
                    String editContent = mEnterLayout.content.getText().toString();
                    if (mPopComment == 1) {
                        if (mCommentItem != null) {
                            prepareAddComment(mCommentItem, true);
                        } else {
                            prepareAddComment(mPhotoTopic, true);
                        }
                        mPopComment = 0;
                    }
                    if (!TextUtils.isEmpty(editContent)) {
                        mEnterLayout.content.setText(editContent);
                        mEnterLayout.content.setSelection(editContent.length());
                    }

                    //loadCommentData();
                } else {
                    if ("暂无数据".equals(msg)) {
                        final NormalDialog dialog = new NormalDialog(mContext);
                        dialog.isTitleShow(false)
                                .bgColor(Color.parseColor("#ffffff"))
                                .cornerRadius(5)
                                .content("该图说已被删除")
                                .btnNum(1)
                                .btnText("确定")
                                .contentGravity(Gravity.CENTER)
                                .contentTextColor(Color.parseColor("#33333d"))
                                .dividerColor(Color.parseColor("#dcdce4"))
                                .btnTextSize(15.5f, 15.5f)
                                .btnTextColor(Color.parseColor("#ff2814"), Color.parseColor("#0077fe"))
                                .widthScale(0.85f)
                                .show();
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.setOnBtnClickL(new OnBtnClickL() {
                            @Override
                            public void onBtnClick() {
                                Intent intent = new Intent();
                                intent.putExtra("position", getIntent().getIntExtra("position", -100));
                                setResult(200, intent);
                                finish();
                                dialog.dismiss();
                            }
                        });
                    } else
                        ProgressHUD.showInfoMessage(mContext, photoTopicRepo.getMsg());
                }
                //mCommentLV.setSelection(0);
            }

            @Override
            public void onFailure(Call<PhotoTopicRepo> call, Throwable t) {
                ProgressHUD.showInfoMessage(mContext, getString(R.string.common_network_error));
            }

        });
    }

    /**
     * 解决：点击空白区域，自动隐藏软键盘
     *
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            // 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体按键会移动焦点）
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                hideSoftInput(v.getWindowToken());
                mCommonEnterRoot.setVisibility(View.GONE);
                mBottomLayout.setVisibility(View.VISIBLE);
                return true;
            }
        }
        return super.dispatchTouchEvent(ev);
    }
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//            if (mCommonEnterRoot != null && mCommonEnterRoot.getVisibility() == View.VISIBLE) {
//                mCommonEnterRoot.setVisibility(View.GONE);
//                return true;
//            }
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
     *
     * @param v
     * @param event
     * @return
     */
    public boolean isShouldHideInput(View v, MotionEvent event) {

        if (v != null && (v instanceof EditText)) {

            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (event.getX() > (left - AppUtility.dip2px(70)) && event.getX() < (right + AppUtility.dip2px(70))
                    && event.getY() > top - AppUtility.dip2px(5) && event.getY() < bottom + AppUtility.dip2px(210)) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return false;
    }

}

package com.ruihai.xingka.ui.trackway;

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
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
import com.ruihai.xingka.api.model.PraiseItem;
import com.ruihai.xingka.api.model.ReportInfo;
import com.ruihai.xingka.api.model.ReportType;
import com.ruihai.xingka.api.model.TrackwayInfo;
import com.ruihai.xingka.api.model.TrackwayInfoRepo;
import com.ruihai.xingka.api.model.User;
import com.ruihai.xingka.api.model.XKRepo;
import com.ruihai.xingka.ui.BaseActivity;
import com.ruihai.xingka.ui.MainActivity;
import com.ruihai.xingka.ui.caption.PraiseListActivity;
import com.ruihai.xingka.ui.caption.adapter.CommentListAdapter;
import com.ruihai.xingka.ui.common.PhotoPagerActivity;
import com.ruihai.xingka.ui.common.enter.EmojiFilter;
import com.ruihai.xingka.ui.common.enter.EmojiUtils;
import com.ruihai.xingka.ui.common.enter.EnterEmojiLayout;
import com.ruihai.xingka.ui.common.enter.EnterLayout;
import com.ruihai.xingka.ui.login.LoginActivity;
import com.ruihai.xingka.ui.mine.UserProfileActivity;
import com.ruihai.xingka.ui.trackway.fragment.TrackwayFragment;
import com.ruihai.xingka.utils.AppUtility;
import com.ruihai.xingka.utils.QiniuHelper;
import com.ruihai.xingka.utils.Security;
import com.ruihai.xingka.utils.glide.GlideHelper;
import com.ruihai.xingka.widget.ProgressHUD;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bingoogolapple.bgabanner.BGAViewPager;
import de.greenrobot.event.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 旅拼详情
 * Created by mac on 16/5/7.
 */
public class TrackwayDetailActivity extends BaseActivity implements CommentListAdapter.OnItemClickListener, PullToRefreshBase.OnRefreshListener2 {

    public static void launch(Activity from, String tGuid, String author) {
        Intent intent = new Intent(from, TrackwayDetailActivity.class);
        intent.putExtra(KEY_TRACKWAY_GUID, tGuid);
        intent.putExtra(KEY_AUTHOR_ACCOUNT, author);
        from.startActivity(intent);
    }

    public final static String KEY_TRACKWAY_GUID = "KEY_TGUID";
    public final static String KEY_AUTHOR_ACCOUNT = "KEY_AUTHOR";
    public static final String DEFAULT_AVATAR_KEY = "00000000-0000-0000-0000-000000000000";
    private static final int DEFAULT_PER_PAGE = 20;

    //    @BindView(R.id.tv_back)
    IconicFontTextView mBack;
    //    @BindView(R.id.tv_title)
    TextView mTitleView;
    //    @BindView(R.id.tv_right)
    IconicFontTextView mRightView;
    // 照片
//    @BindView(R.id.img_viewPager)
    BGAViewPager mViewPager;
    //照片数量
//    @BindView(R.id.num_text)
    TextView mNum;
    @BindView(R.id.rl_toolbar1)
    RelativeLayout mToolbar;
    //----用户信息---------
    //    @BindView(R.id.sdv_avatar)
    SimpleDraweeView mAvatar;
    //    @BindView(R.id.tv_username)
    TextView mUserName;
    //    @BindView(R.id.tv_officalaccount)
    TextView isOffcalAccount;
    //    @BindView(R.id.tv_aftertime)
    TextView afterTime;
    //    @BindView(R.id.btn_add_follow)
    IconicFontTextView mAddFollowButton;
    //    @BindView(R.id.describe_content)
    TextView describeContent; //旅拼描述
    //    @BindView(R.id.btn_content_more)
    ImageView hasMoreBtn; //显示完整旅拼描述内容
    //    @BindView(R.id.trackway_time)
    TextView trackwayTime; //时间
    //    @BindView(R.id.trackway_line)
    TextView trackwayLine; //路线
    //    @BindView(R.id.cost_text)
    TextView cost; //费用
    //    @BindView(R.id.number_people_text)
    TextView numberPeople; //人数
    //    @BindView(R.id.partner_require_text)
    TextView partnewRequire; //旅伴要求

    //点赞头像
//    @BindView(R.id.layout_praise)
    LinearLayout praiseShowLayout;
    //    @BindView(R.id.layout_praise_users)
    LinearLayout praiseUsersLayout;
    //    @BindView(R.id.text_praise_count)
    TextView praiseCountText;
    //更多评论
//    @BindView(R.id.tv_more)
    TextView moreComment;


    @BindView(R.id.elv_comment_list)
    PullToRefreshListView mRefreshListview;
    //软键盘
    @BindView(R.id.commonEnterRoot)
    FrameLayout mCommonEnterRoot;

    //操作按钮
    @BindView(R.id.bottom_layout)
    LinearLayout mBottomLayout;
    @BindView(R.id.ll_praise)
    LinearLayout praiseLayout;
    @BindView(R.id.ifb_praise)
    IconicFontTextView praiseButton;
    @BindView(R.id.ll_collect)
    LinearLayout collectLayout;
    @BindView(R.id.ifb_collect)
    IconicFontTextView collectButton;
    //评论
    @BindView(R.id.ll_comment)
    LinearLayout commentLayout;
    @BindView(R.id.ifb_comment)
    IconicFontTextView commentButtom;
    //分享
    @BindView(R.id.ifb_share)
    IconicFontTextView shareButton;
    @BindView(R.id.ll_share)
    LinearLayout shareLayout;

    private ImageView imageView;
    private PagerAdapter mPagerAdapter;
    private ArrayList<View> viewArrayList = new ArrayList<View>();

    // 评论的position
    private int replyPosition = 0;
    private int mPopComment = 0;

    private String mTGuid;
    private String mAuthor;
    private String mAccount;
    private int mPage = 1;//页数
    private int mMaxPage = 0;//最大页数
    private int mPerPage = DEFAULT_PER_PAGE;//每页数量
    private boolean mLoadMore;
    private CommentItem mCommentItem;
    private boolean hasMore;
    private float mLastY = 0;

    private ClipboardManager clipboard;//粘贴板
    private EnterEmojiLayout mEnterLayout;
    private boolean isDown;
    private ListView mCommentLV;
    private List<CommentItem> mCommentItemList = new ArrayList<>();
    private CommentListAdapter mCommentAdapter;

    private View mListHeaderView;

    private TrackwayInfo mTrackwayInfo;
    private ActionSheetDialog dialog;
    private List<ReportType> reportInfoList = new ArrayList<>();
    private boolean hasMoreContentShow = true;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            int lineCount = msg.what;
            if (lineCount > 6) {
                hasMoreBtn.setVisibility(View.VISIBLE);
                describeContent.setMaxLines(6);
                hasMoreBtn.setImageResource(R.mipmap.icon_content_down);
            } else {
                hasMoreBtn.setVisibility(View.GONE);
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trackway_detial);
        ButterKnife.bind(this);

        if (AccountInfo.getInstance().getReportType() == null) {
            initReportType(); //获取举报类型
        }

//        mTitleView.setVisibility(View.INVISIBLE);
//        mRightView.setText("{xk-report}");
//        mRightView.setVisibility(View.VISIBLE);
        mAccount = String.valueOf(AccountInfo.getInstance().loadAccount().getAccount());
//        Log.e("本账户账号", mAccount);
        mTrackwayInfo = getIntent().getParcelableExtra(TrackwayFragment.KEY_TRACKWAY_ITEM);
        mPopComment = getIntent().getIntExtra(TrackwayFragment.KEY_POP_COMMENT, 0);
        mCommentItem = getIntent().getParcelableExtra(TrackwayFragment.KEY_COMMENT_ITEM);
        mTGuid = getIntent().getStringExtra(KEY_TRACKWAY_GUID);
        mAuthor = getIntent().getStringExtra(KEY_AUTHOR_ACCOUNT);
        mCommentLV = mRefreshListview.getRefreshableView();
        mRefreshListview.setMode(PullToRefreshBase.Mode.BOTH);
        if (!TextUtils.isEmpty(mTGuid) && !TextUtils.isEmpty(mAuthor)) {
            addReadingRecord(mTGuid, mAuthor);
            getData(mTGuid, mAuthor);
        } else if (mTrackwayInfo != null) {
            mTGuid = mTrackwayInfo.gettGuid();
            mAuthor = String.valueOf(mTrackwayInfo.getAccount());
            addReadingRecord(mTGuid, mAuthor);
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
                            mCommentLV.setMinimumHeight(AppUtility.getScreenHeight());
                            mToolbar.setVisibility(View.GONE);
                        } else {
                            mBottomLayout.setVisibility(View.VISIBLE);
                            mCommentLV.setMinimumHeight(AppUtility.getScreenHeight() - AppUtility.dip2px(96));
                            //记录滑动的距离控制等不Toolbar的出现
                            int total = 0;
                            total += getScrollY();
                            if (total > AppUtility.dip2px(250)) {
                                mToolbar.setVisibility(View.VISIBLE);
                            } else {
                                mToolbar.setVisibility(View.GONE);
                            }
                        }
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0 && getScrollY() == 0) {
                    mRefreshListview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
//                    mToolbar.setVisibility(View.VISIBLE);
                    mBottomLayout.setVisibility(View.VISIBLE);
                } else if (hasMore) {
                    mRefreshListview.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
                } else {
                    mRefreshListview.setMode(PullToRefreshBase.Mode.DISABLED);
                }


            }
        });
        mCommonEnterRoot.setTag(1);

    }


    private void initViews() {
        //评论
        moreComment = (TextView) mListHeaderView.findViewById(R.id.tv_more);
        mCommentAdapter = new CommentListAdapter(mContext, mCommentItemList, mAuthor, mAccount, moreComment, 2);
        mCommentAdapter.setOnItemClickListener(this);
        mRefreshListview.setOnRefreshListener(this);
        //去掉评论回复列表的分割线
        mCommentLV.setDivider(null);
        mCommentLV.setAdapter(mCommentAdapter);
        //mCommentExLV.setGroupIndicator(null);
        mCommentLV.setSelector(new ColorDrawable(Color.WHITE));

        mEnterLayout = new EnterEmojiLayout(this, onSendClicked, EnterLayout.Type.TextOnly, EnterEmojiLayout.EmojiType.SmallOnly);

        if (mPopComment == 1) {
            if (mCommentItem != null) {
                prepareAddComment(mCommentItem, true);
            } else {
                prepareAddComment(mTrackwayInfo, true);
            }
        } else {

        }
    }

    private void initHeader() {
        if (mListHeaderView == null) {
            mListHeaderView = mInflater.inflate(R.layout.trackway_userinfo, null, false);
            mCommentLV.addHeaderView(mListHeaderView);
        }
        //------------   顶部toolbar  -------------
        mBack = (IconicFontTextView) mListHeaderView.findViewById(R.id.tv_back);
        mTitleView = (TextView) mListHeaderView.findViewById(R.id.tv_title);
        mRightView = (IconicFontTextView) mListHeaderView.findViewById(R.id.tv_right);

        mTitleView.setVisibility(View.INVISIBLE);
        mRightView.setText("{xk-report}");
        mRightView.setVisibility(View.VISIBLE);
        //返回
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //分享,举报或删除
        mRightView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addReadingRecord(mTGuid, mAuthor);
                ShareTrackwayActivity.launch(TrackwayDetailActivity.this, mTrackwayInfo, 2);
//                final int mAccount = AccountInfo.getInstance().loadAccount().getAccount();
//                final String[] stringItems;
//                if (!TextUtils.isEmpty(mTrackwayInfo.getWebUrl())) {
//                    stringItems = new String[]{"分享"};
//                } else {
//                    if (mTrackwayInfo.getAccount() == mAccount) {
//                        stringItems = new String[]{"分享", "删除"};
//                    } else {
//                        stringItems = new String[]{"分享", "举报"};
//                    }
//                }
//                dialog = new ActionSheetDialog(mContext, stringItems, null);
//                dialog.isTitleShow(false);
//                dialog.itemTextColor(Color.parseColor("#363640"))
//                        .itemTextSize(15.5f)
//                        .itemHeight(36f)
//                        .dividerColor(Color.parseColor("#d8d9e2"))
//                        .show();
//                dialog.cancelText(Color.parseColor("#363640"))
//                        .cancelTextSize(15.5f)
//                        .show();
//                //弹出框透明度设为1
//                dialog.titleBgColor(Color.parseColor("#ffffff"));
//                dialog.lvBgColor(Color.parseColor("#ffffff"));
//                dialog.setOnOperItemClickL(new OnOperItemClickL() {
//                    @Override
//                    public void onOperItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
//                        switch (position) {
//                            case 0://分享
//                                dialog.dismiss();
//                                addReadingRecord(mTGuid, mAuthor);
//                                ShareTrackwayActivity.launch(TrackwayDetailActivity.this, mTrackwayInfo, 2);
//                                break;
//                            case 1://举报
//                                dialog.dismiss();
////                                report();
//                                if (mTrackwayInfo.getAccount() == mAccount) {
//                                    getDeleteTravelTogether(position); //删除旅拼
//                                    return;
//                                } else {
//                                    report(); //举报
////                                    initReportType();
//                                }
//                                break;
//                        }
//                    }
//                });
            }
        });


        //旅拼照片
        mViewPager = (BGAViewPager) mListHeaderView.findViewById(R.id.img_viewPager);
        mNum = (TextView) mListHeaderView.findViewById(R.id.num_text);
        if (mTrackwayInfo.getImageList().size() > 0) {
            //初始化引导图片列表
            for (int i = 0; i < mTrackwayInfo.getImageList().size(); i++) {
                //查找布局文件用LayoutInflater.inflate
                LayoutInflater inflater = getLayoutInflater();
                View view1 = inflater.inflate(R.layout.viewpage_item, null);
                imageView = (ImageView) view1.findViewById(R.id.viewpage_image);
                //获取图片高度
                int mScreenWidth = AppUtility.getScreenWidth();
                int mScreenHeight = AppUtility.getScreenHeight();
                ViewGroup.LayoutParams lp = imageView.getLayoutParams();
                lp.width = mScreenWidth;
                lp.height = mScreenWidth * 9 / 16;
                imageView.setLayoutParams(lp);

                String image = mTrackwayInfo.getImageList().get(i).imgSrc;
                GlideHelper.loadTopicCoverWithKey(image, imageView, null);

                //将view装入数组
                viewArrayList.add(view1);

                //数据适配器
                mPagerAdapter = new PagerAdapter() {

                    @Override
                    //获取当前窗体界面数
                    public int getCount() {
                        // TODO Auto-generated method stub
                        return viewArrayList.size();
                    }

                    @Override
                    //断是否由对象生成界面
                    public boolean isViewFromObject(View arg0, Object arg1) {
                        // TODO Auto-generated method stub
                        return arg0 == arg1;
                    }

                    //是从ViewGroup中移出当前View
                    public void destroyItem(View arg0, int arg1, Object arg2) {
                        ((ViewPager) arg0).removeView(viewArrayList.get(arg1));
                    }

                    //返回一个对象，这个对象表明了PagerAdapter适配器选择哪个对象放在当前的ViewPager中
                    public Object instantiateItem(View arg0, int arg1) {
                        ((ViewPager) arg0).addView(viewArrayList.get(arg1));
                        return viewArrayList.get(arg1);
                    }
                };
                //顶部图片滑动
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addReadingRecord(mTrackwayInfo.gettGuid(), String.valueOf(mTrackwayInfo.getAccount()));
                        // 获得图片原本所在图片集的排序位置
                        ArrayList<String> imageUrls = new ArrayList<>();
                        for (ImageItem image : mTrackwayInfo.getImageList()) {
                            imageUrls.add(QiniuHelper.getOriginalWithKey(image.imgSrc));
                        }
                        Intent intent = new Intent(TrackwayDetailActivity.this, PhotoPagerActivity.class);
                        intent.putExtra(PhotoPagerActivity.EXTRA_CURRENT_ITEM, mViewPager.getCurrentItem());
                        intent.putExtra(PhotoPagerActivity.EXTRA_PHOTOS, imageUrls);
                        intent.putExtra(PhotoPagerActivity.EXTRA_USERNICK, mTrackwayInfo.getNick());
                        intent.putExtra(PhotoPagerActivity.EXTRA_TYPE, 1);
                        startActivity(intent);
                    }
                });

                //默认图片显示数字
                mNum.setText(getString(R.string.image_index, 1, mTrackwayInfo.getImageList().size()));

                //图片滑动的监听
                mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        //滑动后图片显示数字
                        mNum.setText(getString(R.string.image_index, position + 1, mTrackwayInfo.getImageList().size()));
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });

            }

            //绑定适配器
            mViewPager.setAdapter(mPagerAdapter);
        }

        // 头像
        mAvatar = (SimpleDraweeView) mListHeaderView.findViewById(R.id.sdv_avatar);
        Uri avatarUri = Uri.parse(QiniuHelper.getThumbnail96Url(mTrackwayInfo.getAvatar()));
        if (!DEFAULT_AVATAR_KEY.equals(mTrackwayInfo.getAvatar())) {
            mAvatar.setImageURI(avatarUri);
        }
        mAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAccount.equals(String.valueOf(mTrackwayInfo.getAccount()))) {
                    if (MainActivity.currentTabIndex != 3) {
                        MainActivity.launch(TrackwayDetailActivity.this, 1);
                    }
                } else {
                    UserProfileActivity.launch(TrackwayDetailActivity.this, String.valueOf(mTrackwayInfo.getAccount()), 2, 0);
                }
            }
        });
        //性别
        ImageView mSex = (ImageView) mListHeaderView.findViewById(R.id.iv_sex);
        if (mTrackwayInfo.getSex() == 1) {
            mSex.setImageResource(R.mipmap.icon_boy);
        } else if (mTrackwayInfo.getSex() == 2) {
            mSex.setImageResource(R.mipmap.icon_girl);
        } else {
            mSex.setVisibility(View.GONE);
        }

        // 用户名，判断备注是否为空，如果为空，显示nick，如果不为空，则显示remark。
        mUserName = (TextView) mListHeaderView.findViewById(R.id.tv_username);
        if (!TextUtils.isEmpty(mTrackwayInfo.getRemark())) {
            mUserName.setText(mTrackwayInfo.getRemark());
        } else if (!TextUtils.isEmpty(mTrackwayInfo.getNick())) {
            // mUserName.setText(mTrackwayInfo.getNick());
            mUserName.setText(EmojiUtils.fromStringToEmoji1(mTrackwayInfo.getNick(), mContext));
        } else { // 显示行咖号
            mUserName.setText(String.valueOf(mTrackwayInfo.getAccount()));
        }
        isOffcalAccount = (TextView) mListHeaderView.findViewById(R.id.tv_officalaccount);
        if (mTrackwayInfo.isOffical()) {
            isOffcalAccount.setVisibility(View.VISIBLE);
        }

        // 发布时间
        afterTime = (TextView) mListHeaderView.findViewById(R.id.tv_aftertime);
        String datetime = mTrackwayInfo.getAddTime().substring(6, 19);
        long timestamp = Long.valueOf(datetime);
        afterTime.setText(Global.dayToNow(timestamp));
        // 加关注
        mAddFollowButton = (IconicFontTextView) mListHeaderView.findViewById(R.id.btn_add_follow);
        if (mTrackwayInfo.isFriend()) {
            mAddFollowButton.setSelected(true);
            mAddFollowButton.setText("已关注");
            mAddFollowButton.setEnabled(false);
        } else {
            if (currentUser != null) {
                if (mTrackwayInfo.getAccount() == currentUser.getAccount()) {
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
                                            }

        );
        // 旅拼描述
        describeContent = (TextView) mListHeaderView.findViewById(R.id.describe_content);
        hasMoreBtn = (ImageView) mListHeaderView.findViewById(R.id.btn_content_more);
        if (!TextUtils.isEmpty(mTrackwayInfo.getContent())) {
            describeContent.setVisibility(View.VISIBLE);
            describeContent.setText(EmojiUtils.fromStringToEmoji1(mTrackwayInfo.getContent(), this));
            describeContent.post(new Runnable() {

                @Override
                public void run() {
                    int lineCount = describeContent.getLineCount();
                    mHandler.sendEmptyMessage(lineCount);
                }
            });
        } else {
            describeContent.setVisibility(View.GONE);
        }

        hasMoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasMoreContentShow) {
                    describeContent.setMaxLines(describeContent.getLineCount());
                    hasMoreBtn.setImageResource(R.mipmap.icon_content_up);
                    hasMoreContentShow = false;
                } else {
                    describeContent.setMaxLines(6);
                    hasMoreBtn.setImageResource(R.mipmap.icon_content_down);
                    hasMoreContentShow = true;
                }
            }
        });

        describeContent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                normalListDialogNoTitle(mTrackwayInfo.getContent());
                return false;
            }
        });

        //时间
        trackwayTime = (TextView) mListHeaderView.findViewById(R.id.trackway_time);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String beginTime = mTrackwayInfo.getBeginDate().substring(6, 19);
        String endTime = mTrackwayInfo.getEndDate().substring(6, 19);
        String date = dateFormat.format(new Long(beginTime));
        //计算天数
        Date date0 = null;
        Date date1 = null;
        try {
            date0 = dateFormat.parse(date);
            date1 = dateFormat.parse(dateFormat.format(new Long(endTime)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal0 = Calendar.getInstance();
        cal0.setTime(date0);
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        long time0 = cal0.getTimeInMillis();
        long time1 = cal1.getTimeInMillis();
        Long total = (time1 - time0) / (1000 * 3600 * 24) + 1;
        trackwayTime.setText("出发日期:" + date + " 全程" + total + "天");

        //线路
        trackwayLine = (TextView) mListHeaderView.findViewById(R.id.trackway_line);
        String address = "";
        for (int i = 0; i < mTrackwayInfo.getLineInfoList().size(); i++) {
            if (i == mTrackwayInfo.getLineInfoList().size() - 1) {
                address += mTrackwayInfo.getLineInfoList().get(i).getAddress();
            } else {
                address += mTrackwayInfo.getLineInfoList().get(i).getAddress() + "-";
            }
        }
        trackwayLine.setText(address);
        //费用
        cost = (TextView) mListHeaderView.findViewById(R.id.cost_text);
        cost.setText("费用:" + mTrackwayInfo.getCostType());
        //人数
        numberPeople = (TextView) mListHeaderView.findViewById(R.id.number_people_text);
        if (mTrackwayInfo.getPersonNum().equals("0")) {
            numberPeople.setText("人数: 不限");
        } else {
            numberPeople.setText("人数:" + mTrackwayInfo.getPersonNum());
        }
        //旅伴要求
        partnewRequire = (TextView) mListHeaderView.findViewById(R.id.partner_require_text);
        partnewRequire.setText("要求:" + mTrackwayInfo.getPartnerContent());

        // 点赞操作
        if (mTrackwayInfo.isPraise()) {
            praiseButton.setSelected(true);
            praiseButton.setText("{xk-praise-selected}");
        } else {
            praiseButton.setSelected(false);
            praiseButton.setText("{xk-praise}");
        }

        praiseLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppUtility.isFastClick()) {
                    return;
                }
                if (mTrackwayInfo == null) {
                    AppUtility.showToast("旅拼载入失败，不能点赞");
                    return;
                }
                executePraise(mTrackwayInfo);
            }
        });

        // 收藏操作
        if (mTrackwayInfo.isCollect()) {
            collectButton.setSelected(true);
            collectButton.setText("{xk-collect-selected}");
        }

        collectLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppUtility.isFastClick()) {
                    return;
                }
                if (mTrackwayInfo == null) {
                    AppUtility.showToast("旅拼载入失败，不能收藏");
                    return;
                }
                execureCollection(mTrackwayInfo);
            }
        });

        // 评论
        commentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AccountInfo.getInstance().isLogin()) {
                    startActivity(new Intent(mContext, LoginActivity.class));
                    return;
                }
                prepareAddComment(mTrackwayInfo, true);
            }
        });

        //分享
        shareLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addReadingRecord(mTGuid, mAuthor);
                ShareTrackwayActivity.launch(TrackwayDetailActivity.this, mTrackwayInfo, 1);
            }
        });

        // 显示点赞好友头像
        displayPraiseUser();

    }

    //获取旅拼详情数据
    private void getData(String tGuid, String author) {
        User user = AccountInfo.getInstance().loadAccount();
        String account = Security.aesEncrypt(String.valueOf(user.getAccount()));
        String sTGuid = Security.aesEncrypt(tGuid);
        String sAuthor = Security.aesEncrypt(author);
        ApiModule.apiService_1().travelTogetherDetailNoC(account, sTGuid, sAuthor).enqueue(new Callback<TrackwayInfoRepo>() {
            @Override
            public void onResponse(Call<TrackwayInfoRepo> call, Response<TrackwayInfoRepo> response) {
                TrackwayInfoRepo trackwayInfoRepo = response.body();
                String msg = trackwayInfoRepo.getMsg();
                if (trackwayInfoRepo.isSuccess()) {
                    mTrackwayInfo = trackwayInfoRepo.getTrackwayInfo();
                    initHeader();
                    initViews();
                    loadCommentData();
                } else {
                    if ("暂无数据".equals(msg)) {
                        final NormalDialog dialog = new NormalDialog(mContext);
                        dialog.isTitleShow(false)
                                .bgColor(Color.parseColor("#ffffff"))
                                .cornerRadius(5)
                                .content("该旅拼已被删除")
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
                        ProgressHUD.showInfoMessage(mContext, trackwayInfoRepo.getMsg());
                }
            }

            @Override
            public void onFailure(Call<TrackwayInfoRepo> call, Throwable t) {
                Log.i("TAG", "-----错误提示----->" + t.getMessage());
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
        String pGuid = Security.aesEncrypt(mTGuid);
        String author = Security.aesEncrypt(String.valueOf(mAuthor));
        final String page = Security.aesEncrypt(String.valueOf(mPage));
        String perpage = Security.aesEncrypt(String.valueOf(mPerPage));
        //Log.e("TAG", "------页数------>" + mLoadMore + mPage);
        ApiModule.apiService_1().travelTogetherDetailOnlyCPages(account, pGuid, author, page, perpage).enqueue(new Callback<CommentRepo>() {
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
                    //显示最新评论
                    TextView textView = (TextView) findViewById(R.id.tv_more);
                    View view = (View) findViewById(R.id.view);
                    View view1 = (View) findViewById(R.id.view1);
                    //如果评论为空,则评论区域不显示
                    if (mCommentItemList.size() > 0) {
                        //最新评论
                        view.setVisibility(View.VISIBLE);
                        view1.setVisibility(View.VISIBLE);
                        textView.setVisibility(View.VISIBLE);
                    } else {
                        //最新评论
                        textView.setVisibility(View.GONE);
                    }
                    // mCommentAdapter.notifyDataSetChanged();
                    if (mPage == 1) {
                        mCommentLV.setAdapter(mCommentAdapter);
                    } else {
                        mCommentAdapter.notifyDataSetChanged();
                    }
                    //mCommentLV.setSelection(0);
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
        if (data instanceof CommentItem) {
            comment = (CommentItem) data;
            content.setHint("回复" + comment.getReviewUName());
            content.setTag(comment);
        } else if (data instanceof TrackwayInfo) {
            comment = new CommentItem();
            content.setHint("你的评论会让咖主更有动力");
            content.setTag(comment);
        }
        mEnterLayout.restoreLoad(comment);
        Log.i("TAG", "-------是否谈起评论框------->" + mPopComment + popKeyboard);
        mCommonEnterRoot.setVisibility(View.VISIBLE);
        mBottomLayout.setVisibility(View.GONE);
        AppUtility.popSoftkeyboard(this, content, true);

    }

    private View.OnTouchListener onSendClicked = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (AppUtility.isFastClick()) {
                return false;
            }
            if (mTrackwayInfo == null) {
                AppUtility.showToast("旅拼载入失败，不能发送给评论");
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
        String tGuid = Security.aesEncrypt(mTrackwayInfo.gettGuid()); // 图说主键
        String toAccount = Security.aesEncrypt(String.valueOf(mTrackwayInfo.getAccount())); // 图说发布人行账号
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
        ApiModule.apiService_1().travelTogetherCommentAdd(account, tGuid, content, isReply, toUser, toGuid, toAccount).enqueue(new Callback<XKRepo>() {

            @Override
            public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                XKRepo xkRepo = response.body();
                ProgressHUD.dismiss();
                if (xkRepo.isSuccess()) {
                    addReadingRecord(mTGuid, mAuthor);
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
                        mTrackwayInfo.setCommentList(mCommentItemList);

                        EventBus.getDefault().post(mTrackwayInfo);
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


    /**
     * @功能描述 : 点击阅读记录
     */
    private void addReadingRecord(String tGuid, String author) {
        String sTGuid = Security.aesEncrypt(tGuid);
        String sAuthor = Security.aesEncrypt(author);
        ApiModule.apiService_1().travelTogetherReadAdd(sTGuid, sAuthor).enqueue(new Callback<XKRepo>() {
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

    // --------------------------- 点赞 ---------------------------

    /**
     * @功能描述 : 提交点赞操作
     */
    private void executePraise(final TrackwayInfo trackInfo) {
        if (!AccountInfo.getInstance().isLogin()) {
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }
        addReadingRecord(mTGuid, mAuthor);
        final String isPraiseStr = trackInfo.isPraise() ? "0" : "1";
        String account = Security.aesEncrypt(String.valueOf(currentUser.getAccount()));
        String tGuid = Security.aesEncrypt(trackInfo.gettGuid());
        String toAccount = Security.aesEncrypt(String.valueOf(trackInfo.getAccount()));
        final String isPraise = Security.aesEncrypt(isPraiseStr); // 1-点赞，0-取消赞
        ApiModule.apiService_1().travelTogetherPraiseAdd(account, tGuid, isPraise, toAccount).enqueue(new Callback<XKRepo>() {
            @Override
            public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                XKRepo xkRepo = response.body();
                if (xkRepo.isSuccess()) {
                    if (isPraiseStr.equals("0")) {
                        praiseButton.setSelected(false);
                        praiseButton.setText("{xk-praise}");
                        // 设置数据
                        trackInfo.setPraise(false);
                        List<PraiseItem> praiseItems = trackInfo.getPraiseList();
                        for (Iterator<PraiseItem> iterator = praiseItems.iterator(); iterator.hasNext(); ) {
                            PraiseItem praise = iterator.next();
                            if (praise.getAccount() == currentUser.getAccount()) {
                                iterator.remove();
                                trackInfo.setPraiseNum(trackInfo.getPraiseNum() - 1);
                            }
                        }
                    } else {
                        // ProgressHUD.showPraiseOrCollectSuccessMessage(CaptionDetailActivity.this, "谢谢你的喜欢");
                        praiseButton.setSelected(true);
                        praiseButton.setText("{xk-praise-selected}");
                        // 设置数据
                        trackInfo.setPraise(true);

                        List<PraiseItem> praiseItems = trackInfo.getPraiseList();
                        PraiseItem item = new PraiseItem();
                        currentUser = AccountInfo.getInstance().loadAccount();
                        item.setAccount(currentUser.getAccount());
                        item.setAvatar(currentUser.getAvatar());
                        item.setNick(currentUser.getNickname());
                        praiseItems.add(0, item);
                        trackInfo.setPraiseNum(trackInfo.getPraiseNum() + 1);

                    }
                    displayPraiseUser();
                    EventBus.getDefault().post(trackInfo);
                } else {
                    ProgressHUD.showInfoMessage(mContext, xkRepo.getMsg());
                }
            }

            @Override
            public void onFailure(Call<XKRepo> call, Throwable t) {

            }

        });
    }

    // --------------------------- 收藏 ---------------------------

    /**
     * @功能描述 : 提交收藏操作
     */
    private void execureCollection(final TrackwayInfo trackInfo) {
        if (!AccountInfo.getInstance().isLogin()) {
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }
        final String isCollectStr = collectButton.isSelected() ? "0" : "1";
        String account = Security.aesEncrypt(String.valueOf(currentUser.getAccount()));
        String pGuid = Security.aesEncrypt(trackInfo.gettGuid());
        String toAccount = Security.aesEncrypt(String.valueOf(trackInfo.getAccount()));
        String isCollect = Security.aesEncrypt(isCollectStr); // 1-收藏 0-取消收藏
        addReadingRecord(mTGuid, mAuthor);
        ApiModule.apiService_1().travelTogetherCollectionAdd(account, pGuid, isCollect, toAccount).enqueue(new Callback<XKRepo>() {
            @Override
            public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                XKRepo xkRepo = response.body();
                if (xkRepo.isSuccess()) {

                    if (isCollectStr.equals("0")) {
                        collectButton.setSelected(false);
                        collectButton.setText("{xk-collect}");
                        // 设置数据
                        trackInfo.setCollect(false);
                    } else {
                        // ProgressHUD.showPraiseOrCollectSuccessMessage(CaptionDetailActivity.this, "谢谢你的收藏");
                        collectButton.setSelected(true);
                        collectButton.setText("{xk-collect-selected}");
                        // 设置数据
                        trackInfo.setCollect(true);
                    }

                    EventBus.getDefault().post(trackInfo);
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
        mTrackwayInfo.setFriend(true);
        mTrackwayInfo.setFriend(true);
        String account = Security.aesEncrypt(String.valueOf(currentUser.getAccount()));
        String friendAccount = Security.aesEncrypt(String.valueOf(mTrackwayInfo.getAccount()));

        ApiModule.apiService_1().addFriend(account, friendAccount).enqueue(new Callback<XKRepo>() {

            @Override
            public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                XKRepo xkRepo = response.body();
                String message = xkRepo.getMsg();
                if (xkRepo.isSuccess()) {
                    EventBus.getDefault().post(mTrackwayInfo);
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
        praiseShowLayout = (LinearLayout) mListHeaderView.findViewById(R.id.layout_praise);
        praiseUsersLayout = (LinearLayout) mListHeaderView.findViewById(R.id.layout_praise_users);
        praiseCountText = (TextView) mListHeaderView.findViewById(R.id.text_praise_count);
        View view = (View) findViewById(R.id.divider_praise);
        View buttomView = (View) findViewById(R.id.view);
        View view_buttom = (View) findViewById(R.id.view_buttom);

        praiseCountText.setText(String.valueOf(mTrackwayInfo.getPraiseNum()));
        List<PraiseItem> praiseList = mTrackwayInfo.getPraiseList();
        if (praiseList.size() == 0) {
            praiseShowLayout.setVisibility(View.GONE);
            view.setVisibility(View.GONE);
            view_buttom.setVisibility(View.VISIBLE);
        } else {
            praiseShowLayout.setVisibility(View.VISIBLE);
            view.setVisibility(View.VISIBLE);
            buttomView.setVisibility(View.VISIBLE);
            view_buttom.setVisibility(View.GONE);
        }
        praiseUsersLayout.removeAllViews();
        for (int i = 0; i < praiseList.size(); i++) {
            if (i > 7) {
                IconicFontTextView praiseCountView = new IconicFontTextView(TrackwayDetailActivity.this);
                praiseCountView.setTextColor(Color.parseColor("#dcdce0"));
                praiseCountView.setTextSize(25);
                //TextView praiseCountView = (TextView) LayoutInflater.from(getApplicationContext()).inflate(R.layout.praise_count_view, null, false);
//                praiseCountView.setText(mPhotoTopic.getPraiseNum() + "");
                praiseCountView.setText("{xk-praise-more}");
                praiseCountView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PraiseListActivity.launch(TrackwayDetailActivity.this, mTrackwayInfo.gettGuid(), String.valueOf(mTrackwayInfo.getAccount()), 2);
                    }
                });
                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(AppUtility.dip2px(26), AppUtility.dip2px(26));
                params1.setMargins(0, 0, 0, 0);
                praiseUsersLayout.addView(praiseCountView, params1);
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
                    if (mAccount.equals(String.valueOf(praiseItem.getAccount()))) {
                        if (MainActivity.currentTabIndex != 3) {
                            MainActivity.launch(TrackwayDetailActivity.this, 1);
                        }
                    } else if (praiseItem.isAdmin()) {
                        UserProfileActivity.launch(TrackwayDetailActivity.this, String.valueOf(praiseItem.getAccount()), 2, 1);
                    } else {
                        UserProfileActivity.launch(TrackwayDetailActivity.this, String.valueOf(praiseItem.getAccount()), 2, 0);
                    }
                }
            });
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(AppUtility.dip2px(26), AppUtility.dip2px(26));
            params.setMargins(0, 0, AppUtility.dip2px(7), 0);
            praiseUsersLayout.addView(avatarView, params);
        }
    }

    // ------------------------- 举报 -----------------------------

    @OnClick(R.id.tv_right1)
    void onRightClick() {
        addReadingRecord(mTGuid, mAuthor);
        ShareTrackwayActivity.launch(TrackwayDetailActivity.this, mTrackwayInfo, 2);

//        final String[] stringItems;
//        if (!TextUtils.isEmpty(mTrackwayInfo.getWebUrl())) {
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
//                        addReadingRecord(mTGuid, mAuthor);
//                        ShareTrackwayActivity.launch(TrackwayDetailActivity.this, mTrackwayInfo, 2);
//                        break;
//                    case 1://举报
//                        dialog.dismiss();
////                        report();
//                        initReportType();
//                        break;
//                }
//            }
//        });
    }

    /**
     * @功能描述 : 获取举报类型
     */

    private void report() {
        if (AccountInfo.getInstance().getReportType() == null) {
            int mVersion = AppUtility.getAppVersionCode();
            String sVersion = Security.aesEncrypt(String.valueOf(mVersion));
            ApiModule.apiService_1().photoTopicReportTypeList(sVersion).enqueue(new Callback<ReportInfo>() {


                @Override
                public void onResponse(Call<ReportInfo> call, Response<ReportInfo> response) {
                    ReportInfo reportInfo = response.body();
                    if (reportInfo.isSuccess()) {
                        reportInfoList = reportInfo.getListMessage();
                        confirmReportDialog();
                        //保存举报类型到本地
                        AccountInfo.getInstance().saveReportType(reportInfoList);
                    } else {
//                    ProgressHUD.showInfoMessage(mContext, reportInfo.getMsg());
                    }
                }

                @Override
                public void onFailure(Call<ReportInfo> call, Throwable t) {
                    ProgressHUD.showInfoMessage(mContext, getString(R.string.common_network_error));
                }
            });
        } else {
            initReportType();
            reportInfoList = AccountInfo.getInstance().getReportType();
            confirmReportDialog();
        }
    }

    /**
     * @功能描述 : 获取举报类型
     */
    private void initReportType() {
        int mVersion = AppUtility.getAppVersionCode();
        String sVersion = Security.aesEncrypt(String.valueOf(mVersion));
        ApiModule.apiService_1().photoTopicReportTypeList(sVersion).enqueue(new Callback<ReportInfo>() {

            @Override
            public void onResponse(Call<ReportInfo> call, Response<ReportInfo> response) {
                ReportInfo reportInfo = response.body();
                if (reportInfo.isSuccess()) {
                    reportInfoList = reportInfo.getListMessage();
                    //保存举报类型到本地
                    AccountInfo.getInstance().saveReportType(reportInfoList);
                } else {
                    ProgressHUD.showInfoMessage(mContext, reportInfo.getMsg());
                }
            }

            @Override
            public void onFailure(Call<ReportInfo> call, Throwable t) {
                ProgressHUD.showInfoMessage(mContext, getString(R.string.common_network_error));
            }
        });
    }

    /**
     * @功能描述 : 举报确认提示操作框
     */
    private void confirmReportDialog() {
        List<ReportType> reportTypes = reportInfoList;
        final String[] stringItems = new String[reportTypes.size()];
        for (int i = 0; i < reportTypes.size(); i++) {
            stringItems[i] = reportTypes.get(i).getTitle();
            Log.e("举报类型集合", "" + stringItems[i]);
        }

//        final String[] stringItems = {"广告欺诈", "淫秽色情", "骚扰谩骂", "反动政治", "其它"};
        dialog = new ActionSheetDialog(this, stringItems, null);
        dialog.title("请选择举报类型")
                .titleTextColor(Color.parseColor("#9c9ca0"))
                .titleTextSize_SP(15.5f)
                .titleHeight(36f)
                .show();
        dialog.itemTextColor(Color.parseColor("#363640"))
                .itemTextSize(15.5f)
                .itemHeight(36f)
                .show();
        dialog.cancelText(Color.parseColor("#363640"))
                .cancelTextSize(15.5f)
                .dividerColor(Color.parseColor("#d8d9e2"))
                .show();
        //弹出框透明度设为1
        dialog.titleBgColor(Color.parseColor("#ffffff"));
        dialog.lvBgColor(Color.parseColor("#ffffff"));
        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                submitReport(String.valueOf(i));
                dialog.dismiss();
            }
        });
    }

    /**
     * @功能描述 : 提交举报数据至服务器
     */
    private void submitReport(String type) {
        if (!AccountInfo.getInstance().isLogin()) {
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }

        ProgressHUD.showLoadingMessage(mContext, "正在提交举报", false);
        String account = Security.aesEncrypt(String.valueOf(currentUser.getAccount()));
        String tGuid = Security.aesEncrypt(mTrackwayInfo.gettGuid());
        String toAccount = Security.aesEncrypt(String.valueOf(mTrackwayInfo.getAccount()));
        String sType = Security.aesEncrypt(type);

        ApiModule.apiService_1().travelTogetherReportListAdd(account, tGuid, toAccount, sType).enqueue(new Callback<XKRepo>() {

            @Override
            public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                XKRepo xkRepo = response.body();
                ProgressHUD.dismiss();
                if (xkRepo.isSuccess()) {
                    ProgressHUD.showSuccessMessage(mContext, "举报成功");
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
    protected void onResume() {
        super.onResume();
        notifyData(mTGuid, mAuthor);
    }

    @OnClick(R.id.tv_back1)
    void backClicked() {
        finish();
    }

    /**
     * @param view
     * @param position
     * @功能描述 : 处理评论回复
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
            if (selectCircleItem != null) {
                mSelectCircleItemH = selectCircleItem.getHeight();
            }
            if (mEnterLayout != null) {
                softkeyboardHeight = mEnterLayout.softkeyboardHeight;
            }
            if (softkeyboardHeight == 0) {
                softkeyboardHeight = 550;
            }
            int listviewOffset;
            if (mToolbar.getVisibility() == View.GONE) {
                listviewOffset = screenHeight - mSelectCircleItemH - softkeyboardHeight - inputBoxHeight - inputBoxHeight - statusBarH + mToolbar.getHeight();
            } else {
                listviewOffset = screenHeight - mSelectCircleItemH - softkeyboardHeight - inputBoxHeight - inputBoxHeight - statusBarH;
            }

            mCommentLV.setSelectionFromTop(mCirclePosition, listviewOffset);
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

//        final String isPraiseStr = commentItem.isPraise() ? "0" : "1";
//        String account = Security.aesEncrypt(String.valueOf(currentUser.getAccount())); // 行账号
//        String pGuid = Security.aesEncrypt(mTrackwayInfo.gettGuid()); // 主键
//        String toAccount = Security.aesEncrypt(String.valueOf(mTrackwayInfo.getAccount())); // 图说作者行账号
//        String pcGuid = Security.aesEncrypt(commentItem.getGuid()); // 评论主键
//        final String isPraise = Security.aesEncrypt(isPraiseStr); // 1-点赞，0-取消赞
//
//        ApiModule.apiService().photoTopicCommentPraiseAdd(account, pGuid, pcGuid, toAccount, isPraise).enqueue(new Callback<XKRepo>() {
//
//
//            @Override
//            public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
//                XKRepo xkRepo = response.body();
//                if (xkRepo.isSuccess()) {
//                    if (isPraiseStr.equals("0")) {
//                        mCommentItemList.get(position).setIsPraise(false);
//                    } else {
//                        mCommentItemList.get(position).setIsPraise(true);
//                    }
//                    mCommentAdapter.notifyDataSetChanged();
//                } else {
//                    ProgressHUD.showInfoMessage(mContext, xkRepo.getMsg());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<XKRepo> call, Throwable t) {
//                ProgressHUD.showInfoMessage(mContext, getString(R.string.common_network_error));
//            }
//        });
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
        final NormalListDialog dialog = new NormalListDialog(TrackwayDetailActivity.this, testItems);
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
        ClipData clip = ClipData.newPlainText("simple text", content);
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
     * @param tGuid  旅拼GUID
     * @param author 图说作者行账号
     * @功能描述 : 获取旅拼详情
     */
    private void notifyData(String tGuid, String author) {
        User user = AccountInfo.getInstance().loadAccount();
        String account = Security.aesEncrypt(String.valueOf(user.getAccount()));
        String sPGuid = Security.aesEncrypt(tGuid);
        String sAuthor = Security.aesEncrypt(author);
        // Log.i("TAG", "---account---->" + account + "----sPGuid---->" + sPGuid + "---sAuthor----->" + sAuthor);
        ApiModule.apiService_1().travelTogetherDetailNoC(account, sPGuid, sAuthor).enqueue(new Callback<TrackwayInfoRepo>() {

            @Override
            public void onResponse(Call<TrackwayInfoRepo> call, Response<TrackwayInfoRepo> response) {
                TrackwayInfoRepo trackwayInfoRepo = response.body();
                String msg = trackwayInfoRepo.getMsg();
                if (trackwayInfoRepo.isSuccess()) {
                    mTrackwayInfo = trackwayInfoRepo.getTrackwayInfo();
                    //initHeader();
                    //initViews();
                    //loadCommentData();
                    //mEnterLayout = new EnterEmojiLayout(this, onSendClicked, EnterLayout.Type.TextOnly, EnterEmojiLayout.EmojiType.SmallOnly);
                    if (mEnterLayout == null)
                        mEnterLayout = new EnterEmojiLayout(TrackwayDetailActivity.this, onSendClicked, EnterLayout.Type.TextOnly, EnterEmojiLayout.EmojiType.SmallOnly);
                    String editContent = mEnterLayout.content.getText().toString();
                    if (mPopComment == 1) {
                        if (mCommentItem != null) {
                            prepareAddComment(mCommentItem, true);
                        } else {
                            prepareAddComment(mTrackwayInfo, true);
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
                                .content("该旅拼已被删除")
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
                        ProgressHUD.showInfoMessage(mContext, trackwayInfoRepo.getMsg());
                }
                //mCommentLV.setSelection(0);
            }

            @Override
            public void onFailure(Call<TrackwayInfoRepo> call, Throwable t) {
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

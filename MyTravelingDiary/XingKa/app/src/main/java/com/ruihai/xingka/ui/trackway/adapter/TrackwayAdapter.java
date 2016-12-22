package com.ruihai.xingka.ui.trackway.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.flyco.dialog.entity.DialogMenuItem;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.NormalListDialog;
import com.orhanobut.logger.Logger;
import com.ruihai.iconicfontengine.widget.IconicFontTextView;
import com.ruihai.xingka.Global;
import com.ruihai.xingka.R;
import com.ruihai.xingka.api.ApiModule;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.api.model.CommentItem;
import com.ruihai.xingka.api.model.ImageItem;
import com.ruihai.xingka.api.model.PraiseItem;
import com.ruihai.xingka.api.model.TrackwayInfo;
import com.ruihai.xingka.api.model.User;
import com.ruihai.xingka.api.model.XKRepo;
import com.ruihai.xingka.event.OnItemClickListener;
import com.ruihai.xingka.ui.MainActivity;
import com.ruihai.xingka.ui.caption.OfficalCaptionInfoActivity;
import com.ruihai.xingka.ui.caption.PraiseListActivity;
import com.ruihai.xingka.ui.common.PhotoPagerActivity;
import com.ruihai.xingka.ui.common.enter.EmojiUtils;
import com.ruihai.xingka.ui.mine.UserProfileActivity;
import com.ruihai.xingka.ui.trackway.TrackwayDetailActivity;
import com.ruihai.xingka.ui.trackway.fragment.TrackwayFragment;
import com.ruihai.xingka.utils.AppUtility;
import com.ruihai.xingka.utils.QiniuHelper;
import com.ruihai.xingka.utils.Security;
import com.ruihai.xingka.widget.CommentTextView;
import com.ruihai.xingka.widget.HorizontalListView;
import com.ruihai.xingka.widget.ProgressHUD;
import com.shizhefei.mvc.IDataAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 旅拼主页适配器
 * Created by lqfang on 16/5/3.
 */
public class TrackwayAdapter extends RecyclerView.Adapter<TrackwayAdapter.ItemViewHolder> implements IDataAdapter<List<TrackwayInfo>> {

    public static final String DEFAULT_AVATAR_KEY = "00000000-0000-0000-0000-000000000000";

    private LayoutInflater inflater;
    private String mCurrentAccount; //本人行咖号
//    private String mAuthorAccount; //图说作者行咖号
    private ClipboardManager clipboard;//粘贴板
    private Context context;
    private Activity mContext;

    private IconicFontTextView view1;

    private List<TrackwayInfo> trackwayInfoList = new ArrayList<>();

    public OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public TrackwayAdapter(Context context, Activity mContext, String mCurrentAccount) {
        super();
        this.mContext = mContext;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.mCurrentAccount = mCurrentAccount;
    }


    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.adapter_trackway, parent, false);
        return new ItemViewHolder(itemView, viewType);
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, final int position) {
        final TrackwayInfo trackwayInfo = trackwayInfoList.get(position);

        final Handler mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                int lineCount = msg.what;
                if (lineCount > 6) {
                    holder.moreContentBtn.setVisibility(View.VISIBLE);
                    holder.describe_content.setMaxLines(6);
                    holder.moreContentBtn.setImageResource(R.mipmap.icon_content_down);
                } else {
                    holder.moreContentBtn.setVisibility(View.GONE);
                }
                if (trackwayInfo.isHasMore()) {
                    holder.describe_content.setMaxLines(holder.describe_content.getLineCount());
                    holder.moreContentBtn.setImageResource(R.mipmap.icon_content_up);
                }
                return false;
            }
        });

        // --------------------------- 头像 ---------------------------
        Uri avatarUri = Uri.parse(QiniuHelper.getThumbnail96Url(trackwayInfo.getAvatar()));
        if (!DEFAULT_AVATAR_KEY.equals(trackwayInfo.getAvatar())) {
            holder.avatar.setImageURI(avatarUri);
        } else {
            holder.avatar.setImageURI(Uri.parse("res:///" + R.mipmap.default_avatar));
        }

        //性别
        if (trackwayInfo.getSex() == 1) {
            holder.mSex.setVisibility(View.VISIBLE);
            holder.mSex.setImageResource(R.mipmap.icon_boy);
        } else if(trackwayInfo.getSex() == 2){
            holder.mSex.setVisibility(View.VISIBLE);
            holder.mSex.setImageResource(R.mipmap.icon_girl);
        }else {
            holder.mSex.setVisibility(View.GONE);
        }

        // 用户名(昵称/备注),判断备注是否为空，如果为空，显示nick，如果不为空，则显示remark。
        if (!TextUtils.isEmpty(trackwayInfo.getRemark())) {
            holder.name.setText(trackwayInfo.getRemark());
        } else if (!TextUtils.isEmpty(trackwayInfo.getNick())) {
            // holder.name.setText(trackwayInfo.getNick());
            holder.name.setText(EmojiUtils.fromStringToEmoji1(trackwayInfo.getNick(), mContext));
        } else { // 显示行咖号
            holder.name.setText(String.valueOf(trackwayInfo.getAccount()));
        }
        // ---------------------------------   图说作者行咖号 ----------------------
        final String mAuthorAccount = String.valueOf(trackwayInfo.getAccount());

        if (trackwayInfo.isOffical()) {//显示官方账号
            holder.officalAccount.setVisibility(View.VISIBLE);
        } else
            holder.officalAccount.setVisibility(View.GONE);
        // ------------------------ 发布时间 ------------------------
        String datetime = trackwayInfo.getAddTime().substring(6, 19);
        long timestamp = Long.valueOf(datetime);
        holder.aftertime.setText(Global.dayToNow(timestamp));

        // --------------------------- 旅拼描述内容 ---------------------------
        if (!TextUtils.isEmpty(trackwayInfo.getContent())) {
            holder.describe_content.setVisibility(View.VISIBLE);
//            SpannableString content = new SpannableString("旅拼描述:" + trackwayInfo.getContent());
//            content.setSpan(new ForegroundColorSpan(R.color.black), 5, content.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.describe_content.setText(EmojiUtils.fromStringToEmoji1(trackwayInfo.getContent(), context));

//            holder.moreContentBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
////                    backgroundAlpha(0.8f); // 设置背景颜色变暗
////                    moreContentPW.showAtLocation(rootLayout, Gravity.BOTTOM, 0, 0);
////                    int pos = (int) v.getTag();
////                    boolean isExpend = trackwayInfoList.get(pos).isHasMore();
////                    if (isExpend) {
////                        holder.describe_content.setMaxLines(6);
////                        holder.moreContentBtn.setImageResource(R.mipmap.icon_content_down);
////                        trackwayInfoList.get(pos).setHasMore(false);
////                    } else {
////                        holder.describe_content.setMaxLines(holder.describe_content.getLineCount());
////                        holder.moreContentBtn.setImageResource(R.mipmap.icon_content_up);
////                        trackwayInfoList.get(pos).setHasMore(true);
////                    }
//                }
//            });
        } else {
            holder.describe_content.setText("");
            holder.describe_content.setVisibility(View.GONE);
        }

        holder.describe_content.post(new Runnable() {
            @Override
            public void run() {
                int lineCount = holder.describe_content.getLineCount();
                mHandler.sendEmptyMessage(lineCount);
            }
        });
        holder.moreContentBtn.setTag(position);
        holder.describe_content.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                normalListDialogNoTitle(trackwayInfo.getContent(), null, mAuthorAccount, 2, v);
                return false;
            }
        });
        holder.describe_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (trackwayInfo.isOffical() && !TextUtils.isEmpty(trackwayInfo.getWebUrl())) {
                    OfficalCaptionInfoActivity.launch(mContext, trackwayInfo.getWebUrl(), trackwayInfo.gettGuid());
                } else {
                    Intent intent = new Intent(mContext, TrackwayDetailActivity.class);
                    intent.putExtra("position", position);
                    intent.putExtra(TrackwayFragment.KEY_TRACKWAY_ITEM, trackwayInfo);
                    mContext.startActivityForResult(intent, TrackwayFragment.RESULT_EDIT_TRACKWAY);
                }
            }
        });
        //------------路线 旅伴要求 费用 人数  -----------------------
        //主题
        holder.trackway_title.setText(trackwayInfo.getTitle());
        //日期
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String beginTime = trackwayInfo.getBeginDate().substring(6, 19);
        String endTime = trackwayInfo.getEndDate().substring(6, 19);
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
        holder.trackway_date.setText("出发日期:" + date + " 全程" + total + "天");

        //路线
        String address = "";
        for (int i = 0; i < trackwayInfo.getLineInfoList().size(); i++) {
            if (i == trackwayInfo.getLineInfoList().size() - 1) {
                address += trackwayInfo.getLineInfoList().get(i).getAddress();
            } else {
                address += trackwayInfo.getLineInfoList().get(i).getAddress() + " - ";
            }
        }
        holder.trackway_line.setText(address);

//        //费用说明
//        SpannableString cost = new SpannableString("费用:" + trackwayInfo.getCostType());
//        cost.setSpan(new ForegroundColorSpan(R.color.black), 3, cost.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        holder.cost.setText(cost);
//
//        //旅伴要求
//        SpannableString partnerContent = new SpannableString("旅伴要求:" + trackwayInfo.getPartnerContent());
//        partnerContent.setSpan(new ForegroundColorSpan(R.color.black), 5, partnerContent.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        holder.partner_require.setText(partnerContent);
//
//        //人数
//        SpannableString personNum = new SpannableString("人数:" + trackwayInfo.getPersonNum());
//        personNum.setSpan(new ForegroundColorSpan(R.color.black), 3, personNum.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        holder.peopleNum.setText(personNum);

        //-------------------------------- 旅拼照片 ---------------------------------
        if (trackwayInfo.getImageList().size() > 0) {
            holder.swipeViewLayout.setVisibility(View.VISIBLE);
            holder.listView.setVisibility(View.VISIBLE);

            final List<String> originalImageKeys = new ArrayList<>();
            final List<String> imageKeys = new ArrayList<>();
            imageKeys.clear();
            final TrackwayImageAdapter swipeAdapter = new TrackwayImageAdapter(context, imageKeys);
            holder.listView.setAdapter(swipeAdapter);
            for (ImageItem image : trackwayInfo.getImageList()) {
                imageKeys.add(image.imgSrc);
                originalImageKeys.add(image.imgSrc);
            }

            holder.listView.removeAllViewsInLayout();
            swipeAdapter.notifyDataSetChanged();

            holder.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    addReadingRecord(trackwayInfo.gettGuid(), String.valueOf(trackwayInfo.getAccount()));
                    // 获得图片原本所在图片集的排序位置
//                    int position = originalImageKeys.indexOf(i);
                    ArrayList<String> imageUrls = new ArrayList<>();
                    for (String imageKey : originalImageKeys) {
                        imageUrls.add(QiniuHelper.getOriginalWithKey(imageKey));
                    }
                    Intent intent = new Intent(context, PhotoPagerActivity.class);
                    intent.putExtra(PhotoPagerActivity.EXTRA_CURRENT_ITEM, position);
                    intent.putExtra(PhotoPagerActivity.EXTRA_PHOTOS, imageUrls);
                    intent.putExtra(PhotoPagerActivity.EXTRA_USERNICK, trackwayInfo.getNick());
                    intent.putExtra(PhotoPagerActivity.EXTRA_TYPE, 1);
                    context.startActivity(intent);
                }
            });
        }

        // --------------------------- 点赞 ---------------------------
        if (trackwayInfo.isPraise()) {
            holder.praiseButton.setSelected(true);
            holder.praiseButton.setText("{xk-praise-selected}");

        } else {
            holder.praiseButton.setSelected(false);
            holder.praiseButton.setText("{xk-praise}");

        }

        // --------------------------- 显示点赞人员头像 ---------------------------
        holder.praiseCountText.setText(String.valueOf(trackwayInfo.getPraiseNum()));
        List<PraiseItem> praiseList = trackwayInfo.getPraiseList();
        if (praiseList.size() == 0) {
            holder.praiseShowLayout.setVisibility(View.GONE);
            holder.praiseDivider.setVisibility(View.GONE);
        } else {
            holder.praiseShowLayout.setVisibility(View.VISIBLE);
            holder.praiseDivider.setVisibility(View.VISIBLE);
        }
        holder.praiseUsersLayout.removeAllViews();
        for (int i = 0; i < praiseList.size(); i++) { // 遍历点赞集合,动态创建点赞头像
            if (i > 7) { // 点赞数量大于7时不显示全部,显示总数
                IconicFontTextView praiseCountView = new IconicFontTextView(context);
                praiseCountView.setTextColor(Color.parseColor("#dcdce0"));
                praiseCountView.setTextSize(25);
                //TextView praiseCountView = (TextView) LayoutInflater.from(context).inflate(R.layout.praise_count_view, null, false);
                praiseCountView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PraiseListActivity.launch(mContext, trackwayInfo.gettGuid(), String.valueOf(trackwayInfo.getAccount()), 2);
                    }
                });
//                    praiseCountView.setText(String.valueOf(item.getPraiseNum()));
                praiseCountView.setText("{xk-praise-more}");
                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(AppUtility.dip2px(27), AppUtility.dip2px(27));
                params1.setMargins(0, 0, 0, 0);
                holder.praiseUsersLayout.addView(praiseCountView, params1);
                break;
            }
            PraiseItem praiseItem = praiseList.get(i);
            SimpleDraweeView avatarView = (SimpleDraweeView) LayoutInflater.from(context).inflate(R.layout.item_caption_praise_user, null, false);
            avatarView.setTag(praiseItem);
            Uri userAvatarUri = Uri.parse(QiniuHelper.getThumbnail96Url(praiseItem.getAvatar()));
            if (!DEFAULT_AVATAR_KEY.equals(praiseItem.getAvatar())) {
                avatarView.setImageURI(userAvatarUri);
            }

            avatarView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PraiseItem item = (PraiseItem) (v.getTag());
                    if (mCurrentAccount.equals(item.getAccount())) {
                        if (MainActivity.currentTabIndex != 3) {
                            MainActivity.launch(context, 1);
                        }
                    } else {
                        if (item.isAdmin()) {
                            UserProfileActivity.launch(context, String.valueOf(item.getAccount()), 2, 1);
                        } else {
                            UserProfileActivity.launch(context, String.valueOf(item.getAccount()), 2, 0);
                        }

                    }
//                        UserProfileActivity.launch(context, account);
                }
            });
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(AppUtility.dip2px(26), AppUtility.dip2px(26));
            params.setMargins(0, 0, AppUtility.dip2px(7), 0);
            holder.praiseUsersLayout.addView(avatarView, params);
        }

        // --------------------------- 收藏 ---------------------------
        if (trackwayInfo.isCollect()) {
            holder.collectButton.setSelected(true);
            holder.collectButton.setText("{xk-collect-selected}");

        } else {
            holder.collectButton.setSelected(false);
            holder.collectButton.setText("{xk-collect}");

        }

        // --------------------------- 关注 ---------------------------
        if (trackwayInfo.isFriend()) {
            holder.addFollowButton.setVisibility(View.VISIBLE);
            holder.addFollowButton.setSelected(true);
            holder.addFollowButton.setText("已关注");
            holder.addFollowButton.setEnabled(false);
        } else {
            User currentUser = AccountInfo.getInstance().loadAccount();
            if (currentUser != null) {
                if (trackwayInfo.getAccount() == currentUser.getAccount()) {
                    holder.addFollowButton.setVisibility(View.GONE);
                } else {
                    holder.addFollowButton.setVisibility(View.VISIBLE);
                    holder.addFollowButton.setSelected(false);
                    holder.addFollowButton.setText(R.string.caption_add_follow);
                    holder.addFollowButton.setEnabled(true);
                }
            }
        }

        // --------------------------- 评论内容 ---------------------------
        final List<CommentTextView> commentTextViews = new ArrayList<>();
        commentTextViews.add(holder.comment0);
        commentTextViews.add(holder.comment1);
        commentTextViews.add(holder.comment2);
        commentTextViews.add(holder.comment3);
        commentTextViews.add(holder.comment4);
        List<CommentItem> commentList = trackwayInfo.getCommentList();

        if (commentList != null) { //增加评论列表对空指针的判断
            if (commentList.size() <= 0) {
                holder.commentsLayout.setVisibility(View.GONE);
            } else {
                holder.commentsLayout.setVisibility(View.VISIBLE);
            }

//            holder.more_comments.setVisibility(View.VISIBLE);
            view1 = holder.more_comments;
            holder.more_comments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, TrackwayDetailActivity.class);
                    intent.putExtra("position", position);
                    intent.putExtra(TrackwayFragment.KEY_TRACKWAY_ITEM, trackwayInfo);
                    mContext.startActivityForResult(intent, TrackwayFragment.RESULT_EDIT_TRACKWAY);
                }
            });
            int needShow = commentList.size();//首页要展示的评论数量
            for (int i = 0; i < commentTextViews.size(); i++) {
                if (i < needShow) {
                    //获取对应的一级评论
                    CommentItem commentItem = commentList.get(i);
                    commentTextViews.get(i).setVisibility(View.VISIBLE);
                    commentTextViews.get(i).setReply(commentList.get(i));
                    commentTextViews.get(i).setTag(commentItem);
//                    commentTextViews.get(i).setTag(i);
                    commentTextViews.get(i).setListener(new CommentTextView.TextBlankClickListener() {

                        @Override
                        public void onBlankClick(View view) {
                            CommentItem commentItem = (CommentItem) view.getTag();
                            Intent intent = new Intent(mContext, TrackwayDetailActivity.class);
                            intent.putExtra(TrackwayFragment.KEY_TRACKWAY_ITEM, trackwayInfo);
                            intent.putExtra(TrackwayFragment.KEY_POP_COMMENT, 1);
                            intent.putExtra(TrackwayFragment.KEY_COMMENT_ITEM, commentItem);
                            mContext.startActivity(intent);
//                        mOnItemClickListener.onItemChildClick(view, position);
//                        TrackwayDetailActivity.launch(mContext, trackwayInfo.gettGuid(), String.valueOf(trackwayInfo.getAccount()));
                        }

                        @Override
                        public void onLongClick(View view) {
                            CommentItem commentItem = (CommentItem) view.getTag();
                            if (mCurrentAccount.equals(commentItem.getReviewUid())) {
                                normalListDialogNoTitle(commentItem.getReviewContent(), commentItem.getGuid(), mAuthorAccount, 1, view);
                            } else {
                                normalListDialogNoTitle(commentItem.getReviewContent(), commentItem.getGuid(), mAuthorAccount,  2, view);
                            }
                        }
                    });
                } else {
                    commentTextViews.get(i).setVisibility(View.GONE);
                }
            }
        }

        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(holder.itemView, position);
                }
            });

            // 点赞
            holder.praiseLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemChildClick(v, position);
                }
            });

            // 收藏
            holder.collectLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemChildClick(v, position);
                }
            });

            // 评论
            holder.commentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemChildClick(v, position);
                }
            });

            // 分享
            holder.shareLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemChildClick(v, position);
                }
            });


            // 加关注
            holder.addFollowButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemChildClick(v, position);
                }
            });

//            // 图片
//            holder.coverImage.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    mOnItemClickListener.onItemChildClick(v, position);
//                }
//            });

            // 头像
            holder.avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemChildClick(v, position);
                }
            });
            //更多内容
            holder.moreContentBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemChildClick(v, position);
                }
            });

        }

    }


    @Override
    public int getItemCount() {
        return trackwayInfoList.size();
    }

    @Override
    public void notifyDataChanged(List<TrackwayInfo> data, boolean isRefresh) {
        if (isRefresh) {
            trackwayInfoList.clear();
        }
        trackwayInfoList.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public List<TrackwayInfo> getData() {
        return trackwayInfoList;
    }

    @Override
    public boolean isEmpty() {
        return trackwayInfoList.isEmpty();
    }


    static class ItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.sdv_avatar)
        SimpleDraweeView avatar;
        @BindView(R.id.iv_sex)
        ImageView mSex;
        @BindView(R.id.tv_username)
        TextView name;
        @BindView(R.id.tv_aftertime)
        TextView aftertime;
        @BindView(R.id.trackway_title)
        TextView trackway_title;
        @BindView(R.id.describe_content)
        TextView describe_content; //旅拼描述
        @BindView(R.id.trackway_time_text)
        TextView trackway_date; //出发时间
        @BindView(R.id.trackway_line_text)
        TextView trackway_line;
        //        @BindView(R.id.partner_require_text)
//        TextView partner_require; //旅伴要求
//        @BindView(R.id.cost_text)
//        TextView cost; //费用;
//        @BindView(R.id.number_people_text)
//        TextView peopleNum; //人数
        @BindView(R.id.tv_more)
        IconicFontTextView more_comments;
        // 评论内容
        @BindView(R.id.tv_comment_0)
        CommentTextView comment0;
        @BindView(R.id.tv_comment_1)
        CommentTextView comment1;
        @BindView(R.id.tv_comment_2)
        CommentTextView comment2;
        @BindView(R.id.tv_comment_3)
        CommentTextView comment3;
        @BindView(R.id.tv_comment_4)
        CommentTextView comment4;
        @BindView(R.id.layout_comments)
        LinearLayout commentsLayout;
        // 照片
//        @BindView(R.id.imageView1)
//        RoundedImageView coverImage;
//        @BindView(R.id.images_layout)
//        LinearLayout imagesLayout;
        @BindView(R.id.listview)
        HorizontalListView listView;
        @BindView(R.id.swipeview_layout)
        LinearLayout swipeViewLayout;

        @BindView(R.id.layout_praise)
        LinearLayout praiseShowLayout;
        @BindView(R.id.layout_praise_users)
        LinearLayout praiseUsersLayout;
        @BindView(R.id.btn_add_follow)
        IconicFontTextView addFollowButton;
        @BindView(R.id.text_praise_count)
        TextView praiseCountText;

        @BindView(R.id.ll_collect)
        LinearLayout collectLayout;
        @BindView(R.id.ifb_collect)
        IconicFontTextView collectButton;

        @BindView(R.id.tv_officalaccount)
        TextView officalAccount;

        // 点赞
        @BindView(R.id.ll_praise)
        LinearLayout praiseLayout;
        @BindView(R.id.ifb_praise)
        IconicFontTextView praiseButton;
        @BindView(R.id.ll_comment)
        LinearLayout commentLayout;

        @BindView(R.id.ifb_share)
        IconicFontTextView shareButton;
        @BindView(R.id.ll_share)
        LinearLayout shareLayout;
        //显示完整图说内容
        @BindView(R.id.btn_content_more)
        ImageView moreContentBtn;
        //        @BindView(R.id.layout_content_more)
//        LinearLayout moreContentLayout;
        @BindView(R.id.divider_praise)
        View praiseDivider;

        public int viewType;

        public ItemViewHolder(View itemView, int viewType) {
            super(itemView);
            this.viewType = viewType;
            ButterKnife.bind(this, itemView);
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


    //显示复制和删除的弹框
    private void normalListDialogNoTitle(final String content, final String gUid, final String mAuthorAccount, int type, final View v) {
        final ArrayList<DialogMenuItem> testItems = new ArrayList<>();

        if (type == 1) {
            testItems.add(new DialogMenuItem("复制", R.mipmap.ic_winstyle_copy));
            testItems.add(new DialogMenuItem("删除", R.mipmap.ic_winstyle_delete));
        } else if (type == 2) {
            testItems.add(new DialogMenuItem("复制", R.mipmap.ic_winstyle_copy));
        }

        final NormalListDialog dialog = new NormalListDialog(mContext, testItems);
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
                    case 1://删除
                        showDelDialog(gUid, mAuthorAccount, v);

                        break;
                }
                dialog.dismiss();
            }
        });
    }

    /**
     * 执行删除单个评论或回复操作
     *
     * @param
     */
    private void deleteTrackwayComment(final String gUid, final String mAuthorAccount,  final View view) {
        ProgressHUD.showLoadingMessage(mContext, "正在删除...", false);
        User currentUser = AccountInfo.getInstance().loadAccount();
        String sAccount = Security.aesEncrypt(String.valueOf(currentUser.getAccount()));
//        Log.e("TAG", "--行咖号-->" + sAccount);
        String sPCGuid = Security.aesEncrypt(gUid);
//        Log.e("TAG", "--评论主键ID-->" + sPCGuid);
        String sAuthorAccount = Security.aesEncrypt(mAuthorAccount);
//        Log.e("TAG", "---图说发布人行咖号->" + sAuthorAccount);
        Call<XKRepo> call = ApiModule.apiService_1().deleteTravelTogetherComment(sAccount, sPCGuid, sAuthorAccount);
        call.enqueue(new Callback<XKRepo>() {
            @Override
            public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                XKRepo xkRepo = response.body();
                ProgressHUD.dismiss();
                String message = xkRepo.getMsg();
                if (xkRepo.isSuccess()) {
                    ProgressHUD.showSuccessMessage(mContext, "删除成功");
                    view.setVisibility(View.GONE);
//                    view1.setVisibility(View.GONE); //最后一条评论删除后图标隐藏
                } else {
                    ProgressHUD.showInfoMessage(mContext, message);
                }
            }

            @Override
            public void onFailure(Call<XKRepo> call, Throwable t) {
                ProgressHUD.showErrorMessage(mContext, t.getMessage());
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

    //显示删除对话框
    private void showDelDialog(final String gUid, final String mAuthorAccount, final View view) {
        // 1. 布局文件转换为View对象
        LayoutInflater inflater = LayoutInflater.from(mContext);
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.dialog_communal, null);
        final Dialog dialog = new AlertDialog.Builder(mContext).create();
        dialog.setCancelable(false);
        dialog.show();
        dialog.getWindow().setContentView(layout);

        // 3. 消息内容
        TextView dialog_msg = (TextView) layout.findViewById(R.id.update_content);
        dialog_msg.setText("确定删除该评论吗?");

        // 4. 确定按钮
        Button btnOK = (Button) layout.findViewById(R.id.umeng_update_id_ok);
        btnOK.setText("确定");
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTrackwayComment(gUid, mAuthorAccount, view);
                dialog.dismiss();
            }
        });

        // 5. 取消按钮
        Button btnCancel = (Button) layout.findViewById(R.id.umeng_update_id_cancel);
        btnCancel.setText("取消");
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = mContext.getWindow().getAttributes();
        lp.alpha = bgAlpha; // 0.0-1.0
        mContext.getWindow().setAttributes(lp);
    }
}

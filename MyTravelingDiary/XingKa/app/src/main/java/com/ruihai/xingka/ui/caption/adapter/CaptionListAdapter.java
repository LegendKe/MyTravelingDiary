package com.ruihai.xingka.ui.caption.adapter;

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
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.ruihai.xingka.api.model.PhotoTopic;
import com.ruihai.xingka.api.model.PraiseItem;
import com.ruihai.xingka.api.model.User;
import com.ruihai.xingka.api.model.XKRepo;
import com.ruihai.xingka.event.OnItemClickListener;
import com.ruihai.xingka.ui.MainActivity;
import com.ruihai.xingka.ui.caption.CaptionDetailActivity;
import com.ruihai.xingka.ui.caption.OfficalCaptionInfoActivity;
import com.ruihai.xingka.ui.caption.PraiseListActivity;
import com.ruihai.xingka.ui.caption.fragment.CaptionListFragment;
import com.ruihai.xingka.ui.common.PhotoPagerActivity;
import com.ruihai.xingka.ui.common.enter.EmojiUtils;
import com.ruihai.xingka.ui.mine.UserProfileActivity;
import com.ruihai.xingka.utils.AppUtility;
import com.ruihai.xingka.utils.QiniuHelper;
import com.ruihai.xingka.utils.Security;
import com.ruihai.xingka.widget.CommentTextView;
import com.ruihai.xingka.widget.ProgressHUD;
import com.ruihai.xingka.widget.flingswipe.SwipeFlingAdapterView;
import com.shizhefei.mvc.IDataAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 图说列表数据适配器,并且可以添加头部和脚部
 */
public class CaptionListAdapter extends RecyclerView.Adapter<CaptionListAdapter.ItemViewHolder> implements IDataAdapter<List<PhotoTopic>> {
    private static final int HEADER_VIEW_TYPE = -1000;
    private static final int FOOTER_VIEW_TYPE = -2000;
    private static final int NORMAL_VIEW_TYPE = -3000;

    public static final String DEFAULT_AVATAR_KEY = "00000000-0000-0000-0000-000000000000";

    private final List<View> mHeaders = new ArrayList<View>();
    private final List<View> mFooters = new ArrayList<View>();

    private String mCurrentAccount; //本人行咖号
    //    private String mAuthorAccount; //图说作者行咖号
    private ClipboardManager clipboard;//粘贴板
    private Context context;
    private Activity mContext;
    private LayoutInflater inflater;
    private List<PhotoTopic> photoTopics = new ArrayList<>();

    public OnItemClickListener mOnItemClickListener;

    private List<CommentItem> commentList;
    private IconicFontTextView view1;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public CaptionListAdapter(Context context, Activity mContext, String mCurrentAccount) {
        super();
        this.mContext = mContext;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.mCurrentAccount = mCurrentAccount;
        this.clipboard = (ClipboardManager)
                mContext.getSystemService(Context.CLIPBOARD_SERVICE);
    }

b    public void addHeader(@NonNull View view) {
        if (view == null) {
            throw new IllegalArgumentException("You can't have a null header!");
        }
        mHeaders.add(view);
    }

    /**
     * Adds a footer view.
     */
    public void addFooter(@NonNull View view) {
        if (view == null) {
            throw new IllegalArgumentException("You can't have a null footer!");
        }
        mFooters.add(view);
    }

    /**
     * Toggles the visibility of the header views.
     */
    public void setHeaderVisibility(boolean shouldShow) {
        for (View header : mHeaders) {
            header.setVisibility(shouldShow ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * Toggles the visibility of the footer views.
     */
    public void setFooterVisibility(boolean shouldShow) {
        for (View footer : mFooters) {
            footer.setVisibility(shouldShow ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * @return the number of headers.
     */
    public int getHeaderCount() {
        return mHeaders.size();
    }

    /**
     * @return the number of footers.
     */
    public int getFooterCount() {
        return mFooters.size();
    }

    /**
     * Gets the indicated header, or null if it doesn't exist.
     */
    public View getHeader(int i) {
        return i < mHeaders.size() ? mHeaders.get(i) : null;
    }

    /**
     * Gets the indicated footer, or null if it doesn't exist.
     */
    public View getFooter(int i) {
        return i < mFooters.size() ? mFooters.get(i) : null;
    }

    private boolean isHeader(int viewType) {
        return viewType >= HEADER_VIEW_TYPE && viewType < (HEADER_VIEW_TYPE + mHeaders.size());
    }

    private boolean isFooter(int viewType) {
        return viewType >= FOOTER_VIEW_TYPE && viewType < (FOOTER_VIEW_TYPE + mFooters.size());
    }

    public boolean hasHeader() {//判断是否有头部
        return mHeaders.size() > 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (position < mHeaders.size()) {
            return HEADER_VIEW_TYPE;

        } else if (position < (mHeaders.size() + photoTopics.size())) {
            return NORMAL_VIEW_TYPE;
        } else {
            return FOOTER_VIEW_TYPE;
        }
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HEADER_VIEW_TYPE) {
            int whichHeader = Math.abs(viewType - HEADER_VIEW_TYPE);
            View headerView = mHeaders.get(whichHeader);
            return new ItemViewHolder(headerView, viewType) {
            };
        } else if (viewType == FOOTER_VIEW_TYPE) {
            int whichFooter = Math.abs(viewType - FOOTER_VIEW_TYPE);
            View footerView = mFooters.get(whichFooter);
            return new ItemViewHolder(footerView, viewType) {
            };

        } else {
            View itemView = inflater.inflate(R.layout.item_caption_content, parent, false);
            view1 = (IconicFontTextView) itemView.findViewById(R.id.tv_more);
            return new ItemViewHolder(itemView, viewType);
        }

    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, final int position) {

        if (position < mHeaders.size()) {
            // Headers don't need anything special

        } else if (position < mHeaders.size() + photoTopics.size()) {
            // This is a real position, not a header or footer. Bind it.
            final PhotoTopic item;
            if (hasHeader()) {
                item = photoTopics.get(position - 1);
            } else {
                item = photoTopics.get(position);

            }
            final Handler mHandler = new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    int lineCount = msg.what;
                    if (lineCount > 4) {
                        holder.moreContentBtn.setVisibility(View.VISIBLE);
                        holder.content.setMaxLines(4);
                        holder.moreContentBtn.setImageResource(R.mipmap.icon_content_down);
                        // holder.moreContentLayout.setVisibility(View.VISIBLE);
                        //item.setHasMore(false);
                    } else {
                        holder.moreContentBtn.setVisibility(View.GONE);
                        // holder.moreContentLayout.setVisibility(View.GONE);
                        //item.setHasMore(true);
                    }
                    if (item.isHasMore()) {
                        holder.content.setMaxLines(holder.content.getLineCount());
                        holder.moreContentBtn.setImageResource(R.mipmap.icon_content_up);
                    }
                    return false;
                }
            });
            // --------------------------- 头像 ---------------------------
            Uri avatarUri = Uri.parse(QiniuHelper.getThumbnail96Url(item.getAvatar()));
//            Log.e("TAG","---->图说首页"+item.getAvatar());
            if (!DEFAULT_AVATAR_KEY.equals(item.getAvatar())) {
                holder.avatar.setImageURI(avatarUri);
            } else {
                holder.avatar.setImageURI(Uri.parse("res:///" + R.mipmap.default_avatar));
            }
            // 用户名(昵称/备注),判断备注是否为空，如果为空，显示nick，如果不为空，则显示remark。
            if (!TextUtils.isEmpty(item.getRemark())) {
                holder.name.setText(item.getRemark());
            } else if (!TextUtils.isEmpty(item.getNick())) {
                holder.name.setText(EmojiUtils.fromStringToEmoji1(item.getNick(), context));
                //holder.name.setText(item.getNick());
            } else { // 显示行咖号
                holder.name.setText(String.valueOf(item.getAccount()));
            }
            // ------------------------  图说作者行咖号  ---------------------------
            final String mAuthorAccount = String.valueOf(item.getAccount());

            if (item.isOffical()) {//显示官方账号
                holder.officalAccount.setVisibility(View.VISIBLE);
            } else
                holder.officalAccount.setVisibility(View.GONE);

            // --------------------------- 发布时间 ---------------------------
            String datetime = item.getAddTime().substring(6, 19);
            long timestamp = Long.valueOf(datetime);
            holder.aftertime.setText(Global.dayToNow(timestamp));

            // --------------------------- 图说内容 ---------------------------
            if (!TextUtils.isEmpty(item.getContent())) {
                holder.content.setVisibility(View.VISIBLE);
                //String content = String.format(Constant.FORMAT_CAPTION_CONTENT, item.getContent());
                //holder.content.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
                holder.content.setText(EmojiUtils.fromStringToEmoji1(item.getContent(), context));

//                if (holder.content.getLineCount() > 4) {
//                    holder.moreContentBtn.setVisibility(View.VISIBLE);
//                    holder.content.setMaxLines(4);
//                    holder.moreContentBtn.setImageResource(R.mipmap.icon_content_down);
//                    // holder.moreContentLayout.setVisibility(View.VISIBLE);
//                    //item.setHasMore(false);
//                } else {
//                    holder.moreContentBtn.setVisibility(View.GONE);
//                    // holder.moreContentLayout.setVisibility(View.GONE);
//                    //item.setHasMore(true);
//                }
//                if (item.isHasMore()) {
//                    holder.content.setMaxLines(holder.content.getLineCount());
//                    holder.moreContentBtn.setImageResource(R.mipmap.icon_content_up);
//                }
                holder.moreContentBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addReadingRecord(item.getpGuid(), String.valueOf(item.getAccount()));
                        int pos = (int) v.getTag();
                        boolean isExpend = photoTopics.get(pos).isHasMore();
                        if (isExpend) {
                            holder.content.setMaxLines(4);
                            holder.moreContentBtn.setImageResource(R.mipmap.icon_content_down);
                            photoTopics.get(pos).setHasMore(false);
                        } else {
                            holder.content.setMaxLines(holder.content.getLineCount());
                            holder.moreContentBtn.setImageResource(R.mipmap.icon_content_up);
                            photoTopics.get(pos).setHasMore(true);
                        }
                    }
                });
//                //holder.content.setText(Html.fromHtml(content));
//                ViewTreeObserver observer = holder.content.getViewTreeObserver(); //textAbstract为TextView控件
//                observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                    @Override
//                    public void onGlobalLayout() {
//                        ViewTreeObserver obs = holder.content.getViewTreeObserver();
//                        obs.removeGlobalOnLayoutListener(this);
//                        if (holder.content.getLineCount() > 4) //判断行数大于多少时改变
//                        {
//                            int lineEndIndex = holder.content.getLayout().getLineEnd(3); //设置第四行打省略号
//                            String text = holder.content.getText().subSequence(0, lineEndIndex - 1) + "...";
//                            holder.content.setText(EmojiUtils.fromStringToEmoji(text, mContext));
//                        } else {
//
//                        }
//                    }
//                });
            } else {
                holder.content.setText("");
                holder.content.setVisibility(View.GONE);
            }

            holder.content.post(new Runnable() {
                @Override
                public void run() {
                    int lineCount = holder.content.getLineCount();
                    mHandler.sendEmptyMessage(lineCount);
                }
            });
            holder.moreContentBtn.setTag(position);
            // item.setLineCount(holder.content.getLineCount());
            holder.content.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    normalListDialogNoTitle(item.getContent(), null, mAuthorAccount, 2, v, position);
                    return false;
                }
            });
            holder.content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (item.isOffical() && !TextUtils.isEmpty(item.getWebUrl())) {
                        OfficalCaptionInfoActivity.launch(mContext, item.getWebUrl(), item.getpGuid());
                    } else {
                        Intent intent = new Intent(mContext, CaptionDetailActivity.class);
                        intent.putExtra("position", position);
                        Log.i("TAG", "--------->非链接");
                        intent.putExtra(CaptionListFragment.KEY_CAPTION_ITEM, item);
                        mContext.startActivityForResult(intent, CaptionListFragment.RESULT_EDIT_CAPTION);
                    }
                }
            });
            // --------------------------- 图说选择地址 ---------------------------
            if (!TextUtils.isEmpty(item.getAddress())) {
                holder.ll_address.setVisibility(View.VISIBLE);
                holder.mAddress.setText(item.getAddress());
            } else {
                holder.ll_address.setVisibility(View.GONE);
            }

            // --------------------------- 评论内容 ---------------------------
            final List<CommentTextView> commentTextViews = new ArrayList<>();
            commentTextViews.add(holder.comment0);
            commentTextViews.add(holder.comment1);''
            commentTextViews.add(holder.comment2);
            commentTextViews.add(holder.comment3);
            commentTextViews.add(holder.comment4);
//            List<CommentItem> commentList = item.getCommentList();
            commentList = item.getCommentList();

            if (commentList.size() <= 0) {
                holder.commentsLayout.setVisibility(View.GONE);
            } else {
                holder.commentsLayout.setVisibility(View.VISIBLE);
            }

//            view1 = holder.more_comments;

            holder.more_comments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, CaptionDetailActivity.class);
                    intent.putExtra("position", position);
                    intent.putExtra(CaptionListFragment.KEY_CAPTION_ITEM, item);
                    mContext.startActivityForResult(intent, CaptionListFragment.RESULT_EDIT_CAPTION);
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
                            Intent intent = new Intent(mContext, CaptionDetailActivity.class);
                            intent.putExtra(CaptionListFragment.KEY_CAPTION_ITEM, item);
                            intent.putExtra(CaptionListFragment.KEY_POP_COMMENT, 1);
                            intent.putExtra(CaptionListFragment.KEY_COMMENT_ITEM, commentItem);
                            mContext.startActivity(intent);
                            //CaptionDetailActivity.launch(mContext, item.getpGuid(), String.valueOf(item.getAccount()));
                        }

                        @Override
                        public void onLongClick(View view) {
                            CommentItem commentItem = (CommentItem) view.getTag();
                            if (mCurrentAccount.equals(commentItem.getReviewUid())) {
                                normalListDialogNoTitle(commentItem.getReviewContent(), commentItem.getGuid(), mAuthorAccount, 1, view, position);
                            } else {
                                normalListDialogNoTitle(commentItem.getReviewContent(), commentItem.getGuid(), mAuthorAccount, 2, view, position);
                            }
                        }
                    });
                } else {
                    commentTextViews.get(i).setVisibility(View.GONE);
                }
            }

            //--------------------------- 照片展示 ---------------------------
//            if (item.getImageList().size() == 1) {
////                if (item.getImageList().size() == 1) {
////                    holder.lineImage.setVisibility(View.GONE);
////                } else {
////                    holder.lineImage.setVisibility(View.VISIBLE);
////                }
//                holder.imagesLayout.setVisibility(View.VISIBLE);
//                holder.swipeViewLayout.setVisibility(View.GONE);
//                List<String> imageKeys = new ArrayList<>();
//                for (ImageItem image : item.getImageList()) {
//                    imageKeys.add(image.imgSrc);
//                }
//                GlideHelper.loadTopicCoverWithKey(imageKeys.get(0), holder.coverImage, null);
//            } else if (item.getImageList().size() > 1) {
            holder.imagesLayout.setVisibility(View.GONE);
            holder.swipeViewLayout.setVisibility(View.VISIBLE);
            holder.swipeView.setVisibility(View.VISIBLE);

            final List<String> originalImageKeys = new ArrayList<>();
            final List<String> imageKeys = new ArrayList<>();
            imageKeys.clear();
            final ImageFlingAdapter swipeAdapter = new ImageFlingAdapter(context, imageKeys);
            holder.swipeView.setAdapter(swipeAdapter);
            for (ImageItem image : item.getImageList()) {
                imageKeys.add(image.imgSrc);
                originalImageKeys.add(image.imgSrc);
            }
            holder.swipeView.removeAllViewsInLayout();
            swipeAdapter.notifyDataSetChanged();

            holder.swipeView.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
                @Override
                public void removeFirstObjectInAdapter() {
                    // this is the simplest way to delete an object from the Adapter (/AdapterView)
                    imageKeys.remove(0);
                    swipeAdapter.notifyDataSetChanged();
                }

                @Override
                public void onLeftCardExit(Object dataObject) {
                    //Do something on the left!
                    //You also have access to the original object.
                    //If you want to use it just cast it (String) dataObject
                    imageKeys.add(imageKeys.size(), (String) dataObject);
                    swipeAdapter.notifyDataSetChanged();
                }

                @Override
                public void onRightCardExit(Object dataObject) {
                    imageKeys.add(imageKeys.size(), (String) dataObject);
                    swipeAdapter.notifyDataSetChanged();
                }

                @Override
                public void onAdapterAboutToEmpty(int itemsInAdapter) {
                    // Ask for more data here
                    swipeAdapter.notifyDataSetChanged();
                }

                @Override
                public void onScroll(float scrollProgressPercent) {

                }
            });

            holder.swipeView.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
                @Override
                public void onItemClicked(int itemPosition, Object dataObject) {
                    addReadingRecord(item.getpGuid(), String.valueOf(item.getAccount()));
                    // 获得图片原本所在图片集的排序位置
                    int position = originalImageKeys.indexOf(dataObject);
                    ArrayList<String> imageUrls = new ArrayList<>();
                    for (String imageKey : originalImageKeys) {
                        imageUrls.add(QiniuHelper.getOriginalWithKey(imageKey));
                    }
                    if (item.isOffical() && !TextUtils.isEmpty(item.getWebUrl())) {
                        OfficalCaptionInfoActivity.launch(mContext, item.getWebUrl(), item.getpGuid());
                    } else {
                        Intent intent = new Intent(context, PhotoPagerActivity.class);
                        intent.putExtra(PhotoPagerActivity.EXTRA_CURRENT_ITEM, position);
                        intent.putExtra(PhotoPagerActivity.EXTRA_PHOTOS, imageUrls);
                        intent.putExtra(PhotoPagerActivity.EXTRA_USERNICK, item.getNick());
                        intent.putExtra(PhotoPagerActivity.EXTRA_TYPE, 1);
                        context.startActivity(intent);
                    }
                }
            });
//            }


            // --------------------------- 点赞 ---------------------------
            if (item.isPraise()) {
                holder.praiseButton.setSelected(true);
                holder.praiseButton.setText("{xk-praise-selected}");

            } else {
                holder.praiseButton.setSelected(false);
                holder.praiseButton.setText("{xk-praise}");

            }

            // --------------------------- 显示点赞人员头像 ---------------------------
            holder.praiseCountText.setText(String.valueOf(item.getPraiseNum()));
            List<PraiseItem> praiseList = item.getPraiseList();
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
                            PraiseListActivity.launch(mContext, item.getpGuid(), String.valueOf(item.getAccount()), 1);
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
                        PraiseItem data = (PraiseItem) v.getTag();
                        String account = String.valueOf(data.getAccount());
                        if (mCurrentAccount.equals(account)) {
                            if (MainActivity.currentTabIndex != 3) {
                                MainActivity.launch(context, 1);
                            }
                        } else {
                            if (data.isAdmin()) {
                                UserProfileActivity.launch(context, account, 1, 1);
                            } else {
                                UserProfileActivity.launch(context, account, 1, 0);
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
            if (item.isCollect()) {
                holder.collectButton.setSelected(true);
                holder.collectButton.setText("{xk-collect-selected}");
            } else {
                holder.collectButton.setSelected(false);
                holder.collectButton.setText("{xk-collect}");
            }

            // --------------------------- 关注 ---------------------------
            if (item.isFriend()) {
                holder.addFollowButton.setVisibility(View.VISIBLE);
                holder.addFollowButton.setSelected(true);
                holder.addFollowButton.setText("已关注");
                holder.addFollowButton.setEnabled(false);
            } else {
                User currentUser = AccountInfo.getInstance().loadAccount();
                if (currentUser != null) {
                    if (item.getAccount() == currentUser.getAccount()) {
                        holder.addFollowButton.setVisibility(View.GONE);
                    } else {
                        holder.addFollowButton.setVisibility(View.VISIBLE);
                        holder.addFollowButton.setSelected(false);
                        holder.addFollowButton.setText(R.string.caption_add_follow);
                        holder.addFollowButton.setEnabled(true);
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

                // 图片
                holder.coverImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickListener.onItemChildClick(v, position);
                    }
                });

                // 头像
                holder.avatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickListener.onItemChildClick(v, position);
                    }
                });
//                holder.content.setOnLongClickListener(new View.OnLongClickListener() {
//                    @Override
//                    public boolean onLongClick(View v) {
//                        mOnItemClickListener.onItemLongClick(v, position);
//                        return false;
//                    }
//                });

            }
        } else {
            // Footers don't need anything special

        }

    }

    @Override
    public int getItemCount() {
        return mHeaders.size() + photoTopics.size() + mFooters.size();
    }


    @Override
    public void notifyDataChanged(List<PhotoTopic> data, boolean isRefresh) {
        if (isRefresh) {
            photoTopics.clear();
        }
        photoTopics.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public List<PhotoTopic> getData() {
        return photoTopics;
    }

    @Override
    public boolean isEmpty() {
        return photoTopics.isEmpty();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.sdv_avatar)
        SimpleDraweeView avatar;
        @BindView(R.id.tv_username)
        TextView name;
        @BindView(R.id.tv_aftertime)
        TextView aftertime;
        @BindView(R.id.tv_content)
        TextView content;
        @BindView(R.id.at_address_layout)
        LinearLayout ll_address;
        @BindView(R.id.tv_address)
        TextView mAddress;
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
        @BindView(R.id.images_layout)
        LinearLayout imagesLayout;
        @BindView(R.id.iv_image)
        ImageView coverImage;
        @BindView(R.id.swipeview_layout)
        LinearLayout swipeViewLayout;
        @BindView(R.id.swipeview)
        SwipeFlingAdapterView swipeView;

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
        ImageView officalAccount;

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
            if (viewType == HEADER_VIEW_TYPE) {


            } else if (viewType == FOOTER_VIEW_TYPE) {

            } else {
                ButterKnife.bind(this, itemView);
            }

        }
    }


    //显示复制和删除的弹框
    private void normalListDialogNoTitle(final String content, final String gUid, final String mAuthorAccount, int type, final View v, final int position) {
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
                        showDelDialog(gUid, mAuthorAccount, v, position);

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
    private void deletePhotoTopicComment(final String gUid, final String mAuthorAccount, final View view, final int position) {
        ProgressHUD.showLoadingMessage(mContext, "正在删除...", false);
        User currentUser = AccountInfo.getInstance().loadAccount();
        String sAccount = Security.aesEncrypt(String.valueOf(currentUser.getAccount()));
//        Log.e("TAG", "--行咖号-->" + currentUser.getAccount() +"-->"+ mCurrentAccount);
        String sPCGuid = Security.aesEncrypt(gUid);
//        Log.e("TAG", "--评论主键ID-->" + gUid);
        String sAuthorAccount = Security.aesEncrypt(mAuthorAccount);
//        Log.e("TAG", "---图说发布人行咖号->" + mAuthorAccount);
        Call<XKRepo> call = ApiModule.apiService().deletePhotoTopicComment(sAccount, sPCGuid, sAuthorAccount);
        call.enqueue(new Callback<XKRepo>() {
            @Override
            public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                XKRepo xkRepo = response.body();
                ProgressHUD.dismiss();
                String message = xkRepo.getMsg();
                if (xkRepo.isSuccess()) {
                    ProgressHUD.showSuccessMessage(mContext, "删除成功");
                    view.setVisibility(View.GONE);
//                    view1.setVisibility(commentList.size() == 0 ? View.GONE : View.VISIBLE);
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
    private void showDelDialog(final String gUid, final String mAuthorAccount, final View view, final int position) {
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
                deletePhotoTopicComment(gUid, mAuthorAccount, view, position);
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
                //  Logger.d(getString(R.string.common_network_error));
            }
        });
    }

}

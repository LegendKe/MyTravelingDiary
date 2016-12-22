package com.ruihai.xingka.ui.caption.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.flyco.dialog.entity.DialogMenuItem;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.NormalListDialog;
import com.ruihai.iconicfontengine.widget.IconicFontTextView;
import com.ruihai.xingka.Global;
import com.ruihai.xingka.R;
import com.ruihai.xingka.api.ApiModule;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.api.model.CommentItem;
import com.ruihai.xingka.api.model.User;
import com.ruihai.xingka.api.model.XKRepo;
import com.ruihai.xingka.ui.common.enter.EmojiUtils;
import com.ruihai.xingka.utils.AppUtility;
import com.ruihai.xingka.utils.QiniuHelper;
import com.ruihai.xingka.utils.Security;
import com.ruihai.xingka.widget.CommentTextView;
import com.ruihai.xingka.widget.ProgressHUD;
import com.shizhefei.mvc.IDataAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * 评论列表数据适配器
 * <p>
 * Created by zecker on 15/9/23.
 */
public class CommentExListViewAdapter extends BaseExpandableListAdapter implements View.OnClickListener, IDataAdapter<List<CommentItem>> {

    public static final String DEFAULT_AVATAR_KEY = "00000000-0000-0000-0000-000000000000";

    private Context mContext;
    private LayoutInflater mInflater;
    private ClipboardManager clipboard;
    private List<CommentItem> mCommentItemList;
    private String mAuthorAccount;
    private String mUserAccount;
    private int mtype, mPosition;
    private TextView mTextview;
    private int mCurrentGroupPosition = 0;
    private String mMyAccount;

    public OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public CommentExListViewAdapter(Context context, List<CommentItem> commentItemList, String authorAccount, String mAccount, TextView textView) {
        mContext = context;
        mCommentItemList = commentItemList;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        clipboard = (ClipboardManager)
                mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        mAuthorAccount = authorAccount;
        mUserAccount = mAccount;
        mTextview = textView;
        mMyAccount = String.valueOf(AccountInfo.getInstance().loadAccount().getAccount());
    }

    @Override
    public int getGroupCount() {
        return mCommentItemList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        CommentItem commentItem = mCommentItemList.get(groupPosition);
        if (commentItem.getSubCommentItems() == null) {
            return 0;
        } else {
            return commentItem.getSubCommentItems().size();
        }
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mCommentItemList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        CommentItem commentItem = mCommentItemList.get(groupPosition);
        if (commentItem.getSubCommentItems() == null) {
            return null;
        } else {
            return commentItem.getSubCommentItems().get(childPosition);
        }
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        final CommentViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_caption_comment_list, null);
            holder = new CommentViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (CommentViewHolder) convertView.getTag();
        }
        //首条评论上面线条隐藏
        if (groupPosition == 0) {
            holder.view.setVisibility(View.GONE);
        } else {
            holder.view.setVisibility(View.VISIBLE);
        }
        final CommentItem item = mCommentItemList.get(groupPosition);
        // 用户名，判断备注是否为空，如果为空，显示nick，如果不为空，则显示remark。
        if (!TextUtils.isEmpty(item.getReviewURemark())) {
            holder.name.setText(item.getReviewURemark());
        } else if (!TextUtils.isEmpty(item.getReviewUName())) {
            holder.name.setText(item.getReviewUName());
        } else { // 显示行咖号
            holder.name.setText(item.getReviewUid());
        }

        Uri avatarUri = Uri.parse(QiniuHelper.getThumbnail96Url(item.getAvatar()));
        holder.avatar.setImageURI(avatarUri);
        if (!DEFAULT_AVATAR_KEY.equals(item.getAvatar())) {
            holder.avatar.setImageURI(avatarUri);
        }else {
//            holder.mHeadPortrait.setImageURI(Uri.parse(String.valueOf(R.mipmap.default_avatar)));
            holder.avatar.setImageURI(Uri.parse("res:///" + R.mipmap.default_avatar));
        }
        holder.avatar.setTag(item.getReviewUid());
        holder.name.setTag(item.getReviewUid());
        //用户图像添加点击事件
        holder.avatar.setOnClickListener(this);
        //用户姓名添加点击事件
        holder.name.setOnClickListener(this);
        String datetime = item.getAddTime();
        long timestamp = Long.valueOf(datetime);
        holder.datetime.setText(Global.dayToNow(timestamp));
        holder.content.setText(EmojiUtils.fromStringToEmoji(item.getReviewContent(), mContext));
//        Global.MessageParse parse = HtmlContent.parseMessage(item.getReviewContent());
//        MyImageGetter imageGetter = new MyImageGetter(mContext);
//        holder.content.setText(Global.changeHyperlinkColor(parse.text, imageGetter, Global.tagHandler));
        //一级评论的长按删除监听
        holder.content.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {//长按事件
//                holder.content.setBackgroundColor(mContext.getResources().getColor(R.color.gray_press_bg));
                if (mUserAccount.equals(item.getReviewUid())) {
                    normalListDialogNoTitle(((TextView) v).getText().toString(), item.getGuid(), 0, groupPosition, 0, 1);
                } else {
                    normalListDialogNoTitle(((TextView) v).getText().toString(), item.getGuid(), 2, groupPosition, 0, 1);
                }
                return false;
            }
        });

        holder.content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AppUtility.isFastClick()) {
                }
                mOnItemClickListener.onItemReplyClick(view, groupPosition);
            }
        });

        //holder.content.setText(Global.changeHyperlinkColor(parse.text, imageGetter, Global.tagHandler));
        // 点赞
        if (item.isPraise()) {
            holder.praiseView.setSelected(true);
        } else {
            holder.praiseView.setSelected(false);
        }


//        //增加点赞按钮的点击区域
//        holder.linearPraise.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent motionEvent) {
//                if (AppUtility.isFastClick()) {
//                    return false;
//                }
//                mOnItemClickListener.onItemPraiseClick(v, groupPosition);
//                return false;
//            }
//        });

        //点赞
//        holder.praiseView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mOnItemClickListener.onItemPraiseClick(v, groupPosition);
//            }
//        });

        //增加评论按钮的点击区域
//        holder.linearReply.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent motionEvent) {
//                if (AppUtility.isFastClick()) {
//                    return false;
//                }
//                mOnItemClickListener.onItemReplyClick(v, groupPosition);
//                return false;
//            }
//        });

        // 回复
//        holder.replyView.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                mOnItemClickListener.onItemReplyClick(v, groupPosition);
//            }
//        });
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        SubCommentViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_caption_comment_sub_list, null);
            holder = new SubCommentViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (SubCommentViewHolder) convertView.getTag();
        }

        final CommentItem commentItem = mCommentItemList.get(groupPosition);
        if (commentItem.getSubCommentItems() != null) {
            if (isLastChild) {//给最后一项的底部加一个空白布局用来填充
                holder.bottomView.setVisibility(View.VISIBLE);
            }
            final CommentItem subCommentItem = commentItem.getSubCommentItems().get(childPosition);
            holder.comment.setReply(subCommentItem);
            holder.comment.setListener(new CommentTextView.TextBlankClickListener() {
                @Override
                public void onBlankClick(View view) {
                    mOnItemClickListener.onItemReplyToClick(view, groupPosition, childPosition);
                }

                @Override
                public void onLongClick(View view) {//长按复制或者删除
                    if (mUserAccount.equals(subCommentItem.getReviewUid())) {
                        normalListDialogNoTitle(subCommentItem.getReviewContent(), subCommentItem.getGuid(), 1, groupPosition, childPosition, 2);
                    } else {
                        normalListDialogNoTitle(subCommentItem.getReviewContent(), subCommentItem.getGuid(), 2, groupPosition, childPosition, 2);
                    }
                }

            });

            return convertView;
        }
        return null;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.sdv_avatar:
//            case R.id.tv_name:
//                String userId = (String) v.getTag();
//                if(mMyAccount.equals(userId)){
//                    if (MainActivity.currentTabIndex !=3) {
//                        MainActivity.launch(mContext, 1);
//                    }
//                }else {
//
//                    UserProfileActivity.launch(mContext, userId, 1);
//                }
////                UserProfileActivity.launch(mContext, userId);
//                break;
//        }
    }

    @Override
    public void notifyDataChanged(List<CommentItem> commentItems, boolean isRefresh) {
        if (isRefresh) {
            mCommentItemList.clear();
        }
        mCommentItemList.addAll(commentItems);
        notifyDataSetChanged();
    }

    @Override
    public List<CommentItem> getData() {
        return mCommentItemList;
    }

    static class CommentViewHolder {
        @BindView(R.id.sdv_avatar)
        SimpleDraweeView avatar;
        @BindView(R.id.tv_name)
        TextView name;
        @BindView(R.id.tv_datetime)
        TextView datetime;
        @BindView(R.id.tv_content)
        TextView content;
        @BindView(R.id.ifb_praise)
        IconicFontTextView praiseView;
        @BindView(R.id.ifb_reply)
        IconicFontTextView replyView;
//        @BindView(R.id.ll_comment)
//        LinearLayout linearReply;
//        @BindView(R.id.ll_praise)
//        LinearLayout linearPraise;

        @BindView(R.id.view)
        View view;

        CommentViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }
    }

    static class SubCommentViewHolder {
        @BindView(R.id.tv_sub_comment)
        CommentTextView comment;
        @BindView(R.id.bottom_view)
        View bottomView;

        SubCommentViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnItemClickListener {
        void onItemReplyClick(View view, int position);

        void onItemPraiseClick(View view, int position);

        void onItemReplyToClick(View view, int groupPosition, int childPosition);
    }

    //显示复制和删除的弹框
    private void normalListDialogNoTitle(final String content, final String gUid, final int type, final int parentPosition, final int childPosition, final int commentType) {
        final ArrayList<DialogMenuItem> testItems = new ArrayList<>();
//        testItems.add(new DialogMenuItem("复制", R.mipmap.ic_winstyle_copy));
//        testItems.add(new DialogMenuItem("删除", R.mipmap.ic_winstyle_delete));

        if (type == 1 || type == 0) {
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
                        showDelDialog(gUid, parentPosition, childPosition, commentType);
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
    private void deletePhotoTopicComment(final String gUid, final int parentPosition, final int childPosition, final int commentType) {
        ProgressHUD.showLoadingMessage(mContext, "正在删除...", false);
        User currentUser = AccountInfo.getInstance().loadAccount();
        String sAccount = Security.aesEncrypt(String.valueOf(currentUser.getAccount()));
        Log.e("TAG", "--行咖号-->" + sAccount);
        String sPCGuid = Security.aesEncrypt(gUid);
        Log.e("TAG", "--评论主键ID-->" + sPCGuid);
        String sAuthorAccount = Security.aesEncrypt(mAuthorAccount);
        Log.e("TAG", "---图说发布人行咖号->" + sAuthorAccount);

        ApiModule.apiService().deletePhotoTopicComment(sAccount, sPCGuid, sAuthorAccount).enqueue(new Callback<XKRepo>() {


            @Override
            public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                XKRepo xkRepo = response.body();
                ProgressHUD.dismiss();
                String message = xkRepo.getMsg();
                if (xkRepo.isSuccess()) {
                    ProgressHUD.showSuccessMessage(mContext, "删除成功");
                    if (commentType == 1) {//一级评论
                        mCommentItemList.remove(parentPosition);
//                        if (mCommentItemList.isEmpty()) {
//                            mTextview.setVisibility(View.GONE);
//                        }
                        mTextview.setVisibility(mCommentItemList.isEmpty() ? View.GONE : View.VISIBLE);
                    } else if (commentType == 2) {//二级评论
                        mCommentItemList.get(parentPosition).getSubCommentItems().remove(childPosition);
                    }
                    notifyDataSetChanged();
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
    private void showDelDialog(final String gUid, final int parentPosition, final int childPosition, final int commentType) {
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
                deletePhotoTopicComment(gUid, parentPosition, childPosition, commentType);
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

}

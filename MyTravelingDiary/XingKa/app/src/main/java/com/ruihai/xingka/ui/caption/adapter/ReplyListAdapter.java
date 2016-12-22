package com.ruihai.xingka.ui.caption.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyco.dialog.entity.DialogMenuItem;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.NormalListDialog;
import com.ruihai.xingka.R;
import com.ruihai.xingka.api.ApiModule;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.api.model.CommentItem;
import com.ruihai.xingka.api.model.User;
import com.ruihai.xingka.api.model.XKRepo;
import com.ruihai.xingka.utils.Security;
import com.ruihai.xingka.widget.CommentTextView;
import com.ruihai.xingka.widget.ProgressHUD;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mac on 16/3/3.
 */
public class ReplyListAdapter extends BaseAdapter implements View.OnClickListener {

    private Context mContext;
    private LayoutInflater mInflater;
    private ClipboardManager clipboard;
    private List<CommentItem> mCommentItemList;
    private String mAuthorAccount;
    private String mUserAccount;
    private int mtype, mPosition;
    private int mCurrentGroupPosition = 0;
    private int groupPosition;
    private CommentListAdapter.OnItemClickListener mOnItemClickListener;


    public ReplyListAdapter(Context context, List<CommentItem> commentItemList, String authorAccount, String mAccount, int groupPosition) {
        mContext = context;
        mCommentItemList = commentItemList;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        clipboard = (ClipboardManager)
                mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        mAuthorAccount = authorAccount;
        mUserAccount = mAccount;
        this.groupPosition = groupPosition;
    }

    public void setOnItemClickListener(CommentListAdapter.OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public int getCount() {
        return mCommentItemList != null ? mCommentItemList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        SubCommentViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_caption_comment_sub_list, null);
            holder = new SubCommentViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (SubCommentViewHolder) convertView.getTag();
        }

        //final CommentItem commentItem = mCommentItemList.get(groupPosition);
        if (mCommentItemList != null) {
//            if (isLastChild) {//给最后一项的底部加一个空白布局用来填充
//                holder.bottomView.setVisibility(View.VISIBLE);
//            }
            final CommentItem subCommentItem = mCommentItemList.get(position);
            holder.comment.setReply(subCommentItem);
            holder.comment.setListener(new CommentTextView.TextBlankClickListener() {
                @Override
                public void onBlankClick(View view) {
//                    if (AppUtility.isFastClick()) {
//                        return;
//                    }
                    mOnItemClickListener.onItemReplyToClick(view, groupPosition, position);
                }

                @Override
                public void onLongClick(View view) {//长按复制或者删除
                    if (mUserAccount.equals(subCommentItem.getReviewUid())) {
                        normalListDialogNoTitle(subCommentItem.getReviewContent(), subCommentItem.getGuid(), 1, groupPosition, position, 2);
                    } else {
                        normalListDialogNoTitle(subCommentItem.getReviewContent(), subCommentItem.getGuid(), 2, groupPosition, position, 2);
                    }
                }
            });
            return convertView;
        }
        return null;
    }

    @Override
    public void onClick(View v) {

    }

//    public interface OnItemClickListener {
//        void onItemReplyClick(View view, int position);
//
//        void onItemPraiseClick(View view, int position);
//
//        void onItemReplyToClick(View view, int groupPosition, int childPosition);
//    }

    static class SubCommentViewHolder {
        @BindView(R.id.tv_sub_comment)
        CommentTextView comment;
        @BindView(R.id.bottom_view)
        View bottomView;

        SubCommentViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }
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
//                             mTextview.setVisibility(View.GONE);
//                        }
//                        mTextview.setVisibility(mCommentItemList.isEmpty() ? View.GONE : View.VISIBLE);
                    } else if (commentType == 2) {//二级评论
                        mCommentItemList.remove(childPosition);
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

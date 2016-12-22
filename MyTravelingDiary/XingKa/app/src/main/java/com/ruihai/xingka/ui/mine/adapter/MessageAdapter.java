package com.ruihai.xingka.ui.mine.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.ruihai.iconicfontengine.widget.IconicFontTextView;
import com.ruihai.xingka.Global;
import com.ruihai.xingka.R;
import com.ruihai.xingka.api.ApiModule;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.api.model.PushMessage;
import com.ruihai.xingka.api.model.User;
import com.ruihai.xingka.api.model.XKRepo;
import com.ruihai.xingka.ui.MainActivity;
import com.ruihai.xingka.ui.common.enter.EmojiUtils;
import com.ruihai.xingka.ui.mine.UserProfileActivity;
import com.ruihai.xingka.utils.QiniuHelper;
import com.ruihai.xingka.utils.Security;
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
 * 消息适配器
 * Created by apple on 15/9/11.
 */
public class MessageAdapter extends BaseAdapter implements IDataAdapter<List<PushMessage>> {

    public static final String DEFAULT_AVATAR_KEY = "00000000-0000-0000-0000-000000000000";
    private Context mContext;
    private List<PushMessage> mPushMessageList = new ArrayList<>();
    private String mMyAccount;


    public MessageAdapter(Context context) {
        this.mContext = context;
        mMyAccount = String.valueOf(AccountInfo.getInstance().loadAccount().getAccount());
    }

    @Override
    public int getCount() {
        return mPushMessageList.size();
    }

    @Override
    public Object getItem(int position) {
        return mPushMessageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_message, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        final PushMessage item = (PushMessage) getItem(position);

        if (position == 0) {
            holder.lineTop.setVisibility(View.INVISIBLE);
        } else {
            holder.lineTop.setVisibility(View.VISIBLE);
        }

        if (position == mPushMessageList.size() - 1) {
            holder.lineBottom.setVisibility(View.GONE);
        } else {
            holder.lineBottom.setVisibility(View.VISIBLE);
        }

        // icon type
        int pushType = item.getPushType();
        if (pushType == 1 || pushType == 2 || pushType == 21 || pushType == 22) { // 1：发布图说@ 2：评论图说@ 3:发布旅拼@
            holder.typeIcon.setBackgroundResource(R.mipmap.icon_mention);
        } else if (pushType == 3 || pushType == 4 || pushType == 23 || pushType == 24) { // 3：评论图说 4：评论图说下的评论
            holder.typeIcon.setBackgroundResource(R.mipmap.icon_comment);
        } else if (pushType == 5 || pushType == 6 || pushType == 25 || pushType == 26) { //5：图说点赞；6：图说评论点赞; 25:旅拼点赞
            holder.typeIcon.setBackgroundResource(R.mipmap.icon_praise);
        } else if (pushType == 7 || pushType == 27) { // 7：图说收藏 27:旅拼收藏
            holder.typeIcon.setBackgroundResource(R.mipmap.icon_collect);
        } else if (pushType == 8) { // 8：关注
            holder.typeIcon.setBackgroundResource(R.mipmap.icon_attent);
        } else if (pushType == 11) { // 11: 推荐
            holder.typeIcon.setBackgroundResource(R.mipmap.icon_notice);
        }

        // 头像
        Uri avatarUri = Uri.parse(QiniuHelper.getThumbnail96Url(item.getSenderAvatar()));
        if (!DEFAULT_AVATAR_KEY.equals(item.getSenderAvatar())) {
            holder.avatar.setImageURI(avatarUri);
        } else {
            holder.avatar.setImageURI(Uri.parse("res:///" + R.mipmap.default_avatar));
        }
        holder.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = item.getSender();
                if (mMyAccount.equals(account)) {
                    if (MainActivity.currentTabIndex != 3) {
                        MainActivity.launch(mContext, 1);
                    }
                } else if (item.isAdmin()) {
                    UserProfileActivity.launch(mContext, account, 1, 1);
                } else {
                    UserProfileActivity.launch(mContext, account, 1, 0);
                }
            }
        });

        // 时间
        String datetime = item.getSendTime().substring(6, 19);
        long timestamp = Long.valueOf(datetime);
        holder.datetime.setText(Global.dayToNow(timestamp));

        // 昵称
        holder.mNick.setText(item.getSenderNick());
        holder.mNick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = item.getSender();
                if (mMyAccount.equals(account)) {
                    if (MainActivity.currentTabIndex != 3) {
                        MainActivity.launch(mContext, 1);
                    }
                } else if (item.isAdmin()) {
                    UserProfileActivity.launch(mContext, account, 1, 1);
                } else {
                    UserProfileActivity.launch(mContext, account, 1, 0);
                }
            }
        });

        // 图片
        if (!TextUtils.isEmpty(item.getpImage())) {
            holder.picture.setVisibility(View.VISIBLE);
            Uri picUri = Uri.parse(QiniuHelper.getThumbnail96Url(item.getpImage()));
            holder.picture.setImageURI(picUri);
        }

        // content & summary
        if (pushType == 1) { //发布图说@
            holder.summary.setVisibility(View.GONE);
            holder.content.setText("发布的图说中提到了你");
            holder.picture.setVisibility(View.VISIBLE);
            holder.btnAddFollow.setVisibility(View.GONE);
        } else if (pushType == 2) { //发表评论@
            holder.summary.setVisibility(View.GONE);
            holder.content.setText("发表的图说评论中提到了你");
            holder.picture.setVisibility(View.VISIBLE);
            holder.btnAddFollow.setVisibility(View.GONE);
        } else if (pushType == 3) { //评论
            holder.summary.setVisibility(View.VISIBLE);
            holder.content.setText("评论了你的图说");
            holder.summary.setText(EmojiUtils.fromStringToEmoji(item.getPcContent(), mContext));
            //holder.summary.setText(item.getPcContent());
            holder.picture.setVisibility(View.VISIBLE);
            holder.btnAddFollow.setVisibility(View.GONE);
        } else if (pushType == 4) { //评论回复
            holder.summary.setVisibility(View.VISIBLE);
            holder.content.setText("回复了你的评论");
            holder.summary.setText(EmojiUtils.fromStringToEmoji(item.getPcContent(), mContext));
            //holder.summary.setText(item.getPcContent());
            holder.picture.setVisibility(View.VISIBLE);
            holder.btnAddFollow.setVisibility(View.GONE);
        } else if (pushType == 5) { //图说点赞
            holder.summary.setVisibility(View.GONE);
            holder.content.setText("赞了你的图说");
            holder.picture.setVisibility(View.VISIBLE);
            holder.btnAddFollow.setVisibility(View.GONE);
        } else if (pushType == 6) { // 6.图说评论点赞
            holder.summary.setVisibility(View.GONE);
            holder.content.setText("赞了你的评论");
            holder.picture.setVisibility(View.VISIBLE);
            holder.btnAddFollow.setVisibility(View.GONE);
        } else if (pushType == 7) { // 图说收藏
            holder.summary.setVisibility(View.GONE);
            holder.content.setText("收藏了你的图说");
            holder.picture.setVisibility(View.VISIBLE);
            holder.btnAddFollow.setVisibility(View.GONE);
        } else if (pushType == 8) { // 8.关注
            holder.summary.setVisibility(View.GONE);
            holder.content.setText("关注了你");
            holder.picture.setVisibility(View.GONE);
            holder.btnAddFollow.setVisibility(View.VISIBLE);
            if (item.isFriend()) {
                holder.btnAddFollow.setSelected(true);
                holder.btnAddFollow.setText("已关注");
                holder.btnAddFollow.setEnabled(false);
            } else {
                holder.btnAddFollow.setSelected(false);
                holder.btnAddFollow.setText("关注TA");
                holder.btnAddFollow.setEnabled(true);
            }
        } else if (pushType == 11) {  //推荐
            holder.summary.setVisibility(View.GONE);
            holder.content.setText("推荐了你");
            holder.picture.setVisibility(View.VISIBLE);
            holder.btnAddFollow.setVisibility(View.GONE);
        } else if (pushType == 21) { //发布旅拼@
            holder.summary.setVisibility(View.GONE);
            holder.content.setText("发布的旅拼中提到了你");
            holder.picture.setVisibility(View.VISIBLE);
            holder.btnAddFollow.setVisibility(View.GONE);
        } else if (pushType == 22) { //发表旅拼评论@
            holder.summary.setVisibility(View.GONE);
            holder.content.setText("发表的旅拼评论中提到了你");
            holder.picture.setVisibility(View.VISIBLE);
            holder.btnAddFollow.setVisibility(View.GONE);
        } else if (pushType == 23) { //旅拼评论
            holder.summary.setVisibility(View.VISIBLE);
            holder.content.setText("评论了你的旅拼");
            holder.summary.setText(EmojiUtils.fromStringToEmoji(item.getPcContent(), mContext));
            holder.picture.setVisibility(View.VISIBLE);
            holder.btnAddFollow.setVisibility(View.GONE);
        } else if (pushType == 24) { //评论回复
            holder.summary.setVisibility(View.VISIBLE);
            holder.content.setText("回复了你的评论");
            holder.summary.setText(EmojiUtils.fromStringToEmoji(item.getPcContent(), mContext));
            holder.picture.setVisibility(View.VISIBLE);
            holder.btnAddFollow.setVisibility(View.GONE);
        } else if (pushType == 25) { //旅拼点赞
            holder.summary.setVisibility(View.GONE);
            holder.content.setText("赞了你的旅拼");
            holder.picture.setVisibility(View.VISIBLE);
            holder.btnAddFollow.setVisibility(View.GONE);
        } else if (pushType == 26) { //点赞旅拼评论
            holder.summary.setVisibility(View.GONE);
            holder.content.setText("赞了你的评论");
            holder.picture.setVisibility(View.VISIBLE);
            holder.btnAddFollow.setVisibility(View.GONE);
        } else if (pushType == 27) { //旅拼收藏
            holder.summary.setVisibility(View.GONE);
            holder.content.setText("收藏了你的旅拼");
            holder.picture.setVisibility(View.VISIBLE);
            holder.btnAddFollow.setVisibility(View.GONE);
        }
        // 添加对粉丝关注
        holder.btnAddFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User currentUser = AccountInfo.getInstance().loadAccount();
                String sAccount = Security.aesEncrypt(String.valueOf(currentUser.getAccount()));
                String sFriendAccount = Security.aesEncrypt(item.getSender());
                item.setIsFriend(true);
                holder.btnAddFollow.setClickable(false);
                notifyDataSetChanged();

                ApiModule.apiService().addFriend(sAccount, sFriendAccount).enqueue(new Callback<XKRepo>() {
                    @Override
                    public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                        XKRepo xkRepo = response.body();
                        if (xkRepo.isSuccess()) {
//                            item.setIsFriend(true);
//                            notifyDataSetChanged();
                        } else {
                            ProgressHUD.showInfoMessage(mContext, xkRepo.getMsg());
                        }
                    }

                    @Override
                    public void onFailure(Call<XKRepo> call, Throwable t) {
                        // ProgressHUD.showErrorMessage(mContext, mContext.getString(R.string.common_network_error));
                        item.setIsFriend(false);
                        holder.btnAddFollow.setClickable(true);
                        notifyDataSetChanged();
                    }

                });
            }
        });

        return convertView;
    }

    class ViewHolder {
        @BindView(R.id.iv_type_icon)
        ImageView typeIcon;
        @BindView(R.id.timeline_top)
        View lineTop;
        @BindView(R.id.timeline_bottom)
        View lineBottom;
        @BindView(R.id.tv_datetime)
        TextView datetime;
        @BindView(R.id.sdv_avatar)
        SimpleDraweeView avatar;
        @BindView(R.id.tv_nick)
        TextView mNick;
        @BindView(R.id.iv_picture)
        SimpleDraweeView picture;
        @BindView(R.id.tv_content)
        TextView content;
        @BindView(R.id.tv_summary)
        TextView summary;
        @BindView(R.id.btn_add_follow)
        IconicFontTextView btnAddFollow;

        public ViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public void notifyDataChanged(List<PushMessage> pushMessages, boolean isRefresh) {
        if (isRefresh) {
            mPushMessageList.clear();
        }
        mPushMessageList.addAll(pushMessages);
        notifyDataSetChanged();
    }

    @Override
    public List<PushMessage> getData() {
        return mPushMessageList;
    }
}

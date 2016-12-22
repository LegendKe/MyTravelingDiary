package com.ruihai.xingka.ui.mine.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.ruihai.iconicfontengine.widget.IconicFontTextView;
import com.ruihai.xingka.R;
import com.ruihai.xingka.api.ApiModule;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.api.model.SearchUser;
import com.ruihai.xingka.api.model.XKRepo;
import com.ruihai.xingka.utils.AppUtility;
import com.ruihai.xingka.utils.QiniuHelper;
import com.ruihai.xingka.utils.Security;
import com.ruihai.xingka.widget.ProgressHUD;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lqfang on 15/9/25.
 */
public class FriendAdapter extends BaseAdapter {
    List<SearchUser> searchUsers = new ArrayList<SearchUser>();
    Context context;
    private int mMyAccount;

    public static final String DEFAULT_AVATAR_KEY = "00000000-0000-0000-0000-000000000000";

    public FriendAdapter(Context context, List<SearchUser> searchUsers) {
        this.context = context;
        this.searchUsers = searchUsers;
        mMyAccount = AccountInfo.getInstance().loadAccount().getAccount();
    }

    @Override
    public int getCount() {
        if (null != searchUsers) {
            return searchUsers.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return searchUsers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_friend, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        Uri imageUri = Uri.parse(QiniuHelper.getThumbnail96Url(searchUsers.get(position).getAvatar()));
        if (!DEFAULT_AVATAR_KEY.equals(searchUsers.get(position).getAvatar())) {
            holder.mHeadPortrait.setImageURI(imageUri);
        } else {
            holder.mHeadPortrait.setImageURI(Uri.parse("res:///" + R.mipmap.default_avatar));
        }

        holder.mNickName.setText(searchUsers.get(position).getNick());

        holder.mVip.setVisibility(View.GONE);

        if (searchUsers.get(position).getAccount() == mMyAccount) {
            holder.mAttentionTv.setVisibility(View.GONE);
        } else {
            holder.mAttentionTv.setVisibility(View.VISIBLE);
            if (searchUsers.get(position).isFriend()) {
                holder.mAttentionTv.setText("已关注");
                holder.mAttentionTv.setSelected(true);
            } else {
                holder.mAttentionTv.setText(R.string.caption_add_follow);
                holder.mAttentionTv.setSelected(false);
                holder.mAttentionTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        holder.mAttentionTv.setText("已关注");
                        holder.mAttentionTv.setSelected(true);
                        holder.mAttentionTv.setClickable(false);
                        searchUsers.get(position).setIsFriend(true);
                        onAddFriend(position, holder);

                    }
                });
            }
        }
        return convertView;
    }

    private void onAddFriend(final int position, final ViewHolder holder) {
        ApiModule.apiService().addFriend(Security.aesEncrypt(mMyAccount + ""),
                Security.aesEncrypt(searchUsers.get(position).getAccount() + "")).enqueue(new Callback<XKRepo>() {
            @Override
            public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                XKRepo xkRepo = response.body();
                String msg = xkRepo.getMsg();
                if (xkRepo.isSuccess()) { // 成功
                    AppUtility.showToast(msg);
//                                            AppUtility.showToast(msg);
//                                            IconicFontTextView attention= (IconicFontTextView) v.findViewById(R.id.tv_attention);
//                                            attention.setText("已关注");
//                                            attention.setSelected(true);
                } else { // 失败
                    ProgressHUD.showErrorMessage(context, msg);

                }
            }

            @Override
            public void onFailure(Call<XKRepo> call, Throwable t) {
                ProgressHUD.showErrorMessage(context, t.getLocalizedMessage());
                holder.mAttentionTv.setText(R.string.caption_add_follow);
                holder.mAttentionTv.setSelected(false);
                holder.mAttentionTv.setClickable(true);
                searchUsers.get(position).setIsFriend(false);
            }

        });
    }

    class ViewHolder {
        @BindView(R.id.iv_head_portrait)
        SimpleDraweeView mHeadPortrait;
        @BindView(R.id.tv_nickname)
        TextView mNickName;
        @BindView(R.id.iv_vip)
        ImageView mVip;
        @BindView(R.id.tv_attention)
        IconicFontTextView mAttentionTv;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

    }
}

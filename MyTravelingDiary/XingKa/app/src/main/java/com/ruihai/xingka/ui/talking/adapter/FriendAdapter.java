package com.ruihai.xingka.ui.talking.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.orhanobut.hawk.Hawk;
import com.ruihai.iconicfontengine.widget.IconicFontTextView;
import com.ruihai.xingka.R;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.ui.mine.UserProfileActivity;
import com.ruihai.xingka.utils.AppUtility;
import com.ruihai.xingka.utils.QiniuHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 通讯录好友
 * Created by lqfang on 16/8/13.
 */
public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {

    private Context context;
    private List<NimUserInfo> data;

    public FriendAdapter(Context context, List<NimUserInfo> users) {
        this.context = context;
        this.data = users;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_new_friends, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //隐藏同意和拒绝按钮
        holder.agree.setVisibility(View.GONE);
        holder.refuse.setVisibility(View.GONE);



        String avatar = data.get(position).getAvatar();
        String nickname = data.get(position).getName();
        final String account = data.get(position).getAccount();
//        String remark = data.get(position).getSignature();
//        Log.e("TAG","remark-->"+remark);
        // 头像
        Uri imageUri = Uri.parse(QiniuHelper.getThumbnail96Url(avatar));
        holder.avatar.setImageURI(imageUri);
        // 判断若有备注显示备注,没有显示昵称
//        if(!TextUtils.isEmpty(remark)){
//            holder.name.setText(remark);
//        }else {
//            holder.name.setText(nickname);
//        }
        holder.name.setText(nickname);

        //点击头像跳转到个人名片
        holder.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserProfileActivity.launch(context, account, 1, 0);
            }
        });
        //
        holder.layout_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtility.showToast("进入聊天页面");
            }
        });



    }

    @Override
    public int getItemCount() {
        return data.size();
    }

//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    public void clear() {
//        int size = data.size();
//        data.clear();
//        notifyItemRangeRemoved(0, size);
//    }
//
//    public void update( List<RecentContact> newData) {
//        clear();
//        data.addAll(newData);
//        notifyDataSetChanged();
//    }

//    @Override
//    public Object[] getSections() {
//        return new Object[0];
//    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.layout_item)
        LinearLayout layout_item;
        @BindView(R.id.sdv_avatar)
        SimpleDraweeView avatar;
        @BindView(R.id.tv_nick)
        TextView name;
        @BindView(R.id.tv_describe)
        TextView describe;
        @BindView(R.id.btn_agree)
        IconicFontTextView agree;
        @BindView(R.id.btn_refused)
        IconicFontTextView refuse;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

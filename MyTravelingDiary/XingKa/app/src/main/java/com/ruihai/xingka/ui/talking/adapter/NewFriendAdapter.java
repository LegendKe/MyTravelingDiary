package com.ruihai.xingka.ui.talking.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.netease.nimlib.sdk.msg.model.SystemMessage;
import com.ruihai.iconicfontengine.widget.IconicFontTextView;
import com.ruihai.xingka.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lqfang on 16/8/6.
 */
public class NewFriendAdapter extends RecyclerView.Adapter<NewFriendAdapter.ViewHolder>{

    Context context;
    List<SystemMessage> data;

    public NewFriendAdapter(Context context, List<SystemMessage> items) {
        this.context = context;
        this.data = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_new_friends, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        String account = data.get(position).getFromAccount();
        String content = data.get(position).getContent();

        // 头像
//        Uri imageUri = Uri.parse(QiniuHelper.getThumbnail96Url(mCurrentUser.getAvatar()));
//        holder.avatar.setImageURI(imageUri);

        // 名称
        holder.name.setText("高圆圆");
        // 描述
        if (TextUtils.isEmpty(content)) {
            holder.describe.setVisibility(View.GONE);
        } else {
            holder.describe.setText(content);
        }
        holder.describe.setVisibility(View.VISIBLE);
        holder.describe.setText("已经将你添加为好友");

        //点击同意
        holder.agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.agree.setText("已同意");
                holder.agree.setEnabled(false);
                holder.agree.setSelected(false);
                holder.refuse.setVisibility(View.GONE);
            }
        });


    }

    @Override
    public int getItemCount() {
        return 0;
    }




    static class ViewHolder extends RecyclerView.ViewHolder {
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

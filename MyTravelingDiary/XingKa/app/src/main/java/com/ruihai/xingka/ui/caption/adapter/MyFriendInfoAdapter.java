package com.ruihai.xingka.ui.caption.adapter;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.ruihai.xingka.R;
import com.ruihai.xingka.api.model.MyFriendInfoRepo.MyFriendInfo;
import com.ruihai.xingka.utils.QiniuHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zecker on 15/9/14.
 */
public class MyFriendInfoAdapter extends RecyclerView.Adapter<MyFriendInfoAdapter.ViewHolder> {

    public static final String DEFAULT_AVATAR_KEY = "00000000-0000-0000-0000-000000000000";

    private List<MyFriendInfo> mData;

    public MyFriendInfoAdapter(List<MyFriendInfo> data) {
        this.mData = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_users_list, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        MyFriendInfo info = mData.get(position);

        // Avatar
        Uri imageUri = Uri.parse(QiniuHelper.getThumbnail96Url(info.getAvatar()));
        if (!DEFAULT_AVATAR_KEY.equals(info.getAvatar())) {
            holder.avatar.setImageURI(imageUri);
        }
//        }else {
////            holder.avatar.setImageURI(Uri.parse(String.valueOf(R.mipmap.default_avatar)));
//            holder.avatar.setImageURI(Uri.parse("res:///" + R.mipmap.default_avatar));
//        }
        // 名称
        if (!TextUtils.isEmpty(info.getRemark())) {
            holder.name.setText(info.getRemark());
        } else if (!TextUtils.isEmpty(info.getNick())) {
            holder.name.setText(info.getNick());
        } else {
            holder.name.setText(String.valueOf(info.getAccount()));
        }
        holder.dividerLine.setVisibility(View.VISIBLE);
        holder.checkBox.setChecked(info.isSelected());
        holder.checkBox.setTag(info);
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                MyFriendInfo item = (MyFriendInfo) cb.getTag();
                item.setSelected(cb.isChecked());
                mData.get(position).setSelected(cb.isChecked());
            }

        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void clear() {
        int size = mData.size();
        mData.clear();
        notifyItemRangeRemoved(0, size);
    }

    public void update(List<MyFriendInfo> newData) {
        clear();
        mData.addAll(newData);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_avatar)
        SimpleDraweeView avatar;
        @BindView(R.id.tv_name)
        TextView name;
        @BindView(R.id.cb_selected)
        CheckBox checkBox;
        @BindView(R.id.divide_line1)
        View dividerLine;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public List<MyFriendInfo> getMyFriendInfos() {
        return this.mData;
    }
}

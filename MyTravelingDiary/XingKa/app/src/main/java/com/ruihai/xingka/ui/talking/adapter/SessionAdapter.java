package com.ruihai.xingka.ui.talking.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.ruihai.xingka.R;
import com.ruihai.xingka.utils.QiniuHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *
 * Created by lqfang on 16/8/10.
 */
public class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.ViewHolder>{

    private Context context;
    private  List<RecentContact> data;

    public SessionAdapter(List<RecentContact> data){
//        this.context = context;
        this.data = data;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_session, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {


        // 头像
//        Uri imageUri = Uri.parse(QiniuHelper.getThumbnail96Url(brand.getImage()));
//        holder.avatar.setImageURI(imageUri);
        // 名称
//        holder.name.setText();
        //最近会话
//        holder.message.setText();

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
        @BindView(R.id.sdv_avatar)
        SimpleDraweeView avatar;
        @BindView(R.id.tv_name)
        TextView name;
        @BindView(R.id.message)
        TextView message;
        @BindView(R.id.rl_num_bg)
        RelativeLayout rl_num_bg;
        @BindView(R.id.tv_message_num)
        TextView message_num;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

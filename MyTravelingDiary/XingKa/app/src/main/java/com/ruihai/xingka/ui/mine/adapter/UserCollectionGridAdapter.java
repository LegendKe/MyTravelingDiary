package com.ruihai.xingka.ui.mine.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ruihai.xingka.R;
import com.ruihai.xingka.api.model.UserCollection;
import com.ruihai.xingka.utils.glide.GlideHelper;
import com.shizhefei.mvc.IDataAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zecker on 15/11/11.
 */
public class UserCollectionGridAdapter extends BaseAdapter implements IDataAdapter<List<UserCollection>> {

    private Context context;
    private LayoutInflater inflater;
    private List<UserCollection> photoTopics = new ArrayList<>();
    private String mUserAccount;

    public UserCollectionGridAdapter(Context context, String userAccount) {
        super();
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.mUserAccount = userAccount;
    }

    @Override
    public int getCount() {
        return photoTopics.size();
    }

    @Override
    public Object getItem(int position) {
        return photoTopics.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(R.layout.item_user_collection, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        UserCollection photoTopic = photoTopics.get(position);

        int imageCount = photoTopic.getImgNum();
        holder.amount.setText(String.valueOf(imageCount));
        // 图片显示
        if (imageCount == 0) {
            //holder.cover.setBackgroundResource(R.mipmap.icon_caption_error);
            GlideHelper.loadResource(R.mipmap.icon_caption_error, holder.cover);
        } else {
            GlideHelper.loadThumbImageWithKey(photoTopic.getFirstImg(), holder.cover);
        }

        return convertView;
    }

    @Override
    public void notifyDataChanged(List<UserCollection> data, boolean isRefresh) {
        if (isRefresh) {
            photoTopics.clear();
        }
        photoTopics.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public List<UserCollection> getData() {
        return photoTopics;
    }

    @Override
    public boolean isEmpty() {
        return photoTopics.isEmpty();
    }

    static class ViewHolder {
        @BindView(R.id.iv_cover)
        ImageView cover;
        @BindView(R.id.tv_amount)
        TextView amount;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

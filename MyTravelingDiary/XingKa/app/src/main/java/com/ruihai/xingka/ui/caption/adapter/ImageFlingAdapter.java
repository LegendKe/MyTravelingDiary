package com.ruihai.xingka.ui.caption.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.ruihai.xingka.R;
import com.ruihai.xingka.utils.QiniuHelper;
import com.ruihai.xingka.utils.glide.GlideHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zecker on 15/9/29.
 */
public class ImageFlingAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<String> mData;

    public ImageFlingAdapter(Context context, List<String> mData) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = mData;
    }

    public void addData(List<String> data) {
        if (mData != null) {
            mData.clear();
            mData.addAll(data);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public String getItem(int position) {
        return mData.get(position);
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
            convertView = mInflater.inflate(R.layout.item_caption_swipe_content_view, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        holder.imageView.setTag(R.id.image_tag, position);
        GlideHelper.loadTopicCoverWithKey(mData.get(position), holder.imageView, new GlideHelper.ImageLoadingListener() {
            @Override
            public void onLoaded() {

            }

            @Override
            public void onFailed() {

            }
        });

        return convertView;
    }

    class ViewHolder {
        @BindView(R.id.iv_image)
        ImageView imageView;

        public ViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }
    }
}

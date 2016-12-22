package com.ruihai.xingka.ui.trackway.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ruihai.xingka.R;
import com.ruihai.xingka.api.model.TrackwayCollection;
import com.ruihai.xingka.utils.glide.GlideHelper;
import com.shizhefei.mvc.IDataAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mac on 16/5/19.
 */
public class TrackwayCollectionGridAdapter extends BaseAdapter implements IDataAdapter<List<TrackwayCollection>> {

    private Context context;
    private LayoutInflater inflater;
    private List<TrackwayCollection> trackwayCollections = new ArrayList<>();
    private String mUserAccount;

    public TrackwayCollectionGridAdapter(Context context, String userAccount) {
        super();
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.mUserAccount = userAccount;
    }

    @Override
    public int getCount() {
        return trackwayCollections.size();
    }

    @Override
    public Object getItem(int position) {
        return trackwayCollections.get(position);
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

        TrackwayCollection trackwayCollection = trackwayCollections.get(position);

        int imageCount = trackwayCollection.getImgNum();
        holder.amount.setText(String.valueOf(imageCount));
        // 图片显示
        if (imageCount == 0) {
            //holder.cover.setBackgroundResource(R.mipmap.icon_caption_error);
            GlideHelper.loadResource(R.mipmap.icon_caption_error, holder.cover);
        } else {
            GlideHelper.loadThumbImageWithKey(trackwayCollection.getFirstImg(), holder.cover);
        }

        return convertView;
    }

    @Override
    public void notifyDataChanged(List<TrackwayCollection> data, boolean isRefresh) {
        if (isRefresh) {
            trackwayCollections.clear();
        }
        trackwayCollections.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public List<TrackwayCollection> getData() {
        return trackwayCollections;
    }

    @Override
    public boolean isEmpty() {
        return trackwayCollections.isEmpty();
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

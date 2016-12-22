package com.ruihai.xingka.ui.caption.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.ruihai.xingka.R;
import com.ruihai.xingka.api.model.ImageItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zecker on 15/9/24.
 */
public class SwipePhotoAdapter extends ArrayAdapter<ImageItem> {

    private LayoutInflater mInflater;
    private int resourceId;

    public SwipePhotoAdapter(Context context, int resourceId, List<ImageItem> imageItems) {
        super(context, resourceId, imageItems);

        resourceId = resourceId;
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(resourceId, null, false);
            holder = new ViewHolder(convertView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ImageItem imageItem = (ImageItem)getItem(position);


        return convertView;
    }

    static class ViewHolder {

        @BindView(R.id.imageview)
        ImageView photo;

        ViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }
    }
}

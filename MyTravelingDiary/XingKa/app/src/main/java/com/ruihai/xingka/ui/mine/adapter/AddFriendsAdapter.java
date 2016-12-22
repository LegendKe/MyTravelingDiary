package com.ruihai.xingka.ui.mine.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ruihai.xingka.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by apple on 15/9/1.
 */
public class AddFriendsAdapter extends BaseAdapter {

    private Context context;
    String[] option;
    int[] imgs;
    int status;

    public AddFriendsAdapter(Context context, String[] option, int[] imgs) {
        this.context = context;
        this.option = option;
        this.imgs = imgs;
    }

    @Override
    public int getCount() {
        if (null != option) {
            return option.length;
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return option[position];
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
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_add_friends, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        holder.mData.setText(option[position]);
        holder.mTips.setVisibility(View.VISIBLE);
        holder.mTips.setImageResource(imgs[position]);
        return convertView;

    }

    class ViewHolder {

        @BindView(R.id.iv_picture_tips)
        ImageView mTips;
        @BindView(R.id.tv_data)
        TextView mData;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

    }

}

package com.ruihai.xingka.ui.mine.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ruihai.iconicfontengine.widget.IconicFontTextView;
import com.ruihai.xingka.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mac on 16/4/5.
 */
public class MyUserThirdAdapter extends BaseAdapter {
    private Context context;
    String[] option;
    String[] imgs;

    public MyUserThirdAdapter(Context context, String[] option, String[] imgs) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_myuser, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        holder.mData.setText(option[position]);
        holder.mMessage.setVisibility(View.VISIBLE);
        holder.mMessage.setText(imgs[position]);
        holder.mChange.setVisibility(View.VISIBLE);
        return convertView;
    }

    class ViewHolder {
        @BindView(R.id.tv_message)
        IconicFontTextView mMessage;
        @BindView(R.id.tv_data)
        TextView mData;
        @BindView(R.id.tv_change)
        IconicFontTextView mChange;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

    }
}

package com.ruihai.xingka.ui.mine.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ruihai.iconicfontengine.widget.IconicFontTextView;
import com.ruihai.xingka.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mac on 16/7/11.
 */
public class MyUserFivthAdapter extends BaseAdapter {

    private Context context;
    String[] option;
    String[] imgs;
    int num;

    public MyUserFivthAdapter(Context context, String[] option, String[] imgs, int num) {
        this.context = context;
        this.option = option;
        this.imgs = imgs;
        this.num = num;
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

        if (position == 0) { //意见反馈
            if (num > 0) {
                holder.mChange.setTextColor(context.getResources().getColor(R.color.orange));
            } else {
                holder.mChange.setTextColor(context.getResources().getColor(R.color.grey_back));
            }
            holder.mMessage.setVisibility(View.GONE);
            holder.img.setVisibility(View.VISIBLE);
            holder.img.setImageResource(R.mipmap.xk_feedback);

        } else if (position == 1) { //找咖友

        } else { //设置

        }

        return convertView;
    }


    class ViewHolder {
        @BindView(R.id.img)
        ImageView img;
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

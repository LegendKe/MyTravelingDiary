package com.ruihai.xingka.ui.talking.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ruihai.iconicfontengine.widget.IconicFontTextView;
import com.ruihai.xingka.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 好友
 * Created by lqfang on 16/8/5.
 */
public class FirstFriendAdapter extends BaseAdapter{

    private Context context;
    String[] option;
    int[] imgs;

    public FirstFriendAdapter(Context context, String[] option, int[] imgs) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_first_friend, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        holder.mData.setText(option[position]);
        holder.mImageView.setVisibility(View.VISIBLE);
        holder.mImageView.setImageResource(imgs[position]);
        if(position == 0){
            holder.mChange.setVisibility(View.VISIBLE);
            holder.relativeLayout.setVisibility(View.GONE);
//            holder.relativeLayout.setVisibility(View.VISIBLE);
//            holder.messageNum.setText("1");
        }else if (position == 1){
            holder.mChange.setVisibility(View.GONE);
        }


//        holder.mChange.setTextColor(Color.parseColor("#ff7800"));
//        holder.mChange.setTextColor(Color.parseColor("#bcbcc4"));

        return convertView;
    }

    class ViewHolder {
        @BindView(R.id.img)
        ImageView mImageView;
        @BindView(R.id.tv_data)
        TextView mData;
        @BindView(R.id.tv_change)
        IconicFontTextView mChange;
        @BindView(R.id.rl_num_bg)
        RelativeLayout relativeLayout;
        @BindView(R.id.tv_message_num)
        TextView messageNum;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

    }
}

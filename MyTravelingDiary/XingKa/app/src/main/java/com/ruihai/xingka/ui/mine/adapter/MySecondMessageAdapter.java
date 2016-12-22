package com.ruihai.xingka.ui.mine.adapter;

import android.content.Context;
import android.graphics.Color;
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
 * Created by mac on 16/4/5.
 */
public class MySecondMessageAdapter extends BaseAdapter {

    private Context context;
    String[] option;
    int[] imgs;

    private int commentNum, praiseNum, collectNum, focusNum;

    public MySecondMessageAdapter(Context context, String[] option, int[] imgs, int commentNum, int praiseNum, int collectNum, int focusNum) {
        this.context = context;
        this.option = option;
        this.imgs = imgs;
        this.commentNum = commentNum;
        this.collectNum = collectNum;
        this.praiseNum = praiseNum;
        this.focusNum = focusNum;
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
        holder.mImage.setVisibility(View.VISIBLE);
        holder.mImage.setImageResource(imgs[position]);
        holder.mChange.setVisibility(View.VISIBLE);
        // 0:评论 1:点赞 2:收藏 3:关注
        if (position == 0) {
            if (commentNum > 0) {
                //未读消息大于0图标变橙色
                holder.mChange.setTextColor(Color.parseColor("#ff7800"));
            } else {
                //未读消息等于于0图标变灰色
                holder.mChange.setTextColor(Color.parseColor("#b4b4bc"));
            }
        } else if (position == 1) {
            if (praiseNum > 0) { //点赞
                holder.mChange.setTextColor(Color.parseColor("#ff7800"));
            } else {
                holder.mChange.setTextColor(Color.parseColor("#b4b4bc"));
            }
        } else if (position == 2) {
            if (collectNum > 0) { //收藏
                holder.mChange.setTextColor(Color.parseColor("#ff7800"));
            } else {
                holder.mChange.setTextColor(Color.parseColor("#b4b4bc"));
            }
        } else if (position == 3) {
            if (focusNum > 0) { //关注
                holder.mChange.setTextColor(Color.parseColor("#ff7800"));
            } else {
                holder.mChange.setTextColor(Color.parseColor("#b4b4bc"));
            }
        }


        return convertView;
    }


    public void notifyData(int commentNum, int praiseNum, int collectNum, int focusNum) {
        this.commentNum = commentNum;
        this.praiseNum = praiseNum;
        this.collectNum = collectNum;
        this.focusNum = focusNum;
        notifyDataSetChanged();
    }

    class ViewHolder {
        @BindView(R.id.img)
        ImageView mImage;
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

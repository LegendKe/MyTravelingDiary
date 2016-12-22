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
import com.ruihai.xingka.api.model.AccountInfo;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mac on 16/4/7.
 */
public class MyThirdMessageAdapter extends BaseAdapter {

    private Context context;
    String[] option;
    int[] imgs;

    private int officialNum, localOfficialNum;

    public MyThirdMessageAdapter(Context context, String[] option, int[] imgs, int officialNum) {
        this.context = context;
        this.option = option;
        this.imgs = imgs;
        this.officialNum = officialNum;
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
        localOfficialNum = AccountInfo.getInstance().getOfficialNum();
        //灰色
        holder.mChange.setTextColor(Color.parseColor("#b4b4bc"));
        if (position == 0) {
            //系统通知
            if (officialNum - localOfficialNum > 0) {
                //橙色
                holder.mChange.setTextColor(Color.parseColor("#ff7800"));
            } else {
                //灰色
                holder.mChange.setTextColor(Color.parseColor("#b4b4bc"));
            }
        }

        return convertView;
    }

    public void notifyData(int officialNum) {
        this.officialNum = officialNum;
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

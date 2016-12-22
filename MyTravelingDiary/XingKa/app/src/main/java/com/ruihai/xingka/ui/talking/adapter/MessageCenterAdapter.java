package com.ruihai.xingka.ui.talking.adapter;

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
 * Created by lqfang on 16/8/2.
 */
public class MessageCenterAdapter extends BaseAdapter {

    private Context context;
    String[] option;
    String[] imgs;
    int atNum, commentNum, praiseNum, collectNum, focusNum, officialNum, localOfficialNum;

    public MessageCenterAdapter(Context context, String[] option, String[] imgs, int atNum, int commentNum,
                                int praiseNum, int collectNum, int focusNum, int officialNum) {
        this.context = context;
        this.option = option;
        this.imgs = imgs;
        this.atNum = atNum;
        this.commentNum = commentNum;
        this.praiseNum = praiseNum;
        this.collectNum = collectNum;
        this.focusNum = focusNum;
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
        holder.mMessage.setVisibility(View.VISIBLE);
        holder.mMessage.setText(imgs[position]);
        holder.mMessage.setTextColor(Color.parseColor("#0078ff"));
        holder.mChange.setVisibility(View.VISIBLE);

        if (AccountInfo.getInstance().getOfficialNum() != 0) {
            localOfficialNum = AccountInfo.getInstance().getOfficialNum();
        } else {
            localOfficialNum = officialNum;
        }

        if (atNum > 0 || commentNum > 0 || praiseNum > 0 ||
                collectNum > 0 || focusNum > 0 || officialNum - localOfficialNum > 0) {
            holder.mChange.setTextColor(Color.parseColor("#ff7800"));

        } else if (atNum == 0 || commentNum == 0 || praiseNum == 0 ||
                collectNum == 0 || focusNum == 0 || officialNum - localOfficialNum == 0) {
            holder.mChange.setTextColor(Color.parseColor("#bcbcc4"));
        }

        return convertView;
    }

    class ViewHolder {
        @BindView(R.id.img)
        ImageView mImageView;
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

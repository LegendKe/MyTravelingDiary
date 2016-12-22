package com.ruihai.xingka.ui.mine.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.ruihai.xingka.R;
import com.ruihai.xingka.utils.QiniuHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lqfang on 15/12/1.
 */
public class EditDateFirstAdapter extends BaseAdapter {

    public static final String DEFAULT_AVATAR_KEY = "00000000-0000-0000-0000-000000000000";

    private Context context;
    String[] option;
    int status;

    String talk;
    String avatar;

    public EditDateFirstAdapter(Context context, String[] option, int status) {
        this.context = context;
        this.option = option;
        this.status = status;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getTalk() {
        return talk;
    }

    public void setTalk(String talk) {
        this.talk = talk;
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
        final ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_editdata, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        holder.mData.setText(option[position]);
        if (status == 1) {
            if (position == 0) {
                // 用户头像
                holder.mStrTips.setVisibility(View.GONE);
                holder.mAvatar.setVisibility(View.VISIBLE);
                Uri imageUri = Uri.parse(QiniuHelper.getThumbnail96Url(getAvatar()));
                if (!DEFAULT_AVATAR_KEY.equals(getAvatar())) {
                    holder.mAvatar.setImageURI(imageUri);
                } else {
                    holder.mAvatar.setImageURI(Uri.parse("res:///" + R.mipmap.default_avatar));
                }

            } else if (position == 1) {    // 我的说说
                holder.mStrTips.setVisibility(View.VISIBLE);
                String talk = getTalk();
                holder.mStrTips.setText(talk);
                holder.mPicTips.setVisibility(View.GONE);
            }
        }

        return convertView;
    }

    class ViewHolder {

        @BindView(R.id.tv_editdata_option)
        TextView mData;
        @BindView(R.id.tv_str_tips)
        TextView mStrTips;
        @BindView(R.id.iv_picture_tips)
        ImageView mPicTips;
        @BindView(R.id.sdv_avatar)
        SimpleDraweeView mAvatar;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}

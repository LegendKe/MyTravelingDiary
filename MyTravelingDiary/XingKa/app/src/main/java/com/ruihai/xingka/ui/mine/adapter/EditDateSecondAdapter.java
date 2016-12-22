package com.ruihai.xingka.ui.mine.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;
import com.ruihai.xingka.R;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.api.model.User;
import com.ruihai.xingka.utils.QiniuHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lqfang on 15/12/1.
 */
public class EditDateSecondAdapter extends BaseAdapter {
    private Context context;
    String[] option;
    int status;

    int getSex;
    String address;
    String nickname;
    String selectedTags;
    String carBrandImage;

    public EditDateSecondAdapter(Context context, String[] option, int status) {
        this.context = context;
        this.option = option;
        this.status = status;
    }

    public void setSex(int sex) {
        getSex = sex;
    }

    int getSex() {
        return getSex;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setUserTags(String tags) {
        this.selectedTags = tags;
    }

    public void setCarBrandImage(String imageKey) {
        this.carBrandImage = imageKey;
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
        if (status == 2) {
            if (position == 0) {     //我的昵称
                holder.mStrTips.setVisibility(View.VISIBLE);
                holder.mchange.setVisibility(View.VISIBLE);
                holder.mPicTips.setVisibility(View.GONE);
                holder.carImage.setVisibility(View.GONE);
                String NickName = getNickname();
                holder.mStrTips.setText(NickName);

            } else if (position == 1) {  // 性别
                holder.mPicTips.setVisibility(View.VISIBLE);
                holder.carImage.setVisibility(View.GONE);
                int sex = getSex();
                if (sex == 2) {
                    holder.mPicTips.setImageResource(R.mipmap.icon_girl);
                    holder.mchange.setVisibility(View.INVISIBLE);
                } else if (sex == 1) {
                    holder.mPicTips.setImageResource(R.mipmap.icon_boy);
                    holder.mchange.setVisibility(View.INVISIBLE);
                } else {
                    holder.mPicTips.setVisibility(View.GONE);
                }
            } else if (position == 2) {      // 手机号码
                User user = AccountInfo.getInstance().loadAccount();
                holder.mStrTips.setVisibility(View.VISIBLE);
                holder.carImage.setVisibility(View.GONE);
                String phoneNum = user.getPhone();
                holder.mStrTips.setText(phoneNum);
            } else if (position == 3) {  // 我的车型
                holder.mStrTips.setVisibility(View.GONE);
                holder.mPicTips.setVisibility(View.GONE);
                if (!TextUtils.isEmpty(carBrandImage)) {
                    holder.carImage.setVisibility(View.VISIBLE);
                    String imageUrl = QiniuHelper.getThumbnail96Url(carBrandImage);
                    Logger.d(imageUrl);
                    Glide.with(context)
                            .load(imageUrl)
                            .centerCrop()
                            .placeholder(R.mipmap.default_avatar)
                            .crossFade()
                            .into(holder.carImage);
                } else {
                    holder.carImage.setVisibility(View.GONE);
                }
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
        @BindView(R.id.iv_car_img)
        ImageView carImage;
        @BindView(R.id.iv_change)
        ImageView mchange;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}

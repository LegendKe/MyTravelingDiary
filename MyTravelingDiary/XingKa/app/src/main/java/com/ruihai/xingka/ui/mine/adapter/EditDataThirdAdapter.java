package com.ruihai.xingka.ui.mine.adapter;

import android.content.Context;
import android.text.TextUtils;
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
 * Created by lqfang on 15/12/1.
 */
public class EditDataThirdAdapter extends BaseAdapter {

    private Context context;
    String[] option;
    int status;

    String address;
    String selectedTags;

    public EditDataThirdAdapter(Context context, String[] option, int status) {
        this.context = context;
        this.option = option;
        this.status = status;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setUserTags(String tags) {
        this.selectedTags = tags;
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
        if (status == 3) {
            holder.mPicTips.setVisibility(View.GONE);
            holder.mStrTips.setVisibility(View.GONE);
            holder.carImage.setVisibility(View.GONE);
            //获取标签到列表
            if (position == 0) {  // 我的标签
                holder.mStrTips.setVisibility(View.VISIBLE);
                holder.mStrTips.setText(selectedTags);
            } else if (position == 1) {  // 我的所在地
                holder.mStrTips.setVisibility(View.VISIBLE);
                //获取到省市地址
                String address = getAddress();
                if (TextUtils.isEmpty(address)) {
                    holder.mStrTips.setText("安徽省,合肥市");
                } else {
                    // 对省市进行截取
//                    String province = address.substring(0, address.indexOf(",") - 1);
//                    String city = address.substring(address.indexOf(","), address.length()-1);
//                    String addressStr = province + city;
                    holder.mStrTips.setText(address);
                }
            } else if (position == 2) {  // 我的二维码
                holder.mPicTips.setVisibility(View.VISIBLE);
                holder.mPicTips.setImageResource(R.mipmap.icon_qrcode);
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

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}

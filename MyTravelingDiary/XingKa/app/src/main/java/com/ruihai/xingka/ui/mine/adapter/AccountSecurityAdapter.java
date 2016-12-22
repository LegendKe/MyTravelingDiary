package com.ruihai.xingka.ui.mine.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.ruihai.xingka.R;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.api.model.PhotoTopic;
import com.ruihai.xingka.api.model.User;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by apple on 15/8/18.
 */
public class AccountSecurityAdapter extends BaseAdapter {

    private Context context;
    private PhotoTopic mPhotoTopic;
    private User currentUser;
    String[] option;
    int[] imgs;
    int status;
    //int[] imgs
    public AccountSecurityAdapter(Context context, String[] option,int[] imgs, int status) {
        this.context = context;
        this.option = option;
        this.imgs = imgs;
        this.status = status;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_account_security, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        holder.mTips.setImageResource(imgs[position]);
        holder.mData.setText(option[position]);

        if (status == 1) {
            if (position == 0) {
                //通过帐号登录改变头像
//                currentUser = AccountInfo.getInstance().loadAccount();
//                Uri avatarUri = Uri.parse(QiniuHelper.getOriginalWithKey(currentUser.getAvatar()));
//                holder.mTips.setImageURI(avatarUri);
//
//                holder.mNext.setVisibility(View.GONE);
//                holder.mSecurityLevel.setVisibility(View.VISIBLE);
                holder.mNext.setVisibility(View.VISIBLE);
                holder.mSecurityLevel.setVisibility(View.GONE);

            } else {
                holder.mNext.setVisibility(View.VISIBLE);
                holder.mBind.setVisibility(View.GONE);
                if (position == 0) {
                    User user = AccountInfo.getInstance().loadAccount();

                    holder.mPhoneNum.setText(user.getPhone());
                    holder.mPhoneNum.setVisibility(View.VISIBLE);
                } else {
                    holder.mPhoneNum.setVisibility(View.GONE);
                }

            }


        } else if(status == 2){
//            holder.mNext.setVisibility(View.VISIBLE);
//            holder.mBind.setVisibility(View.VISIBLE);
//            if (position == 0) {
//                User user = AccountInfo.getInstance().loadAccount();
//
//                holder.mPhoneNum.setText(user.getPhone());
//                holder.mPhoneNum.setVisibility(View.VISIBLE);
//            } else {
//                holder.mPhoneNum.setVisibility(View.GONE);
//            }
        }

        return convertView;
    }


    class ViewHolder {

        @BindView(R.id.sdv_avatar)
        ImageView mTips;
        @BindView(R.id.tv_data)
        TextView mData;
        @BindView(R.id.tv_phonenumber)
        TextView mPhoneNum;
        @BindView(R.id.rb_security_level)
        RatingBar mSecurityLevel;
        @BindView(R.id.tv_bind)
        TextView mBind;
        @BindView(R.id.iv_next)
        ImageView mNext;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

    }

}

package com.ruihai.xingka.ui.mine.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ruihai.xingka.AppSettings;
import com.ruihai.xingka.R;
import com.ruihai.xingka.utils.AppUtility;
import com.ruihai.xingka.widget.SwitchButton;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by apple on 15/8/17.
 */
public class SettingAdapter extends BaseAdapter {

    private Context context;
    private String[] option;
    private int section;

    public SettingAdapter(Context context, String[] option, int section) {
        this.context = context;
        this.option = option;
        this.section = section;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_set, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        holder.mOption.setText(option[position]);

        if (section == 2) {
            if (position == 0) {
//                if (AccountInfo.getInstance().getUserDraftBoxInfo() != null) {
//                    holder.mBoxNum.setVisibility(View.VISIBLE);
//                } else {
//                    holder.mBoxNum.setVisibility(View.GONE);
//                }
//                holder.mMessagePushBtn.setVisibility(View.GONE);
//                holder.summary.setVisibility(View.GONE);
                holder.mBoxNum.setVisibility(View.GONE);
                holder.mMessagePushBtn.setVisibility(View.GONE);
                holder.summary.setVisibility(View.GONE);
            } else if (position == 1) {
//                holder.mBoxNum.setVisibility(View.GONE);
//                holder.mMessagePushBtn.setVisibility(View.GONE);
//                holder.summary.setVisibility(View.GONE);
                holder.mMessagePushBtn.setVisibility(View.GONE);
                holder.mNext.setVisibility(View.VISIBLE);

                holder.summary.setVisibility(View.VISIBLE);
                int value = AppSettings.getUploadSetting();
                if (value == 0) {
                    holder.summary.setText("自动");
                } else {
                    String summary = context.getResources().getStringArray(R.array.txtUpload)[value];
                    holder.summary.setText(summary);
                }
            } else if (position == 2) {
//                holder.mMessagePushBtn.setVisibility(View.GONE);
//                holder.mNext.setVisibility(View.VISIBLE);
//
//                holder.summary.setVisibility(View.VISIBLE);
//                int value = AppSettings.getUploadSetting();
//                if (value == 0) {
//                    holder.summary.setText("自动");
//                } else {
//                    String summary = context.getResources().getStringArray(R.array.txtUpload)[value];
//                    holder.summary.setText(summary);
//                }
            }
        } else if (section == 3) {
            if (position == 0) {
                holder.summary.setVisibility(View.VISIBLE);
                String version = "V" + String.valueOf(AppUtility.getAppVersionName());
                holder.summary.setText(version);
            }
        }

        return convertView;
    }

    class ViewHolder {
        @BindView(R.id.tv_set_option)
        TextView mOption;
        @BindView(R.id.tv_box_number)
        TextView mBoxNum;
        @BindView(R.id.iv_next)
        ImageView mNext;
        @BindView(R.id.sb_message_push)
        SwitchButton mMessagePushBtn;
        @BindView(R.id.tv_summary)
        TextView summary;

        public ViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }
    }
}

package com.ruihai.xingka.ui.mine.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.orhanobut.hawk.Hawk;
import com.ruihai.xingka.R;
import com.ruihai.xingka.event.OnSwitchButtonClickListener;
import com.ruihai.xingka.widget.SwitchButton;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by apple on 15/8/18.
 */
public class NotificationSettingsAdapter extends BaseAdapter {
    private Context context;
    String[] mOptions;
    String[] mValues;
    int section;

    public OnSwitchButtonClickListener mOnSwitchButtonClickListener;

    public void setOnSwitchButtonClickListener(OnSwitchButtonClickListener listener) {
        this.mOnSwitchButtonClickListener = listener;
    }

    public NotificationSettingsAdapter(Context context, String[] options, String[] values, int section) {
        this.context = context;
        this.mOptions = options;
        this.mValues = values;
        this.section = section;
    }

    @Override
    public int getCount() {
        if (null != mOptions) {
            return mOptions.length;
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return mOptions[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_notification_settings, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        if (section == 1) {
            holder.image.setVisibility(View.GONE);
            holder.optionText.setText(mOptions[position]);
            // 是否开启提醒
            boolean isEnable = Hawk.get(mValues[position], true);
            if (isEnable) {
                holder.switchButton.setToggleOn();
            } else {
                holder.switchButton.setToggleOff();
            }

            holder.switchButton.setOnToggleChanged(new SwitchButton.OnToggleChanged() {
                @Override
                public void onToggle(boolean on) {
                    Hawk.put(mValues[position], on);
                }
            });
        } else if (section == 2) {
            holder.image.setVisibility(View.GONE);
            holder.optionText.setText(mOptions[position]);
            if (position == 0) {
                // 是否开启提醒
                boolean isEnable = Hawk.get(mValues[position], true);
                if (isEnable) {
                    holder.switchButton.setToggleOn();
                } else {
                    holder.switchButton.setToggleOff();
                }
            } else if (position == 1) {
                // 是否开启提醒
                boolean isEnable = Hawk.get(mValues[position], false);
                if (isEnable) {
                    holder.switchButton.setToggleOff();
                } else {
                    holder.switchButton.setToggleOn();

                }
            }

        } else if (section == 3) {
            holder.image.setVisibility(View.GONE);
            holder.optionText.setText(mOptions[position]);

            // 是否开启提醒
            boolean isEnable = Hawk.get(mValues[position], false);
            if (isEnable) {
                holder.switchButton.setToggleOff();
            } else {
                holder.switchButton.setToggleOn();

            }

            if (mOnSwitchButtonClickListener != null) {
                // 点击开启或关闭
                holder.switchButton.setOnToggleChanged(new SwitchButton.OnToggleChanged() {
                    @Override
                    public void onToggle(boolean on) {
                        mOnSwitchButtonClickListener.onClick(holder.switchButton, position);
                    }
                });
            }
        }


        return convertView;
    }

    class ViewHolder {
        @BindView(R.id.tv_set_option)
        TextView optionText;
        @BindView(R.id.sb_notify_setting)
        SwitchButton switchButton;
        @BindView(R.id.image)
        ImageView image;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

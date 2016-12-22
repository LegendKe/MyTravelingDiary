package com.ruihai.xingka.ui.mine.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ruihai.xingka.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mac on 16/7/21.
 */
public class SettingNotDisturbedAdapter extends BaseAdapter {

    private Context context;
    String[] option;
    String[] time;

    public SettingNotDisturbedAdapter(Context context, String[] option, String[] time) {
        this.context = context;
        this.option = option;
        this.time = time;

//        Log.e("TAG", "time0000-->" + time);
    }

    public void SelectedTime(String[] time1) {
        time = time1;
        notifyDataSetChanged();
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
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_setting_not_disturbed, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        holder.tv_ready.setText(option[position]);
        holder.tv_time.setText(time[position]);


        return convertView;
    }

    class ViewHolder {
        @BindView(R.id.tv_ready)
        TextView tv_ready;
        @BindView(R.id.tv_time)
        TextView tv_time;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

    }
}

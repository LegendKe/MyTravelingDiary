package com.ruihai.xingka.ui.mine.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ruihai.iconicfontengine.widget.IconicFontTextView;
import com.ruihai.xingka.R;
import com.ruihai.xingka.api.model.AccountInfo;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 消息中心
 * Created by mac on 16/4/5.
 */
public class MyFirstAdapter extends BaseAdapter {

    private Context context;
    String[] option;
    int section;
    int type;
    //    String[] time = {"22:00", "7:00"};
    String[] time;
    String[] localTime;
    private boolean state;


    public void SelectedTime(String[] time1) {
        time = time1;
        notifyDataSetChanged();
    }

    public MyFirstAdapter(Context context, String[] option, int section, int type) {
        this.context = context;
        this.option = option;
        this.section = section;
        this.type = type;
//        this.time =time;

        if (AccountInfo.getInstance().loadNotDisturbedTime() != null) {
            localTime = AccountInfo.getInstance().loadNotDisturbedTime();
        }

        if (AccountInfo.getInstance().getState() != null) {
            state = AccountInfo.getInstance().getState();
        }

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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_myuser, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        if (section == 1) {
            holder.mData.setText(option[position]);
            holder.mChange.setVisibility(View.VISIBLE);
            holder.mChange.setTextColor(context.getResources().getColor(R.color.blue));

            holder.mTime.setVisibility(View.VISIBLE);


            if (time == null) {
                time = localTime;
            }

            if (state == false) {
                holder.mTime.setText("未设置");
            } else {
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < time.length; i++) {
                    if (i == time.length - 1) {
                        sb.append(time[i]);
                    } else {
                        sb.append(time[i] + "-");
                    }
                }
                String Time1 = sb.toString();
                holder.mTime.setText(Time1);
            }
//            if (state == false) {
//                holder.mTime.setText("未设置");
//            } else {
//                StringBuffer sb = new StringBuffer();
//                for (int i = 0; i < time.length; i++) {
//                    if (i == time.length - 1) {
//                        sb.append(time[i]);
//                    } else {
//                        sb.append(time[i] + "-");
//                    }
//                }
//                String localTime1 = sb.toString();
//                holder.mTime.setText(localTime1);
//            }


//            if(localTime != null){
//                if(state ==false){
//                    holder.mTime.setText("未设置");
//                }else {
//                    StringBuffer sb = new StringBuffer();
//                    for(int i = 0; i < localTime.length; i++){
//                        if(i == localTime.length - 1){
//                            sb. append(localTime[i]);
//                        }else {
//                            sb. append(localTime[i] + "-");
//                        }
//                    }
//                    String localTime1 = sb.toString();
//                    holder.mTime.setText(localTime1);
//                }
//
//            }else {
//                StringBuffer sb = new StringBuffer();
//                for(int i = 0; i < time.length; i++){
//                    if(i == time.length - 1){
//                        sb. append(time[i]);
//                    }else {
//                        sb. append(time[i] + "-");
//                    }
//                }
//                String time1 = sb.toString();
//
//                if(type == 1){
//                    holder.mTime.setText("未设置");
//                }else {
//                    holder.mTime.setText(time1);
//                }
//            }
        }

        return convertView;
    }

    class ViewHolder {
        @BindView(R.id.tv_message)
        IconicFontTextView mMessage;
        @BindView(R.id.tv_data)
        TextView mData;
        @BindView(R.id.tv_number)
        TextView mTime;
        @BindView(R.id.tv_change)
        IconicFontTextView mChange;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

    }
}

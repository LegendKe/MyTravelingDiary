package com.ruihai.xingka.ui.talking.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ruihai.iconicfontengine.widget.IconicFontTextView;
import com.ruihai.xingka.R;
import com.ruihai.xingka.event.OnItemClickListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 提供材料
 * Created by lqfang on 16/8/8.
 */
public class MaterialAdapter extends BaseAdapter{

    private Context context;
    private String[] option;

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public MaterialAdapter(Context context, String[] option) {
        this.context = context;
        this.option = option;
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

        holder.mData.setText("选择聊天记录上传");
        holder.mNum.setVisibility(View.VISIBLE);
        holder.mNum.setText(option[position]);
        holder.mChange.setVisibility(View.VISIBLE);

        if (position == 0) { //提交材料
//            if(num > 0){
//                holder.mChange.setTextColor(context.getResources().getColor(R.color.orange));
//            }else {
//                holder.mChange.setTextColor(context.getResources().getColor(R.color.grey_back));
//            }

        }

        return convertView;
    }


    class ViewHolder {
        @BindView(R.id.tv_data)
        TextView mData;
        @BindView(R.id.tv_number)
        TextView mNum;
        @BindView(R.id.tv_change)
        IconicFontTextView mChange;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

    }
}

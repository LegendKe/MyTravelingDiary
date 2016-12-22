package com.ruihai.xingka.ui.mine.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ruihai.xingka.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by apple on 15/9/10.
 */
public class RecommendAdapter extends BaseAdapter {

    private List<String> labalList;
    private Context context;
    public int curPosition;

    public RecommendAdapter(Context context, List<String> labalList) {
        this.context = context;
        this.labalList = labalList;
    }

    @Override
    public int getCount() {
        if (null != labalList) {
            return labalList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return labalList.get(position);
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
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_xingka_recommend, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        holder.mLabel.setText(labalList.get(position));
        if (position == curPosition) {
            holder.mLabel.setTextColor(context.getResources().getColor(R.color.white));
//            holder.mLabel.setBackgroundResource(R.color.orange1);
            holder.mLabel.setBackgroundResource(R.color.recommend_bg);

        } else {
            holder.mLabel.setTextColor(context.getResources().getColor(R.color.gray_dark));
            holder.mLabel.setBackgroundResource(0);
        }

        return convertView;
    }

    class ViewHolder {
        @BindView(R.id.tv_label)
        TextView mLabel;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

    }
}

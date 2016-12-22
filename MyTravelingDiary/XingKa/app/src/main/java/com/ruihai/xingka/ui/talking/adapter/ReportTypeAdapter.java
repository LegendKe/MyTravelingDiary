package com.ruihai.xingka.ui.talking.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ruihai.xingka.R;
import com.ruihai.xingka.api.model.ReportType;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 投诉举报类型
 * Created by lqfang on 16/8/8.
 */
public class ReportTypeAdapter extends BaseAdapter {

    private Context context;
    private List<ReportType> reportInfoList = new ArrayList<>();
    private int mSelectedIndex;

    public ReportTypeAdapter(Context context, List<ReportType> reportInfoList, int selectedIndex) {
        this.context = context;
        this.reportInfoList = reportInfoList;
        this.mSelectedIndex = selectedIndex;
    }

    public void reportTypeSelectedIndex(int selectedIndex) {
        mSelectedIndex = selectedIndex;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (null != reportInfoList) {
            return reportInfoList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return 0;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_setting_upload, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        List<ReportType> reportTypes = reportInfoList;
        final String[] stringItems = new String[reportTypes.size()];
        for (int i = 0; i < reportTypes.size(); i++) {
            stringItems[i] = reportTypes.get(i).getTitle();
        }

        holder.name.setText(stringItems[position]);
        if (mSelectedIndex == position) {
            holder.checked.setVisibility(View.VISIBLE);

        } else {
            holder.checked.setVisibility(View.GONE);
        }

        return convertView;
    }


    class ViewHolder {
        @BindView(R.id.tv_state)
        TextView name;
        @BindView(R.id.iv_checked)
        ImageView checked;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

    }
}


//public class ReportTypeAdapter extends RecyclerView.Adapter<ReportTypeAdapter.ViewHolder>{
//
//    private List<ReportType> reportInfoList = new ArrayList<>();
//    private int mSelectedIndex;
//
//    private OnItemClickListener mOnItemClickListener;
//
//    public void setOnItemClickListener(OnItemClickListener listener) {
//        this.mOnItemClickListener = listener;
//    }
//
//    public ReportTypeAdapter(List<ReportType> reportInfoList, int selectedIndex){
//        this.reportInfoList = reportInfoList;
//        mSelectedIndex = selectedIndex;
//    }
//
//    public void reportTypeSelectedIndex(int selectedIndex) {
//        mSelectedIndex = selectedIndex;
//        notifyDataSetChanged();
//    }
//
//    @Override
//    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_setting_upload, parent, false);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(final ViewHolder holder,final int position) {
//        List<ReportType> reportTypes = reportInfoList;
//        final String[] stringItems = new String[reportTypes.size()];
//        for (int i = 0; i < reportTypes.size(); i++) {
//            stringItems[i] = reportTypes.get(i).getTitle();
//        }
//
//        holder.name.setText(stringItems[position]);
//        if (mSelectedIndex == position) {
//            holder.checked.setVisibility(View.VISIBLE);
//
//        } else {
//            holder.checked.setVisibility(View.GONE);
//        }
//
//        if (mOnItemClickListener != null) {
//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    mOnItemClickListener.onItemClick(holder.itemView, position);
//                }
//            });
//        }
//    }
//
//    @Override
//    public int getItemCount() {
//        return reportInfoList.size();
//    }
//
//
//
//    static class ViewHolder extends RecyclerView.ViewHolder {
//        @Bind(R.id.tv_state)
//        TextView name;
//        @Bind(R.id.iv_checked)
//        ImageView checked;
//
//        public ViewHolder(View itemView) {
//            super(itemView);
//            ButterKnife.bind(this, itemView);
//        }
//    }
//}



package com.ruihai.xingka.ui.mine.adapter;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ruihai.xingka.Global;
import com.ruihai.xingka.R;
import com.ruihai.xingka.api.model.OfficialMessageDetail;
import com.ruihai.xingka.ui.caption.CaptionDetailActivity;
import com.ruihai.xingka.ui.common.WebActivity;
import com.shizhefei.mvc.IDataAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by apple on 15/9/11.
 */
public class NoticeAdapter extends BaseAdapter implements IDataAdapter<List<OfficialMessageDetail>> {
    private Activity context;
    private List<OfficialMessageDetail> mOfficialMessages = new ArrayList<>();

    public NoticeAdapter(Activity context) {
        this.context = context;

    }

    @Override
    public int getCount() {
        return mOfficialMessages.size();
    }


    @Override
    public Object getItem(int position) {
        return mOfficialMessages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mOfficialMessages.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        final OfficialMessageDetail officialMessageDetail = mOfficialMessages.get(position);
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_notice, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        //图标
        holder.mIcon.setImageResource(R.mipmap.icon_notice);
        if (position == mOfficialMessages.size() - 1) {
            holder.mLineBottom.setVisibility(View.GONE);
        } else {
            holder.mLineBottom.setVisibility(View.VISIBLE);
        }
        if (position == 0) {
            holder.mLineTop.setVisibility(View.INVISIBLE);
        } else {
            holder.mLineTop.setVisibility(View.VISIBLE);
        }
        //标题
        holder.mContent.setText(officialMessageDetail.getTitle());
        holder.mContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (officialMessageDetail.getType() == 1) {//跳转到web界面
                    Intent intent = new Intent(context, WebActivity.class);
                    String url = officialMessageDetail.getUrl();
                    if (!TextUtils.isEmpty(url)) {
                        intent.putExtra("URL", url);
                        context.startActivity(intent);
                    }
                } else if (officialMessageDetail.getType() == 2) {//跳到图说详情页面
                    CaptionDetailActivity.launch(context, officialMessageDetail.getCaptionId(), officialMessageDetail.getAuthorAccount());
                }
            }
        });
        // 时间
        String datetime = officialMessageDetail.getTime().substring(6, 19);
        long timestamp = Long.valueOf(datetime);
        holder.mTime.setText(Global.dayToNow(timestamp));
        //查看详情
        String url = officialMessageDetail.getUrl();
        if (officialMessageDetail.getType() == 3) {
            holder.mDetials.setVisibility(View.GONE);
        } else {
            holder.mDetials.setVisibility(View.VISIBLE);
            holder.mDetials.setText("查看详情");
            holder.mDetials.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (officialMessageDetail.getType() == 1 && !TextUtils.isEmpty(officialMessageDetail.getUrl())) {//跳转到web界面
                        Intent intent = new Intent(context, WebActivity.class);
                        intent.putExtra("URL", officialMessageDetail.getUrl());
                        context.startActivity(intent);

                    } else if (officialMessageDetail.getType() == 2) {//跳到图说详情页面
                        CaptionDetailActivity.launch(context, officialMessageDetail.getCaptionId(), officialMessageDetail.getAuthorAccount());
                    }
                }
            });
        }

        return convertView;
    }

    @Override
    public void notifyDataChanged(List<OfficialMessageDetail> officialMessageDetails, boolean isRefresh) {
        if (isRefresh) {
            mOfficialMessages.clear();
        }
        mOfficialMessages.addAll(officialMessageDetails);
        notifyDataSetChanged();
    }

    @Override
    public List<OfficialMessageDetail> getData() {
        return mOfficialMessages;
    }

    class ViewHolder {
        @BindView(R.id.iv_icon)
        ImageView mIcon;
        @BindView(R.id.iv_dian)
        ImageView mDian;
        @BindView(R.id.iv_line_top)
        View mLineTop;
        @BindView(R.id.iv_line_bottom)
        View mLineBottom;
        @BindView(R.id.tv_time)
        TextView mTime;
        @BindView(R.id.tv_content)
        TextView mContent;
        @BindView(R.id.tv_detials)
        TextView mDetials;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

    }
}

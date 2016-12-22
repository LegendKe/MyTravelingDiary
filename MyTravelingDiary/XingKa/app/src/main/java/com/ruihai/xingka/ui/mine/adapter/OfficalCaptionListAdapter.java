package com.ruihai.xingka.ui.mine.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ruihai.iconicfontengine.widget.IconicFontTextView;
import com.ruihai.xingka.Global;
import com.ruihai.xingka.R;
import com.ruihai.xingka.api.model.ImagesMessage;
import com.ruihai.xingka.api.model.UserPhotoTopic;
import com.ruihai.xingka.event.OnItemClickListener;
import com.ruihai.xingka.utils.QiniuHelper;
import com.ruihai.xingka.widget.CustomImageView;
import com.shizhefei.mvc.IDataAdapter;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mac on 16/7/14.
 */
public class OfficalCaptionListAdapter extends BaseAdapter implements IDataAdapter<List<UserPhotoTopic>> {

    public static final String DEFAULT_AVATAR_KEY = "00000000-0000-0000-0000-000000000000";

    private List<UserPhotoTopic> mData = new ArrayList<>();
    private LayoutInflater inflater;
    private int type;
    private Context context;

    public OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }


    public OfficalCaptionListAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        this.context = context;

    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int i) {
        return mData.get(i);
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_offical_caption, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        UserPhotoTopic photoTopic = mData.get(position);
        holder.num.setText(photoTopic.getReadNum());
        List<ImagesMessage> imagesList = photoTopic.getImagesMessage();
        if (imagesList.isEmpty()) {
            holder.oneImage.setVisibility(View.GONE);
        } else {
            holder.oneImage.setVisibility(View.VISIBLE);
            ImagesMessage image = imagesList.get(0);
            String imageUrl = QiniuHelper.getOriginalWithKey(image.getImgSrc());
            holder.oneImage.setImageUrl(imageUrl);
        }
        // 月份/日期显示
        String datetime = photoTopic.getAddTime().substring(6, 19);
        long timestamp = Long.valueOf(datetime);
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(timestamp);
        int month = calendar.get(GregorianCalendar.MONTH);
        int day = calendar.get(GregorianCalendar.DAY_OF_MONTH);

        holder.monthTv.setText(Global.getMonth(month + 1));

        //分享
        holder.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemChildClick(v, position);
            }
        });
        //判断日期显示
        if (day < 10) {
            holder.dayTv.setText(0 + String.valueOf(day));
        } else {
            holder.dayTv.setText(String.valueOf(day));
        }

        return convertView;

    }

    class ViewHolder {
        @BindView(R.id.num)
        TextView num;
        @BindView(R.id.iv_one_image)
        CustomImageView oneImage;

        @BindView(R.id.tv_delete)
        IconicFontTextView shareButton;
        @BindView(R.id.tv_month)
        TextView monthTv;
        @BindView(R.id.tv_day)
        TextView dayTv;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

    }


    @Override
    public void notifyDataChanged(List<UserPhotoTopic> myFriendInfos, boolean isRefresh) {
        if (isRefresh) {
            mData.clear();
        }
        mData.addAll(myFriendInfos);
        notifyDataSetChanged();
    }

    @Override
    public List<UserPhotoTopic> getData() {
        return mData;
    }
}

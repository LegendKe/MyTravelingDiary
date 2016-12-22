package com.ruihai.xingka.ui.trackway.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ruihai.iconicfontengine.widget.IconicFontTextView;
import com.ruihai.xingka.Global;
import com.ruihai.xingka.R;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.api.model.ImagesMessage;
import com.ruihai.xingka.api.model.MyTrackway;
import com.ruihai.xingka.api.model.User;
import com.ruihai.xingka.event.OnItemClickListener;
import com.ruihai.xingka.utils.QiniuHelper;
import com.ruihai.xingka.widget.CustomImageView;
import com.ruihai.xingka.widget.NineGridlayout;
import com.shizhefei.mvc.IDataAdapter;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 我的旅拼
 * Created by lqfang on 16/5/19.
 */
public class MyTrackwayAdapter extends RecyclerView.Adapter<MyTrackwayAdapter.ItemViewHolder> implements IDataAdapter<List<MyTrackway>> {
    private Context context;
    private LayoutInflater inflater;
    private List<MyTrackway> myTrackwayList = new ArrayList<>();
    private User mCurrentUser;
    private String mUserAccount;

    public OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }


    public MyTrackwayAdapter(Context context, String userAccount) {
        super();
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.mCurrentUser = AccountInfo.getInstance().loadAccount();
        this.mUserAccount = userAccount;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_user_caption, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, final int position) {
        MyTrackway myTrackway = myTrackwayList.get(position);
        holder.num.setText(myTrackway.getReadNum());

        if (position == 0) {
            holder.timelineTop.setVisibility(View.INVISIBLE);
        } else {
            holder.timelineTop.setVisibility(View.VISIBLE);
        }
        if (position == myTrackwayList.size() - 1) {
            holder.timelineBottom.setVisibility(View.INVISIBLE);
        } else {
            holder.timelineBottom.setVisibility(View.VISIBLE);
        }

        List<ImagesMessage> imagesList = myTrackway.getImagesMessage();
        if (imagesList.isEmpty()) {
            holder.moreImage.setVisibility(View.GONE);
            holder.oneImage.setVisibility(View.GONE);
        } else if (imagesList.size() == 1) {
            holder.moreImage.setVisibility(View.GONE);
            holder.oneImage.setVisibility(View.VISIBLE);

            ImagesMessage image = imagesList.get(0);
            String imageUrl = QiniuHelper.getOriginalWithKey(image.getImgSrc());
            holder.oneImage.setImageUrl(imageUrl);
        } else {
            holder.moreImage.setVisibility(View.VISIBLE);
            holder.oneImage.setVisibility(View.GONE);

            List<String> imageUrls = new ArrayList<>();
            for (ImagesMessage image : imagesList) {
                imageUrls.add(QiniuHelper.getOriginalWithKey(image.getImgSrc()));
            }
            holder.moreImage.setImagesData(imageUrls);
        }

        // 月份/日期显示
        String datetime = myTrackway.getAddTime().substring(6, 19);
        long timestamp = Long.valueOf(datetime);
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(timestamp);
        int month = calendar.get(GregorianCalendar.MONTH);
        int day = calendar.get(GregorianCalendar.DAY_OF_MONTH);

        holder.month.setText(Global.getMonth(month + 1));
        //判断日期显示
        if (day < 10) {
            holder.day.setText(0 + String.valueOf(day));
        } else {
            holder.day.setText(String.valueOf(day));
        }

        // 判断图说列表作者是否是当前登录用户,如果不是则不显示删除按钮
        if (mUserAccount.equals(String.valueOf(mCurrentUser.getAccount()))) {
            holder.deleteButton.setVisibility(View.VISIBLE);
        } else {
            holder.deleteButton.setVisibility(View.GONE);
        }

        // 添加点击和长按事件监听
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(v, position);
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mOnItemClickListener.onItemLongClick(v, position);
                    return true;
                }
            });

            // 添加删除按钮
            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemChildClick(v, position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return myTrackwayList.size();
    }

    @Override
    public void notifyDataChanged(List<MyTrackway> data, boolean isRefresh) {
        if (isRefresh) {
            myTrackwayList.clear();
        }
        myTrackwayList.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public List<MyTrackway> getData() {
        return myTrackwayList;
    }

    @Override
    public boolean isEmpty() {
        return myTrackwayList.isEmpty();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.num)
        TextView num;
        @BindView(R.id.iv_ngrid_layout)
        NineGridlayout moreImage;
        @BindView(R.id.iv_one_image)
        CustomImageView oneImage;
        @BindView(R.id.timeline_top)
        View timelineTop;
        @BindView(R.id.timeline_bottom)
        View timelineBottom;
        @BindView(R.id.tv_month)
        TextView month;
        @BindView(R.id.tv_day)
        TextView day;
        @BindView(R.id.tv_delete)
        IconicFontTextView deleteButton;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }
}


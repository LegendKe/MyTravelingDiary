package com.ruihai.xingka.ui.trackway.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.ruihai.xingka.R;
import com.ruihai.xingka.utils.AppUtility;
import com.ruihai.xingka.utils.QiniuHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 旅拼主页图片列表适配器
 * Created by mac on 16/5/11.
 */
public class TrackwayImageAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<String> mData;

    public TrackwayImageAdapter(Context context, List<String> mData) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = mData;
    }

    public void addData(List<String> data) {
        if (mData != null) {
            mData.clear();
            mData.addAll(data);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = mInflater.inflate(R.layout.item_trackway_swipe_content_view, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        //获取屏幕宽度,计算每张图片宽度
        int mScreenWidth = AppUtility.getScreenWidth();
        int mPictureWidth = (mScreenWidth - AppUtility.px2dip(145)) / 3;
//        Log.e("TAG", "图片宽度--->" + mPictureWidth + "屏幕宽度-->" + mScreenWidth);
        ViewGroup.LayoutParams lp1 = holder.imageView1.getLayoutParams();
//        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        // lp.setMargins();
        lp1.width = mPictureWidth;
        lp1.height = mPictureWidth * 3 / 4;

        holder.imageView1.setLayoutParams(lp1);


        holder.imageView1.setTag(R.id.image_tag, position);
        String imageUrl = QiniuHelper.getTopicCoverWithKey(mData.get(position));
        holder.imageView1.setImageURI(Uri.parse(imageUrl));

        if (position == mData.size() - 1) {  //最后一张图片设右边距
            holder.viewRight.setVisibility(View.VISIBLE);
            // holder.imageView1.setPadding(0, 0, AppUtility.dip2px(10), 0);
        } else {
            holder.viewRight.setVisibility(View.GONE);
        }
//        GlideHelper.loadTopicCoverWithKey(mData.get(position), holder.imageView1, new GlideHelper.ImageLoadingListener() {
//            @Override
//            public void onLoaded() {
//
//            }
//
//            @Override
//            public void onFailed() {
//
//            }
//        });

//        Log.e("TAG", "size-->" + mData.size() + "position" + position);

        return convertView;
    }

    class ViewHolder {
        @BindView(R.id.iv_image1)
        ImageView imageView1;
        @BindView(R.id.view_right)
        View viewRight;

        public ViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }
    }
}

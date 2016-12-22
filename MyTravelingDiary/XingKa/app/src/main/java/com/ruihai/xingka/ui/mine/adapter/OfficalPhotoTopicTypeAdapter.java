package com.ruihai.xingka.ui.mine.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ruihai.xingka.R;
import com.ruihai.xingka.api.model.OfficialPhotoTopicTypeRepo;
import com.ruihai.xingka.utils.AppUtility;
import com.ruihai.xingka.utils.QiniuHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sidezhang on 16/7/13.
 */
public class OfficalPhotoTopicTypeAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<OfficialPhotoTopicTypeRepo.OfficialPhotoTopicType> mData;

    public OfficalPhotoTopicTypeAdapter(Context context, List<OfficialPhotoTopicTypeRepo.OfficialPhotoTopicType> mData) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = mData;
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
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = mInflater.inflate(R.layout.item_offical_phototopic_type_view, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        OfficialPhotoTopicTypeRepo.OfficialPhotoTopicType data = mData.get(position);
        //获取屏幕宽度,计算每张图片宽度
        int mScreenWidth = AppUtility.getScreenWidth();
        int mPictureWidth = mScreenWidth / 4;


//      Log.e("TAG", "图片宽度--->" + mPictureWidth + "屏幕宽度-->" + mScreenWidth);
        ViewGroup.LayoutParams lp1 = holder.titleTv.getLayoutParams();
//      RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        // lp.setMargins();
        lp1.width = mPictureWidth;
        holder.titleTv.setLayoutParams(lp1);
        // holder.imageView1.setLayoutParams(lp1);
        holder.imageView1.setTag(R.id.image_tag, position);
        String imageUrl = QiniuHelper.getThumbnail96Url(data.getIcon());
        holder.imageView1.setImageURI(Uri.parse(imageUrl));
        holder.titleTv.setText(data.getTitle());

        return convertView;
    }

    class ViewHolder {
        @BindView(R.id.iv_icon)
        ImageView imageView1;
        @BindView(R.id.tv_title)
        TextView titleTv;


        public ViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }
    }


}

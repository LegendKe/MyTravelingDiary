package com.ruihai.xingka.ui.caption.adapter;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.ruihai.xingka.R;
import com.ruihai.xingka.api.model.CarBrandRepo;
import com.ruihai.xingka.api.model.CarBrandRepo.CarBrand;
import com.ruihai.xingka.event.OnItemClickListener;
import com.ruihai.xingka.utils.QiniuHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 车品牌数据列表适配器
 * Created by zecker on 15/8/25.
 */
public class CarBrandAdapter extends RecyclerView.Adapter<CarBrandAdapter.ViewHolder> implements SectionIndexer {
    private List<CarBrandRepo.CarBrand> data;
    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public CarBrandAdapter(List<CarBrandRepo.CarBrand> data) {
        this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_caption_car_brand, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        CarBrand brand = data.get(position);
        // Logo
        Uri imageUri = Uri.parse(QiniuHelper.getThumbnail96Url(brand.getImage()));
        holder.logo.setImageURI(imageUri);
        // 名称
        holder.name.setText(brand.getName());
        // 描述
        if (TextUtils.isEmpty(brand.getSummary())) {
            holder.summary.setVisibility(View.GONE);
        } else {
            holder.summary.setVisibility(View.VISIBLE);
            holder.summary.setText(brand.getSummary());
        }

        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(holder.itemView, position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void clear() {
        int size = data.size();
        data.clear();
        notifyItemRangeRemoved(0, size);
    }

    public void update(List<CarBrand> newData) {
        clear();
        data.addAll(newData);
        notifyDataSetChanged();
    }

    @Override
    public Object[] getSections() {
        return new Object[0];
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    @Override
    public int getPositionForSection(int sectionIndex) {
        for (int i = 0; i < getItemCount(); i++) {
            String sortStr = data.get(i).getFirstWord();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == sectionIndex) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 根据当前位置获取分类的首字母的Char ascii值
     */
    @Override
    public int getSectionForPosition(int position) {
        return data.get(position).getFirstWord().charAt(0);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_logo)
        SimpleDraweeView logo;
        @BindView(R.id.tv_name)
        TextView name;
        @BindView(R.id.tv_summary)
        TextView summary;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

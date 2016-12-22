package com.ruihai.xingka.ui.caption.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ruihai.xingka.R;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zecker on 15/9/7.
 */
public class SelectedPhotoAdapter extends RecyclerView.Adapter<SelectedPhotoAdapter.ItemViewHolder> {
    private final static int MAX_SELECT_COUNT = 9;

    private Context mContext;

    private ArrayList<String> photoPaths = new ArrayList<String>();
    private LayoutInflater inflater;

    private OnPhotoItemClickListener onPhotoItemClickListener;

    public SelectedPhotoAdapter(Context mContext, ArrayList<String> photoPaths) {
        this.photoPaths = photoPaths;
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
    }

    public void setOnPhotoItemClickListener(OnPhotoItemClickListener onPhotoItemClickListener) {
        this.onPhotoItemClickListener = onPhotoItemClickListener;
    }

    /**
     * 创建新View，被LayoutManager所调用
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_caption_selected_photo, parent, false);
        return new ItemViewHolder(itemView);
    }

    /**
     * 将数据与界面进行绑定操作
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(ItemViewHolder holder, final int position) {
        if (position == photoPaths.size()) {
            holder.ivPhoto.setImageResource(R.mipmap.icon_addpic_unfocused);
            holder.ivDelete.setVisibility(View.GONE);
            if (position == MAX_SELECT_COUNT) {
                holder.ivPhoto.setVisibility(View.GONE);
            }
        } else {
            holder.ivDelete.setVisibility(View.VISIBLE);
            Uri uri = Uri.fromFile(new File(photoPaths.get(position)));
            Glide.with(mContext)
                    .load(uri)
                    .centerCrop()
                    .thumbnail(0.1f)
                    .placeholder(R.mipmap.default_avatar)
                    .error(R.mipmap.default_avatar)
                    .into(holder.ivPhoto);
        }

        if (onPhotoItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onPhotoItemClickListener.onPhotoClick(v, position);
                }
            });
        }
        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoPaths.remove(position);
                notifyDataSetChanged();
            }
        });

    }

    /**
     * 获取数据的数量
     *
     * @return
     */
    @Override
    public int getItemCount() {
        if (photoPaths.size() == MAX_SELECT_COUNT) {
            return MAX_SELECT_COUNT;
        }
        return (photoPaths.size() + 1);
    }

    /**
     * 自定义的ViewHolder，持有每个Item的所有界面元素
     */
    static class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_photo)
        ImageView ivPhoto;
        @BindView(R.id.iv_delete)
        ImageView ivDelete;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnPhotoItemClickListener {
        void onPhotoClick(View v, int position);
    }
}

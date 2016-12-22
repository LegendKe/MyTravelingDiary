package com.ruihai.xingka.widget.pagerimagepicker.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.ruihai.xingka.R;

import java.util.List;

/**
 * Created by zecker on 15/11/28.
 */
public class DefaultImageAdapter extends AbsImageAdapter<DefaultImageAdapter.ImageItemHolder> {
    private final static int MAX_SELECT_COUNT = 9;

    public DefaultImageAdapter(List<String> imageUrls, int defaultPosition) {
        super(imageUrls, defaultPosition);
    }

    @Override
    protected void onImageItemHolderClick(DefaultImageAdapter.ImageItemHolder itemHolder) {
        int position = itemHolder.getAdapterPosition();
        if (position == imageItems.size()) {
            onImageItemListener.onImagePlusClick();
        } else {
            if (onImageItemListener != null) {
                onImageItemListener.onImageItemClick(getItem(itemHolder.getAdapterPosition()), itemHolder.getAdapterPosition());
            }

            if (!TextUtils.isEmpty(selectedImage) && selectedImage != null) {
                selectedImageView.changeImageIndicatorColor(false);
            }

            selectedImageView = itemHolder;
            selectedImage = imageItems.get(itemHolder.getAdapterPosition());

            selectedImageView.changeImageIndicatorColor(true);
        }
    }


    @Override
    public DefaultImageAdapter.ImageItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_view_default_image, parent, false);

        return new ImageItemHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(DefaultImageAdapter.ImageItemHolder holder, int position) {

        if (position == imageItems.size()) {
            holder.ivImage.setImageResource(R.mipmap.icon_add_image);

            if (position == MAX_SELECT_COUNT) {
                holder.ivImage.setVisibility(View.GONE);
            }
        } else {
            String imageUrl = imageItems.get(position);
            holder.setImageView(imageUrl);
            holder.itemView.setSelected(true);

            if (isImageSelected(imageUrl)) {
                holder.updateImageItemView(true);
                selectedImageView = holder;
            } else {
                holder.updateImageItemView(false);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (imageItems.size() == MAX_SELECT_COUNT) {
            return MAX_SELECT_COUNT;
        }
        return (imageItems.size() + 1);
    }

    public int getPagerCount() {
        return imageItems.size();
    }

    @Override
    public String getItem(int position) {
        if (position != imageItems.size()) {
            return imageItems.get(position);
        }
        return null;
    }

    static class ImageItemHolder extends AbsImageItemHolder {
        ImageView ivImage;
        RelativeLayout viewImageIndicator;

        public ImageItemHolder(View itemView, AbsImageAdapter imageAdapter) {
            super(itemView, imageAdapter);
            ivImage = (ImageView) itemView.findViewById(R.id.iv_image_picker);
            viewImageIndicator = (RelativeLayout) itemView.findViewById(R.id.view_image_indicator);
        }

        @Override
        public void setImageView(String imageUrl) {
            Glide.with(itemView.getContext())
                    .load(imageUrl)
                    .thumbnail(0.15f)
                    .centerCrop()
                    .placeholder(R.mipmap.default_avatar)
                    .into(ivImage);
        }

        @Override
        public void changeImageIndicatorColor(boolean isSelected) {
            if (isSelected) {
                viewImageIndicator.setBackgroundResource(R.color.image_item_selected_indicator);
            } else {
                viewImageIndicator.setBackgroundResource(R.color.image_item_unselected_indicator);
            }
        }

        @Override
        protected View getCurrentViewToAnimate() {
            return null;
        }

    }
}

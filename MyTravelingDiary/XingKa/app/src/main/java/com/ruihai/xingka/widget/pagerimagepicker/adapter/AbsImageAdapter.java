package com.ruihai.xingka.widget.pagerimagepicker.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.animation.Animation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zecker on 15/11/28.
 */
public abstract class AbsImageAdapter<T extends AbsImageItemHolder> extends RecyclerView.Adapter<T> {

    public interface ImageItemListener {
        void onImageItemClick(String imageItem, int position);

        void onImagePlusClick();
    }

    protected List<String> imageItems = new ArrayList<>();
    protected DefaultImageAdapter.ImageItemListener onImageItemListener;

    protected String selectedImage;
    protected T selectedImageView = null;

    private Animation currentViewAnimation;

    public AbsImageAdapter(List<String> imageUrls) {
        this(imageUrls, 0);
    }

    public AbsImageAdapter(List<String> imageUrls, int defaultPosition) {
        this.imageItems.addAll(imageUrls);
        setSelectedImage(defaultPosition);
    }

    protected abstract void onImageItemHolderClick(T itemHolder);

    protected boolean isImageSelected(String imageUrl) {
        return selectedImage.equals(imageUrl);
    }

    @Override
    public int getItemCount() {
        return imageItems != null ? imageItems.size() : 0;
    }

    public void setOnImageItemClickListener(ImageItemListener onImageItemListener) {
        this.onImageItemListener = onImageItemListener;
    }

    public String getItem(int position) {
        return imageItems.get(position);
    }

    public void setSelectedImage(int position) {
        notifyItemChanged(getPosition(selectedImage));
        selectedImage = imageItems.get(position);
        notifyItemChanged(position);
    }

    public int getPosition(String selectedImage) {
        return imageItems.indexOf(selectedImage);
    }

    public int getCurrentPosition() {
        return getPosition(selectedImage);
    }

    public Animation getCurrentViewAnimation() {
        return currentViewAnimation;
    }

    public void setCurrentViewAnimation(Animation currentViewAnimation) {
        this.currentViewAnimation = currentViewAnimation;
    }

    public boolean hasCurrentViewAnimation() {
        return currentViewAnimation != null;
    }
}

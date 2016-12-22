package com.ruihai.xingka.widget.pagerimagepicker.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by zecker on 15/11/28.
 */
public abstract class AbsImageItemHolder<T extends AbsImageAdapter> extends RecyclerView.ViewHolder implements View.OnClickListener {

    private T imageAdapter;

    public AbsImageItemHolder(View itemView, T imageAdapter) {
        super(itemView);

        this.imageAdapter = imageAdapter;

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        imageAdapter.onImageItemHolderClick(this);
    }

    public void updateImageItemView(boolean isSelected) {
        changeImageIndicatorColor(isSelected);

        if (imageAdapter.hasCurrentViewAnimation()) {
            if (isSelected) {
                getCurrentViewToAnimate().startAnimation(imageAdapter.getCurrentViewAnimation());
            } else {
                getCurrentViewToAnimate().clearAnimation();
            }
        }
    }

    public abstract void setImageView(String imageUrl);

    public abstract void changeImageIndicatorColor(boolean isSelected);

    protected abstract View getCurrentViewToAnimate();
}

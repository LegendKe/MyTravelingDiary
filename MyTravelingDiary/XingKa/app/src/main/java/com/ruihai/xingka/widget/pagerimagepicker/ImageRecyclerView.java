package com.ruihai.xingka.widget.pagerimagepicker;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.ruihai.xingka.widget.pagerimagepicker.adapter.AbsImageAdapter;
import com.ruihai.xingka.widget.pagerimagepicker.adapter.DefaultImageAdapter;


/**
 * Created by zecker on 15/11/27.
 */
public class ImageRecyclerView extends RecyclerView implements ViewPager.OnPageChangeListener, DefaultImageAdapter.ImageItemListener {

    public interface ImagePickerListener {
        void onImagePickerItemClick(String imageUrl, int position);

        void onImagePlusItemClick();

        void onImagePickerPageSelected(int position);

        void onImagePickerPageStateChanged(int state);

        void onImagePickerPageScrolled(int position, float positionOffset, int positionOffsetPixels);
    }

    private AbsImageAdapter defaultImageAdapter;
    private ImagePickerListener imagePickerListener;
    private ViewPager pager;

    public ImageRecyclerView(Context context) {
        this(context, null);
    }

    public ImageRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        initWidget(context);
    }

    private void initWidget(Context context) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        setLayoutManager(layoutManager);

        if (defaultImageAdapter != null) {
            setAdapter(defaultImageAdapter);
        }
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        if (!(layout instanceof LinearLayoutManager)) {
            throw new IllegalArgumentException("For now ImageRecyclerView supports only LinearLayoutManager");
        }

        LinearLayoutManager linearLayout = (LinearLayoutManager) layout;
        if (linearLayout.getOrientation() != LinearLayoutManager.HORIZONTAL) {
            throw new IllegalArgumentException("For now ImageRecyclerView supports only horizontal scrolling");
        }

        super.setLayoutManager(layout);
    }

    @Override
    public Adapter getAdapter() {
        return defaultImageAdapter;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (!(adapter instanceof AbsImageAdapter)) {
            throw new IllegalArgumentException("Your adapter has to be a ImageAdapter type");
        }

        defaultImageAdapter = (AbsImageAdapter) adapter;
        defaultImageAdapter.setOnImageItemClickListener(this);
        super.setAdapter(defaultImageAdapter);

        scrollToPosition(defaultImageAdapter.getCurrentPosition());
    }

    public void setImagePickerListener(ImagePickerListener imagePickerListener) {
        this.imagePickerListener = imagePickerListener;
    }

    public void setPager(ViewPager pager) {
        this.pager = pager;
        this.pager.setCurrentItem(defaultImageAdapter.getCurrentPosition(), false);

        this.pager.setOnPageChangeListener(this);
    }

    public
    @NonNull
    AbsImageAdapter getImageAdapter() {
        return defaultImageAdapter;
    }

    @Override
    public void onImageItemClick(String imageItem, int position) {
        pager.setCurrentItem(position, true);

        if (imagePickerListener != null) {
            imagePickerListener.onImagePickerItemClick(imageItem, position);
        }
    }

    @Override
    public void onImagePlusClick() {
        if (imagePickerListener != null) {
            imagePickerListener.onImagePlusItemClick();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (imagePickerListener != null) {
            imagePickerListener.onImagePickerPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageSelected(int position) {
        smoothScrollToPosition(position);
        getImageAdapter().setSelectedImage(position);

        if (imagePickerListener != null) {
            imagePickerListener.onImagePickerPageSelected(position);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (imagePickerListener != null) {
            imagePickerListener.onImagePickerPageStateChanged(state);
        }
    }
}

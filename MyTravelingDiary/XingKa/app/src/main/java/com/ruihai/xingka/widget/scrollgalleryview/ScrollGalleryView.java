package com.ruihai.xingka.widget.scrollgalleryview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;


import com.ruihai.xingka.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by veinhorn on 6.8.15.
 */
public class ScrollGalleryView extends LinearLayout {
    private FragmentManager fragmentManager;
    private Context context;
    private Point displayProps;
    private int thumbnailSize; // width and height in pixels
    private boolean isZoom;

    // Views
    private LinearLayout thumbnailsContainer;
    private HorizontalScrollView horizontalScrollView;
    private ViewPager viewPager;
    ////////

    private PagerAdapter pagerAdapter;
    private List<Bitmap> images;


    public ScrollGalleryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        images = new ArrayList<>();

        setOrientation(VERTICAL);
        displayProps = getDisplaySize();
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.scroll_gallery_view, this, true);

        horizontalScrollView = (HorizontalScrollView)findViewById(R.id.thumbnails_scroll_view);

        thumbnailsContainer = (LinearLayout)findViewById(R.id.thumbnails_container);
        thumbnailsContainer.setPadding(displayProps.x / 2, 0,
                displayProps.x / 2, 0);
    }

    public ScrollGalleryView setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
        initializeViewPager();
        return this;
    }

    public ScrollGalleryView addImage(int image) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), image);
        addImage(bitmap);
        return this;
    }

    public ScrollGalleryView addImage(Bitmap image) {
        images.add(image);
        addThumbnail(image);
        pagerAdapter.notifyDataSetChanged();
        return this;
    }

    public ScrollGalleryView addImages(File fromDir) {
        File[] files = fromDir.listFiles();
        BitmapFactory.Options options = new BitmapFactory.Options();

        for (int i=0; files != null && i < files.length; i++) {
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(files[i].getAbsolutePath(), options);
            int imgWidth = options.outWidth;
            int imgHeight = options.outHeight;

            int maxWidth = displayProps.x;
            int maxHeight = displayProps.y;

            /* calculate appropriate inSampleSize for high-res images from SDCARD */
            options.inSampleSize = calculateInSampleSize(imgWidth, imgHeight, maxWidth, maxHeight);
            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeFile(files[i].getAbsolutePath(), options);
            images.add(bitmap);
            addThumbnail(bitmap);
        }
        pagerAdapter.notifyDataSetChanged();
        return this;
    }

    public ScrollGalleryView setThumbnailSize(int thumbnailSize) {
        this.thumbnailSize = thumbnailSize;
        return this;
    }

    public ScrollGalleryView setZoom(boolean isZoom) {
        this.isZoom = isZoom;
        return this;
    }

    private OnClickListener thumbnailOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            scroll(v);
            viewPager.setCurrentItem((int) v.getTag(), true);
        }
    };

    private ViewPager.SimpleOnPageChangeListener viewPagerChangeListener = new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            scroll(thumbnailsContainer.getChildAt(position));
        }
    };

    private Point getDisplaySize() {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point point = new Point();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) display.getSize(point);
        else point.set(display.getWidth(), display.getHeight());
        return point;
    }

    private ScrollGalleryView addThumbnail(Bitmap image) {
        LayoutParams lp = new LayoutParams(thumbnailSize, thumbnailSize);
        lp.setMargins(10, 10, 10, 10);
        Bitmap thumbnail = createThumbnail(image);
        thumbnailsContainer.addView(createThumbnailView(lp, thumbnail));
        return this;
    }

    private ImageView createThumbnailView(LayoutParams lp, Bitmap thumbnail) {
        ImageView thumbnailView = new ImageView(context);
        thumbnailView.setLayoutParams(lp);
        thumbnailView.setImageBitmap(thumbnail);
        thumbnailView.setTag(images.size() - 1);
        thumbnailView.setOnClickListener(thumbnailOnClickListener);
        return thumbnailView;
    }

    private Bitmap createThumbnail(Bitmap image) {
        return ThumbnailUtils.extractThumbnail(image, thumbnailSize, thumbnailSize);
    }

    private void initializeViewPager() {
        viewPager = (HackyViewPager)findViewById(R.id.viewPager);
        pagerAdapter = new ScreenSlidePagerAdapter(fragmentManager, images, isZoom);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerChangeListener);
    }

    private void scroll(View thumbnail) {
        int thumbnailCoords[] = new int [2];
        thumbnail.getLocationOnScreen(thumbnailCoords);

        int thumbnailCenterX = thumbnailCoords[0] + thumbnailSize / 2;
        int thumbnailDelta = displayProps.x / 2 - thumbnailCenterX;

        horizontalScrollView.smoothScrollBy(-thumbnailDelta, 0);
    }

    private int calculateInSampleSize(int imgWidth, int imgHeight, int maxWidth, int maxHeight) {
        int inSampleSize = 1;
        while (imgWidth / inSampleSize > maxWidth || imgHeight / inSampleSize > maxHeight) {
            inSampleSize *= 2;
        }
        return inSampleSize;
    }
}

package com.ruihai.xingka.utils;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by zecker on 15/8/27.
 */
public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration{

    private int halfSpace;

    public GridSpacingItemDecoration(int space) {
        this.halfSpace = space / 2;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.top = halfSpace / 2;
        outRect.bottom = halfSpace / 2;
        outRect.left = halfSpace;
        outRect.right = halfSpace;
    }
}

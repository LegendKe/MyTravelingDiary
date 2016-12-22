package com.ruihai.xingka.widget.pagerimagepicker;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.DimenRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ruihai.xingka.R;


public class RecyclerViewInsetDecoration extends RecyclerView.ItemDecoration {

    private int insets;

    public RecyclerViewInsetDecoration(Context context) {
        insets = context.getResources().getDimensionPixelSize(R.dimen.image_card_insets_default);
    }

    public RecyclerViewInsetDecoration(Context context, @DimenRes int insetsResId) {
        insets = context.getResources().getDimensionPixelSize(insetsResId);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(insets, insets, insets, insets);
    }
}
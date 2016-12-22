package com.ruihai.xingka.event;

import android.view.View;

/**
 * Interface definition for a callback to be invoked when an item in this
 * RecyclerView.Adapter has been clicked.
 *
 * Created by zecker on 15/8/27.
 */
public interface OnItemClickListener {

    void onItemClick(View view, int position);

    void onItemLongClick(View view, int position);

    void onItemChildClick(View childView, int position);
}

package com.ruihai.xingka.ui.caption.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ruihai.xingka.R;
import com.ruihai.xingka.event.OnItemClickListener;
import com.ruihai.xingka.utils.AppUtility;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zecker on 15/8/27.
 */
public class LetterCatalogAdapter extends RecyclerView.Adapter<LetterCatalogAdapter.ViewHolder> {
    // 触摸事件
    private OnTouchingLetterChangedListener onTouchingLetterChangedListener;

    public static String[] letters = {"A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z", "#"};

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_letter_catalog, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;

        holder.letter.setText(letters[position]);
        holder.letter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onTouchingLetterChanged(letters[position]);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return letters.length;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_letter)
        TextView letter;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    /**
     * 对外公开方法
     *
     * @param onTouchingLetterChangedListener
     */
    public void setOnTouchingLetterChangedListener(
            OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
        this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
    }

    /**
     * 接口
     */
    public interface OnTouchingLetterChangedListener {
        public void onTouchingLetterChanged(String s);
    }
}

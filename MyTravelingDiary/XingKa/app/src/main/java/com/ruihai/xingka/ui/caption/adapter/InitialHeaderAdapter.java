package com.ruihai.xingka.ui.caption.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eowise.recyclerview.stickyheaders.StickyHeadersAdapter;
import com.ruihai.xingka.R;
import com.ruihai.xingka.api.model.CarBrandRepo.CarBrand;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zecker on 15/8/25.
 */
public class InitialHeaderAdapter implements StickyHeadersAdapter<InitialHeaderAdapter.ViewHolder> {

    private List<CarBrand> items;

    public InitialHeaderAdapter(List<CarBrand> items) {
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_letter_header, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder headerViewHolder, int position) {
        headerViewHolder.letter.setText(items.get(position).getFirstWord());
    }

    @Override
    public long getHeaderId(int position) {
        return items.get(position).getFirstWord().charAt(0);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_letter)
        TextView letter;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
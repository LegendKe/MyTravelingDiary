package com.ruihai.xingka.ui.mine.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.ruihai.iconicfontengine.widget.IconicFontTextView;
import com.ruihai.xingka.Global;
import com.ruihai.xingka.R;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.api.model.DraftBoxItem;
import com.ruihai.xingka.api.model.PushMessage;
import com.ruihai.xingka.ui.caption.CaptionAddActivity;
import com.ruihai.xingka.utils.AppUtility;
import com.ruihai.xingka.utils.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gjzhang on 16/1/6.
 */
public class DraftBoxAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater inflater;
    private List<DraftBoxItem> mDraftBoxItems = new ArrayList<>();

    public DraftBoxAdapter(Context context) {
        mContext = context;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mDraftBoxItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mDraftBoxItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.list_item_draftbox, parent, false);
        final DraftBoxItem data = mDraftBoxItems.get(position);
        ImageView coverImage = ViewHolder.get(convertView, R.id.sdv_avatar);
        if (data.getmSelectedPath().size() > 0) {
            Bitmap bitmap = AppUtility.getImageThumbnail(data.getmSelectedPath().get(0), AppUtility.dip2px(60), AppUtility.dip2px(60));
            coverImage.setImageBitmap(bitmap);
        }
        TextView title = ViewHolder.get(convertView, R.id.tv_title);
        if (TextUtils.isEmpty(data.getTitle())) {
            title.setVisibility(View.GONE);
        } else {
            title.setText(data.getTitle());
        }

        TextView saveTime = ViewHolder.get(convertView, R.id.tv_time);
        saveTime.setText(Global.dayToNow(data.getSaveTime()));
        IconicFontTextView editBtn = ViewHolder.get(convertView, R.id.btn_edit);//编辑按钮

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CaptionAddActivity.class);
                intent.putExtra("FROM_DRAFTBOX_FLAG", true);
                mContext.startActivity(intent);
            }
        });
        return convertView;
    }

    public void notifyDataChanged(DraftBoxItem draftBoxItem, boolean isRefresh) {
        if (isRefresh) {
            mDraftBoxItems.clear();
        }
        if (draftBoxItem != null) {
            mDraftBoxItems.add(draftBoxItem);
        }
        notifyDataSetChanged();
    }


    public List<DraftBoxItem> getData() {
        return mDraftBoxItems;
    }
}

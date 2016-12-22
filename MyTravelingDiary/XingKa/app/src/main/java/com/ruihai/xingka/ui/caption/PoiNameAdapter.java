package com.ruihai.xingka.ui.caption;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.amap.api.services.core.PoiItem;
import com.ruihai.xingka.R;

import java.util.List;

/**
 * Created by lqfang on 15/9/18.
 */
public class PoiNameAdapter extends BaseAdapter {
    private Context mContext;
    private List<PoiItem> list_msg;

    public PoiNameAdapter(Context mContext, List<PoiItem> list_msg) {
        super();
        this.mContext = mContext;
        this.list_msg = list_msg;
    }

    @Override
    public int getCount() {
        return list_msg.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        String proName = null;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_choose_location, null);
            vh = new ViewHolder();
            vh.show_name = (TextView) convertView.findViewById(R.id.show_name);
            vh.show_address = (TextView) convertView.findViewById(R.id.show_address);
            vh.view = (View) convertView.findViewById(R.id.view);
            vh.view0 = (View) convertView.findViewById(R.id.view0);
            vh.view1 = (View) convertView.findViewById(R.id.view1);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        String address = list_msg.get(position).getSnippet();
        String city = list_msg.get(position).getCityName();
        vh.show_name.setText(list_msg.get(position).getTitle());
        if (TextUtils.isEmpty(address)) {
            vh.show_address.setText(city);
        } else {
            vh.show_address.setText(address);
        }

        if (position == 0 || position == 1) {
            vh.show_address.setVisibility(View.GONE);
            vh.view.setVisibility(View.VISIBLE);
            vh.view0.setVisibility(View.VISIBLE);
            vh.view1.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    class ViewHolder {
        TextView show_name, show_address;
        View view, view0, view1;
    }
}

package com.ruihai.xingka.ui.trackway.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ruihai.iconicfontengine.widget.IconicFontTextView;
import com.ruihai.xingka.R;
import com.ruihai.xingka.utils.ViewHolder;

import java.util.ArrayList;

/**
 * Created by gebixiaozhang on 16/5/11.
 */
public class RouteAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<String> datas = new ArrayList<>();

    public RouteAdapter(Context context, ArrayList<String> datas) {
        this.context = context;
        this.datas = datas;
        inflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return datas.size();
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
        convertView = inflater.inflate(R.layout.item_route_listview, parent, false);
        View startCityIcon = ViewHolder.get(convertView, R.id.icon_start_city);
        IconicFontTextView startCityTv = ViewHolder.get(convertView, R.id.icon_destination);
        View topRedLine = ViewHolder.get(convertView, R.id.red_line_top);
        View bottomRedLine = ViewHolder.get(convertView, R.id.red_line_bottom);
        TextView destinationTv = ViewHolder.get(convertView, R.id.tv_destination);
        TextView destinationCity = ViewHolder.get(convertView, R.id.tv_selected_tv_destination);
        destinationCity.setText(datas.get(position));
        if (position == 0) {
            startCityIcon.setVisibility(View.VISIBLE);
            startCityTv.setVisibility(View.GONE);
            topRedLine.setVisibility(View.INVISIBLE);
            destinationTv.setText("出发城市");
        } else {
            topRedLine.setVisibility(View.VISIBLE);
            startCityIcon.setVisibility(View.GONE);
            startCityTv.setVisibility(View.VISIBLE);
            destinationTv.setText("前往");
        }
        if (position == (datas.size() - 1)) {
            destinationCity.setTextColor(Color.parseColor("#ff7800"));
            bottomRedLine.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }
}

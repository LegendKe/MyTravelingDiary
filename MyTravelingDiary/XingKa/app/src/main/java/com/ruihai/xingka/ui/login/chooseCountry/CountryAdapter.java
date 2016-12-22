package com.ruihai.xingka.ui.login.chooseCountry;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ruihai.xingka.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lqfang on 16/3/4.
 */
public class CountryAdapter extends BaseAdapter {

    private Context context;
    private String country;
    private String code;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public CountryAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_country, viewGroup, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

//        holder.mCountry.setText("中国");
//        holder.mCode.setText("+86");
//        holder.mCountry.setText(getCountry());
//        holder.mCode.setText(getCode());
        return convertView;
    }

    class ViewHolder {

        @BindView(R.id.country_name)
        TextView mCountry;
        @BindView(R.id.country_code)
        TextView mCode;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

    }
}

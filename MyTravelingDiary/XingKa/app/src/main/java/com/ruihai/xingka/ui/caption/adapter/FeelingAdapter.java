package com.ruihai.xingka.ui.caption.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.ruihai.xingka.R;

/**
     * 选择表情的适配器类
     */
    public class FeelingAdapter extends BaseAdapter {
        private Context mContext;
        private int[] imgs;
        private int clickTemp = -1;
        //标识选择的Item
        public void setSeclection(int position) {
            clickTemp = position;
        }


        public FeelingAdapter(Context mContext,int[] imgs) {
            super();
            this.mContext = mContext;
            this.imgs=imgs;
        }

        @Override
        public int getCount() {
            return imgs.length;
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHodler hodler;
            if (convertView == null) {
                hodler = new ViewHodler();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_choose_feeling, parent,false);
                hodler.iv = (ImageView) convertView.findViewById(R.id.iv_face1);
                convertView.setTag(hodler);

            } else {
                hodler = (ViewHodler) convertView.getTag();
            }
            hodler.iv.setImageResource(imgs[position]);
            return convertView;

        }

        class ViewHodler {
            ImageView iv;
        }
    }

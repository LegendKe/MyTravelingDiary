package com.ruihai.xingka.ui.mine;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ruihai.iconicfontengine.widget.IconicFontTextView;
import com.ruihai.xingka.AppSettings;
import com.ruihai.xingka.R;
import com.ruihai.xingka.ui.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

public class UploadSettingActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView mTitleView;
    @BindView(R.id.tv_right)
    IconicFontTextView mRightView;
    @BindView(R.id.listView)
    ListView mListView;

    private UploadSettingAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_setting);
        ButterKnife.bind(this);

        mTitleView.setText("图片上传质量");
        mRightView.setVisibility(View.INVISIBLE);

        String[] uploadStates = getResources().getStringArray(R.array.txtUpload);
        int value = AppSettings.getUploadSetting();
        mAdapter = new UploadSettingAdapter(uploadStates, value);
        mListView.setAdapter(mAdapter);

    }

    @OnClick(R.id.tv_back)
    void onBack() {
        finish();
    }

    @OnItemClick(R.id.listView)
    void onItemClick(int position) {
        AppSettings.setUploadSetting(position);
        mAdapter.updateSelectedIndex(position);
    }

    class UploadSettingAdapter extends BaseAdapter {

        private String[] mStates;
        private int mSelectedIndex;

        private UploadSettingAdapter(String[] states, int selectedIndex) {
            mStates = states;
            mSelectedIndex = selectedIndex;
        }

        public void updateSelectedIndex(int selectedIndex) {
            mSelectedIndex = selectedIndex;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mStates.length;
        }

        @Override
        public Object getItem(int position) {
            return mStates[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView != null) {
                holder = (ViewHolder) convertView.getTag();
            } else {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_setting_upload, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            }

            holder.name.setText(getItem(position).toString());
            if (mSelectedIndex == position) {
                holder.checked.setVisibility(View.VISIBLE);
            } else {
                holder.checked.setVisibility(View.GONE);
            }

            return convertView;
        }
    }

    static class ViewHolder {

        @BindView(R.id.tv_state)
        TextView name;
        @BindView(R.id.iv_checked)
        ImageView checked;

        public ViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }
    }
}

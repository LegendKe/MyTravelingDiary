package com.ruihai.xingka.ui.mine.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;

import com.ruihai.xingka.R;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.api.model.TagInfo;
import com.ruihai.xingka.api.model.UserTag;
import com.ruihai.xingka.widget.ProgressHUD;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 标签适配器
 * Created by apple on 15/8/19.
 */
public class UserLabelAdapter extends BaseAdapter {
    private static final int MAX_TAG_COUNT = 3;

    List<TagInfo> mTagInfos;
    Context context;
    HashSet<String> mHashSet = new HashSet<>();

    public UserLabelAdapter(Context context, List<TagInfo> tagInfos) {
        this.context = context;
        this.mTagInfos = tagInfos;
        List<UserTag> userTags = AccountInfo.getInstance().getUserTags();
        if (userTags != null)
            setSelected(getUserTagNames(userTags));
    }

    private boolean isSelectFill() {
        return mHashSet.size() >= MAX_TAG_COUNT;
    }

    @Override
    public int getCount() {
        return mTagInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return mTagInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_label, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        TagInfo tagInfo = mTagInfos.get(position);
        holder.tagView.setText(tagInfo.getName());

        if (mHashSet.contains(tagInfo.getName())) {
            holder.tagView.setChecked(true);
        } else {
            holder.tagView.setChecked(false);
        }

        return convertView;
    }

    public String getSelected() {
        return TextUtils.join(",", mHashSet);
    }

    public void setSelected(String tags) {
        mHashSet.clear();
        String[] selectedTagNames = tags.trim().split(",");
        for (String name : selectedTagNames) {
            if (!name.trim().isEmpty())
                mHashSet.add(name);
        }
        notifyDataSetChanged();
    }

    public void setSelectedTag(int position) {
        String tagName = mTagInfos.get(position).getName();
        if (!mHashSet.contains(tagName)) {
            if (!isSelectFill()) {
                mHashSet.add(tagName);
            } else {
                ProgressHUD.showInfoMessage(context, String.format("最多只能选择%d个标签!", MAX_TAG_COUNT));
//                AppUtility.showToast(String.format("最多只能选择%d个标签", MAX_TAG_COUNT));
            }
        } else {
            mHashSet.remove(tagName);
        }
        notifyDataSetChanged();
    }

    class ViewHolder {

        @BindView(R.id.tv_label)
        CheckedTextView tagView;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    /*
    * 遍历标签id得到标签名
    * */
    public String getUserTagNames(List<UserTag> userTags) {
        List<String> tags = new ArrayList<>();
        for (UserTag tag : userTags) {
            tags.add(tag.getName());
        }
        return TextUtils.join(",", tags);
    }
}

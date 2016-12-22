package com.ruihai.xingka.ui.mine.adapter;

import android.content.res.Resources;
import android.support.v4.app.FragmentManager;

import com.ruihai.xingka.ui.mine.fragment.BaseScrollFragment;

import java.util.List;

/**
 * Created by Dimitry Ivanov on 21.08.2015.
 */
public class UserProfilePagerAdapter extends FragmentPagerAdapterExt {

    private final Resources mResources;
    private final List<BaseScrollFragment> mFragments;

    public UserProfilePagerAdapter(FragmentManager fm, Resources r, List<BaseScrollFragment> fragments) {
        super(fm);
        this.mResources = r;
        this.mFragments = fragments;
    }

    @Override
    public BaseScrollFragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments != null ? mFragments.size() : 0;
    }

    @Override
    public String makeFragmentTag(int position) {
        return mFragments.get(position).getSelfTag();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragments.get(position).getTitle(mResources);
    }

    public boolean canScrollVertically(int position, int direction) {
        return getItem(position).canScrollVertically(direction);
    }
}

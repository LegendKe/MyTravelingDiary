package com.ruihai.xingka.ui.caption.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.ruihai.xingka.widget.pagerimagepicker.adapter.AbsImageAdapter;
import com.ruihai.xingka.widget.pagerimagepicker.adapter.DefaultImageAdapter;


public abstract class ImagePagerFragmentAdapter<T extends Fragment> extends FragmentStatePagerAdapter {

    private DefaultImageAdapter defaultImageAdapter;

    public ImagePagerFragmentAdapter(FragmentManager fm, AbsImageAdapter defaultImageAdapter) {
        super(fm);
        this.defaultImageAdapter = (DefaultImageAdapter) defaultImageAdapter;
    }

    @Override
    public Fragment getItem(int position) {
        String imageItem = defaultImageAdapter.getItem(position);
        return getFragment(position, imageItem);
    }

    @Override
    public int getCount() {
        return defaultImageAdapter.getPagerCount();
    }

    protected abstract T getFragment(int position, String imageUrl);

}

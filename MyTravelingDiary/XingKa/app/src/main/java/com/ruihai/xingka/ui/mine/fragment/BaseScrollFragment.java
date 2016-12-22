package com.ruihai.xingka.ui.mine.fragment;

import android.content.res.Resources;
import android.support.v4.app.Fragment;

import ru.noties.scrollable.CanScrollVerticallyDelegate;

/**
 * Created by zecker on 15/11/6.
 */
public abstract class BaseScrollFragment extends Fragment implements CanScrollVerticallyDelegate {

    public abstract CharSequence getTitle(Resources r);
    public abstract String getSelfTag();

}

package com.ruihai.xingka.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.ruihai.xingka.ui.common.PhotoPickerActivity;

import java.util.ArrayList;


public class PhotoPickerIntent extends Intent {

    private PhotoPickerIntent() {
    }

    private PhotoPickerIntent(Intent o) {
        super(o);
    }

    private PhotoPickerIntent(String action) {
        super(action);
    }

    private PhotoPickerIntent(String action, Uri uri) {
        super(action, uri);
    }

    private PhotoPickerIntent(Context packageContext, Class<?> cls) {
        super(packageContext, cls);
    }

    public PhotoPickerIntent(Context packageContext) {
        super(packageContext, PhotoPickerActivity.class);
    }

    public void setPhotoCount(int photoCount) {
        this.putExtra(PhotoPickerActivity.EXTRA_MAX_COUNT, photoCount);
    }

    public void setDefaultSelected(ArrayList<String> selectedList) {
        this.putExtra(PhotoPickerActivity.EXTRA_DEFAULT_SELECTED_LIST, selectedList);
    }

    public void setShowCamera(boolean showCamera) {
        this.putExtra(PhotoPickerActivity.EXTRA_SHOW_CAMERA, showCamera);
    }

}

package com.ruihai.xingka.utils;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.ruihai.android.ui.widget.webview.LoadingInterceptor;

public class UnsupportedProtcolInterceptor implements LoadingInterceptor {

    private Activity activity;

    public UnsupportedProtcolInterceptor(Activity activity) {
        this.activity = activity;
    }

    @Override
    public boolean validate(Uri uri) {
        if (uri.getScheme() != null) {
            return !uri.getScheme().equals("http") && !uri.getScheme().equals("https");
        }
        return false;
    }

    @Override
    public void exec(Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        activity.startActivity(intent);
    }
}

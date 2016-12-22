package com.ruihai.android.common.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

import com.ruihai.android.common.context.GlobalContext;

/**
 * Created by zecker on 15/10/13.
 */
public class SystemUtils {

    public enum NetWorkType {
        none, mobile, wifi
    }

    public static NetWorkType getNetworkType() {
        ConnectivityManager connMgr = (ConnectivityManager) GlobalContext.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null) {
            switch (networkInfo.getType()) {
                case ConnectivityManager.TYPE_MOBILE:
                    return NetWorkType.mobile;
                case ConnectivityManager.TYPE_WIFI:
                    return NetWorkType.wifi;
            }
        }

        return NetWorkType.none;
    }

    public static String getSdcardPath() {
        if (hasSDCard())
            return Environment.getExternalStorageDirectory().getAbsolutePath();

        return "/sdcard/";
    }

    public static boolean hasSDCard() {
        boolean mHasSDcard = false;
        if (Environment.MEDIA_MOUNTED.endsWith(Environment.getExternalStorageState())) {
            mHasSDcard = true;
        } else {
            mHasSDcard = false;
        }

        return mHasSDcard;
    }

}

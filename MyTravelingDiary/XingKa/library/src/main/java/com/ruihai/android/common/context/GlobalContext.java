package com.ruihai.android.common.context;

import android.app.Application;

import com.ruihai.android.common.setting.SdcardUtils;
import com.ruihai.android.common.setting.SettingUtility;
import com.ruihai.android.common.utils.ActivityHelper;

import java.io.File;

/**
 * Created by zecker on 15/10/13.
 */
public class GlobalContext extends Application {
    private static GlobalContext _context;

    @Override
    public void onCreate() {
        super.onCreate();

        _context = this;

        // 初始化ActivityHelper
        ActivityHelper.config(this);
        // 初始化设置
        SettingUtility.setSettingUtility();
    }

    public static GlobalContext getInstance() {
        return _context;
    }

    /**
     * 程序的文件目录，如果setting配置的是android，标志目录位于/sdcard/Application/PackageName/...下<br/>
     * 否则，就是/sdcard/setting[root_path]/...目录
     *
     * @return
     */
    public String getAppPath() {
        if ("android".equals(SettingUtility.getStringSetting("root_path")))
            return getExternalCacheDir().getAbsolutePath() + File.separator;

        return SdcardUtils.getSdcardPath() + File.separator + SettingUtility.getStringSetting("root_path") + File.separator;
    }

    /**
     * 关于程序所有的文件缓存根目录
     *
     * @return
     */
    public String getDataPath() {
        return getAppPath() + SettingUtility.getPermanentSettingAsStr("com_m_common_json", "data") + File.separator;
    }

    /**
     * 图片缓存目录
     *
     * @return
     */
    public String getImagePath() {
        return getAppPath() + SettingUtility.getPermanentSettingAsStr("com_m_common_image", "image") + File.separator;
    }

}

package com.ruihai.android.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by zecker on 15/11/3.
 */
public class FileUtils {
    public static void copyFile(File sourceFile, File targetFile) throws Exception {
        FileInputStream in = new FileInputStream(sourceFile);
        byte[] buffer = new byte[128 * 1024];
        int len = -1;
        FileOutputStream out = new FileOutputStream(targetFile);
        while ((len = in.read(buffer)) != -1)
            out.write(buffer, 0, len);
        out.flush();
        out.close();
        in.close();
    }
}

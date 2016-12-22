package com.ruihai.xingka.ui.mine;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.isseiaoki.simplecropview.CropImageView;
import com.ruihai.android.common.utils.SystemUtils;
import com.ruihai.iconicfontengine.widget.IconicFontTextView;
import com.ruihai.xingka.AppSettings;
import com.ruihai.xingka.R;
import com.ruihai.xingka.ui.BaseActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 背景墙图片裁剪
 * Created by lqfang on 16/3/1.
 */
public class CropBgImageActivity extends BaseActivity {

    public static final String ARG_CROP_IMAGE_PATH = "image_path";
    public static final String ARG_CROP_RESULT = "crop_result";

    @BindView(R.id.tv_back)
    IconicFontTextView mBackView;
    @BindView(R.id.tv_title)
    TextView mTitleView;
    @BindView(R.id.crop_imageview)
    CropImageView mCropImageView;

    private String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bg_crop_image);
        ButterKnife.bind(this);

        imagePath = getIntent().getStringExtra(ARG_CROP_IMAGE_PATH);
        initViews();
    }

    private void initViews() {
        mBackView.setText("{xk-close}");
        mTitleView.setText(R.string.caption_crop_image);

//        mCropImageView.setCropMode(CropImageView.CropMode.RATIO_1_1);
//        mCropImageView.setCropMode(CropImageView.CropMode.CIRCLE);
//        mCropImageView.setInitialFrameScale(1.0f);
        mCropImageView.setImageBitmap(BitmapFactory.decodeFile(imagePath));

        //对图片进行压缩处理(测试)
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, opts);

        opts.inSampleSize = computeSampleSize(opts, -1, 1 * 1024 * 1024);
        //这里一定要将其设置回false，因为之前我们将其设置成了true
        opts.inJustDecodeBounds = false;
        try {
            Bitmap bmp = BitmapFactory.decodeFile(imagePath, opts);
            mCropImageView.setImageBitmap(bmp);
        } catch (OutOfMemoryError err) {
        }
    }

    @OnClick(R.id.tv_back)
    void onClose() {
        finish();
    }

    @OnClick(R.id.tv_right)
    void onRight() {
        String cropImagePath = saveImageToExternalStorage(mCropImageView.getCroppedBitmap());
//        String cropImagePath = saveImageToExternalStorage(mCropImageView.getCroppedImage());
//        Intent data = new Intent();
//        data.putExtra(ARG_CROP_RESULT, cropImagePath);
//        setResult(RESULT_OK, data);

        Intent intent = new Intent();
        intent.putExtra("cropImagePath", cropImagePath);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * 保存图片到磁盘并返回地址
     *
     * @param finalBitmap
     * @return
     */
    private String saveImageToExternalStorage(Bitmap finalBitmap) {
        String root = SystemUtils.getSdcardPath() + File.separator + AppSettings.getImageSavePath();
        File myDir = new File(root + File.separator + AppSettings.getImageSavePath());
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-" + n + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Tell the media scanner about the new file so that it is
        // immediately available to the user.
        MediaScannerConnection.scanFile(this, new String[]{file.toString()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                });

        String filePath = file.getParentFile().getAbsolutePath() + File.separator + fname;

        return filePath;
    }


    public static int computeSampleSize(BitmapFactory.Options options,
                                        int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options,
                                                int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == -1) ? 1 :
                (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 :
                (int) Math.min(Math.floor(w / minSideLength),
                        Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            return lowerBound;
        }

        if ((maxNumOfPixels == -1) &&
                (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }
}

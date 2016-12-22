package com.ruihai.xingka.ui.caption;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.isseiaoki.simplecropview.CropImageView;
import com.ruihai.android.common.utils.SystemUtils;
import com.ruihai.iconicfontengine.widget.IconicFontTextView;
import com.ruihai.xingka.AppSettings;
import com.ruihai.xingka.R;
import com.ruihai.xingka.ui.BaseActivity;
import com.ruihai.xingka.utils.AppUtility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 图片裁切页
 */
public class CropImageActivity extends BaseActivity {

    public static final String ARG_CROP_IMAGE_PATH = "image_path";
    public static final String ARG_CROP_RESULT = "crop_result";

    @BindView(R.id.tv_back)
    IconicFontTextView mBackView;
    @BindView(R.id.tv_title)
    TextView mTitleView;
    @BindView(R.id.btn_9_16)
    IconicFontTextView mButton9_16;
    @BindView(R.id.btn_3_4)
    IconicFontTextView mButton3_4;
    @BindView(R.id.btn_1_1)
    IconicFontTextView mButton1_1;
    @BindView(R.id.btn_4_3)
    IconicFontTextView mButton4_3;
    @BindView(R.id.btn_16_9)
    IconicFontTextView mButton16_9;
    @BindView(R.id.crop_imageview)
    CropImageView mCropImageView;

    private String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);
        ButterKnife.bind(this);

        imagePath = getIntent().getStringExtra(ARG_CROP_IMAGE_PATH);

        initViews();
    }

    private void initViews() {
        mBackView.setText("{xk-close}");
        mTitleView.setText(R.string.caption_crop_image);

        mButton4_3.setSelected(true);

        mCropImageView.setInitialFrameScale(1.0f);
//        Glide.with(mContext)
//                .load(imagePath)
//                .error(R.drawable.default_error)
//                .into(mCropImageView);
        Log.i("TAG", "----图片路径2222----->" + imagePath);
        mCropImageView.setImageBitmap(BitmapFactory.decodeFile(imagePath));
//        BitmapFactory.Options bfOptions = new BitmapFactory.Options();
//        bfOptions.inDither = false;
//        bfOptions.inPurgeable = true;
//        bfOptions.inTempStorage = new byte[12 * 1024];
//        bfOptions.inJustDecodeBounds = true;
//        File file = new File(imagePath);
//        FileInputStream fs = null;
//        try {
//            fs = new FileInputStream(file);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        Bitmap bmp = null;
//        if (fs != null)
//            try {
//                bmp = BitmapFactory.decodeFileDescriptor(fs.getFD(), null, bfOptions);
//                mCropImageView.setImageBitmap(bmp);
//            } catch (IOException e) {
//                e.printStackTrace();
//            } finally {
//                if (fs != null) {
//                    try {
//                        fs.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }

        //对图片进行压缩处理(测试)
//        BitmapFactory.Options opts = new BitmapFactory.Options();
//        opts.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(imagePath, opts);
//
//        opts.inSampleSize = computeSampleSize(opts, -1, 128 * 128);
//        //这里一定要将其设置回false，因为之前我们将其设置成了true
//        opts.inJustDecodeBounds = false;
//        try {
//            Bitmap bmp = BitmapFactory.decodeFile(imagePath, opts);
//            mCropImageView.setImageBitmap(bmp);
//        } catch (OutOfMemoryError err) {
//        }
    }

    @OnClick(R.id.tv_back)
    void onClose() {
        finish();
    }

    @OnClick(R.id.tv_right)
    void onRight() {
        String cropImagePath = saveImageToExternalStorage(mCropImageView.getCroppedBitmap());
        Intent data = new Intent();
        data.putExtra(ARG_CROP_RESULT, cropImagePath);
        setResult(RESULT_OK, data);
        finish();
    }

    @OnClick({R.id.btn_9_16, R.id.btn_3_4, R.id.btn_1_1, R.id.btn_4_3, R.id.btn_16_9})
    void cropImage(View v) {
        switch (v.getId()) {
            case R.id.btn_9_16:
                mCropImageView.setCropMode(CropImageView.CropMode.RATIO_9_16);
                setRationSelected(0);
                break;
            case R.id.btn_3_4:
                mCropImageView.setCropMode(CropImageView.CropMode.RATIO_3_4);
                setRationSelected(1);
                break;
            case R.id.btn_1_1:
                mCropImageView.setCropMode(CropImageView.CropMode.RATIO_1_1);
                setRationSelected(2);
                break;
            case R.id.btn_4_3:
                mCropImageView.setCropMode(CropImageView.CropMode.RATIO_4_3);
                setRationSelected(3);
                break;
            case R.id.btn_16_9:
                mCropImageView.setCropMode(CropImageView.CropMode.RATIO_16_9);
                setRationSelected(4);
                break;
        }
    }

    private void setRationSelected(int selectedIndex) {
        TextView[] buttons = {mButton9_16, mButton3_4, mButton1_1, mButton4_3, mButton16_9};
        for (int i = 0; i < buttons.length; i++) {
            if (i == selectedIndex) {
                buttons[i].setSelected(true);
            } else {
                buttons[i].setSelected(false);
            }
        }
    }

    @OnClick(R.id.btn_rotate)
    void ratateImage() {
        Bitmap bitmap = mCropImageView.getImageBitmap();
        mCropImageView.setImageBitmap(rotateBitmap(90, bitmap));
    }


    /**
     * 调整图片旋转方向
     *
     * @param degree 图片旋转角度
     * @param bitmap 需旋转方向的图片
     * @return 旋转后的图片
     */
    private Bitmap rotateBitmap(int degree, Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);

        Bitmap bm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);
        return bm;
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


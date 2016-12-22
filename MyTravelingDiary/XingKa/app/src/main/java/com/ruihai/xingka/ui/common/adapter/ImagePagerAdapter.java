package com.ruihai.xingka.ui.common.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.flyco.dialog.entity.DialogMenuItem;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.NormalListDialog;
import com.orhanobut.logger.Logger;
import com.ruihai.android.common.utils.SystemUtils;
import com.ruihai.android.network.task.TaskException;
import com.ruihai.android.network.task.WorkTask;
import com.ruihai.xingka.AppSettings;
import com.ruihai.xingka.R;
import com.ruihai.xingka.utils.AppUtility;
import com.ruihai.xingka.utils.ImageUtil;
import com.ruihai.xingka.utils.QiniuHelper;
import com.ruihai.xingka.utils.glide.GlideHelper;
import com.ruihai.xingka.widget.ProgressHUD;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by zecker on 15/11/17.
 */
public class ImagePagerAdapter extends PagerAdapter {

    private static final long PROGRESS_DELAY = 300L;

    private List<String> mPaths;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private String userNick;

    public ImagePagerAdapter(Context context, List<String> paths, String userNick) {
        this.mContext = context;
        this.mPaths = paths;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.userNick = userNick;
        Log.i("TAG", "------ImagePagerAdapter------>" + userNick);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.item_full_image, container, false);

        final PhotoView imageView = (PhotoView) itemView.findViewById(R.id.full_image);
        final ProgressBar progressBar = (ProgressBar) itemView.findViewById(R.id.full_progress);

        progressBar.animate().setStartDelay(PROGRESS_DELAY).alpha(1f);

        final String path = mPaths.get(position);
        if (path.startsWith("http")) { //远程图片
            GlideHelper.loadFullImageWithUrl(path, imageView, new GlideHelper.ImageLoadingListener() {

                @Override
                public void onLoaded() {
                    progressBar.animate().cancel();
                    progressBar.animate().alpha(0f);
                }

                @Override
                public void onFailed() {
                    progressBar.animate().alpha(0f);
                }
            });

            // 长按调取保存手机到手机
            imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onPopupMenu(path);
                    return true;
                }
            });
        } else { // 本地图片
            Uri uri = Uri.fromFile(new File(path));
            GlideHelper.loadWithUri(uri, imageView);
            progressBar.animate().cancel();
            progressBar.animate().alpha(0f);
        }

        /** 单击关闭当前浏览页 */
        imageView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                if (!((Activity) mContext).isFinishing()) {
                    ((Activity) mContext).onBackPressed();
                }
            }
        });

        container.addView(itemView);

        return itemView;
    }

    /**
     * 弹出将照片保存到手机按钮
     *
     * @param bitmap
     */
    private void onPopupMenu(final Bitmap bitmap) {
        ArrayList<DialogMenuItem> menuItems = new ArrayList<>();
        menuItems.add(new DialogMenuItem("保存到手机", R.mipmap.ic_winstyle_download));

        final NormalListDialog dialog = new NormalListDialog(mContext, menuItems);
        dialog.title("请选择") //
                .isTitleShow(false) //
                .itemPressColor(Color.parseColor("#CACACA")) //
                .itemTextColor(Color.parseColor("#303030")) //
                .itemTextSize(15) //
                .cornerRadius(2) //
                .widthScale(0.75f) //
                .show();

        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                saveImageToExternalStorage(bitmap);
                dialog.dismiss();
            }
        });
    }


    /**
     * 弹出将照片保存到手机按钮
     *
     * @param imageBaseUrl
     */
    private void onPopupMenu(final String imageBaseUrl) {
        ArrayList<DialogMenuItem> menuItems = new ArrayList<>();
        menuItems.add(new DialogMenuItem("保存到手机", R.mipmap.ic_winstyle_download));

        final NormalListDialog dialog = new NormalListDialog(mContext, menuItems);
        dialog.title("请选择") //
                .isTitleShow(false) //
                .itemPressColor(Color.parseColor("#CACACA")) //
                .itemTextColor(Color.parseColor("#303030")) //
                .itemTextSize(15) //
                .cornerRadius(2) //
                .widthScale(0.75f) //
                .show();

        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                //String imageUrl = QiniuHelper.getLargeWatermarkWithUrl(imageBaseUrl, userNick);
                //String imageUrl = QiniuHelper.getTopicCoverWithKey(imageBaseUrl);
                //Logger.e(imageUrl);
                downloadImage(imageBaseUrl);
                dialog.dismiss();
            }
        });
    }

    private void downloadImage(final String imageUrl) {
        new WorkTask<Void, Void, Bitmap>() {

            @Override
            protected void onPrepare() {
                super.onPrepare();
                ProgressHUD.showLoadingMessage(mContext, "图片下载中...", true);
            }

            @Override
            public Bitmap workInBackground(Void... params) throws TaskException {

                try {
                    Bitmap theBitmap = Glide.with(mContext)
                            .load(imageUrl)
                            .asBitmap()
                            .into(-1, -1)
                            .get();
                    return theBitmap;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onSuccess(Bitmap bitmap) {
                super.onSuccess(bitmap);
                if (bitmap != null) {
                    if (!TextUtils.isEmpty(userNick)) {
                        Bitmap waterBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.water_mark_icon);
//                        int width = waterBitmap.getWidth();
//                        int height = waterBitmap.getHeight();
//                        // 设置想要的大小
//                        int newWidth = AppUtility.dip2px(24);
//                        int newHeight = AppUtility.dip2px(24);
//                        // 计算缩放比例
//                        float scaleWidth = ((float) newWidth) / width;
//                        float scaleHeight = ((float) newHeight) / height;
//                        // 取得想要缩放的matrix参数
//                        Matrix matrix = new Matrix();
//                        matrix.postScale(scaleWidth, scaleHeight);
//                        // 得到新的图片
//                        waterBitmap = Bitmap.createBitmap(waterBitmap, 0, 0, width, height, matrix, true);
                        waterBitmap = ImageUtil.scaleWithWH(waterBitmap, AppUtility.dip2px(24), AppUtility.dip2px(24));
                        //Bitmap watermarkBitmap = ImageUtil.createWaterMaskCenter(bitmap, waterBitmap);
                        Bitmap watermarkBitmap = ImageUtil.createWaterMaskLeftBottom(mContext, bitmap, waterBitmap, 4, 4);
//                      watermarkBitmap = ImageUtil.createWaterMaskRightBottom(mContext, watermarkBitmap, waterBitmap, 0, 0);
//                      watermarkBitmap = ImageUtil.createWaterMaskLeftTop(mContext, watermarkBitmap, waterBitmap, 0, 0);
//                      watermarkBitmap = ImageUtil.createWaterMaskRightTop(mContext, watermarkBitmap, waterBitmap, 0, 0);
//                      Bitmap textBitmap = ImageUtil.drawTextToLeftTop(mContext, watermarkBitmap, "左上角", 16, Color.RED, 0, 0);
//                      textBitmap = ImageUtil.drawTextToRightBottom(mContext, textBitmap, "右下角", 16, Color.RED, 0, 0);
//                      textBitmap = ImageUtil.drawTextToRightTop(mContext, textBitmap, "右上角", 16, Color.RED, 0, 0);
                        Bitmap textBitmap = ImageUtil.drawTextToLeftBottom(mContext, watermarkBitmap, "PHOTO BY " + userNick, 12, Color.WHITE, 32, 11);
//                      textBitmap = ImageUtil.drawTextToCenter(mContext, textBitmap, "中间", 16, Color.RED);
                        saveImageToExternalStorage(textBitmap);
                    } else {
                        saveImageToExternalStorage(bitmap);
                    }
                } else {
                    ProgressHUD.dismiss();
                    AppUtility.showToast(mContext.getString(R.string.msg_save_pic_faild));
                }
            }

            @Override
            protected void onFinished() {
                super.onFinished();
            }
        }.execute();
    }

    private void saveImageToExternalStorage(Bitmap finalBitmap) {
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

        ProgressHUD.dismiss();
        String path = file.getParentFile().getAbsolutePath();
        AppUtility.showToast(String.format(mContext.getString(R.string.msg_save_pic_success), path));
        // Tell the media scanner about the new file so that it is
        // immediately available to the user.
        MediaScannerConnection.scanFile(mContext, new String[]{file.toString()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                });
    }

    @Override
    public int getCount() {
        return mPaths.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}

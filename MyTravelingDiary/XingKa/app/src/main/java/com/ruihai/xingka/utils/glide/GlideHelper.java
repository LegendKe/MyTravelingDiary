package com.ruihai.xingka.utils.glide;

import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.ViewPropertyAnimation;
import com.rohitarya.glide.facedetection.transformation.CenterFaceCrop;
import com.ruihai.xingka.R;
import com.ruihai.xingka.XKApplication;
import com.ruihai.xingka.utils.QiniuHelper;

/**
 * Created by zecker on 15/11/16.
 */
public class GlideHelper {

    private static final ViewPropertyAnimation.Animator ANIMATOR =
            new ViewPropertyAnimation.Animator() {
                @Override
                public void animate(View view) {
                    view.setAlpha(0f);
                    view.animate().alpha(1f);
                }
            };

    public static void loadResource(@DrawableRes int drawableId, @NonNull ImageView image) {
        Glide.with(XKApplication.getInstance().getApplicationContext()).load(drawableId).animate(ANIMATOR).into(image);
    }

    public static void loadWithUri(@NonNull Uri uri, @NonNull ImageView image) {
        Glide.with(image.getContext()).load(uri).animate(ANIMATOR).into(image);
    }

    public static void loadThumbImageWithKey(@NonNull String imageKey, @NonNull final ImageView image) {
        Glide.with(XKApplication.getInstance().getApplicationContext())
                .load(QiniuHelper.getMediumWithKey(imageKey))
                .centerCrop()
                .crossFade()
                .placeholder(R.mipmap.default_logo_gray)
                .error(R.mipmap.icon_caption_error)
                .thumbnail(
                        Glide.with(image.getContext())
                                .load(QiniuHelper.getThumbnailWithKey(imageKey))
                                .centerCrop()
                                .animate(ANIMATOR)
                                .diskCacheStrategy(DiskCacheStrategy.SOURCE))
                .into(new GlideDrawableTarget(image));
    }

    /**
     * 根据图片原地址加载小图片显示
     *
     * @param imageUrl
     * @param image
     */
    public static void loadThumbImageWithUrl(@NonNull String imageUrl, @NonNull final ImageView image) {
        Glide.with(XKApplication.getInstance().getApplicationContext())
                .load(QiniuHelper.getMediumWithUrl(imageUrl))
                .dontAnimate()
                .placeholder(R.mipmap.default_square_logo)
                .crossFade()
                .thumbnail(
                        Glide.with(image.getContext())
                                .load(QiniuHelper.getThumbnailWithUrl(imageUrl))
                                .animate(ANIMATOR)
                                .diskCacheStrategy(DiskCacheStrategy.SOURCE))
                .into(new GlideDrawableTarget(image));
    }

    /**
     * 根据图片Key加载大图显示
     *
     * @param imageKey
     * @param image
     * @param listener
     */
    public static void loadFullImageWithKey(@NonNull String imageKey, @NonNull final ImageView image, @Nullable final ImageLoadingListener listener) {
        final String imageUrl = QiniuHelper.getLargeWithKey(imageKey);
        Glide.with(XKApplication.getInstance().getApplicationContext())
                .load(imageUrl)
                .centerCrop()
                .crossFade()
                .placeholder(R.mipmap.default_logo_gray)
                .thumbnail(Glide.with(image.getContext())
                        .load(QiniuHelper.getMediumWithKey(imageKey))
                        .animate(ANIMATOR)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE))
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .listener(new GlideDrawableListener() {
                    @Override
                    public void onSuccess(String url) {
                        if (url.equals(imageUrl)) {
                            if (listener != null) listener.onLoaded();
                        }
                    }

                    @Override
                    public void onFail(String url) {
                        if (listener != null) listener.onFailed();
                    }
                })
                .into(new GlideDrawableTarget(image));
    }

    /**
     * 根据图片原地址加载图片到图片控件
     *
     * @param imageBaseUrl
     * @param image
     * @param listener
     */
    public static void loadFullImageWithUrl(@NonNull String imageBaseUrl, @NonNull final ImageView image, @Nullable final ImageLoadingListener listener) {
        final String imageUrl = QiniuHelper.getLargeWithUrl(imageBaseUrl);
        Glide.with(XKApplication.getInstance().getApplicationContext())
                .load(imageUrl)
                .dontAnimate()
                .placeholder(image.getDrawable())
                .thumbnail(Glide.with(image.getContext())
                        .load(QiniuHelper.getMediumWithUrl(imageBaseUrl))
                        .animate(ANIMATOR)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE))
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .listener(new GlideDrawableListener() {
                    @Override
                    public void onSuccess(String url) {
                        if (url.equals(imageUrl)) {
                            if (listener != null) listener.onLoaded();
                        }
                    }

                    @Override
                    public void onFail(String url) {
                        if (listener != null) listener.onFailed();
                    }
                })
                .into(new GlideDrawableTarget(image));
    }


    /**
     * 根据图片Key加载图说列表图片显示
     *
     * @param imageKey
     * @param image
     * @param listener
     */
    public static void loadTopicCoverWithKey(@NonNull String imageKey, @NonNull final ImageView image, @Nullable final ImageLoadingListener listener) {
        final String imageUrl = QiniuHelper.getTopicCoverWithKey(imageKey);
        Glide.with(XKApplication.getInstance().getApplicationContext())
                .load(imageUrl)
                .centerCrop()
                .crossFade()
                .transform(new CenterFaceCrop())
                .placeholder(R.mipmap.default_loading_logo)
                .thumbnail(Glide.with(image.getContext())
                        .load(QiniuHelper.getThumbnailWithKey(imageKey))
                        .animate(ANIMATOR)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE))
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .listener(new GlideDrawableListener() {
                    @Override
                    public void onSuccess(String url) {
                        if (url.equals(imageUrl)) {
                            if (listener != null) listener.onLoaded();
                        }
                    }

                    @Override
                    public void onFail(String url) {
                        if (listener != null) listener.onFailed();
                    }
                })
                .into(new GlideDrawableTarget(image));
    }


    public interface ImageLoadingListener {
        void onLoaded();

        void onFailed();
    }
}

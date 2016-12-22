package com.ruihai.xingka.ui.mine;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.ruihai.xingka.Global;
import com.ruihai.xingka.R;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.api.model.User;
import com.ruihai.xingka.ui.BaseActivity;
import com.ruihai.xingka.ui.common.enter.EmojiUtils;
import com.ruihai.xingka.utils.AppUtility;
import com.ruihai.xingka.utils.AsyncImageLoader;
import com.ruihai.xingka.utils.CreateQRUtils;
import com.ruihai.xingka.utils.QiniuHelper;
import com.ruihai.xingka.utils.Security;
import com.ruihai.xingka.widget.ProgressHUD;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 二维码
 * Created by apple on 15/8/19.
 */
public class PictureDisplayActivity extends BaseActivity {

    @BindView(R.id.sdv_picture)
    SimpleDraweeView mPicture;
    @BindView(R.id.tv_car_brand)
    TextView mCarBrand;
    @BindView(R.id.sdv_avatar)
    SimpleDraweeView mAvatar;
    @BindView(R.id.tv_nickname)
    TextView mNickName;
    @BindView(R.id.iv_sex)
    ImageView mSex;
    @BindView(R.id.tv_xingka_number)
    TextView mNumber;

    private Bitmap logoBm;//用户头像logo
    private String text;//用于生成二维码的字符串

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_display);
        ButterKnife.bind(this);
        initViews();
    }

    private void initViews() {
        String account = String.valueOf(currentUser.getAccount());
        String avatar = currentUser.getAvatar();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        //ProgressHUD.showLoadingMessage(this, "大咖息怒，跪着干脆面，为你加载中……", false);
        int i = bundle.getInt("show");
        if (i == 1) {//  车型
            mCarBrand.setVisibility(View.VISIBLE);
            String carBrandIcon = bundle.getString("brandIcon");
            Uri brandUri = Uri.parse(QiniuHelper.getThumbnail200Url(carBrandIcon));
            mPicture.setImageURI(brandUri);
            ProgressHUD.dismiss();
            String carBrandName = bundle.getString("brandName");
            // Log.e("------->", carBrandName);
            mCarBrand.setText(carBrandName);
        } else {//  二维码
            mCarBrand.setVisibility(View.INVISIBLE);

            Uri imageUri = Uri.parse(QiniuHelper.getThumbnail200Url(currentUser.getAvatar()));
            mAvatar.setImageURI(imageUri);

            if (currentUser.getSex() == 1) {
                mSex.setImageResource(R.mipmap.icon_boy);
            } else if (currentUser.getSex() == 2) {
                mSex.setImageResource(R.mipmap.icon_girl);
            } else {
                mSex.setVisibility(View.GONE);
            }

            mNickName.setText(EmojiUtils.fromStringToEmoji1(currentUser.getNickname(), mContext));
            mNumber.setText(account);

            //用户行咖号加密,编码
            String SUId = Security.aesEncrypt(account);

            try {
                text = String.format(Global.SHARE_CARD_URL, URLEncoder.encode(SUId, "utf-8").replace("%", "-"));

                if (!TextUtils.isEmpty(avatar)) {//用户头像不为空
                    ProgressHUD.showLoadingMessage(this, "二维码加载中……", false);
                    new AsyncImageLoader().loadDrawable(QiniuHelper.getThumbnail96Url(avatar), new AsyncImageLoader.ImageCallback() {
                        @Override
                        public void imageLoaded(Drawable imageDrawable, String imageUrl) {
                            Bitmap bitmap = AppUtility.drawableToBitmap(imageDrawable);
                            logoBm = AppUtility.getRoundedCornerBitmap(bitmap, AppUtility.dip2px(2), AppUtility.dip2px(1));
                            mPicture.setImageBitmap(CreateQRUtils.createQRImage(PictureDisplayActivity.this, text, null));//生成二维码
                            ProgressHUD.dismiss();
                        }
                    });
//                    ImageRequest imageRequest = ImageRequestBuilder
//                            .newBuilderWithSource(Uri.parse(QiniuHelper.getThumbnail96Url(imageUrl)))
//                            .setProgressiveRenderingEnabled(true)
//                            .build();
//                    ImagePipeline imagePipeline = Fresco.getImagePipeline();
//                    DataSource<CloseableReference<CloseableImage>>
//                            dataSource = imagePipeline.fetchDecodedImage(imageRequest, mContext);
//                    dataSource.subscribe(new BaseBitmapDataSubscriber() {
//                                             @Override
//                                             public void onNewResultImpl(@Nullable Bitmap bitmap) {
//                                                 // You can use the bitmap in only limited ways
//                                                 // No need to do any cleanup.
//                                                 logoBm = AppUtility.getRoundedCornerBitmap(bitmap, AppUtility.dip2px(2), AppUtility.dip2px(1));
//                                                 mPicture.setImageBitmap(CreateQRUtils.createQRImage(PictureDisplayActivity.this, text, logoBm));//生成二维码
//                                                 // ProgressHUD.dismiss();
//                                             }
//                                             @Override
//                                             public void onFailureImpl(DataSource dataSource) {
//                                                 // No cleanup required here.
//                                                 logoBm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);//默认logo
//                                                 mPicture.setImageBitmap(CreateQRUtils.createQRImage(PictureDisplayActivity.this, text, AppUtility.getRoundedCornerBitmap(logoBm, AppUtility.dip2px(2), AppUtility.dip2px(1))));//生成二维码
//                                                 // ProgressHUD.dismiss();
//                                             }
//                                         },
//                            CallerThreadExecutor.getInstance());
                } else {//用户头像为空
                    logoBm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);//默认logo
                    mPicture.setImageBitmap(CreateQRUtils.createQRImage(this, text, logoBm));//生成二维码
                    // ProgressHUD.dismiss();
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return true;
    }

//    public Bitmap returnBitMap(String url) {
//        URL myFileUrl = null;
//        Bitmap bitmap = null;
//        try {
//            myFileUrl = new URL(url);
//        } catch (MalformedURLException e) {
//            AppUtility.showToast("11111");
//            e.printStackTrace();
//        }
//        try {
//            HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
//            conn.setDoInput(true);
//            conn.connect();
//            InputStream is = conn.getInputStream();
//            bitmap = BitmapFactory.decodeStream(is);
//            is.close();
//        } catch (IOException e) {
//            AppUtility.showToast("22222");
//            e.printStackTrace();
//        }
//        return bitmap;
//    }

}

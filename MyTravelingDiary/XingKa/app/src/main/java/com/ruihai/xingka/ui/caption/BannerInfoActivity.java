package com.ruihai.xingka.ui.caption;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.TextView;

import com.ruihai.android.ui.widget.webview.WebViewStateListener;
import com.ruihai.android.ui.widget.webview.XKWebContainerView;
import com.ruihai.iconicfontengine.widget.IconicFontTextView;
import com.ruihai.xingka.R;
import com.ruihai.xingka.api.model.BannerInfoRepo;
import com.ruihai.xingka.ui.BaseActivity;
import com.ruihai.xingka.utils.UnsupportedProtcolInterceptor;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 首页banner活动详情页
 */
public class BannerInfoActivity extends BaseActivity {
    public final static String KEY_BANNER_ITEM = "BANNER_ITEM";

    public static void launch(Activity from, BannerInfoRepo.BannerInfo item) {
        Intent intent = new Intent(from, BannerInfoActivity.class);
        intent.putExtra(KEY_BANNER_ITEM, item);
        from.startActivity(intent);
    }

    @BindView(R.id.webview_view)
    XKWebContainerView webContainerView;
    @BindView(R.id.tv_right)
    IconicFontTextView rightView;
    @BindView(R.id.tv_title)
    TextView titleView;
    BannerInfoRepo.BannerInfo mBannerInfo;
    private String mCurrentUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner_info);
        ButterKnife.bind(this);
        //初始化右侧分享按钮
        rightView.setText("{xk-share}");
        mBannerInfo = (BannerInfoRepo.BannerInfo) getIntent().getSerializableExtra(KEY_BANNER_ITEM);
        titleView.setText(mBannerInfo.getTitle());
        String url = mBannerInfo.getContent1();

        webContainerView.addLoadingInterceptor(new UnsupportedProtcolInterceptor(this));
        webContainerView.loadUrl(url);

        webContainerView.addOnWebViewStateListener(new WebViewStateListener() {
            @Override
            public void onStartLoading(String url, Bitmap favicon) {

            }

            @Override
            public void onError(int errorCode, String description, String failingUrl) {
            }

            @Override
            public void onFinishLoaded(String loadedUrl) {

                mCurrentUrl = loadedUrl;
            }

            @Override
            public void onProgressChanged(WebView view, int progress) {

            }
        });
    }

    @OnClick(R.id.tv_back)
    void backClicked() {
        onBackPressed();
    }

    @OnClick(R.id.tv_right)
    void shareClicked() {
        ShareBannerActivity.launch(BannerInfoActivity.this, mBannerInfo, mCurrentUrl);
    }

    @Override
    public void onBackPressed() {
        if (webContainerView.getTitle().equalsIgnoreCase("index")) {
            finish();
        } else {
            if (webContainerView.canGoBack()) {
                webContainerView.goBack();
            } else {
                super.onBackPressed();
            }
        }
    }

}

package com.ruihai.xingka.ui.mine;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.ruihai.android.ui.widget.webview.WebViewStateListener;
import com.ruihai.android.ui.widget.webview.XKWebContainerView;
import com.ruihai.iconicfontengine.widget.IconicFontTextView;
import com.ruihai.xingka.Global;
import com.ruihai.xingka.R;
import com.ruihai.xingka.ui.BaseActivity;
import com.ruihai.xingka.utils.UnsupportedProtcolInterceptor;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by lqfang on 15/9/29.
 */
public class AboutActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView titleView;
    @BindView(R.id.tv_right)
    IconicFontTextView rightView;
    @BindView(R.id.webview_view)
    XKWebContainerView webContainerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);

        //titleView.setText("关于行咖");
        rightView.setText("{xk-share}");

        webContainerView.addLoadingInterceptor(new UnsupportedProtcolInterceptor(this));
        webContainerView.loadUrl(Global.ABOUTUS_URL);
        webContainerView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webContainerView.addOnWebViewStateListener(new WebViewStateListener() {
            @Override
            public void onStartLoading(String url, Bitmap favicon) {

            }

            @Override
            public void onError(int errorCode, String description, String failingUrl) {

            }

            @Override
            public void onFinishLoaded(String loadedUrl) {

            }

            @Override
            public void onProgressChanged(WebView view, int progress) {
                titleView.setText(webContainerView.getTitle());
                if ("用户协议".equals(webContainerView.getTitle())) {
                    rightView.setVisibility(View.GONE);
                } else {
                    rightView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @OnClick(R.id.tv_back)
    void onBack() {
        onBackPressed();
    }

    @OnClick(R.id.tv_right)
    void onShare() {
        startActivity(new Intent(this, ShareAboutActivity.class));
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

package com.ruihai.xingka.ui.common;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.ruihai.android.ui.widget.webview.WebViewStateListener;
import com.ruihai.android.ui.widget.webview.XKWebContainerView;
import com.ruihai.iconicfontengine.widget.IconicFontTextView;
import com.ruihai.xingka.R;
import com.ruihai.xingka.ui.BaseActivity;
import com.ruihai.xingka.utils.UnsupportedProtcolInterceptor;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WebActivity extends BaseActivity {
    @BindView(R.id.webview_view)
    XKWebContainerView webContainerView;
    @BindView(R.id.tv_right)
    IconicFontTextView rightView;
    @BindView(R.id.tv_title)
    TextView titleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner_info);
        ButterKnife.bind(this);
        //初始化右侧分享按钮
        //rightView.setText("{xk-share}");
        rightView.setVisibility(View.GONE);
        titleView.setText("系统消息");
        String url = getIntent().getStringExtra("URL");

        webContainerView.addLoadingInterceptor(new UnsupportedProtcolInterceptor(this));
        webContainerView.loadUrl(url);
        webContainerView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webContainerView.getSettings().setJavaScriptEnabled(true);
        webContainerView.getSettings().setAllowFileAccess(true);//启用WebView访问文件数据
        webContainerView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        // 自适应屏幕
        webContainerView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webContainerView.getSettings().setUseWideViewPort(true);
        webContainerView.getSettings().setLoadWithOverviewMode(true);
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

            }
        });
    }

    @OnClick(R.id.tv_back)
    void backClicked() {
        onBackPressed();
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

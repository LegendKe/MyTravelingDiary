package com.ruihai.xingka.ui.common;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
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

/**
 * 公用的web页面
 * Created by gjzhang on 15/8/17.
 */
public class CommonWebActivity extends BaseActivity {
    public static final String WEB_URL = "WEB_URL";
    public static final String WEB_TITLE = "WEB_TITLE";
    @BindView(R.id.webview_view)
    XKWebContainerView webContainerView;
    @BindView(R.id.tv_right)
    IconicFontTextView rightView;
    @BindView(R.id.tv_title)
    TextView titleView;
    private String mTitle;
    private String mUrl;

    public static void launch(Context from, String title, String url) {
        Intent intent = new Intent(from, CommonWebActivity.class);
        intent.putExtra(WEB_TITLE, title);
        intent.putExtra(WEB_URL, url);
        from.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner_info);
        ButterKnife.bind(this);
        mTitle = getIntent().getStringExtra(WEB_TITLE);
        mUrl = getIntent().getStringExtra(WEB_URL);
        //初始化右侧分享按钮
        //rightView.setText("{xk-share}");
        rightView.setVisibility(View.GONE);
        titleView.setText(mTitle);
        webContainerView.addLoadingInterceptor(new UnsupportedProtcolInterceptor(this));
        webContainerView.loadUrl(mUrl);

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


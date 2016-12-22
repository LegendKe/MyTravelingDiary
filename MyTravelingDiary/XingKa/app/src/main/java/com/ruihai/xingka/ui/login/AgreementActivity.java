package com.ruihai.xingka.ui.login;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ruihai.xingka.Global;
import com.ruihai.xingka.R;
import com.ruihai.xingka.ui.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by apple on 15/8/17.
 */
public class AgreementActivity extends BaseActivity {

    @BindView(R.id.webView)
    WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agreement);
        ButterKnife.bind(this);

        initWebView();
    }

    private void initWebView() {
        // 支持Javascript
        mWebView.getSettings().setJavaScriptEnabled(true);
        // 优先使用缓存
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        // 加载用户注册协议
        mWebView.loadUrl(Global.AGREEMENT_URL);
    }

    @OnClick(R.id.btn_back)
    void onBackClicked(){
        finish();
    }

    @OnClick(R.id.btn_agree)
    void agreement() {
        finish();
    }


}

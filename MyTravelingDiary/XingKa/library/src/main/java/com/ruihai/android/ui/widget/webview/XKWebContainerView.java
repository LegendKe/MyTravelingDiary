package com.ruihai.android.ui.widget.webview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.ruihai.android.R;

public class XKWebContainerView extends RelativeLayout {

    private XKWebView xkWebView;

    private ProgressBar progressBar;

    private ViewGroup errorView;

    private Button reloadButton;

    private static final Animation animation = new AlphaAnimation(1f, 0f);

    public XKWebContainerView(Context context) {
        super(context);
        initialize();
    }

    public XKWebContainerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
        setupWebSettings(attrs);
    }

    public XKWebContainerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
        setupWebSettings(attrs);
    }

    private void setupWebSettings(AttributeSet attrs) {
        TypedArray args = getContext().obtainStyledAttributes(attrs, R.styleable.wv);
        xkWebView.setupWebSettings(args);
        args.recycle();
    }

    private void initialize() {
        bindViews();
        bindWebViewState();
        animation.setDuration(1000);
    }

    private void bindWebViewState() {
        xkWebView.addOnWebViewStateListener(new WebViewStateListener() {
            @Override
            public void onStartLoading(String url, Bitmap favicon) {
                progressBar.clearAnimation();
                progressBar.setProgress(0);
                progressBar.setVisibility(View.VISIBLE);
                errorView.setVisibility(View.GONE);
            }

            @Override
            public void onError(int errorCode, String description, String failingUrl) {
                progressBar.setVisibility(View.GONE);
                xkWebView.setVisibility(View.GONE);
                errorView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinishLoaded(String loadedUrl) {
                progressBar.startAnimation(animation);
                progressBar.setVisibility(View.GONE);
                xkWebView.setVisibility(View.VISIBLE);
                errorView.setVisibility(View.GONE);
            }

            @Override
            public void onProgressChanged(WebView view, int progress) {
                if (xkWebView.getVisibility() != View.VISIBLE && progress > 80) {
                    xkWebView.setVisibility(View.VISIBLE);
                }
                progressBar.setProgress(progress);
            }
        });
    }

    private void bindViews() {
        View.inflate(getContext(), R.layout.view_web_container, this);
        xkWebView = (XKWebView) findViewById(R.id.web_view);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        errorView = (ViewGroup) findViewById(R.id.error_view);
        reloadButton = (Button) findViewById(R.id.reload_button);
        reloadButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (xkWebView != null) {
                    xkWebView.reload();
                }
            }
        });
    }

    public void addOnWebViewStateListener(WebViewStateListener webViewStateListener) {
        xkWebView.addOnWebViewStateListener(webViewStateListener);
    }

    public void addLoadingInterceptor(LoadingInterceptor loadingInterceptor) {
        xkWebView.addLoadingInterceptor(loadingInterceptor);
    }

    public void loadUrl(String url) {
        xkWebView.loadUrl(url);
    }

    public boolean canGoBack() {
        return xkWebView.canGoBack();
    }

    public void goBack() {
        xkWebView.goBack();
    }

    public String getTitle() {
        return xkWebView.getTitle();
    }

    public String getUrl() {
        return xkWebView.getUrl();
    }

    public XKWebView getWebView() {
        return xkWebView;
    }

    public String getUserAgentString() {
        return xkWebView.getSettings().getUserAgentString();
    }

    public void setUserAgentString(String ua) {
        xkWebView.getSettings().setUserAgentString(ua);

    }

    public android.webkit.WebSettings getSettings() {
        return xkWebView.getSettings();
    }

}

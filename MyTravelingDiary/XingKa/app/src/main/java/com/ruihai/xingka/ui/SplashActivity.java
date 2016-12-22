package com.ruihai.xingka.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ruihai.android.ui.widget.webview.WebViewStateListener;
import com.ruihai.android.ui.widget.webview.XKWebContainerView;
import com.ruihai.xingka.Global;
import com.ruihai.xingka.R;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.ui.login.LoginActivity;
import com.ruihai.xingka.utils.AppUtility;
import com.ruihai.xingka.utils.UnsupportedProtcolInterceptor;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * 欢迎页面.
 */
public class SplashActivity extends BaseActivity implements View.OnClickListener {
    private final int SPLASH_DISPLAY_LENGTH = 2 * 1000; // 延迟2秒
    private static final int RETRY_INTERVAL = 5;
    private int time = RETRY_INTERVAL;
    boolean isFirstIn = false;
    @BindView(R.id.webview_view)
    XKWebContainerView webContainerView;
    @BindView(R.id.slpash_layout)
    RelativeLayout splashLayout;
    @BindView(R.id.btn_count_down)
    TextView countDownBtn;//倒计时按钮
    private long currentMills;
    private boolean isLoaded;
    private boolean isContinueLoad;
    private TimerTask timerTask;
    private Timer timer;
    private int i = 5;
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            int mills = msg.what;
            if (mills == 0) {
                timer.cancel();
                skip();
            } else if (mills > 0) {
                String unReceive = getString(R.string.count_down_msg, mills);
                countDownBtn.setText(Html.fromHtml(unReceive));
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_splash);
        Calendar cal = Calendar.getInstance();// 当前日期
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        //默认欢迎页
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        SharedPreferences preferences = getSharedPreferences("first_pref",
                MODE_PRIVATE);
        isFirstIn = preferences.getBoolean("isFirstIn", true);

        countDownBtn.setOnClickListener(this);
        webContainerView.addLoadingInterceptor(new UnsupportedProtcolInterceptor(this));
        webContainerView.loadUrl(Global.COOPEN_URL);

        webContainerView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webContainerView.getSettings().setJavaScriptEnabled(true);
        webContainerView.getSettings().setAllowFileAccess(true);//启用WebView访问文件数据

        webContainerView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        // 自适应屏幕
        webContainerView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webContainerView.getSettings().setUseWideViewPort(true);
        webContainerView.getSettings().setLoadWithOverviewMode(true);
        //webContainerView.getSettings().addJavascriptInterface(new CodeBoyJsInterface(), "codeboy");
        webContainerView.addOnWebViewStateListener(new WebViewStateListener() {
            @Override
            public void onStartLoading(String url, Bitmap favicon) {

            }

            @Override
            public void onError(int errorCode, String description, String failingUrl) {
                skip();

            }

            @Override
            public void onFinishLoaded(String loadedUrl) {
                isLoaded = true;
                if (isContinueLoad) {
                    splashLayout.setVisibility(View.GONE);
                    webContainerView.setVisibility(View.VISIBLE);
                    countDownBtn.setVisibility(View.VISIBLE);
                    countDown();
                }
            }

            @Override
            public void onProgressChanged(WebView view, int progress) {

            }
        });
        // 创建一个Handler
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (AppUtility.isNetWorkAvilable()) {
                    if (isLoaded) {
                        splashLayout.setVisibility(View.GONE);
                        webContainerView.setVisibility(View.VISIBLE);
                        countDownBtn.setVisibility(View.VISIBLE);
                        countDown();
                    } else {
                        isContinueLoad = true;
                    }
                } else {
                    skip();
                }
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    /**
     * 倒数计时
     **/
    private void countDown() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = i;
                mHandler.sendMessage(msg);
                i--;
            }
        };
        timer = new Timer();
        timer.schedule(timerTask, 0, 1000);
    }

    @Override
    public void onClick(View v) {
        timer.cancel();
        skip();
    }

    public void skip() {
//        if (isFirstIn) {
//            // start ViewPageActivity
//            startActivity(new Intent(SplashActivity.this, GuideActivity.class));
//            SplashActivity.this.finish();
//        } else {
        // start LoginOfGuideActivity
        if (AccountInfo.getInstance().isLogin()) { // 判断用户是否登录
            //login(String.valueOf(AccountInfo.getInstance().loadAccount().getAccount()), AccountInfo.getInstance().loadAccount().getPassword());
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            SplashActivity.this.finish();
        } else {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            SplashActivity.this.finish();
        }
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null)
            timer.cancel();
    }
}

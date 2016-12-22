package com.ruihai.xingka.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.ruihai.xingka.Global;
import com.ruihai.xingka.R;
import com.ruihai.xingka.utils.AppUtility;
import com.umeng.onlineconfig.OnlineConfigAgent;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zecker on 16/5/25.
 */
public class SplashDialogFragment extends DialogFragment {

    @BindView(R.id.webview_view)
    WebView mWebView;

    @BindView(R.id.tv_count_down)
    TextView mCountDownText;//倒计时按钮

    private TimerTask timerTask;
    private Timer timer;
    private int displayTime = 5;

    private Activity mActivity;

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            int mills = msg.what;
            if (mills == 0) {
                timer.cancel();
                skip();
            } else if (mills > 0) {
                String unReceive = mActivity.getString(R.string.count_down_msg, mills);
                mCountDownText.setText(Html.fromHtml(unReceive));
            }
        }
    };

    public static SplashDialogFragment newInstance() {
        SplashDialogFragment fragment = new SplashDialogFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Translucent_NoTitleBar);

        String timeValue = OnlineConfigAgent.getInstance().getConfigParams(getActivity(), "splashDisplayTime");
        if (!TextUtils.isEmpty(timeValue)) {
            displayTime = Integer.parseInt(timeValue);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
        getDialog().getWindow()
                .getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_splash_dialog, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 设置WebView属性，能够执行Javascript脚本
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setAllowFileAccess(true); // 启用WebView访问文件数据

        if (AppUtility.isNetWorkAvilable()) {
            if (AppUtility.getNetworkType1() == 1) {
                mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
            } else {
                mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            }
        } else {
            mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
        //mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        // 自适应屏幕
        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        // 加载需要显示的网页
        mWebView.loadUrl(Global.COOPEN_URL);
        countDown();
    }

    @OnClick(R.id.tv_count_down)
    public void skipClicked(View view) {
        skip();
    }

    /**
     * 倒数计时
     **/
    private void countDown() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = displayTime;
                mHandler.sendMessage(msg);
                displayTime--;
            }
        };
        timer = new Timer();
        timer.schedule(timerTask, 500, 1000);
    }

    private void skip() {
        if (getDialog() != null) {
            dismissAllowingStateLoss();
            dismiss();
        }
    }


}

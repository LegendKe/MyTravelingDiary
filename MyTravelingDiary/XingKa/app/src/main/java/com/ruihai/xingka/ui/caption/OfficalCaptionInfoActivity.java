package com.ruihai.xingka.ui.caption;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.ruihai.xingka.R;
import com.ruihai.xingka.api.model.ShareInfo;
import com.ruihai.xingka.ui.BaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 官方图说的详情页面
 * Created by gjzhang on 16/1/14.
 */
public class OfficalCaptionInfoActivity extends BaseActivity {
    private static final String TAG = "OfficalCaptionInfoActivity";

    public final static String KEY_CAPTION_URL = "caption_url";
    public final static String KEY_CAPTION_GUID = "caption_guid";

    //缓存第一个加载的图片链接
    private String cacheFirstLoadImageUrl = null;

    public static void launch(Activity from, String webUrl, String gUid) {
        Intent intent = new Intent(from, OfficalCaptionInfoActivity.class);
        intent.putExtra(KEY_CAPTION_URL, webUrl);
        intent.putExtra(KEY_CAPTION_GUID, gUid);
        from.startActivity(intent);
    }

    @BindView(R.id.webview)
    WebView webView;
    @BindView(R.id.tv_right)
    TextView rightView;
    @BindView(R.id.tv_title)
    TextView titleView;

    private String mContentUrl; //web地址
    private String shareTitle;
    private String shareDesc;
    private String shareImage;
    private String shareUrl;
    private String mGUid;

    private final static int FILECHOOSER_RESULTCODE = 1;
    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> mUploadCallbackAboveL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        ButterKnife.bind(this);
        // 初始化右侧分享按钮
        rightView.setVisibility(View.GONE);
        rightView.setText("分享");
        titleView.setText("");
        mGUid = getIntent().getStringExtra(KEY_CAPTION_GUID);
        mContentUrl = getIntent().getStringExtra(KEY_CAPTION_URL);
        webView.setWebViewClient(new CBWebViewClient());
        webView.setWebChromeClient(new WebChromeClient() {
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                OfficalCaptionInfoActivity.this.startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
            }

            public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                OfficalCaptionInfoActivity.this.startActivityForResult(
                        Intent.createChooser(i, "File Browser"),
                        FILECHOOSER_RESULTCODE);
            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                OfficalCaptionInfoActivity.this.startActivityForResult(Intent.createChooser(i, "File Browser"), FILECHOOSER_RESULTCODE);
            }

            // For Android 5.0+
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                mUploadCallbackAboveL = filePathCallback;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                OfficalCaptionInfoActivity.this.startActivityForResult(Intent.createChooser(i, "File Browser"), FILECHOOSER_RESULTCODE);
                return true;
            }
        });
        //webView.clearCache(true);
        WebSettings settings = webView.getSettings();
        // 允许javascript的功能
        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccess(true);//启用WebView访问文件数据
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        // 自适应屏幕
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);

        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        webView.addJavascriptInterface(new CodeBoyJsInterface(), "codeboy");
        //webView.loadUrl("http://activity.xingka.cc/SpecialTopic/ceshi.html?acc=fU39olWVtTny3e3gZIj7wQ-3d-3d&bid=NCnauHvvd0-2b2CiFfCDkmqcqgomsPGE5JcjkuL4OUkT2N2Zx2GHe7kkgpjh8hLSwK");
        webView.loadUrl(mContentUrl);
    }

    @OnClick(R.id.tv_back)
    void backClicked() {
        onBackPressed();
    }

    @OnClick(R.id.tv_right)
    void shareClicked() {
        ShareInfo shareInfo = new ShareInfo();
        shareInfo.setTitle(shareTitle);
        shareInfo.setContent(shareDesc);
        shareInfo.setImageUrl(shareImage);
        shareInfo.setTargetUrl(shareUrl);
        Logger.d(shareInfo.toString());

        ShareOfficalCatpionActivity.launch(this, shareInfo, mGUid);
    }

    @Override
    public void onBackPressed() {
        if (webView.getTitle().equalsIgnoreCase("index")) {
            finish();
        } else {
            if (webView.canGoBack()) {
                webView.goBack();
            } else {
                super.onBackPressed();
            }
        }
    }


    /*
     * tips
     * 实现Parcelable接口，主要是避免因为混淆而导致方法名变更
     * */
    @SuppressLint({"JavascriptInterface", "ParcelCreator"})
    public class CodeBoyJsInterface implements Parcelable {

        public CodeBoyJsInterface() {
        }

        //解决throws Uncaught Error: Error calling method on NPObject on Android
        @SuppressLint("LongLogTag")
        @JavascriptInterface
        public void callme(final String str) {
            //这里是javascript里回调的，注意回调是通过非UI线程
            try {
                JSONObject json = new JSONObject(str);
                shareTitle = getJsonStr(json, "msg_title");
                shareDesc = getJsonStr(json, "msg_desc");
                shareImage = getJsonStr(json, "msg_link");
                shareUrl = getJsonStr(json, "msg_cdn_url");
                Log.d(TAG, "msg_title:" + shareTitle + " msg_desc:" + shareDesc
                        + " msg_link:" + shareImage + " msg_cdn_url:" + shareUrl);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private String getJsonStr(JSONObject json, String key) {
            try {
                String value = json.getString(key);
                //对数据进行转义
                return Html.fromHtml(value).toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public int describeContents() {
            //ignore
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            //ignore
        }
    }

    class CBWebViewClient extends WebViewClient {

        @SuppressLint("LongLogTag")
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.v(TAG, "onPageFinished 加载完成：" + url);
            rightView.setVisibility(View.VISIBLE);

            //页面加载完毕后，判断是否为微信的公众平台文章
//            if (url.startsWith("http://mp.weixin.qq.com") || url.startsWith("https://mp.weixin.qq.com")) {
            //读取分享的信息，将数据以json的格式返回,方便日后扩展
            view.loadUrl("javascript:window.codeboy.callme(JSON.stringify({" +
                    "\"msg_title\":shareTitle.toString()," +
                    "\"msg_desc\":descContent.toString()," +
                    "\"msg_link\":imgUrl.toString()," +
                    "\"msg_cdn_url\":lineLink.toString()" +
                    "}))");
            titleView.setText(view.getTitle());
//            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            cacheFirstLoadImageUrl = null;
            rightView.setVisibility(View.GONE);
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
            //读取加载中的资源里的图片
            if (cacheFirstLoadImageUrl == null && !TextUtils.isEmpty(url)) {
                if (url.contains(".png") || url.contains(".jpg")) {
                    cacheFirstLoadImageUrl = url;
                }
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage && null == mUploadCallbackAboveL) return;
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (mUploadCallbackAboveL != null) {
                onActivityResultAboveL(requestCode, resultCode, data);
            } else if (mUploadMessage != null) {
                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent data) {
        if (requestCode != FILECHOOSER_RESULTCODE
                || mUploadCallbackAboveL == null) {
            return;
        }
        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
            if (data == null) {
            } else {
                String dataString = data.getDataString();
                ClipData clipData = data.getClipData();
                if (clipData != null) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
                    }
                }
                if (dataString != null)
                    results = new Uri[]{Uri.parse(dataString)};
            }
        }
        mUploadCallbackAboveL.onReceiveValue(results);
        mUploadCallbackAboveL = null;
        return;
    }


}

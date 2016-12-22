package com.ruihai.xingka.ui.mine;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Switch;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.ruihai.xingka.Constant;
import com.ruihai.xingka.Global;
import com.ruihai.xingka.R;
import com.ruihai.xingka.api.ApiModule;
import com.ruihai.xingka.api.model.XKRepo;
import com.ruihai.xingka.ui.BaseActivity;
import com.ruihai.xingka.utils.AppUtility;
import com.ruihai.xingka.utils.QiniuHelper;
import com.ruihai.xingka.utils.Security;
import com.umeng.onlineconfig.OnlineConfigAgent;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMVideo;
import com.umeng.socialize.media.UMusic;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.Log;
import com.umeng.socialize.utils.ShareBoardlistener;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * 分享名片
 * Created by lqfang on 15/10/13.
 */
public class ShareCardActivity extends BaseActivity implements View.OnClickListener{

    public static void launch(Activity from, String account, String avatar, String nick) {
        Intent intent = new Intent(from, ShareCardActivity.class);
        intent.putExtra("account", account);
        intent.putExtra("avatar", avatar);
        intent.putExtra("nick", nick);
        from.startActivity(intent);
    }

    private Dialog dialog;//对话框

    private String mAccount;
    private String mAavatar;
    private String mNick;

    private String shareTitle;
    private String shareContent;
    private String targetUrl;
    private String shareImageUrl;
    private String shareImageWeiboUrl;
    private UMImage image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.custom_dialog);
        setContentView(R.layout.activity_share_caption);
        ButterKnife.bind(this);

        shareTitle = "我的行咖名片";
        mAccount = getIntent().getStringExtra("account");
        mAavatar = getIntent().getStringExtra("avatar");
        mNick = getIntent().getStringExtra("nick");
        if (mAccount != null) {
            if (!TextUtils.isEmpty(mAccount)) {
                shareContent = OnlineConfigAgent.getInstance().getConfigParams(mContext, "shareCardContent");
                shareContent = String.format(shareContent, mNick, mAccount);
                //shareContent = "我是行咖" + mNick + "，行咖号：" + mAccount + "。行咖在路上，我们在世界中央!";
            } else {
                shareContent = "名片|行咖在路上";
            }
            //对行咖号加密
            String account = Security.aesEncrypt(mAccount);

            try {
                account = URLEncoder.encode(account, "utf-8").replace("%", "-");
                // 分享名片URL
                targetUrl = String.format(Global.SHARE_CARD_URL, account);
                Logger.e(targetUrl);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            //对图片处理
            shareImageUrl = QiniuHelper.getThumbnail96Url(mAavatar);
            shareImageWeiboUrl = QiniuHelper.getOriginalWithKey(mAavatar); //微博分享时调用原图
            image = new UMImage(mContext, shareImageUrl);

//            //获取对话框布局中的控件，并设置事件监听
//            findViewById(R.id.share_wechat).setOnClickListener(this);
//            findViewById(R.id.share_friends).setOnClickListener(this);
//            findViewById(R.id.share_qq).setOnClickListener(this);
//            findViewById(R.id.share_qzone).setOnClickListener(this);
//            findViewById(R.id.share_weibo).setOnClickListener(this);
//            findViewById(R.id.login_cancel).setOnClickListener(this);
//            findViewById(R.id.dialog_layout).setOnClickListener(this);

        }

        showDialog();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.share_wechat:
                new ShareAction(this).setPlatform(SHARE_MEDIA.WEIXIN).setCallback(umShareListener)
                        .withText(shareContent)
                        .withTitle(shareTitle)
                        .withMedia(image)
                        .withTargetUrl(targetUrl)
                        .share();
                dismiss();
                finish();
                break;
            case R.id.share_friends:
                new ShareAction(this).setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE).setCallback(umShareListener)
                        .withText(shareContent)
                        .withTitle(shareContent)
                        .withMedia(image)
                        .withTargetUrl(targetUrl)
                        .share();
                dismiss();
                finish();
                break;
            case R.id.share_qq:
                new ShareAction(this).setPlatform(SHARE_MEDIA.QQ).setCallback(umShareListener)
                        .withText(shareContent)
                        .withTitle(shareTitle)
                        .withMedia(image)
                        .withTargetUrl(targetUrl)
                        .share();
                dismiss();
                finish();
                break;
            case R.id.share_qzone:
                new ShareAction(this).setPlatform(SHARE_MEDIA.QZONE).setCallback(umShareListener)
                        .withText(shareContent)
                        .withTitle(shareTitle)
                        .withMedia(image)
                        .withTargetUrl(targetUrl)
                        .share();
                dismiss();
                finish();
                break;
            case R.id.share_weibo:
                new ShareAction(this).setPlatform(SHARE_MEDIA.SINA).setCallback(umShareListener)
                        .withText(shareContent)
                        .withTitle(shareTitle)
                        .withMedia(new UMImage(mContext, shareImageWeiboUrl))
                        .withTargetUrl(targetUrl)
                        .share();
                dismiss();
                finish();
                break;
            //点击取消按钮，对话框消失
            case R.id.login_cancel:
                dismiss();
//                finish();
                break;
            //设置点击对话框布局外，对话框消失
            case R.id.dialog_layout:
                dismiss();
//                finish();
                break;
            default:
                break;
        }
    }

    /**
     * 显示对话框
     */
    public void showDialog() {
        if (dialog == null) {
            dialog = new Dialog(this, R.style.custom_dialog);
            dialog.setCanceledOnTouchOutside(true);
            //获取对话框的窗口，并设置窗口参数
            Window win = dialog.getWindow();
            WindowManager.LayoutParams params = new WindowManager.LayoutParams();
            // 不能写成这样,否则Dialog显示不出来
            // LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
            //对话框窗口的宽和高
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            //对话框窗口显示的位置
            params.x = 120;
            params.y = 100;
            win.setAttributes(params);
            //设置对话框布局
            dialog.setContentView(R.layout.custom_dialog);
            //获取对话框布局中的控件，并设置事件监听
            dialog.findViewById(R.id.share_wechat).setOnClickListener(this);
            dialog.findViewById(R.id.share_friends).setOnClickListener(this);
            dialog.findViewById(R.id.share_qq).setOnClickListener(this);
            dialog.findViewById(R.id.share_qzone).setOnClickListener(this);
            dialog.findViewById(R.id.share_weibo).setOnClickListener(this);
            dialog.findViewById(R.id.login_cancel).setOnClickListener(this);
            dialog.findViewById(R.id.dialog_layout).setOnClickListener(this);
        }
        dialog.show();
    }
    /**
     * 隐藏对话框
     */
    public void dismiss() {
        //隐藏对话框之前先判断对话框是否存在，以及是否正在显示
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    private UMShareListener umShareListener= new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {

            if(platform.name().equals("SINA")){
                recordShare("微博");
            }else if(platform.name().equals("WEIXIN")){
                recordShare("微信");
            }else if(platform.name().equals("WEIXIN_CIRCLE")){
                recordShare("朋友圈");
            }else if(platform.name().equals("QQ")){
                recordShare("QQ");
            }else if(platform.name().equals("QZONE")){
                recordShare("QQ空间");
            }
            AppUtility.showToast(" 分享成功");
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            AppUtility.showToast(" 分享失败");
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            AppUtility.showToast("取消分享");
        }
    };

    /**
     * @param requestCode
     * @param resultCode
     * @param data
     * @功能描述 : 如果有使用任一平台的SSO授权或者集成了facebook平台, 则必须在对应的activity中实现onActivityResult方法.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        dismiss();
        finish();
        return true;
    }

    public void recordShare(String shareType) {
        String sP_Guid = Security.aesEncrypt("22222222-2222-2222-2222-222222222222");
        String sShareAccount = Security.aesEncrypt(mAccount);
        String sOS = Security.aesEncrypt("1");
        String sToPlace = Security.aesEncrypt(shareType);
        ApiModule.apiService_1().getPhotoTopicShareRecord(sP_Guid, sShareAccount, sOS, sToPlace).enqueue(new Callback<XKRepo>() {

            @Override
            public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {

            }

            @Override
            public void onFailure(Call<XKRepo> call, Throwable t) {

            }
        });
    }

}

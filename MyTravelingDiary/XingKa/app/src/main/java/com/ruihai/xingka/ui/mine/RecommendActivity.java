package com.ruihai.xingka.ui.mine;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.ruihai.xingka.Global;
import com.ruihai.xingka.R;
import com.ruihai.xingka.api.ApiModule;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.api.model.XKRepo;
import com.ruihai.xingka.ui.BaseActivity;
import com.ruihai.xingka.utils.AppUtility;
import com.ruihai.xingka.utils.QiniuHelper;
import com.ruihai.xingka.utils.Security;
import com.umeng.onlineconfig.OnlineConfigAgent;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 行咖推荐分享
 * Created by lqfang on 15/8/18.
 */
public class RecommendActivity extends BaseActivity implements View.OnClickListener {

    public static final String DEFAULT_BACKGROUND_KEY = "00000000-0000-0000-0000-000000000000";

    //    private String shareTitle = "行咖，一个有温度的车友社交平台";
//    private String shareContent = "我发现一款有温度的车友社交APP，一起来嗨吧。";
    private String shareTitle = "快来和我一起在路上，发现更多美好";
    private String shareContent = "";
    //    private String targetUrl = Global.SHARE_PRODUCT_URL;
    private String targetUrl;
    private UMImage image;

    private String mAccount;
    private String mNick;
    private String mAvatar;

    private Dialog dialog;//对话框

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.custom_dialog);
        setContentView(R.layout.activity_share_caption);
        ButterKnife.bind(this);
        mAccount = String.valueOf(AccountInfo.getInstance().loadAccount().getAccount());
        mNick = AccountInfo.getInstance().loadAccount().getNickname();
        mAvatar = AccountInfo.getInstance().loadAccount().getAvatar();
        if (mAccount != null) {
            if (!TextUtils.isEmpty(mAccount)) {
                shareTitle = OnlineConfigAgent.getInstance().getConfigParams(mContext, "inviteTitle1");
                shareContent = OnlineConfigAgent.getInstance().getConfigParams(mContext, "inviteContent1");
                shareContent = String.format(shareContent, mNick, mAccount);
            } else {
                shareContent = "我是行咖" + mNick + "，行咖号：" + mAccount + "。行咖在路上，我们在世界中央!";
            }

            //对url的处理
            String account = Security.aesEncrypt(mAccount);
            try {
                account = URLEncoder.encode(account.trim(), "utf-8").replace("%", "-");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            targetUrl = String.format(Global.SHARE_PRODUCT_URL, account);

            //对图片处理
            if (mAvatar.equals(DEFAULT_BACKGROUND_KEY)) { //没有设置头像
                image = new UMImage(this, R.mipmap.icon_96);
            } else {
                String shareImageUrl = QiniuHelper.getThumbnail96Url(mAvatar);
                image = new UMImage(mContext, shareImageUrl);
            }
        }

        showDialog();
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
                        .withMedia(image)
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

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            if (platform.name().equals("SINA")) {
                recordShare("微博");
            } else if (platform.name().equals("WEIXIN")) {
                recordShare("微信");
            } else if (platform.name().equals("WEIXIN_CIRCLE")) {
                recordShare("朋友圈");
            } else if (platform.name().equals("QQ")) {
                recordShare("QQ");
            } else if (platform.name().equals("QZONE")) {
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        dismiss();
        finish();
        return true;
    }

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

    public void recordShare(String shareType) {
        String sP_Guid = Security.aesEncrypt("00000000-0000-0000-0000-000000000000");
        String sAccount = Security.aesEncrypt(mAccount);
        String sOS = Security.aesEncrypt("1");
        String sToPlace = Security.aesEncrypt(shareType);
        ApiModule.apiService_1().getPhotoTopicShareRecord(sP_Guid, sAccount, sOS, sToPlace).enqueue(new Callback<XKRepo>() {
            @Override
            public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {

            }

            @Override
            public void onFailure(Call<XKRepo> call, Throwable t) {

            }

        });
    }
}

package com.ruihai.xingka.ui.mine;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.flyco.dialog.widget.ActionSheetDialog;
import com.orhanobut.logger.Logger;
import com.ruihai.xingka.Global;
import com.ruihai.xingka.R;
import com.ruihai.xingka.api.ApiModule;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.api.model.OfficialPhotoTopicTypeRepo;
import com.ruihai.xingka.api.model.ReportType;
import com.ruihai.xingka.api.model.XKRepo;
import com.ruihai.xingka.ui.BaseActivity;
import com.ruihai.xingka.utils.AppUtility;
import com.ruihai.xingka.utils.QiniuHelper;
import com.ruihai.xingka.utils.Security;
import com.ruihai.xingka.widget.ProgressHUD;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaseShareActivity extends BaseActivity implements View.OnClickListener {

    public final static String KEY_CAPTION_ITEM = "CAPTION_ITEM";


    public static void launch(Activity from, OfficialPhotoTopicTypeRepo.OfficialPhotoTopicType item, String account) {
        Intent intent = new Intent(from, BaseShareActivity.class);
        intent.putExtra(KEY_CAPTION_ITEM, item);
        intent.putExtra("account", account);
        from.startActivity(intent);
    }


    private OfficialPhotoTopicTypeRepo.OfficialPhotoTopicType mPhotoTopic;
    private String shareTitle;
    private String shareContent;
    private String targetUrl;
    private String shareImageUrl;
    private String shareImageWeiboUrl;
    private String shareCircleTitle;
    private UMImage image;
    private String mUserAccount;

    private Dialog dialog;//对话框
    private ActionSheetDialog dialog1;

    private ClipboardManager clipboard;//粘贴板

    private String mMyAccount; // 我的行咖号

    private List<ReportType> reportInfoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_caption);
        ButterKnife.bind(this);

        mMyAccount = String.valueOf(AccountInfo.getInstance().loadAccount().getAccount());
        mUserAccount = getIntent().getStringExtra("account");
        mPhotoTopic = (OfficialPhotoTopicTypeRepo.OfficialPhotoTopicType) getIntent().getSerializableExtra(KEY_CAPTION_ITEM);

        if (mPhotoTopic != null) {
            shareContent = mPhotoTopic.getDescrip();
            shareTitle = mPhotoTopic.getTitle();
            shareCircleTitle = mPhotoTopic.getDescrip();

            String userAccount = Security.aesEncrypt(mUserAccount).trim();
            String type = Security.aesEncrypt(String.valueOf(mPhotoTopic.getType())).trim();
            // 分享图说图片URL
            try {
                targetUrl = String.format(Global.OFFICAL_GRAOUP_SHARE_URL, URLEncoder.encode(userAccount, "utf-8").replace("%", "-"), URLEncoder.encode(type, "utf-8").replace("%", "-"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Logger.d(targetUrl);
            // 分享首张图片
            String imageKey = mPhotoTopic.getIcon();
            shareImageUrl = QiniuHelper.getThumbnail96Url(imageKey);
            shareImageWeiboUrl = QiniuHelper.getOriginalWithKey(imageKey); //微博分享时调用原图
            image = new UMImage(mContext, shareImageUrl);
            Logger.d(shareImageUrl);
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
//            if (mType == 2) {
//                dialog.findViewById(R.id.ll_action).setVisibility(View.VISIBLE);
//            }
            //获取对话框布局中的控件，并设置事件监听
            dialog.findViewById(R.id.share_wechat).setOnClickListener(this);
            dialog.findViewById(R.id.share_friends).setOnClickListener(this);
            dialog.findViewById(R.id.share_qq).setOnClickListener(this);
            dialog.findViewById(R.id.share_qzone).setOnClickListener(this);
            dialog.findViewById(R.id.share_weibo).setOnClickListener(this);
            dialog.findViewById(R.id.action_link).setOnClickListener(this);
            dialog.findViewById(R.id.action_report).setOnClickListener(this);
            dialog.findViewById(R.id.login_cancel).setOnClickListener(this);
            dialog.findViewById(R.id.dialog_layout).setOnClickListener(this);
        }
        dialog.show();
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
                finish();
                dismiss();
                break;
            case R.id.share_friends:
                new ShareAction(this).setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE).setCallback(umShareListener)
                        .withText(shareContent)
                        .withTitle(shareContent)
                        .withMedia(image)
                        .withTargetUrl(targetUrl)
                        .share();
                finish();
                dismiss();
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
                finish();
                dismiss();
                break;
            case R.id.share_weibo:
                new ShareAction(this).setPlatform(SHARE_MEDIA.SINA).setCallback(umShareListener)
                        .withText(shareContent)
                        .withTitle(shareTitle)
                        .withMedia(new UMImage(mContext, shareImageWeiboUrl))
                        .withTargetUrl(targetUrl)
                        .share();
                finish();
                dismiss();
                break;
            case R.id.action_link:
                // copyFromComment(targetUrl);
//                AppUtility.showToast("复制成功");
                ProgressHUD.showSuccessMessage(mContext, "复制成功", new ProgressHUD.SimpleHUDCallback() {
                    @Override
                    public void onSimpleHUDDismissed() {
                        dismiss();
                        finish();
                    }
                });

//                dismiss();
                break;
            case R.id.action_report:
                // report();
                // finish();
                dismiss();
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
     * 隐藏对话框
     */
    public void dismiss() {
        //隐藏对话框之前先判断对话框是否存在，以及是否正在显示
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }


//    /**
//     * 微信平台
//     */
//    @OnClick(R.id.share_wechat)
//    void shareWechat() {
//        new ShareAction(this).setPlatform(SHARE_MEDIA.WEIXIN).setCallback(umShareListener)
//                .withText(shareContent)
//                .withTitle(shareTitle)
//                .withMedia(image)
//                .withTargetUrl(targetUrl)
//                .share();
//    }
//
//    /**
//     * 微信朋友圈
//     */
//    @OnClick(R.id.share_friends)
//    void shareFriends() {
//        new ShareAction(this).setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE).setCallback(umShareListener)
//                .withText(shareContent)
//                .withTitle(shareCircleTitle)
//                .withMedia(image)
//                .withTargetUrl(targetUrl)
//                .share();
//    }
//
//    @OnClick(R.id.share_qq)
//    void shareQQ() {
//        new ShareAction(this).setPlatform(SHARE_MEDIA.QQ).setCallback(umShareListener)
//                .withText(shareContent)
//                .withTitle(shareTitle)
//                .withMedia(image)
//                .withTargetUrl(targetUrl)
//                .share();
//    }
//
//    @OnClick(R.id.share_qzone)
//    void shareQzone() {
//        new ShareAction(this).setPlatform(SHARE_MEDIA.QZONE).setCallback(umShareListener)
//                .withText(shareContent)
//                .withTitle(shareTitle)
//                .withMedia(image)
//                .withTargetUrl(targetUrl)
//                .share();
//    }
//
//    @OnClick(R.id.share_weibo)
//    void shareWeibo() {
//        new ShareAction(this).setPlatform(SHARE_MEDIA.SINA).setCallback(umShareListener)
//                .withText(shareContent)
//                .withTitle(shareTitle)
//                .withMedia(new UMImage(mContext, shareImageWeiboUrl))
//                .withTargetUrl(targetUrl)
//                .share();
//    }

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

    public void recordShare(String type) {

        String sP_Guid = Security.aesEncrypt(mPhotoTopic.getSharecode());
        String sShareAccount = Security.aesEncrypt(mMyAccount);
        String sOS = Security.aesEncrypt("1");
        String sToPlace = Security.aesEncrypt(type);
        ApiModule.apiService_1().getPhotoTopicShareRecord(sP_Guid, sShareAccount, sOS, sToPlace).enqueue(new Callback<XKRepo>() {

            @Override
            public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                XKRepo xkRepo = response.body();
                if (xkRepo.isSuccess()) {
//                    AppUtility.showToast("成功");
                } else {
                }
            }

            @Override
            public void onFailure(Call<XKRepo> call, Throwable t) {
//                ProgressHUD.showErrorMessage(mContext, getString(R.string.common_network_error));
//                Log.e("图说分享失败","");
//                AppUtility.showToast("图说分享失败"+error.getMessage());
            }

        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
//        dismiss();
        return true;
    }

    /**
     * @param requestCode
     * @param resultCode
     * @param data
     * @功能描述 : 如果有使用任一平台的SSO授权或者集成了facebook平台, 则必须在对应的activity中实现onActivityResult方法.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**使用SSO授权必须添加如下代码 */
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }


}

package com.ruihai.xingka.ui.caption;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.orhanobut.logger.Logger;
import com.ruihai.xingka.R;
import com.ruihai.xingka.api.model.BannerInfoRepo;
import com.ruihai.xingka.ui.BaseActivity;
import com.ruihai.xingka.utils.QiniuHelper;

import butterknife.ButterKnife;

/**
 * 分享活动
 * Created by nextdoor zhang on 15/12/16.
 */
public class ShareBannerActivity extends BaseActivity {

    public final static String KEY_BANNER_ITEM = "BANNER_ITEM";
    public final static String KEY_URL = "URL";

    public static void launch(Activity from, BannerInfoRepo.BannerInfo item, String currentUrl) {
        Intent intent = new Intent(from, ShareBannerActivity.class);
        intent.putExtra(KEY_BANNER_ITEM, item);
        intent.putExtra(KEY_URL, currentUrl);
        from.startActivity(intent);
    }

//    private final UMSocialService mController = UMServiceFactory
//            .getUMSocialService(Constant.UM_SHARE_DESCRIPTOR);

    private BannerInfoRepo.BannerInfo mBannerInfo;
    private String shareTitle;
    private String shareContent;
    private String targetUrl;
    private String shareImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activtiy_share_banner);
        ButterKnife.bind(this);
        mBannerInfo = (BannerInfoRepo.BannerInfo) getIntent().getSerializableExtra(KEY_BANNER_ITEM);
        shareTitle = mBannerInfo.getTitle();
        if (mBannerInfo.isShareSelf()) {
            shareContent = getIntent().getStringExtra(KEY_URL);
        } else {
            shareContent = mBannerInfo.getDescription();
        }

        // 分享图片URL
        shareImageUrl = QiniuHelper.getOriginalWithKey(mBannerInfo.getThumbnailImg());
        // 分享图说图片URL
        targetUrl = mBannerInfo.getContent2();
        Logger.d(targetUrl);
        Logger.d(shareImageUrl);

        // 配置需要分享的相关平台
//        configPlatforms();
//        // 设置分享的内容
//        setShareContent();

    }

//    private void configPlatforms() {
//        // 添加QQ,QZone平台
//        addQQQZonePlatform();
//        // 添加微信、微信朋友圈平台
//        addWXPlatform();
//    }
//
//    /**
//     * @功能描述 : 添加微信平台分享
//     */
//    private void addWXPlatform() {
//        // 添加微信平台
//        UMWXHandler wxHandler = new UMWXHandler(this, Constant.WEIXIN_APPID, Constant.WEIXIN_APPSECRET);
//        wxHandler.addToSocialSDK();
//
//        // 支持微信朋友圈
//        UMWXHandler wxCircleHandler = new UMWXHandler(this, Constant.WEIXIN_APPID, Constant.WEIXIN_APPSECRET);
//        wxCircleHandler.setToCircle(true);
//        wxCircleHandler.addToSocialSDK();
//    }
//
//    /**
//     * @功能描述 : 添加QQ平台支持 QQ分享的内容， 包含四种类型， 即单纯的文字、图片、音乐、视频. </br>
//     * 参数说明 : title, summary, image url中必须至少设置一个, targetUrl必须设置,网页地址必须以"http://"开头. </br>
//     * title :要分享标题 </br>
//     * summary : 要分享的文字概述 </br>
//     * image url : 图片地址 </br>
//     * [以上三个参数至少填写一个] </br>
//     * targetUrl : 用户点击该分享时跳转到的目标地址 [必填] ( 若不填写则默认设置为友盟主页 )
//     */
//    private void addQQQZonePlatform() {
//        // 添加QQ支持, 并且设置QQ分享内容的target url
//        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(this, Constant.QQSSO_APPID, Constant.QQSSO_APPKEY);
//        qqSsoHandler.setTargetUrl(targetUrl);
//        qqSsoHandler.addToSocialSDK();
//
//        // 添加QZone平台
//        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(this, Constant.QQSSO_APPID, Constant.QQSSO_APPKEY);
//        qZoneSsoHandler.addToSocialSDK();
//    }
//
//    /**
//     * @功能描述 : 根据不同的平台设置不同的分享内容
//     */
//    private void setShareContent() {
//        UMImage urlImage = new UMImage(this, shareImageUrl);
//
//        // 设置微信分享内容
//        WeiXinShareContent weixinContent = new WeiXinShareContent();
//        weixinContent
//                .setShareContent(shareContent);
//        weixinContent.setTitle(shareTitle);
//        weixinContent.setTargetUrl(targetUrl);
//        weixinContent.setShareMedia(urlImage);
//        mController.setShareMedia(weixinContent);
//
//        // 设置朋友圈分享内容
//        CircleShareContent circleMedia = new CircleShareContent();
//        circleMedia
//                .setShareContent(shareContent);
//        circleMedia.setTitle(shareTitle);
//        circleMedia.setShareMedia(urlImage);
//        circleMedia.setTargetUrl(targetUrl);
//        mController.setShareMedia(circleMedia);
//
//        // 设置QQ空间分享内容
//        QZoneShareContent qzone = new QZoneShareContent();
//        qzone.setShareContent(shareContent);
//        qzone.setTargetUrl(targetUrl);
//        qzone.setTitle(shareTitle);
//        qzone.setShareMedia(urlImage);
//        mController.setShareMedia(qzone);
//
//        // 设置QQ分享内容
//        QQShareContent qqShareContent = new QQShareContent();
//        qqShareContent.setShareContent(shareContent);
//        qqShareContent.setTitle(shareTitle);
//        qqShareContent.setShareMedia(urlImage);
//        qqShareContent.setTargetUrl(targetUrl);
//        mController.setShareMedia(qqShareContent);
//
//        // 新浪微博分享内容
////        SinaShareContent sinaContent = new SinaShareContent();
////        sinaContent
////                .setShareContent(shareContent + targetUrl);
////        sinaContent.setShareImage(urlImage);
////        mController.setShareMedia(sinaContent);
//    }
//
//    /**
//     * 微信平台
//     */
//    @OnClick(R.id.iv_wechat)
//    void shareWechat() {
//        performShare(SHARE_MEDIA.WEIXIN);
//    }
//
//    /**
//     * 微信朋友圈
//     */
//    @OnClick(R.id.iv_friends)
//    void shareFriends() {
//        performShare(SHARE_MEDIA.WEIXIN_CIRCLE);
//    }
//
//    @OnClick(R.id.iv_qq)
//    void shareQQ() {
//        performShare(SHARE_MEDIA.QQ);
//    }
//
//    @OnClick(R.id.iv_qzone)
//    void shareQzone() {
//        performShare(SHARE_MEDIA.QZONE);
//    }
//
//    @OnClick(R.id.iv_weibo)
//    void shareWeibo() {
////        performShare(SHARE_MEDIA.SINA);
////        UMSocialSinaHandler openSSOWithRedirectURL:"http://sns.whalecloud.com/sina2/callback";
//        // 添加新浪SSO授权
//        mController.getConfig().setSsoHandler(new SinaSsoHandler());
//        mController.setShareContent(shareContent + targetUrl);
//        mController.setShareMedia(new UMImage(this, shareImageUrl));
//        mController.directShare(this, SHARE_MEDIA.SINA, new SocializeListeners.SnsPostListener() {
//            @Override
//            public void onStart() {
//                Logger.d("开始分享");
//            }
//
//            @Override
//            public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity socializeEntity) {
//                if (eCode == StatusCode.ST_CODE_SUCCESSED) {
//                    Logger.d("分享成功");
//                } else {
//                    Logger.d("分享失败");
//                }
//            }
//        });
//
//    }
//
//    private void performShare(SHARE_MEDIA platform) {
//        mController.postShare(this, platform, new SocializeListeners.SnsPostListener() {
//            @Override
//            public void onStart() {
//
//            }
//
//            @Override
//            public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity socializeEntity) {
//                String showText = platform.toString();
//                if (eCode == StatusCode.ST_CODE_SUCCESSED) {
//                    showText += "平台分享成功";
//                } else {
//                    showText += "平台分享失败";
//                }
////                AppUtility.showToast(showText);
//                Logger.d(showText);
//                finish();
//            }
//        });
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        finish();
//        return true;
//    }
//
//    /**
//     * @param requestCode
//     * @param resultCode
//     * @param data
//     * @功能描述 : 如果有使用任一平台的SSO授权或者集成了facebook平台, 则必须在对应的activity中实现onActivityResult方法.
//     */
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        UMSsoHandler ssoHandler = SocializeConfig.getSocializeConfig().getSsoHandler(requestCode);
//        if (ssoHandler != null) {
//            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
//        }
//    }
}

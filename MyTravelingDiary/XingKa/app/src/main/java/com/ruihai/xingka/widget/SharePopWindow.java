package com.ruihai.xingka.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.ruihai.xingka.Global;
import com.ruihai.xingka.R;
import com.ruihai.xingka.api.ApiModule;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.api.model.User;
import com.ruihai.xingka.api.model.XKRepo;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mac on 16/6/14.
 */
public class SharePopWindow extends PopupWindow implements View.OnClickListener {

    private Context context;
    private Activity activity;
    private LayoutInflater inflater;

    private User mCurrentUser;
    private String mAccount;
    private String mAavatar;
    private String mNick;

    private String shareTitle;
    private String shareContent;
    private String targetUrl;
    private String shareImageUrl;
    private String shareImageWeiboUrl;
    private UMImage image;


    public SharePopWindow(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        int mScreenHeight = AppUtility.getScreenHeight();
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
//        this.setHeight((int) (3.0F * (mScreenHeight / 4.0F)));
//        this.setBackgroundDrawable(new ColorDrawable(0xb0000000));// 这样设置才能点击屏幕外dismiss窗口
        this.setOutsideTouchable(false);

        this.setAnimationStyle(R.style.timepopwindow_anim_style);
        inflater = LayoutInflater.from(context);
        View contentView = inflater.inflate(R.layout.custom_dialog, null);


        shareTitle = "我的行咖名片";
        mCurrentUser = AccountInfo.getInstance().loadAccount();
        mAccount = String.valueOf(mCurrentUser.getAccount());
        ;
        mAavatar = mCurrentUser.getAvatar();
        mNick = mCurrentUser.getNickname();
        if (mAccount != null) {
            if (!TextUtils.isEmpty(mAccount)) {
                shareContent = OnlineConfigAgent.getInstance().getConfigParams(context, "shareCardContent");
                shareContent = String.format(shareContent, mNick, mAccount);
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
            image = new UMImage(context, shareImageUrl);

            //获取对话框布局中的控件，并设置事件监听
            ImageView shareWechat, shareFriends, shareQQ, shareQzone, shareWeibo;
            TextView cancle;
            RelativeLayout dialogCancle;

            shareWechat = (ImageView) contentView.findViewById(R.id.share_wechat);
            shareFriends = (ImageView) contentView.findViewById(R.id.share_friends);
            shareQQ = (ImageView) contentView.findViewById(R.id.share_qq);
            shareQzone = (ImageView) contentView.findViewById(R.id.share_qzone);
            shareWeibo = (ImageView) contentView.findViewById(R.id.share_weibo);
            cancle = (TextView) contentView.findViewById(R.id.login_cancel);
            dialogCancle = (RelativeLayout) contentView.findViewById(R.id.dialog_layout);

            shareWechat.setOnClickListener(this);
            shareFriends.setOnClickListener(this);
            shareQQ.setOnClickListener(this);
            shareQzone.setOnClickListener(this);
            shareWeibo.setOnClickListener(this);
            cancle.setOnClickListener(this);
            dialogCancle.setOnClickListener(this);

            setContentView(contentView);
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.share_wechat:
                new ShareAction(activity).setPlatform(SHARE_MEDIA.WEIXIN).setCallback(umShareListener)
                        .withText(shareContent)
                        .withTitle(shareTitle)
                        .withMedia(image)
                        .withTargetUrl(targetUrl)
                        .share();
                break;
            case R.id.share_friends:
                new ShareAction(activity).setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE).setCallback(umShareListener)
                        .withText(shareContent)
                        .withTitle(shareContent)
                        .withMedia(image)
                        .withTargetUrl(targetUrl)
                        .share();
                break;
            case R.id.share_qq:
                new ShareAction(activity).setPlatform(SHARE_MEDIA.QQ).setCallback(umShareListener)
                        .withText(shareContent)
                        .withTitle(shareTitle)
                        .withMedia(image)
                        .withTargetUrl(targetUrl)
                        .share();
                dismiss();
                break;
            case R.id.share_qzone:
                new ShareAction(activity).setPlatform(SHARE_MEDIA.QZONE).setCallback(umShareListener)
                        .withText(shareContent)
                        .withTitle(shareTitle)
                        .withMedia(image)
                        .withTargetUrl(targetUrl)
                        .share();
                break;
            case R.id.share_weibo:
                new ShareAction(activity).setPlatform(SHARE_MEDIA.SINA).setCallback(umShareListener)
                        .withText(shareContent)
                        .withTitle(shareTitle)
                        .withMedia(new UMImage(context, shareImageWeiboUrl))
                        .withTargetUrl(targetUrl)
                        .share();
                break;
            //点击取消按钮，对话框消失
            case R.id.login_cancel:
                dismiss();
                break;
            //设置点击对话框布局外，对话框消失
            case R.id.dialog_layout:
                dismiss();
                break;
            default:
                break;
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

//    /**
//     * @param requestCode
//     * @param resultCode
//     * @param data
//     * @功能描述 : 如果有使用任一平台的SSO授权或者集成了facebook平台, 则必须在对应的activity中实现onActivityResult方法.
//     */
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        UMShareAPI.get(activity).onActivityResult(requestCode, resultCode, data);
//    }




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





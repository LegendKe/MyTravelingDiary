package com.ruihai.xingka.ui.caption;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.orhanobut.logger.Logger;
import com.ruihai.xingka.Global;
import com.ruihai.xingka.R;
import com.ruihai.xingka.api.ApiModule;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.api.model.ImageItem;
import com.ruihai.xingka.api.model.ImagesMessage;
import com.ruihai.xingka.api.model.PhotoTopic;
import com.ruihai.xingka.api.model.ReportInfo;
import com.ruihai.xingka.api.model.ReportType;
import com.ruihai.xingka.api.model.UserPhotoTopic;
import com.ruihai.xingka.api.model.XKRepo;
import com.ruihai.xingka.ui.BaseActivity;
import com.ruihai.xingka.ui.login.LoginActivity;
import com.ruihai.xingka.utils.AppUtility;
import com.ruihai.xingka.utils.QiniuHelper;
import com.ruihai.xingka.utils.Security;
import com.ruihai.xingka.widget.ProgressHUD;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * 分享图说
 * Created by lqfang on 15/10/20.
 */
public class ShareCaptionActivity extends BaseActivity implements View.OnClickListener {

    public final static String KEY_CAPTION_ITEM = "CAPTION_ITEM";
    public final static String KEY_PHOTO_TOPIC_ITEM = "PHOTO_TOPIC_ITEM";

    public static void launch(Activity from, PhotoTopic item, int type) {
        Intent intent = new Intent(from, ShareCaptionActivity.class);
        intent.putExtra(KEY_CAPTION_ITEM, item);
        intent.putExtra("type", type);
        from.startActivity(intent);
    }

    public static void launch(Context from, UserPhotoTopic item, int type) {
        Intent intent = new Intent(from, ShareCaptionActivity.class);
        intent.putExtra(KEY_PHOTO_TOPIC_ITEM, item);
        intent.putExtra("type", type);
        from.startActivity(intent);
    }

    private PhotoTopic mPhotoTopic;
    private UserPhotoTopic mUserPhotoTopic;
    private String shareTitle;
    private String shareContent;
    private String targetUrl;
    private String shareImageUrl;
    private String shareImageWeiboUrl;
    private String shareCircleTitle;
    private UMImage image;
    private int mType;

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

        if (AccountInfo.getInstance().getReportType() == null) {
            initReportType(); //获取举报类型
        }else {
            reportInfoList = AccountInfo.getInstance().getReportType();
        }

        mType = getIntent().getIntExtra("type", 1);
        mMyAccount = String.valueOf(AccountInfo.getInstance().loadAccount().getAccount());

        shareTitle = "行咖丨每一处风景都有温度，镜头里的你会发光";
        mPhotoTopic = getIntent().getParcelableExtra(KEY_CAPTION_ITEM);
        mUserPhotoTopic = (UserPhotoTopic) getIntent().getSerializableExtra(KEY_PHOTO_TOPIC_ITEM);
        if (mUserPhotoTopic != null) {
            if (!TextUtils.isEmpty(mUserPhotoTopic.getContent())) {
                shareContent = mUserPhotoTopic.getContent();
                shareCircleTitle = "行咖丨" + mUserPhotoTopic.getContent();
            } else {
                shareContent = "每一张图片都是旅途的浓缩，每一个句子都是心情的传递，行咖，遇见更多的【自己】";
                shareCircleTitle = shareTitle;
            }

            // 分享图说URL
            String account = String.valueOf(mUserPhotoTopic.getAccount());
            String pGuid = mUserPhotoTopic.getpGuid();
//            String account = Security.aesEncrypt(String.valueOf(mPhotoTopic.getAccount()));
//            String pGuid = Security.aesEncrypt(mPhotoTopic.getpGuid());

//            try {
//                account = URLEncoder.encode(account, "utf-8").replace("%", "-");
//                pGuid = URLEncoder.encode(pGuid, "utf-8").replace("%", "-");
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
            // 分享图说图片URL
            targetUrl = String.format(Global.SHARE_CAPTION_URL, account, pGuid, String.valueOf(1));
            Logger.d(targetUrl);
            // 分享首张图片
            List<ImagesMessage> imageItems = mUserPhotoTopic.getImagesMessage();
            if (imageItems.size() > 0) {
                String imageKey = imageItems.get(0).getImgSrc();
                shareImageUrl = QiniuHelper.getThumbnail96Url(imageKey);
                shareImageWeiboUrl = QiniuHelper.getOriginalWithKey(imageKey); //微博分享时调用原图
                image = new UMImage(mContext, shareImageUrl);
                Logger.d(shareImageUrl);
            }
        }
        if (mPhotoTopic != null) {
            if (!TextUtils.isEmpty(mPhotoTopic.getContent())) {
                shareContent = mPhotoTopic.getContent();
                shareCircleTitle = "行咖丨" + mPhotoTopic.getContent();
            } else {
                shareContent = "每一张图片都是旅途的浓缩，每一个句子都是心情的传递，行咖，遇见更多的【自己】";
                shareCircleTitle = shareTitle;
            }

            // 分享图说URL
            String account = String.valueOf(mPhotoTopic.getAccount());
            String pGuid = mPhotoTopic.getpGuid();
//            String account = Security.aesEncrypt(String.valueOf(mPhotoTopic.getAccount()));
//            String pGuid = Security.aesEncrypt(mPhotoTopic.getpGuid());

//            try {
//                account = URLEncoder.encode(account, "utf-8").replace("%", "-");
//                pGuid = URLEncoder.encode(pGuid, "utf-8").replace("%", "-");
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
            // 分享图说图片URL
            targetUrl = String.format(Global.SHARE_CAPTION_URL, account, pGuid, String.valueOf(1));
            Logger.d(targetUrl);
            // 分享首张图片
            List<ImageItem> imageItems = mPhotoTopic.getImageList();
            if (imageItems.size() > 0) {
                String imageKey = imageItems.get(0).imgSrc;
                shareImageUrl = QiniuHelper.getThumbnail96Url(imageKey);
                shareImageWeiboUrl = QiniuHelper.getOriginalWithKey(imageKey); //微博分享时调用原图
                image = new UMImage(mContext, shareImageUrl);
                Logger.d(shareImageUrl);
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
            if (mType == 2) {
                dialog.findViewById(R.id.ll_action).setVisibility(View.VISIBLE);
            }
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
//                finish();
                dismiss();
                break;
            case R.id.share_friends:
                new ShareAction(this).setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE).setCallback(umShareListener)
                        .withText(shareContent)
                        .withTitle(shareContent)
                        .withMedia(image)
                        .withTargetUrl(targetUrl)
                        .share();
//                finish();
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
//                finish();
                break;
            case R.id.share_qzone:
                new ShareAction(this).setPlatform(SHARE_MEDIA.QZONE).setCallback(umShareListener)
                        .withText(shareContent)
                        .withTitle(shareTitle)
                        .withMedia(image)
                        .withTargetUrl(targetUrl)
                        .share();
//                finish();
                dismiss();
                break;
            case R.id.share_weibo:
                new ShareAction(this).setPlatform(SHARE_MEDIA.SINA).setCallback(umShareListener)
                        .withText(shareContent)
                        .withTitle(shareTitle)
                        .withMedia(new UMImage(mContext, shareImageWeiboUrl))
                        .withTargetUrl(targetUrl)
                        .share();
//                finish();
                dismiss();
                break;
            case R.id.action_link:
                copyFromComment(targetUrl);
                ProgressHUD.showSuccessMessage(mContext, "复制成功", new ProgressHUD.SimpleHUDCallback() {
                    @Override
                    public void onSimpleHUDDismissed() {
                        dismiss();
                    }
                });
                break;
            case R.id.action_report:
//                report();
//                initReportType();
                confirmReportDialog();
                dismiss();
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

    /**
     * 隐藏对话框
     */
    public void dismiss() {
        //隐藏对话框之前先判断对话框是否存在，以及是否正在显示
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
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

    public void recordShare(String type) {

        String sP_Guid = Security.aesEncrypt(mPhotoTopic.getpGuid());
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

    //复制内容到粘贴板
    private void copyFromComment(String content) {
        // Gets a handle to the clipboard service.
        if (null == clipboard) {
            //获取粘贴板服务
            clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        }
        // Creates a new text clip to put on the clipboard
        ClipData clip = ClipData.newPlainText("simple text",
                content);
        // Set the clipboard's primary clip.
        clipboard.setPrimaryClip(clip);
    }

    /**
     * @功能描述 : 获取举报类型
     */
    private void initReportType() {
        int mVersion = AppUtility.getAppVersionCode();
        String sVersion = Security.aesEncrypt(String.valueOf(mVersion));
        ApiModule.apiService().photoTopicReportTypeList(sVersion).enqueue(new Callback<ReportInfo>() {


            @Override
            public void onResponse(Call<ReportInfo> call, Response<ReportInfo> response) {
                ReportInfo reportInfo = response.body();
                if (reportInfo.isSuccess()) {
                    reportInfoList = reportInfo.getListMessage();
//                    confirmReportDialog();
                    //保存举报类型到本地
                    AccountInfo.getInstance().saveReportType(reportInfoList);
                } else {
                    ProgressHUD.showInfoMessage(mContext, reportInfo.getMsg());
                }
            }

            @Override
            public void onFailure(Call<ReportInfo> call, Throwable t) {
                ProgressHUD.showErrorMessage(mContext, getString(R.string.common_network_error));
            }
        });
    }

    /**
     * @功能描述 : 举报确认提示操作框
     */
    private void confirmReportDialog() {
        final List<ReportType> reportTypes = reportInfoList;
        final String[] stringItems = new String[reportTypes.size()];
        for (int i = 0; i < reportTypes.size(); i++) {
            stringItems[i] = reportTypes.get(i).getTitle();
        }


        dialog1 = new ActionSheetDialog(this, stringItems, null);
        //弹出框透明度设为1
        dialog1.titleBgColor(Color.parseColor("#ffffff"));
        dialog1.lvBgColor(Color.parseColor("#ffffff"));
        dialog1.title("请选择举报类型")
                .titleTextColor(Color.parseColor("#9c9ca0"))
                .titleTextSize_SP(15.5f)
                .titleHeight(36f);
        dialog1.itemTextColor(Color.parseColor("#363640"))
                .itemTextSize(15.5f)
                .itemHeight(36f);
        dialog1.cancelText(Color.parseColor("#363640"))
                .cancelTextSize(15.5f)
                .dividerColor(Color.parseColor("#d8d9e2"))
                .show();

        dialog1.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                submitReport(String.valueOf(i));
                dialog1.dismiss();
                finish();
            }
        });

        dialog1.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                dialog1.dismiss();
                finish();
            }
        });

    }

    /**
     * @功能描述 : 提交举报数据至服务器
     */
    private void submitReport(String type) {
        if (!AccountInfo.getInstance().isLogin()) {
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }

        ProgressHUD.showLoadingMessage(mContext, "正在提交举报", false);
        String account = Security.aesEncrypt(String.valueOf(currentUser.getAccount()));
        String tGuid = Security.aesEncrypt(mPhotoTopic.getpGuid());
        String toAccount = Security.aesEncrypt(String.valueOf(mPhotoTopic.getAccount()));
        String sType = Security.aesEncrypt(type);

        ApiModule.apiService_1().photoTopicReportListAdd2(account, tGuid, toAccount, sType).enqueue(new Callback<XKRepo>() {

            @Override
            public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                XKRepo xkRepo = response.body();
                ProgressHUD.dismiss();
                if (xkRepo.isSuccess()) {
                    ProgressHUD.showSuccessMessage(mContext, "举报成功");
//                    ProgressHUD.showSuccessMessage(mContext, "举报成功", new ProgressHUD.SimpleHUDCallback() {
//                        @Override
//                        public void onSimpleHUDDismissed() {
//                            dismiss();
//                        }
//                    });
                } else {
                    ProgressHUD.showInfoMessage(mContext, xkRepo.getMsg());
                }
            }

            @Override
            public void onFailure(Call<XKRepo> call, Throwable t) {
                ProgressHUD.showErrorMessage(mContext, getString(R.string.common_network_error));
            }

        });
    }

}

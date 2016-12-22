package com.ruihai.xingka.ui.trackway;

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
import android.widget.Toast;

import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.orhanobut.logger.Logger;
import com.ruihai.xingka.Global;
import com.ruihai.xingka.R;
import com.ruihai.xingka.api.ApiModule;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.api.model.ImageItem;
import com.ruihai.xingka.api.model.ReportInfo;
import com.ruihai.xingka.api.model.ReportType;
import com.ruihai.xingka.api.model.TrackwayInfo;
import com.ruihai.xingka.api.model.User;
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
import com.umeng.socialize.media.UMVideo;
import com.umeng.socialize.media.UMusic;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.ShareBoardlistener;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 旅拼分享
 * Created by mac on 16/5/16.
 */
public class ShareTrackwayActivity extends BaseActivity implements View.OnClickListener{

    public final static String KEY_TRACKWAY_ITEM = "TRACKWAY_ITEM";

    public static void launch(Activity from, TrackwayInfo item,int type) {
        Intent intent = new Intent(from, ShareTrackwayActivity.class);
        intent.putExtra(KEY_TRACKWAY_ITEM, item);
        intent.putExtra("type", type);
        from.startActivity(intent);
    }

    private TrackwayInfo mTrackwayinfo;
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

    private int mMyAccount; // 我的行咖号
    private int userAccount;

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

        mMyAccount = AccountInfo.getInstance().loadAccount().getAccount();
        mTrackwayinfo = getIntent().getParcelableExtra(KEY_TRACKWAY_ITEM);
        mType = getIntent().getIntExtra("type", 1);
        userAccount = mTrackwayinfo.getAccount();

        if (mTrackwayinfo != null) {
            shareTitle = mTrackwayinfo.getTitle();
            shareContent = mTrackwayinfo.getContent();
            String account = String.valueOf(mTrackwayinfo.getAccount());
            String tGuid = mTrackwayinfo.gettGuid();
            String sAccount = URLEncoder.encode(Security.aesEncrypt(account).trim());
            String sTGuid = URLEncoder.encode(Security.aesEncrypt(tGuid));

            // 分享旅拼地址URL
            targetUrl = String.format(Global.SHARE_TRACKWAY_URL, sAccount, sTGuid, String.valueOf(1));
            Logger.d(targetUrl);
            // 分享首张图片
            List<ImageItem> imageItems = mTrackwayinfo.getImageList();
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
            if(mType == 2){
                dialog.findViewById(R.id.ll_action).setVisibility(View.VISIBLE);
            }
            if(userAccount == (mMyAccount)){ //显示删除图标, 隐藏举报按钮
                dialog.findViewById(R.id.ll_delete).setVisibility(View.VISIBLE);
                dialog.findViewById(R.id.ll_report).setVisibility(View.GONE);
            }
            //获取对话框布局中的控件，并设置事件监听
            dialog.findViewById(R.id.share_wechat).setOnClickListener(this);
            dialog.findViewById(R.id.share_friends).setOnClickListener(this);
            dialog.findViewById(R.id.share_qq).setOnClickListener(this);
            dialog.findViewById(R.id.share_qzone).setOnClickListener(this);
            dialog.findViewById(R.id.share_weibo).setOnClickListener(this);
            dialog.findViewById(R.id.action_link).setOnClickListener(this);
            dialog.findViewById(R.id.action_report).setOnClickListener(this);
            dialog.findViewById(R.id.ll_delete).setOnClickListener(this);
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
            case R.id.action_link:
                copyFromComment(targetUrl);
                ProgressHUD.showSuccessMessage(mContext, "复制成功", new ProgressHUD.SimpleHUDCallback() {
                    @Override
                    public void onSimpleHUDDismissed() {
                        finish();
                    }
                });
                break;
            case R.id.action_report:
                confirmReportDialog();
                dismiss();
                break;
            case R.id.ll_delete:
                getDeleteTravelTogether();
                finish();
            //点击取消按钮，对话框消失
            case R.id.login_cancel:
//                finish();
                dismiss();
                break;
            //设置点击对话框布局外，对话框消失
            case R.id.dialog_layout:
//                finish();
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

    public void recordShare(String type) {

        String sT_Guid = Security.aesEncrypt(mTrackwayinfo.gettGuid());
        String sShareAccount = Security.aesEncrypt(String.valueOf(mTrackwayinfo.getAccount()));
        ApiModule.apiService_1().travelTogetherShareAdd(sT_Guid, sShareAccount).enqueue(new Callback<XKRepo>() {


            @Override
            public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                XKRepo xkRepo = response.body();
                if (xkRepo.isSuccess()) {
//                    Log.e("图说分享成功","");
//                    AppUtility.showToast("成功");
                } else {
                }
            }

            @Override
            public void onFailure(Call<XKRepo> call, Throwable t) {
//                ProgressHUD.showErrorMessage(mContext, getString(R.string.common_network_error));
            }

        });
    }

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
        ApiModule.apiService_1().photoTopicReportTypeList(sVersion).enqueue(new Callback<ReportInfo>() {

            @Override
            public void onResponse(Call<ReportInfo> call, Response<ReportInfo> response) {
                ReportInfo reportInfo = response.body();
                if (reportInfo.isSuccess()) {
                    reportInfoList = reportInfo.getListMessage();
                    //保存举报类型到本地
                    AccountInfo.getInstance().saveReportType(reportInfoList);
                } else {
                    ProgressHUD.showInfoMessage(mContext, reportInfo.getMsg());
                }
            }

            @Override
            public void onFailure(Call<ReportInfo> call, Throwable t) {
                ProgressHUD.showInfoMessage(mContext, getString(R.string.common_network_error));
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
            Log.e("举报类型集合", "" + stringItems[i]);
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
        String tGuid = Security.aesEncrypt(mTrackwayinfo.gettGuid());
        String toAccount = Security.aesEncrypt(String.valueOf(mTrackwayinfo.getAccount()));
        String sType = Security.aesEncrypt(type);

        ApiModule.apiService_1().travelTogetherReportListAdd(account, tGuid, toAccount, sType).enqueue(new Callback<XKRepo>() {

            @Override
            public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                XKRepo xkRepo = response.body();
                ProgressHUD.dismiss();
                if (xkRepo.isSuccess()) {
                    ProgressHUD.showSuccessMessage(ShareTrackwayActivity.this, "举报成功");
                } else {
                    ProgressHUD.showInfoMessage(ShareTrackwayActivity.this, xkRepo.getMsg());
                }
            }

            @Override
            public void onFailure(Call<XKRepo> call, Throwable t) {
                ProgressHUD.showInfoMessage(ShareTrackwayActivity.this, getString(R.string.common_network_error));
            }

        });
    }

    //删除单篇旅拼
    private void getDeleteTravelTogether() {
        ProgressHUD.showLoadingMessage(mContext, "正在删除...", false);
        String account = Security.aesEncrypt(String.valueOf(mMyAccount));
        String sTGuid = Security.aesEncrypt(mTrackwayinfo.gettGuid());
        String sAuthor = Security.aesEncrypt(String.valueOf(mTrackwayinfo.getAccount()));
        // Log.i("TAG", "---account---->" + account + "----sPGuid---->" + sPGuid + "---sAuthor----->" + sAuthor);
        ApiModule.apiService_1().deleteTravelTogether(account, sTGuid, sAuthor).enqueue(new Callback<XKRepo>() {
            @Override
            public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                ProgressHUD.dismiss();
                XKRepo xkRepo = response.body();
                String msg = xkRepo.getMsg();
                if (xkRepo.isSuccess()) {
                    ProgressHUD.showSuccessMessage(mContext, "删除成功");
                    finish();
                } else
                    ProgressHUD.showInfoMessage(mContext, msg);
            }

            @Override
            public void onFailure(Call<XKRepo> call, Throwable t) {
                ProgressHUD.showErrorMessage(mContext, getString(R.string.common_network_error));
            }
        });
    }
}

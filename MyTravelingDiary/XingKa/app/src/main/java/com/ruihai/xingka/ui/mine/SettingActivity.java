package com.ruihai.xingka.ui.mine;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.orhanobut.logger.Logger;
import com.ruihai.android.common.utils.DateUtils;
import com.ruihai.android.network.task.TaskException;
import com.ruihai.android.network.task.WorkTask;
import com.ruihai.iconicfontengine.widget.IconicFontTextView;
import com.ruihai.xingka.Global;
import com.ruihai.xingka.R;
import com.ruihai.xingka.XKApplication;
import com.ruihai.xingka.api.ApiModule;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.api.model.User;
import com.ruihai.xingka.api.model.XKRepo;
import com.ruihai.xingka.ui.BaseActivity;
import com.ruihai.xingka.ui.common.CommonWebActivity;
import com.ruihai.xingka.ui.login.LoginActivity;
import com.ruihai.xingka.ui.mine.adapter.SettingAdapter;
import com.ruihai.xingka.utils.Security;
import com.ruihai.xingka.utils.cache.ImagePipelineConfigFactory;
import com.ruihai.xingka.widget.ProgressHUD;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by apple on 15/8/17.
 */
public class SettingActivity extends BaseActivity {

    private static final int RETAIN_TIME = 2 * 24 * 60 * 60 * 1000;

    public static void launch(Activity from) {
        Intent intent = new Intent(from, SettingActivity.class);
        from.startActivity(intent);
    }

    String[] data_first = {"账号安全"};
    //String[] data_second = {"草稿箱", "消息推送", "图片水印"};
//    String[] data_second = {"草稿箱", "消息推送", "图片上传质量"};
    String[] data_second = {"通知设置", "图片上传质量"};
    //String[] data_second = {"消息推送", "图片上传质量"};
    String[] data_third = {"关于行咖", "行咖公约"};
    //    String[] data_fourth = {"检查更新", "意见反馈", "清除缓存", "退出登录"};
    String[] data_fourth = {"清除缓存", "退出登录"};

    @BindView(R.id.tv_title)
    TextView mTitleView;
    @BindView(R.id.tv_right)
    IconicFontTextView mRightView;
    @BindView(R.id.lv_first)
    ListView mLvFirst;
    @BindView(R.id.lv_second)
    ListView mLvSecond;
    @BindView(R.id.lv_third)
    ListView mLvThird;
    @BindView(R.id.lv_fourth)
    ListView mLvFourth;

    private static final int first_part = 1;
    private static final int second_part = 2;
    private static final int third_part = 3;

    private ActionSheetDialog dialog;

    private SettingAdapter mSecondAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        initViews();
    }

    private void initViews() {
        mTitleView.setText(R.string.mine_setting);
        mRightView.setVisibility(View.GONE);

        SettingAdapter adapter_first = new SettingAdapter(this, data_first, first_part);
        mSecondAdapter = new SettingAdapter(this, data_second, second_part);
        SettingAdapter adapter_third = new SettingAdapter(this, data_third, third_part);
        SettingAdapter adapter_fourth = new SettingAdapter(this, data_fourth, first_part);

        mLvFirst.setAdapter(adapter_first);
        mLvSecond.setAdapter(mSecondAdapter);
        mLvThird.setAdapter(adapter_third);
        mLvFourth.setAdapter(adapter_fourth);
    }

    @OnClick(R.id.tv_back)
    void onBack() {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSecondAdapter.notifyDataSetChanged();
    }

    @OnItemClick(R.id.lv_first)
    void onFirstItemClik(int position) {
        if (position == 0) { // 账号安全
            Intent intent = new Intent();
            intent.setClass(this, AccountSecurityActivity.class);
            startActivity(intent);
        }
    }

    @OnItemClick(R.id.lv_second)
    void onSecondItemClik(int position) {
        if (position == 0) {
            // 消息推送
            Intent intent = new Intent();
//            intent.setClass(this, SettingNotDisturbedActivity.class);
            intent.setClass(this, NotificationSettingsActivity.class);
            startActivity(intent);
        } else if (position == 1) {
            // 图片上传质量
            Intent intent = new Intent(this, UploadSettingActivity.class);
            startActivity(intent);
        } else if (position == 2) { // 图片上传质量
//            Intent intent = new Intent(this, UploadSettingActivity.class);
//            startActivity(intent);
        }
    }

    @OnItemClick(R.id.lv_third)
    void onThirdItemClik(int position) {
        if (position == 0) { // 关于行咖
            Intent intent = new Intent();
            intent.setClass(this, AboutActivity.class);
            startActivity(intent);
        } else if (position == 1) { // 社区公约
            Intent intent = new Intent();
            //intent.setClass(this, RecommendActivity.class);
            CommonWebActivity.launch(SettingActivity.this, data_third[1], Global.CONVENTION_URL);
        }
//        else if (position == 2) { //意见反馈
//            FeedbackActivity.launch(this);
//        }
    }

    @OnItemClick(R.id.lv_fourth)
    void onFourthItemClik(int position) {
        if (position == 0) {
//            checkUpdate();
            clearCache(true);
        } else if (position == 1) {
//            Intent intent = new Intent();
//            intent.setClass(this, FeedbackActivity.class);
//            startActivity(intent);
            final String[] stringItems = {"确定"};
            dialog = new ActionSheetDialog(mContext, stringItems, null);
            //弹出框透明度设为1
            dialog.titleBgColor(Color.parseColor("#ffffff"));
            dialog.lvBgColor(Color.parseColor("#ffffff"));
            dialog.title("是否确定退出登录？")
                    .titleTextSize_SP(14.5f)
                    .itemTextColor(Color.parseColor("#ff554b"))
                    .cancelText(Color.parseColor("#077dfe"))
                    .show();

            dialog.setOnOperItemClickL(new OnOperItemClickL() {
                @Override
                public void onOperItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    dialog.dismiss();
                    updateDeviceToken("");
                    // AccountInfo.getInstance().clearOfficialNum();
//                    AccountInfo.getInstance().clearAccount();
//                    XKApplication.getInstance().exit();
//                    Intent intent = new Intent(mContext, LoginActivity.class);
//                    startActivity(intent);
                }
            });
//        } else if (position == 2) {
//            clearCache(true);
//        } else if (position == 3) {
//            final String[] stringItems = {"确定"};
//            dialog = new ActionSheetDialog(this, stringItems, null);
//            dialog.title("是否确定退出登录？")
//                    .titleTextSize_SP(14.5f)
//                    .show();
//            //弹出框透明度设为1
//            dialog.titleBgColor(Color.parseColor("#ffffff"));
//            dialog.lvBgColor(Color.parseColor("#ffffff"));
//            dialog.setOnOperItemClickL(new OnOperItemClickL() {
//                @Override
//                public void onOperItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                    dialog.dismiss();
//                    AccountInfo.getInstance().clearAccount();
//                    XKApplication.getInstance().exit();
//                    Intent intent = new Intent(mContext, LoginActivity.class);
//                    startActivity(intent);
//                }
//            });
        }
    }

    /**
     * @功能描述 : 检查更新
     */
    private void checkUpdate() {
        UmengUpdateAgent.setUpdateAutoPopup(false);
        UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
            @Override
            public void onUpdateReturned(int updateStatus, UpdateResponse updateResponse) {
                switch (updateStatus) {
                    case UpdateStatus.Yes: //has update
                        UmengUpdateAgent.showUpdateDialog(mContext, updateResponse);
                        break;
                    case UpdateStatus.No: //has no update
//                        AppUtility.showToast("没有更新");
                        ProgressHUD.showSuccessMessage(mContext, getString(R.string.action_noUpdate));
                        break;
                    case UpdateStatus.NoneWifi: //none wifi
//                      AppUtility.showToastMsg(mContext, "没有wifi连接， 只在wifi下更新");
                        UmengUpdateAgent.showUpdateDialog(mContext, updateResponse);
                        break;
                    case UpdateStatus.Timeout: //time out
//                      AppUtility.showToast(getString(R.string.common_network_error));
                        ProgressHUD.showErrorMessage(mContext, getString(R.string.common_network_error));
                        break;
                }
            }
        });
        UmengUpdateAgent.update(mContext);
    }

    /**
     * @param all 是否完全清除
     * @功能描述 : 清除缓存
     */
    private void clearCache(final boolean all) {
        // final String cachePath = GlobalContext.getInstance().getImagePath();
        WorkTask<Void, String, Void> task = new WorkTask<Void, String, Void>() {
            @Override
            public Void workInBackground(Void... params) throws TaskException {
                // File cacheRootFile = new File(cachePath);
                File cacheRootFile = ImagePipelineConfigFactory.getExternalCacheDir(mContext);
                deleteFile(cacheRootFile, all);
                return null;
            }

            void deleteFile(File file, boolean all) {
                if (!file.exists())
                    return;

                if (!isCancelled()) {
                    if (file.isDirectory()) {
                        File[] childFiles = file.listFiles();
                        for (File childFile : childFiles) {
                            deleteFile(childFile, all);
                        }
                    } else {
                        publishProgress(String.valueOf(file.length()));

                        boolean clear = all;
                        if (!clear) {
                            Logger.v(String.format("文件最后修改时间是%s", DateUtils.formatDate(file.lastModified(), DateUtils.TYPE_01)));
                            clear = System.currentTimeMillis() - file.lastModified() >= RETAIN_TIME;
                            if (clear)
                                Logger.v("ClearCache", "缓存超过2天，删除该缓存");
                        } else {
                            file.delete();
                        }
                    }
                }
            }

            @Override
            protected void onProgressUpdate(String... values) {
                super.onProgressUpdate(values);

                if (values != null && values.length > 0) {
                    int value = Integer.parseInt(values[0]);
                    ProgressHUD.showLoadingMessage(mContext, getString(R.string.settings_cache_clear), false);
                }
            }

            @Override
            protected void onFinished() {
                super.onFinished();
                ProgressHUD.showSuccessMessage(mContext, getString(R.string.settings_cache_clear_complete));
            }
        }.execute();
    }

    /**
     * 退出登录
     */
    @OnClick(R.id.btn_exit_login)
    void exitSign() {
        final String[] stringItems = {"确定"};
        dialog = new ActionSheetDialog(this, stringItems, null);
        //弹出框透明度设为1
        dialog.titleBgColor(Color.parseColor("#ffffff"));
        dialog.lvBgColor(Color.parseColor("#ffffff"));
        dialog.title("是否确定退出登录？")
                .titleTextSize_SP(14.5f);
        dialog.itemTextColor(Color.parseColor("#dc445b"))
                .show();

        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                dialog.dismiss();
                //updateDeviceToken("");
            }
        });
    }

    @Override
    protected void onStop() {
        if (dialog != null) {
            if (dialog.isShowing())
                dialog.dismiss();
            dialog = null;
        }
        super.onStop();
    }

    /**
     * 更新设备令牌值
     *
     * @param regId JPush Registration Id
     */
    private void updateDeviceToken(final String regId) {
        // 判断用户是否登录,如果没有登录,则保存DeviceToken值,待用户登录时再进行提交
        if (AccountInfo.getInstance().isLogin()) {
            User currentUser = AccountInfo.getInstance().loadAccount();
            String sAccount = Security.aesEncrypt(String.valueOf(currentUser.getAccount()));
            String sToken = Security.aesEncrypt(regId);
            Call<XKRepo> call = ApiModule.apiService().editDeviceToken(sAccount, sToken);
            call.enqueue(new Callback<XKRepo>() {
                @Override
                public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                    XKRepo xkRepo = response.body();
                    ProgressHUD.dismiss();
                    if (xkRepo.isSuccess()) {
                        logout();
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

    private void logout() {
        // Umeng账号统计:账号登出时调用此接口,调用之后不再发送账号相关内容.
        MobclickAgent.onProfileSignOff();
        AccountInfo.getInstance().clearAccount();
        XKApplication.getInstance().exit();
        Intent intent = new Intent(mContext, LoginActivity.class);
        startActivity(intent);
    }
}

package com.ruihai.xingka.ui.login.otherLogin;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.ruihai.android.common.utils.SystemUtils;
import com.ruihai.android.network.task.TaskException;
import com.ruihai.android.network.task.WorkTask;
import com.ruihai.xingka.AppSettings;
import com.ruihai.xingka.R;
import com.ruihai.xingka.api.ApiModule;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.api.model.User;
import com.ruihai.xingka.api.model.UserCarInfo;
import com.ruihai.xingka.api.model.UserRepo;
import com.ruihai.xingka.api.model.UserTag;
import com.ruihai.xingka.api.model.XKRepo;
import com.ruihai.xingka.ui.BaseActivity;
import com.ruihai.xingka.ui.MainActivity;
import com.ruihai.xingka.ui.login.LoginActivity;
import com.ruihai.xingka.utils.AppUtility;
import com.ruihai.xingka.utils.Security;
import com.ruihai.xingka.widget.ProgressHUD;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 新用户直接进入-绑定账号
 * Created by lqfang on 16/5/21.
 */
public class BindActivity extends BaseActivity {

    public static void launch(Context from, String sign, String headimgurl, String nickname,String type) {
        Intent intent = new Intent(from, BindActivity.class);
        intent.putExtra("sign", sign);
        intent.putExtra("headimgurl", headimgurl);
        intent.putExtra("nickname", nickname);
        intent.putExtra("type", type);
        from.startActivity(intent);
    }

    @BindView(R.id.bind_account)
    TextView mBindAccount;
    @BindView(R.id.new_user)
    TextView mNewUser;

    private String mSign;
    private String mHeadimgUrl;
    private String mNickName;
    private String mType;
    private String mNewImg;

    private UploadManager uploadManager;

    public LoginActivity loginActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind);
        ButterKnife.bind(this);
        mSign = getIntent().getStringExtra("sign");
        mHeadimgUrl = getIntent().getStringExtra("headimgurl");
        mNickName = getIntent().getStringExtra("nickname");
        mType = getIntent().getStringExtra("type");
        mNewImg = "0";

        //进入绑定账号
        mBindAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AccountBindActivity.launch(mContext, mSign, mType);
            }
        });

        //防重复点击判断
        if(AppUtility.isFastClick()){
            return;
        }

        //新用户直接进入
        mNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(mHeadimgUrl)) {
                    otherLogin("");
                }else {
                    downloadImage(mHeadimgUrl);
                }
            }
        });

    }

    /**
     * 三方登录
     *
     */
    public void otherLogin(String img) {
        if(mNickName == null || TextUtils.isEmpty(mNickName)){
            mNickName = "";
        }

        String sSign = Security.aesEncrypt(mSign);
        String sImg = Security.aesEncrypt(img);
        String sIsNewImg = Security.aesEncrypt(mNewImg);
        String sNick = Security.aesEncrypt(mNickName);
        String sType = Security.aesEncrypt(mType);

        ProgressHUD.showLoadingMessage(mContext, "正在登录", true);
        Call<UserRepo> call = ApiModule.apiService_1().otherLogin(sSign, sImg, sIsNewImg, sNick, sType);
        call.enqueue(new Callback<UserRepo>() {
            @Override
            public void onResponse(Call<UserRepo> call, Response<UserRepo> response) {
                UserRepo userRepo = response.body();
                ProgressHUD.dismiss();
                if (userRepo.isSuccess()) {
                    User user = userRepo.getUserInfo();
                    List<UserTag> userTags = userRepo.getUserTags();
                    UserCarInfo userCarInfo = userRepo.getUserCarInfo();
                    // 保存用户基本数据到本地
                    AccountInfo.getInstance().saveAccount(user);
                    AccountInfo.getInstance().savePassWord(Security.aesEncrypt(user.getPassword()));
                    AccountInfo.getInstance().saveUserTags(userTags);
                    AccountInfo.getInstance().saveUserCarInfo(userCarInfo);
                    // 判断JPush Reg Id是否存在,如果存在则提交到服务器进行更新
                    String deviceToken = AppSettings.getJPushRegId();
                    if (!TextUtils.isEmpty(deviceToken)) {
                        updateDeviceToken(deviceToken);
                    } else { // 直接跳转至主页
                        ProgressHUD.dismiss();
//                        loginActivity.login(String.valueOf(user.getAccount()));
                        loginSuccess();
                    }
                } else {
                    ProgressHUD.showInfoMessage(mContext, userRepo.getMsg());
                }
            }

            @Override
            public void onFailure(Call<UserRepo> call, Throwable t) {
                ProgressHUD.showErrorMessage(mContext, getString(R.string.common_network_error));
            }


        });

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
                        Logger.d("更新DeviceToken成功" + regId);
                        loginSuccess();
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


    /**
     * 下载头像
     *
     * @param imageUrl
     */
    private void downloadImage(final String imageUrl) {
        new WorkTask<Void, Void, Bitmap>() {

            @Override
            protected void onPrepare() {
                super.onPrepare();
            }

            @Override
            public Bitmap workInBackground(Void... params) throws TaskException {

                try {
                    Bitmap theBitmap = Glide.with(mContext)
                            .load(imageUrl)
                            .asBitmap()
                            .into(-1, -1)
                            .get();
                    return theBitmap;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onSuccess(Bitmap bitmap) {
                super.onSuccess(bitmap);
                if (bitmap != null) {
                    saveImageToExternalStorage(bitmap);
                } else {
                    ProgressHUD.dismiss();
                }
            }

            @Override
            protected void onFinished() {
                super.onFinished();
            }
        }.execute();
    }

    /**
     * 保存头像
     *
     * @param finalBitmap
     */
    private void saveImageToExternalStorage(Bitmap finalBitmap) {
        String root = SystemUtils.getSdcardPath() + File.separator + AppSettings.getImageSavePath();
        File myDir = new File(root + File.separator + AppSettings.getImageSavePath());
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-" + n + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            //上传到七牛
            updateAvatarToQiniu(file);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 上传到七牛
     *
     * @param avatarFile
     */
    private void updateAvatarToQiniu(final File avatarFile) {
        String randomStr = Security.aesEncrypt("android");
        Call<XKRepo> call = ApiModule.apiService().getQiniuToken(randomStr);
        call.enqueue(new Callback<XKRepo>() {
            @Override
            public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                XKRepo xkRepo = response.body();
                if (xkRepo.isSuccess()) {
                    String token = xkRepo.getMsg();
                    upload(token, avatarFile);
                } else {
                    ProgressHUD.dismiss();
                    ProgressHUD.showInfoMessage(mContext, xkRepo.getMsg());
                }
            }

            @Override
            public void onFailure(Call<XKRepo> call, Throwable t) {
                ProgressHUD.showErrorMessage(mContext, getString(R.string.common_network_error));
            }

        });
    }

    private void upload(String uploadToken, File avatarFile) {
        if (this.uploadManager == null) {
            this.uploadManager = new UploadManager();
        }

        final String uploadFilKey = AppUtility.generateUUID();
        this.uploadManager.put(avatarFile, uploadFilKey, uploadToken, new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                if (info.isOK()) {
                    String fileKey = response.optString("key");
                    otherLogin(fileKey); // 新用户登录
                }
            }
        }, null);
    }


    /**
     * 登录成功
     */
    private void loginSuccess() {
        Intent intent = new Intent(BindActivity.this, MainActivity.class);
        intent.putExtra("launchSplash", false);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.tv_back)
    void onBack() {
        finish();
    }

}

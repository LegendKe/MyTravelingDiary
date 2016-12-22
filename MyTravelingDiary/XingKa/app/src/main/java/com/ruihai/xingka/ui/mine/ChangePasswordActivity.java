package com.ruihai.xingka.ui.mine;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ruihai.iconicfontengine.widget.IconicFontTextView;
import com.ruihai.xingka.R;
import com.ruihai.xingka.XKApplication;
import com.ruihai.xingka.api.ApiModule;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.api.model.User;
import com.ruihai.xingka.api.model.XKRepo;
import com.ruihai.xingka.ui.BaseActivity;
import com.ruihai.xingka.ui.login.LoginActivity;
import com.ruihai.xingka.utils.Security;
import com.ruihai.xingka.widget.ProgressHUD;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 修改密码
 * Created by lqfang on 15/8/20.
 */
public class ChangePasswordActivity extends BaseActivity {


    @BindView(R.id.et_old_password)
    EditText mOldPasswordEditText;
    @BindView(R.id.et_new_password)
    EditText mNewPasswordEditText;
    @BindView(R.id.et_repassword)
    EditText mRePasswordEditText;
    @BindView(R.id.tv_title)
    TextView mTitle;
    @BindView(R.id.tv_right)
    IconicFontTextView mRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ButterKnife.bind(this);
        mTitle.setText(R.string.mime_change_password);
        mRight.setVisibility(View.GONE);
    }

    @OnClick(R.id.tv_back)
    void onBack() {
        finish();
    }

    @OnClick(R.id.btn_complete)
    void onCompleteBtn() {

        final String repassword = mRePasswordEditText.getText().toString().trim();
        String newpassword = mNewPasswordEditText.getText().toString().trim();
        String oldpassword = mOldPasswordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(oldpassword)) {
            ProgressHUD.showInfoMessage(mContext, getString(R.string.please_input_old_password));
            return;
        } else if (TextUtils.isEmpty(newpassword)) {
            ProgressHUD.showInfoMessage(mContext, getString(R.string.please_input_new_password));
            return;
        } else if (newpassword.length() < 6 || newpassword.length() > 18) {
            ProgressHUD.showInfoMessage(mContext, getString(R.string.register_password_tip));
            return;
        } else if (TextUtils.isEmpty(repassword)) {
            ProgressHUD.showInfoMessage(mContext, getString(R.string.please_input_repassword));
            return;
        } else if (!newpassword.equals(repassword)) {
            ProgressHUD.showInfoMessage(mContext, getString(R.string.please_input_equal));
            return;
        }
        Log.e("repassword", repassword);
        Log.e("newpassword", newpassword);
        Log.e("oldpassword", oldpassword);

        User user = AccountInfo.getInstance().loadAccount();
        ApiModule.apiService().editPassWord(Security.aesEncrypt(String.valueOf(user.getAccount())),
                Security.aesEncrypt(oldpassword), Security.aesEncrypt(newpassword)).enqueue(new Callback<XKRepo>() {
            @Override
            public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                XKRepo xkRepo = response.body();
                String msg = xkRepo.getMsg();
                if (xkRepo.isSuccess()) { // 更改密码成功
                    ProgressHUD.dismiss();
                    ProgressHUD.showSuccessMessage(mContext, msg);
                    AccountInfo.getInstance().clearAccount();
                    XKApplication.getInstance().exit();
                    startActivity(new Intent(ChangePasswordActivity.this, LoginActivity.class));
//                            finish();
                    updateDeviceToken("");
                } else { // 更改密码失败
                    ProgressHUD.showErrorMessage(mContext, msg);
                }
            }

            @Override
            public void onFailure(Call<XKRepo> call, Throwable t) {
                ProgressHUD.showErrorMessage(mContext, t.getLocalizedMessage());
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

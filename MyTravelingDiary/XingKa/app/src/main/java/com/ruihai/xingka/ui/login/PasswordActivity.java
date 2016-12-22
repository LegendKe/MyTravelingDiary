package com.ruihai.xingka.ui.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ruihai.xingka.R;
import com.ruihai.xingka.api.ApiModule;
import com.ruihai.xingka.api.model.XKRepo;
import com.ruihai.xingka.ui.common.UmengActivity;
import com.ruihai.xingka.utils.Security;
import com.ruihai.xingka.widget.ProgressHUD;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.smssdk.SMSSDK;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mac on 15/12/15.
 */
public class PasswordActivity extends UmengActivity {

    public static void launch(Activity from, String mPhoneNum, String countryNum) {
        Intent intent = new Intent(from, PasswordActivity.class);
        intent.putExtra("phoneNum", mPhoneNum);
        intent.putExtra("countryNumber", countryNum);
        from.startActivity(intent);
    }

    @BindView(R.id.et_newPassWord)
    EditText mNewPassWordEdittext;
    @BindView(R.id.et_repassword)
    EditText mRepassword;
    @BindView(R.id.tv_base)
    TextView mBase;
    @BindView(R.id.tv_title)
    TextView mTitle;

    private Context mContext;
    private String phoneNum;
    private String countryNumber = "+86";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        ButterKnife.bind(this);
        mContext = this;
        initListener();

        mTitle.setText("密码重置");
        mBase.setVisibility(View.VISIBLE);
        phoneNum = getIntent().getStringExtra("phoneNum");
        countryNumber = getIntent().getStringExtra("countryNumber");
    }

    private void initListener() {

    }

    /**
     * Toolbar头部返回键
     *
     * @param view
     */
    @OnClick(R.id.iv_back)
    public void onBackClicked(View view) {
        finish();
    }

    /**
     * 提交按钮监听
     *
     * @param view
     */
    @OnClick(R.id.btn_register)
    public void onRegisteredClicked(View view) {
        String password = mNewPassWordEdittext.getText().toString().trim();
        String repassword = mRepassword.getText().toString().trim();

        if (TextUtils.isEmpty(password)) {
            ProgressHUD.showInfoMessage(mContext, getString(R.string.register_please_input_password));
            return;
        } else if (password.length() < 6 || password.length() > 16) {
            ProgressHUD.showInfoMessage(mContext, getString(R.string.register_password_tip));
            return;
        } else if (TextUtils.isEmpty(repassword)) {
            ProgressHUD.showInfoMessage(mContext, getString(R.string.register_please_input_repassword));
        } else if (repassword.length() < 6 || repassword.length() > 16) {
            ProgressHUD.showInfoMessage(mContext, getString(R.string.register_password_tip));
        } else {
            submitRegist();
        }

    }

    /**
     * 提交重置信息
     */
    private void submitRegist() {
        String password = mNewPassWordEdittext.getText().toString().trim();
        String securityPhoneNum = Security.aesEncrypt(phoneNum);
        String securityPassword = Security.aesEncrypt(password);
        String securityCountryNum = Security.aesEncrypt(countryNumber);

        ProgressHUD.showLoadingMessage(mContext, "正在修改", false);
        ApiModule.apiService_1().findPassWord2(securityPhoneNum, securityPassword,
                securityCountryNum).enqueue(new Callback<XKRepo>() {
            @Override
            public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                XKRepo xkRepo = response.body();
                String msg = xkRepo.getMsg();
                if (xkRepo.isSuccess()) { // 注册成功
                    startActivity(new Intent(PasswordActivity.this, LoginActivity.class));
                    ProgressHUD.showSuccessMessage(mContext, msg);
//                                    enterLoginPage();
                } else { // 登录失败
                    ProgressHUD.showErrorMessage(mContext, msg);
                }
            }

            @Override
            public void onFailure(Call<XKRepo> call, Throwable t) {
                ProgressHUD.showErrorMessage(mContext, t.getLocalizedMessage());
            }

        });
    }

    //注册回调监听接口解绑
    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterAllEventHandler();
    }

}

package com.ruihai.xingka.ui.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ruihai.xingka.R;
import com.ruihai.xingka.api.ApiModule;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.api.model.User;
import com.ruihai.xingka.api.model.XKRepo;
import com.ruihai.xingka.ui.BaseActivity;
import com.ruihai.xingka.utils.Security;
import com.ruihai.xingka.widget.ProgressHUD;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lqfang on 15/12/16.
 */
public class RegisterPasswordActivity extends BaseActivity {

    public static void launch(Activity from, String mPhoneNum, String countryNum) {
        Intent intent = new Intent(from, RegisterPasswordActivity.class);
        intent.putExtra("phoneNum", mPhoneNum);
        intent.putExtra("countryNumber", countryNum);
        from.startActivity(intent);
    }

    @BindView(R.id.et_newPassWord)
    EditText mPasswordEditText;
    @BindView(R.id.et_repassword)
    EditText mRepassword;
    @BindView(R.id.tv_title)
    TextView mTitle;
    @BindView(R.id.ll_base)
    LinearLayout mBase;
    @BindView(R.id.btn_register)
    TextView mRegister;

    private Context mContext;
    private String phoneNum;
    private String countryNumber = "+86";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        ButterKnife.bind(this);
        mContext = this;

        mTitle.setText("用户注册");
        mRegister.setText("完成注册");
        mBase.setVisibility(View.VISIBLE);

        phoneNum = getIntent().getStringExtra("phoneNum");
        countryNumber = getIntent().getStringExtra("countryNumber");
    }

    /**
     * 监听Toolbar返回按钮
     *
     * @param view
     */
    @OnClick(R.id.iv_back)
    public void onBackClicked(View view) {
        finish();
    }


    //注册按钮 监听
    @OnClick(R.id.btn_register)
    public void submit(View view) {
        String password = mPasswordEditText.getText().toString().trim();
        String repassword = mRepassword.getText().toString().trim();

        if (TextUtils.isEmpty(password)) {
            ProgressHUD.showInfoMessage(mContext, getString(R.string.register_please_input_password));
            return;
        } else if (password.length() < 6 || password.length() > 16) {
            ProgressHUD.showInfoMessage(mContext, getString(R.string.register_password_tip));
            return;
        } else if (TextUtils.isEmpty(repassword)) {
            ProgressHUD.showInfoMessage(mContext, getString(R.string.register_please_input_repassword));
            return;
        } else if (repassword.length() < 6 || repassword.length() > 16) {
            ProgressHUD.showInfoMessage(mContext, getString(R.string.register_password_tip));
            return;
        } else {
            submitRegist();
        }
    }

    //用户协议 点击监听
    @OnClick(R.id.tv_user_agreement)
    void onAgreementClicked() {
        Intent i = new Intent();
        i.setClass(this, AgreementActivity.class);
        startActivity(i);

    }

    /**
     * 提交注册并需要注册邀请码
     */
    private void submitRegistWithInvitationCode(String inviteCode) {
        String password = mPasswordEditText.getText().toString().trim();
        String securityPhoneNum = Security.aesEncrypt(phoneNum);
        String securityPassword = Security.aesEncrypt(password);
        String securityCode = Security.aesEncrypt(inviteCode);

        ProgressHUD.showLoadingMessage(mContext, "正在注册", false);
        ApiModule.apiService_1().invitationRegister(securityPhoneNum, securityPassword, securityCode).enqueue(new Callback<XKRepo>() {
            @Override
            public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                XKRepo xkRepo = response.body();
                ProgressHUD.dismiss();
                String msg = xkRepo.getMsg();
                if (xkRepo.isSuccess()) { // 注册成功
                    ProgressHUD.showSuccessMessage(mContext, msg);
                    User user = new User();
                    user.setAccount(xkRepo.getCode());
                    AccountInfo.getInstance().saveAccount(user);
                    enterLoginPage();
                } else { // 注册失败
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
     * 提交注册信息
     */
    private void submitRegist() {
        String password = mPasswordEditText.getText().toString().trim();
        String securityPhoneNum = Security.aesEncrypt(phoneNum);
        final String securityPassword = Security.aesEncrypt(password);
        String securityCountryNum = Security.aesEncrypt(countryNumber);

        ProgressHUD.showLoadingMessage(mContext, "正在提交注册", false);
        ApiModule.apiService_1().userRegister2(securityPhoneNum, securityPassword,
                securityCountryNum).enqueue(new Callback<XKRepo>() {
            @Override
            public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                XKRepo xkRepo = response.body();
                String msg = xkRepo.getMsg();
                User user = new User();
                AccountInfo.getInstance().savePassWord(securityPassword);
                user.setAccount(xkRepo.getCode());//Code传的是行咖号
                AccountInfo.getInstance().saveAccount(user);
                if (xkRepo.isSuccess()) { // 注册成功
                    ProgressHUD.showSuccessMessage(mContext, msg);
                    enterLoginPage();
                } else { // 注册失败
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
     * 跳转进入完善资料页
     */
    private void enterLoginPage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    startActivity(new Intent(mContext, PerfectDataActivity.class));
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}

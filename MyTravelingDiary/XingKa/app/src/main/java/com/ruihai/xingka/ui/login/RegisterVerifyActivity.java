package com.ruihai.xingka.ui.login;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.ruihai.xingka.R;
import com.ruihai.xingka.api.SMSApiModule;
import com.ruihai.xingka.api.model.SMSStatus;
import com.ruihai.xingka.ui.BaseActivity;
import com.ruihai.xingka.utils.AppUtility;
import com.ruihai.xingka.utils.Security;
import com.ruihai.xingka.widget.ProgressHUD;
import com.umeng.onlineconfig.OnlineConfigAgent;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mob.tools.utils.R.getStringRes;

/**
 * Created by lqfang on 15/12/16.
 */
public class RegisterVerifyActivity extends BaseActivity {

    public static void launch(Activity from, String phoneNum, String countryNum) {
        Intent intent = new Intent(from, RegisterVerifyActivity.class);
        intent.putExtra("phoneNum", phoneNum);
        intent.putExtra("countryNumber", countryNum);
        from.startActivity(intent);
    }

    private static final int RETRY_INTERVAL = 60;
    private int time = RETRY_INTERVAL;

    @BindView(R.id.et_verify_code)
    EditText mVerifyCodeEditText;
    @BindView(R.id.btn_get_code)
    TextView mGetCodeButton;
    @BindView(R.id.tv_title)
    TextView mTitle;
    @BindView(R.id.ll_base)
    LinearLayout mBase;
    private EventHandler mHandler;
    private String mPhoneNum;
    private String verifyCode;
    private String countryNumber = "+86";

    private int smsChannel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        ButterKnife.bind(this);

        initListener();

        mTitle.setText("用户注册");
        mBase.setVisibility(View.VISIBLE);

        mPhoneNum = getIntent().getStringExtra("phoneNum");
        countryNumber = getIntent().getStringExtra("countryNumber");

        String smsChannelValue = OnlineConfigAgent.getInstance().getConfigParams(this, "SMSChannel");
        if (!TextUtils.isEmpty(smsChannelValue)) {
            smsChannel = Integer.parseInt(smsChannelValue);
        }

        countDown();
    }

    private void initListener() {
        mHandler = new EventHandler() {
            @Override
            public void afterEvent(final int event, final int result, final Object data) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ProgressHUD.dismiss();
                        if (result == SMSSDK.RESULT_COMPLETE) {
                            if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                                afterVerificationCodeRequested();
                            } else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                                // 提交验证码
                                afterSubmit(result, data);
                            }
                        } else {
                            // 根据服务器返回的网络错误，给出提示
                            int status = 0;
                            try {
                                ((Throwable) data).printStackTrace();
                                Throwable throwable = (Throwable) data;
                                //  {"detail":"用户提交校验的验证码错误。","status":468,"description":"需要校验的验证码错误"}
                                JSONObject object = new JSONObject(throwable.getMessage());
                                String des = object.optString("description");

                                if (!TextUtils.isEmpty(des)) {
                                    showDialog(des);
                                    return;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            // 如果木有找到资源，默认提示
                            int resId = 0;
                            if (status >= 400) {
                                resId = getStringRes(mContext,
                                        "smssdk_error_desc_" + status);
                            } else {
                                resId = getStringRes(mContext,
                                        "smssdk_network_error");
                            }
                            if (resId > 0) {
                                Toast.makeText(mContext, resId, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        };
        SMSSDK.registerEventHandler(mHandler);
    }

    public void showDialog(String des) {
        final NormalDialog dialog = new NormalDialog(mContext);
        dialog.setCancelable(false); // 设置点击返回键Dialog不消失
        dialog.setCanceledOnTouchOutside(false); // 设置点击屏幕Dialog不消失

        dialog.isTitleShow(false)
                .bgColor(Color.parseColor("#ffffff"))
                .cornerRadius(5)
                .content(des)
                .btnNum(1)
                .btnText("确定")
                .contentGravity(Gravity.CENTER)
                .contentTextColor(Color.parseColor("#33333d"))
                .dividerColor(Color.parseColor("#dcdce4"))
                .btnTextSize(15.5f, 15.5f)
                .btnTextColor(Color.parseColor("#ff2814"), Color.parseColor("#0077fe"))
                .widthScale(0.85f)
                .show();

        dialog.setOnBtnClickL(new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                dialog.dismiss();
            }
        });
    }

    /**
     * 倒数计时
     */
    private void countDown() {
        new CountDownTimer(time * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                String unReceive = getString(R.string.receive_msg, millisUntilFinished / 1000);
                mGetCodeButton.setText(Html.fromHtml(unReceive));
                mGetCodeButton.setEnabled(false);
            }

            public void onFinish() {
                mGetCodeButton.setText(getString(R.string.resend_identify_code));
                mGetCodeButton.setEnabled(true);
            }
        }.start();
    }

    /**
     * 监听Toolbar返回按钮
     *
     * @param view
     */
    @OnClick(R.id.iv_back)
    public void onBackClicked(View view) {
        showNotifyDialog();
    }

    /**
     * 监听获取验证码按钮
     */
    @OnClick(R.id.btn_get_code)
    void getVerifyCode() {
        checkPhoneNum(mPhoneNum, countryNumber);
    }

    //下一步按钮 监听
    @OnClick(R.id.btn_register)
    public void submit(View view) {
        verifyCode = mVerifyCodeEditText.getText().toString().trim();

        if (TextUtils.isEmpty(verifyCode)) {
            ProgressHUD.showInfoMessage(mContext, getString(R.string.register_input_verify_code));
            return;
        }

        if (smsChannel == 0) {
            commitVerifyCodeBySMSApi();
        } else {
            SMSSDK.submitVerificationCode(countryNumber, mPhoneNum, verifyCode);
        }
    }

    private void commitVerifyCodeBySMSApi() {
        String aesPhoneNum = Security.aesEncrypt1(mPhoneNum);
        String aesCode = Security.aesEncrypt1(verifyCode);
        SMSApiModule.apiService().verification(aesPhoneNum, aesCode, "1.1").enqueue(new Callback<SMSStatus>() {
            @Override
            public void onResponse(Call<SMSStatus> call, Response<SMSStatus> response) {
                SMSStatus smsStatus = response.body();
                if (smsStatus.isSuccess()) {
                    RegisterPasswordActivity.launch(RegisterVerifyActivity.this, mPhoneNum, countryNumber);
                    finish();
                } else {
                    String bl = smsStatus.getCode();
                    String message = "验证码验证失败";
                    if (bl.equals("1")) {
                        message = "验证码不正确，请重新输入";
                    } else if (bl.equals("2")) {
                        message = "验证码验证超时，请重新获取";
                    }
                    ProgressHUD.showErrorMessage(mContext, message);
                }
            }

            @Override
            public void onFailure(Call<SMSStatus> call, Throwable t) {
                ProgressHUD.showErrorMessage(mContext, t.getLocalizedMessage());
            }
        });
    }

    //用户协议 点击监听
    @OnClick(R.id.tv_user_agreement)
    void onAgreementClicked() {
        Intent i = new Intent();
        i.setClass(this, AgreementActivity.class);
        startActivity(i);
    }

    /**
     * 提交验证码成功后的执行事件
     *
     * @param result
     * @param data
     */
    private void afterSubmit(final int result, final Object data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (result == SMSSDK.RESULT_COMPLETE) {
                    RegisterPasswordActivity.launch(RegisterVerifyActivity.this, mPhoneNum, countryNumber);
                    finish();
                } else {
                    ((Throwable) data).printStackTrace();
                    // 验证码不正确
                    ProgressHUD.showErrorMessage(mContext, getString(R.string.register_verify_code_wrong));
                }
            }
        });
    }


    /**
     * 检测用户手机号码
     *
     * @param phone 手机号码
     * @param code  国家编码
     */
    private void checkPhoneNum(String phone, String code) {
        if (code.startsWith("+")) {
            code = code.substring(1);
        }
        if (TextUtils.isEmpty(phone)) {
            ProgressHUD.showInfoMessage(mContext, getString(R.string.register_please_input_phone));
            return;
        } else if (countryNumber.equals("+86") && !AppUtility.isMobilePhone(phone)) {
            ProgressHUD.showInfoMessage(mContext, getString(R.string.register_please_input_right_phone));
            return;
        }
        ProgressHUD.showLoadingMessage(mContext, "正在发送中...", false);
        // 获取手机短信验证码
        if (smsChannel == 0) { // 默认SMS API
            sendVerfiyCodeBySMSApi(phone);
        } else { // Mob SMS
            SMSSDK.getVerificationCode(code, phone);
        }
    }

    /**
     * 发送短信验证码
     */
    private void sendVerfiyCodeBySMSApi(String phoneNum) {
        String aesZone = Security.aesEncrypt1(countryNumber);
        String aesPhone = Security.aesEncrypt1(phoneNum);
        SMSApiModule.apiService().send(aesZone, aesPhone, "1.1").enqueue(new Callback<SMSStatus>() {
            @Override
            public void onResponse(Call<SMSStatus> call, Response<SMSStatus> response) {
                SMSStatus smsStatus = response.body();
                if (smsStatus.isSuccess()) {
                    ProgressHUD.dismiss();
                    afterVerificationCodeRequested();
                } else {
                    String bl = smsStatus.getCode();
                    String message = "验证码发送失败";
                    if (bl.equals("-888")) {
                        message = "已经超过当天最大发送条数";
                    } else if (bl.equals("-999")) {
                        message = "短信服务器异常，请重试";
                    }
                    ProgressHUD.showErrorMessage(mContext, message);
                }
            }

            @Override
            public void onFailure(Call<SMSStatus> call, Throwable t) {
                ProgressHUD.showErrorMessage(mContext, t.getLocalizedMessage());
            }
        });
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        SMSSDK.unregisterAllEventHandler();
//    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    /**
     * 按返回键时，弹出的提示对话框
     * <p/>
     * 验证码短信可能略有延迟,确定返回并重新开始  返回|等待
     */
    private void showNotifyDialog() {
        RelativeLayout layout = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.dialog_communal, null);
        final Dialog dialog = new AlertDialog.Builder(this).create();

        dialog.setCancelable(false);
        dialog.show();
        dialog.getWindow().setContentView(layout);

        layout.findViewById(R.id.update_icon).setVisibility(View.GONE);
        TextView message = (TextView) layout.findViewById(R.id.update_content);
        message.setGravity(Gravity.CENTER);
        message.setText("验证码短信可能略有延迟,确定返回并重新开始");

        Button btnOK = (Button) layout.findViewById(R.id.umeng_update_id_ok);
        btnOK.setText("等待");
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Button btnCancel = (Button) layout.findViewById(R.id.umeng_update_id_cancel);
        btnCancel.setText("返回");
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
//                startActivity(new Intent(RegisterVerifyActivity.this,RegisterPhoneNumActivity.class));
//                Intent intent = new Intent(RegisterVerifyActivity.this,RegisterPhoneNumActivity.class);
//                intent.putExtra("moble",mPhoneNum);

                finish();
            }
        });
    }

    private void afterVerificationCodeRequested() {
        // 请求验证码后，执行倒计时操作
        countDown();
    }

}


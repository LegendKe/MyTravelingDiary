package com.ruihai.xingka.ui.mine;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.Html;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.ruihai.iconicfontengine.widget.IconicFontTextView;
import com.ruihai.xingka.R;
import com.ruihai.xingka.XKApplication;
import com.ruihai.xingka.api.ApiModule;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.api.model.User;
import com.ruihai.xingka.api.model.XKRepo;
import com.ruihai.xingka.ui.BaseActivity;
import com.ruihai.xingka.ui.login.LoginActivity;
import com.ruihai.xingka.ui.login.chooseCountry.CharacterParserUtil;
import com.ruihai.xingka.ui.login.chooseCountry.CountryActivity;
import com.ruihai.xingka.ui.login.chooseCountry.CountrySortModel;
import com.ruihai.xingka.ui.login.chooseCountry.GetCountryNameSort;
import com.ruihai.xingka.utils.AppUtility;
import com.ruihai.xingka.utils.Security;
import com.ruihai.xingka.widget.ProgressHUD;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
 * Created by apple on 15/8/20.
 */
public class ChangeMobileActivity extends BaseActivity {

    @BindView(R.id.et_new_mobile)
    EditText mNewMobile;
    @BindView(R.id.et_login_password)
    EditText mLoginPassword;
    @BindView(R.id.et_get_verify_code)
    EditText mGetVerifyCode;
    @BindView(R.id.btn_verify_code)
    TextView mVerifyCodeTxt;
    @BindView(R.id.tv_title)
    TextView mTitle;
    @BindView(R.id.tv_right)
    IconicFontTextView mRight;
    @BindView(R.id.tv_num)
    TextView mNum;

    private static final int RETRY_INTERVAL = 60;
    private int time = RETRY_INTERVAL;
    private EventHandler mHandler;

    private GetCountryNameSort countryChangeUtil;

    private CharacterParserUtil characterParserUtil;

    private List<CountrySortModel> mAllCountryList;

    String beforText = null;

    private String countryNumber = "+86";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_mobile);
        ButterKnife.bind(this);
        mTitle.setText(R.string.mime_change_phone);
        mRight.setVisibility(View.GONE);

        initView();
        initCountryList();
        setListener();

        initListener();
    }

    private void initListener() {

        mHandler = new EventHandler() {
            @Override
            public void afterEvent(final int event, final int result, final Object data) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (result == SMSSDK.RESULT_COMPLETE) {
                            if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                                // 请求验证码后，执行倒计时操作
                                countDown();
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

                                JSONObject object = new JSONObject(throwable.getMessage());
                                String des = object.optString("detail");
                                if (!TextUtils.isEmpty(des)) {
//                                    Toast.makeText(mContext, des, Toast.LENGTH_SHORT).show();
                                    dialog(des);
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
//                            ProgressHUD.showErrorMessage(mContext, getString(R.string.common_network_error));
                        }
                    }
                });
            }
        };
        SMSSDK.registerEventHandler(mHandler);
    }

    public void dialog(String des) {
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
                mVerifyCodeTxt.setText(Html.fromHtml(unReceive));
                mVerifyCodeTxt.setEnabled(false);
            }

            public void onFinish() {
                mVerifyCodeTxt.setText(getString(R.string.resend_identify_code));
                mVerifyCodeTxt.setEnabled(true);
            }
        }.start();
    }

    @OnClick(R.id.tv_back)
    void onBack() {
        finish();
    }

    // 验证码按钮点击监听
    @OnClick(R.id.btn_verify_code)
    void onVerifyCode() {
        String phoneNum = mNewMobile.getText().toString().trim().replaceAll("\\s*", "");
//        checkPhoneNum(phoneNum, "+86");
        checkPhoneNum(phoneNum, countryNumber);

    }

    //确定按钮点击监听
    @OnClick(R.id.btn_complete)
    void onComplete() {

        String phoneNum = mNewMobile.getText().toString().trim().replaceAll("\\s*", "");
        String password = mLoginPassword.getText().toString().trim();
        String verifyCode = mGetVerifyCode.getText().toString().trim();

        if (TextUtils.isEmpty(password)) {
            ProgressHUD.showInfoMessage(mContext, getString(R.string.please_input_old_password));
            return;
        } else if (password.length() < 6 || password.length() > 16) {
            ProgressHUD.showInfoMessage(mContext, getString(R.string.register_password_tip));
            return;
        } else if (TextUtils.isEmpty(phoneNum)) {
            ProgressHUD.showInfoMessage(mContext, getString(R.string.register_please_input_phone));
            return;
        } else if (countryNumber.equals("+86") && !AppUtility.isMobilePhone(phoneNum)) {
//            ProgressHUD.showInfoMessage(mContext, getString(R.string.register_please_input_right_phone));
            dialog(getString(R.string.register_please_input_right_phone));
            return;
        } else if (phoneNum.equals(currentUser.getPhone())) {
            ProgressHUD.showInfoMessage(mContext, "对不起,您输入的新号码和原号码一致,请重新输入");
            return;
        } else if (TextUtils.isEmpty(verifyCode)) {
            ProgressHUD.showInfoMessage(mContext, getString(R.string.register_input_verify_code));
            return;
        } else {
            submitRegist();
        }

//        SMSSDK.submitVerificationCode("86", phoneNum, verifyCode);
        SMSSDK.submitVerificationCode(countryNumber, phoneNum, verifyCode);

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
                    submitRegist();
                } else {
                    ((Throwable) data).printStackTrace();
                    // 验证码不正确
                    ProgressHUD.showErrorMessage(mContext, getString(R.string.register_verify_code_wrong));
                }
            }
        });
    }

    /**
     * 提交更改信息
     */
    private void submitRegist() {
        User user = AccountInfo.getInstance().loadAccount();

        String phoneNum = mNewMobile.getText().toString().trim();
        String password = mLoginPassword.getText().toString().trim();
        String securityPhoneNum = Security.aesEncrypt(phoneNum);
        String securityPassword = Security.aesEncrypt(password);
        String securityAccount = Security.aesEncrypt(String.valueOf(user.getAccount()));
        String securityCountryNum = Security.aesEncrypt(countryNumber);

        ProgressHUD.showLoadingMessage(mContext, "正在更换", false);
        ApiModule.apiService_1().changePhone2(securityAccount, securityPassword, securityPhoneNum,
                securityCountryNum).enqueue(new Callback<XKRepo>() {
            @Override
            public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                XKRepo xkRepo = response.body();
                String msg = xkRepo.getMsg();
                if (xkRepo.isSuccess()) { // 更改成功
                    ProgressHUD.showSuccessMessage(mContext, msg);
                    AccountInfo.getInstance().clearAccount();
                    XKApplication.getInstance().exit();
                    startActivity(new Intent(ChangeMobileActivity.this, LoginActivity.class));
                    finish();
                } else { // 更改失败
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
//            dialog(getString(R.string.register_please_input_right_phone));
            return;
        }
        // 获取手机短信验证码
        SMSSDK.getVerificationCode(code, phone);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterAllEventHandler();
    }

    private void initView() {

        mAllCountryList = new ArrayList<CountrySortModel>();
        countryChangeUtil = new GetCountryNameSort();
        characterParserUtil = new CharacterParserUtil();

    }

    private void initCountryList() {
        String[] countryList = getResources().getStringArray(R.array.country_code_list_ch);

        for (int i = 0, length = countryList.length; i < length; i++) {
            String[] country = countryList[i].split("\\*");

            String countryName = country[0];
            String countryNumber = country[1];
            String countrySortKey = characterParserUtil.getSelling(countryName);
            CountrySortModel countrySortModel = new CountrySortModel(countryName, countryNumber,
                    countrySortKey);
            String sortLetter = countryChangeUtil.getSortLetterBySortKey(countrySortKey);
            if (sortLetter == null) {
                sortLetter = countryChangeUtil.getSortLetterBySortKey(countryName);
            }

            countrySortModel.sortLetters = sortLetter;
            mAllCountryList.add(countrySortModel);
        }

    }

    private void setListener() {
        mNum.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(ChangeMobileActivity.this, CountryActivity.class);
                startActivityForResult(intent, 12);
            }
        });

        mNum.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                beforText = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
                String contentString = mNum.getText().toString();

                CharSequence contentSeq = mNum.getText();

//				Log.i(TAG, "contentString :" + contentString.length());

                if (contentString.length() > 1) {
                    // 按照输入内容进行匹配
                    List<CountrySortModel> fileterList = (ArrayList<CountrySortModel>) countryChangeUtil
                            .search(contentString, mAllCountryList);

                    if (fileterList.size() == 1) {
//                        mNum.setText(fileterList.get(0).countryNumber);
                    } else {
//                        mNum.setText("国家代码无效");
                    }

                } else {
                    if (contentString.length() == 0) {
//                        mNum.setText(beforText);
//                        mNum.setText("从列表选择");
                    } else if (contentString.length() == 1 && contentString.equals("+")) {
//                        mNum.setText("从列表选择");
                    }

                }

                if (contentSeq instanceof Spannable) {
                    Spannable spannable = (Spannable) contentSeq;
                    Selection.setSelection(spannable, contentSeq.length());
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        switch (requestCode) {
            case 12:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
//                    String countryName = bundle.getString("countryName");
//                    String countryNumber = bundle.getString("countryNumber");
                    countryNumber = bundle.getString("countryNumber");

                    mNum.setText(countryNumber);
//                    tv_countryName.setText(countryName);

                }
                break;

            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}

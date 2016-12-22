package com.ruihai.xingka.ui.login;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.ruihai.xingka.R;
import com.ruihai.xingka.api.ApiModule;
import com.ruihai.xingka.api.SMSApiModule;
import com.ruihai.xingka.api.model.SMSStatus;
import com.ruihai.xingka.api.model.XKRepo;
import com.ruihai.xingka.ui.common.UmengActivity;
import com.ruihai.xingka.ui.login.chooseCountry.CharacterParserUtil;
import com.ruihai.xingka.ui.login.chooseCountry.CountryActivity;
import com.ruihai.xingka.ui.login.chooseCountry.CountrySortModel;
import com.ruihai.xingka.ui.login.chooseCountry.GetCountryNameSort;
import com.ruihai.xingka.utils.AppUtility;
import com.ruihai.xingka.utils.Security;
import com.ruihai.xingka.widget.ProgressHUD;
import com.umeng.onlineconfig.OnlineConfigAgent;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.UserInterruptException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mob.tools.utils.R.getStringRes;

/**
 * Created by lqfang on 15/12/15.
 */
public class PhoneNumActivity extends UmengActivity {

    @BindView(R.id.et_phone)
    EditText mPhoneEditText;
    @BindView(R.id.tv_base)
    TextView mBase;
    @BindView(R.id.tv_title)
    TextView mTitle;
    @BindView(R.id.tv_num)
    TextView mNum;

    private GetCountryNameSort countryChangeUtil;

    private CharacterParserUtil characterParserUtil;

    private List<CountrySortModel> mAllCountryList;

    String beforText = null;

    private EventHandler handler;
    private String countryNumber = "+86";

    private Context mContext;

    private int smsChannel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);
        ButterKnife.bind(this);


        mContext = this;

        String smsChannelValue = OnlineConfigAgent.getInstance().getConfigParams(this, "SMSChannel");
        if (!TextUtils.isEmpty(smsChannelValue)) {
            smsChannel = Integer.parseInt(smsChannelValue);
        }

        mTitle.setText("密码重置");
        mBase.setVisibility(View.VISIBLE);

        initView();
        initCountryList();
        setListener();

        initListener();
    }

    private void initListener() {

        handler = new EventHandler() {
            public void afterEvent(final int event, final int result,
                                   final Object data) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        ProgressHUD.dismiss();
                        if (result == SMSSDK.RESULT_COMPLETE) {
                            if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                                // 请求验证码后，跳转到验证码填写页面
                                boolean smart = (Boolean) data;
                                afterVerificationCodeRequested();
                            }
                        } else {
                            if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE
                                    && data != null
                                    && (data instanceof UserInterruptException)) {
                                // 由于此处是开发者自己决定要中断发送的，因此什么都不用做
                                return;
                            }

                            int status = 0;
                            // 根据服务器返回的网络错误，给toast提示
                            try {
                                ((Throwable) data).printStackTrace();
                                Throwable throwable = (Throwable) data;

                                JSONObject object = new JSONObject(
                                        throwable.getMessage());
                                String des = object.optString("detail");
                                status = object.optInt("status");
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
                                resId = getStringRes(mContext, "smssdk_error_desc_" + status);
                            } else {
                                resId = getStringRes(mContext, "smssdk_network_error");
                            }

                            if (resId > 0) {
                                Toast.makeText(mContext, resId, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        };
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
     * Toolbar头部返回键
     *
     * @param view
     */
    @OnClick(R.id.iv_back)
    public void onBackClicked(View view) {
        finish();
    }

    /**
     * 下一步按钮监听
     *
     * @param view
     */
    @OnClick(R.id.btn_register)
    public void onRegisteredClicked(View view) {
        String phoneNum = mPhoneEditText.getText().toString().trim().replaceAll("\\s*", "");

        if (TextUtils.isEmpty(phoneNum)) {
            ProgressHUD.showInfoMessage(mContext, getString(R.string.register_please_input_phone));
            return;
        } else if (countryNumber.equals("+86") && !AppUtility.isMobilePhone(phoneNum)) {
            ProgressHUD.showInfoMessage(mContext, getString(R.string.register_please_input_right_phone));
            return;
        } else {
            showDialog(phoneNum);
        }
    }

    private void showDialog(final String phone) {
        RelativeLayout layout = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.dialog_communal, null);
        final Dialog dialog = new AlertDialog.Builder(this).create();

        dialog.setCancelable(false);
        dialog.show();
        dialog.getWindow().setContentView(layout);

        layout.findViewById(R.id.update_icon).setVisibility(View.GONE);
        TextView message = (TextView) layout.findViewById(R.id.update_content);
        message.setGravity(Gravity.CENTER);
        message.setText("我们将发送验证码短信到这个号码:\n" + countryNumber + " " + phone);

        // 4. 确定按钮
        Button btnOK = (Button) layout.findViewById(R.id.umeng_update_id_ok);
        btnOK.setText("确定");
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                ProgressHUD.showLoadingMessage(mContext, "正在发送中...", false);
                verfiyPhoneNum(phone.trim());
            }
        });

        // 5. 取消按钮
        Button btnCancel = (Button) layout.findViewById(R.id.umeng_update_id_cancel);
        btnCancel.setText("取消");
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    /**
     * 验证手机号码是否已经注册
     */
    private void verfiyPhoneNum(final String phoneNum) {
        String sCountryNum = Security.aesEncrypt(countryNumber);
        String sAccount = Security.aesEncrypt(phoneNum);
        ApiModule.apiService_1().isRegisterByPhone(sCountryNum, sAccount).enqueue(new Callback<XKRepo>() {
            @Override
            public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                XKRepo xkRepo = response.body();
                int code = xkRepo.getCode(); // 100: 未注册 200:已注册
                if (xkRepo.isSuccess()) {
                    if (code == 100) {
                        ProgressHUD.showErrorMessage(mContext, xkRepo.getMsg());
                    } else if (code == 200) {
                        if (smsChannel == 0) { // 默认SMS API
                            sendVerfiyCodeBySMSApi(phoneNum);
                        } else { // Mob SMS
                            SMSSDK.getVerificationCode(countryNumber, phoneNum);
                        }
                    }
                } else {
                    ProgressHUD.showErrorMessage(mContext, xkRepo.getMsg());
                }
            }

            @Override
            public void onFailure(Call<XKRepo> call, Throwable t) {
                ProgressHUD.showErrorMessage(mContext, t.getLocalizedMessage());
            }
        });
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


    /**
     * 请求验证码后，跳转到验证码填写页面
     */
    private void afterVerificationCodeRequested() {
        String phone = mPhoneEditText.getText().toString().trim().replaceAll("\\s*", "");
        VerifyActivity.launch(PhoneNumActivity.this, phone, countryNumber);
        ProgressHUD.dismiss();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SMSSDK.registerEventHandler(handler);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SMSSDK.unregisterEventHandler(handler);
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
                intent.setClass(PhoneNumActivity.this, CountryActivity.class);
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
        switch (requestCode) {
            case 12:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    countryNumber = bundle.getString("countryNumber");
                    mNum.setText(countryNumber);
                }
                break;

            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}

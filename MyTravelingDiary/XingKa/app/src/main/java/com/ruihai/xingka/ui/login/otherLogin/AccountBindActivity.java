package com.ruihai.xingka.ui.login.otherLogin;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.orhanobut.logger.Logger;
import com.qiniu.android.storage.UploadManager;
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
import com.ruihai.xingka.ui.login.RegisterPhoneNumActivity;
import com.ruihai.xingka.ui.login.chooseCountry.CharacterParserUtil;
import com.ruihai.xingka.ui.login.chooseCountry.CountryActivity;
import com.ruihai.xingka.ui.login.chooseCountry.CountrySortModel;
import com.ruihai.xingka.ui.login.chooseCountry.GetCountryNameSort;
import com.ruihai.xingka.utils.AppUtility;
import com.ruihai.xingka.utils.Security;
import com.ruihai.xingka.widget.ProgressHUD;
import com.ruihai.xingka.widget.SoftKeyboardHandledLinearLayout;
import com.ruihai.xingka.widget.SwitchButton;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 绑定账号
 * Created by lqfang on 16/5/21.
 */
public class AccountBindActivity extends BaseActivity{

    public static void launch(Context from, String sign, String type) {
        Intent intent = new Intent(from, AccountBindActivity.class);
        intent.putExtra("sign", sign);
        intent.putExtra("type", type);
        from.startActivity(intent);
    }

    @BindView(R.id.main_layout)
    SoftKeyboardHandledLinearLayout mainLayout;
    @BindView(R.id.et_account)
    EditText mAccountEditText;
    @BindView(R.id.et_password)
    EditText mPasswordEditText;
    @BindView(R.id.avatar_layout)
    RelativeLayout avatarLayout;
    @BindView(R.id.checked_show)
    SwitchButton mShow;
    @BindView(R.id.ll_change)
    LinearLayout mChange;
    @BindView(R.id.tv_num)
    TextView mNum;
    @BindView(R.id.tv_change)
    TextView mTextChange;
    @BindView(R.id.btn_login)
    TextView loginText;

    private String mSign;
    private String mType;

    private GetCountryNameSort countryChangeUtil;
    private CharacterParserUtil characterParserUtil;
    private List<CountrySortModel> mAllCountryList;
    private String countryNumber = "+86";
    private int flag = 1;
    String beforText = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_account);
        ButterKnife.bind(this);
        mSign = getIntent().getStringExtra("sign");
        mType = getIntent().getStringExtra("type");

        initView();
        initCountryList();
        initListener();

        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLoginBindOtherAcc(mSign, mType);
            }
        });
    }

    private void listenerSoftInput() {
        mainLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = mainLayout.getRootView().getHeight() - mainLayout.getHeight();
                if (heightDiff > 100) { // 如果高度差超过100像素，就很有可能是有软键盘...
                    avatarLayout.setVisibility(View.GONE);
                } else {
                    avatarLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    private void initListener() {
        mainLayout.setOnSoftKeyboardVisibilityChangeListener(
                new SoftKeyboardHandledLinearLayout.SoftKeyboardVisibilityChangeListener() {
                    @Override
                    public void onSoftKeyboardShow() {
                        avatarLayout.setVisibility(View.GONE);
                        listenerSoftInput();
                    }

                    @Override
                    public void onSoftKeyboardHide() {
                        avatarLayout.setVisibility(View.VISIBLE);
                        listenerSoftInput();
                    }
                });

        mShow.setOnToggleChanged(new SwitchButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean isChecked) {
                if (isChecked) {
                    //如果选中，显示密码
                    mPasswordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    //否则隐藏密码
                    mPasswordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
    }

    /**
     * 切换手机号和行咖号
     */
    @OnClick(R.id.tv_change)
    void onChange() {
        if (flag == 0) {
            mAccountEditText.setHint("请输入手机号");
            mChange.setVisibility(View.VISIBLE);
            mAccountEditText.setPadding(mChange.getWidth() + 40, 0, 6, 0);
            flag = 1;
        } else if (flag == 1) {
            mAccountEditText.setHint("请输入行咖号");
            mChange.setVisibility(View.INVISIBLE);
            mAccountEditText.setPadding(AppUtility.dip2px(20), 0, 6, 0);
            flag = 0;
        }
    }

    /**
     * 用户登录(带第三方账号绑定)
     */
    public void userLoginBindOtherAcc(String sign, String type) {
        String account = mAccountEditText.getText().toString().trim();
        final String password = mPasswordEditText.getText().toString().trim();
        String equipment = android.os.Build.MODEL;

        String sCountryNum = Security.aesEncrypt(countryNumber);
        String sAccount = Security.aesEncrypt(account);
        final String sPassword = Security.aesEncrypt(password);
        String sOS = Security.aesEncrypt("1");
        String sEquipment = Security.aesEncrypt(equipment);
        String sType = Security.aesEncrypt(type);
        String sSign = Security.aesEncrypt(sign);

        if (TextUtils.isEmpty(account)) {
            ProgressHUD.showInfoMessage(mContext, getString(R.string.login_input_account));
            return;
        } else if (TextUtils.isEmpty(password)) {
            ProgressHUD.showInfoMessage(mContext, getString(R.string.login_input_password));
            return;
        } else if (password.length() < 6 || password.length() > 16) {
            ProgressHUD.showInfoMessage(mContext, getString(R.string.login_password_error));
            return;
        }

//        ProgressHUD.showLoadingMessage(mContext, "正在登录", false);

        Call<UserRepo> call = ApiModule.apiService_1().userLoginBindOtherAcc(sCountryNum, sAccount, sPassword, sOS, sEquipment, sType, sSign);
        call.enqueue(new Callback<UserRepo>() {
            @Override
            public void onResponse(Call<UserRepo> call, Response<UserRepo> response) {
                UserRepo userRepo = response.body();
                ProgressHUD.dismiss();
                if (userRepo.isSuccess()) {
                    User user = userRepo.getUserInfo();
                    List<UserTag> userTags = userRepo.getUserTags();
                    UserCarInfo userCarInfo = userRepo.getUserCarInfo();
                    // Umeng启动账号统计
                    MobclickAgent.onProfileSignIn(String.valueOf(user.getAccount()));
                    // 保存用户基本数据到本地
                    AccountInfo.getInstance().saveAccount(user);
                    AccountInfo.getInstance().savePassWord(sPassword);
                    AccountInfo.getInstance().saveUserTags(userTags);
                    AccountInfo.getInstance().saveUserCarInfo(userCarInfo);
                    // 判断JPush Reg Id是否存在,如果存在则提交到服务器进行更新
                    String deviceToken = AppSettings.getJPushRegId();
                    if (!TextUtils.isEmpty(deviceToken)) {
                        updateDeviceToken(deviceToken);
                    } else { // 直接跳转至主页
                        ProgressHUD.dismiss();
                        loginSuccess();
                    }
//                    Log.e("TAG","账号-->"+user.getAccount()+"密码-->"+user.getPassword());
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
     * 登录成功
     */
    private void loginSuccess() {
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.putExtra("launchSplash", false);
        startActivity(intent);
//        startActivity(new Intent(AccountBindActivity.this, MainActivity.class));
        finish();
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

        mNum.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(AccountBindActivity.this, CountryActivity.class);
                startActivityForResult(intent, 12);
            }
        });

        mNum.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {beforText = s.toString();}

            @Override
            public void afterTextChanged(Editable s) {
                String contentString = mNum.getText().toString();

                CharSequence contentSeq = mNum.getText();

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
                    countryNumber = bundle.getString("countryNumber");
                    mNum.setText(countryNumber);
                    Log.e("gg", countryNumber);
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
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



    @OnClick(R.id.tv_back)
    void onBack() {
        finish();
    }

}

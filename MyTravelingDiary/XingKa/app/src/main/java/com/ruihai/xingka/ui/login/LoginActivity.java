package com.ruihai.xingka.ui.login;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
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
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.orhanobut.logger.Logger;
import com.qiniu.android.storage.UploadManager;
import com.ruihai.xingka.AppSettings;
import com.ruihai.xingka.R;
import com.ruihai.xingka.XKCache;
import com.ruihai.xingka.api.ApiModule;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.api.model.User;
import com.ruihai.xingka.api.model.UserCarInfo;
import com.ruihai.xingka.api.model.UserPreferences;
import com.ruihai.xingka.api.model.UserRepo;
import com.ruihai.xingka.api.model.UserTag;
import com.ruihai.xingka.api.model.XKRepo;
import com.ruihai.xingka.ui.MainActivity;
import com.ruihai.xingka.ui.common.UmengActivity;
import com.ruihai.xingka.ui.login.chooseCountry.CharacterParserUtil;
import com.ruihai.xingka.ui.login.chooseCountry.CountryActivity;
import com.ruihai.xingka.ui.login.chooseCountry.CountrySortModel;
import com.ruihai.xingka.ui.login.chooseCountry.GetCountryNameSort;
import com.ruihai.xingka.ui.login.otherLogin.BindActivity;
import com.ruihai.xingka.utils.AppUtility;
import com.ruihai.xingka.utils.Security;
import com.ruihai.xingka.widget.ProgressHUD;
import com.ruihai.xingka.widget.SoftKeyboardHandledLinearLayout;
import com.ruihai.xingka.widget.SwitchButton;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 登录页面
 */
public class LoginActivity extends UmengActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    public static void launch(Context from) {
        Intent intent = new Intent(from, LoginActivity.class);
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
    @BindView(R.id.bottom_layout)
    LinearLayout bottomLayout;
    @BindView(R.id.checked_show)
    SwitchButton mShow;
    @BindView(R.id.ll_change)
    LinearLayout mChange;
    @BindView(R.id.tv_num)
    TextView mNum;
    @BindView(R.id.tv_change)
    TextView mTextChange;

    private GetCountryNameSort countryChangeUtil;

    private CharacterParserUtil characterParserUtil;

    private List<CountrySortModel> mAllCountryList;

    private String countryNumber = "+86";

    private int flag = 1;

    String beforText = null;

    private UploadManager uploadManager;

    private Context mContext;

    private String uid, headimgurl, nickname, type, isNewImg;

    private UMShareAPI mShareAPI = null;

    private AbortableFuture<LoginInfo> loginRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mShareAPI = UMShareAPI.get(this);

        mContext = this;

        initView();
        initCountryList();
        setListener();

        initListener();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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
                        bottomLayout.setVisibility(View.VISIBLE);
                        listenerSoftInput();
                    }

                    @Override
                    public void onSoftKeyboardHide() {
                        avatarLayout.setVisibility(View.VISIBLE);
                        bottomLayout.setVisibility(View.VISIBLE);
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

    @OnClick(R.id.btn_register)
    public void registerClicked(View view) {
        Intent intent = new Intent();
        intent.setClass(this, RegisterPhoneNumActivity.class);
        startActivity(intent);
    }


    @OnClick(R.id.iv_forget_pwd)
    void forgetPassword() {
        startActivity(new Intent(this, PhoneNumActivity.class));
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
     * 登录操作
     *
     * @param view
     */
    @OnClick(R.id.btn_login)
    public void loginClicked(View view) {
        final String account = mAccountEditText.getText().toString().trim();
        final String password = mPasswordEditText.getText().toString().trim();
        String equipment = Build.MODEL;
        String version = Build.VERSION.RELEASE;
        String appVersion = AppUtility.getAppVersionName();
        Log.e("TAG", "---设备型号--->" + equipment + "---厂家--->" + Build.MANUFACTURER);
        if (TextUtils.isEmpty(account)) {
            ProgressHUD.showInfoMessage(mContext, getString(R.string.login_input_account));
            return;
        } else if (account.length() > 6 && flag == 0) {
            ProgressHUD.showInfoMessage(mContext, "行咖号不能超过6位");
            return;
        } else if (TextUtils.isEmpty(password)) {
            ProgressHUD.showInfoMessage(mContext, getString(R.string.login_input_password));
            return;
        } else if (password.length() < 6 || password.length() > 16) {
            ProgressHUD.showInfoMessage(mContext, getString(R.string.login_password_error));
            return;
        } else if (flag == 1 && countryNumber.equals("+86") && !AppUtility.isMobilePhone(account)) {
            ProgressHUD.showInfoMessage(mContext, getString(R.string.register_please_input_right_phone));
//            dialog(getString(R.string.register_please_input_right_phone));
            return;
        }

        ProgressHUD.showLoadingMessage(mContext, "正在登录", false);

        if (loginRequest != null) {
            loginRequest.abort();
            onLoginDone();
        }

        final String sAccount = Security.aesEncrypt(account);
//        Log.e("账号", account + "加密" + sAccount);
        final String sPassword = Security.aesEncrypt(password);
//        Log.e("密码", password);
        String sOS = Security.aesEncrypt("1");
//        Log.e("系统", sOS);
        String sEquipment = Security.aesEncrypt(equipment);
//        Log.e("设备", equipment);
        // 行咖号登录默认+86
        String sCountryNum = Security.aesEncrypt(countryNumber);
//        Log.e("国家码", countryNumber);
        String sVersion = Security.aesEncrypt(version);
        String sAppVersion = Security.aesEncrypt(appVersion);

        Call<UserRepo> call = ApiModule.apiService_1().userLogin4(sAccount, sPassword, sOS, sEquipment, sCountryNum, sVersion, sAppVersion);
        call.enqueue(new Callback<UserRepo>() {
            @Override
            public void onResponse(Call<UserRepo> call, Response<UserRepo> response) {
                UserRepo userRepo = response.body();
                String msg = userRepo.getMsg();
                if (userRepo.isSuccess()) { // 登录成功
                    login(account, sAccount);
                    User user = userRepo.getUserInfo();
                    //user.setPassword(Security.aesEncrypt(password));
                    List<UserTag> userTags = userRepo.getUserTags();
                    UserCarInfo userCarInfo = userRepo.getUserCarInfo();
                    // Umeng启动账号统计
                    MobclickAgent.onProfileSignIn(String.valueOf(user.getAccount()));
                    // 保存用户基本数据到本地
                    AccountInfo.getInstance().saveAccount(user);
                    AccountInfo.getInstance().savePassWord(sPassword);
                    AccountInfo.getInstance().saveUserTags(userTags);
                    AccountInfo.getInstance().saveUserCarInfo(userCarInfo);
                    AccountInfo.getInstance().saveIsIMReg(user.isIM());
                    // 判断JPush Reg Id是否存在,如果存在则提交到服务器进行更新
                    String deviceToken = AppSettings.getJPushRegId();
                    if (!TextUtils.isEmpty(deviceToken)) {
                        updateDeviceToken(deviceToken);
                    } else { // 直接跳转至主页
                        ProgressHUD.dismiss();
                        loginSuccess();
                    }
                } else { // 登录失败
                    ProgressHUD.showErrorMessage(mContext, msg);
                }
            }

            @Override
            public void onFailure(Call<UserRepo> call, Throwable t) {
                Log.i("TAG", "-----错误原因---->" + t.getMessage());
                ProgressHUD.showErrorMessage(mContext, getString(R.string.common_network_error));
            }
        });
    }

    /**
     * ***************************************** 云信IM 登录方法 **************************************
     */
    public void login(final String account, final String token){

        Log.e("TAG", "云信账号-->" + account + "\n" + "密码-->" + token);

        // 登录
        //111126 加密:ich0Sc0dUecKouXMzIJr8w==
        //111101 加密:5nj4pjw2KiP4EzLr40PbUA==
        //111120 加密:XCc3qj1c6o0zL3rTWQYMlQ==
        //111132     F2xdKaO1/I6Gmy+I09uSxA==
        loginRequest = NIMClient.getService(AuthService.class).login(new LoginInfo(account, token));
        loginRequest.setCallback(new RequestCallback<LoginInfo>() {
            @Override
            public void onSuccess(LoginInfo loginInfo) {
                Log.e("TAG", "云信 IM login success-->"+loginInfo.getAccount());

                Toast.makeText(mContext, "登录成功: " + loginInfo.getAccount(), Toast.LENGTH_SHORT).show();

                onLoginDone();
                XKCache.setAccount(loginInfo.getAccount());
                AccountInfo.getInstance().saveUserAccount(loginInfo.getAccount());
                AccountInfo.getInstance().saveUserToken(loginInfo.getToken());//保存账号和token到本地

                // 初始化消息提醒
                NIMClient.toggleNotification(UserPreferences.getNotificationToggle());

                // 初始化免打扰
                if (UserPreferences.getStatusConfig() == null) {
                    UserPreferences.setStatusConfig(XKCache.getNotificationConfig());
                }
                NIMClient.updateStatusBarNotificationConfig(UserPreferences.getStatusConfig());

                // 构建缓存
//                DataCacheManager.buildDataCacheAsync();

            }

            @Override
            public void onFailed(int code) {
                Log.e("TAG", "云信 IM login faile-->" + code);
                onLoginDone();
                if (code == 302 || code == 404) {
                    Toast.makeText(mContext, "帐号或密码错误", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "登录失败: " + code, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onException(Throwable exception) {
                Toast.makeText(mContext, "无效输入", Toast.LENGTH_LONG).show();
                onLoginDone();
            }
        });
    }

    public void onLoginDone() {
        loginRequest = null;
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
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("launchSplash", false);
        startActivity(intent);
//        startActivity(new Intent(LoginActivity.this,PerfectDataActivity.class));
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

    }

    private void setListener() {
        mNum.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, CountryActivity.class);
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
                    Log.e("gg", countryNumber);
//                    tv_countryName.setText(countryName);

                }
                break;

            default:
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
        mShareAPI.onActivityResult(requestCode, resultCode, data);
    }


    //------------------------------------ 第三方登录  -------------------------------------

    /**
     * 微信登录
     */
    @OnClick(R.id.iv_wechat)
    void loginForWechat() {  // 添加微信平台
        //判断是否安装微信客户端
        mShareAPI.isInstall(this, SHARE_MEDIA.WEIXIN);

        SHARE_MEDIA platform = SHARE_MEDIA.WEIXIN;
        ProgressHUD.showLoadingMessage(mContext, "正在获取用户信息", true);
        mShareAPI.doOauthVerify(this, platform, new UMAuthListener() {
            @Override
            public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> map) {
                ProgressHUD.dismiss();

                //取相关授权信息
                mShareAPI.getPlatformInfo(LoginActivity.this, platform, new UMAuthListener() {
                    @Override
                    public void onComplete(SHARE_MEDIA share_media, int status, Map<String, String> info) {
                        if (info != null) {
//                            Toast.makeText(mContext, "返回信息-->" + info, Toast.LENGTH_LONG).show();
                            uid = info.get("unionid");
                            nickname = info.get("nickname");
                            headimgurl = info.get("headimgurl");
                            isNewImg = "0";
                            type = "WX";
                            //三方账号是否已经绑定
                            IsOtherAccountRegister(uid, type, headimgurl, nickname);

                        } else {
                            ProgressHUD.showErrorMessage(mContext, "获取用户信息失败");
                        }
                    }

                    @Override
                    public void onError(SHARE_MEDIA share_media, int status, Throwable throwable) {
                        ProgressHUD.showErrorMessage(mContext, "获取用户信息失败");
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA share_media, int status) {
                    }
                });
            }

            @Override
            public void onError(SHARE_MEDIA platform, int action, Throwable t) {
                ProgressHUD.dismiss();
                ProgressHUD.showErrorMessage(mContext, "授权失败");
            }

            @Override
            public void onCancel(SHARE_MEDIA platform, int action) {
                ProgressHUD.dismiss();
                ProgressHUD.showErrorMessage(mContext, "授权取消");
            }
        });
    }

    /**
     * qq登录
     */
    @OnClick(R.id.iv_qq)
    void loginForQQ() {

        SHARE_MEDIA platform = SHARE_MEDIA.QQ;
        ProgressHUD.showLoadingMessage(mContext, "正在获取用户信息", true);
        mShareAPI.doOauthVerify(this, platform, new UMAuthListener() {
            @Override
            public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> map) {
                ProgressHUD.dismiss();
                uid = map.get("access_token"); //获取唯一标识

                mShareAPI.getPlatformInfo(LoginActivity.this, platform, new UMAuthListener() {
                    @Override
                    public void onComplete(SHARE_MEDIA share_media, int status, Map<String, String> info) {
                        if (info != null) {

                            nickname = info.get("screen_name");
                            headimgurl = info.get("profile_image_url");
                            isNewImg = "0";
                            type = "QQ";
                            //三方账号是否已经绑定
                            IsOtherAccountRegister(uid, type, headimgurl, nickname);

                        } else {
                            ProgressHUD.showErrorMessage(mContext, "获取用户信息失败");
                        }
                    }

                    @Override
                    public void onError(SHARE_MEDIA share_media, int status, Throwable throwable) {
                        ProgressHUD.showErrorMessage(mContext, "获取用户信息失败");
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA share_media, int status) {
                    }
                });
            }

            @Override
            public void onError(SHARE_MEDIA platform, int action, Throwable t) {
                ProgressHUD.dismiss();
                ProgressHUD.showErrorMessage(mContext, "授权失败");
            }

            @Override
            public void onCancel(SHARE_MEDIA platform, int action) {
                ProgressHUD.dismiss();
                ProgressHUD.showErrorMessage(mContext, "授权取消");
            }
        });
    }

    /**
     * 新浪微博登录
     */
    @OnClick(R.id.iv_weibo)
    void loginForWeibo() {
//        if(!mShareAPI.isInstall(this, SHARE_MEDIA.SINA)){
//            AppUtility.showToast("请安装微博客户端");
//            return;
//        }

        SHARE_MEDIA platform = SHARE_MEDIA.SINA;
        ProgressHUD.showLoadingMessage(mContext, "正在获取用户信息", true);
        mShareAPI.doOauthVerify(this, platform, new UMAuthListener() {
            @Override
            public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> map) {
                ProgressHUD.dismiss();
                uid = map.get("access_token"); //获取唯一标识

                mShareAPI.getPlatformInfo(LoginActivity.this, platform, new UMAuthListener() {
                    @Override
                    public void onComplete(SHARE_MEDIA share_media, int status, Map<String, String> info) {
                        if (info != null) {
                            String jsonInfo = info.get("result");
                            JSONObject jsonObject = null;
                            try {
                                jsonObject = new JSONObject(jsonInfo);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            nickname = jsonObject.optString("screen_name");
                            headimgurl = jsonObject.optString("profile_image_url");
                            isNewImg = "0";
                            type = "XL";
                            //三方账号是否已经绑定
                            IsOtherAccountRegister(uid, type, headimgurl, nickname);
                        } else {
                            ProgressHUD.showErrorMessage(mContext, "获取用户信息失败");
                        }
                    }

                    @Override
                    public void onError(SHARE_MEDIA share_media, int status, Throwable throwable) {
                        ProgressHUD.showErrorMessage(mContext, "获取用户信息失败");
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA share_media, int status) {
                    }
                });
            }

            @Override
            public void onError(SHARE_MEDIA platform, int action, Throwable t) {
                ProgressHUD.dismiss();
                ProgressHUD.showErrorMessage(mContext, "授权失败");
            }

            @Override
            public void onCancel(SHARE_MEDIA platform, int action) {
                ProgressHUD.dismiss();
                ProgressHUD.showErrorMessage(mContext, "授权取消");
            }
        });
    }

    /**
     * 三方账号是否已经绑定
     */
    public void IsOtherAccountRegister(final String uid, final String type, final String headimgurl, final String nickname) {
        String sSign = Security.aesEncrypt(uid);
        String sType = Security.aesEncrypt(type);

        Call<XKRepo> call = ApiModule.apiService_1().isOtherAccountRegister(sType, sSign);
        call.enqueue(new Callback<XKRepo>() {
            @Override
            public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                XKRepo xkRepo = response.body();
                if (xkRepo.isSuccess()) {
                    if (xkRepo.getCode() == 200) { //code=100：未绑定 code=200：已绑定
                        ProgressHUD.showLoadingMessage(mContext, "正在登录", true);
                        otherLogin(uid, type, nickname);
                    } else if (xkRepo.getCode() == 100) {
                        BindActivity.launch(mContext, uid, headimgurl, nickname, type);
                    }
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

    /**
     * 三方登录
     */
    public void otherLogin(String uid, String type, String nickname) {
        String sSign = Security.aesEncrypt(uid);
        String sImg = Security.aesEncrypt("");
        String sIsNewImg = Security.aesEncrypt("0");
        String sNick = Security.aesEncrypt(nickname);
        String sType = Security.aesEncrypt(type);

//                ProgressHUD.showLoadingMessage(mContext, "正在登录", true);
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
                    // Umeng启动账号统计
                    MobclickAgent.onProfileSignIn(String.valueOf(user.getAccount()));
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
//                        login(String.valueOf(user.getAccount()));
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

//    @Override
//    public void onStart() {
//        super.onStart();
//
//        // ATTENTION: This was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        client.connect();
//        Action viewAction = Action.newAction(
//                Action.TYPE_VIEW, // TODO: choose an action type.
//                "Login Page", // TODO: Define a title for the content shown.
//                // TODO: If you have web page content that matches this app activity's content,
//                // make sure this auto-generated web page URL is correct.
//                // Otherwise, set the URL to null.
//                Uri.parse("http://host/path"),
//                // TODO: Make sure this auto-generated app URL is correct.
//                Uri.parse("android-app://com.ruihai.xingka.ui.login/http/host/path")
//        );
//        AppIndex.AppIndexApi.start(client, viewAction);
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//
//        // ATTENTION: This was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        Action viewAction = Action.newAction(
//                Action.TYPE_VIEW, // TODO: choose an action type.
//                "Login Page", // TODO: Define a title for the content shown.
//                // TODO: If you have web page content that matches this app activity's content,
//                // make sure this auto-generated web page URL is correct.
//                // Otherwise, set the URL to null.
//                Uri.parse("http://host/path"),
//                // TODO: Make sure this auto-generated app URL is correct.
//                Uri.parse("android-app://com.ruihai.xingka.ui.login/http/host/path")
//        );
//        AppIndex.AppIndexApi.end(client, viewAction);
//        client.disconnect();
//    }
}
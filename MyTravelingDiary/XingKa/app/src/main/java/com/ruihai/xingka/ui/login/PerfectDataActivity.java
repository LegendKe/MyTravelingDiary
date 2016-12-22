package com.ruihai.xingka.ui.login;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.flyco.dialog.widget.NormalDialog;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.ruihai.xingka.AppSettings;
import com.ruihai.xingka.R;
import com.ruihai.xingka.XKApplication;
import com.ruihai.xingka.api.ApiModule;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.api.model.User;
import com.ruihai.xingka.api.model.UserAccount;
import com.ruihai.xingka.api.model.UserCarInfo;
import com.ruihai.xingka.api.model.UserRepo;
import com.ruihai.xingka.api.model.UserTag;
import com.ruihai.xingka.api.model.XKRepo;
import com.ruihai.xingka.ui.BaseActivity;
import com.ruihai.xingka.ui.MainActivity;
import com.ruihai.xingka.utils.AppUtility;
import com.ruihai.xingka.utils.QiniuHelper;
import com.ruihai.xingka.utils.Security;
import com.ruihai.xingka.widget.ProgressHUD;

import org.json.JSONObject;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bingoogolapple.badgeview.BGABadgeRadioButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 完善资料页
 * <p/>
 * Created by apple on 15/9/17.
 */
public class PerfectDataActivity extends BaseActivity {

    //    @BindView(R.id.btn_add_avatar)
//    IconicFontTextView mAddAvatar;
    @BindView(R.id.et_nickname)
    EditText mNickEditText;
    @BindView(R.id.sdv_avatar)
    SimpleDraweeView mAvatarView;
    @BindView(R.id.tv_sex)
    TextView mSexTextView;
    @BindView(R.id.rl_logo)
    RelativeLayout mRelativeLayout;
    @BindView(R.id.iv_boy)
//    CheckBox mBoy;
     BGABadgeRadioButton mBoy;
    @BindView(R.id.iv_girl)
//    CheckBox mGirl;
    BGABadgeRadioButton mGirl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prefect_info);
        ButterKnife.bind(this);


//        //checkbox监听
//        mBoy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    mGirl.setChecked(false);
//                    //选中后不可再点击
//                    mBoy.setClickable(false);
//
//                } else {
//                    mGirl.setChecked(true);
//                    mBoy.setClickable(true);
//                }
//
//            }
//        });
//        mGirl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    mBoy.setChecked(false);
//                    //选择后不可再点击
//                    mGirl.setClickable(false);
//
//                } else {
//                    mBoy.setChecked(true);
//                    mGirl.setClickable(true);
//                }
//
//            }
//        });
    }

    @OnClick(R.id.ib_back)
    void onBack() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @OnClick(R.id.iv_boy)
    void chooseBoy() {
        currentUser.setSex(1);
        mSexTextView.setText("选择性别");
        mRelativeLayout.setBackgroundColor(getResources().getColor(R.color.blue_man));
    }

    @OnClick(R.id.iv_girl)
    void chooseGirl() {
        currentUser.setSex(2);
        mSexTextView.setText("选择性别");
        mRelativeLayout.setBackgroundColor(getResources().getColor(R.color.orange_woman));
    }

    @OnClick(R.id.btn_skip)
    void skip() {
//        startActivity(new Intent(this, MainActivity.class));
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("launchSplash", false);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.btn_complete)
    void perfectDataBtn() {
        String nickname = mNickEditText.getText().toString().trim();
        if (TextUtils.isEmpty(nickname)) {
            nickDialog();
            return;
        } else if (nickname.length() > 10) {
            nickCheckDialog();
            return;
        } else if (!AppUtility.isNickNameOk(nickname)) {
            nickCheckDialog();
            return;
        } else {
            currentUser.setNickname(nickname);
        }


        if (currentUser.getSex() == 1 || currentUser.getSex() == 0) {
            sexDialog(currentUser.getSex());
            return;
        } else if (currentUser.getSex() == 2) {
            sexDialog(currentUser.getSex());
            return;
        } else {
//            currentUser.setSex(0);
            editData();
        }

    }

    private void nickDialog() {
        final NormalDialog dialog = new NormalDialog(mContext);
        dialog.isTitleShow(false)
                .bgColor(Color.parseColor("#ffffff"))
                .cornerRadius(5)
                .content("请输入您的昵称")
                .btnNum(1)
                .btnText("确定")
                .contentGravity(Gravity.CENTER)
                .contentTextColor(Color.parseColor("#33333d"))
                .dividerColor(Color.parseColor("#dcdce4"))
                .btnTextSize(15.5f, 15.5f)
                .btnTextColor(Color.parseColor("#ff2814"), Color.parseColor("#0077fe"))
                .widthScale(0.85f)
                .show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnBtnClickL(new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                dialog.dismiss();
            }
        });

    }

    private void nickCheckDialog() {
        final NormalDialog dialog = new NormalDialog(mContext);
        dialog.isTitleShow(false)
                .bgColor(Color.parseColor("#ffffff"))
                .cornerRadius(5)
                .content("昵称10个字符以内，可由中英文，数字、“－”、“_”组成")
                .btnNum(1)
                .btnText("确定")
                .contentGravity(Gravity.CENTER)
                .contentTextColor(Color.parseColor("#33333d"))
                .dividerColor(Color.parseColor("#dcdce4"))
                .btnTextSize(15.5f, 15.5f)
                .btnTextColor(Color.parseColor("#ff2814"), Color.parseColor("#0077fe"))
                .widthScale(0.85f)
                .show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnBtnClickL(new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                dialog.dismiss();
            }
        });
    }

    //选择性别弹出框
    private void sexDialog(final int sex) {
        String mSex = "";
        if (sex == 1 || sex == 0) {
            mSex = "您选择的性别是男";
        } else if (sex == 2) {
            mSex = "您选择的性别是女";
        }

        // 1. 布局文件转换为View对象
        LayoutInflater inflater = LayoutInflater.from(mContext);
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.dialog_choose_sex, null);
        final Dialog dialog = new AlertDialog.Builder(mContext).create();
        dialog.setCancelable(false);
        dialog.show();
        dialog.getWindow().setContentView(layout);
        //2.标题
        TextView dialog_title = (TextView) layout.findViewById(R.id.tv_title);
//        dialog_title.setTextColor(getResources().getColor(R.color.black));
        dialog_title.setVisibility(View.VISIBLE);
        dialog_title.setText(mSex);
        // 3. 消息内容
        //显示内容
        TextView dialog_msg = (TextView) layout.findViewById(R.id.umeng_update_content);
        dialog_msg.setText("确认性别后不能再修改");
//        dialog_msg.setVisibility(View.GONE);

        // 4. 确定按钮
        Button btnOK = (Button) layout.findViewById(R.id.umeng_update_id_ok);
        btnOK.setText("确定");
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editData();
                dialog.dismiss();
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

    @OnClick(R.id.sdv_avatar)
    void addAvatar() {
        showEditAvatarDialog();
    }

    /**
     * 显示修改头像选择对话框
     */
    private void showEditAvatarDialog() {
        final String[] actionItems = {"拍照", "从手机相册选择"};
        final ActionSheetDialog dialog = new ActionSheetDialog(this, actionItems, null);
        dialog.isTitleShow(false).show();

        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    camera();
                } else if (position == 1) {
                    photo();
                }
                dialog.dismiss();
            }
        });
    }

    private void editData() {
        ProgressHUD.showLoadingMessage(mContext, "正在提交...", false);

        UserAccount account = new UserAccount();
        account.userNick = currentUser.getNickname();
        account.userSex = currentUser.getSex();
        Gson gson = new Gson();
        String jsonValue = gson.toJson(account);
        Logger.d(jsonValue);
        Call<UserRepo> call = ApiModule.apiService().editUserInfoMore(Security.aesEncrypt(currentUser.getAccount() + ""), Security.aesEncrypt(jsonValue));
        call.enqueue(new Callback<UserRepo>() {
            @Override
            public void onResponse(Call<UserRepo> call, Response<UserRepo> response) {
                UserRepo userRepo = response.body();
                String msg = userRepo.getMsg();
                if (userRepo.isSuccess()) { // 修改资料成功
                    User user = userRepo.getUserInfo();
//                            List<User> user = userRepo.getUserInfo();
                    List<UserTag> userTags = userRepo.getUserTags();
                    UserCarInfo userCarInfo = userRepo.getUserCarInfo();

                    // 保存用户基本数据到本地
                    AccountInfo.getInstance().saveAccount(user);
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
                } else { // 修改资料失败
                    ProgressHUD.showErrorMessage(mContext, msg);
                }
            }

            @Override
            public void onFailure(Call<UserRepo> call, Throwable t) {
                ProgressHUD.showErrorMessage(mContext, t.getLocalizedMessage());
            }

        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_REQUEST_PHOTO) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    fileUri = data.getData();
                }
                fileCropUri = getOutputMediaFileUri();
                cropImageUri(fileUri, fileCropUri, 560, 560, RESULT_REQUEST_PHOTO_CROP);
            }
        } else if (requestCode == RESULT_REQUEST_PHOTO_CROP) {
            if (resultCode == Activity.RESULT_OK) {
                String filePath = AppUtility.getFilePath(this, fileCropUri);
                File avatarFile = new File(filePath);
                Logger.d("filePath:" + filePath);
                // Todo: 将头像文件上传到指定服务器上
                updateAvatarToQiniu(avatarFile);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateAvatarToQiniu(File avatarFile) {
        UploadManager uploadManager = new UploadManager();
        // 指定七牛服务上的文件名
        final String genKey = AppUtility.generateUUID();
        Logger.d(genKey);
        String token = QiniuHelper.getUpToken();
        uploadManager.put(avatarFile, genKey, token,
                new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo info, JSONObject res) {
                        //  res 包含hash、key等信息，具体字段取决于上传策略的设置。
                        Logger.i("qiniu", key + ",\r\n " + info + ",\r\n " + res);
                        updateAvatarKey(genKey);
                    }
                }, null);
    }

    private void updateAvatarKey(final String key) {
        String account = Security.aesEncrypt(String.valueOf(currentUser.getAccount()));
        String type = Security.aesEncrypt("img");
        String value = Security.aesEncrypt(key);
        Call<XKRepo> call = ApiModule.apiService().editUserInfo(account, type, value);
        call.enqueue(new Callback<XKRepo>() {
            @Override
            public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                XKRepo xkRepo = response.body();
                Logger.i(xkRepo.getMsg());
                if (xkRepo.isSuccess()) {
                    // 更新本地头像信息
                    currentUser.setAvatar(key);
                    AccountInfo.getInstance().saveAccount(currentUser);

                    String avatarUrl = QiniuHelper.getThumbnail200Url(key);
                    mAvatarView.setImageURI(Uri.parse(avatarUrl));
//                            mAddAvatar.setVisibility(View.GONE);
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
     * @param regId JPush Registration Id
     * @功能描述 : 更新设备令牌值
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
        XKApplication.getInstance().exit();
//        startActivity(new Intent(mContext, MainActivity.class));
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("launchSplash", false);
        startActivity(intent);
        finish();
    }
}

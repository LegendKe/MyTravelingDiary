package com.ruihai.xingka.ui.mine;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyco.animation.BaseAnimatorSet;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.ruihai.iconicfontengine.widget.IconicFontTextView;
import com.ruihai.xingka.Constant;
import com.ruihai.xingka.R;
import com.ruihai.xingka.api.ApiModule;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.api.model.CarBrandRepo.CarBrand;
import com.ruihai.xingka.api.model.User;
import com.ruihai.xingka.api.model.UserAccount;
import com.ruihai.xingka.api.model.UserCarInfo;
import com.ruihai.xingka.api.model.UserLabel;
import com.ruihai.xingka.api.model.UserRepo;
import com.ruihai.xingka.api.model.UserTag;
import com.ruihai.xingka.api.model.XKRepo;
import com.ruihai.xingka.ui.BaseActivity;
import com.ruihai.xingka.ui.caption.ChooseCarBrandActivity;
import com.ruihai.xingka.ui.mine.adapter.EditDataThirdAdapter;
import com.ruihai.xingka.ui.mine.adapter.EditDateFirstAdapter;
import com.ruihai.xingka.ui.mine.adapter.EditDateSecondAdapter;
import com.ruihai.xingka.utils.AppUtility;
import com.ruihai.xingka.utils.Security;
import com.ruihai.xingka.widget.MyNickDialog;
import com.ruihai.xingka.widget.MyTalkDialog;
import com.ruihai.xingka.widget.ProgressHUD;
import com.ruihai.xingka.widget.SetUserLabelDialog;
import com.ruihai.xingka.widget.SexDialog;
import com.ruihai.xingka.widget.wheel.OptionsPopupWindow;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * 编辑资料
 * Created by apple on 15/8/18.
 */
public class EditUserDataActivity extends BaseActivity {

    public static void launch(Activity from) {
        Intent intent = new Intent(from, EditUserDataActivity.class);
        from.startActivity(intent);
    }

    public static final int RESULT_REQUEST_CHOOSE_CARBARND = 0x1001;

    String[] data_first = {"修改头像", "我的说说"};
    String[] data_second = {"我的昵称", "性别", "手机号码", "我的车型"};
    String[] data_third = {"我的标签", "我的所在地", "我的二维码"};

    private static final int first_part = 1;
    private static final int second_part = 2;
    private static final int third_part = 3;

    @BindView(R.id.lv_first)
    ListView mLvFirst;
    @BindView(R.id.lv_second)
    ListView mLvSecond;
    @BindView(R.id.lv_third)
    ListView mLvThird;
    @BindView(R.id.root_layout)
    RelativeLayout rootLayout;
    @BindView(R.id.tv_title)
    TextView mTitle;
    @BindView(R.id.tv_right)
    IconicFontTextView mRight;

    EditDateFirstAdapter adapter_first;
    EditDateSecondAdapter adapter_second;
    EditDataThirdAdapter adapter_third;
    int getSex;
    boolean isChangeEdit = false;

    private String myTalk;
    private String myNick;

    private BaseAnimatorSet bas_in;
    private BaseAnimatorSet bas_out;

    private UploadManager uploadManager;

    private UserAccount account;
    private List<UserLabel> userLabels = new ArrayList<>();
    private UserCarInfo mUserCarInfo;
    private String userCarImage = "";

    private OptionsPopupWindow pwOptions;
    private ArrayList<String> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editdata);
        ButterKnife.bind(this);
        mTitle.setText(R.string.mine_editdata);
        mRight.setVisibility(View.GONE);

        currentUser = AccountInfo.getInstance().loadAccount();
        List<UserTag> userTags = AccountInfo.getInstance().getUserTags();
        List<String> tagNames = new ArrayList<>();
        if (userTags != null) {
            UserLabel userLabel = null;
            for (UserTag userTag : userTags) {
                userLabel = new UserLabel();
                userLabel.tagType = 1;
                userLabel.tagName = userTag.getName();
                userLabels.add(userLabel);

                tagNames.add(userTag.getName());
            }
        }
        //获取车品牌图标
        mUserCarInfo = AccountInfo.getInstance().getUserCarInfo();
        if (mUserCarInfo != null) {
            userCarImage = mUserCarInfo.getImgkey();
        }

        adapter_first = new EditDateFirstAdapter(this, data_first, first_part);
        adapter_first.setAvatar(currentUser.getAvatar());
        adapter_first.setTalk(currentUser.getTalk());

        adapter_second = new EditDateSecondAdapter(this, data_second, second_part);
        adapter_second.setSex(currentUser.getSex());
        adapter_second.setNickname(currentUser.getNickname());
        adapter_second.setCarBrandImage(userCarImage);

        adapter_third = new EditDataThirdAdapter(this, data_third, third_part);
        adapter_third.setAddress(currentUser.getAddress());
        adapter_third.setUserTags(TextUtils.join(",", tagNames));

        mLvFirst.setAdapter(adapter_first);
        adapter_first.notifyDataSetChanged();

        mLvSecond.setAdapter(adapter_second);
        adapter_second.notifyDataSetChanged();

        mLvThird.setAdapter(adapter_third);
        adapter_third.notifyDataSetChanged();

        initViews();
    }

    private void initViews() {
        //选项选择器
        pwOptions = new OptionsPopupWindow(this, 2);
        options1Items = new ArrayList<>(Arrays.asList(Constant.provinces));

        ArrayList<String> citys = new ArrayList<>();
        for (int i = 0; i < options1Items.size(); i++) {
            citys = new ArrayList<>(Arrays.asList(Constant.cities[i]));
            options2Items.add(citys);
        }
        // 2级联动效果
        pwOptions.setPicker(options1Items, options2Items, true);

        //监听确定选择按钮
        pwOptions.setOnoptionsSelectListener(new OptionsPopupWindow.OnOptionsSelectListener() {

            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                // 返回的分别是三个级别的选中位置
                String address = options1Items.get(options1)
                        + "," + options2Items.get(options1).get(option2);
                currentUser.setAddress(address);
                adapter_third.setAddress(currentUser.getAddress());
                adapter_third.notifyDataSetChanged();
                changeData();
            }
        });

        pwOptions.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1f);
            }
        });
    }

    @OnClick(R.id.tv_back)
    void onBack(View view) {
        finish();
    }

    @OnClick(R.id.btn_editdata_complete)
    void completeBtn(View view) {
        finish();
    }

    @OnItemClick(R.id.lv_first)
    void onFirstItemClik(int position) {
        if (position == 0) {        //我的头像
            showEditAvatarDialog();
        } else if (position == 1) {       //我的说说
            myTalk = currentUser.getTalk();
            MyTalkDialog talkDialog = new MyTalkDialog(this, myTalk, new MyTalkDialog.OnMyTalkDialogListener() {
                @Override
                public void myTalk(String talk) {
                    isChangeEdit = true;
                    currentUser.setTalk(talk);
                }
            });
            talkDialog.setHint("发表说说(最多40字)");
            talkDialog.showAnim(bas_in).show();
        }
    }

    @OnItemClick(R.id.lv_second)
    void onSecondItemClik(int position) {
        if (position == 0) { // 我的昵称
            adapter_second.notifyDataSetChanged();
            myNick = currentUser.getNickname();
            MyNickDialog talkDialog = new MyNickDialog(this, myNick, new MyNickDialog.OnMyNickDialogListener() {
                @Override
                public void nickName(String nick) {
                    isChangeEdit = true;
                    currentUser.setNickname(nick);
                }
            });
            talkDialog.setHint("我的昵称(最多10字)");
            talkDialog.showAnim(bas_in).show();
        } else if (position == 1) { // 性别
            int sex = currentUser.getSex();
            Logger.e("-->" + sex);
            if (sex == 0) {
                SexDialog sexDialog = new SexDialog(this, R.style.CommonDialog, currentUser.getSex(), currentUser,
                        new SexDialog.OnSexDialogListener() {
                            @Override
                            public void sex(int sex) {
                                if (sex == 1) {
                                    getSex = 1;
                                    currentUser.setSex(getSex);
                                    isChangeEdit = true;
                                    AccountInfo.getInstance().saveAccount(currentUser);
//                                    adapter_second.setSex(getSex);
//                                    adapter_second.notifyDataSetChanged();
                                } else if (sex == 2) {
                                    getSex = 2;
                                    currentUser.setSex(getSex);
                                    isChangeEdit = true;
                                    AccountInfo.getInstance().saveAccount(currentUser);
//                                    adapter_second.setSex(getSex);
//                                    adapter_second.notifyDataSetChanged();
                                }
                            }
                        });
                sexDialog.show();

            } else {
//                getSex = 0;
//                currentUser.setSex(getSex);
//                isChangeEdit = true;
//                AccountInfo.getInstance().saveAccount(currentUser);
                ProgressHUD.showInfoMessage(mContext, "您已选择性别,无法进行修改");
            }
        } else if (position == 2) {   // 手机号码
            startActivity(new Intent(this, ChangeMobileActivity.class));
        } else {  // 我的车品牌
            Intent intent = new Intent(this, ChooseCarBrandActivity.class);
            startActivityForResult(intent, RESULT_REQUEST_CHOOSE_CARBARND);
        }
    }

    @OnItemClick(R.id.lv_third)
    void onThirdItemClik(int position) {
        if (position == 0) { // 我的标签
            SetUserLabelDialog setUserLabelDialog = new SetUserLabelDialog(this, new SetUserLabelDialog.OnSetLabelDialogListener() {
                @Override
                public void selectedLabels(String selectedTag) {
                    Logger.e(selectedTag);
                    adapter_third.setUserTags(selectedTag);
                    adapter_third.notifyDataSetChanged();
                    String[] tagNames = selectedTag.split(",");
                    UserLabel userLabel = null;
                    userLabels.clear();
                    for (String tag : tagNames) {
                        userLabel = new UserLabel();
                        userLabel.tagName = tag;
                        userLabel.tagType = 1;
                        userLabels.add(userLabel);
                    }
                    changeData();
                }
            });
            setUserLabelDialog.show();
        } else if (position == 1) {  //我的所在地
//            LocationDialogFragment locationDialogFragment = new LocationDialogFragment(this, new LocationDialogFragment.OnLocationDialogListener() {
//                @Override
//                public void location(String province, String city) {
//                    String address = province + "," + city;
//                    currentUser.setAddress(address);
//                    isChangeEdit = true;
//                }
//            });
//            locationDialogFragment.show(getSupportFragmentManager(), "dialog");

            // 设置默认选中值
            String address = currentUser.getAddress();
            int provincePosition = 0;
            int cityPosition = 0;

            if (!TextUtils.isEmpty(address)) {
                // 截取省市
                String[] pcStr = address.split(",");
                if (pcStr.length > 1) {
                    // 遍历省市数组获取数组下标
                    for (int i = 0; i < options1Items.size(); i++) {
                        if (options1Items.get(i).contains(pcStr[0])) {
                            provincePosition = i;
                            for (int j = 0; j < options2Items.get(i).size(); j++) {
                                if (options2Items.get(i).get(j).contains(pcStr[1])) {
                                    cityPosition = j;
                                }
                            }
                        }
                    }
                }
            }
            pwOptions.setSelectOptions(provincePosition, cityPosition);
            backgroundAlpha(0.8f); // 设置背景颜色变暗
            pwOptions.showAtLocation(rootLayout, Gravity.BOTTOM, 0, 0);
        } else { // 我的二维码
            Intent intent = new Intent();
            intent.setClass(this, PictureDisplayActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("show", 2);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    /**
     * 显示修改头像选择对话框
     */
    private void showEditAvatarDialog() {
        final String[] actionItems = {"拍照", "从手机相册选择"};
        final ActionSheetDialog dialog = new ActionSheetDialog(this, actionItems, null);
        dialog.isTitleShow(false).show();
        //弹出框背景色为白色
        dialog.lvBgColor(Color.parseColor("#ffffff"));

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
//                Bundle bundle = data.getExtras();
//                String cropImagePath = bundle.getString("cropImagePath");
                File avatarFile = new File(filePath);
                Logger.d("filePath:" + filePath);
                // Todo: 将头像文件上传到指定服务器上
                updateAvatarToQiniu(avatarFile);
            }
        } else if (requestCode == RESULT_REQUEST_CHOOSE_CARBARND) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    CarBrand carBrand = (CarBrand) data.getSerializableExtra("cardBrand");
                    if (carBrand != null) {
                        updateCardBrand(carBrand);
                    } else {
                        adapter_second.setCarBrandImage("");
                        adapter_second.notifyDataSetChanged();
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 修改车品牌
     *
     * @param carBrand
     */
    private void updateCardBrand(final CarBrand carBrand) {
        adapter_second.setCarBrandImage(carBrand.getImage());
        adapter_second.notifyDataSetChanged();
        Logger.d(carBrand.getName() + " :" + carBrand.getImage());
        String sAccount = Security.aesEncrypt(String.valueOf(currentUser.getAccount()));
        String sType = Security.aesEncrypt("brand");
        String sValue = Security.aesEncrypt(String.valueOf(carBrand.getId()));
        ApiModule.apiService().editUserInfo(sAccount, sType, sValue).enqueue(new Callback<XKRepo>() {
            @Override
            public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                XKRepo xkRepo = response.body();
                if (xkRepo.isSuccess()) {
                    Logger.d("修改成功");
                    if (mUserCarInfo == null)
                        mUserCarInfo = new UserCarInfo();
                    mUserCarInfo.setImgkey(carBrand.getImage());
                    mUserCarInfo.setName(carBrand.getName());
                    AccountInfo.getInstance().saveUserCarInfo(mUserCarInfo);

                    adapter_second.setCarBrandImage(carBrand.getImage());
                    adapter_second.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<XKRepo> call, Throwable t) {
                ProgressHUD.showErrorMessage(mContext, getString(R.string.common_network_error));
            }

        });
    }

    private void updateAvatarToQiniu(final File avatarFile) {
        ProgressHUD.showLoadingMessage(mContext, "正在更新", false);
        String randomStr = Security.aesEncrypt("android");
        ApiModule.apiService().getQiniuToken(randomStr).enqueue(new Callback<XKRepo>() {
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

        String uploadFilKey = AppUtility.generateUUID();
        this.uploadManager.put(avatarFile, uploadFilKey, uploadToken, new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                if (info.isOK()) {
                    String fileKey = response.optString("key");
                    Logger.d(fileKey);
                    updateAvatarKey(fileKey);
                }
            }
        }, null);
    }

    private void updateAvatarKey(final String key) {
        String account = Security.aesEncrypt(String.valueOf(currentUser.getAccount()));
        String type = Security.aesEncrypt("img");
        String value = Security.aesEncrypt(key);
        ApiModule.apiService().editUserInfo(account,
                type, value).enqueue(new Callback<XKRepo>() {
            @Override
            public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                XKRepo xkRepo = response.body();
                Logger.i(xkRepo.getMsg());
                ProgressHUD.dismiss();
                if (xkRepo.isSuccess()) {
                    ProgressHUD.showSuccessMessage(mContext, "已完成头像更新");
                    // 更新本地头像信息
                    currentUser.setAvatar(key);
                    Logger.d("avatar key: " + key);
                    AccountInfo.getInstance().saveAccount(currentUser);

                    adapter_first.setAvatar(key);
                    adapter_first.notifyDataSetChanged();
                } else {
                    ProgressHUD.showSuccessMessage(mContext, xkRepo.getMsg());
                }
            }

            @Override
            public void onFailure(Call<XKRepo> call, Throwable t) {
                ProgressHUD.showErrorMessage(mContext, getString(R.string.common_network_error));
            }
        });
    }

    /*
    * 遍历标签id得到标签名
    * */
    public String fromListToString(List<UserTag> userTags) {
        List<String> tags = new ArrayList<>();
        for (UserTag tag : userTags) {
            tags.add(tag.getName());
        }
        return TextUtils.join(",", tags);
    }

    /**
     * 修改资料(单个)
     */
    public void changeUserInfo(String type, final String value) {
        adapter_second.notifyDataSetChanged();
        String sAccount = Security.aesEncrypt(String.valueOf(currentUser.getAccount()));
        String sType = Security.aesEncrypt(type);
        String sValue = Security.aesEncrypt(value);

        ApiModule.apiService().editUserInfo(sAccount, sType, sValue).enqueue(new Callback<XKRepo>() {
            @Override
            public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                XKRepo xkRepo = response.body();
                String msg = xkRepo.getMsg();
                if (xkRepo.isSuccess()) {
                    adapter_second.setSex(Integer.parseInt(value));
                    adapter_second.notifyDataSetChanged();
                    ProgressHUD.showSuccessMessage(mContext, msg);
                } else {
                    ProgressHUD.showErrorMessage(mContext, msg);
                }
            }

            @Override
            public void onFailure(Call<XKRepo> call, Throwable t) {
                ProgressHUD.showErrorMessage(mContext, getString(R.string.common_network_error));
            }
        });

    }


    /**
     * 修改资料(批量)
     */
    public void changeData() {
        account = new UserAccount();
        account.userTalk = currentUser.getTalk();
        account.userNick = currentUser.getNickname();
        account.userSex = currentUser.getSex();
        account.userAdress = currentUser.getAddress();
        account.userTag.addAll(userLabels);

        Gson gson = new Gson();
        String jsonValue = gson.toJson(account);

        Logger.d(jsonValue);

        ApiModule.apiService().editUserInfoMore(Security.aesEncrypt(String.valueOf(currentUser.getAccount())),
                Security.aesEncrypt(jsonValue)).enqueue(new Callback<UserRepo>() {
            @Override
            public void onResponse(Call<UserRepo> call, Response<UserRepo> response) {
                UserRepo userRepo = response.body();
                String msg = userRepo.getMsg();
                if (userRepo.isSuccess()) { // 修改资料成功
                    isChangeEdit = false;
                    User user = userRepo.getUserInfo();
                    List<UserTag> useTags = userRepo.getUserTags();
                    AccountInfo.getInstance().saveUserTags(useTags);
                    AccountInfo.getInstance().saveAccount(user);

                    adapter_first.setTalk(user.getTalk());
                    adapter_first.notifyDataSetChanged();

                    adapter_second.setSex(user.getSex());
                    adapter_second.setNickname(user.getNickname());
                    adapter_second.notifyDataSetChanged();

                    adapter_third.setAddress(user.getAddress());
                    if (useTags != null) {
                        adapter_third.setUserTags(fromListToString(useTags));
                        adapter_third.notifyDataSetChanged();
                    }
                } else { // 修改资料失败
                    ProgressHUD.showErrorMessage(mContext, msg);
                }
            }

            @Override
            public void onFailure(Call<UserRepo> call, Throwable t) {
                ProgressHUD.showErrorMessage(mContext, getString(R.string.common_network_error));
            }
        });
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; // 0.0-1.0
        getWindow().setAttributes(lp);
    }

    @Override
    public void onBackPressed() {
        if (pwOptions != null && pwOptions.isShowing()) {
            pwOptions.dismiss();
            return;
        }
        super.onBackPressed();
    }

}



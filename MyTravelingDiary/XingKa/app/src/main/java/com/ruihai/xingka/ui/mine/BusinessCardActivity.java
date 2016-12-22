package com.ruihai.xingka.ui.mine;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyco.animation.BaseAnimatorSet;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.flyco.dialog.widget.NormalDialog;
import com.orhanobut.logger.Logger;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.ruihai.xingka.R;
import com.ruihai.xingka.api.ApiModule;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.api.model.User;
import com.ruihai.xingka.api.model.UserCard;
import com.ruihai.xingka.api.model.UserCounts;
import com.ruihai.xingka.api.model.XKRepo;
import com.ruihai.xingka.ui.BaseActivity;
import com.ruihai.xingka.ui.mine.fragment.ViewAvatarFragment;
import com.ruihai.xingka.utils.AppUtility;
import com.ruihai.xingka.utils.QiniuHelper;
import com.ruihai.xingka.utils.Security;
import com.ruihai.xingka.widget.ProgressHUD;

import org.json.JSONObject;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * 用户名片
 * Created by apple on 15/8/17.
 */
public class BusinessCardActivity extends BaseActivity implements View.OnClickListener {
    private ActionSheetDialog dialog;

    public static void launch(Activity from, String account, UserCard userCard) {
        Intent intent = new Intent(from, BusinessCardActivity.class);
        intent.putExtra("account", account);
        intent.putExtra("USERCARD", userCard);
        from.startActivity(intent);
    }

    @BindView(R.id.iv_card_more)
    ImageView rightMore;//右上角更多操作按钮
    @BindView(R.id.iv_card_qr_code)
    ImageView scanCode;//扫描二维码按钮
    @BindView(R.id.tv_username)
    TextView mUserName;
    @BindView(R.id.tv_xingka_number)
    TextView mXingkaNum;
    @BindView(R.id.tv_note)
    TextView mNote;
    @BindView(R.id.tv_position)
    TextView mPosition;
    @BindView(R.id.tv_praise_number)
    TextView mPraiseNum;
    @BindView(R.id.tv_attention_number)
    TextView mAttentionNum;
    @BindView(R.id.tv_fans_number)
    TextView mFansNum;

    @BindView(R.id.sdv_avatar)
    ImageView mAvatarView;
    @BindView(R.id.iv_sex)
    ImageView mSex;
    @BindView(R.id.ll_location)
    LinearLayout mLocationLayout;

    @BindView(R.id.car_brand_layout)
    LinearLayout mCarBrandLayout;
    @BindView(R.id.sdv_car_brand)
    ImageView mCarBrandView;
    @BindView(R.id.tv_addfriend)
    TextView mAttentionFriend;
    @BindView(R.id.card_layout)
    RelativeLayout cardLayout;

    private final String DEFAULT_MYTALK = "有点害羞，不知道说什么O(∩_∩)O~";
    private UserCard mUserCard;//当前名片数据类

    private BaseAnimatorSet bas_in;

    private String mUserAccount;//当前名片的行咖号
    private User mCurrentUser;
    private String avatar;//当前名片头像
    private int sex;
    private String nick;
    private String address;
    private String remark;
    private int nexus;  //被查看人是查看人的什么关系
    private String bgImg;//名片页面背景图片

    private String mCarIcon;//车型图标
    private String mCarName;//车型名称

    private UploadManager uploadManager;
    private String myAccount;//我的行咖号
    private boolean isMine;//判断是不是我的名片
    //private AsyncImageLoader asyncImageLoader;//图片加载工具类

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_card);
        ButterKnife.bind(this);
        mCurrentUser = AccountInfo.getInstance().loadAccount();

        if (savedInstanceState == null && getIntent() != null) {
            mUserAccount = getIntent().getStringExtra("account");
        } else {
            if (savedInstanceState != null) {
                mUserAccount = savedInstanceState.getString("account");
            }
        }
        //获取我的行咖号
        myAccount = String.valueOf(AccountInfo.getInstance().loadAccount().getAccount());
        if (!myAccount.equals(mUserAccount)) {//查看好友名片
            isMine = false;
            scanCode.setVisibility(View.GONE);
        } else {
            isMine = true;
        }
        //判断跳转的时候是否带了当前名片页的所有数据过来
        mUserCard = (UserCard) getIntent().getSerializableExtra("USERCARD");
        if (mUserCard != null) {
            initViews(mUserCard);
        } else {
            loadData();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isMine) {
            loadData();
        }
    }

    // 获取名片的信息
    private void loadData() {
        ProgressHUD.showLoadingMessage(mContext, "跪着干脆面，为你加载中……", false);
        String sMyAccount = Security.aesEncrypt(String.valueOf(mCurrentUser.getAccount()));
        String sAccount = Security.aesEncrypt(mUserAccount);
        ApiModule.apiService().getUserBusinessCard(sMyAccount, sAccount).enqueue(new Callback<UserCard>() {
            @Override
            public void onResponse(Call<UserCard> call, Response<UserCard> response) {
                UserCard userCard = response.body();
                ProgressHUD.dismiss();
                if (userCard.isSuccess()) {
                    mUserCard = userCard;
                    initViews(userCard);
                } else {
                    ProgressHUD.showInfoMessage(mContext, userCard.getMsg());
                }
            }

            @Override
            public void onFailure(Call<UserCard> call, Throwable t) {
                ProgressHUD.showErrorMessage(mContext, getString(R.string.common_network_error));
            }

        });
    }

    public void initViews(final UserCard userCard) {
        //获取到当前用户的头像,性别,昵称,地址
        avatar = userCard.getAvatar();
        sex = userCard.getSex();
        nick = userCard.getNick();
        address = userCard.getAddress();
        remark = userCard.getRemark();
        nexus = userCard.getNexus();//被查看人与自己的关系
        bgImg = userCard.getBgImg();
        Log.i("TAG", "----名片背景-->" + bgImg);
//        if (!"00000000-0000-0000-0000-000000000000".equals(bgImg)) {
//            new AsyncImageLoader().loadDrawable(QiniuHelper.getOriginalWithKey(bgImg), new AsyncImageLoader.ImageCallback() {
//                @Override
//                public void imageLoaded(Drawable imageDrawable, String imageUrl) {
//                    cardLayout.setBackground(imageDrawable);
//                }
//            });
//        }
//        if (!TextUtils.isEmpty(bgImg)) {
//            new AsyncImageLoader().loadDrawable(QiniuHelper.getOriginalWithKey(bgImg), new AsyncImageLoader.ImageCallback() {
//                @Override
//                public void imageLoaded(Drawable imageDrawable, String imageUrl) {
//                    cardLayout.setBackground(imageDrawable);
//                }
//            });
//
//
//        }

        mXingkaNum.setText(String.valueOf(userCard.getAccount()));
        // 用户头像
        Uri avatarUri = Uri.parse(QiniuHelper.getThumbnail200Url(userCard.getAvatar()));
        mAvatarView.setImageURI(avatarUri);
        mAvatarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMine) {
                    showEditAvatarDialog();
                } else {
                    showAvatarDialog(userCard.getAvatar());
                }
            }
        });

        // 性别
        if (userCard.getSex() == 1) {
            mSex.setImageResource(R.mipmap.icon_boy);
        } else if (userCard.getSex() == 2) {
            mSex.setImageResource(R.mipmap.icon_girl);
        } else {
            mSex.setVisibility(View.GONE);
        }
        // 昵称
        if (!TextUtils.isEmpty(userCard.getRemark())) {//判断是否有备注名
            mUserName.setText(userCard.getRemark());
        } else {
            mUserName.setText(userCard.getNick());
        }
        // 说说
        String myTalk = DEFAULT_MYTALK;
        if (!TextUtils.isEmpty(userCard.getTalk())) {
            myTalk = userCard.getTalk();
        }
        mNote.setText(String.format("“ %s ”", myTalk));

        // 定位
        if (TextUtils.isEmpty(userCard.getAddress())) {
            mLocationLayout.setVisibility(View.INVISIBLE);
        } else {
            mLocationLayout.setVisibility(View.VISIBLE);
            mPosition.setText(userCard.getAddress());
        }

        // 汽车品牌
        if (TextUtils.isEmpty(userCard.getCarName())) {
            mCarBrandLayout.setVisibility(View.INVISIBLE);
        } else {
            mCarBrandLayout.setVisibility(View.VISIBLE);
            mCarIcon = userCard.getCarImage();
            mCarName = userCard.getCarName();
            Uri brandUri = Uri.parse(QiniuHelper.getThumbnail96Url(mCarIcon));
//          Uri brandUri = Uri.parse(QiniuHelper.getThumbnail96Url(userCard.getCarImage()));
            mCarBrandView.setImageURI(brandUri);
        }

        // 赞、关注、粉丝数量显示
        UserCounts userCounts = userCard.getUserCounts();
        mAttentionNum.setText(String.valueOf(userCounts.getFriendsNum()));
        mPraiseNum.setText(String.valueOf(userCounts.getPraiseNum()));
        mFansNum.setText(String.valueOf(userCounts.getFansNum()));
        if (isMine) {
            mUserName.setOnClickListener(this);
            mNote.setOnClickListener(this);
            mLocationLayout.setOnClickListener(this);
            mPosition.setOnClickListener(this);
        } else {//查看好友名片
            if (nexus == 1 || nexus == 2) {//关注和好友关系
                mAttentionFriend.setVisibility(View.GONE);
                rightMore.setImageResource(R.mipmap.icon_remark);//修改备注名按钮
            } else {//未关注的人
                mAttentionFriend.setVisibility(View.VISIBLE);
                rightMore.setVisibility(View.GONE);
                //rightMore.setImageResource(R.mipmap.icon_attention_friend);//加好友按钮
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //点击昵称进入编辑资料修改昵称
            case R.id.tv_username:
                startActivity(new Intent(BusinessCardActivity.this, EditUserDataActivity.class));
                break;
            //点击定位进入编辑资料修改我的所在地
            case R.id.tv_position:
                startActivity(new Intent(BusinessCardActivity.this, EditUserDataActivity.class));
                break;
            //点击说说进入编辑资料修改心情
            case R.id.tv_note:
                startActivity(new Intent(BusinessCardActivity.this, EditUserDataActivity.class));
                break;
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

    /**
     * 显示头像大图
     */
    private void showAvatarDialog(String avatarKey) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ViewAvatarFragment avatarFragment = ViewAvatarFragment.newInstance(avatarKey);
        avatarFragment.show(ft, "dialog");
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
                updateAvatarToQiniu(avatarFile, 0);
            }
        } else if (requestCode == RESULT_REQUEST_BACKGROUD) {//选择背景墙图片
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    fileUri = data.getData();
                }
                fileCropUri = getOutputMediaFileUri();
                cropImageUri(fileUri, fileCropUri, 720, 720, RESULT_REQUEST_BACKGROUD_CROP);
            }
        } else if (requestCode == RESULT_REQUEST_BACKGROUD_CROP) {
            if (resultCode == Activity.RESULT_OK) {
                String filePath = AppUtility.getFilePath(this, fileCropUri);
                File avatarFile = new File(filePath);
                Logger.d("filePath:" + filePath);
                // Todo: 将背景图片文件上传到指定服务器上
                updateAvatarToQiniu(avatarFile, 1);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateAvatarToQiniu(final File avatarFile, final int type) {
        ProgressHUD.showLoadingMessage(mContext, "正在更新", false);
        String randomStr = Security.aesEncrypt("android");
        ApiModule.apiService().getQiniuToken(randomStr).enqueue(new Callback<XKRepo>() {
            @Override
            public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                XKRepo xkRepo = response.body();
                if (xkRepo.isSuccess()) {
                    String token = xkRepo.getMsg();
                    upload(token, avatarFile, type);
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

    private void upload(String uploadToken, final File avatarFile, final int type) {
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
                    if (type == 0) {//修改头像
                        updateAvatarKey(fileKey, avatarFile);
                    } else if (type == 1) {//修改背景墙
                        updateBackGroud(fileKey, avatarFile);
                    }

                }
            }
        }, null);
    }

    //修改头像
    private void updateAvatarKey(final String key, final File avatarFile) {
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
                    ProgressHUD.showSuccessMessage(mContext, "已完成头像更新!");
                    // 更新本地头像信息
                    currentUser.setAvatar(key);
                    // mAvatarView.setImageBitmap(BitmapFactory.decodeFile(avatarFile.getPath()));
                    mAvatarView.setImageURI(Uri.parse(QiniuHelper.getThumbnail200Url(key)));

                    AccountInfo.getInstance().saveAccount(currentUser);
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

    //修改背景墙
    private void updateBackGroud(final String key, final File imageFile) {
        String account = Security.aesEncrypt(String.valueOf(currentUser.getAccount()));
        String type = Security.aesEncrypt("bgimg");
        String value = Security.aesEncrypt(key);
        ApiModule.apiService().editUserInfo(account,
                type, value).enqueue(new Callback<XKRepo>() {
            @Override
            public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                XKRepo xkRepo = response.body();
                Logger.i(xkRepo.getMsg());
                ProgressHUD.dismiss();
                if (xkRepo.isSuccess()) {
                    ProgressHUD.showSuccessMessage(mContext, "修改背景墙成功!");
                    // 更新本地头像信息
                    currentUser.setBackgroud(key);
                    Drawable drawable = new BitmapDrawable(BitmapFactory.decodeFile(imageFile.getPath()));
                    cardLayout.setBackground(drawable);
                    AccountInfo.getInstance().saveAccount(currentUser);
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

    @OnClick(R.id.sdv_car_brand)
    void onCarBrand() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        intent.setClass(this, PictureDisplayActivity.class);
        bundle.putInt("show", 1);
        bundle.putString("brandIcon", mCarIcon);
        bundle.putString("brandName", mCarName);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    //二维码头像
    @OnClick(R.id.iv_qr_code)
    void onQrCode() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        intent.setClass(this, PictureDisplayActivity.class);//跳转到二维码显示页面.
        bundle.putInt("show", 2);
        bundle.putString("account", mUserAccount);
        bundle.putString("UserIcon", avatar);

        intent.putExtras(bundle);
        startActivity(intent);
    }

    @OnClick(R.id.iv_back)
    void onBack() {
        finish();
    }


    @OnClick(R.id.iv_card_qr_code)
        //扫一扫
    void onTopQrCode() {
        Intent intent = new Intent();
        intent.setClass(this, MipcaActivityCapture.class);
        startActivity(intent);
    }

    @OnClick(R.id.tv_addfriend)
        // 加关注
    void onAttentionFriend() {
        final NormalDialog dialog = new NormalDialog(this);
        dialog.isTitleShow(false)
                .bgColor(Color.parseColor("#ffffff"))
                .cornerRadius(5)
                .content("是否关注" + nick + "?")
                .btnText("取消", "确定")
                .contentGravity(Gravity.CENTER)
                .contentTextColor(Color.parseColor("#33333d"))
                .dividerColor(Color.parseColor("#dcdce4"))
                .btnTextSize(15.5f, 15.5f)
                .btnTextColor(Color.parseColor("#ff2814"), Color.parseColor("#0077fe"))
                .widthScale(0.85f)
//                .showAnim(bas_in)
//                .dismissAnim(bas_out)
                .show();

        dialog.setOnBtnClickL(new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                dialog.dismiss();
            }
        }, new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                dialog.dismiss();
                ProgressHUD.showLoadingMessage(BusinessCardActivity.this, "正在关注...", false);
                ApiModule.apiService().addFriend(Security.aesEncrypt(myAccount),
                        Security.aesEncrypt(mUserAccount)).enqueue(new Callback<XKRepo>() {
                    @Override
                    public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                        XKRepo xkRepo = response.body();
                        ProgressHUD.dismiss();
                        String msg = xkRepo.getMsg();
                        if (xkRepo.isSuccess()) { // 关注成功
                            mAttentionFriend.setVisibility(View.GONE);
                            rightMore.setVisibility(View.VISIBLE);
                            rightMore.setImageResource(R.mipmap.icon_remark); // 修改备注名按钮
                        } else { // 关注失败
                            ProgressHUD.showErrorMessage(BusinessCardActivity.this, msg);
                        }
                    }

                    @Override
                    public void onFailure(Call<XKRepo> call, Throwable t) {
                        ProgressHUD.dismiss();
                        ProgressHUD.showErrorMessage(BusinessCardActivity.this, t.getLocalizedMessage());
                    }
                });
            }
        });
    }

    /**
     * 更多功能
     */
    @OnClick(R.id.iv_card_more)
    void onMore() {
        if (isMine) {
            //final String[] stringItems = {"分享名片", "编辑资料", "修改背景"};
            final String[] stringItems = {"分享名片", "编辑资料"};
            dialog = new ActionSheetDialog(this, stringItems, null);
            dialog.title("分享名片").isTitleShow(false).show();
            //弹出框背景色为白色
            dialog.lvBgColor(Color.parseColor("#ffffff"));
//            dialog.itemTextColor(Color.parseColor("#ff2814"));
            dialog.setOnOperItemClickL(new OnOperItemClickL() {
                @Override
                public void onOperItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    if (position == 0) {
                        //分享名片
                        ShareCardActivity.launch(BusinessCardActivity.this, myAccount, avatar, nick);
                        dialog.dismiss();
                    } else if (position == 1) {
                        //编辑资料
                        Intent intent = new Intent(mContext, EditUserDataActivity.class);
                        startActivity(intent);
                        dialog.dismiss();
                    } else if (position == 2) {
                        //修改背景
//                        dialog.dismiss();
//                        showChangeBGDialog();
                    }

                }
            });
        }
    }

    private void showChangeBGDialog() {//修改背景
        //final String[] stringItems = {"去拍一张美图", "去我的相册中选择", "去背景墙选择"};
        final String[] stringItems = {"去拍一张美图", "去我的相册中选择"};
        dialog = new ActionSheetDialog(this, stringItems, null);
        dialog.isTitleShow(false).show();
        //dialog.title("分享名片").isTitleShow(false).show();
        //弹出框背景色为白色
        dialog.lvBgColor(Color.parseColor("#ffffff"));
//            dialog.itemTextColor(Color.parseColor("#ff2814"));
        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (position == 0) {
                    //去拍一张美图
                    backgroupFromCamera();
                } else if (position == 1) {
                    //去我的相册中选择
                    backgroupFromPhoto();
                }
                dialog.dismiss();
            }
        });

    }

    @OnClick(R.id.rl_praise)
    void onPraise() {
        if (myAccount.equals(mUserAccount)) {
            PraiseActivity.launch(this, myAccount);
        } else {

        }
        //好友
        //            FriendsActivity.launch(this, mUserAccount);


    }

    //点击后跳转到添加好友关注模块
    @OnClick(R.id.rl_attention)
    void onAttention() {
        FollowAndFansActivity.launch(this, mUserAccount, 1);
    }

    //点击后跳转到粉丝模块
    @OnClick(R.id.rl_fans)
    void onFans() {
        if (myAccount.equals(mUserAccount)) {
            FollowAndFansActivity.launch(this, mUserAccount, 2);
        } else {
            FollowAndFansActivity.launch(this, mUserAccount, 2);
        }
    }

    public void backgroupFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, RESULT_REQUEST_BACKGROUD);
    }

    public void backgroupFromPhoto() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_REQUEST_BACKGROUD);
    }

}

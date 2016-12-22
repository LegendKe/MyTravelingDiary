package com.ruihai.xingka.ui.caption;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.LocationSource;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.orhanobut.logger.Logger;
import com.ruihai.android.network.http.Params;
import com.ruihai.iconicfontengine.widget.IconicFontTextView;
import com.ruihai.xingka.R;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.api.model.DraftBoxItem;
import com.ruihai.xingka.api.model.MyFriendInfoRepo.MyFriendInfo;
import com.ruihai.xingka.ui.BaseActivity;
import com.ruihai.xingka.ui.MainActivity;
import com.ruihai.xingka.ui.caption.adapter.SelectedPhotoAdapter;
import com.ruihai.xingka.ui.caption.publisher.bean.PublishBean;
import com.ruihai.xingka.ui.caption.publisher.bean.PublishType;
import com.ruihai.xingka.ui.caption.publisher.service.PublishService;
import com.ruihai.xingka.ui.common.MultiImageSelectorActivity;
import com.ruihai.xingka.ui.common.PhotoPagerActivity;
import com.ruihai.xingka.ui.common.PhotoPickerActivity;
import com.ruihai.xingka.ui.common.enter.EmojiTranslate;
import com.ruihai.xingka.ui.common.enter.EmojiconSpan;
import com.ruihai.xingka.utils.AppUtility;
import com.ruihai.xingka.utils.ImageCaptureManager;
import com.ruihai.xingka.widget.FullyGridLayoutManager;
import com.ruihai.xingka.widget.ProgressHUD;
import com.ruihai.xingka.widget.SwitchButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 发布图说
 */
public class CaptionAddActivity extends BaseActivity
        implements SelectedPhotoAdapter.OnPhotoItemClickListener, AMapLocationListener, LocationSource {

    public static final String EXTRA_SELECTED_LIST = "selected_list";
    private static final int TEXT_MAX_COUNT = 1000;
    public final static String KEY_TAKE_PICTURE = "TAKE_PICTURE";

    public final static int REQUEST_PHOTO_PICKER_CODE = 1;
    public final static int REQUEST_FRIEND_PICKER_CODE = 2;
    public final static int REQUEST_TAKE_PICTURE_CODE = 3;
    public final static int REQUEST_READ_PICTURE_CODE = 4;
    public static final int RESULT_REQUEST_CHOOSE_ADDRESS = 0x1001;

    @BindView(R.id.et_content)
    EditText mContentEditText;
    @BindView(R.id.tv_count)
    TextView mCountTextView;
    @BindView(R.id.sb_only_visible)
    SwitchButton mOnlyVisibleButton;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.tv_friends)
    TextView mFriendName;
    @BindView(R.id.at_users_layout)
    LinearLayout mAtUsersLayout;
    @BindView(R.id.tv_choose_friends)
    IconicFontTextView mAtUserIcon;
    @BindView(R.id.divide_line)
    View dividerLine;
    @BindView(R.id.tv_address)
    TextView mAddress;
    @BindView(R.id.tv_location)
    IconicFontTextView mAddressIcon;
    @BindView(R.id.img_only_visible)
    ImageView mOnlyVisibleIcon;

    private double x, y;
    private String address;
    private SelectedPhotoAdapter mPhotoAdapter;
    private ArrayList<String> mSelectedPath = new ArrayList<>();
    private ArrayList<MyFriendInfo> selectedFriends = new ArrayList<>();
    private int onlyVisible = 0;
    public PublishBean mPublishBean;
    private ImageCaptureManager captureManager;
    private LocationManagerProxy mAMapLocationManager;
    private LocationSource.OnLocationChangedListener mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caption_add);

        boolean isTakePicture = getIntent().getBooleanExtra(KEY_TAKE_PICTURE, false);
        if (isTakePicture) {
            takePicture();
        }
        ButterKnife.bind(this);

        if (mPublishBean == null) {
            mPublishBean = newPublishBean();
        }
        activate(mListener);
        initViews();
        initListeners();
    }

    /**
     * 利用系统自带的相机应用进行拍照
     */
    private void takePicture() {
        captureManager = new ImageCaptureManager(this);
        Intent intent = null;
        try {
            intent = captureManager.dispatchTakePictureIntent();
            startActivityForResult(intent, REQUEST_TAKE_PICTURE_CODE);
        } catch (IOException e) {
            ProgressHUD.showErrorMessage(mContext, "调用相机失败");
            e.printStackTrace();
        }
    }

    private void initViews() {
        mContentEditText.setSelection(mContentEditText.length()); // 将光标移动至最后一个字符后面
        if (AccountInfo.getInstance().getUserDraftBoxInfo() != null) {
            DraftBoxItem item = AccountInfo.getInstance().getUserDraftBoxInfo();
            mSelectedPath = item.getmSelectedPath();
            selectedFriends = item.getSelectedFriends();
            onlyVisible = item.getOnlyVisible();
            address = item.getAddress();
            x = item.getX();
            y = item.getY();
            mContentEditText.setText(item.getTitle());
            if (TextUtils.isEmpty(address)) {
                mAddress.setText("标记位置");
                mAddressIcon.setTextColor(Color.parseColor("#9999a3"));
            } else {
                mAddressIcon.setTextColor(Color.parseColor("#ffa427"));
                mAddress.setText(address);
            }
            if (onlyVisible == 1) {
                mOnlyVisibleButton.setToggleOn(); // 仅自己可见开启
                mAtUsersLayout.setVisibility(View.GONE);
                dividerLine.setVisibility(View.GONE);
                mOnlyVisibleIcon.setImageResource(R.mipmap.icon_secret_highlighted);
                selectedFriends.clear();
            } else {
                mOnlyVisibleButton.setToggleOff(); // 仅自己可见关闭
                mAtUsersLayout.setVisibility(View.VISIBLE);
                mOnlyVisibleIcon.setImageResource(R.mipmap.icon_secret);
                dividerLine.setVisibility(View.VISIBLE);
            }
            displayAtUserNames();
        } else {
            mOnlyVisibleButton.setToggleOff(); // 默认仅自己可见关闭

        }
        setLeftCount();
        mRecyclerView.setLayoutManager(new FullyGridLayoutManager(this, 4));
        mPhotoAdapter = new SelectedPhotoAdapter(this, mSelectedPath);
        mPhotoAdapter.setOnPhotoItemClickListener(this);
        mRecyclerView.setAdapter(mPhotoAdapter);
    }

    private void initListeners() {
        mContentEditText.addTextChangedListener(new TextWatcher() {
                                                    private int editStart;
                                                    private int editEnd;

                                                    @Override
                                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                                    }

                                                    @Override
                                                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                                                        if (count == 1 || count == 2) {
                                                            String newString = s.subSequence(start, start + count).toString();
                                                            String imgName = EmojiTranslate.sEmojiMap.get(newString);
                                                            if (imgName != null) {
                                                                final String format = ":%s:";
                                                                String replaced = String.format(format, imgName);
                                                                Editable editable = mContentEditText.getText();
                                                                editable.replace(start, start + count, replaced);
                                                                editable.setSpan(new EmojiconSpan(CaptionAddActivity.this, imgName), start, start + replaced.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                            }
                                                        } else {
                                                            int emojiStart = 0;
                                                            int emojiEnd;
                                                            boolean startFinded = false;
                                                            int end = start + count;
                                                            for (int i = start; i < end; ++i) {
                                                                if (s.charAt(i) == ':') {
                                                                    if (!startFinded) {
                                                                        emojiStart = i;
                                                                        startFinded = true;
                                                                    } else {
                                                                        emojiEnd = i;
                                                                        if (emojiEnd - emojiStart < 2) { // 指示的是两个：的位置，如果是表情的话，间距肯定大于1
                                                                            emojiStart = emojiEnd;
                                                                        } else {
                                                                            String newString = s.subSequence(emojiStart + 1, emojiEnd).toString();
                                                                            EmojiconSpan emojiSpan = new EmojiconSpan(CaptionAddActivity.this, newString);
                                                                            if (emojiSpan.getDrawable() != null) {
                                                                                Editable editable = mContentEditText.getText();
                                                                                editable.setSpan(emojiSpan, emojiStart, emojiEnd + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                                                startFinded = false;
                                                                            } else {
                                                                                emojiStart = emojiEnd;
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void afterTextChanged(Editable s) {
                                                        editStart = mContentEditText.getSelectionStart();
                                                        editEnd = mContentEditText.getSelectionEnd();

                                                        // 先去掉监听器，否则会出现栈溢出
                                                        mContentEditText.removeTextChangedListener(this);
                                                        // 注意这里只能每次都对整个EditText的内容求长度，不能对删除的单个字符求长度
                                                        // 因为是中英文混合，单个字符而言，calculateLength函数都会返回1
                                                        while (calculateLength(s.toString()) > TEXT_MAX_COUNT) { // 当输入字符个数超过限制的大小时，进行截断操作
                                                            s.delete(editStart - 1, editEnd);
                                                            editStart--;
                                                            editEnd--;
                                                            mContentEditText.setText(s);
                                                            mContentEditText.setSelection(editStart);
                                                        }


                                                        // 恢复监听器
                                                        mContentEditText.addTextChangedListener(this);

                                                        setLeftCount();
                                                    }
                                                }

        );

        mOnlyVisibleButton.setOnToggleChanged(new SwitchButton.OnToggleChanged()

                                              {
                                                  @Override
                                                  public void onToggle(boolean on) {
                                                      if (on) {
                                                          onlyVisible = 1;
                                                          mOnlyVisibleIcon.setImageResource(R.mipmap.icon_secret_highlighted);
                                                          mAtUsersLayout.setVisibility(View.GONE);
                                                          dividerLine.setVisibility(View.GONE);
                                                          selectedFriends.clear();
                                                          displayAtUserNames();

                                                      } else {
                                                          onlyVisible = 0;
                                                          mOnlyVisibleIcon.setImageResource(R.mipmap.icon_secret);
                                                          mAtUsersLayout.setVisibility(View.VISIBLE);
                                                          dividerLine.setVisibility(View.VISIBLE);
                                                          mFriendName.setText("好友");
                                                      }
                                                      getPublishBean().getParams().addParameter("visible", String.valueOf(onlyVisible));
                                                  }
                                              }

        );
    }

    @OnClick(R.id.at_users_layout)
        //选择好友
    void chooseFriends() {
        Intent intent = new Intent(this, ChooseFriendsActivity.class);
        intent.putParcelableArrayListExtra(ChooseFriendsActivity.KEY_SELECTED_FRIENDS, selectedFriends);
        startActivityForResult(intent, REQUEST_FRIEND_PICKER_CODE);
    }

    /**
     * 刷新剩余输入字数，最大值140个字
     */
    private void setLeftCount() {
        mCountTextView.setText(String.format(getString(R.string.edittext_content_count),
                getInputCount(), TEXT_MAX_COUNT));
    }

    /**
     * 获取用户输入的分享内容字数
     *
     * @return
     */
    private long getInputCount() {
        return calculateLength(mContentEditText.getText().toString());
    }

    /**
     * 计算图说内容的字数，一个汉字=两个英文字符，一个中文标点=两个英文标点
     * 注意：该函数的不是用于对单个字符进行计算，因为单个字符四舍五入后都是1
     *
     * @param c
     * @return
     */
    private long calculateLength(CharSequence c) {
        double len = 0;
        for (int i = 0; i < c.length(); i++) {
            int tmp = (int) c.charAt(i);
            if (tmp > 0 && tmp < 127) {
//                len += 0.5;
                len += 1;
            } else {
                len++;
            }
        }
        return Math.round(len);
    }

    @OnClick(R.id.at_address_layout)
    void chooseAddress() {
        Intent intent = new Intent(this, ChooseLocationActivity.class);
        startActivityForResult(intent, RESULT_REQUEST_CHOOSE_ADDRESS);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        List<String> photos = null;
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_PHOTO_PICKER_CODE) { // 从相册选择照片
                if (data != null) {
                    photos = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                }
                mSelectedPath.clear();
                if (photos != null) {
                    mSelectedPath.addAll(photos);
                }
                mPhotoAdapter.notifyDataSetChanged();
            } else if (requestCode == REQUEST_TAKE_PICTURE_CODE) { // 拍照
                captureManager.galleryAddPic();
                String path = captureManager.getCurrentPhotoPath();

                if (!TextUtils.isEmpty(path)) {
                    mSelectedPath.add(0, path);
                }
                mPhotoAdapter.notifyDataSetChanged();
            } else if (requestCode == REQUEST_FRIEND_PICKER_CODE) { // 选择@好友
                selectedFriends = data.getParcelableArrayListExtra(ChooseFriendsActivity.KEY_SELECTED_FRIENDS);
                displayAtUserNames();
            } else if (requestCode == RESULT_REQUEST_CHOOSE_ADDRESS) {//选择地理位置
                if (data != null) {
                    address = data.getStringExtra("address");
                    Log.e("位置", address);
                    x = data.getDoubleExtra("x", x);
                    y = data.getDoubleExtra("y", y);
                    //city = data.getStringExtra("city");
                    // String str = city + "·" + address;
                    if (TextUtils.isEmpty(address)) {
                        mAddress.setText("标记位置");
                        mAddressIcon.setTextColor(Color.parseColor("#9999a3"));
                    } else {
                        mAddressIcon.setTextColor(Color.parseColor("#ffa427"));
                        mAddress.setText(address);
                    }
                }
            } else if (requestCode == REQUEST_READ_PICTURE_CODE) {
                mSelectedPath.clear();
                mSelectedPath.addAll(data.getStringArrayListExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS));
                //mSelectedPath = data.getStringArrayListExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS);
                mPhotoAdapter.notifyDataSetChanged();
            }
        }
    }

    private void displayAtUserNames() {
        String friendNameStr = "";
        for (int i = 0; i < selectedFriends.size(); i++) {
            MyFriendInfo friend = selectedFriends.get(i);
            String name = String.valueOf(friend.getAccount());
            if (!TextUtils.isEmpty(friend.getRemark())) {
                name = friend.getRemark();
            } else if (!TextUtils.isEmpty(friend.getNick())) {
                name = friend.getNick();
            }
            if (i > 0 && i < selectedFriends.size()) {
                friendNameStr += ",";
            }
            friendNameStr += name;
        }
        if (TextUtils.isEmpty(friendNameStr)) {
            mAtUserIcon.setTextColor(Color.parseColor("#9999a3"));
        } else {
            mAtUserIcon.setTextColor(Color.parseColor("#ffa427"));
        }
        mFriendName.setText(friendNameStr);

    }

    @Override
    public void onPhotoClick(View v, int position) {
        if (position == mSelectedPath.size()) { // 点击加号
            Intent intent = new Intent(mContext, MultiImageSelectorActivity.class);
            // 是否显示拍摄图片
            intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
            // 最大可选择图片数量
            intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 9);
            // 选择模式
            intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
            // 默认选择
            if (mSelectedPath != null && mSelectedPath.size() > 0) {
                intent.putExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, mSelectedPath);
            }
            startActivityForResult(intent, REQUEST_PHOTO_PICKER_CODE);
        } else {
            Intent intent = new Intent(mContext, PhotoPagerActivity.class);
            intent.putExtra(PhotoPagerActivity.EXTRA_CURRENT_ITEM, position);
            intent.putExtra(PhotoPagerActivity.EXTRA_PHOTOS, mSelectedPath);
            intent.putExtra(PhotoPagerActivity.EXTRA_TYPE, 3);
            startActivityForResult(intent, REQUEST_READ_PICTURE_CODE);
        }
    }

    @OnClick(R.id.btn_caption_edit)
    void captionEdit() {
        Intent intent = new Intent(this, CaptionEditActivity.class);
        intent.putExtra(CaptionEditActivity.EXTRA_SELECTED_LIST, mSelectedPath);
        startActivityForResult(intent, REQUEST_PHOTO_PICKER_CODE);
    }

    /**
     * 返回按钮
     */
    @OnClick(R.id.tv_back)
    void onBack() {
        showExitConfirmDialog();
        //finish();
    }

    /**
     * 提交图说内容
     */
    @OnClick(R.id.tv_right)
    void rightClicked() {
        immediatelyPublish();
    }

    //@OnClick(R.id.btn_immediately_publish)
    void immediatelyPublish() {
        if (mSelectedPath.size() <= 0) {
            AppUtility.showToast("请选择要发布的照片");
            return;
        }

        for (String path : mSelectedPath) {
            Logger.e(path);
        }

        String[] pics = getPublishBean().getPics();
        pics = mSelectedPath.toArray(new String[0]);
        getPublishBean().setPics(pics);
        getPublishBean().setSelectedFriends(selectedFriends);
        String[] atUser = new String[selectedFriends.size()];
        for (int i = 0; i < selectedFriends.size(); i++) {


            MyFriendInfo info = selectedFriends.get(i);
            atUser[i] = String.valueOf(info.getAccount());
        }
        getPublishBean().setAtUsers(atUser);
        String content = mContentEditText.getText().toString().trim();
        getPublishBean().setText(content);
        getPublishBean().getParams().addParameter("content", content);
        getPublishBean().getParams().addParameter("visible", String.valueOf(onlyVisible));
        getPublishBean().setX(x);
        getPublishBean().setY(y);
        getPublishBean().setAddress(address);

        PublishService.publish(mContext, getPublishBean());
        MainActivity.launch(CaptionAddActivity.this, 3);
        finish();
    }

    @Override
    public void onBackPressed() {
        showExitConfirmDialog();
        //finish();
        return;
    }

    /**
     * 是否将未保存的内容存入草稿箱
     */
    private void showExitConfirmDialog() {
        final String[] actionItems = {"保存草稿", "不保存"};
        final ActionSheetDialog dialog = new ActionSheetDialog(CaptionAddActivity.this, actionItems, null);

        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) { // 临时保存
                    //存入草稿箱
                    DraftBoxItem data = new DraftBoxItem();
                    data.setTitle(mContentEditText.getText().toString());
                    data.setSaveTime(System.currentTimeMillis());
                    data.setType("图说");
                    data.setSelectedFriends(selectedFriends);
                    data.setmSelectedPath(mSelectedPath);
                    data.setOnlyVisible(onlyVisible);
                    data.setAddress(address);
                    data.setX(x);
                    data.setY(y);
                    AccountInfo.getInstance().saveUserDraftBoxInfo(data);
                } else if (position == 1) { // 不保存
                    AccountInfo.getInstance().clearUserDraftBoxInfo();
                }
                finish();
                dialog.dismiss();
            }
        });
        if (!TextUtils.isEmpty(mContentEditText.getText().toString()) || mSelectedPath.size() > 0) {
            dialog.title("是否保存到草稿?").titleTextSize_SP(14.5f).isTitleShow(true).show();
        } else {
            finish();
        }
    }

    public PublishBean getPublishBean() {
        return mPublishBean;
    }

    private PublishBean newPublishBean() {
        PublishBean bean = new PublishBean();
        bean.setStatus(PublishBean.PublishStatus.create);
        bean.setType(PublishType.caption);
        Params params = new Params();
        // 默认所有人可见
        params.addParameter("visible", "0");
        bean.setParams(params);
        return bean;
    }

    //定位
    @Override
    public void onLocationChanged(AMapLocation aLocation) {
        if (aLocation != null) {
            // mALocation = aLocation;
            /**
             * 获取城市的编码
             */
            //cityCode = aLocation.getCityCode();
            String city = aLocation.getCity();
            String add = aLocation.getPoiName();
            //Log.i("TAG", "------>" + aLocation.getPoiName() + "--->" + aLocation.getDistrict() + "------>" + aLocation.getProvider());
            if (!TextUtils.isEmpty(city) && !TextUtils.isEmpty(add)) {
                mAddress.setText(city + " · " + add);
                address = city + " · " + add;
            }

//            StringBuffer sb = new StringBuffer(256);
//            sb.append(aLocation.getCity());
//            if (sb.toString() != null && sb.toString().length() > 0) {
//                String lngCityName = sb.toString();
//
//            }

        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mAMapLocationManager == null) {
            mAMapLocationManager = LocationManagerProxy.getInstance(this);
            mAMapLocationManager.setGpsEnable(true);
            /*
             * mAMapLocManager.setGpsEnable(false);
			 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true Location
			 * API定位采用GPS和网络混合定位方式
			 * ，第一个参数是定位provider，第二个参数时间最短是2000毫秒，第三个参数距离间隔单位是米，第四个参数是定位监听者
			 */
            mAMapLocationManager.requestLocationUpdates(LocationProviderProxy.AMapNetwork, 2000, 0, this);
        }
    }

    @Override
    public void deactivate() {
        // mListener = null;
        if (mAMapLocationManager != null) {
            mAMapLocationManager.removeUpdates(this);
            mAMapLocationManager.destory();
        }
        mAMapLocationManager = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        deactivate();
    }
}

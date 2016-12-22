package com.ruihai.xingka.ui.common;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.ruihai.iconicfontengine.widget.IconicFontTextView;
import com.ruihai.xingka.R;
import com.ruihai.xingka.ui.caption.CropImageActivity;
import com.ruihai.xingka.ui.common.fragment.ImagePagerFragment;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 图片浏览
 */
public class PhotoPagerActivity extends UmengActivity {

    @BindView(R.id.tv_trash)
    IconicFontTextView mTrash;
    @BindView(R.id.tv_title)
    TextView mTitleView;
    @BindView(R.id.btn_download_pic)
    IconicFontTextView mDownloadPicButton;
    @BindView(R.id.edit_layout)
    LinearLayout editLayout;
    public String userNick;
    private ImagePagerFragment mPagerFragment;

    public final static String EXTRA_CURRENT_ITEM = "current_item";
    public final static String EXTRA_PHOTOS = "photos";
    public final static String EXTRA_TYPE = "type"; // 1:下载 2:删除 3.发布图说预览页面,有裁剪和删除
    public static final int REQUEST_CROP_IMAGE_CODE = 1;
    public static final String EXTRA_SELECTED_LIST = "selected_list";
    public static final String EXTRA_RESULT = "select_result";
    public static final String EXTRA_USERNICK = "user_nick";

    private int currentItem;

    private ArrayList<String> mPaths;

    //private String userNick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_pager);
        ButterKnife.bind(this);

        currentItem = getIntent().getIntExtra(EXTRA_CURRENT_ITEM, 0);
        mPaths = getIntent().getStringArrayListExtra(EXTRA_PHOTOS);
        int type = getIntent().getIntExtra(EXTRA_TYPE, 0);
        userNick = getIntent().getStringExtra(EXTRA_USERNICK);
        Log.i("TAG", "------PhotoPagerActivity------>" + userNick);
        mPagerFragment = (ImagePagerFragment) getSupportFragmentManager().findFragmentById(R.id.photoPagerFragment);
        mPagerFragment.setPhotos(mPaths, currentItem, userNick);
        if (type == 1) {
            mDownloadPicButton.setVisibility(View.GONE);
        } else if (type == 3) {
            editLayout.setVisibility(View.GONE);
            mTrash.setVisibility(View.VISIBLE);
        }
        updateActionBarTitle();
        mPagerFragment.getViewPager().addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                updateActionBarTitle();
            }

            @Override
            public void onPageSelected(int i) {
                currentItem = i;
                String imagePath = mPaths.get(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CROP_IMAGE_CODE) {
                String imagePath = data.getStringExtra(CropImageActivity.ARG_CROP_RESULT);
                mPaths.set(currentItem, imagePath);
                mPagerFragment.setPhotos(mPaths, currentItem, userNick);
            }
        }
    }

    @OnClick(R.id.tv_back)
    void onBack() {
        onBackPressed();
    }

    @OnClick(R.id.btn_download_pic)
    void downloadPic() {

    }

    @OnClick(R.id.tv_crop)
    void cropImage() {
        //int position = imageAdapter.getCurrentPosition();
        Intent intent = new Intent(this, CropImageActivity.class);
        intent.putExtra(CropImageActivity.ARG_CROP_IMAGE_PATH, mPaths.get(currentItem));
        startActivityForResult(intent, REQUEST_CROP_IMAGE_CODE);
    }

    @OnClick(R.id.tv_trash)
    void deleteImage() {
        confirmDeleteDialog();
    }

    /**
     * @功能描述 : 删除确认提示操作框
     */
    private void confirmDeleteDialog() {
        final NormalDialog dialog = new NormalDialog(this);
        dialog.isTitleShow(false)
                .bgColor(Color.parseColor("#ffffff"))
                .cornerRadius(5)
                .content("确定要删除这张图片?")
                .btnText(" 取消", "确定")
                .contentGravity(Gravity.CENTER)
                .contentTextColor(Color.parseColor("#33333d"))
                .dividerColor(Color.parseColor("#dcdce4"))
                .btnTextSize(15.5f, 15.5f)
                .btnTextColor(Color.parseColor("#0077fe"), Color.parseColor("#ff2814"))
                .widthScale(0.85f)
                .show();

        dialog.setOnBtnClickL(new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                dialog.dismiss();
            }
        }, new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                confirmDel();
                dialog.dismiss();
            }
        });
    }

    private void confirmDel() {
        //int position = imageAdapter.getCurrentPosition();
        mPaths.remove(currentItem);
        onBack();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        //intent.putExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS, mPagerFragment.getPaths());
        intent.putExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS, mPaths);
        setResult(RESULT_OK, intent);
        finish();
        super.onBackPressed();
    }

    public void updateActionBarTitle() {
        mTitleView.setText(getString(R.string.image_index, mPagerFragment.getViewPager().getCurrentItem() + 1,
                mPagerFragment.getPaths().size()));
    }

}

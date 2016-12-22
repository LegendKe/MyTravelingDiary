package com.ruihai.xingka.ui.caption;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Gravity;

import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.ruihai.xingka.R;
import com.ruihai.xingka.ui.BaseActivity;
import com.ruihai.xingka.ui.caption.adapter.ImagePagerFragmentAdapter;
import com.ruihai.xingka.ui.caption.fragment.ImagePageFragment;
import com.ruihai.xingka.widget.pagerimagepicker.ImageRecyclerView;
import com.ruihai.xingka.widget.pagerimagepicker.RecyclerViewInsetDecoration;
import com.ruihai.xingka.widget.pagerimagepicker.adapter.DefaultImageAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 图说照片编辑页
 */
public class CaptionEditActivity extends BaseActivity {

    public static final int REQUEST_CROP_IMAGE_CODE = 1;
    public static final String EXTRA_SELECTED_LIST = "selected_list";
    public static final String EXTRA_RESULT = "select_result";

    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.image_picker)
    ImageRecyclerView imagePicker;

    private ArrayList<String> selectedList = new ArrayList<>();
    private DefaultImageAdapter imageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caption_edit);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        selectedList = intent.getStringArrayListExtra(EXTRA_SELECTED_LIST);

        initViews();

        initListeners();
    }

    private void initViews() {
        imagePicker.addItemDecoration(new RecyclerViewInsetDecoration(this, R.dimen.image_card_insets));
        setupImagePickerDataSet(0);
    }

    private void initListeners() {

    }

    @OnClick(R.id.tv_crop)
    void cropImage() {
        int position = imageAdapter.getCurrentPosition();
        Intent intent = new Intent(this, CropImageActivity.class);
        intent.putExtra(CropImageActivity.ARG_CROP_IMAGE_PATH, selectedList.get(position));
        startActivityForResult(intent, REQUEST_CROP_IMAGE_CODE);
    }

    @OnClick(R.id.tv_back)
    void onBack() {
        Intent intent = new Intent();
        intent.putStringArrayListExtra(EXTRA_RESULT, selectedList);
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    @OnClick(R.id.tv_right)
    void onRight() {
        Intent intent = new Intent();
        intent.putStringArrayListExtra(EXTRA_RESULT, selectedList);
        setResult(RESULT_OK, intent);
        finish();
    }

    @OnClick(R.id.tv_trash)
    void deleteImage() {
        confirmDeleteDialog();
    }

    /**
     * @功能描述 : 举报确认提示操作框
     */
    private void confirmDeleteDialog() {
        final NormalDialog dialog = new NormalDialog(mContext);
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
                dialog.dismiss();
                confirmDel();
            }
        });
    }

    private void confirmDel() {
        int position = imageAdapter.getCurrentPosition();
        selectedList.remove(position);
        if (selectedList.size() > 0) {
            setupImagePickerDataSet(0);
        } else {
            onBack();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CROP_IMAGE_CODE) { // 从图片裁剪获得图片
                if (data != null) {
                    String path = data.getStringExtra(CropImageActivity.ARG_CROP_RESULT);
                    int position = imageAdapter.getCurrentPosition();
                    selectedList.set(position, path);
                    setupImagePickerDataSet(position);
                }
            }
        }
    }

    private void setupImagePickerDataSet(int defaultPosition) {
        imageAdapter = new DefaultImageAdapter(selectedList, defaultPosition);
        imagePicker.setAdapter(imageAdapter);
        ImagePagerFragmentAdapter fragmentAdapter = new ImagePagerFragmentAdapter(getSupportFragmentManager(), imagePicker.getImageAdapter()) {
            @Override
            protected Fragment getFragment(int position, String imageUrl) {
                return ImagePageFragment.newInstance(position, imageUrl);
            }
        };

        viewPager.setAdapter(fragmentAdapter);
        imagePicker.setPager(viewPager);

        imagePicker.setImagePickerListener(new ImageRecyclerView.ImagePickerListener() {
            @Override
            public void onImagePickerItemClick(String imageUrl, int position) {

            }

            @Override
            public void onImagePlusItemClick() { // 点击+号
                onBack();
            }

            @Override
            public void onImagePickerPageSelected(int position) {

            }

            @Override
            public void onImagePickerPageStateChanged(int state) {

            }

            @Override
            public void onImagePickerPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        onBack();
        super.onBackPressed();
    }
}

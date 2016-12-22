package com.ruihai.xingka.ui.common;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.ruihai.xingka.R;
import com.ruihai.xingka.event.UpdateImageGridEvent;
import com.ruihai.xingka.ui.caption.CaptionAddActivity;
import com.ruihai.xingka.ui.caption.CaptionEditActivity;
import com.ruihai.xingka.utils.AppUtility;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 多图选择
 * Created by Nereo on 2015/4/7.
 */
public class MultiImageSelectorActivity extends FragmentActivity implements MultiImageSelectorFragment.Callback {

    /**
     * 最大图片选择次数，int类型，默认9
     */
    public static final String EXTRA_SELECT_COUNT = "max_select_count";
    /**
     * 图片选择模式，默认多选
     */
    public static final String EXTRA_SELECT_MODE = "select_count_mode";
    /**
     * 是否显示相机，默认显示
     */
    public static final String EXTRA_SHOW_CAMERA = "show_camera";
    /**
     * 选择结果，返回为 ArrayList&lt;String&gt; 图片路径集合
     */
    public static final String EXTRA_RESULT = "select_result";
    /**
     * 默认选择集
     */
    public static final String EXTRA_DEFAULT_SELECTED_LIST = "default_list";

    /**
     * 单选
     */
    public static final int MODE_SINGLE = 0;
    /**
     * 多选
     */
    public static final int MODE_MULTI = 1;

    public final static int REQUEST_PHOTO_EDIT_CODE = 1;

    private ArrayList<String> resultList = new ArrayList<>();
    private TextView mSubmitButton;
    private TextView mTitleView;
    private int mDefaultCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default);
        //设置竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Intent intent = getIntent();
        mDefaultCount = intent.getIntExtra(EXTRA_SELECT_COUNT, 9);
        int mode = intent.getIntExtra(EXTRA_SELECT_MODE, MODE_MULTI);
        boolean isShow = intent.getBooleanExtra(EXTRA_SHOW_CAMERA, true);
        if (mode == MODE_MULTI && intent.hasExtra(EXTRA_DEFAULT_SELECTED_LIST)) {
            resultList = intent.getStringArrayListExtra(EXTRA_DEFAULT_SELECTED_LIST);
        }
        Bundle bundle = new Bundle();
        bundle.putInt(MultiImageSelectorFragment.EXTRA_SELECT_COUNT, mDefaultCount);
        bundle.putInt(MultiImageSelectorFragment.EXTRA_SELECT_MODE, mode);
        bundle.putBoolean(MultiImageSelectorFragment.EXTRA_SHOW_CAMERA, isShow);
        bundle.putStringArrayList(MultiImageSelectorFragment.EXTRA_DEFAULT_SELECTED_LIST, resultList);

        Fragment fragment = Fragment.instantiate(this, MultiImageSelectorFragment.class.getName(), bundle);
        EventBus.getDefault().register(fragment);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.image_grid, fragment)
                .commit();
        // 返回按钮
        findViewById(R.id.tv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        mTitleView = (TextView) findViewById(R.id.tv_title);
        mSubmitButton = (TextView) findViewById(R.id.tv_right); // 完成按钮
//        mSubmitButton.setText("{xk-next}");
        updateTitle();

        if (resultList == null || resultList.size() <= 0) {
            mSubmitButton.setEnabled(false);
        } else {
            updateTitle();
            mSubmitButton.setEnabled(true);
        }
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (resultList != null && resultList.size() > 0) {
                    // 返回已选择的图片数据
                    Intent data = new Intent();
                    data.putStringArrayListExtra(EXTRA_RESULT, resultList);
                    setResult(RESULT_OK, data);
                    finish();
                    for (int i = 0; i < resultList.size(); i++) {
                        Log.i("TAG", "---图片角度1----->" + AppUtility.readPictureDegree(resultList.get(i)));
                    }
                    //图片编辑页
//                    Intent intent = new Intent(MultiImageSelectorActivity.this, CaptionEditActivity.class);
//                    Intent intent = new Intent(MultiImageSelectorActivity.this, CaptionAddActivity.class);
//                    intent.putExtra(CaptionEditActivity.EXTRA_SELECTED_LIST, resultList);
//                    startActivityForResult(intent, REQUEST_PHOTO_EDIT_CODE);
                }
            }
        });
    }

    @Override
    public void onSingleImageSelected(String path) {
        Intent data = new Intent();
        resultList.add(path);
        data.putStringArrayListExtra(EXTRA_RESULT, resultList);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onImageSelected(String path) {
        if (!resultList.contains(path)) {
            resultList.add(path);
        }
        // 有图片之后，改变按钮状态
        if (resultList.size() > 0) {
            updateTitle();
            if (!mSubmitButton.isEnabled()) {
                mSubmitButton.setEnabled(true);
            }
        }
    }

    @Override
    public void onImageUnselected(String path) {
        if (resultList.contains(path)) {
            resultList.remove(path);
            updateTitle();
        } else {
            updateTitle();
        }
        // 当为选择图片时候的状态
        if (resultList.size() == 0) {
            mSubmitButton.setEnabled(false);
        }
    }

    @Override
    public void onCameraShot(File imageFile) {
        if (imageFile != null) {
            Intent data = new Intent();
            Log.i("TAG", "---图片角度1----->" + AppUtility.readPictureDegree(imageFile.getAbsolutePath()));
            resultList.add(imageFile.getAbsolutePath());
            data.putStringArrayListExtra(EXTRA_RESULT, resultList);
            setResult(RESULT_OK, data);
            finish();
        }
    }

    private void updateTitle() {
        mTitleView.setText("已经选择 (" + resultList.size() + "/" + mDefaultCount + ") 张照片");
        SpannableStringBuilder builder = new SpannableStringBuilder(mTitleView.getText().toString());
        // ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
        ForegroundColorSpan oraneSpan = new ForegroundColorSpan(getResources().getColor(R.color.orange));
        builder.setSpan(oraneSpan, 6, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTitleView.setText(builder);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<String> photos = null;
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_PHOTO_EDIT_CODE) {
                if (data != null) {
                    photos = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                }
                resultList.clear();
                if (photos != null) {
                    resultList.addAll(photos);
                }
                Intent theData = new Intent();
                theData.putStringArrayListExtra(EXTRA_RESULT, resultList);
                setResult(RESULT_OK, theData);
                finish();
            }
        } else if (resultCode == RESULT_CANCELED) {
            if (requestCode == REQUEST_PHOTO_EDIT_CODE) {
                if (data != null) {
                    photos = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                }
                resultList.clear();
                if (photos != null) {
                    resultList.addAll(photos);
                }
                updateTitle(); // 更新Title显示
                EventBus.getDefault().post(new UpdateImageGridEvent(resultList));
            }
        }
    }
}






//public class MultiImageSelectorActivity extends FragmentActivity implements MultiImageSelectorFragment.Callback {
//
//    /**
//     * 最大图片选择次数，int类型，默认9
//     */
//    public static final String EXTRA_SELECT_COUNT = "max_select_count";
//    /**
//     * 图片选择模式，默认多选
//     */
//    public static final String EXTRA_SELECT_MODE = "select_count_mode";
//    /**
//     * 是否显示相机，默认显示
//     */
//    public static final String EXTRA_SHOW_CAMERA = "show_camera";
//    /**
//     * 选择结果，返回为 ArrayList&lt;String&gt; 图片路径集合
//     */
//    public static final String EXTRA_RESULT = "select_result";
//    /**
//     * 默认选择集
//     */
//    public static final String EXTRA_DEFAULT_SELECTED_LIST = "default_list";
//
//    /**
//     * 单选
//     */
//    public static final int MODE_SINGLE = 0;
//    /**
//     * 多选
//     */
//    public static final int MODE_MULTI = 1;
//
//    public final static int REQUEST_PHOTO_EDIT_CODE = 1;
//
//    private ArrayList<String> resultList = new ArrayList<>();
//    private TextView mSubmitButton;
//    private TextView mTitleView;
//    private int mDefaultCount;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_default);
//        //设置竖屏
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        Intent intent = getIntent();
//        mDefaultCount = intent.getIntExtra(EXTRA_SELECT_COUNT, 9);
//        int mode = intent.getIntExtra(EXTRA_SELECT_MODE, MODE_MULTI);
//        boolean isShow = intent.getBooleanExtra(EXTRA_SHOW_CAMERA, true);
//        if (mode == MODE_MULTI && intent.hasExtra(EXTRA_DEFAULT_SELECTED_LIST)) {
//            resultList = intent.getStringArrayListExtra(EXTRA_DEFAULT_SELECTED_LIST);
//        }
//        Bundle bundle = new Bundle();
//        bundle.putInt(MultiImageSelectorFragment.EXTRA_SELECT_COUNT, mDefaultCount);
//        bundle.putInt(MultiImageSelectorFragment.EXTRA_SELECT_MODE, mode);
//        bundle.putBoolean(MultiImageSelectorFragment.EXTRA_SHOW_CAMERA, isShow);
//        bundle.putStringArrayList(MultiImageSelectorFragment.EXTRA_DEFAULT_SELECTED_LIST, resultList);
//
//        Fragment fragment = Fragment.instantiate(this, MultiImageSelectorFragment.class.getName(), bundle);
//        EventBus.getDefault().register(fragment);
//        getSupportFragmentManager().beginTransaction()
//                .add(R.id.image_grid, fragment)
//                .commit();
//        // 返回按钮
//        findViewById(R.id.tv_back).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                setResult(RESULT_CANCELED);
//                finish();
//            }
//        });
//
//        mTitleView = (TextView) findViewById(R.id.tv_title);
//        mSubmitButton = (TextView) findViewById(R.id.tv_right); // 完成按钮
////        mSubmitButton.setText("{xk-next}");
//        updateTitle();
//
//        if (resultList == null || resultList.size() <= 0) {
//            mSubmitButton.setEnabled(false);
//        } else {
//            updateTitle();
//            mSubmitButton.setEnabled(true);
//        }
//        mSubmitButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (resultList != null && resultList.size() > 0) {
//                    // 返回已选择的图片数据
//                    Intent data = new Intent();
//                    data.putStringArrayListExtra(EXTRA_RESULT, resultList);
//                    setResult(RESULT_OK, data);
//                    finish();
////                    Intent intent = new Intent(MultiImageSelectorActivity.this, CaptionEditActivity.class);
////                    intent.putExtra(CaptionEditActivity.EXTRA_SELECTED_LIST, resultList);
////                    Log.i("TAG","-----集合长度------->"+resultList.size());
////                    startActivityForResult(intent, REQUEST_PHOTO_EDIT_CODE);
//                }
//            }
//        });
//    }
//
//    @Override
//    public void onSingleImageSelected(String path) {
//        Intent data = new Intent();
//        resultList.add(path);
//        data.putStringArrayListExtra(EXTRA_RESULT, resultList);
//        setResult(RESULT_OK, data);
//        finish();
//    }
//
//    @Override
//    public void onImageSelected(String path) {
//        if (!resultList.contains(path)) {
//            resultList.add(path);
//        }
//        // 有图片之后，改变按钮状态
//        if (resultList.size() > 0) {
//            updateTitle();
//            if (!mSubmitButton.isEnabled()) {
//                mSubmitButton.setEnabled(true);
//            }
//        }
//    }
//
//    @Override
//    public void onImageUnselected(String path) {
//        if (resultList.contains(path)) {
//            resultList.remove(path);
//            updateTitle();
//        } else {
//            updateTitle();
//        }
//        // 当为选择图片时候的状态
//        if (resultList.size() == 0) {
//            mSubmitButton.setEnabled(false);
//        }
//    }
//
//    @Override
//    public void onCameraShot(File imageFile) {
//        if (imageFile != null) {
//            Intent data = new Intent();
//            resultList.add(imageFile.getAbsolutePath());
//            data.putStringArrayListExtra(EXTRA_RESULT, resultList);
//            setResult(RESULT_OK, data);
//            finish();
//        }
//    }
//
//    public void updateTitle() {
//        mTitleView.setText("已经选择 (" + resultList.size() + "/" + mDefaultCount + ") 张照片");
//        SpannableStringBuilder builder = new SpannableStringBuilder(mTitleView.getText().toString());
//        // ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
//        ForegroundColorSpan oraneSpan = new ForegroundColorSpan(getResources().getColor(R.color.orange));
//        builder.setSpan(oraneSpan, 6, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        mTitleView.setText(builder);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        List<String> photos = null;
//        if (resultCode == RESULT_OK) {
//            if (requestCode == REQUEST_PHOTO_EDIT_CODE) {
//                if (data != null) {
//                    photos = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
//                }
//                resultList.clear();
//                if (photos != null) {
//                    resultList.addAll(photos);
//                }
//                Intent theData = new Intent();
//                theData.putStringArrayListExtra(EXTRA_RESULT, resultList);
//                setResult(RESULT_OK, theData);
//                finish();
//            }
//        } else if (resultCode == RESULT_CANCELED) {
//            if (requestCode == REQUEST_PHOTO_EDIT_CODE) {
//                if (data != null) {
//                    photos = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
//                    Log.i("TAG","------相册长度------>"+photos.size());
//                }
//                resultList.clear();
//                if (photos != null) {
//                    resultList.addAll(photos);
//                }
//                updateTitle(); // 更新Title显示
//                EventBus.getDefault().post(new UpdateImageGridEvent(resultList));
//            }
//        }
//    }
//}

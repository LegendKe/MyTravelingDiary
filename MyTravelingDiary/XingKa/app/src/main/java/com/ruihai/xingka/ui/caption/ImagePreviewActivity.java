package com.ruihai.xingka.ui.caption;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ruihai.xingka.R;
import com.ruihai.xingka.ui.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by lqfang on 16/3/23.
 */
public class ImagePreviewActivity extends BaseActivity {

    public final static String EXTRA_PHOTOS = "photos";

    @BindView(R.id.tv_title)
    TextView mTitleView;
    @BindView(R.id.tv_right)
    TextView mRight;
    @BindView(R.id.image_preview)
    ImageView mPreview;

    private String mPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mTitleView.setText("图片预览");
        mRight.setVisibility(View.GONE);
        mPath = getIntent().getStringExtra(EXTRA_PHOTOS);
//        Log.i("TAG", "-----图片路径1----->" + mPath);
        Uri uri = Uri.parse(mPath);
        mPreview.setImageURI(uri);
    }


    @OnClick(R.id.tv_back)
    void onBack() {
        Intent intent = new Intent();
        intent.putExtra(ImagePreviewActivity.EXTRA_PHOTOS, mPath);
        setResult(RESULT_OK, intent);
        finish();
    }
}

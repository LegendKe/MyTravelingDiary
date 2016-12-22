package com.ruihai.xingka.ui.mine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ruihai.iconicfontengine.widget.IconicFontTextView;
import com.ruihai.xingka.R;
import com.ruihai.xingka.ui.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by apple on 15/8/28.
 */
public class ScanResultActivity extends BaseActivity {

    @BindView(R.id.tv_scanresult)
    TextView mScanResult;
    @BindView(R.id.tv_title)
    TextView mTitle;
    @BindView(R.id.tv_right)
    IconicFontTextView mRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanresult);
        ButterKnife.bind(this);
        mTitle.setText(R.string.scanresult);
        mRight.setVisibility(View.GONE);

        Intent intent = getIntent();
        String result = intent.getStringExtra("scanResult");
        mScanResult.setText(result);
    }

    @OnClick(R.id.tv_back)
    void onBack() {
        finish();
    }
}

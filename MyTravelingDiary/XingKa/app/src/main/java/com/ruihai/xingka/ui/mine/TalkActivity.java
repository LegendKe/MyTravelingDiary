package com.ruihai.xingka.ui.mine;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ruihai.iconicfontengine.widget.IconicFontTextView;
import com.ruihai.xingka.R;
import com.ruihai.xingka.ui.BaseActivity;
import com.ruihai.xingka.utils.AppUtility;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 我的说说
 * Created by lafang on 15/10/27.
 */
public class TalkActivity extends BaseActivity {
    private final String PREFERENCES_NAME = "talk";
    SharedPreferences sp, sp1;
    SharedPreferences.Editor editor;

    @BindView(R.id.tv_title)
    TextView mTitleView;
    @BindView(R.id.tv_right)
    IconicFontTextView mRightView;
    @BindView(R.id.et_mytalk)
    EditText mTalk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talk);
        ButterKnife.bind(this);
        initView();

    }

    private void initView() {
        mRightView.setVisibility(View.GONE);
        mTitleView.setText("我的说说");


        //存储修改的说说到本地
        sp = getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);
        mTalk.setText(sp.getString("talk", null));
        //光标在内容后面
        CharSequence text = mTalk.getText();
        if (text instanceof Spannable) {
            Spannable spanText = (Spannable) text;
            Selection.setSelection(spanText, text.length());
        }
    }

    @OnClick(R.id.tv_edit_complete)
    void OnComplete() {
        String mytalk = mTalk.getText().toString();
        if (TextUtils.isEmpty(mytalk)) {
            AppUtility.showToast("说说不能为空");
        } else if (mytalk.length() > 90) {
            AppUtility.showToast("说说长度不能超过90个字");
        } else {
            Intent intent = getIntent();
            intent.putExtra("talk", mytalk);
            // 设置该Activity的结果码，并设置结束之后退回的Activity
            setResult(100, intent); //intent为A传来的带有Bundle的intent，当然也可以自己定义新的Bundle
            finish();//此处一定要调用finish()方法

        }
    }

    @Override
    public void onStop() {
        super.onStop();
        sp1 = getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);
        editor = sp1.edit();

        String talk = mTalk.getText().toString();
        editor.putString("talk", talk);
        editor.commit();

    }

//    @OnClick(R.id.left)
//    void OnBack(){
//        finish();
//    }
}

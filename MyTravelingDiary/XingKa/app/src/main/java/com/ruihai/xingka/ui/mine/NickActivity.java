package com.ruihai.xingka.ui.mine;

import android.app.Activity;
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
 * 我的昵称
 * Created by lqfang on 15/10/27.
 */
public class NickActivity extends BaseActivity {

    private final String PREFERENCES_NAME = "nick";
    SharedPreferences sp, sp1;
    SharedPreferences.Editor editor;

    @BindView(R.id.right)
    IconicFontTextView mRight;
    @BindView(R.id.et_mynick)
    EditText mNick;
    @BindView(R.id.tv_title)
    TextView mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nick);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mRight.setVisibility(View.GONE);
        mTitle.setText("我的说说");

        //存储修改的说说到本地
        sp = getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);
        mNick.setText(sp.getString("nick", null));
        //光标在内容后面
        CharSequence text = mNick.getText();
        if (text instanceof Spannable) {
            Spannable spanText = (Spannable) text;
            Selection.setSelection(spanText, text.length());
        }
    }

    @OnClick(R.id.tv_edit_complete)
    void OnComplete() {
        String mynick = mNick.getText().toString();
        if (TextUtils.isEmpty(mynick)) {
            AppUtility.showToast("请输入昵称");
        } else if (mynick.length() > 10) {
            AppUtility.showToast("说说长度不能超过90个字");
        } else {

        }
    }

    @Override
    public void onStop() {
        super.onStop();
        sp1 = getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);
        editor = sp1.edit();

        String nick = mNick.getText().toString();
        editor.putString("nick", nick);
        editor.commit();

    }

    @OnClick(R.id.left)
    void OnBack() {
        finish();
    }
}

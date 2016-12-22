package com.ruihai.xingka.widget;

import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.flyco.animation.BaseAnimatorSet;
import com.flyco.dialog.widget.base.BottomBaseDialog;
import com.flyco.dialog.widget.base.BottomTopBaseDialog;
import com.flyco.dialog.widget.base.TopBaseDialog;
import com.ruihai.xingka.R;
import com.ruihai.xingka.ui.mine.EditUserDataActivity;
import com.ruihai.xingka.utils.AppUtility;

/**
 * TopBaseDialog
 * Created by lqfang on 15/10/22.
 */
public class MyNickDialog extends TopBaseDialog {
    private final OnMyNickDialogListener myNickDialogListener;
    private EditUserDataActivity context;
    private final String NICKNAME_LIMIT = "10个字符以内，可由中英文，数字、“－”、“_”组成";

    //定义回调事件，用于dialog的点击事件
    public interface OnMyNickDialogListener {
        public void nickName(String nick);
    }

    public MyNickDialog(EditUserDataActivity context, String myNick, OnMyNickDialogListener myNickDialogListener) {
        super(context);
        this.context = context;
        this.myNick = myNick;
        this.myNickDialogListener = myNickDialogListener;
    }

    public EditText mMyNick;
    TextView mEditComplete;
    TextView mTvLimit;
    String hint;
    String myNick;
    private int num = 10;//字数限制


    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    @Override
    public View onCreateView() {
        View view = View.inflate(context, R.layout.dialog_mynick, null);
        mMyNick = (EditText) view.findViewById(R.id.et_mytalk);
        mTvLimit = (TextView) view.findViewById(R.id.tv_nickname_limit);
        mEditComplete = (TextView) view.findViewById(R.id.tv_edit_complete);

        mTvLimit.setText(NICKNAME_LIMIT);
        //对EditText加监听
//        mMyNick.addTextChangedListener(new TextWatcher() {
//            private CharSequence temp;
//            private int selectionStart;
//            private int selectionEnd;
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                temp = s;
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                int number = num - s.length();
//                mTvCount.setText("还能输入" + number + "字");
//                // 输入的时候，只有一个光标，那么这两个值是相等的
//                selectionStart = mMyNick.getSelectionStart();
//                selectionEnd = mMyNick.getSelectionEnd();
//                if (temp.length() > num) {
//                    s.delete(selectionStart - 1, selectionEnd);
//                    int tempSelection = selectionEnd;
//                    mMyNick.setText(s);
//                    mMyNick.setSelection(tempSelection);//设置光标在最后
//                }
//            }
//        });

        mMyNick.setHint(hint);
        mMyNick.setText(myNick);

        //光标在内容后面
        CharSequence text = mMyNick.getText();
        if (text instanceof Spannable) {
            Spannable spanText = (Spannable) text;
            Selection.setSelection(spanText, text.length());
        }
        return view;
    }

    @Override
    public void setUiBeforShow() {

        mEditComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mynick = mMyNick.getText().toString().trim();
                //判断昵称是否修改
                if (TextUtils.isEmpty(mynick)) {
                    ProgressHUD.showInfoMessage(getContext(), "对不起,请输入您的昵称!");
//                    AppUtility.showToast("请输入昵称!");
                } else if (mynick.length() > 10) {
                    ProgressHUD.showInfoMessage(getContext(), "对不起,昵称不能超过10个字!");
//                    AppUtility.showToast("昵称不能超过10个字!");
                } else {
                    if (mynick.equals(myNick)) {
                        dismiss();
                    } else if (AppUtility.isNickNameOk(mynick)) {
//                        context.changeUserInfo("nick", mynick);
                        myNickDialogListener.nickName(mynick);
                        context.changeData();
                        dismiss();
                    } else {
                        ProgressHUD.showInfoMessage(getContext(), "对不起,你的昵称不符合要求!");
                    }
                }
            }
        });

    }

}

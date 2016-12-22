package com.ruihai.xingka.widget;

import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.flyco.dialog.widget.base.TopBaseDialog;
import com.ruihai.xingka.R;
import com.ruihai.xingka.ui.mine.EditUserDataActivity;
import com.ruihai.xingka.utils.AppUtility;

/**
 * Created by apple on 15/9/5.
 */
public class MyTalkDialog extends TopBaseDialog {
    private EditUserDataActivity context;


    //定义回调事件，用于dialog的点击事件
    public interface OnMyTalkDialogListener {
        public void myTalk(String talk);
    }

    public MyTalkDialog(EditUserDataActivity context, String myTalk, OnMyTalkDialogListener myTalkDialogListener) {
        super(context);
        this.context = context;
        this.myTalk = myTalk;
        this.myTalkDialogListener = myTalkDialogListener;
    }

    private OnMyTalkDialogListener myTalkDialogListener;

    EditText mMyTalk;
    TextView mEditComplete;
    TextView mTvCount;
    String hint;
    String myTalk;
    private int num = 40;//字数限制

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    @Override
    public View onCreateView() {
        View view = View.inflate(context, R.layout.dialog_mytalk, null);
        mMyTalk = (EditText) view.findViewById(R.id.et_mytalk);
        mTvCount = (TextView) view.findViewById(R.id.tv_count);
        mTvCount.setText("还能输入" + num + "字");
        //对EditText加监听
        mMyTalk.addTextChangedListener(new TextWatcher() {
            private CharSequence temp;
            private int selectionStart;
            private int selectionEnd;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                temp = s;
                int number = num - s.length();
                mTvCount.setText("还能输入" + number + "字");
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() < 40) {
                    // 输入的时候，只有一个光标，那么这两个值是相等的
                    selectionStart = mMyTalk.getSelectionStart();
                    selectionEnd = mMyTalk.getSelectionEnd();
                    if (temp.length() > num) {
                        s.delete(selectionStart - 1, selectionEnd);
                        int tempSelection = selectionEnd;
                        mMyTalk.setText(s);
                        mMyTalk.setSelection(tempSelection);//设置光标在最后
                    }
                }
//                int number = num - s.length();
//                mTvCount.setText("还能输入" + number + "字");
//                // 输入的时候，只有一个光标，那么这两个值是相等的
//                selectionStart = mMyTalk.getSelectionStart();
//                selectionEnd = mMyTalk.getSelectionEnd();
//                if (temp.length() > num) {
//                    s.delete(selectionStart - 1, selectionEnd);
//                    int tempSelection = selectionEnd;
//                    mMyTalk.setText(s);
//                    mMyTalk.setSelection(tempSelection);//设置光标在最后
//                }
            }
        });
        mMyTalk.setHint(hint);
        mMyTalk.setText(myTalk);
        //光标在内容后面
        CharSequence text = mMyTalk.getText();
        if (text instanceof Spannable) {
            Spannable spanText = (Spannable) text;
            Selection.setSelection(spanText, text.length());
        }

        mEditComplete = (TextView) view.findViewById(R.id.tv_edit_complete);
        mEditComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUiBeforShow();
            }
        });
        return view;
    }

    @Override
    public void setUiBeforShow() {

        mEditComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mytalk = mMyTalk.getText().toString().trim();
                myTalkDialogListener.myTalk(mytalk);

                if (mytalk.length() > 40) {
                    ProgressHUD.showErrorMessage(context, "一句话说说不能超过40个字!");
                } else if (mytalk.equals(myTalk)) {
                    dismiss();
                } else {
                    context.changeData();
                    dismiss();
                }

            }

        });

    }


}

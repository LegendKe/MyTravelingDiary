package com.ruihai.xingka.widget;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ruihai.iconicfontengine.widget.IconicFontTextView;
import com.ruihai.xingka.R;
import com.ruihai.xingka.api.model.User;
import com.ruihai.xingka.ui.mine.EditUserDataActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by apple on 15/8/19.
 */
public class SexDialog extends Dialog {

    //定义回调事件，用于dialog的点击事件
    public interface OnSexDialogListener {
        public void sex(int sex);
    }

    EditUserDataActivity mContext;
    int mSex;
    User mUser;

    public SexDialog(EditUserDataActivity context, int theme, int sex, User user, OnSexDialogListener sexDialogListener) {
        super(context, theme);
        this.mContext = context;
        this.mSex = sex;
        this.mUser = user;
        this.sexDialogListener = sexDialogListener;
    }

    @BindView(R.id.iv_male)
    IconicFontTextView mMale;
    @BindView(R.id.iv_female)
    IconicFontTextView mFemale;
    @BindView(R.id.iv_male_choose)
    ImageView mMaleChoose;
    @BindView(R.id.iv_female_choose)
    ImageView mFemaleChoose;

    private OnSexDialogListener sexDialogListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_sex);
        ButterKnife.bind(this);

        if (mSex == 2) {
            mMaleChoose.setVisibility(View.GONE);
            mFemaleChoose.setVisibility(View.VISIBLE);
        } else if (mSex == 1) {
            mFemaleChoose.setVisibility(View.GONE);
            mMaleChoose.setVisibility(View.VISIBLE);
        } else if (mSex == 0) {
            mMaleChoose.setVisibility(View.GONE);
            mFemaleChoose.setVisibility(View.GONE);
        }

    }

    @OnClick(R.id.iv_male)
    void chooseMale() {
//        sexDialogListener.sex(1);
//        mMaleChoose.setVisibility(View.VISIBLE);
//        mFemaleChoose.setVisibility(View.GONE);
        sexDialog(mUser.getSex(), 1);
//        mContext.changeData();
        dismiss();
    }

    @OnClick(R.id.iv_female)
    void chooseFeMale() {
//        mMaleChoose.setVisibility(View.GONE);
//        mFemaleChoose.setVisibility(View.VISIBLE);
//        sexDialogListener.sex(2);
        sexDialog(mUser.getSex(), 2);
//        mContext.changeData();
        dismiss();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        dismiss();
        return true;
    }

    //选择性别弹出框
    private void sexDialog(final int sex, final int flag) {
        String mSex = "";
        if (flag == 1) {
            mSex = "您选择的性别是男";
        } else if (flag == 2) {
            mSex = "您选择的性别是女";
        }

        // 1. 布局文件转换为View对象
        LayoutInflater inflater = LayoutInflater.from(mContext);
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.dialog_choose_sex, null);
        final Dialog dialog = new AlertDialog.Builder(mContext).create();
        dialog.setCancelable(false);
        dialog.show();
        dialog.getWindow().setContentView(layout);
        //2.标题
        TextView dialog_title = (TextView) layout.findViewById(R.id.tv_title);
//        dialog_title.setTextColor(getResources().getColor(R.color.black));
        dialog_title.setVisibility(View.VISIBLE);
        dialog_title.setText(mSex);
        // 3. 消息内容
        //显示内容
        TextView dialog_msg = (TextView) layout.findViewById(R.id.umeng_update_content);
        dialog_msg.setText("确认性别后不能再修改");
//        dialog_msg.setVisibility(View.GONE);

        // 4. 确定按钮
        Button btnOK = (Button) layout.findViewById(R.id.umeng_update_id_ok);
        btnOK.setText("确定");
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag == 1) {
                    sexDialogListener.sex(1);
                    mMaleChoose.setVisibility(View.VISIBLE);
                    mFemaleChoose.setVisibility(View.GONE);
                } else if (flag == 2) {
                    mMaleChoose.setVisibility(View.GONE);
                    mFemaleChoose.setVisibility(View.VISIBLE);
                    sexDialogListener.sex(2);
                }
                mContext.changeData();
                dialog.dismiss();
            }
        });

        // 5. 取消按钮
        Button btnCancel = (Button) layout.findViewById(R.id.umeng_update_id_cancel);
        btnCancel.setText("取消");
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

}
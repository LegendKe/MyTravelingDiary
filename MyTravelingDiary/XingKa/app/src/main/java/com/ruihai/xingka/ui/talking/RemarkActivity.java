package com.ruihai.xingka.ui.talking;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.flyco.animation.BaseAnimatorSet;
import com.orhanobut.logger.Logger;
import com.ruihai.iconicfontengine.widget.IconicFontTextView;
import com.ruihai.xingka.R;
import com.ruihai.xingka.api.ApiModule;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.api.model.MyFriendInfoRepo;
import com.ruihai.xingka.api.model.XKRepo;
import com.ruihai.xingka.ui.BaseActivity;
import com.ruihai.xingka.utils.Security;
import com.ruihai.xingka.widget.ProgressHUD;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 好友备注
 * Created by lqfang on 15/10/19.
 */
public class RemarkActivity extends BaseActivity {

    public static void launch(Activity from, String account, String remark) {
        Intent intent = new Intent(from, RemarkActivity.class);
        intent.putExtra("account", account);
        intent.putExtra("remark", remark);
        from.startActivity(intent);
    }

    @BindView(R.id.tv_title)
    TextView mTitleView;
    @BindView(R.id.tv_right)
    IconicFontTextView mRightView;
    @BindView(R.id.edit_remark)
    EditText mRemark;

    private String mUserAccount;//当前名片的行咖号
    private String myAccount;//我的行咖号
    private String remark;//修改备注


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_remark);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mTitleView.setText(R.string.mine_friend_remark);
        mRightView.setVisibility(View.VISIBLE);
        //获取当前名片行咖号
        mUserAccount = getIntent().getStringExtra("account");
        //获取备注名
        remark = getIntent().getStringExtra("remark");
        //获取我的行咖号
        myAccount = String.valueOf(AccountInfo.getInstance().loadAccount().getAccount());

        //光标在内容后面
        CharSequence text = mRemark.getText();
        if (text instanceof Spannable) {
            Spannable spanText = (Spannable) text;
            Selection.setSelection(spanText, text.length());
        }

        //判断是否有备注名
        if(!TextUtils.isEmpty(remark) || remark != null){
            mRemark.setText(remark);
        }

    }

    public void doRemark() {
        remark = mRemark.getText().toString().trim();
        String accid = Security.aesEncrypt(myAccount);
        String faccid = Security.aesEncrypt(mUserAccount);
        String alias = Security.aesEncrypt(String.valueOf(remark));

        ApiModule.apiService_1().addIMFriendRemark(accid, faccid, alias).enqueue( new Callback<XKRepo>() {
            @Override
            public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                XKRepo xkRepo = response.body();
                String msg = xkRepo.getMsg();
                if (xkRepo.isSuccess()) {
                    ProgressHUD.showSuccessMessage(RemarkActivity.this, "修改备注成功");
                    Intent intent = new Intent();
                    intent.putExtra("remarkIM", remark);
                    setResult(RESULT_OK, intent);
                    finish();
                }else {
                    ProgressHUD.showInfoMessage(mContext, msg);
                }
            }

            @Override
            public void onFailure(Call<XKRepo> call, Throwable t) {
                ProgressHUD.showErrorMessage(mContext, getString(R.string.common_network_error));
            }
        });
    }

    @OnClick(R.id.tv_right)
    void onComplete(){
        doRemark();
    }

    @OnClick(R.id.tv_back)
    void onBack() {
        finish();
    }

}

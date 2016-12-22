package com.ruihai.xingka.ui.mine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.ruihai.iconicfontengine.widget.IconicFontTextView;
import com.ruihai.xingka.R;
import com.ruihai.xingka.ui.BaseActivity;
import com.ruihai.xingka.ui.mine.adapter.AccountSecurityAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

/**
 * 账号安全
 * Created by apple on 15/8/18.
 */
public class AccountSecurityActivity extends BaseActivity {

    @BindView(R.id.lv_first)
    ListView mLvFirst;
    @BindView(R.id.lv_second)
    ListView mLvSecond;
    @BindView(R.id.tv_title)
    TextView mTitle;
    @BindView(R.id.tv_right)
    IconicFontTextView mRight;

    private static final int first_part = 1;
    private static final int second_part = 2;
    /**
     * 注释的为后期开发需要的功能
     */
//    String[] data_first = {"账号安全等级", "修改密码"};
//    String[] data_second = {"手机绑定"};
//    String[] data_second = {"手机绑定", "微信", "QQ", "新浪微博"};
    String[] data_first = {"修改密码", "更换手机"};

    int[] pic_first = {R.mipmap.change_password, R.mipmap.change_phone};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_security);

        ButterKnife.bind(this);
        mTitle.setText(R.string.account_security);
        mRight.setVisibility(View.GONE);
        //pic_first
        AccountSecurityAdapter adapter_first = new AccountSecurityAdapter(this, data_first, pic_first, first_part);
        //pic_second
        //AccountSecurityAdapter adapter_second = new AccountSecurityAdapter(this, data_second,pic_second, second_part);

        mLvFirst.setAdapter(adapter_first);
//        mLvSecond.setAdapter(adapter_second);
    }

    @OnClick(R.id.tv_back)
    void onBack(View view) {
        finish();
    }

    @OnItemClick(R.id.lv_first)
    void onFirstItemClik(int position) {
        if (position == 0) {
            Intent intent = new Intent();
            intent.setClass(this, ChangePasswordActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent();
            intent.setClass(this, ChangeMobileActivity.class);
            startActivity(intent);
        }
    }

//    @OnItemClick(R.id.lv_second)
//    void onSecondItemClik(int position) {
//        switch (position) {
//            case 0:
//                Intent intent = new Intent();
//                intent.setClass(this, ChangeMobileActivity.class);
//                startActivity(intent);
//                break;
//            case 1:
//
//                break;
//            case 2:
//
//                break;
//            case 3:
//
//                break;
//            default:
//                break;
//        }
//    }


}

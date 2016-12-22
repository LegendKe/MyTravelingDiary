package com.ruihai.xingka.ui.mine;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.widget.EditText;
import android.widget.TextView;

import com.ruihai.xingka.R;
import com.ruihai.xingka.ui.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by apple on 15/9/8.
 */
public class InviteFriendsActivity extends BaseActivity {

    @BindView(R.id.et_invite_message)
    EditText mInviteMessage;
    @BindView(R.id.tv_addressee_name)
    TextView mAddresseeName;

    String addresseePhone;
    String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friends);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        String addresseeName = intent.getStringExtra("contactName");
        addresseePhone = intent.getStringExtra("contactPhone");
        mAddresseeName.setText(addresseeName);
        SpannableString  inviteMessage = new SpannableString(this.getResources().getString(R.string.mine_send_message));
        message = this.getResources().getString(R.string.mine_send_message);
        inviteMessage.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 54, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        inviteMessage.setSpan(new URLSpan("http://www.xingka.cc"), 54, 74,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        inviteMessage.setSpan(new ForegroundColorSpan(Color.BLACK), 74, 88, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mInviteMessage.setText(inviteMessage);
        mInviteMessage.setMovementMethod(LinkMovementMethod.getInstance());

    }

    @OnClick(R.id.tv_back)
    void onBack() {
        finish();
    }

    @OnClick(R.id.btn_send)
    void onSend() {
        sendMsg(addresseePhone,message);
    }

    private void sendMsg(String phone,String inviteMessage) {
        Uri smsToUri = Uri.parse("smsto:"+phone);
        Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
        intent.putExtra("sms_body", inviteMessage);
        startActivity(intent);
    }
}

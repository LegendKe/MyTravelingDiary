package com.ruihai.xingka.ui.talking;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.netease.nim.uikit.common.activity.UI;
import com.ruihai.xingka.R;
import com.ruihai.xingka.ui.BaseActivity;
import com.ruihai.xingka.ui.talking.fragment.TalkingFragment;

/**
 * Created by lqfang on 16/8/3.
 */
public class ChatActivity extends UI{

    public static void launch(Context context) {
        Intent intent = new Intent(context, ChatActivity.class);
//        intent.putExtra("userAccount", userAccount);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);


    }

}

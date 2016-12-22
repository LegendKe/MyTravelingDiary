package com.ruihai.xingka.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.api.model.DraftBoxItem;
import com.ruihai.xingka.ui.MainActivity;
import com.ruihai.xingka.ui.caption.publisher.PublishManager;

/**
 * 我的自定义广播接收器
 * Created by gjzhang on 16/1/8.
 */


public class MyReceiver extends BroadcastReceiver {

    @Override

    public void onReceive(Context context, Intent intent) {
        //Bundle bundle = intent.getExtras();

        if (PublishManager.ACTION_PUBLISH_CHANNGED.equals(intent.getAction())) {//图说发布成功
            // AccountInfo.getInstance().clearUserDraftBoxInfo(AccountInfo.getInstance().getUserDraftBoxInfo());//清空草稿箱

        } else if (PublishManager.ACTION_PUBLISH_FAILED.equals(intent.getAction())) {//图说发布失败
            AccountInfo.getInstance().saveUserDraftBoxInfo((DraftBoxItem) intent.getSerializableExtra("PUT_DRAFTBOX"));

        }
    }
}

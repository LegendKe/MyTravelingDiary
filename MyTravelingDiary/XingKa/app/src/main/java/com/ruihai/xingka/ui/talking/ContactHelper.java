package com.ruihai.xingka.ui.talking;

import android.content.Context;

import com.netease.nim.uikit.NimUIKit;
import com.netease.nim.uikit.contact.ContactEventListener;
import com.ruihai.xingka.ui.mine.UserProfileActivity;

/**
 * UIKit联系人列表定制展示类
 * <p/>
 * Created by huangjun on 2015/9/11.
 */
public class ContactHelper {

    public static void init() {
        setContactEventListener();
    }

    private static void setContactEventListener() {
        NimUIKit.setContactEventListener(new ContactEventListener() {
            @Override
            public void onItemClick(Context context, String account) {
//                UserProfileActivity.start(context, account);
                UserProfileActivity.launch(context, account, 1, 0);
            }

            @Override
            public void onItemLongClick(Context context, String account) {

            }

            @Override
            public void onAvatarClick(Context context, String account) {
//                UserProfileActivity.start(context, account);
                UserProfileActivity.launch(context, account, 1, 0);
            }
        });
    }

}

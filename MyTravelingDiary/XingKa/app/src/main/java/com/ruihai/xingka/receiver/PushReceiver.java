package com.ruihai.xingka.receiver;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.gson.Gson;
import com.orhanobut.hawk.Hawk;
import com.orhanobut.logger.Logger;
import com.ruihai.xingka.R;
import com.ruihai.xingka.api.ApiModule;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.api.model.MsgType;
import com.ruihai.xingka.api.model.UnreadMessage;
import com.ruihai.xingka.api.model.User;
import com.ruihai.xingka.api.model.XKRepo;
import com.ruihai.xingka.notifier.UnreadMessageNotifier;
import com.ruihai.xingka.ui.MainActivity;
import com.ruihai.xingka.ui.login.LoginActivity;
import com.ruihai.xingka.ui.mine.ClassifyMessageActivity;
import com.ruihai.xingka.ui.mine.MyMessageActivity;
import com.ruihai.xingka.utils.Security;

import cn.jpush.android.api.JPushInterface;
import de.greenrobot.event.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lqfang on 15/10/10.
 * 自定义接收器
 * 如果不定义这个 PushReceiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class PushReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Logger.d("[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Logger.d("[MyReceiver] 接收Registration Id : " + regId);
            //send the Registration Id to your server...
            updateDeviceToken(regId);
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            Logger.d("[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
            processCustomMessage(context, bundle);

        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Logger.d("[MyReceiver] 接收到推送下来的通知");
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            Logger.d("[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);

        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Logger.d("[MyReceiver] 用户点击打开了通知");
            //打开自定义的Activity
            Intent i = new Intent(context, MainActivity.class);
//            i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setAction(MainActivity.ACTION_NOTIFICATION);
            //i.putExtras(bundle);
            intent.putExtra("launchSplash", false);
            i.putExtra("type", 6);
            MsgType msgType = new MsgType(6);
            EventBus.getDefault().post(msgType);
            context.startActivity(i);
        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            Logger.d("[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等...
        } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
            boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
            Logger.w("[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
        } else {
            Logger.d("[MyReceiver] Unhandled intent - " + intent.getAction());
        }
    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }


    //send msg to MainActivity
    private void processCustomMessage(Context context, Bundle bundle) {
        String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);

        Gson gson = new Gson();
        UnreadMessage unreadMessage = gson.fromJson(extras, UnreadMessage.class);
        UnreadMessageNotifier unreadMessageNotifier = new UnreadMessageNotifier(context);
        unreadMessageNotifier.notifyUnreadMessage(unreadMessage, message);
    }

    /**
     * 更新设备令牌值
     *
     * @param regId JPush Registration Id
     */
    private void updateDeviceToken(final String regId) {
        Hawk.put("deviceToken", regId);
        // 判断用户是否登录,如果没有登录,则保存DeviceToken值,待用户登录时再进行提交
        if (AccountInfo.getInstance().isLogin()) {
            User currentUser = AccountInfo.getInstance().loadAccount();
            String sAccount = Security.aesEncrypt(String.valueOf(currentUser.getAccount()));
            String sToken = Security.aesEncrypt(regId);
            Call<XKRepo> call = ApiModule.apiService().editDeviceToken(sAccount, sToken);
            call.enqueue(new Callback<XKRepo>() {
                @Override
                public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                    XKRepo xkRepo = response.body();
                    if (xkRepo.isSuccess()) {
                        Logger.d("更新DeviceToken成功" + regId);
                    } else {
                        Logger.d("更新DeviceToken失败" + regId);
                    }
                }

                @Override
                public void onFailure(Call<XKRepo> call, Throwable t) {
                    Logger.d(t.getLocalizedMessage());
                }

            });
        }
    }
}

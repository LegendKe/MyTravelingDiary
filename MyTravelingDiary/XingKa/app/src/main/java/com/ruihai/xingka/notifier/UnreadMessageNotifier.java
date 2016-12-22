package com.ruihai.xingka.notifier;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.ruihai.xingka.AppSettings;
import com.ruihai.xingka.R;
import com.ruihai.xingka.XKApplication;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.api.model.MsgType;
import com.ruihai.xingka.api.model.UnreadMessage;
import com.ruihai.xingka.api.model.User;
import com.ruihai.xingka.event.DisableUser;
import com.ruihai.xingka.ui.MainActivity;
import com.ruihai.xingka.ui.login.LoginActivity;
import com.ruihai.xingka.widget.ProgressHUD;

import java.util.List;
import java.util.StringTokenizer;

import de.greenrobot.event.EventBus;

/**
 * Created by zecker on 15/10/31.
 */
public class UnreadMessageNotifier extends Notifier {

    private EventBus eventBus = EventBus.getDefault();

    public UnreadMessageNotifier(Context context) {
        super(context);
    }

    public void notifyUnreadMessage(UnreadMessage unreadMessage, String message) {
        String fromXingka = context.getString(R.string.notifer_from_xingka);
        String readDetail = "点击查看";
        int pushType = unreadMessage.getPushType();
        if (AppSettings.isNotifyMention() && pushType == 1 || AppSettings.isNotifyPraise() && pushType == 21) { // 被@的推送
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setSmallIcon(R.mipmap.statusbar_ic_gray_logo)
                    .setContentTitle(message)
                    .setContentText(fromXingka);

            Intent intent = new Intent(context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setAction(MainActivity.ACTION_NOTIFICATION);
            intent.putExtra("type", 1);
            PendingIntent contentIntent = PendingIntent.getActivity(context, RemindUnreadForMentionCaption, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            builder.setContentIntent(contentIntent).setAutoCancel(true);
            builder.setTicker(message);
            MsgType msgType = new MsgType(1);
            eventBus.post(msgType);
            notify(RemindUnreadForMentionCaption, builder);
        } else if (AppSettings.isNotifyMention() && pushType == 2 || AppSettings.isNotifyPraise() && pushType == 22) { // 被@的推送
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder
                    .setSmallIcon(R.mipmap.statusbar_ic_gray_logo)
                    .setContentTitle(message)
                    .setContentText(fromXingka);

            Intent intent = new Intent(context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setAction(MainActivity.ACTION_NOTIFICATION);
            intent.putExtra("type", 1);
            PendingIntent contentIntent = PendingIntent.getActivity(context, RemindUnreadForMentionComments, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            builder.setContentIntent(contentIntent).setAutoCancel(true);
            builder.setTicker(message);
            MsgType msgType = new MsgType(1);
            eventBus.post(msgType);
            notify(RemindUnreadForMentionComments, builder);
        } else if (AppSettings.isNotifyComment() && pushType == 3 || AppSettings.isNotifyPraise() && pushType == 23) { // 被评论的推送
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setSmallIcon(R.mipmap.statusbar_ic_gray_logo)
                    .setContentTitle(message)
                    .setContentText(readDetail);
            // .setContentText(unreadMessage.getPcContent());

            Intent intent = new Intent(context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setAction(MainActivity.ACTION_NOTIFICATION);
            intent.putExtra("type", 2);
            PendingIntent contentIntent = PendingIntent.getActivity(context, RemindUnreadComments, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            builder.setContentIntent(contentIntent).setAutoCancel(true);
            builder.setTicker(message);
            MsgType msgType = new MsgType(2);
            eventBus.post(msgType);
            notify(RemindUnreadComments, builder);
        } else if (AppSettings.isNotifyComment() && pushType == 4 || AppSettings.isNotifyPraise() && pushType == 24) { // 被评论的推送
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setSmallIcon(R.mipmap.statusbar_ic_gray_logo)

                    .setContentTitle(message)
                    .setContentText(readDetail);
            //.setContentText(unreadMessage.getPcContent());

            Intent intent = new Intent(context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setAction(MainActivity.ACTION_NOTIFICATION);
            intent.putExtra("type", 2);
            PendingIntent contentIntent = PendingIntent.getActivity(context, RemindUnreadReplyComment, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            builder.setContentIntent(contentIntent).setAutoCancel(true);
            builder.setTicker(message);
            MsgType msgType = new MsgType(2);
            eventBus.post(msgType);
            notify(RemindUnreadReplyComment, builder);
        } else if (AppSettings.isNotifyPraise() && pushType == 5 || AppSettings.isNotifyPraise() && pushType == 25) { // 被赞的推送
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setSmallIcon(R.mipmap.statusbar_ic_gray_logo)
                    .setContentTitle(message)
                    .setContentText(fromXingka);

            Intent intent = new Intent(context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setAction(MainActivity.ACTION_NOTIFICATION);
            intent.putExtra("type", 3);
            PendingIntent contentIntent = PendingIntent.getActivity(context, RemindUnreadForPariseCaption, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            builder.setContentIntent(contentIntent).setAutoCancel(true);
            builder.setTicker(message);
            MsgType msgType = new MsgType(3);
            eventBus.post(msgType);
            notify(RemindUnreadForPariseCaption, builder);
        } else if (AppSettings.isNotifyPraise() && pushType == 6 || AppSettings.isNotifyPraise() && pushType == 26) { // 被评论赞的推送
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setSmallIcon(R.mipmap.statusbar_ic_gray_logo)
                    .setContentTitle(message)
                    .setContentText(fromXingka);

            Intent intent = new Intent(context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setAction(MainActivity.ACTION_NOTIFICATION);
            intent.putExtra("type", 3);
            PendingIntent contentIntent = PendingIntent.getActivity(context, RemindUnreadForPariseComments, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            builder.setContentIntent(contentIntent).setAutoCancel(true);
            builder.setTicker(message);
            MsgType msgType = new MsgType(3);
            eventBus.post(msgType);
            notify(RemindUnreadForPariseComments, builder);
        } else if (AppSettings.isNotifyPraise() && pushType == 7 || AppSettings.isNotifyPraise() && pushType == 27) { // 被收藏的推送
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setSmallIcon(R.mipmap.statusbar_ic_gray_logo)
                    .setContentTitle(message)
                    .setContentText(fromXingka);

            Intent intent = new Intent(context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setAction(MainActivity.ACTION_NOTIFICATION);
            intent.putExtra("type", 4);
            PendingIntent contentIntent = PendingIntent.getActivity(context, RemindUnreadForCollectionCaption, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            builder.setContentIntent(contentIntent).setAutoCancel(true);
            builder.setTicker(message);
            MsgType msgType = new MsgType(4);
            eventBus.post(msgType);
            notify(RemindUnreadForPariseComments, builder);
        } else if (AppSettings.isNotifyFollower() && pushType == 8) {  // 被关注的推送
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setSmallIcon(R.mipmap.statusbar_ic_gray_logo)
                    .setContentTitle(message)
                    .setContentText(fromXingka);


            Intent intent = new Intent(context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setAction(MainActivity.ACTION_NOTIFICATION);
            intent.putExtra("type", 5);
            PendingIntent contentIntent = PendingIntent.getActivity(context, RemindUnreadForFollowers, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            builder.setContentIntent(contentIntent).setAutoCancel(true);
            builder.setTicker(message);
            MsgType msgType = new MsgType(5);
            eventBus.post(msgType);
            notify(RemindUnreadForFollowers, builder);
        } else if (AppSettings.isNotifyFollower() && pushType == 11) {// 被推荐的推送
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setSmallIcon(R.mipmap.statusbar_ic_gray_logo)
                    .setContentTitle(message)
                    .setContentText(fromXingka);

            Intent intent = new Intent(context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setAction(MainActivity.ACTION_NOTIFICATION);
            intent.putExtra("type", 5);
            PendingIntent contentIntent = PendingIntent.getActivity(context, RemindUnreadRecommend, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            builder.setContentIntent(contentIntent).setAutoCancel(true);
            builder.setTicker(message);
            MsgType msgType = new MsgType(5);
            eventBus.post(msgType);
            notify(RemindUnreadForFollowers, builder);
        } else if (pushType == 10) { // 被后台禁用强制退出登录
            if (isRunningInForeground()) { // 在前台
//                ProgressHUD.showInfoMessage(context, "您的账号涉嫌违规操作,已被后台禁用,如有问题请联系客服QQ:2015686190");
                // Post the event

                eventBus.post(new DisableUser());
            } else { // 在后台,直接退出登录
                logout();
            }

        }
    }

    private void notify(int request, NotificationCompat.Builder builder) {
        Notification notification = builder.build();

        if (AppSettings.isNotifyVibrate()) {
            notification.defaults = 0;
            notification.vibrate = vibrate;
        }
        if (AppSettings.isNotifyLED()) {
//				notification.defaults = Notification.DEFAULT_LIGHTS;
            notification.flags = notification.flags | Notification.FLAG_SHOW_LIGHTS;
//				notification.ledARGB = Color.parseColor(AppSettings.getThemeColor());
            notification.ledARGB = Color.BLUE;
            notification.ledOffMS = 200;
            notification.ledOnMS = 700;
        }
        if (AppSettings.isNotifySound()) {
            notification.defaults = notification.defaults | Notification.DEFAULT_SOUND;
        }

        notify(request, notification);
    }

    protected boolean isRunningInForeground() {
        ActivityManager manager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = manager.getRunningTasks(1);
        if (tasks.isEmpty()) {
            return false;
        }
        String topActivityName = tasks.get(0).topActivity.getPackageName();
        return topActivityName.equalsIgnoreCase(context.getPackageName());
    }

    private void logout() {
        AccountInfo.getInstance().clearAccount();
        XKApplication.getInstance().exit();
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

}

package com.ruihai.xingka.ui.trackway.publisher;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.orhanobut.logger.Logger;
import com.ruihai.xingka.AppSettings;
import com.ruihai.xingka.R;
import com.ruihai.xingka.XKApplication;
import com.ruihai.xingka.notifier.Notifier;
import com.ruihai.xingka.ui.caption.publisher.bean.PublishBean;
import com.ruihai.xingka.ui.caption.publisher.bean.PublishType;
import com.ruihai.xingka.ui.trackway.TrackWayAddActivity;
import com.ruihai.xingka.ui.trackway.publisher.service.PublishService;

/**
 * Created by zecker on 15/10/2.
 */
public class PublishNotifier extends Notifier {

    private final String TAG = "PublishNotifier";

    public PublishNotifier(Context context) {
        super(context.getApplicationContext());
    }

    public void notifyTimingPublish(PublishBean bean) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        builder.setSmallIcon(R.mipmap.statusbar_ic_sending)
                .setContentTitle(context.getString(R.string.notifer_timging_draft))
                .setAutoCancel(true)
                .setContentText(bean.getText())
                .setTicker(context.getString(R.string.notifer_timging_draft));

        notify(121212, 5, builder);

        // 延迟一秒后取消通知
        XKApplication.getInstance().getHandler().removeCallbacks(removeTimingPublishRunnable);
        XKApplication.getInstance().getHandler().postDelayed(removeTimingPublishRunnable, 1000);
    }

    private Runnable removeTimingPublishRunnable = new Runnable() {

        @Override
        public void run() {
            cancelNotification(121212);
        }
    };

    public void notifyPrePublish(PublishBean bean) {
        Logger.d(TAG, "notifyPrePublish-" + bean.getText());

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.mipmap.statusbar_ic_sending).setContentTitle(context.getString(R.string.notifer_cancel_publish))
                .setContentText(getContentText(bean))
                .setTicker(getContentTitle(bean, 0));

        Intent intent = new Intent(XKApplication.getInstance(), PublishService.class);
        intent.setAction("com.ruihai.xingka.Cancel");
        PendingIntent contentIntent = PendingIntent.getService(XKApplication.getInstance(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(contentIntent).setAutoCancel(true);

        Logger.d(TAG, "notify PrePublish");
        notify(getRequest(bean), 0, builder);
    }

    public void notifyPublishCancelled(final PublishBean bean) {
        Logger.d(TAG, "notifyPublishCancelled-" + bean.getText());

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        builder.setSmallIcon(R.mipmap.statusbar_ic_send_fail).setContentTitle(getContentTitle(bean, 4)).setAutoCancel(true)
                .setContentText(getContentText(bean)).setTicker(getContentTitle(bean, 4));

        Logger.d(TAG, "notify canceledPublish");
        notify(1212, 4, builder);

        // 延迟一秒后取消通知
        XKApplication.getInstance().getHandler().postDelayed(new Runnable() {

            @Override
            public void run() {
                cancelNotification(1212);
            }
        }, 1000);
    }

    public void notifyPublishing(PublishBean bean) {
        Logger.d(TAG, "notifyPublishing-" + bean.getText());

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        builder.setSmallIcon(R.mipmap.statusbar_ic_sending).setContentTitle(getContentTitle(bean, 1))
                .setContentText(getContentText(bean)).setTicker(getContentTitle(bean, 1));

        Logger.d(TAG, "notify Publishing");
        notify(getRequest(bean), 1, builder);
    }

    public void notifyPublishFaild(PublishBean bean, String errorMsg) {
        Logger.d(TAG, "notifyPublishFaild-" + bean.getText());

        Intent intent = new Intent(context, TrackWayAddActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //intent.setAction(MainActivity.ACTION_NOTIFICATION);
        intent.putExtra("type", "6");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        builder.setSmallIcon(R.mipmap.statusbar_ic_send_fail).setContentTitle(getContentTitle(bean, 2))
                .setContentText(errorMsg).setTicker(getContentTitle(bean, 2));
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(contentIntent).setAutoCancel(true);

        Logger.d(TAG, "notify PublishFaild");
        notify(getRequest(bean), 2, builder);
    }

    public void notifyPublishSuccess(final PublishBean bean) {
        Logger.d(TAG, "notifyPublishSuccess-" + bean.getText());

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        builder.setSmallIcon(R.mipmap.statusbar_ic_send_success).setContentTitle(getContentTitle(bean, 3)).setAutoCancel(true)
                .setContentText(getContentText(bean)).setTicker(getContentTitle(bean, 3));

        Logger.d(TAG, "notify PublishSuccess");
        notify(getRequest(bean), 3, builder);

        // 延迟一秒后取消通知
        XKApplication.getInstance().getHandler().postDelayed(new Runnable() {

            @Override
            public void run() {
                cancelNotification(getRequest(bean));
            }
        }, 1000);
    }

    private int getRequest(PublishBean bean) {
        if (bean.getType() == PublishType.caption)
            return PublishCaptionNotificationRequest;
        return -1;
    }

    private String getContentText(PublishBean bean) {
        return bean.getText();
    }

    private String getContentTitle(PublishBean bean, int status) {
        String text = null;

        switch (bean.getType()) {
            // 图说
            case caption:
                switch (status) {
                    // 准备发布
                    case 0:
                        text = String.format(context.getString(R.string.travel_together_notifer_send_delay), String.valueOf(bean.getDelay() / 1000));
                        break;
                    // 正在发布
                    case 1:
                        text = context.getString(R.string.travel_togethe_notifer_status_sending);
                        break;
                    // 发布成功
                    case 3:
                        text = context.getString(R.string.travel_togethe_notifer_send_success);
                        break;
                    // 发布失败
                    case 2:
                        text = context.getString(R.string.travel_togethe_notifer_send_faild);
                        break;
                    // 取消发布
                    case 4:
                        text = context.getString(R.string.travel_togethe_notifer_send_cancel_save_draft);
                        break;
                }
                break;
        }

        return text;
    }

    private void notify(int request, int status, NotificationCompat.Builder builder) {
        Notification notification = builder.build();

        if (status > 1 && AppSettings.isSendVibrate()) {
//			notification.defaults |= Notification.DEFAULT_VIBRATE;
            notification.vibrate = vibrate;
        }
        notify(request, notification);
    }
}

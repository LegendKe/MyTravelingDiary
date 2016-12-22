package com.ruihai.xingka.notifier;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

import com.ruihai.xingka.XKApplication;

/**
 * Created by zecker on 15/10/2.
 */
public abstract class Notifier {

    final protected Context context;
    final private NotificationManager notificationManager;

    public static final long[] vibrate = new long[]{0, 150, 100, 250};

    public static final int PublishCaptionNotificationRequest = 10000; // 新图说

    public static final int RemindUnreadForMentionCaption = 1000; // 新提及图说
    public static final int RemindUnreadForMentionComments = 2000; // 新提及评论
    public static final int RemindUnreadComments = 3000; // 新评论
    public static final int RemindUnreadReplyComment = 4000; // 回复评论
    public static final int RemindUnreadForPariseCaption = 5000; // 图说点赞
    public static final int RemindUnreadForPariseComments = 6000; // 图说评论点赞
    public static final int RemindUnreadForCollectionCaption = 7000; // 图说收藏点赞
    public static final int RemindUnreadForFollowers = 8000; // 新粉丝
    public static final int RemindUnreadRecommend = 11000; // 推荐

    public static final int PublishTrackwayNotificationRequest = 210000; // 新旅拼

    public static final int RemindUnreadForMentionTrackway = 21000; // 新提及旅拼
    public static final int RemindUnreadForMentionTrackwayComments = 22000; // 新提及评论
    public static final int RemindUnreadTrackwayComments = 23000; // 新评论
    public static final int RemindUnreadTrackwayReplyComment = 24000; // 回复评论
    public static final int RemindUnreadForPariseTrackway = 25000; // 旅拼点赞
    public static final int RemindUnreadForPariseTrackwayComments = 26000; // 旅拼评论点赞
    public static final int RemindUnreadForCollectionTrackway = 27000; // 旅拼收藏点赞



    public Notifier(Context context) {
        this.context = context;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    final protected void notify(int id, Notification notification) {
        notificationManager.notify(id, notification);
    }

    final public void cancelNotification(int request) {
        notificationManager.cancel(request);
    }

    final public static void cancelAll() {
        NotificationManager notificationManager = (NotificationManager) XKApplication.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.cancel(PublishCaptionNotificationRequest);
        notificationManager.cancel(RemindUnreadForMentionCaption);
        notificationManager.cancel(RemindUnreadForMentionComments);
        notificationManager.cancel(RemindUnreadReplyComment);
        notificationManager.cancel(RemindUnreadForFollowers);
        notificationManager.cancel(RemindUnreadComments);
        notificationManager.cancel(RemindUnreadForPariseCaption);
        notificationManager.cancel(RemindUnreadForPariseComments);
        notificationManager.cancel(RemindUnreadForCollectionCaption);
        notificationManager.cancel(RemindUnreadRecommend);
    }

}

package com.ruihai.xingka;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Handler;
import android.support.multidex.MultiDex;
import android.text.TextUtils;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.netease.nim.uikit.ImageLoaderKit;
import com.netease.nim.uikit.NimUIKit;
import com.netease.nim.uikit.cache.FriendDataCache;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.cache.TeamDataCache;
import com.netease.nim.uikit.contact.ContactProvider;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.SDKOptions;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.msg.MessageNotifierCustomization;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.team.constant.TeamFieldEnum;
import com.netease.nimlib.sdk.team.model.IMMessageFilter;
import com.netease.nimlib.sdk.team.model.UpdateTeamAttachment;
import com.netease.nimlib.sdk.uinfo.UserInfoProvider;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.orhanobut.hawk.Hawk;
import com.orhanobut.hawk.HawkBuilder;
import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;
import com.rohitarya.glide.facedetection.transformation.core.GlideFaceDetector;
import com.ruihai.android.common.context.GlobalContext;
import com.ruihai.iconicfontengine.IconicFontEngine;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.api.model.UserPreferences;
import com.ruihai.xingka.ui.MainActivity;
import com.ruihai.xingka.ui.talking.ContactHelper;
import com.ruihai.xingka.utils.AppUtility;
import com.ruihai.xingka.utils.cache.ImagePipelineConfigFactory;
import com.ruihai.xingka.widget.IconCommonEngine;
import com.umeng.analytics.MobclickAgent;
import com.umeng.onlineconfig.OnlineConfigAgent;
import com.umeng.socialize.PlatformConfig;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;
import cn.smssdk.SMSSDK;

/**
 * Created by apple on 15/8/18.
 */
public class XKApplication extends GlobalContext {

    private final String TAG = this.getClass().getSimpleName();
    private static XKApplication instance;
    private List<Activity> activities = new LinkedList<>();
    public static int sEmojiNormal;
    public static int sEmojiMonkey;
    private static Handler mHandler;  //用来在主线程中刷新ui

    /**
     * 在主线程中刷新UI的方法
     *
     * @param r
     */
    public static void runOnUIThread(Runnable r) {
        XKApplication.getHandler().post(r);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        //初始化handler
        mHandler = new Handler();

        AppUtility.initialize(this);
        Fresco.initialize(this, ImagePipelineConfigFactory.getImagePipelineConfig(this));
        // 初始化短信验证码SDK接口
        SMSSDK.initSDK(this, Constant.MOBSMS_APPKEY, Constant.MOBSMS_APPSECRET);

        IconicFontEngine.addDefaultEngine(
                new IconCommonEngine(Typeface.createFromAsset(getAssets(), "fonts/icomoon.ttf")));


        JPushInterface.init(this);    // 初始化 JPush
        initConfigs(false);
        sEmojiNormal = getResources().getDimensionPixelSize(R.dimen.emoji_normal);
        sEmojiMonkey = getResources().getDimensionPixelSize(R.dimen.emoji_monkey);

        //意见反馈匿名反馈初始化方式
        FeedbackAPI.initAnnoy(this, "23389994");

        //初始化Glide库
        GlideFaceDetector.initialize(this);

        //云信IM初始化
        XKCache.setContext(this);
        // SDK初始化（启动后台服务，若已经存在用户登录信息， SDK 将完成自动登录）
        NIMClient.init(this, loginInfo(), options());

    }

    private LoginInfo loginInfo() {
        String account = AccountInfo.getInstance().getUserAccount();
        String token = AccountInfo.getInstance().getUserToken();

        if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(token)) {
            XKCache.setAccount(account.toLowerCase());

            return new LoginInfo(account, token);
        } else {
            return null;
        }
    }

    private void initUIKit() {
        // 初始化，需要传入用户信息提供者
        NimUIKit.init(this, infoProvider, contactProvider);

//        // 设置地理位置提供者。如果需要发送地理位置消息，该参数必须提供。如果不需要，可以忽略。
//        NimUIKit.setLocationProvider(new NimDemoLocationProvider());
//
//        // 会话窗口的定制初始化。
//        SessionHelper.init();
//
//        // 通讯录列表定制初始化
        ContactHelper.init();
    }

    private SDKOptions options() {
        SDKOptions options = new SDKOptions();

        // 如果将新消息通知提醒托管给SDK完成，需要添加以下配置。
        StatusBarNotificationConfig config = UserPreferences.getStatusConfig();
        if (config == null) {
            config = new StatusBarNotificationConfig();
        }
        // 点击通知需要跳转到的界面
        config.notificationEntrance = MainActivity.class;
        config.notificationSmallIconId = R.mipmap.statusbar_ic_gray_logo;

        // 通知铃声的uri字符串
        config.notificationSound = "android.resource://com.netease.nim.demo/raw/msg";

        // 呼吸灯配置
        config.ledARGB = Color.GREEN;
        config.ledOnMs = 1000;
        config.ledOffMs = 1500;

        options.statusBarNotificationConfig = config;
        XKCache.setNotificationConfig(config);
        UserPreferences.setStatusConfig(config);

        // 配置保存图片，文件，log等数据的目录
        String sdkPath = Environment.getExternalStorageDirectory() + "/" + getPackageName() + "/nim";
        options.sdkStorageRootPath = sdkPath;

        // 配置数据库加密秘钥
        options.databaseEncryptKey = "NETEASE";

        // 配置是否需要预下载附件缩略图
        options.preloadAttach = true;

        // 配置附件缩略图的尺寸大小，
//        options.thumbnailSize = MsgViewHolderThumbBase.getImageMaxEdge();
//
//        // 用户信息提供者
//        options.userInfoProvider = infoProvider;

        // 定制通知栏提醒文案（可选，如果不定制将采用SDK默认文案）
        options.messageNotifierCustomization = messageNotifierCustomization;

        return options;
    }

    private UserInfoProvider infoProvider = new UserInfoProvider() {
        @Override
        public UserInfo getUserInfo(String account) {
            UserInfo user = NimUserInfoCache.getInstance().getUserInfo(account);
            if (user == null) {
                NimUserInfoCache.getInstance().getUserInfoFromRemote(account, null);
            }

            return user;
        }

        @Override
        public int getDefaultIconResId() {
            return R.mipmap.default_avatar;
        }

        @Override
        public Bitmap getTeamIcon(String teamId) {
            Drawable drawable = getResources().getDrawable(R.drawable.nim_avatar_group);
            if (drawable instanceof BitmapDrawable) {
                return ((BitmapDrawable) drawable).getBitmap();
            }

            return null;
        }

        @Override
        public Bitmap getAvatarForMessageNotifier(String account) {
            /**
             * 注意：这里最好从缓存里拿，如果读取本地头像可能导致UI进程阻塞，导致通知栏提醒延时弹出。
             */
            UserInfo user = getUserInfo(account);
            return (user != null) ? ImageLoaderKit.getNotificationBitmapFromCache(user) : null;
        }

        @Override
        public String getDisplayNameForMessageNotifier(String account, String sessionId, SessionTypeEnum sessionType) {
            String nick = null;
            if (sessionType == SessionTypeEnum.P2P) {
                nick = NimUserInfoCache.getInstance().getAlias(account);
            } else if (sessionType == SessionTypeEnum.Team) {
                nick = TeamDataCache.getInstance().getTeamNick(sessionId, account);
                if (TextUtils.isEmpty(nick)) {
                    nick = NimUserInfoCache.getInstance().getAlias(account);
                }
            }
            // 返回null，交给sdk处理。如果对方有设置nick，sdk会显示nick
            if (TextUtils.isEmpty(nick)) {
                return null;
            }

            return nick;
        }
    };

    private ContactProvider contactProvider = new ContactProvider() {
        @Override
        public List<UserInfoProvider.UserInfo> getUserInfoOfMyFriends() {
            List<NimUserInfo> nimUsers = NimUserInfoCache.getInstance().getAllUsersOfMyFriend();
            List<UserInfoProvider.UserInfo> users = new ArrayList<>(nimUsers.size());
            if (!nimUsers.isEmpty()) {
                users.addAll(nimUsers);
            }

            return users;
        }

        @Override
        public int getMyFriendsCount() {
            return FriendDataCache.getInstance().getMyFriendCounts();
        }

        @Override
        public String getUserDisplayName(String account) {
            return NimUserInfoCache.getInstance().getUserDisplayName(account);
        }
    };

    private MessageNotifierCustomization messageNotifierCustomization = new MessageNotifierCustomization() {
        @Override
        public String makeNotifyContent(String nick, IMMessage message) {
            return null; // 采用SDK默认文案
        }

        @Override
        public String makeTicker(String nick, IMMessage message) {
            return null; // 采用SDK默认文案
        }
    };



    //微信, QQ, 微博的AppKey
    {
        PlatformConfig.setWeixin(Constant.WEIXIN_APPID, Constant.WEIXIN_APPSECRET);
        //微信 appid appsecret
        PlatformConfig.setSinaWeibo(Constant.WEIBO_APPKEY, Constant.WEIBO_APPSECRCT);
        //新浪微博 appkey appsecret
        PlatformConfig.setQQZone(Constant.QQSSO_APPID, Constant.QQSSO_APPKEY);
        // QQ和Qzone appid appkey
    }

    /**
     * 初始化配置信息
     *
     * @param isDebugMode 是否是开发模式
     */
    private void initConfigs(boolean isDebugMode) {
        // 友盟在线参数调式模式：开启
        OnlineConfigAgent.getInstance().setDebugMode(isDebugMode);
        // 友盟统计调式模式：开启
        MobclickAgent.setDebugMode(isDebugMode);
        // Initialize JPush
        JPushInterface.setDebugMode(isDebugMode); // 设置开启日志,发布时需关闭日志

        if (isDebugMode) { // 开发模式
            Logger
                    .init(TAG)                    // default PRETTYLOGGER or use just init()
                    .setMethodCount(3)            // default 2
                    .hideThreadInfo()             // default shown
                    .setLogLevel(LogLevel.FULL);  // default LogLevel.FULL | LogLevel.NONE

            // Initialize the hawk
            Hawk.init(this)
                    .setEncryptionMethod(HawkBuilder.EncryptionMethod.MEDIUM)
                    .setStorage(HawkBuilder.newSqliteStorage(this))
                    .setLogLevel(com.orhanobut.hawk.LogLevel.FULL)
                    .build();
        } else {
            Logger
                    .init(TAG)                    // default PRETTYLOGGER or use just init()
                    .setMethodCount(3)            // default 2
                    .hideThreadInfo()             // default shown
                    .setLogLevel(LogLevel.NONE);  // default LogLevel.FULL | LogLevel.NONE

            // Initialize the hawk
            Hawk.init(this)
                    .setEncryptionMethod(HawkBuilder.EncryptionMethod.MEDIUM)
                    .setStorage(HawkBuilder.newSqliteStorage(this))
                    .setLogLevel(com.orhanobut.hawk.LogLevel.NONE)
                    .build();
        }
    }

    public static XKApplication getInstance() {
        return instance;
    }

    public static Handler getHandler() {
        return mHandler;
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    //    这两个方法是为了在某个界面结束所有Activity
    public void addActivity(Activity activity) {
        activities.add(activity);
    }

    public void exit() {
        for (Activity activity : activities) {
            activity.finish();
        }
    }

}


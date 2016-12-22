package com.ruihai.xingka.ui.caption.publisher.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.orhanobut.logger.Logger;
import com.ruihai.xingka.XKApplication;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.db.PublishDB;
import com.ruihai.xingka.ui.caption.publisher.PublishManager;
import com.ruihai.xingka.ui.caption.publisher.bean.PublishBean;


/**
 * 任务发布服务，将所有发布任务保存在一个队列依次发布
 * <p>
 * Created by zecker on 15/10/2.
 */
public class PublishService extends Service implements IPublisher {

    public static void publish(Context context, PublishBean bean) {
        Intent intent = new Intent(context, PublishService.class);
        intent.setAction("com.ruihai.xingka.PUBLISH");
        intent.putExtra("data", bean);
        context.startService(intent);
    }

    public static void stopPublish() {
        Intent intent = new Intent(XKApplication.getInstance(), PublishService.class);
        XKApplication.getInstance().stopService(intent);
    }

    private PublishManager publishManager;
    private PublishService publisher;
    private PublishBinder binder;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (publisher == null)
            publisher = this;
        if (binder == null)
            binder = new PublishBinder();
        if (publishManager == null)
            publishManager = new PublishManager(this);

        if (intent != null) {
            if ("com.ruihai.xingka.PUBLISH".equals(intent.getAction())) {
                PublishBean data = (PublishBean) intent.getSerializableExtra("data");
                if (data != null)
                    publish(data);
            } else if ("com.ruihai.xingka.Cancel".equals(intent.getAction())) {
                publishManager.cancelPublish();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void publish(PublishBean data) {
        if (data.getStatus() == PublishBean.PublishStatus.create) {
            publishManager.onPublish(data);
        } else {
            // 如果不是新建的，都当做草稿重新发布
            data.setStatus(PublishBean.PublishStatus.create);
            PublishDB.addPublish(data, AccountInfo.getInstance().loadAccount());

            publishManager.onPublish(data);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Logger.d("停止发布服务");
        publishManager.stop();
    }

    public class PublishBinder extends Binder {

        public IPublisher getPublisher() {
            return publisher;
        }

    }
}

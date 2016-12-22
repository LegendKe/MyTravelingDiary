package com.ruihai.xingka.ui.caption.publisher;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.ruihai.xingka.R;
import com.ruihai.xingka.api.ApiModule;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.api.model.CaptionInfo;
import com.ruihai.xingka.api.model.DraftBoxItem;
import com.ruihai.xingka.api.model.ImageModule;
import com.ruihai.xingka.api.model.MyFriendInfoRepo;
import com.ruihai.xingka.api.model.User;
import com.ruihai.xingka.api.model.XKRepo;
import com.ruihai.xingka.db.PublishDB;
import com.ruihai.xingka.ui.caption.publisher.bean.PublishBean;
import com.ruihai.xingka.ui.caption.publisher.service.PublishService;
import com.ruihai.xingka.utils.AppUtility;
import com.ruihai.xingka.utils.Security;

import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;


/**
 * Created by zecker on 15/10/2.
 */
public class PublishManager extends Handler implements PublishQueue.PublishQueueCallback {

    public static final int publishDelay = 1;

    public static final String ACTION_PUBLISH_CHANNGED = "com.ruihai.xingka.ACTION_PUBLISH_SUCCESSED";
    public static final String ACTION_PUBLISH_FAILED = "com.ruihai.xingka.ACTION_PUBLISH_FAILED";

    private Context context;
    private DraftBoxItem mDraftBoxItem;// 草稿箱数据类
    private PublishQueue publishQueue;
    private PublishNotifier publishNotifier;

    private User currentUser;
    private UploadManager uploadManager;

    private List<String> generatedImageKeys = new ArrayList<>();

    public PublishManager(Context context) {
        this.context = context.getApplicationContext();
        this.currentUser = AccountInfo.getInstance().loadAccount();
        publishQueue = new PublishQueue(this);
        publishNotifier = new PublishNotifier(context);

        publishInit();
    }

    public void stop() {
        Logger.d("停止发布一个任务");
    }

    public void cancelPublish() {
        removeMessages(publishDelay);

        PublishBean bean = publishQueue.poll();
        if (bean != null) {
            bean.setStatus(PublishBean.PublishStatus.draft);
            PublishDB.addPublish(bean, currentUser);

            publishNotifier.notifyPublishCancelled(bean);

            refreshDraftbox();

            onPublish(publishQueue.peek());
        }
    }

    /**
     * 将添加状态的消息都加入到队列当中
     */
    private void publishInit() {
        List<PublishBean> beans = PublishDB.getPublishOfAddStatus(currentUser);
        for (PublishBean bean : beans)
            publishQueue.add(bean);

        onPublish(publishQueue.peek());
    }

    public void onPublish(PublishBean bean) {
        if (bean == null)
            return;

        // 如果队列为空，放入首位等待发送，否则，添加到队列等待发布
        if (publishQueue.isEmpty()) {
            publishQueue.add(bean);
        }

        PublishBean firstBean = publishQueue.peek();
        if (firstBean != null && firstBean.getId().equals(bean.getId())) {
            // 立即发布
            if (bean.getDelay() <= 0) {
                Logger.d(bean.getText() + "-立即发布");
                publishCaption(bean);
            }
            // 延迟发布
            else {
                Logger.d(bean.getText() + "-延迟发布");

                publishNotifier.notifyPrePublish(bean);
                Message msg = obtainMessage(publishDelay);
                msg.obj = bean;
                bean.setDelay(bean.getDelay() - 1000);
                sendMessageDelayed(msg, 1000);

                if (bean.getStatus() != PublishBean.PublishStatus.waiting) {
                    bean.setStatus(PublishBean.PublishStatus.waiting);
                    PublishDB.updatePublish(bean, currentUser);
                }
            }
        } else {
            Logger.d(bean.getText() + "-添加到队列等等发布");

            publishQueue.add(bean);
            if (firstBean == null)
                onPublish(bean);

            if (bean.getStatus() != PublishBean.PublishStatus.waiting) {
                bean.setStatus(PublishBean.PublishStatus.waiting);
                PublishDB.updatePublish(bean, currentUser);
            }
        }
        refreshDraftbox();
    }

    private void refreshDraftbox() {//0:添加 1:删除 2:
        Intent intent = new Intent();
        intent.setAction(ACTION_PUBLISH_CHANNGED);
//        Bundle bundle=new Bundle();
//        bundle.putInt("PUBLISH_STATE",state);
//        intent.putExtra("PUBLISH_DATA",bundle);
        context.sendBroadcast(intent);
    }

    private void deleteDraftbox() {
        Intent intent = new Intent();
        intent.setAction(ACTION_PUBLISH_CHANNGED);
        context.sendBroadcast(intent);
    }

    private void putDraftbox() {
        Log.i("TAG", "------向草稿箱发广播------->");
        Intent intent = new Intent();
        intent.setAction(ACTION_PUBLISH_FAILED);
        intent.putExtra("PUT_DRAFTBOX", mDraftBoxItem);
//        Bundle bundle=new Bundle();
//        bundle.putInt("PUBLISH_STATE",state);
//        intent.putExtra("PUBLISH_DATA",bundle);
        context.sendBroadcast(intent);
    }


    @Override
    public void onPublishPoll(PublishBean bean) {
        // 任务失败不从DB删除
        if (bean.getStatus() != PublishBean.PublishStatus.faild)
            PublishDB.deletePublish(bean, currentUser);
        putDraftbox();
    }

    @Override
    public void onPublishAdd(PublishBean bean) {
        if (bean.getStatus() != PublishBean.PublishStatus.create)
            bean.setStatus(PublishBean.PublishStatus.draft);
        bean.setErrorMsg("");
        PublishDB.addPublish(bean, currentUser);
    }

    @Override
    public void onPublishPeek(PublishBean bean) {
        bean.setStatus(PublishBean.PublishStatus.sending);
        PublishDB.updatePublish(bean, currentUser);
        //更新界面
        refreshDraftbox();
    }

    private synchronized void publishFinished(PublishBean bean) {
        publishQueue.poll();

        Logger.w("publishFinished" + publishQueue.size());

        // 队列发送完毕了，且当前运行的页面不是发布页面，就停止服务
        if (publishQueue.size() == 0)
            context.stopService(new Intent(context, PublishService.class));
        else {
            postDelayed(new Runnable() {

                @Override
                public void run() {
                    onPublish(publishQueue.peek());
                }

            }, 2 * 1000);
        }
    }

    private void publishCaption(final PublishBean bean) {
        if (bean != null) {
            publishNotifier.notifyPublishing(bean);
        } else {
            publishInit();
        }
        // 是否仅自己可见
        int isVisible = 0;
        if (AccountInfo.getInstance().loadAccount().getLoginState() == 2) {//该用户被后台设置,发布的图说仅自己可见
            isVisible = 1;
        } else
            isVisible = Integer.parseInt(bean.getParams().getParameter("visible"));
        ArrayList<String> mSelectedPaths = new ArrayList<>();
        for (int i = 0; i < bean.getPics().length; i++) {
            mSelectedPaths.add(bean.getPics()[i]);
        }
        mDraftBoxItem = new DraftBoxItem(AccountInfo.getInstance().loadAccount().getAccount(), bean.getText(), bean.getSelectedFriends(), mSelectedPaths, "图说", System.currentTimeMillis(), isVisible, bean.getAddress(), bean.getX(), bean.getY());
        String randomStr = Security.aesEncrypt("android");
        ApiModule.apiService().getQiniuToken(randomStr).enqueue(new retrofit2.Callback<XKRepo>() {
            @Override
            public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                XKRepo xkRepo = response.body();
                if (xkRepo.isSuccess()) {
                    String token = xkRepo.getMsg();
                    upload(token, bean);
                } else {
                    if (bean != null) {
                        publishNotifier.notifyPublishFaild(bean, xkRepo.getMsg());
                        bean.setStatus(PublishBean.PublishStatus.faild);
                        bean.setErrorMsg(xkRepo.getMsg());
                        putDraftbox();//添加到草稿箱
                        PublishService.stopPublish();//停止发布
                    }

                }
            }

            @Override
            public void onFailure(Call<XKRepo> call, Throwable t) {
                if (bean != null) {
                    publishNotifier.notifyPublishFaild(bean, context.getString(R.string.common_network_error));
                    bean.setStatus(PublishBean.PublishStatus.faild);
                    bean.setErrorMsg(t.getLocalizedMessage());
                    putDraftbox();
                    PublishService.stopPublish();//停止发布

                }
            }


        });
    }

    private void upload(String uploadToken, final PublishBean bean) {
        if (this.uploadManager == null) {
            this.uploadManager = new UploadManager();
        }
        final List<String> imageKeys = new ArrayList<>();
        final String[] selectedPhotos = bean.getPics();

        for (int i = 0; i < selectedPhotos.length; i++) {
            final String imagePath = selectedPhotos[i];
            File uploadFile;
            //以下判断是针对图片方向
            if (AppUtility.readPictureDegree(imagePath) == 90) {
                Bitmap bp = AppUtility.rotateBitmapByDegree(BitmapFactory.decodeFile(imagePath), 90);
                String rotateImagePath = AppUtility.saveImageToExternalStorage(bp, context);
                uploadFile = new File(rotateImagePath);
            } else if (AppUtility.readPictureDegree(imagePath) == 180) {
                Bitmap bp = AppUtility.rotateBitmapByDegree(BitmapFactory.decodeFile(imagePath), 180);
                String rotateImagePath = AppUtility.saveImageToExternalStorage(bp, context);
                uploadFile = new File(rotateImagePath);
            } else if (AppUtility.readPictureDegree(imagePath) == 270) {
                Bitmap bp = AppUtility.rotateBitmapByDegree(BitmapFactory.decodeFile(imagePath), 270);
                String rotateImagePath = AppUtility.saveImageToExternalStorage(bp, context);
                uploadFile = new File(rotateImagePath);
            } else {
                uploadFile = new File(imagePath);
            }
            String uploadFilKey = AppUtility.generateUUID();
            generatedImageKeys.add(uploadFilKey);
            // 压缩文件
            uploadFile = AppUtility.getUploadFile(uploadFile);
            Logger.w("上传图片大小" + (uploadFile.length() / 1024) + "KB");

            this.uploadManager.put(uploadFile, uploadFilKey, uploadToken, new UpCompletionHandler() {
                @Override
                public void complete(String key, ResponseInfo info, JSONObject response) {
                    if (info.isOK()) {
                        String fileKey = response.optString("key");
                        Logger.e(fileKey + " | " + imagePath);
                        imageKeys.add(fileKey);
                        Logger.d(fileKey);

                        if (imageKeys.size() == selectedPhotos.length) {
                            Logger.d("执行上传动作");
                            submitCaption(bean);
                        }
                    } else {
                        if (bean != null) {
                            publishNotifier.notifyPublishFaild(bean, context.getString(R.string.common_network_error));

                            bean.setStatus(PublishBean.PublishStatus.faild);
                            bean.setErrorMsg(info.error);
                            putDraftbox();
                            PublishService.stopPublish();//停止发布

                        }
                    }
                }
            }, null);
        }
    }

    void submitCaption(final PublishBean bean) {
        CaptionInfo info = new CaptionInfo();
        // 账号
        info.account = currentUser.getAccount();
        // 图说内容
        info.content = bean.getParams().getParameter("content");
        //地理位置和经纬度
        info.x = bean.getX();
        info.y = bean.getY();
        info.address = bean.getAddress();
        // 图说图片
        List<ImageModule> imageItems = new ArrayList<>();
        for (String imageKey : generatedImageKeys) {

            Logger.e(imageKey);

            ImageModule image1 = new ImageModule();
            image1.imgSrc = imageKey;
            ImageModule.ImageTag imgTag1 = image1.new ImageTag();
            image1.moodTag = imgTag1;

            ImageModule.ImageTag imgTag2 = image1.new ImageTag();
            image1.brandTag = imgTag2;

            ImageModule.ImageTag imgTag3 = image1.new ImageTag();
            image1.addressTag = imgTag3;

            imageItems.add(image1);
        }
        info.imgModule = imageItems;

        // @好友
        String[] atUserAccounts = bean.getAtUsers();
        CaptionInfo.AtUser user = null;
        List<CaptionInfo.AtUser> atUsers = new ArrayList<>();
        for (int i = 0; i < atUserAccounts.length; i++) {
            user = info.new AtUser();
            user.account = Integer.parseInt(atUserAccounts[i]);
            atUsers.add(user);
        }

        info.pushModule = atUsers;

        // 是否仅自己可见
        if (AccountInfo.getInstance().loadAccount().getLoginState() == 2) {//该用户被后台设置,发布的图说仅自己可见
            info.isHidden = 1;
        } else
            info.isHidden = Integer.parseInt(bean.getParams().getParameter("visible"));

        Log.i("TAG", "-----是否自己可见------>" + info.isHidden);
        ArrayList<String> mSelectedPaths = new ArrayList<>();
        for (int i = 0; i < bean.getPics().length; i++) {
            mSelectedPaths.add(bean.getPics()[i]);
        }
        String captionJson = new Gson().toJson(info).replace("{}", "null");
        Logger.e(captionJson);

        String account = Security.aesEncrypt(String.valueOf(currentUser.getAccount()));
        ApiModule.apiService().photoTopicAdd(account, Security.aesEncrypt(captionJson)).enqueue(new retrofit2.Callback<XKRepo>() {
            @Override
            public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                XKRepo xkRepo = response.body();
                if (xkRepo.isSuccess()) {
                    if (bean != null) {
                        publishNotifier.notifyPublishSuccess(bean);
                        AccountInfo.getInstance().clearUserDraftBoxInfo();
                        PublishDB.deletePublish(bean, currentUser);
                    }
                } else {
                    if (bean != null) {
                        publishNotifier.notifyPublishFaild(bean, xkRepo.getMsg());
                        bean.setStatus(PublishBean.PublishStatus.faild);
                        bean.setErrorMsg(xkRepo.getMsg());
                        putDraftbox();
                    }
                }
                PublishService.stopPublish();//停止发布
                // finished
//                if (bean != null) {
//                    publishFinished(bean);
//                    refreshDraftbox();
//                }
            }

            @Override
            public void onFailure(Call<XKRepo> call, Throwable t) {
                if (bean != null) {
                    publishNotifier.notifyPublishFaild(bean, context.getString(R.string.common_network_error));
                    bean.setStatus(PublishBean.PublishStatus.faild);
                    bean.setErrorMsg(t.getLocalizedMessage());
                    putDraftbox();
                    PublishService.stopPublish();//停止发布
                }
            }
        });
    }

    public DraftBoxItem getDraftBoxData() {
        return mDraftBoxItem;
    }
}

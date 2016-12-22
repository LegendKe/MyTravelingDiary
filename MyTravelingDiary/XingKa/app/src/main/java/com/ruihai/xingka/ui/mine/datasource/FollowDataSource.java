package com.ruihai.xingka.ui.mine.datasource;

import com.orhanobut.logger.Logger;
import com.ruihai.xingka.api.ApiModule;
import com.ruihai.xingka.api.RetrofitRequestHandle;
import com.ruihai.xingka.api.model.PushMessage;
import com.ruihai.xingka.api.model.PushMessageRepo;
import com.ruihai.xingka.api.model.ReadMsg;
import com.ruihai.xingka.utils.Security;
import com.shizhefei.mvc.IAsyncDataSource;
import com.shizhefei.mvc.RequestHandle;
import com.shizhefei.mvc.ResponseSender;

import java.util.List;

import de.greenrobot.event.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mac on 16/4/7.
 */
public class FollowDataSource implements IAsyncDataSource<List<PushMessage>> {
    private static final int DEFAULT_PER_PAGE = 20;
    private static final int DEFAULT_READTYPE = -1;
    private int mPage;
    private int mMaxPage = 0;
    private int mPerPage = DEFAULT_PER_PAGE;
    private int mPushtype = 8;
    private int mPushtype1 = 11;
    private int mReadtype = DEFAULT_READTYPE;
    private String mCurrentAccount;

    public FollowDataSource(String mAccount) {
        this.mCurrentAccount = mAccount;
    }

    @Override
    public RequestHandle refresh(ResponseSender<List<PushMessage>> sender) throws Exception {
        return loadMessageData(sender, 1);
    }

    @Override
    public RequestHandle loadMore(ResponseSender<List<PushMessage>> sender) throws Exception {
        return loadMessageData(sender, mPage + 1);
    }

    private RequestHandle loadMessageData(final ResponseSender<List<PushMessage>> sender, final int page) throws Exception {
        Logger.d("---> 刷新 " + page + " / " + mMaxPage);
        String sAccount = Security.aesEncrypt(mCurrentAccount);//我的行咖号
        String sPage = Security.aesEncrypt(String.valueOf(page));//页数
        String sPerPage = Security.aesEncrypt(String.valueOf(mPerPage));//每页数量
        String sPushType = Security.aesEncrypt(String.format("%s,%s", mPushtype, mPushtype1));//消息类型
        String sReadType = Security.aesEncrypt(String.valueOf(mReadtype));//阅读类型

        Call<PushMessageRepo> call = ApiModule.apiService_1().photoTopicPushList(sAccount, sPushType, sReadType, sPage, sPerPage);
        call.enqueue(new Callback<PushMessageRepo>() {
            @Override
            public void onResponse(Call<PushMessageRepo> call, Response<PushMessageRepo> response) {
                PushMessageRepo repo = response.body();
                mMaxPage = (repo.getRecordCount() + mPerPage - 1) / mPerPage;
                ReadMsg readMsg = new ReadMsg(5);
                EventBus.getDefault().post(readMsg);
                if (repo.isSuccess()) {
                    mPage = page;
                    sender.sendData(repo.getPushMessages());
                } else {
                    mPage = page;
                    sender.sendData(repo.getPushMessages());
                }
            }

            @Override
            public void onFailure(Call<PushMessageRepo> call, Throwable t) {
                sender.sendError(new Exception(t));
            }
        });

        return new RetrofitRequestHandle(call);
    }

    @Override
    public boolean hasMore() {
        return mPage < mMaxPage;
    }
}

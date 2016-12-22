package com.ruihai.xingka.ui.mine.datasource;

import com.orhanobut.logger.Logger;
import com.ruihai.xingka.api.ApiModule;
import com.ruihai.xingka.api.RetrofitRequestHandle;
import com.ruihai.xingka.api.model.UserPhotoTopic;
import com.ruihai.xingka.api.model.UserPhotoTopicRepo;
import com.ruihai.xingka.utils.Security;
import com.shizhefei.mvc.IAsyncDataSource;
import com.shizhefei.mvc.RequestHandle;
import com.shizhefei.mvc.ResponseSender;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mac on 16/7/14.
 */
public class OfficalCaptionListDataSource implements IAsyncDataSource<List<UserPhotoTopic>> {
    private static final int DEFAULT_PER_PAGE = 20;
    private static final int DEFAULT_TYPE = 0;
    private int mPage;
    private int mMaxPage = 0;
    private int mPerPage = DEFAULT_PER_PAGE;
    private int mType = DEFAULT_TYPE;
    private String mCurrentAccount;


    public OfficalCaptionListDataSource(String mAccount, int type) {
        this.mCurrentAccount = mAccount;
        this.mType = type;
    }

    @Override
    public RequestHandle refresh(ResponseSender<List<UserPhotoTopic>> sender) throws Exception {
        return loadMessageData(sender, 1);
    }

    @Override
    public RequestHandle loadMore(ResponseSender<List<UserPhotoTopic>> sender) throws Exception {
        return loadMessageData(sender, mPage + 1);
    }

    private RequestHandle loadMessageData(final ResponseSender<List<UserPhotoTopic>> sender, final int page) throws Exception {
        Logger.d("---> 刷新 " + page + " / " + mMaxPage);
        String sAccount = Security.aesEncrypt(mCurrentAccount);//我的行咖号
        String sPage = Security.aesEncrypt(String.valueOf(page));//页数
        String sPerPage = Security.aesEncrypt(String.valueOf(mPerPage));//每页数量
        String sType = Security.aesEncrypt(String.valueOf(mType));//消息类型
        String isHidden = Security.aesEncrypt("0");

        Call<UserPhotoTopicRepo> call = ApiModule.apiService_1().myPhotoTopicWithType(sAccount, sType, isHidden, sPage, sPerPage);
        call.enqueue(new Callback<UserPhotoTopicRepo>() {
            @Override
            public void onResponse(Call<UserPhotoTopicRepo> call, Response<UserPhotoTopicRepo> response) {
                UserPhotoTopicRepo repo = response.body();
                mMaxPage = (repo.getRecordCount() + mPerPage - 1) / mPerPage;
//                ReadMsg readMsg = new ReadMsg(6);
//                EventBus.getDefault().post(readMsg);
                mPage = page;
                sender.sendData(repo.getUserPhotoTopicList());

            }

            @Override
            public void onFailure(Call<UserPhotoTopicRepo> call, Throwable t) {
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

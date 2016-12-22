package com.ruihai.xingka.ui.mine.datasource;

import android.util.Log;

import com.orhanobut.logger.Logger;
import com.ruihai.xingka.api.ApiModule;
import com.ruihai.xingka.api.RetrofitRequestHandle;
import com.ruihai.xingka.api.model.UserPhotoTopic;
import com.ruihai.xingka.api.model.UserPhotoTopicRepo;
import com.ruihai.xingka.event.UpdateCountEvent;
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
 * Created by zecker on 15/11/11.
 */
public class MyCaptionDataSource implements IAsyncDataSource<List<UserPhotoTopic>> {

    private static final int DEFAULT_PER_PAGE = 10;

    private int mPage;
    private int mMaxPage = 0;
    private int mPerPage = DEFAULT_PER_PAGE;

    private String mCurrentAccount;
    private String mIsHidden;

    public MyCaptionDataSource(String account, String isHidden) {
        mCurrentAccount = account;
        mIsHidden = isHidden;
    }

    @Override
    public RequestHandle refresh(ResponseSender<List<UserPhotoTopic>> sender) throws Exception {
        EventBus.getDefault().post(new UpdateCountEvent());
        return loadUserCaptions(sender, 1);
    }

    @Override
    public RequestHandle loadMore(ResponseSender<List<UserPhotoTopic>> sender) throws Exception {
        return loadUserCaptions(sender, mPage + 1);
    }

    @Override
    public boolean hasMore() {
        return mPage < mMaxPage;
    }

    private RequestHandle loadUserCaptions(final ResponseSender<List<UserPhotoTopic>> sender, final int page) throws Exception {
        Logger.d("---> 刷新 " + page + " / " + mMaxPage);
        String sAccount = Security.aesEncrypt(mCurrentAccount);
        String sIsHidden = Security.aesEncrypt(String.valueOf(mIsHidden));
        String sPage = Security.aesEncrypt(String.valueOf(page));
        String sPerPage = Security.aesEncrypt(String.valueOf(mPerPage));

        Call<UserPhotoTopicRepo> call = ApiModule.apiService_1().myPhotoTopic(sAccount, sIsHidden, sPage, sPerPage);
        call.enqueue(new Callback<UserPhotoTopicRepo>() {
            @Override
            public void onResponse(Call<UserPhotoTopicRepo> call, Response<UserPhotoTopicRepo> response) {
                UserPhotoTopicRepo repo = response.body();
                mMaxPage = (repo.getRecordCount() + mPerPage - 1) / mPerPage;
                if (repo.isSuccess()) {
                    updateCountEvent();
                    mPage = page;
                    sender.sendData(repo.getUserPhotoTopicList());
                } else {
                    mPage = page;
                    sender.sendData(repo.getUserPhotoTopicList());
                }
            }

            @Override
            public void onFailure(Call<UserPhotoTopicRepo> call, Throwable t) {
                sender.sendError(new Exception(t));
//                Log.e("TAG","错误-->"+t.getMessage());
            }
        });

        return new RetrofitRequestHandle(call);
    }

    private void updateCountEvent() {
        EventBus.getDefault().post(new UpdateCountEvent());
    }
}

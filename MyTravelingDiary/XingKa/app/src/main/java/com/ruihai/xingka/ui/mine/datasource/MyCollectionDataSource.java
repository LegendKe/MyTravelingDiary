package com.ruihai.xingka.ui.mine.datasource;

import com.orhanobut.logger.Logger;
import com.ruihai.xingka.api.ApiModule;
import com.ruihai.xingka.api.RetrofitRequestHandle;
import com.ruihai.xingka.api.model.UserCollection;
import com.ruihai.xingka.api.model.UserCollectionRepo;
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
public class MyCollectionDataSource implements IAsyncDataSource<List<UserCollection>> {

    private static final int DEFAULT_PER_PAGE = 18;

    private int mPage;
    private int mMaxPage = 0;
    private int mPerPage = DEFAULT_PER_PAGE;

    private String mCurrentAccount;

    public MyCollectionDataSource(String account) {
        mCurrentAccount = account;
    }

    @Override
    public RequestHandle refresh(ResponseSender<List<UserCollection>> sender) throws Exception {
        EventBus.getDefault().post(new UpdateCountEvent());
        return loadUserCaptions(sender, 1);
    }

    @Override
    public RequestHandle loadMore(ResponseSender<List<UserCollection>> sender) throws Exception {
        return loadUserCaptions(sender, mPage + 1);
    }

    @Override
    public boolean hasMore() {
        return mPage < mMaxPage;
    }

    private RequestHandle loadUserCaptions(final ResponseSender<List<UserCollection>> sender, final int page) throws Exception {
        Logger.d("---> 刷新 " + page + " / " + mMaxPage);
        String sAccount = Security.aesEncrypt(mCurrentAccount);
        String sPage = Security.aesEncrypt(String.valueOf(page));
        String sPerPage = Security.aesEncrypt(String.valueOf(mPerPage));

        Call<UserCollectionRepo> call = ApiModule.apiService_1().myPhotoTopicCollections(sAccount, sPage, sPerPage);
        call.enqueue(new Callback<UserCollectionRepo>() {
            @Override
            public void onResponse(Call<UserCollectionRepo> call, Response<UserCollectionRepo> response) {
                UserCollectionRepo repo = response.body();
                mMaxPage = (repo.getRecordCount() + mPerPage - 1) / mPerPage;
                if (repo.isSuccess()) {
                    mPage = page;
                    sender.sendData(repo.getUserCollectionList());
                } else {
                    mPage = page;
                    sender.sendData(repo.getUserCollectionList());
                }
            }

            @Override
            public void onFailure(Call<UserCollectionRepo> call, Throwable t) {
                sender.sendError(new Exception(t));
            }
        });

        return new RetrofitRequestHandle(call);
    }

    private void updateCountEvent() {
        EventBus.getDefault().post(new UpdateCountEvent());
    }
}

package com.ruihai.xingka.ui.trackway.datasource;

import com.orhanobut.logger.Logger;
import com.ruihai.xingka.api.ApiModule;
import com.ruihai.xingka.api.RetrofitRequestHandle;
import com.ruihai.xingka.api.model.TrackwayCollection;
import com.ruihai.xingka.api.model.TrackwayCollectionRepo;
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
 * 旅拼收藏数据源
 * Created by lqfang on 16/5/19.
 */
public class TrackwayCollectDataSource implements IAsyncDataSource<List<TrackwayCollection>> {

    private static final int DEFAULT_PER_PAGE = 18;

    private int mPage;
    private int mMaxPage = 0;
    private int mPerPage = DEFAULT_PER_PAGE;

    private String mCurrentAccount;

    public TrackwayCollectDataSource(String account) {
        mCurrentAccount = account;
    }

    @Override
    public RequestHandle refresh(ResponseSender<List<TrackwayCollection>> sender) throws Exception {
        EventBus.getDefault().post(new UpdateCountEvent());
        return loadUserCaptions(sender, 1);
    }

    @Override
    public RequestHandle loadMore(ResponseSender<List<TrackwayCollection>> sender) throws Exception {
        return loadUserCaptions(sender, mPage + 1);
    }

    @Override
    public boolean hasMore() {
        return mPage < mMaxPage;
    }

    private RequestHandle loadUserCaptions(final ResponseSender<List<TrackwayCollection>> sender, final int page) throws Exception {
        Logger.d("---> 刷新 " + page + " / " + mMaxPage);
        String sAccount = Security.aesEncrypt(mCurrentAccount);
        String sPage = Security.aesEncrypt(String.valueOf(page));
        String sPerPage = Security.aesEncrypt(String.valueOf(mPerPage));

        Call<TrackwayCollectionRepo> call = ApiModule.apiService_1().myTravelTogetherCollections(sAccount, sPage, sPerPage);
        call.enqueue(new Callback<TrackwayCollectionRepo>() {
            @Override
            public void onResponse(Call<TrackwayCollectionRepo> call, Response<TrackwayCollectionRepo> response) {
                TrackwayCollectionRepo repo = response.body();
                mMaxPage = (repo.getRecordCount() + mPerPage - 1) / mPerPage;
                if (repo.isSuccess()) {
                    mPage = page;
                    sender.sendData(repo.getTrackwayCollectionList());
                } else {
                    mPage = page;
                    sender.sendData(repo.getTrackwayCollectionList());
                }
            }

            @Override
            public void onFailure(Call<TrackwayCollectionRepo> call, Throwable t) {
                sender.sendError(new Exception(t));
            }
        });

        return new RetrofitRequestHandle(call);
    }
}

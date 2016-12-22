package com.ruihai.xingka.ui.trackway.datasource;

import com.orhanobut.logger.Logger;
import com.ruihai.xingka.api.ApiModule;
import com.ruihai.xingka.api.RetrofitRequestHandle;
import com.ruihai.xingka.api.model.MyTrackway;
import com.ruihai.xingka.api.model.MyTrackwayRepo;
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
 * 我的旅拼发布数据源
 * Created by mac on 16/5/19.
 */
public class MyTrackwayDataSource implements IAsyncDataSource<List<MyTrackway>> {

    private static final int DEFAULT_PER_PAGE = 10;

    private int mPage;
    private int mMaxPage = 0;
    private int mPerPage = DEFAULT_PER_PAGE;

    private String mCurrentAccount;
    private String mIsHidden;

    public MyTrackwayDataSource(String account, String isHidden) {
        mCurrentAccount = account;
        mIsHidden = isHidden;
    }

    @Override
    public RequestHandle refresh(ResponseSender<List<MyTrackway>> sender) throws Exception {
        EventBus.getDefault().post(new UpdateCountEvent());
        return loadUserCaptions(sender, 1);
    }

    @Override
    public RequestHandle loadMore(ResponseSender<List<MyTrackway>> sender) throws Exception {
        return loadUserCaptions(sender, mPage + 1);
    }

    @Override
    public boolean hasMore() {
        return mPage < mMaxPage;
    }

    private RequestHandle loadUserCaptions(final ResponseSender<List<MyTrackway>> sender, final int page) throws Exception {
        Logger.d("---> 刷新 " + page + " / " + mMaxPage);
        String sAccount = Security.aesEncrypt(mCurrentAccount);
        String sIsHidden = Security.aesEncrypt(String.valueOf(mIsHidden));
        String sPage = Security.aesEncrypt(String.valueOf(page));
        String sPerPage = Security.aesEncrypt(String.valueOf(mPerPage));

        Call<MyTrackwayRepo> call = ApiModule.apiService_1().myTravelTogether(sAccount, sIsHidden, sPage, sPerPage);
        call.enqueue(new Callback<MyTrackwayRepo>() {
            @Override
            public void onResponse(Call<MyTrackwayRepo> call, Response<MyTrackwayRepo> response) {
                MyTrackwayRepo repo = response.body();
                mMaxPage = (repo.getRecordCount() + mPerPage - 1) / mPerPage;
                if (repo.isSuccess()) {
                    updateCountEvent();
                    mPage = page;
                    sender.sendData(repo.getUserTrackwayInfoList());
                } else {
                    mPage = page;
                    sender.sendData(repo.getUserTrackwayInfoList());
                }
            }

            @Override
            public void onFailure(Call<MyTrackwayRepo> call, Throwable t) {
                sender.sendError(new Exception(t));
            }
        });

        return new RetrofitRequestHandle(call);
    }

    private void updateCountEvent() {
        EventBus.getDefault().post(new UpdateCountEvent());
    }
}

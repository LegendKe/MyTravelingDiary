package com.ruihai.xingka.ui.trackway.datasource;

import com.ruihai.xingka.api.ApiModule;
import com.ruihai.xingka.api.RetrofitRequestHandle;
import com.ruihai.xingka.api.XKApiService;
import com.ruihai.xingka.api.model.TrackwayInfo;
import com.ruihai.xingka.api.model.TrackwayInfoListRepo;
import com.ruihai.xingka.utils.Security;
import com.shizhefei.mvc.IAsyncDataSource;
import com.shizhefei.mvc.RequestHandle;
import com.shizhefei.mvc.ResponseSender;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 旅拼主页数据源
 * Created by mac on 16/5/4.
 */
public class TrackwayMainDataSource implements IAsyncDataSource<List<TrackwayInfo>> {

    private static final int DEFAULT_PER_PAGE = 10;

    private int mPage;
    private int mMaxPage = 0;
    private int mPerPage = DEFAULT_PER_PAGE;

    private String mCurrentAccount;//行咖号
    private String mTimeStamp;//时间戳，格式“yyyy-MM-dd HH:mm:ss”
    private List<TrackwayInfo> trackwayInfoList = new ArrayList<>();

    public TrackwayMainDataSource(String account, String timeStamp) {
        this.mCurrentAccount = account;
        this.mTimeStamp = timeStamp;
    }

    @Override
    public RequestHandle refresh(ResponseSender<List<TrackwayInfo>> responseSender) throws Exception {
        return loadTrackways(responseSender, 1);
    }

    @Override
    public RequestHandle loadMore(ResponseSender<List<TrackwayInfo>> responseSender) throws Exception {
        return loadTrackways(responseSender, mPage + 1);
    }

    @Override
    public boolean hasMore() {
        return mPage < mMaxPage;
    }

    private RequestHandle loadTrackways(final ResponseSender<List<TrackwayInfo>> sender, final int page) throws Exception {
        final String sAccount = Security.aesEncrypt(mCurrentAccount); // 行咖号
        String sOnlyFriends = Security.aesEncrypt(String.valueOf("0")); // 0：所有；1：关注；2：最新（不包括关注）
        String sPage = Security.aesEncrypt(String.valueOf(page));
        String sPerPage = Security.aesEncrypt(String.valueOf(mPerPage));

        if (page == 1) {
            mTimeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        }
        String sTimeStamp = Security.aesEncrypt(mTimeStamp); // 时间戳，格式“yyyy-MM-dd HH:mm:ss”

        Call<TrackwayInfoListRepo> call = ApiModule.apiService_1().getTravelTogetherMain(sAccount, sOnlyFriends, sTimeStamp, sPage, sPerPage);
        call.enqueue(new Callback<TrackwayInfoListRepo>() {
            @Override
            public void onResponse(Call<TrackwayInfoListRepo> call, Response<TrackwayInfoListRepo> response) {
                TrackwayInfoListRepo repo = response.body();
                mMaxPage = (repo.getRecordCount() + mPerPage - 1) / mPerPage;
                mPage = page;
                if (trackwayInfoList != null)
                    trackwayInfoList.clear();
                trackwayInfoList.addAll(repo.getTrackwayInfoList());
                sender.sendData(trackwayInfoList);
            }

            @Override
            public void onFailure(Call<TrackwayInfoListRepo> call, Throwable t) {
                sender.sendError(new Exception(t));
            }
        });
        return new RetrofitRequestHandle(call);

    }
}

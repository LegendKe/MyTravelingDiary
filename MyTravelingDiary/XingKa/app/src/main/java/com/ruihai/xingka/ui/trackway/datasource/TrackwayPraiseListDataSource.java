package com.ruihai.xingka.ui.trackway.datasource;

import com.orhanobut.logger.Logger;
import com.ruihai.xingka.api.ApiModule;
import com.ruihai.xingka.api.RetrofitRequestHandle;
import com.ruihai.xingka.api.model.PraiseItem;
import com.ruihai.xingka.api.model.PraiseRepo;
import com.ruihai.xingka.utils.Security;
import com.shizhefei.mvc.IAsyncDataSource;
import com.shizhefei.mvc.RequestHandle;
import com.shizhefei.mvc.ResponseSender;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 旅拼点赞列表数据源
 * Created by lqfang on 16/5/18.
 */
public class TrackwayPraiseListDataSource implements IAsyncDataSource<List<PraiseItem>> {

    private static final int DEFAULT_PER_PAGE = 20;

    private int mPage;
    private int mMaxPage = 0;
    private int mPerPage = DEFAULT_PER_PAGE;

    private String mCurrentAccount; // 行咖号
    private String mUserAccount; // 作者行咖号
    private String mGuid; // 旅拼ID

    public TrackwayPraiseListDataSource(String account, String userAccount, String gUid) {
        this.mCurrentAccount = account;
        this.mUserAccount = userAccount;
        this.mGuid = gUid;
    }

    @Override
    public RequestHandle refresh(ResponseSender<List<PraiseItem>> sender) throws Exception {
        return loadCaptions(sender, 1);
    }

    @Override
    public RequestHandle loadMore(ResponseSender<List<PraiseItem>> sender) throws Exception {
        return loadCaptions(sender, mPage + 1);
    }

    @Override
    public boolean hasMore() {
        return mPage < mMaxPage;
    }

    private RequestHandle loadCaptions(final ResponseSender<List<PraiseItem>> sender, final int page) throws Exception {
        Logger.d("---> 刷新 " + page + " / " + mMaxPage);
        String sAccount = Security.aesEncrypt(mCurrentAccount);
        String sUserAccount = Security.aesEncrypt(mUserAccount);
        String sGUid = Security.aesEncrypt(String.valueOf(mGuid));
        String sPage = Security.aesEncrypt(String.valueOf(page));
        String sPerPage = Security.aesEncrypt(String.valueOf(mPerPage));

        Call<PraiseRepo> call = ApiModule.apiService_1().travelTogetherPraiseListPage(sAccount, sGUid, sUserAccount, sPage, sPerPage);
        call.enqueue(new Callback<PraiseRepo>() {
            @Override
            public void onResponse(Call<PraiseRepo> call, Response<PraiseRepo> response) {
                PraiseRepo repo = response.body();
                mMaxPage = (repo.getRecordCount() + mPerPage - 1) / mPerPage;
                if (repo.isSuccess()) {
                    mPage = page;
                    sender.sendData(repo.getPraiseItemItemList());
                } else {
                    mPage = page;
                    sender.sendData(repo.getPraiseItemItemList());
                }
            }

            @Override
            public void onFailure(Call<PraiseRepo> call, Throwable t) {
                sender.sendError(new Exception(t));
            }
        });

        return new RetrofitRequestHandle(call);
    }
}

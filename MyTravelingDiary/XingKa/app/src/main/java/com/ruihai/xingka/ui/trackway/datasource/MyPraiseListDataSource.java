package com.ruihai.xingka.ui.trackway.datasource;

import com.orhanobut.logger.Logger;
import com.ruihai.xingka.api.ApiModule;
import com.ruihai.xingka.api.RetrofitRequestHandle;
import com.ruihai.xingka.api.model.PraiseInfo;
import com.ruihai.xingka.api.model.PraiseListRepo;
import com.ruihai.xingka.utils.Security;
import com.shizhefei.mvc.IAsyncDataSource;
import com.shizhefei.mvc.RequestHandle;
import com.shizhefei.mvc.ResponseSender;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 我的旅拼点赞人列表
 * Created by lqfang on 16/5/19.
 */
public class MyPraiseListDataSource implements IAsyncDataSource<List<PraiseInfo>> {
    private static final int DEFAULT_PER_PAGE = 20;

    private int mPage;
    private int mMaxPage = 0;
    private int mPerPage = DEFAULT_PER_PAGE;

    private String mCurrentAccount; // 行咖号
    private String mUserAccount; // 作者行咖号

    public MyPraiseListDataSource(String account, String userAccount) {
        this.mCurrentAccount = account;
        this.mUserAccount = userAccount;
    }

    @Override
    public RequestHandle refresh(ResponseSender<List<PraiseInfo>> sender) throws Exception {
        return loadCaptions(sender, 1);
    }

    @Override
    public RequestHandle loadMore(ResponseSender<List<PraiseInfo>> sender) throws Exception {
        return loadCaptions(sender, mPage + 1);
    }

    @Override
    public boolean hasMore() {
        return mPage < mMaxPage;
    }

    private RequestHandle loadCaptions(final ResponseSender<List<PraiseInfo>> sender, final int page) throws Exception {
        Logger.d("---> 刷新 " + page + " / " + mMaxPage);
        String sAccount = Security.aesEncrypt(mCurrentAccount);
        String sUserAccount = Security.aesEncrypt(mUserAccount);
        String sPage = Security.aesEncrypt(String.valueOf(page));
        String sPerPage = Security.aesEncrypt(String.valueOf(mPerPage));

        Call<PraiseListRepo> call = ApiModule.apiService_1().myTravelTogetherPraiseListPage(sAccount, sUserAccount, sPage, sPerPage);
        call.enqueue(new Callback<PraiseListRepo>() {
            @Override
            public void onResponse(Call<PraiseListRepo> call, Response<PraiseListRepo> response) {
                PraiseListRepo repo = response.body();
                mMaxPage = (repo.getRecordCount() + mPerPage - 1) / mPerPage;
                if (repo.isSuccess()) {
                    mPage = page;
                    sender.sendData(repo.getPraiseInfos());
                } else {
                    mPage = page;
                    sender.sendData(repo.getPraiseInfos());
                }
            }

            @Override
            public void onFailure(Call<PraiseListRepo> call, Throwable t) {
                sender.sendError(new Exception(t));
            }
        });
        return new RetrofitRequestHandle(call);
    }
}

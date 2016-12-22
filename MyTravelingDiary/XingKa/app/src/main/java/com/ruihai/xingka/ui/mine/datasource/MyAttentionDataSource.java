package com.ruihai.xingka.ui.mine.datasource;

import android.util.Log;

import com.orhanobut.logger.Logger;
import com.ruihai.xingka.api.ApiModule;
import com.ruihai.xingka.api.RetrofitRequestHandle;
import com.ruihai.xingka.api.model.MyFriendInfoRepo;
import com.ruihai.xingka.utils.Security;
import com.shizhefei.mvc.IAsyncDataSource;
import com.shizhefei.mvc.RequestHandle;
import com.shizhefei.mvc.ResponseSender;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mac on 15/11/13.
 */
public class MyAttentionDataSource implements IAsyncDataSource<List<MyFriendInfoRepo.MyFriendInfo>> {
    private static final int DEFAULT_PER_PAGE = 20;
    private int mPage;
    private int mMaxPage = 0;
    private int mPerPage = DEFAULT_PER_PAGE;

    private String mCurrentAccount;
    private String mUserAccount;
    private String mType;

    public MyAttentionDataSource(String mAccount, String mUserAccount, String mType) {
        this.mCurrentAccount = mAccount;
        this.mUserAccount = mUserAccount;
        this.mType = mType;
    }

    @Override
    public RequestHandle refresh(ResponseSender<List<MyFriendInfoRepo.MyFriendInfo>> sender) throws Exception {
        return loadAttentionData(sender, 1);
    }

    @Override
    public RequestHandle loadMore(ResponseSender<List<MyFriendInfoRepo.MyFriendInfo>> sender) throws Exception {
        return loadAttentionData(sender, mPage + 1);
    }

    private RequestHandle loadAttentionData(final ResponseSender<List<MyFriendInfoRepo.MyFriendInfo>> sender, final int page) throws Exception {
        Logger.d("---> 刷新 " + page + " / " + mMaxPage);
        String sAccount = Security.aesEncrypt(mCurrentAccount);//我的行咖号
        String sUserAccount = Security.aesEncrypt(mUserAccount);//当前页行咖号
        String sType = Security.aesEncrypt(mType);//类型
        String sPage = Security.aesEncrypt(String.valueOf(page));//页数
        String sPerPage = Security.aesEncrypt(String.valueOf(mPerPage));//每页数量

        Call<MyFriendInfoRepo> call = ApiModule.apiService_1().myUsersList(sAccount, sUserAccount, sType, sPage, sPerPage);
        call.enqueue(new Callback<MyFriendInfoRepo>() {
            @Override
            public void onResponse(Call<MyFriendInfoRepo> call, Response<MyFriendInfoRepo> response) {
                MyFriendInfoRepo repo = response.body();
                mMaxPage = (repo.getRecordCount() + mPerPage - 1) / mPerPage;
                if (repo.isSuccess()) {
                    mPage = page;
                    sender.sendData(repo.getMyFriendInfoList());
                } else {
                    mPage = page;
                    sender.sendData(repo.getMyFriendInfoList());
                }
            }

            @Override
            public void onFailure(Call<MyFriendInfoRepo> call, Throwable t) {
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

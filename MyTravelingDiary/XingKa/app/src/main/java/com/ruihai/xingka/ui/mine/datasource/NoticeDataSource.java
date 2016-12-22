package com.ruihai.xingka.ui.mine.datasource;

import com.orhanobut.logger.Logger;
import com.ruihai.xingka.api.ApiModule;
import com.ruihai.xingka.api.RetrofitRequestHandle;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.api.model.OfficialMessageDetail;
import com.ruihai.xingka.api.model.OfficialMessageRepo;
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
 * 官方通知数据源
 * Created by gjzhang on 15/11/19.
 */

public class NoticeDataSource implements IAsyncDataSource<List<OfficialMessageDetail>> {
    private static final int DEFAULT_PER_PAGE = 20;
    private static final int DEFAULT_TYPE = 0;
    private int mPage;
    private int mMaxPage = 0;
    private int mPerPage = DEFAULT_PER_PAGE;
    private int mType = DEFAULT_TYPE;
    private String mCurrentAccount;

    public NoticeDataSource(String mAccount) {
        this.mCurrentAccount = mAccount;
    }

    @Override
    public RequestHandle refresh(ResponseSender<List<OfficialMessageDetail>> sender) throws Exception {
        return loadMessageData(sender, 1);
    }

    @Override
    public RequestHandle loadMore(ResponseSender<List<OfficialMessageDetail>> sender) throws Exception {
        return loadMessageData(sender, mPage + 1);
    }

    private RequestHandle loadMessageData(final ResponseSender<List<OfficialMessageDetail>> sender, final int page) throws Exception {
        Logger.d("---> 刷新 " + page + " / " + mMaxPage);
        String sAccount = Security.aesEncrypt(mCurrentAccount);//我的行咖号
        String sPage = Security.aesEncrypt(String.valueOf(page));//页数
        String sPerPage = Security.aesEncrypt(String.valueOf(mPerPage));//每页数量
        String sType = Security.aesEncrypt(String.valueOf(mType));//消息类型
        String sAttr = Security.aesEncrypt("9");
        String sPaltform = Security.aesEncrypt("1");

        Call<OfficialMessageRepo> call = ApiModule.apiService_1().officialMessageList(sAccount, sType, sAttr, sPaltform, sPage, sPerPage);
        call.enqueue(new Callback<OfficialMessageRepo>() {
            @Override
            public void onResponse(Call<OfficialMessageRepo> call, Response<OfficialMessageRepo> response) {
                OfficialMessageRepo repo = response.body();
                mMaxPage = (repo.getRecordCount() + mPerPage - 1) / mPerPage;
                ReadMsg readMsg = new ReadMsg(6);
                EventBus.getDefault().post(readMsg);
                if (repo.isSuccess()) {
                    mPage = page;
                    sender.sendData(repo.getOfficialMessages());
                    //保存系统通知
                    AccountInfo.getInstance().saveOfficialNum(repo.getRecordCount());
                } else {
                    mPage = page;
                    sender.sendData(repo.getOfficialMessages());
                }
            }

            @Override
            public void onFailure(Call<OfficialMessageRepo> call, Throwable t) {
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



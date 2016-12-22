package com.ruihai.xingka.ui.caption.datasource;

import com.orhanobut.logger.Logger;
import com.ruihai.xingka.api.ApiModule;
import com.ruihai.xingka.api.RetrofitRequestHandle;
import com.ruihai.xingka.api.XKApiService;
import com.ruihai.xingka.api.model.PhotoTopic;
import com.ruihai.xingka.api.model.PhotoTopicListRepo;
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
 * Created by zecker on 16/6/6.
 */
public class CaptionListDataSource implements IAsyncDataSource<List<PhotoTopic>> {
    private static final int DEFAULT_PER_PAGE = 10;

    private int mPage;
    private int mMaxPage = 0;
    private int mPerPage = DEFAULT_PER_PAGE;

    private String mCurrentAccount; // 行咖号
    private String mTimeStamp; // 时间戳，格式“yyyy-MM-dd HH:mm:ss”
    private int isShowFollow;
    private List<PhotoTopic> mPhotoTopics = new ArrayList<>();
    private int mType;

    public CaptionListDataSource(String account, int isShowFollow, String timeStamp, int type) {
        this.mCurrentAccount = account;
        this.isShowFollow = isShowFollow;
        this.mTimeStamp = timeStamp;
        this.mType = type;
    }

    @Override
    public RequestHandle refresh(ResponseSender<List<PhotoTopic>> sender) throws Exception {
        return loadCaptions(sender, 1);
    }

    @Override
    public RequestHandle loadMore(ResponseSender<List<PhotoTopic>> sender) throws Exception {
        return loadCaptions(sender, mPage + 1);
    }

    @Override
    public boolean hasMore() {
        return mPage < mMaxPage;
    }

    private RequestHandle loadCaptions(final ResponseSender<List<PhotoTopic>> sender, final int page) throws Exception {
        Logger.d("---> 刷新 " + page + " / " + mMaxPage);
        final String sAccount = Security.aesEncrypt(mCurrentAccount);//行咖号
        String sOnlyFriends = Security.aesEncrypt(String.valueOf(isShowFollow));
        String sPage = Security.aesEncrypt(String.valueOf(page));
        String sPerPage = Security.aesEncrypt(String.valueOf(mPerPage));
        if (page == 1) {
            mTimeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        }
        String sTimeStamp = Security.aesEncrypt(mTimeStamp);//时间戳，格式“yyyy-MM-dd HH:mm:ss”

        XKApiService apiService = ApiModule.apiService_1();
        Call<PhotoTopicListRepo> call;
        if (mType == 2) { // 推荐图说
            call = apiService.photoTopicRecommendList(sAccount, sPage, sPerPage);
            call.enqueue(new Callback<PhotoTopicListRepo>() {
                @Override
                public void onResponse(Call<PhotoTopicListRepo> call, Response<PhotoTopicListRepo> response) {
                    PhotoTopicListRepo repo = response.body();
                    mMaxPage = (repo.getRecordCount() + mPerPage - 1) / mPerPage;
                    mPage = page;
                    if (mPhotoTopics != null)
                        mPhotoTopics.clear();
                    mPhotoTopics.addAll(repo.getPhotoTopicList());
                    sender.sendData(mPhotoTopics);
                }

                @Override
                public void onFailure(Call<PhotoTopicListRepo> call, Throwable t) {
                    sender.sendError(new Exception(t));
                }
            });

        } else { // 发现和关注
            call = apiService.photoTopicMain(sAccount, sOnlyFriends, sPage, sPerPage, sTimeStamp);
            call.enqueue(new Callback<PhotoTopicListRepo>() {
                @Override
                public void onResponse(Call<PhotoTopicListRepo> call, Response<PhotoTopicListRepo> response) {
                    final PhotoTopicListRepo repo = response.body();
                    mMaxPage = (repo.getRecordCount() + mPerPage - 1) / mPerPage;
                    mPage = page;
                    if (mPhotoTopics != null)
                        mPhotoTopics.clear();
                    mPhotoTopics.addAll(repo.getPhotoTopicList());
                    // 关注页面请求官方最新图说一条
                    if (page == 1 && mType == 1) {
                        // 加载关注页最新一条图说
                        ApiModule.apiService_1().getPhotoTopicOfficialList(sAccount).enqueue(new Callback<PhotoTopicListRepo>() {

                            @Override
                            public void onResponse(Call<PhotoTopicListRepo> call, Response<PhotoTopicListRepo> response) {
                                PhotoTopicListRepo photoTopicListRepo = response.body();
                                if (photoTopicListRepo.isSuccess()) {
                                    mPhotoTopics.add(0, photoTopicListRepo.getPhotoTopicList().get(0));
                                }
                                sender.sendData(mPhotoTopics);
                            }

                            @Override
                            public void onFailure(Call<PhotoTopicListRepo> call, Throwable t) {
                                sender.sendData(mPhotoTopics);
                            }
                        });
                    } else {
                        sender.sendData(mPhotoTopics);
                    }
                }

                @Override
                public void onFailure(Call<PhotoTopicListRepo> call, Throwable t) {
                    sender.sendError(new Exception(t));
                }
            });
        }
        return new RetrofitRequestHandle(call);
    }
}

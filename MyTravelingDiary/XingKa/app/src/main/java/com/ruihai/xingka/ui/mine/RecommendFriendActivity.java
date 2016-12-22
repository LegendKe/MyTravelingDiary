package com.ruihai.xingka.ui.mine;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.ruihai.iconicfontengine.widget.IconicFontTextView;
import com.ruihai.xingka.R;
import com.ruihai.xingka.api.ApiModule;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.api.model.RecommendInfo;
import com.ruihai.xingka.api.model.TagInfo;
import com.ruihai.xingka.api.model.User;
import com.ruihai.xingka.api.model.XKRepo;
import com.ruihai.xingka.ui.BaseActivity;
import com.ruihai.xingka.ui.mine.adapter.RecommendAdapter;
import com.ruihai.xingka.ui.mine.adapter.RecommendUserAdapter;
import com.ruihai.xingka.utils.Security;
import com.ruihai.xingka.widget.ListViewForScrollView;
import com.ruihai.xingka.widget.ProgressHUD;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * 行咖推荐页面
 * Created by apple on 15/9/10.
 */
public class RecommendFriendActivity extends BaseActivity {

    @BindView(R.id.lvfs_labal)
    ListViewForScrollView mLvLabal;
    @BindView(R.id.lv_recommend)
    ListView mLvRecommend;
    @BindView(R.id.tv_title)
    TextView mTitle;
    @BindView(R.id.tv_right)
    IconicFontTextView mRight;

    List<String> labalList = new ArrayList<String>();
    List<TagInfo> tag = new ArrayList<TagInfo>();
    List<RecommendInfo> recommendInfoList = new ArrayList<RecommendInfo>();

    RecommendAdapter recommendAdapter;
    RecommendUserAdapter recommendUserAdapter;
    String version;
    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xingka_recommend);

        ButterKnife.bind(this);
        mTitle.setText("行咖推荐");
        mRight.setVisibility(View.GONE);
        currentUser = AccountInfo.getInstance().loadAccount();
        getLabel();
    }

    @OnClick(R.id.tv_back)
    void onBack() {
        finish();
    }

    private void getLabel() {//查询所有标签

        ProgressHUD.showLoadingMessage(mContext, "正在努力搜索...", false);

        ApiModule.apiService().getSysTagList(Security.aesEncrypt("3"),
                Security.aesEncrypt(currentUser.version)).enqueue(new Callback<XKRepo>() {
            @Override
            public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                XKRepo xkRepo = response.body();
                String msg = xkRepo.getMsg();
                Log.e("version", currentUser.version);
                Log.e("getVersion", xkRepo.getVersion() + "");
                if (currentUser.version != xkRepo.getVersion() + "") {
                    currentUser.version = xkRepo.getVersion() + " ";
                    tag = xkRepo.getTagInfo();
                    currentUser.setTagInfo(tag);
                    AccountInfo.getInstance().saveAccount(currentUser);
                    if (xkRepo.isSuccess()) { // 登录成功
                        labalList.add("推荐");
                        for (int i = 0; i < tag.size(); i++) {
                            labalList.add(tag.get(i).getName());
                        }
                        recommendAdapter = new RecommendAdapter(mContext, labalList);
                        mLvLabal.setAdapter(recommendAdapter);
                        recommendView(0);
                        ProgressHUD.dismiss();
                    } else { //登录失败
                        ProgressHUD.showErrorMessage(mContext, msg);
                    }
                } else {
                    tag = currentUser.getTagInfo();
                    labalList.add("推荐");
                    for (int i = 0; i < tag.size(); i++) {
                        labalList.add(tag.get(i).getName());
                    }
                    recommendAdapter = new RecommendAdapter(mContext, labalList);
                    mLvLabal.setAdapter(recommendAdapter);
                    recommendView(0);
                }
            }

            @Override
            public void onFailure(Call<XKRepo> call, Throwable t) {
                Log.e("error", t.getLocalizedMessage());
                ProgressHUD.showErrorMessage(mContext, t.getLocalizedMessage());
            }
        });
    }

    @OnItemClick(R.id.lvfs_labal)
    void onLabelListview(int position) {
        if (recommendAdapter.curPosition != position) {
            ProgressHUD.showLoadingMessage(mContext, "正在努力搜索...", false);
            recommendView(position);
            recommendAdapter.curPosition = position;
            recommendAdapter.notifyDataSetChanged();
        }
    }

    private void recommendView(int position) {
        String tagId = null;
        if (position == 0) {
            tagId = String.valueOf(0);
        } else if (position > 0) {
            tagId = String.valueOf(tag.get(position - 1).getId());
        }
        Logger.e("行咖号--" + currentUser.getAccount() + "-->" + Security.aesEncrypt(currentUser.getAccount() + "") +
                "位置--" + tagId + "-->" + Security.aesEncrypt(tagId) +
                "版本号--" + currentUser.version + "-->" + Security.aesEncrypt(currentUser.version));
        ApiModule.apiService().recommendUserList(Security.aesEncrypt(String.valueOf(currentUser.getAccount())), Security.aesEncrypt(tagId),
                Security.aesEncrypt(currentUser.version)).enqueue(new Callback<XKRepo>() {
            @Override
            public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                XKRepo xkRepo = response.body();
                String msg = xkRepo.getMsg();
                recommendInfoList = xkRepo.getRecommendInfo();
                if (xkRepo.isSuccess()) { // 登录成功
                    recommendUserAdapter = new RecommendUserAdapter(mContext, recommendInfoList);
                    mLvRecommend.setDividerHeight(0);
                    //View view=LayoutInflater.from(RecommendFriendActivity.this).inflate(R.layout.profile_list_load_empty);
                    //mLvRecommend.setEmptyView();
                    mLvRecommend.setAdapter(recommendUserAdapter);

                } else { // 登录失败
                    ProgressHUD.showErrorMessage(mContext, msg);
                }
                ProgressHUD.dismiss();
            }

            @Override
            public void onFailure(Call<XKRepo> call, Throwable t) {
                Log.e("error", t.getLocalizedMessage());
                ProgressHUD.dismiss();
                ProgressHUD.showErrorMessage(mContext, t.getLocalizedMessage());
            }

        });
    }

}

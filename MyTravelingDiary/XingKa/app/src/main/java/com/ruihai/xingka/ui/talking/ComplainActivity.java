package com.ruihai.xingka.ui.talking;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.ruihai.iconicfontengine.widget.IconicFontTextView;
import com.ruihai.xingka.AppSettings;
import com.ruihai.xingka.R;
import com.ruihai.xingka.api.ApiModule;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.api.model.ReportInfo;
import com.ruihai.xingka.api.model.ReportType;
import com.ruihai.xingka.ui.BaseActivity;
import com.ruihai.xingka.ui.login.LoginActivity;
import com.ruihai.xingka.ui.talking.adapter.MaterialAdapter;
import com.ruihai.xingka.ui.talking.adapter.ReportTypeAdapter;
import com.ruihai.xingka.utils.AppUtility;
import com.ruihai.xingka.utils.Security;
import com.ruihai.xingka.widget.AppNoScrollerListView;
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
 * 投诉
 * Created by lqfang on 16/8/6.
 */
public class ComplainActivity extends BaseActivity {

    public static void launch(Context context) {
        Intent intent = new Intent(context, ComplainActivity.class);
//        intent.putExtra("userAccount", userAccount);
        context.startActivity(intent);
    }

    @BindView(R.id.tv_title)
    TextView mTitle;
    @BindView(R.id.tv_right)
    IconicFontTextView mRignt;
    @BindView(R.id.listview)
    ListView listview;
    @BindView(R.id.appns_list)
    AppNoScrollerListView appns_list;

    private List<ReportType> reportInfoList = new ArrayList<>();

    private ReportTypeAdapter reportTypeAdapter;
    private MaterialAdapter materialAdapter;

    private String[] materialType = {"必填"};

    private String reportType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complain);
        ButterKnife.bind(this);
        initViews();
    }

    private void initViews() {
        mTitle.setText("投诉");

        if (AccountInfo.getInstance().getReportType() == null) {  //获取投诉类型
            initReportType();
        } else {
            reportInfoList = AccountInfo.getInstance().getReportType();
        }

        int value = AppSettings.getReportTypeSetting();
//        recycleview.setLayoutManager(new LinearLayoutManager(this));
        reportTypeAdapter = new ReportTypeAdapter(this, reportInfoList, value);
        listview.setAdapter(reportTypeAdapter);

        materialAdapter = new MaterialAdapter(this, materialType);
        appns_list.setAdapter(materialAdapter);
        materialAdapter.notifyDataSetChanged();

    }

    /**
     * @功能描述 : 获取举报类型
     */
    private void initReportType() {
        int mVersion = AppUtility.getAppVersionCode();
        String sVersion = Security.aesEncrypt(String.valueOf(mVersion));
        ApiModule.apiService().photoTopicReportTypeList(sVersion).enqueue(new Callback<ReportInfo>() {


            @Override
            public void onResponse(Call<ReportInfo> call, Response<ReportInfo> response) {
                ReportInfo reportInfo = response.body();
                if (reportInfo.isSuccess()) {
                    reportInfoList = reportInfo.getListMessage();
                    //保存举报类型到本地
                    AccountInfo.getInstance().saveReportType(reportInfoList);
                } else {
                    ProgressHUD.showInfoMessage(mContext, reportInfo.getMsg());
                }
            }

            @Override
            public void onFailure(Call<ReportInfo> call, Throwable t) {
                ProgressHUD.showInfoMessage(mContext, getString(R.string.common_network_error));
            }
        });
    }


    /**
     * @功能描述 : 提交举报数据至服务器
     */
    private void submitReport(String type) {
        if (!AccountInfo.getInstance().isLogin()) {
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }

//        ProgressHUD.showLoadingMessage(mContext, "正在提交举报", false);
//        String account = Security.aesEncrypt(String.valueOf(currentUser.getAccount()));
//        String pGuid = Security.aesEncrypt(mPhotoTopic.getpGuid());
//        String toAccount = Security.aesEncrypt(String.valueOf(mPhotoTopic.getAccount()));
//        String sType = Security.aesEncrypt(type);
//
//        ApiModule.apiService().photoTopicReportListAdd2(account, pGuid, toAccount, sType).enqueue(new Callback<XKRepo>() {
//
//
//            @Override
//            public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
//                XKRepo xkRepo = response.body();
//                ProgressHUD.dismiss();
//                if (xkRepo.isSuccess()) {
//                    ProgressHUD.showSuccessMessage(mContext, "举报成功");
//                } else {
//                    ProgressHUD.showInfoMessage(mContext, xkRepo.getMsg());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<XKRepo> call, Throwable t) {
//                ProgressHUD.showInfoMessage(mContext, getString(R.string.common_network_error));
//            }
//
//        });
    }

    @OnClick(R.id.tv_back)
    void onBack() {
        finish();
    }

    @OnClick(R.id.tv_right)
    void onComplete() {
        int value = AppSettings.getReportTypeSetting();
        if (value < reportInfoList.size()) {
            reportType = reportInfoList.get(value).getTitle();
//        Log.e("TAG","测试选择的举报类型-->"+reportType);
        } else {
            reportType = "";
        }


        if (reportType.equals("")) {
            ProgressHUD.showInfoMessage(mContext, "请选择投诉原因");
            return;
        }

        if (value < reportInfoList.size()) {
            submitReport(String.valueOf(value));
        }

    }

    @OnItemClick(R.id.listview)
    void onItemClick(int position) {
        AppSettings.setReportTypeSetting(position);
        reportTypeAdapter.reportTypeSelectedIndex(position);
    }

}

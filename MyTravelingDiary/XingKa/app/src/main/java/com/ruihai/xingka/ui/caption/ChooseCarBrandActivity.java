package com.ruihai.xingka.ui.caption;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eowise.recyclerview.stickyheaders.StickyHeadersBuilder;
import com.eowise.recyclerview.stickyheaders.StickyHeadersItemDecoration;
import com.orhanobut.logger.Logger;
import com.ruihai.iconicfontengine.widget.IconicFontTextView;
import com.ruihai.xingka.R;
import com.ruihai.xingka.api.ApiModule;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.api.model.CarBrandRepo;
import com.ruihai.xingka.api.model.CarBrandRepo.CarBrand;
import com.ruihai.xingka.api.model.UserCarInfo;
import com.ruihai.xingka.api.model.XKRepo;
import com.ruihai.xingka.event.OnItemClickListener;
import com.ruihai.xingka.ui.BaseActivity;
import com.ruihai.xingka.ui.caption.adapter.CarBrandAdapter;
import com.ruihai.xingka.ui.caption.adapter.InitialHeaderAdapter;
import com.ruihai.xingka.ui.caption.adapter.LetterCatalogAdapter;
import com.ruihai.xingka.ui.mine.EditUserDataActivity;
import com.ruihai.xingka.utils.AppUtility;
import com.ruihai.xingka.utils.GridSpacingItemDecoration;
import com.ruihai.xingka.widget.ClearableEditText;
import com.ruihai.xingka.utils.Security;
import com.ruihai.xingka.widget.ProgressHUD;
import com.ruihai.xingka.widget.ProgressLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * 车品牌页选择
 */
public class ChooseCarBrandActivity extends BaseActivity implements OnItemClickListener {

    @BindView(R.id.rv_catalog)
    RecyclerView mCatalogRecyclerView;
//    @BindView(R.id.et_search_key)
//    ClearableEditText mSearchKeyEditText;

    @BindView(R.id.tv_title)
    TextView mTitleView;
    @BindView(R.id.tv_right)
    TextView mRightView;
    @BindView(R.id.rv_list)
    RecyclerView mCarBrandRecyclerView;
    @BindView(R.id.progress_layout)
    ProgressLayout progressLayout;

    private List<CarBrand> mCacheCarBrands;
    private List<CarBrand> mCarBrands = new ArrayList<>();
    private CarBrandAdapter mCarBrandAdapter;
    private StickyHeadersItemDecoration mHeader;

    private LetterCatalogAdapter mCatalogAdapter;
    private UserCarInfo mUserCarInfo;
    private CarBrand carBrand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_car_brand);
        ButterKnife.bind(this);

        if (AccountInfo.getInstance().getCarBrand() == null) {
            getCarBrandList();
        }

        String userCarImage = "";
        mUserCarInfo = AccountInfo.getInstance().getUserCarInfo();
        if (mUserCarInfo != null) {
            userCarImage = mUserCarInfo.getImgkey();
        }

        initViews();
        initListeners();
    }

    private void initViews() {
//        mSearchKeyEditText.setHint(R.string.caption_input_brand_name);
        mTitleView.setText("选择汽车品牌");
        mRightView.setVisibility(View.GONE);
        if (mUserCarInfo != null) {
            mRightView.setVisibility(View.VISIBLE);
            mRightView.setText("删除");
        } else {
            mRightView.setVisibility(View.GONE);
        }

        mCarBrandRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mCatalogAdapter = new LetterCatalogAdapter();
        mCatalogRecyclerView.setLayoutManager(new GridLayoutManager(this, 7));
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing_10);
        mCatalogRecyclerView.addItemDecoration(new GridSpacingItemDecoration(spacingInPixels));
        mCatalogRecyclerView.setAdapter(mCatalogAdapter);

        mCacheCarBrands = CarBrandRepo.getCacheCarBrands();
        if (mCacheCarBrands != null) {
            mCarBrands.addAll(mCacheCarBrands);
        }
        mCarBrandAdapter = new CarBrandAdapter(mCarBrands);
        mCarBrandAdapter.setOnItemClickListener(this);
        mCarBrandAdapter.setHasStableIds(true);
        mHeader = new StickyHeadersBuilder()
                .setAdapter(mCarBrandAdapter)
                .setRecyclerView(mCarBrandRecyclerView)
                .setStickyHeadersAdapter(new InitialHeaderAdapter(mCarBrands))
                .build();
        mCarBrandRecyclerView.setAdapter(mCarBrandAdapter);
        mCarBrandRecyclerView.addItemDecoration(mHeader);
    }

    private void initListeners() {
        mCatalogAdapter.setOnTouchingLetterChangedListener(new LetterCatalogAdapter.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                // 该字母首次出现位置
                int position = mCarBrandAdapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    LinearLayoutManager layoutManager = (LinearLayoutManager) mCarBrandRecyclerView.getLayoutManager();
                    layoutManager.scrollToPositionWithOffset(position, 0);
                } else if (position == -1) {
                    AppUtility.showToast("暂没有该品牌的车型");
                }
            }
        });
    }

    private void getCarBrandList() {
        if (mCacheCarBrands == null)
            progressLayout.showLoading();

        String sVersion = Security.aesEncrypt(String.valueOf(CarBrandRepo.getCacheCarBrandVersion()));
        ApiModule.apiService().getCarBrandList(Security.aesEncrypt("0"), sVersion).enqueue(
                new Callback<CarBrandRepo>() {

                    @Override
                    public void onResponse(Call<CarBrandRepo> call, Response<CarBrandRepo> response) {
                        CarBrandRepo carBrandResponse=response.body();
                        progressLayout.showContent();
                        if (carBrandResponse.isSuccess()) {
                            mCarBrands = carBrandResponse.getCarBrandList();
                            mCarBrandAdapter.update(mCarBrands);
                            CarBrandRepo.setCacheCarBrands(mCarBrands);
                            CarBrandRepo.setCacheCarBrandVersion(carBrandResponse.getVersion());
                            //保存车品牌到本地
                            AccountInfo.getInstance().saveCarBrand(mCarBrands);
                        }
                    }

                    @Override
                    public void onFailure(Call<CarBrandRepo> call, Throwable t) {
                        ProgressHUD.showInfoMessage(mContext, getString(R.string.common_network_error));
                    }


                });
    }

    /**
     * 删除车品牌
     */
    private void deleteCardBrand() {

        String sAccount = Security.aesEncrypt(String.valueOf(currentUser.getAccount()));
        String sType = Security.aesEncrypt("brand");
        String sValue = Security.aesEncrypt(String.valueOf(0));
        ApiModule.apiService().editUserInfo(sAccount, sType, sValue).enqueue( new Callback<XKRepo>() {


            @Override
            public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                XKRepo xkRepo=response.body();
                if (xkRepo.isSuccess()) {
                    Logger.d("修改成功");
                    if (mUserCarInfo == null)
                        mUserCarInfo = new UserCarInfo();
                    mUserCarInfo.setImgkey("");
                    mUserCarInfo.setName("");
                    AccountInfo.getInstance().saveUserCarInfo(mUserCarInfo);
                    //清除本地记录
                    AccountInfo.getInstance().clearUserCarInfo();

                    Intent intent = new Intent();
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<XKRepo> call, Throwable t) {
                ProgressHUD.showErrorMessage(mContext, getString(R.string.common_network_error));
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
//        CarBrand carBrand = mCarBrands.get(position);
        carBrand = mCarBrands.get(position);
        Intent intent = new Intent();
        intent.putExtra("cardBrand", carBrand);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @OnClick(R.id.tv_back)
    void onBack() {
        finish();
    }

    @OnClick(R.id.tv_right)
    void onDelete() {
        // 1. 布局文件转换为View对象
        LayoutInflater inflater = LayoutInflater.from(mContext);
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.dialog_communal, null);
        final Dialog dialog = new AlertDialog.Builder(mContext).create();
        dialog.setCancelable(false);
        dialog.show();
        dialog.getWindow().setContentView(layout);

        // 3. 消息内容
        TextView dialog_msg = (TextView) layout.findViewById(R.id.update_content);
        dialog_msg.setText("确定删除我的车型吗?");

        // 4. 确定按钮
        Button btnOK = (Button) layout.findViewById(R.id.umeng_update_id_ok);
        btnOK.setText("确定");
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCardBrand();
//                mRightView.setVisibility(View.GONE);
                mCarBrandAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });

        // 5. 取消按钮
        Button btnCancel = (Button) layout.findViewById(R.id.umeng_update_id_cancel);
        btnCancel.setText("取消");
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onItemLongClick(View view, int position) {

    }

    @Override
    public void onItemChildClick(View childView, int position) {

    }
}

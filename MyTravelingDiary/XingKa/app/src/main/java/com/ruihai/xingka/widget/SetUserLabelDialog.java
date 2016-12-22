package com.ruihai.xingka.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import com.ruihai.xingka.R;
import com.ruihai.xingka.api.ApiModule;
import com.ruihai.xingka.api.model.TagInfo;
import com.ruihai.xingka.api.model.XKRepo;
import com.ruihai.xingka.ui.mine.adapter.UserLabelAdapter;
import com.ruihai.xingka.utils.Security;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by apple on 15/8/19.
 */
public class SetUserLabelDialog extends Dialog {

    @BindView(R.id.gv_label)
    GridView laberGv;
    @BindView(R.id.btn_sure)
    Button sureBtn;
    private Context context;

    private List<TagInfo> mTagInfos = new ArrayList<>();

    //定义回调事件，用于dialog的点击事件
    public interface OnSetLabelDialogListener {
        public void selectedLabels(String selectedTag);
    }

    private OnSetLabelDialogListener labelDialogListener;
    private UserLabelAdapter mAdapter;

    public SetUserLabelDialog(Context context, OnSetLabelDialogListener labelDialogListener) {
        super(context, R.style.CommonDialog);
        this.context = context;
        this.labelDialogListener = labelDialogListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_user_label);
        ButterKnife.bind(this);

        getTagLabels();

        if (TagInfo.getCacheSysTagInfos() != null)
            mTagInfos = TagInfo.getCacheSysTagInfos();
        mAdapter = new UserLabelAdapter(context, mTagInfos);
        laberGv.setAdapter(mAdapter);
        laberGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mAdapter.setSelectedTag(position);
            }
        });
    }

    private void getTagLabels() {
        String sType = Security.aesEncrypt("3"); // 用户标签
        String sVersion = Security.aesEncrypt(String.valueOf(TagInfo.getCacheSysTagInfoVersion())); // 当前版本号
        ApiModule.apiService().getSysTagList(sType, sVersion).enqueue(new Callback<XKRepo>() {
            @Override
            public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                XKRepo xkRepo = response.body();
                if (xkRepo.isSuccess()) {
                    mTagInfos.clear();
                    mTagInfos.addAll(xkRepo.getTagInfo());
                    mAdapter.notifyDataSetChanged();

                    TagInfo.setCacheSysTagInfoVersion(xkRepo.getVersion());
                    TagInfo.setCacheSysTagInfos(xkRepo.getTagInfo());
                }
            }

            @Override
            public void onFailure(Call<XKRepo> call, Throwable t) {
                ProgressHUD.showErrorMessage(context, context.getString(R.string.common_network_error));
            }
        });
    }

    @OnClick(R.id.btn_sure)
    protected void sureClick() {
        String selectedTag = mAdapter.getSelected();
        labelDialogListener.selectedLabels(selectedTag);
        dismiss();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        dismiss();
        return true;
    }

}

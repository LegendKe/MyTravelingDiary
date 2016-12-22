package com.ruihai.xingka.ui.trackway;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ruihai.xingka.R;
import com.ruihai.xingka.api.ApiModule;
import com.ruihai.xingka.api.model.TagInfo;
import com.ruihai.xingka.api.model.XKRepo;
import com.ruihai.xingka.ui.BaseActivity;
import com.ruihai.xingka.ui.common.enter.EmojiTranslate;
import com.ruihai.xingka.ui.common.enter.EmojiconSpan;
import com.ruihai.xingka.utils.AppUtility;
import com.ruihai.xingka.utils.Security;
import com.ruihai.xingka.widget.ProgressHUD;
import com.ruihai.xingka.widget.TagCloudView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by gebixiaozhang on 16/5/16.
 */
public class ChooseRequirementActivity extends BaseActivity implements TagCloudView.OnTagClickListener {
    @BindView(R.id.tv_right)
    TextView mRight;
    @BindView(R.id.tv_title)
    TextView mTitle;
    @BindView(R.id.tag_cloud_view)
    TagCloudView tagCloudView;
    @BindView(R.id.et_content)
    EditText requirementEt;
    @BindView(R.id.tv_count)
    TextView mCountTextView;
    private static final int TEXT_MAX_COUNT = 300;

    private List<TagInfo> mTagInfos = new ArrayList<>();
    StringBuffer sb = new StringBuffer(256);

    private ArrayList<String> tagList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_requirement);
        ButterKnife.bind(this);
        initView();
        initListennner();
        progress();

    }

    private void getTagLabels() {
        String sType = Security.aesEncrypt("4"); // 用户标签
        int appVersion = AppUtility.getAppVersionCode();
        String sVersion = Security.aesEncrypt(String.valueOf(appVersion)); // 当前版本号
        ApiModule.apiService().getSysTagList(sType, sVersion).enqueue(new Callback<XKRepo>() {
            @Override
            public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                XKRepo xkRepo = response.body();
                if (xkRepo.isSuccess()) {
                    mTagInfos.clear();
                    mTagInfos.addAll(xkRepo.getTagInfo());
                    //TagInfo.setCacheSysTagInfoVersion(xkRepo.getVersion());
                    // TagInfo.setCacheSysTagInfos(xkRepo.getTagInfo());

                    tagList.clear();
                    for (int i = 0; i < mTagInfos.size(); i++) {
                        tagList.add(mTagInfos.get(i).getName());
                    }
                    tagCloudView.setTags(tagList);
                }
            }

            @Override
            public void onFailure(Call<XKRepo> call, Throwable t) {
                // ProgressHUD.showErrorMessage(ChooseRequirementActivity.this, getString(R.string.common_network_error));
            }
        });
    }

    private void initView() {
        mTitle.setText("旅伴要求");
        mRight.setVisibility(View.VISIBLE);
        requirementEt.setHint("相遇行咖,一起开启美好旅程...");
        String content = getIntent().getStringExtra("travel_requirement");
        if (!TextUtils.isEmpty(content)) {
            requirementEt.setText(content);
        }

    }

    private void progress() {
        //tagCloudView.setTags();

        getTagLabels();

    }


    private void initListennner() {
        tagCloudView.setOnTagClickListener(this);
        //对EditText加监听
        requirementEt.addTextChangedListener(new TextWatcher() {
            private CharSequence temp;
            private int selectionStart;
            private int selectionEnd;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                temp = s;
                mCountTextView.setText(s.length() + "/" + TEXT_MAX_COUNT);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() < 40) {
                    // 输入的时候，只有一个光标，那么这两个值是相等的
                    selectionStart = requirementEt.getSelectionStart();
                    selectionEnd = requirementEt.getSelectionEnd();
                    if (temp.length() > TEXT_MAX_COUNT) {
                        s.delete(selectionStart - 1, selectionEnd);
                        int tempSelection = selectionEnd;
                        requirementEt.setText(s);
                        requirementEt.setSelection(tempSelection);//设置光标在最后
                    }
                }
//                int number = num - s.length();
//                mTvCount.setText("还能输入" + number + "字");
//                // 输入的时候，只有一个光标，那么这两个值是相等的
//                selectionStart = mMyTalk.getSelectionStart();
//                selectionEnd = mMyTalk.getSelectionEnd();
//                if (temp.length() > num) {
//                    s.delete(selectionStart - 1, selectionEnd);
//                    int tempSelection = selectionEnd;
//                    mMyTalk.setText(s);
//                    mMyTalk.setSelection(tempSelection);//设置光标在最后
//                }
            }
        });

    }


    @Override
    public void onTagClick(int position) {
        if (position == 0) {
            sb.append(tagList.get(position));
        } else {
            sb.append(" " + tagList.get(position));
        }

        requirementEt.setText(sb.toString());
    }


    @OnClick(R.id.tv_back)
    void onBack() {
        finish();
    }

    @OnClick(R.id.tv_right)
    void onRight() {
        if (requirementEt.length() > TEXT_MAX_COUNT) {
            AppUtility.showToast("字数不能超过300字");
        } else {
            Intent intent = new Intent();
            intent.putExtra("requirement", requirementEt.getText().toString());
            setResult(RESULT_OK, intent);
            finish();
        }

    }

    /**
     * 刷新剩余输入字数，最大值300个字
     */
    private void setLeftCount() {
        mCountTextView.setText(String.format(getString(R.string.edittext_content_count),
                getInputCount(), TEXT_MAX_COUNT));
    }

    /**
     * 获取用户输入的分享内容字数
     *
     * @return
     */
    private long getInputCount() {
        return calculateLength(requirementEt.getText().toString());
    }

    /**
     * 计算图说内容的字数，一个汉字=两个英文字符，一个中文标点=两个英文标点
     * 注意：该函数的不是用于对单个字符进行计算，因为单个字符四舍五入后都是1
     *
     * @param c
     * @return
     */
    private long calculateLength(CharSequence c) {
        double len = 0;
        for (int i = 0; i < c.length(); i++) {
            int tmp = (int) c.charAt(i);
            if (tmp > 0 && tmp < 127) {
//                len += 0.5;
                len += 1;
            } else {
                len++;
            }
        }
        return Math.round(len);
    }
}

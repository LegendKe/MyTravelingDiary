package com.ruihai.xingka.ui.mine;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.orhanobut.hawk.Hawk;
import com.ruihai.xingka.Constant;
import com.ruihai.xingka.R;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.api.model.NotDistrubed;
import com.ruihai.xingka.event.OnSwitchButtonClickListener;
import com.ruihai.xingka.ui.BaseActivity;
import com.ruihai.xingka.ui.mine.adapter.NotificationSettingsAdapter;
import com.ruihai.xingka.ui.mine.adapter.SettingNotDisturbedAdapter;
import com.ruihai.xingka.widget.AppNoScrollerListView;
import com.ruihai.xingka.widget.SwitchButton;
import com.ruihai.xingka.widget.wheel.OptionsPopupWindow;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import de.greenrobot.event.EventBus;

/**
 * 免打扰设置
 * Created by lqfang on 16/7/20.
 */
public class SettingNotDisturbedActivity extends BaseActivity implements OnSwitchButtonClickListener {

    @BindView(R.id.root_layout)
    LinearLayout rootLayout;
    @BindView(R.id.tv_title)
    TextView mTitle;
    @BindView(R.id.tv_right)
    TextView mRight;
    @BindView(R.id.lv_first)
    AppNoScrollerListView mLvFirst;
    @BindView(R.id.list1)
    AppNoScrollerListView mList1;

    private NotificationSettingsAdapter notificationSettingsAdapter;
    String[] dataKeys = {"免打扰"};
    String[] dataValues = {"pFollower", "pComment", "pPraise", "pMention", "pSystem"};

    private SettingNotDisturbedAdapter settingNotDisturbedAdapter;
    String[] data_first = {"从", "到"};
    String time1;
    String time2;
    String[] time = {"22:00", "7:00"};
    ArrayList<String> list = new ArrayList<String>();
    private int mType = 1;


    private OptionsPopupWindow pwOptions;
    private ArrayList<String> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_no_disturbed);
        ButterKnife.bind(this);

        initViews();
    }

    private void initViews() {
        mTitle.setText("免打扰设置");
        mRight.setVisibility(View.INVISIBLE);

        //选项选择器
        pwOptions = new OptionsPopupWindow(this, 1);
        options1Items = new ArrayList<>(Arrays.asList(Constant.startTime));

        ArrayList<String> endTime = new ArrayList<>();
        for (int i = 0; i < options1Items.size(); i++) {
            endTime = new ArrayList<>(Arrays.asList(Constant.endTime[i]));
            options2Items.add(endTime);
        }
        // 2级联动效果
        pwOptions.setPicker(options1Items, options2Items, true);

        //监听确定选择按钮
        pwOptions.setOnoptionsSelectListener(new OptionsPopupWindow.OnOptionsSelectListener() {

            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                // 返回的分别是三个级别的选中位置
                time1 = options1Items.get(options1);
                time2 = options2Items.get(options1).get(option2);

                if (list.size() > 0) {
                    list.clear();
                }
                list.add(time1);
                list.add(time2);

                time = list.toArray(new String[list.size()]);
//                Log.e("TAG","time时间-->"+list.toString());
//                EventBus.getDefault().post(time);
                AccountInfo.getInstance().saveNotDisturbedTime(time);

                settingNotDisturbedAdapter = new SettingNotDisturbedAdapter(SettingNotDisturbedActivity.this, data_first, time);
                mList1.setAdapter(settingNotDisturbedAdapter);

            }
        });
        pwOptions.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1f);
            }
        });

        notificationSettingsAdapter = new NotificationSettingsAdapter(this, dataKeys, dataValues, 3);
        notificationSettingsAdapter.setOnSwitchButtonClickListener(this);
        mLvFirst.setAdapter(notificationSettingsAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
//        settingNotDisturbedAdapter.notifyDataSetChanged();
    }

    @OnItemClick(R.id.lv_first)
    void onFirstItemClick(int position) {
//        boolean isEnable = Hawk.get(dataValues[position], false);
//        if (isEnable) {
//            Hawk.put(dataValues[position], true);
//            AccountInfo.getInstance().saveState(true);
//
//        } else {
//            Hawk.put(dataValues[position], false);
//            AccountInfo.getInstance().saveState(false);
//        }
//        notificationSettingsAdapter.notifyDataSetChanged();
    }

    @OnItemClick(R.id.list1)
    void onSecondItemClick(int position) {
        AccountInfo.getInstance().saveNotDisturbedTime(time);
        settingNotDisturbedAdapter.SelectedTime(time);
    }


    @OnClick(R.id.tv_back)
    void onBack() {

        if (mType == 2) {
            AccountInfo.getInstance().saveNotDisturbedTime(time);
            AccountInfo.getInstance().saveState(true);
            Intent intent = new Intent();
            intent.putExtra("type", mType);
            intent.putExtra("time", time);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            AccountInfo.getInstance().saveState(false);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (pwOptions != null && pwOptions.isShowing()) {
            pwOptions.dismiss();
            return;
        }
        super.onBackPressed();
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; // 0.0-1.0
        getWindow().setAttributes(lp);
    }


    @Override
    public void onClick(SwitchButton switchButton, final int position) {
        boolean state = false;
        if (AccountInfo.getInstance().getState() != null) {
            state = AccountInfo.getInstance().getState();
        }
        if (state == true) {
            mType = 2;
            mList1.setVisibility(View.VISIBLE);
            showDialog();
            Hawk.put(dataValues[position], true);
        }
//        Log.e("TAG","state-->"+state);


        switchButton.setOnToggleChanged(new SwitchButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                if (on) {
                    mType = 2;
                    mList1.setVisibility(View.VISIBLE);
                    showDialog();
                    AccountInfo.getInstance().saveState(true);
                } else {
                    mType = 1;
                    mList1.setVisibility(View.GONE);
                    AccountInfo.getInstance().saveState(false);
                }

            }
        });

        NotDistrubed notDistrubed = new NotDistrubed(mType, time);
        EventBus.getDefault().post(notDistrubed);


    }


    private void showDialog() {
        // 设置默认选中值
        String time = "";
        int startTimePosition = 21;
        int endTimePosition = 6;

        if (!TextUtils.isEmpty(time)) {
            // 截取时间
            String[] pcStr = time.split(",");
            if (pcStr.length > 1) {
                // 遍历时间数组获取数组下标
                for (int i = 0; i < options1Items.size(); i++) {
                    if (options1Items.get(i).contains(pcStr[0])) {
                        startTimePosition = i;
                        for (int j = 0; j < options2Items.get(i).size(); j++) {
                            if (options2Items.get(i).get(j).contains(pcStr[1])) {
                                endTimePosition = j;
                            }
                        }
                    }
                }
            }
        }
        pwOptions.setSelectOptions(startTimePosition, endTimePosition);
        backgroundAlpha(0.8f); // 设置背景颜色变暗
        pwOptions.showAtLocation(rootLayout, Gravity.BOTTOM, 0, 0);

    }
}

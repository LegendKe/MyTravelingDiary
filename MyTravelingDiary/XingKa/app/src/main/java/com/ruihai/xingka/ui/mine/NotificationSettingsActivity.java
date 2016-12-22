package com.ruihai.xingka.ui.mine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.orhanobut.hawk.Hawk;
import com.ruihai.iconicfontengine.widget.IconicFontTextView;
import com.ruihai.xingka.R;
import com.ruihai.xingka.api.model.NotDistrubed;
import com.ruihai.xingka.ui.BaseActivity;
import com.ruihai.xingka.ui.mine.adapter.MyFirstAdapter;
import com.ruihai.xingka.ui.mine.adapter.NotificationSettingsAdapter;
import com.ruihai.xingka.widget.AppNoScrollerListView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import de.greenrobot.event.EventBus;

/**
 * 消息通知设置
 * Created by zecker on 15/8/18.
 */
public class NotificationSettingsActivity extends BaseActivity {

    @BindView(R.id.lv_first)
    ListView mLvFirst;
    @BindView(R.id.tv_title)
    TextView mTitleView;
    @BindView(R.id.tv_right)
    IconicFontTextView mRightView;
    @BindView(R.id.list1)
    AppNoScrollerListView mList1;
    @BindView(R.id.list2)
    AppNoScrollerListView mList2;

    private MyFirstAdapter mAdapter1;
    private NotificationSettingsAdapter mFirstAdapter, mAdapter2;

    String[] dataKeys = {"被关注的推送", "被评论的推送", "被赞的推送", "被@的推送", "系统消息推送"};
    String[] dataValues = {"pFollower", "pComment", "pPraise", "pMention", "pSystem"};

    String[] types = {"响铃", "震动"};
    String[] typeValues = {"pNotifySound", "pNotifyVibrate"};

    String[] notDisturbed = {"免打扰"};
    String[] mTime = {"22:00", "7:00"};
//    String[] mTime;

    private int type = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_notification_settings);
        ButterKnife.bind(this);
        initViews();
    }

    private void initViews() {
        mTitleView.setText(R.string.mine_notification_settings_title);
        mRightView.setVisibility(View.INVISIBLE);

        mFirstAdapter = new NotificationSettingsAdapter(this, dataKeys, dataValues, 1);
        mLvFirst.setAdapter(mFirstAdapter);

        mAdapter1 = new MyFirstAdapter(this, notDisturbed, 1, type);
        mList1.setAdapter(mAdapter1);

        mAdapter2 = new NotificationSettingsAdapter(this, types, typeValues, 2);
        mList2.setAdapter(mAdapter2);
    }

    public void onEvent(NotDistrubed notDistrubed) {
        if (notDistrubed.type == 2) {
            mTime = notDistrubed.time;

            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < mTime.length; i++) {
                if (i == mTime.length - 1) {
                    sb.append(mTime[i]);
                } else {
                    sb.append(mTime[i] + "-");
                }
            }
            String Time1 = sb.toString();
            Log.e("TAG", "mTime-->" + Time1);

            mAdapter1.notifyDataSetChanged();
        }
    }


    @OnItemClick(R.id.lv_first)
    void onItemClick(int position) {
        boolean isEnable = Hawk.get(dataValues[position], true);
        if (isEnable) {
            Hawk.put(dataValues[position], false);
        } else {
            Hawk.put(dataValues[position], true);
        }
        mFirstAdapter.notifyDataSetChanged();
    }

    @OnItemClick(R.id.list1)
    void onList1Click(int position) {
        // 消息推送
        Intent intent = new Intent();
        intent.setClass(this, SettingNotDisturbedActivity.class);
        startActivityForResult(intent, 100);

//        mAdapter1.notifyDataSetChanged();

    }

    @OnItemClick(R.id.list2)
    void onList2Click(int position) {
        boolean isEnable = Hawk.get(typeValues[position], true);
        if (isEnable) {
            Hawk.put(typeValues[position], false);
        } else {
            Hawk.put(typeValues[position], true);
        }
        mAdapter2.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            Bundle bundle = data.getExtras();
            type = bundle.getInt("type");
            mTime = bundle.getStringArray("time");
//            Log.e("TAG","typeKK->"+type);

            mAdapter1.SelectedTime(mTime);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.tv_back)
    void onBack(View view) {
        finish();
    }


    @Override
    protected void onResume() {
        super.onResume();
//        mAdapter1.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}

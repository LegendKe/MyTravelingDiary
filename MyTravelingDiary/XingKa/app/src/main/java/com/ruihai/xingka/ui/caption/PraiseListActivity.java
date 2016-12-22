package com.ruihai.xingka.ui.caption;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.ruihai.iconicfontengine.widget.IconicFontTextView;
import com.ruihai.xingka.R;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.api.model.PraiseItem;
import com.ruihai.xingka.event.OnItemClickListener;
import com.ruihai.xingka.ui.BaseActivity;
import com.ruihai.xingka.ui.MainActivity;
import com.ruihai.xingka.ui.caption.adapter.PraiseItemAdapter;
import com.ruihai.xingka.ui.caption.datasource.PraiseListDataSource;
import com.ruihai.xingka.ui.mine.UserProfileActivity;
import com.ruihai.xingka.ui.trackway.datasource.TrackwayPraiseListDataSource;
import com.shizhefei.mvc.MVCHelper;
import com.shizhefei.mvc.MVCUltraHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;

public class PraiseListActivity extends BaseActivity implements OnItemClickListener {
    public final static String KEY_PHOTOTOPIC_GUID = "KEY_PGUID";
    public final static String KEY_AUTHOR_ACCOUNT = "KEY_AUTHOR";
    @BindView(R.id.tv_title)
    TextView titleView;
    @BindView(R.id.tv_right)
    IconicFontTextView rightView;
    @BindView(R.id.rl_recyclerview_refresh)
    PtrClassicFrameLayout mRefreshLayout;
    @BindView(R.id.rv_list)
    ListView mListView;

    private PraiseItemAdapter mAdapter;
    private MVCHelper<List<PraiseItem>> listViewHelper;
    private String mCurrentAccount;
    private String mUserAccount;
    private String mGuid;
    private int mType;

    public static void launch(Activity from, String Guid, String author, int type) {
        Intent intent = new Intent(from, PraiseListActivity.class);
        intent.putExtra(KEY_PHOTOTOPIC_GUID, Guid);
        intent.putExtra(KEY_AUTHOR_ACCOUNT, author);
        intent.putExtra("type", type);
        from.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_praise_list);
        ButterKnife.bind(this);
        mCurrentAccount = String.valueOf(AccountInfo.getInstance().loadAccount().getAccount());
        mUserAccount = getIntent().getStringExtra(KEY_AUTHOR_ACCOUNT);
        mGuid = getIntent().getStringExtra(KEY_PHOTOTOPIC_GUID);
        mType = getIntent().getIntExtra("type", 0);
        initView();
    }

    private void initView() {
        titleView.setText("点赞列表");
        rightView.setVisibility(View.INVISIBLE);

        listViewHelper = new MVCUltraHelper<>(mRefreshLayout);
        // 设置数据源
        if (mType == 1) {
//            listViewHelper.setLoadViewFractory(new ProfileLoadViewFactory("想为你点赞,却发现没有图说"));
            listViewHelper.setDataSource(new PraiseListDataSource(mCurrentAccount, mUserAccount, mGuid));
        } else if (mType == 2) {
//            listViewHelper.setLoadViewFractory(new ProfileLoadViewFactory("想为你点赞,却发现没有旅拼"));
            listViewHelper.setDataSource(new TrackwayPraiseListDataSource(mCurrentAccount, mUserAccount, mGuid));
        }

        // 设置适配器
        mAdapter = new PraiseItemAdapter(this);
        mAdapter.setOnItemClickListener(this);
        listViewHelper.setAdapter(mAdapter);
        // 加载数据
        listViewHelper.refresh();
    }


    @OnClick(R.id.tv_back)
    void onBack() {
        finish();
    }

    @Override
    public void onItemClick(View view, int position) {
        String account = String.valueOf(mAdapter.getData().get(position).getAccount());
//        UserProfileActivity.launch(this, account);
        if (mCurrentAccount.equals(account)) {
            if (MainActivity.currentTabIndex != 3) {
                MainActivity.launch(this, 1);
            }
        } else if (mAdapter.getData().get(position).isAdmin()) {
            UserProfileActivity.launch(this, account, 1, 1);
        } else {
            UserProfileActivity.launch(this, account, 1, 0);
        }
    }

    @Override
    public void onItemLongClick(View view, int position) {

    }

    @Override
    public void onItemChildClick(View childView, int position) {

    }

}

package com.ruihai.xingka.ui.mine;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ruihai.iconicfontengine.widget.IconicFontTextView;
import com.ruihai.xingka.R;
import com.ruihai.xingka.api.model.OfficialPhotoTopicTypeRepo;
import com.ruihai.xingka.api.model.UserPhotoTopic;
import com.ruihai.xingka.event.OnItemClickListener;
import com.ruihai.xingka.ui.BaseActivity;
import com.ruihai.xingka.ui.caption.CaptionDetailActivity;
import com.ruihai.xingka.ui.caption.ShareCaptionActivity;
import com.ruihai.xingka.ui.mine.adapter.OfficalCaptionListAdapter;
import com.ruihai.xingka.ui.mine.datasource.OfficalCaptionListDataSource;
import com.ruihai.xingka.ui.mine.impl.ProfileLoadViewFactory;
import com.ruihai.xingka.utils.AppUtility;
import com.ruihai.xingka.utils.QiniuHelper;
import com.shizhefei.mvc.MVCHelper;
import com.shizhefei.mvc.MVCUltraHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;

public class OfficalPhotoTopicListActivity extends BaseActivity implements OnItemClickListener, AdapterView.OnItemClickListener {

    public static void launch(Context from, String userAccount, OfficialPhotoTopicTypeRepo.OfficialPhotoTopicType data) {
        Intent intent = new Intent(from, OfficalPhotoTopicListActivity.class);
        intent.putExtra("userAccount", userAccount);
        intent.putExtra("officalPhotoTopicType", data);
        from.startActivity(intent);
    }

    @BindView(R.id.head_toolbar)
    RelativeLayout headToolBar;
    @BindView(R.id.tv_title)
    TextView titleView;
    @BindView(R.id.tv_right)
    IconicFontTextView rightView;
    @BindView(R.id.rl_recyclerview_refresh)
    PtrClassicFrameLayout mRefreshLayout;
    @BindView(R.id.rv_list)
    ListView listView;


    private OfficalCaptionListAdapter mAdapter;
    private int mType = 1;//类型
    private String mUserAccount;//被查看人行咖号
    private MVCHelper<List<UserPhotoTopic>> listViewHelper;
    private OfficialPhotoTopicTypeRepo.OfficialPhotoTopicType data;
    private int mPictureHeight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offical_photo_topic_list);
        ButterKnife.bind(this);
        mUserAccount = getIntent().getStringExtra("userAccount");
        data = (OfficialPhotoTopicTypeRepo.OfficialPhotoTopicType) getIntent().getSerializableExtra("officalPhotoTopicType");
        mType = data.getType();

        initHeadView();
        initView();
        initListenner();
    }

    private void initListenner() {
        listView.setOnItemClickListener(OfficalPhotoTopicListActivity.this);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int scrollY = getScrollY();
                int alpha = (int) (scrollY * 1.5);
                if (alpha > 255) {
                    alpha = 255;
                }

                if (scrollY > mPictureHeight) {
                    titleView.setVisibility(View.VISIBLE);

                } else {
                    titleView.setVisibility(View.GONE);

                }
                headToolBar.setBackgroundColor(Color.argb(alpha, 255, 255, 255));//
                // headToolBar.getBackground().setAlpha(alpha);
                Log.i("TAG", "-----滑动距离---->" + scrollY);
            }
        });
    }

    private void initHeadView() {
        View headView = getLayoutInflater().inflate(R.layout.header_offical_captipon_layout, null);
        ImageView coverImageView = (ImageView) headView.findViewById(R.id.iv_cover);
        TextView titleTv = (TextView) headView.findViewById(R.id.tv_title);
        TextView descripTv = (TextView) headView.findViewById(R.id.tv_descrip);
        ViewGroup.LayoutParams lp = coverImageView.getLayoutParams();
        int mPictureWidth = AppUtility.getScreenWidth();
        mPictureHeight = (int) (mPictureWidth * 13F / 36F);
        lp.height = mPictureHeight;
        lp.width = mPictureWidth;
        coverImageView.setLayoutParams(lp);
        String imageUrl = QiniuHelper.getTopicCoverWithKey(data.getCover());
        coverImageView.setImageURI(Uri.parse(imageUrl));
        titleTv.setText(data.getTitle());
        descripTv.setText(data.getDescrip());
        listView.addHeaderView(headView);
    }

    private void initView() {
        headToolBar.setBackgroundColor(Color.argb(0, 255, 255, 255));// 全透明
        // headToolBar.getBackground().setAlpha(0);
        titleView.setText(data.getTitle());
        titleView.setVisibility(View.GONE);
        //加好友图标
        rightView.setVisibility(View.VISIBLE);
        rightView.setText("{xk-report}");
        //rightView.setText("{xk-add-friend}");
        mAdapter = new OfficalCaptionListAdapter(mContext);
        listViewHelper.setLoadViewFractory(new ProfileLoadViewFactory("暂时还没有内容哦"));
        listViewHelper = new MVCUltraHelper<>(mRefreshLayout);

        // 设置数据源
        listViewHelper.setDataSource(new OfficalCaptionListDataSource(mUserAccount, mType));
        mAdapter.setOnItemClickListener(this);
        listViewHelper.setAdapter(mAdapter);
        // 加载数据
        listViewHelper.refresh();
    }


    @OnClick(R.id.tv_back)
    void onBack() {
        finish();
    }

    @OnClick(R.id.tv_right)
    void onShare() {
        BaseShareActivity.launch(this, data, mUserAccount);
    }

    @Override
    public void onItemClick(View view, int position) {

    }

    @Override
    public void onItemLongClick(View view, int position) {

    }

    @Override
    public void onItemChildClick(View childView, int position) {
        UserPhotoTopic item = mAdapter.getData().get(position);
        item.setAccount(mUserAccount);
        switch (childView.getId()) {
            case R.id.tv_delete:
                ShareCaptionActivity.launch(OfficalPhotoTopicListActivity.this, item, 1);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position != 0) {
            UserPhotoTopic photoTopic = mAdapter.getData().get(position - 1);
            String pGuid = photoTopic.getpGuid();
            CaptionDetailActivity.launch(this, pGuid, mUserAccount);
        }
    }

    public int getScrollY() {
        View c = listView.getChildAt(0);
        if (c == null) {
            return 0;
        }
        int firstVisiblePosition = listView.getFirstVisiblePosition();
        int top = c.getTop();
        return -top + firstVisiblePosition * c.getHeight();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}

package com.ruihai.xingka.ui.mine.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;
import com.ruihai.xingka.R;
import com.ruihai.xingka.api.ApiModule;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.api.model.MyTrackway;
import com.ruihai.xingka.api.model.User;
import com.ruihai.xingka.api.model.UserPhotoTopic;
import com.ruihai.xingka.api.model.XKRepo;
import com.ruihai.xingka.event.OnItemClickListener;
import com.ruihai.xingka.ui.caption.CaptionDetailActivity;
import com.ruihai.xingka.ui.mine.adapter.UserCaptionListAdapter;
import com.ruihai.xingka.ui.mine.datasource.MyCaptionDataSource;
import com.ruihai.xingka.ui.mine.impl.ProfileLoadViewFactory;
import com.ruihai.xingka.ui.trackway.TrackwayDetailActivity;
import com.ruihai.xingka.ui.trackway.adapter.MyTrackwayAdapter;
import com.ruihai.xingka.ui.trackway.datasource.MyTrackwayDataSource;
import com.ruihai.xingka.utils.Security;
import com.ruihai.xingka.widget.ProgressHUD;
import com.shizhefei.mvc.MVCHelper;
import com.shizhefei.mvc.MVCUltraHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 用户发布图说列表
 * <p/>
 * A simple {@link Fragment} subclass.
 */
public class UserCaptionFragment extends BaseScrollFragment implements OnItemClickListener {

    static final String TAG = "tag.UserCaptionFragment";
    private static final String ARG_USER_ACCOUNT = "user_account";

    //    @Bind(R.id.id_swiperefreshlayout)
//    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.rl_recyclerview_refresh)
    PtrClassicFrameLayout mRefreshLayout;
    @BindView(R.id.id_recyclerview)
    RecyclerView mRecyclerView;
    @BindView(R.id.floatButton)
    FloatingActionButton floatButton;

    private LinearLayoutManager mLinearLayoutManager;

    private MVCHelper<List<UserPhotoTopic>> listViewHelper;
    private MVCHelper<List<MyTrackway>> trackwaylistViewHelper;
    private UserCaptionListAdapter mAdapter;
    private MyTrackwayAdapter mTrackwayAdapter;

    private int mType;
    protected User mCurrentUser;
    protected String mUserAccount;

    public static UserCaptionFragment newInstance(String userAccount, int flag) {
        UserCaptionFragment fragment = new UserCaptionFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_USER_ACCOUNT, userAccount);
        args.putInt("flag", flag);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCurrentUser = AccountInfo.getInstance().loadAccount();
//        mUserAccount = savedInstanceState == null ? getArguments().getString(ARG_USER_ACCOUNT)
//                : savedInstanceState.getString(ARG_USER_ACCOUNT);
        mType = getArguments().getInt("flag");
        mUserAccount = String.valueOf(mCurrentUser.getAccount());
        mAdapter = new UserCaptionListAdapter(getActivity(), mUserAccount);
        mTrackwayAdapter = new MyTrackwayAdapter(getActivity(), mUserAccount);
        mAdapter.setOnItemClickListener(this);
        mTrackwayAdapter.setOnItemClickListener(this);

//        Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
//        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_caption, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String isHidden = String.valueOf("0");
        //测试
        mCurrentUser = AccountInfo.getInstance().loadAccount();
        if (mUserAccount.equals(String.valueOf(mCurrentUser.getAccount()))) {
            isHidden = String.valueOf("1");
        }
        switch (mType) {
            case 0: //无图说收藏
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                listViewHelper = new MVCUltraHelper<>(mRefreshLayout);
                // 设置LoadView的factory，用于创建使用者自定义的加载失败，加载中，加载更多等布局
                listViewHelper.setLoadViewFractory(new ProfileLoadViewFactory("喵，我的风景还在路上O(∩_∩)O~\n"));
                // 设置数据源
                listViewHelper.setDataSource(new MyCaptionDataSource(mUserAccount, isHidden));
                // 设置适配器
                listViewHelper.setAdapter(mAdapter);
                // 加载数据
                listViewHelper.refresh();
                break;
            case 1: //无旅拼收藏
                mRecyclerView.setLayoutManager(mLinearLayoutManager = new LinearLayoutManager(getActivity()));
                trackwaylistViewHelper.setLoadViewFractory(new ProfileLoadViewFactory("喵，我的风景还在路上O(∩_∩)O~\n"));
                trackwaylistViewHelper = new MVCUltraHelper<>(mRefreshLayout);
                // 设置数据源
                trackwaylistViewHelper.setDataSource(new MyTrackwayDataSource(mUserAccount, isHidden));
                // 设置适配器
                trackwaylistViewHelper.setAdapter(mTrackwayAdapter);
                // 加载数据
                trackwaylistViewHelper.refresh();
                break;
            default:
                break;
        }


        floatButton.setVisibility(View.GONE);
        //给recyleView添加滑动监听
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                int lastVisibleItem = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                int firstVisbleItem = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                if (firstVisbleItem == 0 && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    floatButton.setVisibility(View.GONE);
                } else {
                    floatButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        floatButton.attachToRecyclerView(mRecyclerView);//将置顶按钮和RecyclerView关联
        floatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecyclerView.scrollToPosition(0);//点击快速回到顶部
            }
        });

    }

    @Override
    public void onItemClick(View view, int position) {
        if (mType == 0) { //进入图说详情页
            UserPhotoTopic photoTopic = mAdapter.getData().get(position);
            String pGuid = photoTopic.getpGuid();
            CaptionDetailActivity.launch(getActivity(), pGuid, mUserAccount);
        } else if (mType == 1) { //进入旅拼详情页
            MyTrackway myTrackway = mTrackwayAdapter.getData().get(position);
            String tGuid = myTrackway.gettGuid();
            TrackwayDetailActivity.launch(getActivity(), tGuid, mUserAccount);
        }

    }

    @Override
    public void onItemLongClick(View view, int position) {

    }

    @Override
    public void onItemChildClick(View childView, int position) {
        if (mType == 0) {
            UserPhotoTopic photoTopic = mAdapter.getData().get(position);
            showDelDialog(photoTopic, null, position);
        } else if (mType == 1) {
            MyTrackway myTrackway = mTrackwayAdapter.getData().get(position);
            showDelDialog(null, myTrackway, position);
        }

    }

    private void showDelDialog(final UserPhotoTopic photoTopic, final MyTrackway myTrackway, final int position) {
        // 1. 布局文件转换为View对象
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.dialog_communal, null);
        final Dialog dialog = new AlertDialog.Builder(getActivity()).create();

        dialog.setCancelable(false);
        dialog.show();
        dialog.getWindow().setContentView(layout);

        // 3. 消息内容
        TextView dialog_msg = (TextView) layout.findViewById(R.id.update_content);
        if (mType == 0) {
            dialog_msg.setText("确定删除该图说吗?");
        } else if (mType == 1) {
            dialog_msg.setText("确定删除该旅拼吗?");
        }

        // 4. 确定按钮
        Button btnOK = (Button) layout.findViewById(R.id.umeng_update_id_ok);
        btnOK.setText("确定");
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mType == 0) {
                    deletePhotoTopic(photoTopic, null, position);
                } else if (mType == 1) {
                    deletePhotoTopic(null, myTrackway, position);
                }
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

    /**
     * 执行删除图说操作
     *
     * @param photoTopic
     */
    private void deletePhotoTopic(UserPhotoTopic photoTopic, MyTrackway myTrackway, final int position) {
        ProgressHUD.showLoadingMessage(getActivity(), "正在删除...", false);

        if (mType == 0) { //图说
            String securityAccount = Security.aesEncrypt(String.valueOf(mUserAccount));
            String securityPGuid = Security.aesEncrypt(photoTopic.getpGuid());

            ApiModule.apiService().deletePhotoTopic(securityAccount, securityPGuid, securityAccount).enqueue(new Callback<XKRepo>() {
                @Override
                public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                    XKRepo xkRepo = response.body();
                    ProgressHUD.dismiss();
                    String message = xkRepo.getMsg();
                    if (xkRepo.isSuccess()) {
                        //updateCountEvent();
                        mAdapter.getData().remove(position);
                        mAdapter.notifyDataSetChanged();
                        ProgressHUD.showSuccessMessage(getActivity(), "删除成功");
                    } else {
                        ProgressHUD.showInfoMessage(getActivity(), message);
                    }
                }

                @Override
                public void onFailure(Call<XKRepo> call, Throwable t) {
                    ProgressHUD.showErrorMessage(getActivity(), getString(R.string.common_network_error));
                }
            });
        } else if (mType == 1) { //旅拼
            String securityAccount = Security.aesEncrypt(String.valueOf(mUserAccount));
            String securityTGuid = Security.aesEncrypt(myTrackway.gettGuid());

            ApiModule.apiService_1().deleteTravelTogether(securityAccount, securityTGuid, securityAccount).enqueue(new Callback<XKRepo>() {
                @Override
                public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                    XKRepo xkRepo = response.body();
                    ProgressHUD.dismiss();
                    String message = xkRepo.getMsg();
                    if (xkRepo.isSuccess()) {
                        //updateCountEvent();
                        mTrackwayAdapter.getData().remove(position);
                        mTrackwayAdapter.notifyDataSetChanged();
                        ProgressHUD.showSuccessMessage(getActivity(), "删除成功");
                    } else {
                        ProgressHUD.showInfoMessage(getActivity(), message);
                    }
                }

                @Override
                public void onFailure(Call<XKRepo> call, Throwable t) {
                    ProgressHUD.showErrorMessage(getActivity(), getString(R.string.common_network_error));
                }
            });
        }
    }

    @Override
    public CharSequence getTitle(Resources r) {
        return "图说";
    }

    @Override
    public String getSelfTag() {
        return TAG;
    }

    @Override
    public boolean canScrollVertically(int direction) {
        return mRecyclerView != null && mRecyclerView.canScrollVertically(direction);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && listViewHelper != null) {
            listViewHelper.refresh();
        } else if (isVisibleToUser && trackwaylistViewHelper != null) {
            trackwaylistViewHelper.refresh();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mType == 0) {
            listViewHelper.destory();
        } else if (mType == 1) {
            trackwaylistViewHelper.destory();
        }
    }

}



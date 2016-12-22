package com.ruihai.xingka.ui.trackway.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ruihai.xingka.R;
import com.ruihai.xingka.api.ApiModule;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.api.model.TrackwayCollection;
import com.ruihai.xingka.api.model.UserCollection;
import com.ruihai.xingka.api.model.XKRepo;
import com.ruihai.xingka.event.UpdateCountEvent;
import com.ruihai.xingka.ui.caption.CaptionDetailActivity;
import com.ruihai.xingka.ui.mine.adapter.UserCollectionGridAdapter;
import com.ruihai.xingka.ui.mine.datasource.MyCollectionDataSource;
import com.ruihai.xingka.ui.mine.impl.ProfileLoadViewFactory;
import com.ruihai.xingka.ui.trackway.TrackwayDetailActivity;
import com.ruihai.xingka.ui.trackway.adapter.TrackwayCollectionGridAdapter;
import com.ruihai.xingka.ui.trackway.datasource.TrackwayCollectDataSource;
import com.ruihai.xingka.utils.Security;
import com.ruihai.xingka.widget.GridViewHandler;
import com.ruihai.xingka.widget.GridViewWithHeaderAndFooter;
import com.ruihai.xingka.widget.ProgressHUD;
import com.shizhefei.mvc.MVCHelper;
import com.shizhefei.mvc.MVCUltraHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lqfang on 16/5/19.
 */
public class MyCollectFragment extends Fragment {

    //    @Bind(R.id.id_swiperefreshlayout)
//    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.rl_recyclerview_refresh)
    PtrClassicFrameLayout mRefreshLayout;
    @BindView(R.id.girdView)
    GridViewWithHeaderAndFooter mGridView;

    private int mType;//类型
    private String mUserAccount;
    private MVCHelper<List<UserCollection>> listViewHelper;
    private MVCHelper<List<TrackwayCollection>> trackwaylistViewHelper;
    private UserCollectionGridAdapter mAdapter;
    private TrackwayCollectionGridAdapter mTrackwayAdapter;


    public static MyCollectFragment newInstance(int type) {
        MyCollectFragment fragment = new MyCollectFragment();
        Bundle bundle = new Bundle();
//        bundle.putString("userAccount", userAccount);
        bundle.putInt("type", type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mType = getArguments().getInt("type");
//        mUserAccount = getArguments().getString("userAccount");
        mUserAccount = String.valueOf(AccountInfo.getInstance().loadAccount().getAccount());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mycollection, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        switch (mType) {
            case 0: //图说收藏
                mAdapter = new UserCollectionGridAdapter(getActivity(), mUserAccount);
                listViewHelper.setLoadViewFractory(new ProfileLoadViewFactory("世界那么大，风景那么美，我想先看一看再说\\(^o^)/"));
                listViewHelper = new MVCUltraHelper<>(mRefreshLayout);
                // 设置数据源
                listViewHelper.setDataSource(new MyCollectionDataSource(mUserAccount));
                // 设置适配器
                listViewHelper.setAdapter(mAdapter, new GridViewHandler());
                // 加载数据
                listViewHelper.refresh();
                break;
            case 1: //旅拼收藏
                mTrackwayAdapter = new TrackwayCollectionGridAdapter(getActivity(), mUserAccount);
                trackwaylistViewHelper.setLoadViewFractory(new ProfileLoadViewFactory("世界那么大，风景那么美，我想先看一看再说\\(^o^)/"));
                trackwaylistViewHelper = new MVCUltraHelper<>(mRefreshLayout);
                // 设置数据源
                trackwaylistViewHelper.setDataSource(new TrackwayCollectDataSource(mUserAccount));
                // 设置适配器
                trackwaylistViewHelper.setAdapter(mTrackwayAdapter, new GridViewHandler());
                // 加载数据
                trackwaylistViewHelper.refresh();
                break;
            default:
                break;
        }


        //点击进入详情页
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (mType == 0) {
                    final UserCollection photoTopic = mAdapter.getData().get(position);
                    String pGuid = photoTopic.getpGuid();
                    String author = String.valueOf(photoTopic.getAuthorAccount());
                    if (photoTopic.getImgNum() == 0) {
                        ProgressHUD.showInfoMessage(getActivity(), "此图说已被主人删除", new ProgressHUD.SimpleHUDCallback() {
                            @Override
                            public void onSimpleHUDDismissed() {
                                cancelCollect(photoTopic, null, position, false);
                            }
                        });

                    } else {
                        CaptionDetailActivity.launch(getActivity(), pGuid, author);
                    }
                } else if (mType == 1) {
                    final TrackwayCollection trackwayCollection = mTrackwayAdapter.getData().get(position);
                    String tGuid = trackwayCollection.gettGuid();
                    String author = String.valueOf(trackwayCollection.getAuthorAccount());
                    if (trackwayCollection.getImgNum() == 0) {
                        ProgressHUD.showInfoMessage(getActivity(), "此旅拼已被主人删除", new ProgressHUD.SimpleHUDCallback() {
                            @Override
                            public void onSimpleHUDDismissed() {
                                cancelCollect(null, trackwayCollection, position, false);
                            }
                        });

                    } else {
                        TrackwayDetailActivity.launch(getActivity(), tGuid, author);
                    }
                }

            }
        });

        //长按取消收藏
        mGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (mType == 0) {
                    UserCollection photoTopic = mAdapter.getData().get(position);
                    showCancelDialog(photoTopic, null, position);
                } else if (mType == 1) {
                    TrackwayCollection trackwayCollection = mTrackwayAdapter.getData().get(position);
                    showCancelDialog(null, trackwayCollection, position);
                }
                return true;
            }
        });


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
        // 释放资源
        if (mType == 0) {
            listViewHelper.destory();
        } else if (mType == 1) {
            trackwaylistViewHelper.destory();
        }

    }


    private void showCancelDialog(final UserCollection userCollection, final TrackwayCollection trackwayCollection, final int position) {
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
            dialog_msg.setText("确定取消收藏该图说吗?");
        } else if (mType == 1) {
            dialog_msg.setText("确定取消收藏该旅拼吗?");
        }

        // 4. 确定按钮
        Button btnOK = (Button) layout.findViewById(R.id.umeng_update_id_ok);
        btnOK.setText("确定");
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mType == 0) {
                    cancelCollect(userCollection, null, position, true);
                } else if (mType == 1) {
                    cancelCollect(null, trackwayCollection, position, true);
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
     * 执行取消收藏图说操作
     *
     * @param photoTopic
     * @param position
     */
    private void cancelCollect(UserCollection photoTopic, TrackwayCollection trackwayCollection, final int position, final Boolean showDialog) {
        // 整理取消图说收藏参数并加密
        if (mType == 0) {
            String account = Security.aesEncrypt(String.valueOf(mUserAccount));
            String isCollect = Security.aesEncrypt("0");
            String pGuid = Security.aesEncrypt(photoTopic.getpGuid());
            String toAccount = Security.aesEncrypt(String.valueOf(photoTopic.getAuthorAccount()));

            if (showDialog) {
                ProgressHUD.showLoadingMessage(getActivity(), "正在取消收藏...", false);
            }

            ApiModule.apiService().photoTopicCollectionAdd(account, pGuid, isCollect, toAccount).enqueue(new Callback<XKRepo>() {
                @Override
                public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                    XKRepo xkRepo = response.body();
                    updateCountEvent();

                    ProgressHUD.dismiss();
                    if (xkRepo.isSuccess()) {
                        mAdapter.getData().remove(position);
                        mAdapter.notifyDataSetChanged();
                        if (showDialog) {
                            ProgressHUD.showSuccessMessage(getActivity(), "取消收藏成功");
                        }
                    } else {
                        if (showDialog) {
                            ProgressHUD.showInfoMessage(getActivity(), xkRepo.getMsg());
                        }
                    }
                }

                @Override
                public void onFailure(Call<XKRepo> call, Throwable t) {
                    ProgressHUD.showErrorMessage(getActivity(), getString(R.string.common_network_error));
                }
            });
        } else if (mType == 1) {
            String account = Security.aesEncrypt(String.valueOf(mUserAccount));
            String isCollect = Security.aesEncrypt("0");
            String tGuid = Security.aesEncrypt(trackwayCollection.gettGuid());
            String toAccount = Security.aesEncrypt(String.valueOf(trackwayCollection.getAuthorAccount()));

            if (showDialog) {
                ProgressHUD.showLoadingMessage(getActivity(), "正在取消收藏...", false);
            }

            ApiModule.apiService_1().travelTogetherCollectionAdd(account, tGuid, isCollect, toAccount).enqueue(new Callback<XKRepo>() {
                @Override
                public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                    XKRepo xkRepo = response.body();
                    updateCountEvent();

                    ProgressHUD.dismiss();
                    if (xkRepo.isSuccess()) {
                        mTrackwayAdapter.getData().remove(position);
                        mTrackwayAdapter.notifyDataSetChanged();
                        if (showDialog) {
                            ProgressHUD.showSuccessMessage(getActivity(), "取消收藏成功");
                        }
                    } else {
                        if (showDialog) {
                            ProgressHUD.showInfoMessage(getActivity(), xkRepo.getMsg());
                        }
                    }
                }

                @Override
                public void onFailure(Call<XKRepo> call, Throwable t) {
                    ProgressHUD.showErrorMessage(getActivity(), getString(R.string.common_network_error));
                }
            });
        }

    }

    private void updateCountEvent() {
        EventBus.getDefault().post(new UpdateCountEvent());
    }

}

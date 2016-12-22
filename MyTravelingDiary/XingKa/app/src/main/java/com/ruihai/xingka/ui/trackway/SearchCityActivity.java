package com.ruihai.xingka.ui.trackway;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiItemDetail;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.ruihai.xingka.R;
import com.ruihai.xingka.ui.BaseActivity;
import com.ruihai.xingka.ui.caption.PoiNameAdapter;
import com.ruihai.xingka.utils.AppUtility;
import com.ruihai.xingka.widget.ClearableEditText;
import com.ruihai.xingka.widget.PullListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by gebixiaozhang on 16/5/16.
 */
public class SearchCityActivity extends BaseActivity implements PoiSearch.OnPoiSearchListener {
    @BindView(R.id.tv_right)
    TextView mRight;
    @BindView(R.id.tv_title)
    TextView mTitle;
    @BindView(R.id.et_search_key)
    ClearableEditText mSearch;
    @BindView(R.id.list)
    PullListView show_msg;

    private int mPage = 0;

    private double x, y;
    private String cityCode, searchKey;
    private String city = "";
    // private AMapLocation mALocation;
    //   private LocationSource.OnLocationChangedListener mListener;
    // private LocationManagerProxy mAMapLocationManager;
    private PoiNameAdapter poiNameAdapter;
    private PoiSearch.Query endSearchQuery;
    private List<PoiItem> poiItems = new ArrayList<PoiItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_city);
        ButterKnife.bind(this);
        initView();
        initListener();
    }

    private void initView() {
        mTitle.setText("搜索地点");
        mSearch.setHint("搜索关键字");
        mRight.setVisibility(View.INVISIBLE);
        poiNameAdapter = new PoiNameAdapter(SearchCityActivity.this, poiItems);
        // poiNameAdapter.notifyDataSetChanged();

        show_msg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                PoiItem item = poiItems.get(position - 1);
                String address = "";
                Intent intent = new Intent();
                if (position == 1) {
                    intent.putExtra("address", mSearch.getText().toString());
                    Log.i("TAG", "------>" + position + mSearch.getText().toString());
                } else {
                    intent.putExtra("address", item.getTitle());
                    Log.i("TAG", "------>" + position + item.getTitle());
                }
//                Log.e("位置", address);
//                intent.putExtra("latitude", x);
//                intent.putExtra("Longitude", y);
                // intent.putExtra("city", city);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        show_msg.setCanRefresh(false);
        //下拉刷新
//        show_msg.setOnRefreshListener(new PullListView.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                mPage = 0;
//                searchKey = mSearch.getText().toString().trim();
//                searchPoi(searchKey);
//            }
//        });
        //上拉加载更多
        show_msg.setOnGetMoreListener(new PullListView.OnGetMoreListener() {

            @Override
            public void onGetMore() {
                mPage++;
                //停止定位
                //deactivate();
                searchKey = mSearch.getText().toString().trim();
                searchPoi(searchKey);
            }
        });

        show_msg.setAdapter(poiNameAdapter);
    }

    private void initListener() {
        //对关键词进行搜索
//        mSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int keyCode, KeyEvent event) {
//                //修改回车键功能
//                if (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
//                    //激活定位
////                        activate(mListener);
//                    final String Key = v.getText().toString().trim();
//                    if (TextUtils.isEmpty(Key)) {
//                        // 先隐藏键盘
//                        hideSoftInput(v.getWindowToken());
//                        AppUtility.showToast("请输入搜索关键词");
//                        //删除输入框中关键词后回到原来位置
//                        if (TextUtils.isEmpty(searchKey)) {
//                           // searchPoi("");
//                            poiItems.clear();
//                            poiNameAdapter.notifyDataSetChanged();;
//
//                        }
//                        return true;
//                    } else {
//                        searchPoi(Key);
//                        // 先隐藏键盘
//                        hideSoftInput(v.getWindowToken());
//                    }
//                    return true;
//                }
//                return false;
//            }
//
//        });
        mSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // searchPoi(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString())) {
                    searchPoi(s.toString());
                } else {
                    poiItems.clear();
                    poiNameAdapter.notifyDataSetChanged();
                }

            }
        });

    }

    /**
     * 关键词搜索
     */
    public void searchPoi(String msg) {
        endSearchQuery = new PoiSearch.Query(msg, ""); // 第一个参数表示查询关键字，第二参数表示poi搜索类型，第三个参数表示城市区号或者城市名
        endSearchQuery.setPageNum(mPage);// 设置查询第几页，第一页从0开始
        endSearchQuery.setPageSize(30);// 设置每页返回多少条数据
        PoiSearch poiSearch = new PoiSearch(SearchCityActivity.this, endSearchQuery);
        //设置搜索范围
        // poiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(mALocation.getLatitude(), mALocation.getLongitude()), 50000));
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();// 异步poi查询
    }

    @Override
    public void onPoiSearched(PoiResult result, int rCode) {
        // ProgressHUD.dismiss();
        if (rCode == 0) {// 返回成功
            if (mPage == 0) {
                show_msg.refreshComplete();
            } else {
                show_msg.getMoreComplete();
            }
            if (result != null && result.getQuery() != null && result.getPois() != null && result.getPois().size() > 0) {// 搜索poi的结果
                // 取得搜索到的poiitems有多少条
                int resultPages = result.getPageCount();
                searchKey = mSearch.getText().toString().trim();
                PoiItem item = new PoiItem("", null, "自定义该地点", "是否直接添加到线路中?");
                //  PoiItem item1 = new PoiItem("", null, city, "");
                if (mPage == 0) {
                    poiItems.clear();
                    poiItems.add(0, item);
                    if (TextUtils.isEmpty(searchKey)) {
                        // poiItems.add(1, item1);
                    }
                }
                // poiItems = result.getPois();
                // 取得poiitem数据
                poiItems.addAll(result.getPois());
                poiNameAdapter.notifyDataSetChanged();

            } else {
                // ProgressHUD.dismiss();
               // AppUtility.showToast(getString(R.string.no_result));
            }
        } else if (rCode == 27) {
            AppUtility.showToast(getString(R.string.error_network));
        } else if (rCode == 32) {
            //  AppUtility.showToast(getString(R.string.error_key));
        } else {
            // AppUtility.showToast(getString(R.string.error_other) + rCode);
        }
    }

    @Override
    public void onPoiItemDetailSearched(PoiItemDetail poiItemDetail, int i) {

    }

    @OnClick(R.id.tv_back)
    void onBack() {
        finish();
    }

    @OnClick(R.id.tv_right)
    void onRight() {

    }

}

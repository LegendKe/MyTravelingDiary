package com.ruihai.xingka.ui.caption;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiItemDetail;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.ruihai.xingka.R;
import com.ruihai.xingka.ui.BaseActivity;
import com.ruihai.xingka.utils.AppUtility;
import com.ruihai.xingka.widget.ClearableEditText;
import com.ruihai.xingka.widget.ProgressHUD;
import com.ruihai.xingka.widget.PullListView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 地点选择页
 */
public class ChooseLocationActivity extends BaseActivity implements LocationSource, AMapLocationListener, PoiSearch.OnPoiSearchListener {

    @BindView(R.id.et_search_key)
    ClearableEditText mSearch;
    @BindView(R.id.list)
    PullListView show_msg;
    @BindView(R.id.tv_search1)
    TextView search;

    private int mPage = 0;

    private AMap aMap;
    private MapView mapView;
    private double x, y;
    private String cityCode, searchKey;
    private String city = "";
    private AMapLocation mALocation;
    private LocationSource.OnLocationChangedListener mListener;
    private LocationManagerProxy mAMapLocationManager;
    private PoiNameAdapter poiNameAdapter;
    private PoiSearch.Query endSearchQuery;
    private List<PoiItem> poiItems = new ArrayList<PoiItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_location);
        ButterKnife.bind(this);

        ProgressHUD.showLoadingMessage(mContext, "正在获取地理位置", true);

        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写,且在初始化AMap对象之前
        init();
        initListener();
        initView();
    }

    private void initListener() {
        if (mAMapLocationManager == null) {
            ProgressHUD.showInfoMessage(mContext, "请检查GPS是否开启");
        } else {
            //对关键词进行搜索
            mSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int keyCode, KeyEvent event) {
                    //修改回车键功能
                    if (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                        //激活定位
//                        activate(mListener);
                        final String Key = v.getText().toString().trim();
                        if (TextUtils.isEmpty(Key)) {
                            // 先隐藏键盘
                            hideSoftInput(v.getWindowToken());
                            AppUtility.showToast("请输入搜索关键词");
                            //删除输入框中关键词后回到原来位置
                            if (TextUtils.isEmpty(searchKey)) {
                                PoiCheckEnd(mALocation, "");
                            }
                            return true;
                        } else {
                            SearchPoi(Key);
                            // 先隐藏键盘
                            hideSoftInput(v.getWindowToken());
                        }
                        return true;
                    }
                    return false;
                }

            });
        }
    }

    private void initView() {
        mSearch.setHint("搜索附近的兴趣点");
        search.setVisibility(View.VISIBLE);
        poiNameAdapter = new PoiNameAdapter(ChooseLocationActivity.this, poiItems);
        // poiNameAdapter.notifyDataSetChanged();

        show_msg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                PoiItem item = poiItems.get(position - 1);
                String address = "";
                if (position != 1) {
                    //判断城市名和地点相同时只显示城市
                    if (String.valueOf(item).equals(city)) {
                        address = String.valueOf(item);
                    } else {
                        if (!TextUtils.isEmpty(city)) {
                            address = city + " · " + String.valueOf(item);
                        }
                    }
                }
                Intent intent = new Intent();
                intent.putExtra("address", address);
                Log.e("位置", address);
                intent.putExtra("latitude", x);
                intent.putExtra("Longitude", y);
                // intent.putExtra("city", city);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
        //下拉刷新
        show_msg.setOnRefreshListener(new PullListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPage = 0;
                searchKey = mSearch.getText().toString().trim();
                if (TextUtils.isEmpty(searchKey)) {
                    PoiCheckEnd(mALocation, "");
                } else {
                    SearchPoi(searchKey);
                }
            }
        });
        //上拉加载更多
        show_msg.setOnGetMoreListener(new PullListView.OnGetMoreListener() {

            @Override
            public void onGetMore() {
                mPage++;
                //停止定位
                deactivate();
                searchKey = mSearch.getText().toString().trim();
                if (TextUtils.isEmpty(searchKey)) {
                    PoiCheckEnd(mALocation, "");
                } else {
                    SearchPoi(searchKey);
                }
            }
        });
        show_msg.setAdapter(poiNameAdapter);
    }

    /**
     * 初始化AMap对象
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }
    }

    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation aLocation) {
        if (mListener != null && aLocation != null) {
            mALocation = aLocation;
            /**
             * 获取城市的编码
             */
            cityCode = aLocation.getCityCode();
            city = aLocation.getCity();
            mListener.onLocationChanged(aLocation);// 显示系统小蓝点
            //获取经纬度
            BigDecimal latitude = new BigDecimal(aLocation.getLatitude());
            x = latitude.setScale(8, BigDecimal.ROUND_DOWN).doubleValue();
            BigDecimal longitude = new BigDecimal(aLocation.getLongitude());
            y = longitude.setScale(8, BigDecimal.ROUND_DOWN).doubleValue();
            //搜索框内容为空时执行周边搜索方法,有内容时执行关键词搜索方法
            final String searchKey = mSearch.getText().toString().trim();
            if (TextUtils.isEmpty(searchKey)) {
                PoiCheckEnd(aLocation, "");
            } else {
                initListener();
            }
        }
    }

    /**
     * 关键词搜索
     */
    public void SearchPoi(String msg) {
        endSearchQuery = new PoiSearch.Query(msg, "", cityCode);
//        endSearchQuery = new PoiSearch.Query(msg, "道路附属设施|地名地址信息|风景名胜|交通设施服务|科教文化服务", cityCode); // 第一个参数表示查询关键字，第二参数表示poi搜索类型，第三个参数表示城市区号或者城市名
        endSearchQuery.setPageNum(mPage);// 设置查询第几页，第一页从0开始
        endSearchQuery.setPageSize(30);// 设置每页返回多少条数据
        PoiSearch poiSearch = new PoiSearch(ChooseLocationActivity.this, endSearchQuery);
        //设置搜索范围
        poiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(mALocation.getLatitude(), mALocation.getLongitude()), 50000));
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();// 异步poi查询
    }

    /**
     * 周边搜索
     */
    public void PoiCheckEnd(AMapLocation aLocation, String msg) {
        endSearchQuery = new PoiSearch.Query(msg, "道路附属设施|地名地址信息|风景名胜|交通设施服务|科教文化服务", cityCode); // 第一个参数表示查询关键字，第二参数表示poi搜索类型，第三个参数表示城市区号或者城市名
        endSearchQuery.setPageNum(mPage);// 设置查询第几页，第一页从0开始
        endSearchQuery.setPageSize(30);// 设置每页返回多少条数据
        PoiSearch poiSearch = new PoiSearch(ChooseLocationActivity.this, endSearchQuery);
        //设置搜索范围
        poiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(aLocation.getLatitude(), aLocation.getLongitude()), 5000));
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();// 异步poi查询
    }

    @Override
    public void onPoiSearched(PoiResult result, int rCode) {
        // TODO Auto-generated method stub
        ProgressHUD.dismiss();
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
                PoiItem item = new PoiItem("", null, "不显示位置", "");
                PoiItem item1 = new PoiItem("", null, city, "");
                if (mPage == 0) {
                    poiItems.clear();
                    poiItems.add(0, item);
                    if (TextUtils.isEmpty(searchKey)) {
                        poiItems.add(1, item1);
                    }
                }
                Log.e("TAG", "----城市1----->" + city + "---集合--->" + item1);
                // poiItems = result.getPois();
                // 取得poiitem数据
                poiItems.addAll(result.getPois());
                poiNameAdapter.notifyDataSetChanged();

            } else {
                ProgressHUD.dismiss();
                AppUtility.showToast(getString(R.string.no_result));
            }
        } else if (rCode == 27) {
            AppUtility.showToast(getString(R.string.error_network));
        } else if (rCode == 32) {
            AppUtility.showToast(getString(R.string.error_key));
        } else {
            AppUtility.showToast(getString(R.string.error_other) + rCode);
        }
    }

    @OnClick(R.id.tv_back)
    void onBack() {
        finish();
    }

    @OnClick(R.id.tv_search)
    void onSearch() {
        searchKey = mSearch.getText().toString().trim();

        if (TextUtils.isEmpty(searchKey)) {
            AppUtility.showToast("请输入搜索关键词");
            //删除输入框中关键词后回到原来位置
            if (TextUtils.isEmpty(searchKey)) {
                PoiCheckEnd(mALocation, "");
            }
        } else {
            SearchPoi(searchKey);
        }
    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        // 自定义系统定位小蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.yuan_image));// 设置小蓝点的图标
        myLocationStyle.strokeColor(Color.BLACK);// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));// 设置圆形的填充颜色
        // myLocationStyle.anchor(int,int)//设置小蓝点的锚点
        myLocationStyle.strokeWidth(0.0f);// 设置圆形的边框粗细
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        //aMap.setMyLocationType();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }


    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        deactivate();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mAMapLocationManager == null) {
            mAMapLocationManager = LocationManagerProxy.getInstance(this);
            mAMapLocationManager.setGpsEnable(true);
            /*
             * mAMapLocManager.setGpsEnable(false);
			 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true Location
			 * API定位采用GPS和网络混合定位方式
			 * ，第一个参数是定位provider，第二个参数时间最短是2000毫秒，第三个参数距离间隔单位是米，第四个参数是定位监听者
			 */
            mAMapLocationManager.requestLocationUpdates(LocationProviderProxy.AMapNetwork, 2000, 10, this);
        }

    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mAMapLocationManager != null) {
            mAMapLocationManager.removeUpdates(this);
            mAMapLocationManager.destory();
        }
        mAMapLocationManager = null;
    }

    @Override
    public void onPoiItemDetailSearched(PoiItemDetail arg0, int arg1) {
    }

    /**
     * 此方法已经废弃
     */
    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

}

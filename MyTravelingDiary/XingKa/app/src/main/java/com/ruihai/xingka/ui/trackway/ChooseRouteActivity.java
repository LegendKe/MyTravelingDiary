package com.ruihai.xingka.ui.trackway;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.LocationSource;
import com.ruihai.xingka.R;
import com.ruihai.xingka.ui.BaseActivity;
import com.ruihai.xingka.ui.trackway.adapter.RouteAdapter;
import com.ruihai.xingka.utils.AppUtility;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by gebixiaozhang on 16/5/16.
 */
public class ChooseRouteActivity extends BaseActivity implements AdapterView.OnItemClickListener, AMapLocationListener, LocationSource {
    public final static int REQUEST_SELECT_BEIGIN_CITY_CODE = 0X0000;
    public final static int REQUEST_SELECT_DETINATION_CODE = 0X0001;
    @BindView(R.id.tv_right)
    TextView mRight;
    @BindView(R.id.tv_title)
    TextView mTitle;
    @BindView(R.id.listview)
    ListView listView;
    private ArrayList<String> datas = new ArrayList<>();
    private RouteAdapter mRouteAdapter;
    private LocationManagerProxy mAMapLocationManager;
    private LocationSource.OnLocationChangedListener mListener;
    private int positionLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_route);
        ButterKnife.bind(this);
        initView();
        initListennner();
        progress();
    }

    private void progress() {
        activate(mListener);
        datas.add(0, "正在定位...");
        datas.add(1, "添加地点");
        mRouteAdapter = new RouteAdapter(this, datas);
        listView.setAdapter(mRouteAdapter);
        ArrayList<String> list = getIntent().getStringArrayListExtra("travel_route");
        if (list != null && list.size() > 0) {
            datas.clear();
            datas.addAll(list);
            mRouteAdapter.notifyDataSetChanged();
        }
    }

    private void initListennner() {
        listView.setOnItemClickListener(this);
    }

    private void initView() {
        mTitle.setText("线路规划");
        mRight.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.tv_back)
    void onBack() {
        finish();
    }

    @OnClick(R.id.tv_right)
    void onRight() {
        if (datas.size() == 2) {
            AppUtility.showToast("请选择前往城市");
        } else {
            Intent intent = new Intent();
            intent.putStringArrayListExtra("route", datas);
            setResult(RESULT_OK, intent);
            finish();
        }

    }


    //定位
    @Override
    public void onLocationChanged(AMapLocation aLocation) {
        if (aLocation != null) {
            // mALocation = aLocation;
            /**
             * 获取城市的编码
             */
            //cityCode = aLocation.getCityCode();
            String city = aLocation.getCity();
            if (!TextUtils.isEmpty(city)) {
                datas.set(0, city);
                mRouteAdapter.notifyDataSetChanged();
            }

//            StringBuffer sb = new StringBuffer(256);
//            sb.append(aLocation.getCity());
//            if (sb.toString() != null && sb.toString().length() > 0) {
//                String lngCityName = sb.toString();
//
//            }

        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
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

    @Override
    public void deactivate() {
        // mListener = null;
        if (mAMapLocationManager != null) {
            mAMapLocationManager.removeUpdates(this);
            mAMapLocationManager.destory();
        }
        mAMapLocationManager = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        deactivate();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        positionLabel = position;
        if (position == 0) {
            startActivityForResult(new Intent(ChooseRouteActivity.this, LocationActivity.class), REQUEST_SELECT_BEIGIN_CITY_CODE);
        } else {
            startActivityForResult(new Intent(ChooseRouteActivity.this, SearchCityActivity.class), REQUEST_SELECT_DETINATION_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_SELECT_BEIGIN_CITY_CODE) {
                String city = data.getStringExtra("lngCityName");
                datas.set(0, city);
            } else if (requestCode == REQUEST_SELECT_DETINATION_CODE) {
                String city = data.getStringExtra("address");
                Log.i("TAG", "----城市--->" + city);
                if (!TextUtils.isEmpty(city)) {
                    datas.set(positionLabel, city);
                    if (positionLabel == datas.size() - 1) {
                        datas.add("添加地点");
                    }

                }
            }
            mRouteAdapter.notifyDataSetChanged();
        }


    }
}

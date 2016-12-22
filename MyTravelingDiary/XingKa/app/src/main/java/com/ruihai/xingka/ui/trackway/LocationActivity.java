package com.ruihai.xingka.ui.trackway;


import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.ruihai.xingka.R;
import com.ruihai.xingka.api.model.City;
import com.ruihai.xingka.ui.BaseActivity;
import com.ruihai.xingka.utils.AppUtility;
import com.ruihai.xingka.widget.MyLetterListView;
import com.ruihai.xingka.widget.ProgressHUD;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * Created by gebixiaozhang on 16/5/16.
 */
public class LocationActivity extends BaseActivity implements AMapLocationListener, LocationSource {
    private ListAdapter adapter;
    private ListView personList;
    // private TextView overlay; // 对话框首字母textview
    private MyLetterListView letterListView; // A-Z listview
    private HashMap<String, Integer> alphaIndexer;// 存放存在的汉语拼音首字母和与之对应的列表位置
    private String[] sections;// 存放存在的汉语拼音首字母
    // private OverlayThread overlayThread; // 显示首字母对话框
    private ArrayList<City> allCity_lists; // 所有城市列表
    private ArrayList<City> ShowCity_lists; // 需要显示的城市列表-随搜索而改变
    private ArrayList<City> city_lists;// 城市列表
    private String lngCityName = "";//存放返回的城市名
    private JSONArray chineseCities;
    private LocationSource.OnLocationChangedListener mListener;
    private LocationManagerProxy mAMapLocationManager;
    private EditText sh;
    private TextView lng_city;
    private LinearLayout lng_city_lay;
    private static final int SHOWDIALOG = 2;
    private static final int DISMISSDIALOG = 3;
//    private AMap aMap;
//    private MapView mapView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectcity);
//        mapView = (MapView) findViewById(R.id.map);
//        mapView.onCreate(savedInstanceState);// 此方法必须重写,且在初始化AMap对象之前
        // init();
        personList = (ListView) findViewById(R.id.list_view);
        allCity_lists = new ArrayList<City>();
        letterListView = (MyLetterListView) findViewById(R.id.MyLetterListView01);
        lng_city_lay = (LinearLayout) findViewById(R.id.lng_city_lay);
        sh = (EditText) findViewById(R.id.sh);
        lng_city = (TextView) findViewById(R.id.lng_city);
        letterListView.setOnTouchingLetterChangedListener(new LetterListViewListener());
        alphaIndexer = new HashMap<String, Integer>();
        // overlayThread = new OverlayThread();
        personList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Intent intent = new Intent();
                intent.putExtra("lngCityName", ShowCity_lists.get(arg2).name);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        lng_city_lay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("lngCityName", lngCityName);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        activate(mListener);
        handler2.sendEmptyMessage(SHOWDIALOG);
        Thread thread = new Thread() {
            @Override
            public void run() {
                hotCityInit();
                handler2.sendEmptyMessage(DISMISSDIALOG);
                super.run();
            }
        };
        thread.start();
    }

    /**
     * 热门城市
     */
    public void hotCityInit() {
        City city;
        city = new City("上海", "");
        allCity_lists.add(city);
        city = new City("北京", "");
        allCity_lists.add(city);
        city = new City("广州", "");
        allCity_lists.add(city);
        city = new City("深圳", "");
        allCity_lists.add(city);
//        city = new City("武汉", "");
//        allCity_lists.add(city);
//        city = new City("天津", "");
//        allCity_lists.add(city);
//        city = new City("西安", "");
//        allCity_lists.add(city);
//        city = new City("南京", "");
//        allCity_lists.add(city);
//        city = new City("杭州", "");
//        allCity_lists.add(city);
//        city = new City("成都", "");
//        allCity_lists.add(city);
//        city = new City("重庆", "");
//        allCity_lists.add(city);
        city_lists = getCityList();
        allCity_lists.addAll(city_lists);
        ShowCity_lists = allCity_lists;
    }

    private ArrayList<City> getCityList() {
        ArrayList<City> list = new ArrayList<City>();
        try {
            chineseCities = new JSONArray(getResources().getString(R.string.citys));
            for (int i = 0; i < chineseCities.length(); i++) {
                JSONObject jsonObject = chineseCities.getJSONObject(i);
                City city = new City(jsonObject.getString("name"), jsonObject.getString("pinyin"));
                list.add(city);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        Collections.sort(list, comparator);
        return list;
    }

    /**
     * a-z排序
     */
    Comparator comparator = new Comparator<City>() {
        @Override
        public int compare(City lhs, City rhs) {
            String a = lhs.getPinyi().substring(0, 1);
            String b = rhs.getPinyi().substring(0, 1);
            int flag = a.compareTo(b);
            if (flag == 0) {
                return a.compareTo(b);
            } else {
                return flag;
            }

        }
    };

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        // mapView.onResume();
    }


    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        //  mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // mapView.onDestroy();
        deactivate();
    }

    /**
     * 定位成功后回调函数
     */

    @Override
    public void onLocationChanged(AMapLocation aLocation) {
        // AppUtility.showLongToast("定位成功");
        if (aLocation != null) {
            // mALocation = aLocation;
            /**
             * 获取城市的编码
             */
            //cityCode = aLocation.getCityCode();
            String city = aLocation.getCity();
            StringBuffer sb = new StringBuffer(256);
            sb.append(aLocation.getCity());
            if (sb.toString() != null && sb.toString().length() > 0) {
                lngCityName = sb.toString();
                lng_city.setText(lngCityName);
            }
            //获取经纬度
//            BigDecimal latitude = new BigDecimal(aLocation.getLatitude());
//            x = latitude.setScale(8, BigDecimal.ROUND_DOWN).doubleValue();
//            BigDecimal longitude = new BigDecimal(aLocation.getLongitude());
//            y = longitude.setScale(8, BigDecimal.ROUND_DOWN).doubleValue();
//            //搜索框内容为空时执行周边搜索方法,有内容时执行关键词搜索方法
//            final String searchKey = mSearch.getText().toString().trim();
//            if (TextUtils.isEmpty(searchKey)) {
//                PoiCheckEnd(aLocation, "");
//            } else {
//                initListener();
//            }
        }
//        if (mListener != null && aLocation != null) {
//            // mALocation = aLocation;
//            /**
//             * 获取城市的编码
//             */
//            //cityCode = aLocation.getCityCode();
//            String city = aLocation.getCity();
//            StringBuffer sb = new StringBuffer(256);
//            sb.append(aLocation.getCity());
//            Log.i("TAG", "--------->" + "定位成功" + city);
//            if (sb.toString() != null && sb.toString().length() > 0) {
//                lngCityName = sb.toString();
//                lng_city.setText(lngCityName);
//            }
//            //获取经纬度
////            BigDecimal latitude = new BigDecimal(aLocation.getLatitude());
////            x = latitude.setScale(8, BigDecimal.ROUND_DOWN).doubleValue();
////            BigDecimal longitude = new BigDecimal(aLocation.getLongitude());
////            y = longitude.setScale(8, BigDecimal.ROUND_DOWN).doubleValue();
////            //搜索框内容为空时执行周边搜索方法,有内容时执行关键词搜索方法
////            final String searchKey = mSearch.getText().toString().trim();
////            if (TextUtils.isEmpty(searchKey)) {
////                PoiCheckEnd(aLocation, "");
////            } else {
////                initListener();
////            }
//        }
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
        // mListener = null;
        if (mAMapLocationManager != null) {
            mAMapLocationManager.removeUpdates(this);
            mAMapLocationManager.destory();
        }
        mAMapLocationManager = null;
    }

//	private void setAdapter(List<City> list) {
//		adapter = new ListAdapter(this, list);
//		personList.setAdapter(adapter);
//	}

    public class ListAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        final int VIEW_TYPE = 3;

        public ListAdapter(Context context) {
            this.inflater = LayoutInflater.from(context);
            alphaIndexer = new HashMap<String, Integer>();
            sections = new String[ShowCity_lists.size()];
            for (int i = 0; i < ShowCity_lists.size(); i++) {
                // 当前汉语拼音首字母
                String currentStr = getAlpha(ShowCity_lists.get(i).getPinyi());
                // 上一个汉语拼音首字母，如果不存在为“ ”
                String previewStr = (i - 1) >= 0 ? getAlpha(ShowCity_lists.get(i - 1)
                        .getPinyi()) : " ";
                if (!previewStr.equals(currentStr)) {
                    String name = getAlpha(ShowCity_lists.get(i).getPinyi());
                    alphaIndexer.put(name, i);
                    sections[i] = name;
                }
            }
        }

        @Override
        public int getCount() {
            return ShowCity_lists.size();
        }

        @Override
        public Object getItem(int position) {
            return ShowCity_lists.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            // TODO Auto-generated method stub
            int type = 2;

            if (position == 0 && sh.getText().length() == 0) {//不是在搜索状态下
                type = 0;
            }
            return type;
        }

        @Override
        public int getViewTypeCount() {// 这里需要返回需要集中布局类型，总大小为类型的种数的下标
            return VIEW_TYPE;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            int viewType = getItemViewType(position);
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.list_location_city_item, null);
                holder = new ViewHolder();
                holder.alpha = (TextView) convertView
                        .findViewById(R.id.alpha);
                holder.name = (TextView) convertView
                        .findViewById(R.id.name);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
//				if (sh.getText().length()==0) {//搜所状态
//					holder.name.setText(list.get(position).getName());
//					holder.alpha.setVisibility(View.GONE);
//				}else if(position>0){
            //显示拼音和热门城市，一次检查本次拼音和上一个字的拼音，如果一样则不显示，如果不一样则显示

            holder.name.setText(ShowCity_lists.get(position).getName());
            String currentStr = getAlpha(ShowCity_lists.get(position).getPinyi());//本次拼音
            String previewStr = (position - 1) >= 0 ? getAlpha(ShowCity_lists.get(position - 1).getPinyi()) : " ";//上一个拼音
            if (!previewStr.equals(currentStr)) {//不一样则显示
                holder.alpha.setVisibility(View.VISIBLE);
                if (currentStr.equals("#")) {
                    currentStr = "热门城市";
                }
                holder.alpha.setText(currentStr);
            } else {
                holder.alpha.setVisibility(View.GONE);
            }
//				}
            return convertView;
        }

        private class ViewHolder {
            TextView alpha; // 首字母标题
            TextView name; // 城市名字
        }
    }

//    // 初始化汉语拼音首字母弹出提示框
//    private void initOverlay() {
//        LayoutInflater inflater = LayoutInflater.from(this);
//        overlay = (TextView) inflater.inflate(R.layout.overlay, null);
//        overlay.setVisibility(View.INVISIBLE);
//        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
//                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
//                WindowManager.LayoutParams.TYPE_APPLICATION,
//                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
//                        | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
//                PixelFormat.TRANSLUCENT);
//        WindowManager windowManager = (WindowManager) this
//                .getSystemService(Context.WINDOW_SERVICE);
//        windowManager.addView(overlay, lp);
//    }

    private class LetterListViewListener implements
            MyLetterListView.OnTouchingLetterChangedListener {

        @Override
        public void onTouchingLetterChanged(final String s) {
            if (alphaIndexer.get(s) != null) {
                int position = alphaIndexer.get(s);
                personList.setSelection(position);
                // overlay.setText(sections[position]);
                // overlay.setVisibility(View.VISIBLE);
                // handler.removeCallbacks(overlayThread);
                // 延迟一秒后执行，让overlay为不可见
                // handler.postDelayed(overlayThread, 1500);
            }
        }

    }

//    // 设置overlay不可见
//    private class OverlayThread implements Runnable {
//        @Override
//        public void run() {
//            overlay.setVisibility(View.GONE);
//        }
//
//    }

    // 获得汉语拼音首字母
    private String getAlpha(String str) {

        if (str.equals("-")) {
            return "&";
        }
        if (str == null) {
            return "#";
        }
        if (str.trim().length() == 0) {
            return "#";
        }
        char c = str.trim().substring(0, 1).charAt(0);
        // 正则表达式，判断首字母是否是英文字母
        Pattern pattern = Pattern.compile("^[A-Za-z]+$");
        if (pattern.matcher(c + "").matches()) {
            return (c + "").toUpperCase();
        } else {
            return "#";
        }
    }

//    /**
//     * 初始化AMap对象
//     */
//    private void init() {
//        if (aMap == null) {
//            aMap = mapView.getMap();
//            aMap.setLocationSource(this);
//        }
//    }


//
//    private class MyLocationListenner implements BDLocationListener {
//        @Override
//        public void onReceiveLocation(BDLocation location) {
//
//            if (location == null)
//                return;
//            StringBuffer sb = new StringBuffer(256);
//            if (location.getLocType() == BDLocation.TypeGpsLocation) {
////				sb.append(location.getAddrStr());
//                sb.append(location.getCity());
//            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
//                sb.append(location.getCity());
//            }
//            if (sb.toString() != null && sb.toString().length() > 0) {
//                lngCityName = sb.toString();
//                lng_city.setText(lngCityName);
//            }
//
//        }
//
//        public void onReceivePoi(BDLocation poiLocation) {
//
//        }
//    }

    Handler handler2 = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case SHOWDIALOG:
                    ProgressHUD.showLoadingMessage(LocationActivity.this, "正在加载数据，请稍候...", true);
                    //   progress = AppUtil.showProgress(LocationActivity.this, "正在加载数据，请稍候...");
                    break;
                case DISMISSDIALOG:
                    ProgressHUD.dismiss();
                    adapter = new ListAdapter(LocationActivity.this);
                    personList.setAdapter(adapter);
//				personList.setAdapter(adapter);

                    sh.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count,
                                                      int after) {
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            //搜索符合用户输入的城市名
                            if (s.length() > 0) {
                                ArrayList<City> changecity = new ArrayList<City>();
                                for (int i = 0; i < city_lists.size(); i++) {
                                    if (city_lists.get(i).name.indexOf(sh.getText().toString()) != -1) {
                                        changecity.add(city_lists.get(i));
                                    }
                                }
                                ShowCity_lists = changecity;
                            } else {
                                ShowCity_lists = allCity_lists;
                            }
                            adapter.notifyDataSetChanged();
                        }
                    });
                    break;
                default:
                    break;
            }
        }

        ;
    };

}

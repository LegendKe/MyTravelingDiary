package com.ruihai.xingka.ui.mine.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ruihai.xingka.R;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.api.model.User;
import com.ruihai.xingka.ui.mine.EditUserDataActivity;
import com.ruihai.xingka.widget.wheelview.AbstractWheelTextAdapter;
import com.ruihai.xingka.widget.wheelview.ArrayWheelAdapter;
import com.ruihai.xingka.widget.wheelview.OnWheelChangedListener;
import com.ruihai.xingka.widget.wheelview.OnWheelScrollListener;
import com.ruihai.xingka.widget.wheelview.WheelView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by apple on 15/8/22.
 */
@SuppressLint("ValidFragment")
public class LocationDialogFragment extends DialogFragment {

    private boolean scrolling = false;

    final String[] provinces = new String[]{"北京市", "天津市", "安徽省", "上海市", "江苏省", "浙江省", "河北省", "山西省", "内蒙古", "辽宁省", "吉林省", "黑龙江省", "福建省", "江西省", "山东省", "河南省", "湖北省", "湖南省", "广东省", "青海省", "甘肃省", "陕西省", "广西", "海南省", "重庆市", "四川省", "贵州省", "云南省 ", "新疆", "西藏", "宁夏"};

    final String cities[][] = new String[][]{
            //北京市
            new String[]{"东城区", "西城区", "朝阳区", "丰台区", "石景山区", "海淀区", "门头沟区", "房山区", "通州区", "顺义区", "昌平区", "大兴区", "怀柔区", "平谷区", "密云县", "延庆县"},
            //天津市
            new String[]{"和平区", "河东区", "河西区", "南开区", "河北区", "红桥区", "东丽区", "西青区", "津南区", "北辰区", "武清区", "宝坻区", "滨海新区", "宁河县", "静海县", "蓟县"},
            //安徽省
            new String[]{"合肥市", "巢湖市", "安庆市", "蚌埠市", "亳州市", "池州市", "滁州市", "阜阳市", "淮北市", "淮南市", "黄山市", "六安市", "马鞍山市", "宿州市", "铜陵市", "芜湖市", "宣城市"},
            //上海市
            new String[]{"黄浦区", "徐汇区", "长宁区", "静安区", "普陀区", "闸北区", "虹口区", "杨浦区", "闵行区", "宝山区", "嘉定区", "浦东新区", "金山区", "松江区", "青浦区", "奉贤区", "崇明县"},
            //江苏省
            new String[]{"南京市", "无锡市", "徐州市", "常州市", "苏州市", "淮安市", "连云港市", "南通市", "宿迁市", "泰州市", "盐城市", "扬州市", "镇江市"},

            //浙江省
            new String[]{"杭州市", "湖州市", "嘉兴市", "金华市", "丽水市", "宁波市", "衢州市", "绍兴市", "台州市", "温州市", "舟山市"},
            //河北省
            new String[]{"石家庄市", "唐山市", "秦皇岛市", "邯郸市", "邢台市", "保定市", "张家口市", "承德市", "沧州市", "廊坊市", "衡水市"},
            // 山西省
            new String[]{"太原市", "大同市", "阳泉市", "长治市", "晋城市", "朔州市", "晋中市", "运城市", "忻州市", "临汾市", "吕梁市"},
            //内蒙古自治区
            new String[]{"呼和浩特市", "包头市", "乌海市", "赤峰市", "通辽市", "鄂尔多斯市", "呼伦贝尔市", "巴彦淖尔市", "乌兰察布市", "兴安盟", "锡林郭勒盟", " 阿拉善盟"},
            //辽宁省
            new String[]{"沈阳市", "大连市", "鞍山市", "本溪市", "丹东市", "锦州市", "营口市", "阜新市", "辽阳市", "盘锦市", "铁岭市", "朝阳市", "葫芦岛市"},

            //吉林省
            new String[]{"长春市", "吉林市", "四平市", "辽源市", "通化市", "白山市", "松原市", "白城市", "延边朝鲜族自治州"},
            // 黑龙江省
            new String[]{"哈尔滨市", "齐齐哈尔市", "鸡西市", "鹤岗市", "双鸭山市", "大庆市", "伊春市", "佳木斯市", "七台河市", "牡丹江市", "黑河市", "绥化市", "大兴安岭地区"},
            // 福建省
            new String[]{"福州市", "龙岩市", "南平市", "宁德市", "莆田市", "泉州市", "三明市", "厦门市", "漳州市"},
            //江西省
            new String[]{"南昌市", "抚州市", "赣州市", "吉安市", "景德镇市", "九江市", "上饶市", "新余市", "宜春市", "鹰潭市"},
            //山东省
            new String[]{"济南市", "滨州市", "德州市", "东营市", "菏泽市", "济宁市", "莱芜市", "聊城市", "临沂市", "青岛市", "日照市", "泰安市", "威海市", "潍坊市", "烟台市", "潍坊市", "烟台市", "枣庄市", "淄博市"},

            //  河南省
            new String[]{"郑州市", "安阳市", "鹤壁市", "焦作市", "开封市", "洛阳市", "漯河市", "南阳市", "平顶山市", "濮阳市", "三门峡市", "商丘市", "新乡市", "信阳市", "许昌市", "周口市", "驻马店市"},
            //  湖北省
            new String[]{"武汉市", "鄂州市", "黄冈市", "黄石市", "荆门市", "荆州市", "恩施土家族苗族自治州", "十堰市", "随州市", "咸宁市", "襄樊市", "孝感市", "宜昌市", "省直辖"},
            // 湖南省
            new String[]{"长沙市", "常德市", "郴州市", "衡阳市", "怀化市", "娄底市", "邵阳市", "湘潭市", "湘西土家族苗族自治州", "益阳市", "永州市", "岳阳市", "张家界市", "株洲市"},
            // 广东省
            new String[]{"广州市", "潮州市", "东莞市", "佛山市", "河源市", "惠州市", "江门市", "揭阳市", "茂名市", "梅州市", "清远市", "汕头市", "汕尾市", "韶关市", "深圳市", "阳江市", "云浮市", "湛江市", "肇庆市", "中山市", "珠海市"},
            // 青海省
            new String[]{"西宁市", "果洛藏族自治州", "海北藏族自治州", "海东地区", "海南藏族自治州", "海西蒙古族藏族自治州", "黄南藏族自治州", "玉树藏族自治州"},

            // 甘肃省 "", "广西壮族自治区", "海南省", ""
            new String[]{"兰州市", "白银市", "定西市", "甘南藏族自治州", "嘉峪关市", "金昌市", "酒泉市", "临夏回族自治州", "陇南市", "平凉市", "庆阳市", "天水市", "武威市", "张掖市"},
            // 陕西省
            new String[]{"西安市", "安康市", "咸阳市", "宝鸡市", "汉中市", "商洛市", "铜川市", "渭南市", "延安市", "榆林市", "庆阳市", "天水市", "武威市", "张掖市"},
            // 广西壮族自治区
            new String[]{"南宁市", "百色市", "北海市", "崇左市", "防城港市", "贵港市", "桂林市", "河池市", "贺州市", "来宾市", "柳州市", "钦州市", "梧州市", "玉林市"},
            // 海南省
            new String[]{"海口市", "三亚市", "三沙市", "省直辖"},
            // 重庆市
            new String[]{"万州区", "涪陵区", "渝中区", "大渡口区", "江北区", "沙坪坝区", "九龙坡区", "南岸区", "北碚区", "綦江区", "大足区", "渝北区", "巴南区", "黔江区", "长寿区", "江津区", "合川区", "永川区", "南川区", "潼南县", "铜梁县", "荣昌县", "璧山县", "梁平县", "城口县", "丰都县", "垫江县", "武隆县", "忠县", "开县", "云阳县", "奉节县", "巫山县", "巫溪县", "石柱土家族自治县", "秀山土家族苗族自治县", "酉阳土家族苗族自治县", "彭水苗族土家族自治县"},

            //四川省
            new String[]{"成都市", "巴中市", "达州市", "德阳市", "甘孜藏族自治州", "广安市", "广元市", "乐山市", "凉山彝族自治州", "泸州市", "眉山市", "绵阳市", "阿坝藏族羌族自治州", "内江市", "南充市", "攀枝花市", "遂宁市", "雅安市", "宜宾市", "资阳市", "自贡市"},
            //贵州省
            new String[]{"贵阳市", "安顺市", "毕节地区", "六盘水市", "黔东南苗族侗族自治州", "黔南布依族苗族自治州", "黔西南布依族苗族自治州", "铜仁地区", "遵义市"},
            // 云南省
            new String[]{"昆明市", "保山市", "楚雄彝族自治州", "大理白族自治州", "德宏傣族景颇族自治州", "迪庆藏族自治州", "红河哈尼族彝族自治州", "丽江市", "临沧市", "怒江僳僳族自治州", "普洱市", "曲靖市", "文山壮族苗族自治州", "西双版纳傣族自治州", "玉溪市", "昭通市"},
            // 新疆维吾尔自治区
            new String[]{"乌鲁木齐市", "阿克苏地区", "阿勒泰地区", "自治区直辖县级行政区划", "巴音郭楞蒙古自治州", "博尔塔拉蒙古自治州", "昌吉回族自治州", "哈密地区", "和田地区", "喀什地区", "克拉玛依市", "克孜勒苏柯尔克孜自治州", "塔城地区", "吐鲁番地区", "伊犁哈萨克自治州"},
            // 西藏自治区
            new String[]{"拉萨市", "阿里地区", "昌都地区", "林芝地区", "那曲地区", "日喀则地区", "山南地区"},
            // 宁夏回族自治区
            new String[]{"银川市", "固原市", "石嘴山市", "吴忠市", "中卫市"},
    };

    @BindView(R.id.wheel_province)
    WheelView mProvince;
    @BindView(R.id.wheel_city)
    WheelView mCity;
    @BindView(R.id.btn_positive)
    Button mPositiveBtn;
    @BindView(R.id.btn_negative)
    Button mNegativeBtn;

    private EditUserDataActivity context;
    private User currentUser;
    private String Province;
    private String City;

    public interface OnLocationDialogListener {
        public void location(String province, String city);
    }

    OnLocationDialogListener locationDialogListener;

    public LocationDialogFragment(EditUserDataActivity context, OnLocationDialogListener locationDialogListener) {
        this.context = context;
        this.locationDialogListener = locationDialogListener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Dialog dialog = new Dialog(getActivity(), R.style.CommonDialog);
        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        View v = mInflater.inflate(R.layout.dialogfragment_location, null);
        ButterKnife.bind(this, v);
        mProvince.setVisibleItems(5);
        mProvince.setViewAdapter(new CountryAdapter(getActivity(), provinces));

        final WheelView city = (WheelView) v.findViewById(R.id.wheel_city);
        city.setVisibleItems(5);

        mProvince.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                if (!scrolling) {
                    updateCities(city, cities, newValue);
                }
            }
        });

        mProvince.addScrollingListener(new OnWheelScrollListener() {
            public void onScrollingStarted(WheelView wheel) {
                scrolling = true;
            }

            public void onScrollingFinished(WheelView wheel) {
                scrolling = false;
                updateCities(city, cities, mProvince.getCurrentItem());
            }
        });

        /**
         * 再次点击所在地后为上次默认选择的地点
         */
        currentUser = AccountInfo.getInstance().loadAccount();
        String address = currentUser.getAddress();
        //判断从服务器传回的地址
        if (TextUtils.isEmpty(address)) {
            mProvince.setCurrentItem(0);
        } else {
            //截取省市
            Province = address.substring(0, address.indexOf(","));
            City = address.substring(address.indexOf(",") + 1);
//            Province = address;
//            City = address;
            //遍历省市数组获取数组下标
            for (int i = 0; i < provinces.length; i++) {
                if (provinces[i].equals(Province)) {
                    mProvince.setCurrentItem(i);
                    Log.i("省份", provinces[i]);
                    for (int j = 0; j < cities[i].length; j++) {
                        if (cities[i][j].equals(City)) {
                            city.setCurrentItem(j);
                            Log.i("城市", cities[i][j]);
                        }
                    }
                }

            }
        }

        mPositiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = mProvince.getCurrentItem();
                String provincesName = provinces[i];
                int j = city.getCurrentItem();
                String cityName = cities[i][j];
                locationDialogListener.location(provincesName, cityName);
//                AppUtility.showToast(provincesName + cityName);
                context.changeData();
                dismiss();
            }
        });

        mNegativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        dialog.setContentView(v);

        return dialog;
    }

    /**
     * Updates the city wheel
     */
    private void updateCities(WheelView city, String cities[][], int index) {
        ArrayWheelAdapter<String> adapter =
                new ArrayWheelAdapter<String>(getActivity(), cities[index]);
        adapter.setTextSize(18);
        city.setViewAdapter(adapter);
        city.setCurrentItem(cities[index].length / 2);
    }


    /**
     * Adapter for countries
     */
    private class CountryAdapter extends AbstractWheelTextAdapter {

        String[] provinces;

        /**
         * Constructor
         */
        protected CountryAdapter(Context context, String[] provinces) {
            super(context, R.layout.layout_province, NO_RESOURCE);
            this.provinces = provinces;
            setItemTextResource(R.id.province_name);
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            View view = super.getItem(index, cachedView, parent);
            return view;
        }

        @Override
        public int getItemsCount() {
            return provinces.length;
        }

        @Override
        protected CharSequence getItemText(int index) {
            return provinces[index];
        }


    }

}

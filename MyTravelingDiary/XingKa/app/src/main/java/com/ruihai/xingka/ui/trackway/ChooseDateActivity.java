package com.ruihai.xingka.ui.trackway;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ruihai.xingka.R;
import com.ruihai.xingka.ui.BaseActivity;
import com.ruihai.xingka.utils.AppUtility;
import com.ruihai.xingka.widget.MyCalendar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author gebixiaozhang
 */
@SuppressLint("SimpleDateFormat")
public class ChooseDateActivity extends BaseActivity implements MyCalendar.OnDaySelectListener {
    @BindView(R.id.tv_right)
    TextView mRight;
    @BindView(R.id.tv_title)
    TextView mTitle;
    @BindView(R.id.ll)
    LinearLayout ll;

    MyCalendar c1;

    private View lastView;
    private String lastDate;

    Date date;
    String nowday;
    long nd = 1000 * 24L * 60L * 60L;//一天的毫秒数
    SimpleDateFormat simpleDateFormat, sd1, sd2;
    //    SharedPreferences sp;
    //    SharedPreferences.Editor editor;

    private String inday, outday, sp_inday, sp_outday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_date);
        ButterKnife.bind(this);
        //      sp = getSharedPreferences("date", Context.MODE_PRIVATE);
        //本地保存
        //        sp_inday = sp.getString("dateIn", "");
        //        sp_outday = sp.getString("dateOut", "");
        //     editor = sp.edit();
        sp_inday = getIntent().getStringExtra("beginTime");
        sp_outday = getIntent().getStringExtra("endTime");
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        nowday = simpleDateFormat.format(new Date());
        sd1 = new SimpleDateFormat("yyyy");
        sd2 = new SimpleDateFormat("dd");

        mTitle.setText("日期选择");
        mRight.setVisibility(View.VISIBLE);
        init();
    }

    private void init() {
        List<String> listDate = getDateList();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        for (int i = 0; i < listDate.size(); i++) {
            c1 = new MyCalendar(this);
            c1.setLayoutParams(params);
            Date date = null;
            try {
                date = simpleDateFormat.parse(listDate.get(i));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (!TextUtils.isEmpty(sp_inday)) {
                inday = sp_inday;
                c1.setInDay(sp_inday);
            }
            if (!TextUtils.isEmpty(sp_outday)) {
                outday = sp_outday;
                c1.setOutDay(sp_outday);
            }
            c1.setTheDay(date);
            c1.setOnDaySelectListener(this);
            ll.addView(c1);
        }
    }

    @OnClick(R.id.tv_back)
    void onBack() {
        finish();
    }

    @OnClick(R.id.tv_right)
    void onRight() {

        if (TextUtils.isEmpty(inday)) {
            AppUtility.showToast("请选择出发时间");
        } else if (TextUtils.isEmpty(outday)) {
            AppUtility.showToast("请选择返回时间");
        } else {
            Intent intent = new Intent();
            intent.putExtra("beginTime", inday);
            intent.putExtra("endTime", outday);
            setResult(RESULT_OK, intent);
            finish();
        }

    }

    @Override
    public void onDaySelectListener(View view, String date) {
        //若日历日期小于当前日期，或日历日期-当前日期超过三个月，则不能点击

        try {
            if (simpleDateFormat.parse(date).getTime() < simpleDateFormat.parse(nowday).getTime()) {
                return;
            }

//            long dayxc = (simpleDateFormat.parse(date).getTime() - simpleDateFormat.parse(nowday).getTime()) / nd;
//            if (dayxc > 90) {
//                return;
//            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
//        若以前已经选择了日期，则在进入日历后会显示以选择的日期，该部分作用则是重新点击日历时，清空以前选择的数据（包括背景图案）
        if (!TextUtils.isEmpty(sp_inday)) {
            c1.viewIn.setBackgroundColor(Color.WHITE);
            ((TextView) c1.viewIn.findViewById(R.id.tv_calendar_day)).setTextColor(Color.BLACK);
            ((TextView) c1.viewIn.findViewById(R.id.tv_calendar)).setText("");
        }
        if (!TextUtils.isEmpty(sp_outday)) {
            c1.viewOut.setBackgroundColor(Color.WHITE);
            ((TextView) c1.viewOut.findViewById(R.id.tv_calendar_day)).setTextColor(Color.BLACK);
            ((TextView) c1.viewOut.findViewById(R.id.tv_calendar)).setText("");
        }

        String dateDay = date.split("-")[2];
        if (Integer.parseInt(dateDay) < 10) {
            dateDay = date.split("-")[2].replace("0", "");
        }

        TextView textDayView = (TextView) view.findViewById(R.id.tv_calendar_day);
        TextView textView = (TextView) view.findViewById(R.id.tv_calendar);
        view.setBackgroundColor(Color.parseColor("#33B5E5"));
        textDayView.setTextColor(Color.WHITE);

        if (TextUtils.isEmpty(inday)) {
            try {
                if (outday != null && simpleDateFormat.parse(outday).getTime() < simpleDateFormat.parse(date).getTime()) {
                    view.setBackgroundColor(Color.WHITE);
                    textDayView.setTextColor(Color.BLACK);
                    Toast.makeText(ChooseDateActivity.this, "返回日期不能小于出发日期", Toast.LENGTH_SHORT).show();
                    inday = "";
                    return;
                } else {
                    textDayView.setText(dateDay);
                    textView.setText("出发");
                    inday = date;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }


        } else {

            if (inday.equals(date)) {
                view.setBackgroundColor(Color.WHITE);
                textDayView.setText(dateDay);
                textDayView.setTextColor(Color.BLACK);
                textView.setText("");
                inday = "";

            }
//            else  if (!TextUtils.isEmpty(outday)){
//
//            }

            else {
                try {
                    if (simpleDateFormat.parse(date).getTime() < simpleDateFormat.parse(inday).getTime()) {
                        view.setBackgroundColor(Color.WHITE);
                        textDayView.setTextColor(Color.BLACK);
                        Toast.makeText(ChooseDateActivity.this, "返回日期不能小于出发日期", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (!TextUtils.isEmpty(outday)) {
                    if (outday.equals(date)) {
                        view.setBackgroundColor(Color.WHITE);
                        textDayView.setText(dateDay);
                        textDayView.setTextColor(Color.BLACK);
                        textView.setText("");
                        outday = "";
                    } else {
                        if (lastView != null) {
                            TextView textDayView1 = (TextView) lastView.findViewById(R.id.tv_calendar_day);
                            TextView textView1 = (TextView) lastView.findViewById(R.id.tv_calendar);
                            //  lastView.setBackgroundColor(Color.parseColor("#33B5E5"));
                            //  textDayView1.setTextColor(Color.WHITE);
                            lastView.setBackgroundColor(Color.WHITE);
                            textDayView1.setText(lastDate);
                            textDayView1.setTextColor(Color.BLACK);
                            textView1.setText("");

                            textDayView.setText(dateDay);
                            textView.setText("返回");
                            outday = date;
                            lastView = view;
                            lastDate = dateDay;
                        }
                    }
                } else {

                    textDayView.setText(dateDay);
                    textView.setText("返回");
                    outday = date;
                    lastView = view;
                    lastDate = dateDay;

                    // c1.notifyData(inday, outday);
//                editor.putString("dateIn", inday);
//                editor.putString("dateOut", outday);
                    //editor.commit();
                    // finish();
                }
            }
        }

    }

    //六个月
    @SuppressLint("SimpleDateFormat")
    public List<String> getDateList() {
        List<String> list = new ArrayList<String>();
        Date date = new Date();
        int nowMon = date.getMonth() + 1;
        String yyyy = sd1.format(date);
        String dd = sd2.format(date);
        if (nowMon == 8) {
            list.add(simpleDateFormat.format(date));
            list.add(yyyy + "-9-" + dd);
            list.add(yyyy + "-10-" + dd);
            list.add(yyyy + "-11-" + dd);
            list.add(yyyy + "-12-" + dd);
            list.add((Integer.parseInt(yyyy) + 1) + "-01-" + dd);
        } else if (nowMon == 9) {
            list.add(simpleDateFormat.format(date));
            list.add(yyyy + "-10-" + dd);
            list.add(yyyy + "-11-" + dd);
            list.add(yyyy + "-12-" + dd);
            list.add((Integer.parseInt(yyyy) + 1) + "-01-" + dd);
            list.add((Integer.parseInt(yyyy) + 1) + "-02-" + dd);

        } else if (nowMon == 10) {
            list.add(simpleDateFormat.format(date));
            list.add(yyyy + "-11-" + dd);
            list.add(yyyy + "-12-" + dd);
            list.add((Integer.parseInt(yyyy) + 1) + "-01-" + dd);
            list.add((Integer.parseInt(yyyy) + 1) + "-02-" + dd);
            list.add((Integer.parseInt(yyyy) + 1) + "-03-" + dd);

        } else if (nowMon == 11) {
            list.add(simpleDateFormat.format(date));
            list.add(yyyy + "-12-" + dd);
            list.add((Integer.parseInt(yyyy) + 1) + "-01-" + dd);
            list.add((Integer.parseInt(yyyy) + 1) + "-02-" + dd);
            list.add((Integer.parseInt(yyyy) + 1) + "-03-" + dd);
            list.add((Integer.parseInt(yyyy) + 1) + "-04-" + dd);

        } else if (nowMon == 12) {
            list.add(simpleDateFormat.format(date));
            list.add((Integer.parseInt(yyyy) + 1) + "-01-" + dd);
            list.add((Integer.parseInt(yyyy) + 1) + "-02-" + dd);
            list.add((Integer.parseInt(yyyy) + 1) + "-03-" + dd);
            list.add((Integer.parseInt(yyyy) + 1) + "-04-" + dd);
            list.add((Integer.parseInt(yyyy) + 1) + "-05-" + dd);

        } else {
            list.add(simpleDateFormat.format(date));
            list.add(yyyy + "-" + getMon((nowMon + 1)) + "-" + dd);
            list.add(yyyy + "-" + getMon((nowMon + 2)) + "-" + dd);
            list.add(yyyy + "-" + getMon((nowMon + 3)) + "-" + dd);
            list.add(yyyy + "-" + getMon((nowMon + 4)) + "-" + dd);
            list.add(yyyy + "-" + getMon((nowMon + 5)) + "-" + dd);

        }
        return list;
    }

    public String getMon(int mon) {
        String month = "";
        if (mon < 10) {
            month = "0" + mon;
        } else {
            month = "" + mon;
        }
        return month;
    }

}

package com.ruihai.xingka;

import android.graphics.Bitmap;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.style.QuoteSpan;
import android.text.style.URLSpan;

import com.ruihai.xingka.base.htmltext.GrayQuoteSpan;
import com.ruihai.xingka.base.htmltext.URLSpanNoUnderline;

import org.xml.sax.XMLReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;

/**
 * 公共的全局方法
 * <p/>
 * Created by zecker on 15/9/25.
 */
public class Global {

    public static final String DEFAULT_HOST = "http://api.xingka.cc";
    public static final String DEFAULT_SHARE_HOST = "http://web.xingka.cc";
    public static final String AGREEMENT_URL = String.format("%s/share/agreement.html", DEFAULT_HOST);
    public static final String ABOUTUS_URL = "http://mobile.xingka.cc/about/index.html";
    public static final String CONVENTION_URL = String.format("%s/share/Convention.html", DEFAULT_SHARE_HOST);//社区公约
    public static final String COOPEN_URL = "http://activity.xingka.cc/Coopen/index.html";//开机启动广告页面
    //public static final String COOPEN_URL = "http://activity.xingka.cc/Coopen/test/index.html";//开机启动广告页面
    //public static final String SHARE_CAPTION_URL = DEFAULT_SHARE_HOST + "/share/photo.html?account=%s&id=%s";
    public static final String SHARE_CAPTION_URL = DEFAULT_SHARE_HOST + "/share/details.html?account=%s&id=%s&web=%s";
    public static final String SHARE_TRACKWAY_URL = DEFAULT_SHARE_HOST + "/share/lp/details.html?account=%s&tguid=%s&share=1";
    public static final String SHARE_CARD_URL = DEFAULT_SHARE_HOST + "/share/card.html?account=%s";
    //    public static final String SHARE_PRODUCT_URL = DEFAULT_SHARE_HOST + "/Share/Share.html";
    public static final String SHARE_PRODUCT_URL = "http://share.xingka.cc/v2.html?acc=%s";
    public static final String INTEGRAL_URL = "http://Integral.xingka.cc/index.html?acc=%s&os=1";
    public static final String OFFICAL_GRAOUP_SHARE_URL = "http://share.xingka.cc/groupshare/index.html?acc=%s&te=%s";

    //public static final String INTEGRAL_URL = "http://192.168.199.154:8540/index.html?acc=%s&os=1";
    private static final SimpleDateFormat sFormatToday = new SimpleDateFormat("今天 HH:mm");
    private static final SimpleDateFormat sFormatThisYear = new SimpleDateFormat("MM-dd HH:mm");
    private static final SimpleDateFormat sFormatOtherYear = new SimpleDateFormat("yy/MM/dd HH:mm");
    private static final SimpleDateFormat sFormatMessageToday = new SimpleDateFormat("今天");
    private static final SimpleDateFormat sFormatMessageThisYear = new SimpleDateFormat("MM/dd");
    private static final SimpleDateFormat sFormatMessageOtherYear = new SimpleDateFormat("yy/MM/dd");


    public static String dayToNow(long time) {
        return dayToNow(time, true);
    }

    public static String dayToNow(long time, boolean displayHour) {
        long nowMill = System.currentTimeMillis();

        long minute = (nowMill - time) / 60000;
        if (minute < 60) {
            if (minute <= 0) {
                return Math.max((nowMill - time) / 1000, 1) + "秒前"; // 由于手机时间的原因，有时候会为负，这时候显示1秒前
            } else {
                return minute + "分钟前";
            }
        }

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(time);
        int year = calendar.get(GregorianCalendar.YEAR);
        int month = calendar.get(GregorianCalendar.MONTH);
        int day = calendar.get(GregorianCalendar.DAY_OF_MONTH);

        calendar.setTimeInMillis(nowMill);
        Long timeObject = new Long(time);
        if (calendar.get(GregorianCalendar.YEAR) != year) { // 不是今年
            SimpleDateFormat sFormatOtherYear = displayHour ? Global.sFormatOtherYear : Global.sFormatMessageOtherYear;
            return sFormatOtherYear.format(timeObject);
        } else if (calendar.get(GregorianCalendar.MONTH) != month
                || calendar.get(GregorianCalendar.DAY_OF_MONTH) != day) { // 今年
            SimpleDateFormat sFormatThisYear = displayHour ? Global.sFormatThisYear : Global.sFormatMessageThisYear;
            return sFormatThisYear.format(timeObject);
        } else { // 今天
            SimpleDateFormat sFormatToday = displayHour ? Global.sFormatToday : Global.sFormatMessageToday;
            return sFormatToday.format(timeObject);
        }
    }

    /**
     * 月份转中文
     *
     * @param month
     * @return
     */
    public static String getMonth(int month) {
        String monthStr = null;
        switch (month) {
            case 1:
                monthStr = "一月";
                break;
            case 2:
                monthStr = "二月";
                break;
            case 3:
                monthStr = "三月";
                break;
            case 4:
                monthStr = "四月";
                break;
            case 5:
                monthStr = "五月";
                break;
            case 6:
                monthStr = "六月";
                break;
            case 7:
                monthStr = "七月";
                break;
            case 8:
                monthStr = "八月";
                break;
            case 9:
                monthStr = "九月";
                break;
            case 10:
                monthStr = "十月";
                break;
            case 11:
                monthStr = "十一";
                break;
            case 12:
                monthStr = "十二";
                break;
        }
        return monthStr;
    }

    public static Html.TagHandler tagHandler = new Html.TagHandler() {
        @Override
        public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
            if (tag.toLowerCase().equals("code") && !opening) {
                output.append("\n\n");
            }
        }
    };

    public static Spannable changeHyperlinkColor(String content, Html.ImageGetter imageGetter, Html.TagHandler tagHandler) {
        return changeHyperlinkColor(content, imageGetter, tagHandler, 0xFF3BBD79);
    }

    public static Spannable changeHyperlinkColor(String content, Html.ImageGetter imageGetter, Html.TagHandler tagHandler, int color) {
        Spannable s = (Spannable) Html.fromHtml(content, imageGetter, tagHandler);
        return getCustomSpannable(color, s);
    }

    private static Spannable getCustomSpannable(int color, Spannable s) {
        URLSpan[] urlSpan = s.getSpans(0, s.length(), URLSpan.class);
        for (URLSpan span : urlSpan) {
            int start = s.getSpanStart(span);
            int end = s.getSpanEnd(span);
            s.removeSpan(span);
            span = new URLSpanNoUnderline(span.getURL(), color);
            s.setSpan(span, start, end, 0);
        }

        QuoteSpan quoteSpans[] = s.getSpans(0, s.length(), QuoteSpan.class);
        for (QuoteSpan span : quoteSpans) {
            int start = s.getSpanStart(span);
            int end = s.getSpanEnd(span);
            s.removeSpan(span);
            GrayQuoteSpan grayQuoteSpan = new GrayQuoteSpan();
            s.setSpan(grayQuoteSpan, start, end, 0);
        }

        return s;
    }

    public static class MessageParse {
        public String text = "";
        public ArrayList<String> uris = new ArrayList<>();
        public boolean isVoice;
        public String voiceUrl;
        public int voiceDuration;
        public int played;
        public int id;

        public String toString() {
            String s = "text " + text + "\n";
            for (int i = 0; i < uris.size(); ++i) {
                s += uris.get(i) + "\n";
            }
            return s;
        }
    }

}

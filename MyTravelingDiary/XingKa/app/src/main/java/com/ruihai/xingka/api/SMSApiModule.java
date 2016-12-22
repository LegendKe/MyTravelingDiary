package com.ruihai.xingka.api;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.DateTypeAdapter;
import com.ruihai.xingka.api.model.SecretHeader;
import com.ruihai.xingka.utils.AppUtility;
import com.ruihai.xingka.utils.Security;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by zecker on 16/5/26.
 */
public class SMSApiModule {

    public static final String API_URL = "http://sms.xingka.cc/vcode.asmx/";

    private static final int DEFAULT_TIMEOUT = 5;

    private static final Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .registerTypeAdapter(Date.class, new DateTypeAdapter())
            .setDateFormat("yyyy-MM-dd HH:mm:ss") // 使用 gson coverter，统一日期请求格式
            .create();

    public static Retrofit initRetrofit() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS) // 设置超时时间
                .addInterceptor(interceptor)
                .build();

        return new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient)
                .build();
    }

    /**
     * 创建 RetrofitManage 服务
     *
     * @return SMSApiService
     */
    public static SMSApiService apiService() {
        return initRetrofit().create(SMSApiService.class);
    }

}

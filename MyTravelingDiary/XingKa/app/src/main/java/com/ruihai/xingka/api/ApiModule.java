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
 * Created by zecker on 15/8/11.
 */
public class ApiModule {
    // 正式API
//    public static final String API_URL_1 = "http://api.xingka.cc/Interface.asmx/";
//    public static final String API_URL = "http://api.xingka.cc/Interface_v2.asmx/";
    // 测试API
    public static final String API_URL_1 = "http://192.168.199.254/Interface.asmx/";
    public static final String API_URL = "http://192.168.199.254/Interface_v2.asmx/";
    private static final int DEFAULT_TIMEOUT = 5;

    // 让 Gson 自动将 API 中的下划线全小写式变量名转换成 Java 的小写开头驼峰式
    private static final Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .registerTypeAdapter(Date.class, new DateTypeAdapter())
            .create();
//    private static final RestAdapter restAdapte1 = new RestAdapter.Builder()
//            .setEndpoint(API_URL)
//            .setConverter(new GsonConverter(gson))
//            .setRequestInterceptor(new RequestInterceptor() {
//                @Override
//                public void intercept(RequestFacade request) {
//                    String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
//                    String gUid = AppUtility.generateUUID();
//                    SecretHeader header = new SecretHeader(date, gUid);
//                    Gson gson = new Gson();
//                    String jsonValue = gson.toJson(header);
//                    request.addHeader("secretKey", Security.aesEncrypt1(jsonValue));
//                    request.addHeader("versionCode", "1.1");
//                }
//            })
//            .setLogLevel(RestAdapter.LogLevel.FULL)
//            .build();
//
//    private static final RestAdapter restAdapter = new RestAdapter.Builder()
//            .setEndpoint(API_URL_1)
//            .setConverter(new GsonConverter(gson))
//            .setLogLevel(RestAdapter.LogLevel.FULL)
//            .build();


    //   public static XKApiService apiService() {
    //     return restAdapter.create(XKApiService.class);
//    }
//
//    public static XKApiService apiService_1() {
//        return restAdapte1.create(XKApiService.class);
//    }


    public static Retrofit initRetrofit() {
        OkHttpClient httpClient = new OkHttpClient();

//        if (BuildConfig.DEBUG) {
//            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
//            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
//            httpClient = new OkHttpClient.Builder().addInterceptor(logging).build();
//        }
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();//使用 gson coverter，统一日期请求格式
        return new Retrofit.Builder()
                .baseUrl(API_URL_1)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient)
                .build();
    }

    public static Retrofit initRetrofit_1() {
        OkHttpClient httpClient = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                // Customize the request header
                String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                String gUid = AppUtility.generateUUID();
                SecretHeader header = new SecretHeader(date, gUid);
                Gson gson = new Gson();
                String jsonValue = gson.toJson(header);

                Request request = original.newBuilder()
                        //.header("Accept", "application/json")
                        //.header("Authorization", "auth-token")
                        .header("secretKey", Security.aesEncrypt1(jsonValue))
                        .header("versionCode", "1.1")
                        .method(original.method(), original.body())
                        .build();

                Response response = chain.proceed(request);

                // Customize or return the response
                return response;
            }
        })
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS) // 设置超时时间
                .build();
//
//        if (BuildConfig.DEBUG) {
//            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
//            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
//            httpClient = new OkHttpClient.Builder().addInterceptor(logging).build();
//        }

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();//使用 gson coverter，统一日期请求格式
        return new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient)
                .build();
    }

    /**
     * 创建 RetrofitManage 服务
     *
     * @return ApiService
     */
    public static XKApiService apiService() {
        return initRetrofit().create(XKApiService.class);
    }

    public static XKApiService apiService_1() {
        return initRetrofit_1().create(XKApiService.class);
    }

}

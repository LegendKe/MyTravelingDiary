package com.ruihai.xingka.api;

import com.ruihai.xingka.api.model.SMSStatus;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by zecker on 16/5/26.
 */
public interface SMSApiService {

    /**
     * 发送短信验证码
     * @param zone  国家码,如:+86
     * @param phoneNum 手机号码
     * @param version 版本号,请使用最新版本（不加密）
     * @return
     */
    @FormUrlEncoded
    @POST("Send")
    Call<SMSStatus> send(
            @Field("zone") String zone,
            @Field("phoneNum") String phoneNum,
            @Field("version") String version
    );


    /**
     * 验证手机短信验证码
     * @param phoneNum 手机号码
     * @param code 已经接受到的验证码
     * @param version 版本号，请使用最新版本 （不加密）
     * @return
     */
    @FormUrlEncoded
    @POST("Verification")
    Call<SMSStatus> verification(
            @Field("phoneNum") String phoneNum,
            @Field("code") String code,
            @Field("version") String version
    );

}

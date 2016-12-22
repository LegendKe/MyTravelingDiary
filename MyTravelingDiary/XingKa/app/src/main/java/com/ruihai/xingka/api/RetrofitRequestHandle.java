package com.ruihai.xingka.api;

import com.shizhefei.mvc.RequestHandle;

import retrofit2.Call;


public class RetrofitRequestHandle implements RequestHandle {

    private final Call call;

    public RetrofitRequestHandle(Call call) {
        super();
        this.call = call;
    }

    @Override
    public void cancle() {
        call.cancel();
    }

    @Override
    public boolean isRunning() {
        return false;
    }
}
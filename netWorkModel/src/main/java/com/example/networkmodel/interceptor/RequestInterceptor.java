package com.example.networkmodel.interceptor;


import androidx.annotation.NonNull;

import com.example.networkmodel.INetworkRequiredInfo;
import com.example.networkmodel.utils.DateUtil;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 请求拦截器
 */
public class RequestInterceptor implements Interceptor {
    /**
     * 网络请求信息
     */
    private final INetworkRequiredInfo mINetworkRequiredInfo;

    public RequestInterceptor(INetworkRequiredInfo iNetworkRequiredInfo) {
        this.mINetworkRequiredInfo = iNetworkRequiredInfo;
    }

    /**
     * 拦截
     */
    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        String nowDateTime = DateUtil.getNowDateTime();
        //构建器
        Request.Builder builder = chain.request().newBuilder();
        //添加使用环境
        builder.addHeader("os", "android");
        //添加版本号
        builder.addHeader("appVersionCode", this.mINetworkRequiredInfo.getVersionCode());
        //添加版本名
        builder.addHeader("appVersionName", this.mINetworkRequiredInfo.getVersionName());
        //添加日期时间
        builder.addHeader("datetime", nowDateTime);
        //返回
        return chain.proceed(builder.build());
    }
}

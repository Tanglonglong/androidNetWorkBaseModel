package com.example.networkbasemodel.api;


import com.example.networkbasemodel.bean.NewResponse;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * ApiService接口 统一管理应用所有的接口
 */
public interface ApiService {
    @POST("toutiao/index")
    @FormUrlEncoded
    Observable<NewResponse> getNews(@FieldMap Map<String, String> params);

}

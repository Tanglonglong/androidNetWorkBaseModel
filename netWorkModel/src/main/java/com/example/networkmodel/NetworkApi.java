package com.example.networkmodel;


import com.example.networkmodel.interceptor.ResponseInterceptor;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 网络API
 */
public class NetworkApi {

    //获取APP运行状态及版本信息，用于日志打印
    private static INetworkRequiredInfo sINetworkRequiredInfo;
    //OkHttp客户端
    private static OkHttpClient sOkHttpClient;
    //sRetrofitHashMap
    private static HashMap<String, Retrofit> sRetrofitHashMap = new HashMap<>();

    public static void init(INetworkRequiredInfo networkRequiredInfo) {
        sINetworkRequiredInfo = networkRequiredInfo;
    }

    public static <T> T createService(Class<T> serviceClass) {
        return getRetrofit(serviceClass).create(serviceClass);
    }

/**
 *
 * retrofit直接实现网络请求
 *
 * **/
//    public static void getJUHENews() {
//        Retrofit.Builder retrofitBuilder = new Retrofit.Builder();
//        retrofitBuilder.baseUrl(sINetworkRequiredInfo.getBaseUrl());
//        retrofitBuilder.client(getOkHttpClient());
//        retrofitBuilder.addConverterFactory(GsonConverterFactory.create());
//        Retrofit retrofit = retrofitBuilder.build();
//        NewsApiInterface apiInterface = retrofit.create(NewsApiInterface.class);
//
//
//        HashMap<String, String> map = new HashMap<>();
//        map.put("key", apiKey);
//        map.put("type", "");
//        map.put("page", "");
//        map.put("page_size", "");
//        map.put("is_filter", "");
//        Call<NewsResponse> newsChannels = apiInterface.getNews(map.get("key"),
//                map.get("type"), map.get("page"),
//                map.get("page_size"), map.get("is_filter"));
//
//        newsChannels.enqueue(new Callback() {
//            @Override
//            public void onResponse(Call call, Response response) {
//
//                Log.e(TAG, response.body().toString());
//            }
//
//            @Override
//            public void onFailure(Call call, Throwable t) {
//
//                Log.e(TAG, "Failure:" + t.getMessage());
//            }
//        });
//
//
//    }


    /**
     * 配置OkHttp
     *
     * @return OkHttpClient
     */
    private static OkHttpClient getOkHttpClient() {
        if (sOkHttpClient == null) {
            //OkHttp构建器
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            //设置缓存大小
            int cacheSize = 100 * 1024 * 1024;
            //设置OkHttp网络缓存
            builder.cache(new Cache(sINetworkRequiredInfo.getApplication().getCacheDir(), cacheSize));
            //设置网络请求超时时长，这里设置为6s
            builder.connectTimeout(6, TimeUnit.SECONDS);
            //添加请求拦截器，如果接口有请求头的话，可以放在这个拦截器里面
            builder.addInterceptor(new RequestInterceptor(sINetworkRequiredInfo));
            //添加返回拦截器，可用于查看接口的请求耗时，对于网络优化有帮助
            builder.addInterceptor(new ResponseInterceptor());
            //当程序在debug过程中则打印数据日志，方便调试用。
            if (sINetworkRequiredInfo != null && sINetworkRequiredInfo.isDebug()) {
                //sINetworkRequiredInfo不为空且处于debug状态下则初始化日志拦截器
                HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
                //设置要打印日志的内容等级，BODY为主要内容，还有BASIC、HEADERS、NONE。
                System.out.println("接口请求日志开始------------");
                //httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
                httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                //将拦截器添加到OkHttp构建器中
                builder.addInterceptor(httpLoggingInterceptor);
            }
            //OkHttp配置完成
            sOkHttpClient = builder.build();
        }
        return sOkHttpClient;
    }

    /**
     * 配置Retrofit
     *
     * @param serviceClass 服务类
     * @return Retrofit
     */
    private static Retrofit getRetrofit(Class serviceClass) {
        if (sRetrofitHashMap.get(sINetworkRequiredInfo.getBaseUrl() + serviceClass.getName()) != null) {
            //刚才上面定义的Map中键是String，值是Retrofit，当键不为空时，必然有值，有值则直接返回。
            return sRetrofitHashMap.get(sINetworkRequiredInfo.getBaseUrl() + serviceClass.getName());
        }
        //初始化Retrofit  Retrofit是对OKHttp的封装，通常是对网络请求做处理，也可以处理返回数据。
        //Retrofit构建器
        Retrofit.Builder builder = new Retrofit.Builder();
        //设置访问地址
        builder.baseUrl(sINetworkRequiredInfo.getBaseUrl());
        //设置OkHttp代理
        builder.client(getOkHttpClient());
        //设置数据解析器
        builder.addConverterFactory(GsonConverterFactory.create());
        //设置请求回调，使用RxJava 对网络返回进行处理
        builder.addCallAdapterFactory(RxJava2CallAdapterFactory.create());
        //retrofit配置完成
        Retrofit retrofit = builder.build();
        //放入Map中
        sRetrofitHashMap.put(sINetworkRequiredInfo.getBaseUrl() + serviceClass.getName(), retrofit);
        //最后返回即可
        return retrofit;
    }

    /**
     * 配置RxJava 完成线程的切换
     */
    public static <T> ObservableTransformer<T, T> applySchedulers() {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                return upstream.subscribeOn(Schedulers.io())//线程订阅
                        .observeOn(AndroidSchedulers.mainThread());//观察Android主线程

            }
        };
    }

    public static <T> ObservableTransformer<T, T> applySchedulers1() {
        return observable -> observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}

package com.example.networkbasemodel;

import android.app.Application;

import com.example.networkmodel.NetworkApi;

import static com.example.networkbasemodel.NetWorkConfig.JUHE_BASE_URL;


public class MyApplication extends Application {

    public NetworkRequiredInfo mNetworkRequiredInfo;

    @Override
    public void onCreate() {
        super.onCreate();
        mNetworkRequiredInfo = new NetworkRequiredInfo.Builder(this)
                .baseUrl(JUHE_BASE_URL)
                .build();
        NetworkApi.init(mNetworkRequiredInfo);
    }
}

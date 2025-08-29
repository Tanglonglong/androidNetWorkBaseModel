package com.example.networkmodel;

import android.app.Application;

/**
 * 接口请求一些公共基本数据
 */
public interface INetworkRequiredInfo {
    String getBaseUrl();

    String getVersionName();

    String getVersionCode();

    boolean isDebug();

    Application getApplication();
}

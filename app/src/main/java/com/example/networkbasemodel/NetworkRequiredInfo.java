package com.example.networkbasemodel;

import android.app.Application;
import android.os.Build;

import com.example.networkmodel.INetworkRequiredInfo;


/**
 * 网络请求基本数据封装，用建造者模式
 */
public class NetworkRequiredInfo implements INetworkRequiredInfo {
    private String baseUrl;
    private final String versionName = BuildConfig.VERSION_NAME;
    private final String versionCode = String.valueOf(BuildConfig.VERSION_CODE);
    private final boolean isDebug = BuildConfig.DEBUG;
    private Application application;


    private NetworkRequiredInfo(Builder build) {
        this.baseUrl = build.baseUrl;
        this.application = build.application;
    }

    public static class Builder {
        private String baseUrl;
        private Application application;

        public Builder(Application application) {
            this.application = application;
        }

        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder application(Application application) {
            this.application = application;
            return this;
        }

        public NetworkRequiredInfo build() {
            return new NetworkRequiredInfo(this);
        }
    }

    public String getVersionName() {
        return versionName;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public Application getApplication() {
        return application;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public boolean isDebug() {
        return isDebug;
    }
}

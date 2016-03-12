package com.majun.earthquake;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

/**
 * Created by ws02 on 2016/1/29.
 */

//使用application初始化
public class StartApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(this);
    }
}

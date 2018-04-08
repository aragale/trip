package com.example.yuze.bysjdemo;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

/**
 * 添加一个全局的初始化变量
 * <p>
 * Created by yuze on 2018/4/8.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(this);
    }
}

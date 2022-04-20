package com.liucj.jetpack_base;

import android.app.Application;

import com.liucj.lib_network.ApiService;

public class BaseApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ApiService.init("http://123.56.232.18:8080/serverdemo", null);

    }
}

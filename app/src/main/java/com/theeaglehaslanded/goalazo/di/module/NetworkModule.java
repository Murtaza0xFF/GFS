package com.theeaglehaslanded.goalazo.di.module;

import android.app.Application;

import com.squareup.okhttp.OkHttpClient;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;


@Module
public class NetworkModule {

    Application application;
    int cacheSize = 100 * 1024 * 1024;

    public NetworkModule(Application application){
        this.application  = application;
    }

    @Singleton
    @Provides
    public OkHttpClient provideOkHttpClient(){
        //        File cacheDirectory = application.getCacheDir();
//        Cache cache = new Cache(cacheDirectory, cacheSize);
//        okHttpClient.setCache(cache);
        return new OkHttpClient();
    }
}

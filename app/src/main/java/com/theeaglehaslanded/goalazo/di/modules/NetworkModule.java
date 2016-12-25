package com.theeaglehaslanded.goalazo.di.modules;

import android.content.Context;

import com.squareup.okhttp.OkHttpClient;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;


@Module
public class NetworkModule {

    Context context;

    public NetworkModule(Context context){
        this.context  = context;
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

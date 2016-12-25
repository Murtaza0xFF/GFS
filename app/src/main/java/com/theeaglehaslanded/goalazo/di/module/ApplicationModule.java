package com.theeaglehaslanded.goalazo.di.module;

import android.app.Application;

import dagger.Module;

@Module
public class ApplicationModule {
    private Application mApp;

    public ApplicationModule(Application app) {
        mApp = app;
    }


}

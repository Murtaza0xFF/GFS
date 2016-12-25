package com.theeaglehaslanded.goalazo.di.modules;

import android.app.Application;

import dagger.Module;
import dagger.Provides;

@Module
public class GFSApplicationModule {
    private Application mApp;

    public GFSApplicationModule(Application app) {
        mApp = app;
    }

    @Provides
    Application providesApplication(){
        return mApp;
    }

}

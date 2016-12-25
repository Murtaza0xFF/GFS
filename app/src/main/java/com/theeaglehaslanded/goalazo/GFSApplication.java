package com.theeaglehaslanded.goalazo;

import android.app.Application;
import android.content.Context;

import com.theeaglehaslanded.goalazo.di.components.DaggerGFSApplicationComponent;
import com.theeaglehaslanded.goalazo.di.components.GFSApplicationComponent;
import com.theeaglehaslanded.goalazo.di.modules.GFSApplicationModule;

import javax.inject.Inject;

/**
 * Created on 5/10/15.
 */
public class GFSApplication extends android.app.Application {

    @Inject
    Application application;
    private GFSApplicationComponent mComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        mComponent = DaggerGFSApplicationComponent.builder()
            .gFSApplicationModule(new GFSApplicationModule(this))
            .build();
        mComponent.inject(this);
    }


    public GFSApplicationComponent getComponent() {
        return mComponent;
    }

    public static GFSApplication getApplication(Context context) {
        return (GFSApplication) context.getApplicationContext();
    }

}

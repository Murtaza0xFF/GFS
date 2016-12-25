package com.theeaglehaslanded.goalazo;

import android.content.Context;

import com.theeaglehaslanded.goalazo.di.components.ApplicationComponent;
import com.theeaglehaslanded.goalazo.di.components.DaggerApplicationComponent;
import com.theeaglehaslanded.goalazo.di.module.ApplicationModule;

/**
 * Created on 5/10/15.
 */
public class Application extends android.app.Application {

    private ApplicationComponent mComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        mComponent = DaggerApplicationComponent.builder()
            .applicationModule(new ApplicationModule(this))
            .build();
    }

    public ApplicationComponent getComponent() {
        return mComponent;
    }

    public static Application getApplication(Context context) {
        return (Application) context.getApplicationContext();
    }

}

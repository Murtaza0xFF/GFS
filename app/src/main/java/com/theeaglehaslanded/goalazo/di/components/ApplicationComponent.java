package com.theeaglehaslanded.goalazo.di.components;

import android.app.Application;

import javax.inject.Singleton;

import com.theeaglehaslanded.goalazo.di.module.ApplicationModule;
import dagger.Component;

@Singleton
@Component(modules = {ApplicationModule.class})
public interface ApplicationComponent {
    void inject(Application app);
}

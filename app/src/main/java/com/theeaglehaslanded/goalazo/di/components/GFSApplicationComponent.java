package com.theeaglehaslanded.goalazo.di.components;

import com.theeaglehaslanded.goalazo.GFSApplication;
import com.theeaglehaslanded.goalazo.di.modules.GFSApplicationModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {GFSApplicationModule.class})
public interface GFSApplicationComponent {
    void inject(GFSApplication app);
}

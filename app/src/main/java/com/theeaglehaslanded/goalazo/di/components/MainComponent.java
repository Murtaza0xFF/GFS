package com.theeaglehaslanded.goalazo.di.components;

import com.theeaglehaslanded.goalazo.di.modules.MainModule;
import com.theeaglehaslanded.goalazo.presenters.MainPresenterImpl;
import com.theeaglehaslanded.goalazo.view.MainActivity;

import javax.inject.Singleton;

import dagger.Component;


@Singleton
@Component(modules = {MainModule.class})
public interface MainComponent {
    void inject(MainActivity mainActivity);
    void inject(MainPresenterImpl mainPresenter);
}

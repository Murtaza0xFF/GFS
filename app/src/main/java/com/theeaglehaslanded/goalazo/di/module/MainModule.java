package com.theeaglehaslanded.goalazo.di.module;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.theeaglehaslanded.goalazo.network.request.RetrieveSubmissionListing;
import com.theeaglehaslanded.goalazo.presenters.MainPresenterImpl;
import com.theeaglehaslanded.goalazo.view.MainView;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by murtaza on 6/10/15.
 */

@Module
public class MainModule {

    private MainView mainView;
    private Context context;

    public MainModule(MainView mainView, Context context){
        this.mainView = mainView;
        this.context = context;
    }

    @Provides
    public MainPresenterImpl providesMainPresenterIpl(){
        return new MainPresenterImpl(mainView, context);
    }

    @Provides
    @Singleton
    SharedPreferences providePreference(){
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Provides
    RetrieveSubmissionListing provideRetrieveSubmissionListing(){
        return new RetrieveSubmissionListing(mainView, context);
    }


}

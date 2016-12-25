package com.theeaglehaslanded.goalazo.di.components;

import com.theeaglehaslanded.goalazo.di.modules.NetworkModule;
import com.theeaglehaslanded.goalazo.network.request.DownloadStream;
import com.theeaglehaslanded.goalazo.network.request.RetrieveSubmissionListing;

import javax.inject.Singleton;

import dagger.Component;


@Singleton
@Component(modules = {NetworkModule.class})
public interface NetworkComponent {
    void inject(RetrieveSubmissionListing retrieveSubmissionListing);
    void inject(DownloadStream downloadStream);
}

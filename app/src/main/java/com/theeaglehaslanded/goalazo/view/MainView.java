package com.theeaglehaslanded.goalazo.view;

import android.support.v7.widget.RecyclerView;

import com.theeaglehaslanded.goalazo.di.components.MainComponent;

public interface MainView {



    MainComponent getMainComponent();

    void startRefresh();

    void showRefreshLayout();

    void hideRefreshLayout();

    void setAdapterForRecyclerView(RecyclerView.Adapter adapter);

    void startVideoFragment(String Url);

    void stopProgressBarBottom();

    void redditError();

}

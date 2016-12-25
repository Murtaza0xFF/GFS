package com.theeaglehaslanded.goalazo.presenters;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import com.squareup.okhttp.Response;
import com.theeaglehaslanded.goalazo.model.AccessToken;
import com.theeaglehaslanded.goalazo.model.RedditJson;
import com.theeaglehaslanded.goalazo.network.oauth.UserlessAuthorization;
import com.theeaglehaslanded.goalazo.network.request.RetrieveSubmissionListing;
import com.theeaglehaslanded.goalazo.parse.ParseJson;
import com.theeaglehaslanded.goalazo.view.MainView;
import com.theeaglehaslanded.goalazo.view.Submissions;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


public class MainPresenterImpl implements MainPresenter {

    String jsonObject;
    @Inject
    RetrieveSubmissionListing retrieveSubmissionListing;
    @Inject
    SharedPreferences sharedPreferences;
    ParseJson parseJson;
    Subscription responseSubscription;
    Subscription tokenSubscription;
    Subscription dbSubscription;
    Subscription parseJsonSubscription;
    List<RedditJson> redditJson;
    Submissions submissions;
    private MainView mainView;
    private Context context;
    public static final String PARCEL_KEY = "PARCELED REDDITJSON";
    boolean refresh;

    public MainPresenterImpl(MainView mainView, Context context) {
        this.mainView = mainView;
        this.context = context;
        mainView.getMainComponent().inject(this);
        parseJson = new ParseJson();
    }

    public void initiateAuthorization() {
        try {
            UserlessAuthorization userlessAuthorization = new UserlessAuthorization(context);
            tokenSubscription = userlessAuthorization.getAccessToken().subscribeOn(Schedulers.io()).
                    observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<String>() {
                        @Override
                        public void onCompleted() {
                            setAccessToken();
                        }

                        @Override
                        public void onError(Throwable e) {
                        }

                        @Override
                        public void onNext(String s) {
                            jsonObject = s;
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void initiateNetworkOperations(boolean refresh) {
        this.refresh = refresh;
        if (getDBSize() == 0) {
            initiateAuthorization();
        } else {
            getData("0", "0", null, false);
        }
    }


    void getData(String count, String after, final ArrayList<RedditJson> redditOldJson, final boolean loadMore) {
        responseSubscription = retrieveSubmissionListing.getResponse(count, after)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(new Subscriber<Response>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Response response) {
                        feedToParser(response, redditOldJson, loadMore);
                    }
                });
    }

    public void jsonParser(JSONObject s, ArrayList<RedditJson> redditOldJson, final boolean loadMore) {
        parseJsonSubscription = parseJson.parseJson(s, redditOldJson)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(new Subscriber<List<RedditJson>>() {
                    @Override
                    public void onCompleted() {
                        if(loadMore) {
                            mainView.stopProgressBarBottom();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<RedditJson> redditJsons) {
                        redditJson = redditJsons;
                        if (loadMore || refresh) {
                            submissions.updateDataSet(new ArrayList<RedditJson>(redditJson));
                        } else {
                            Log.d("MainActivity", Integer.toString(redditJson.size()));
                            submissions = new Submissions(mainView, context, redditJson);
                            mainView.setAdapterForRecyclerView(submissions);
                        }
                        mainView.hideRefreshLayout();
                    }
                });
    }

    void feedToParser(Response response, final ArrayList<RedditJson> redditOldJson, final boolean loadMore){

        if (response.isSuccessful()) {
            try {
                retrieveSubmissionListing.retrieveJson(response)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<JSONObject>() {
                            @Override
                            public void call(JSONObject s) {
                                jsonParser(s, redditOldJson, loadMore);
                            }
                        });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if(response.code() == 503){
            mainView.redditError();
            mainView.hideRefreshLayout();
        }
        else {
            initiateAuthorization();
        }
    }

    public void loadMore() {
        int size = redditJson.size();
        getData(Integer.toString(size), redditJson.get(size - 1).getName(),
                new ArrayList<RedditJson>(redditJson), true);
    }

    int getDBSize() {
        Realm realm = Realm.getInstance(context);
        RealmResults<AccessToken> results = realm
                .where(AccessToken.class)
                .findAll();
        return results.size();
    }

    public void setAccessToken() {
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                Realm realm = Realm.getInstance(context);
                realm.beginTransaction();
                realm.clear(AccessToken.class);
                AccessToken accessToken = realm.createObject(AccessToken.class);
                accessToken.setAccessToken(jsonObject);
                realm.commitTransaction();
                subscriber.onCompleted();
            }
        }).subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
                getData("0", "0", null, false);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(String s) {

            }
        });
    }

    public void onSaveInstanceState(Bundle outState){
        if(redditJson!=null){
            outState.putParcelableArrayList(PARCEL_KEY, new ArrayList<Parcelable>(redditJson));
        }
    }

    public void onRestoreInstanceState(Bundle savedInstanceState){
        redditJson = savedInstanceState.getParcelableArrayList(PARCEL_KEY);
        if(redditJson!=null) {
            submissions = new Submissions(mainView, context, redditJson);
            mainView.setAdapterForRecyclerView(submissions);
        }else{
            mainView.startRefresh();
        }
    }

    public void onDestroy() {
        if (responseSubscription != null) {
            responseSubscription.unsubscribe();
        }
        if (tokenSubscription != null) {
            tokenSubscription.unsubscribe();
        }
        if (dbSubscription != null) {
            dbSubscription.unsubscribe();
        }
        if (parseJsonSubscription != null) {
            parseJsonSubscription.unsubscribe();
        }
        sharedPreferences.edit().remove("pageNo").apply();
    }
}
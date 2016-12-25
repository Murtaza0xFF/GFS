package com.theeaglehaslanded.goalazo.network.request;

import android.content.Context;

import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.theeaglehaslanded.goalazo.di.components.DaggerNetworkComponent;
import com.theeaglehaslanded.goalazo.di.components.NetworkComponent;
import com.theeaglehaslanded.goalazo.di.modules.NetworkModule;
import com.theeaglehaslanded.goalazo.model.AccessToken;
import com.theeaglehaslanded.goalazo.utils.Credentials;
import com.theeaglehaslanded.goalazo.view.MainView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observable;
import rx.Subscriber;

public class RetrieveSubmissionListing {

    String userAgent = Credentials.userAgent;
    String clientID = Credentials.clientID;
    String redirectURI = "https://localhost:3000";
    Request request;
    StringBuilder content = null;
    String jsonObject;
    Context context;
    MainView mainView;
    Response response;
    static NetworkComponent networkComponent;

    @Inject
    OkHttpClient client;


    public RetrieveSubmissionListing(MainView mainView, Context context) {
        this.context = context;
        this.mainView = mainView;
        initNetworkComponent(context);
        networkComponent.inject(this);
        response = null;
    }

    public Observable<Response> getResponse(final String count, final String after) {

        return Observable.create(new Observable.OnSubscribe<Response>() {
            @Override
            public void call(Subscriber<? super Response> subscriber) {

                String accessToken = retrieveAccessToken();

                HttpUrl httpUrl = new HttpUrl.Builder()
                        .scheme("https")
                        .host("oauth.reddit.com")
                        .addPathSegment("r")
                        .addPathSegment(Credentials.subreddit)
                        .addPathSegment("new")
                        .addQueryParameter("count", count)
                        .addQueryParameter("after", after)
                        .addQueryParameter("Uniq", String.valueOf(System.currentTimeMillis()/1000))
                        .build();

                request = new Request.Builder()
                        .url(httpUrl)
                        .header("User-Agent", userAgent)
                        .header("Authorization", "Bearer" + " " + accessToken)
                        //.cacheControl(CacheControl.FORCE_NETWORK)
                        .build();
                try {
                    response = client.newCall(request).execute();
                    subscriber.onNext(response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public Observable<JSONObject> retrieveJson(Response res) throws IOException {
        response = res;
        return Observable.create(new Observable.OnSubscribe<JSONObject>() {
            @Override
            public void call(Subscriber<? super JSONObject> subscriber) {
                BufferedReader br = null;
                try {
                    br = new BufferedReader(new InputStreamReader(response.body().byteStream()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                StringBuilder content = new StringBuilder();
                String line;
                assert br != null;
                try {
                    while ((line = br.readLine()) != null) {
                        content.append(line);
                    }
                    JSONObject jsonObject = new JSONObject(content.toString());
                    subscriber.onNext(jsonObject);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });
    }


    String retrieveAccessToken() {
        Realm realm = Realm.getInstance(context);
        RealmResults<AccessToken> results = realm
                .where(AccessToken.class)
                .findAll();
        return results.first().getAccessToken();
    }

    static void initNetworkComponent(Context context){
            networkComponent = DaggerNetworkComponent.builder()
                    .networkModule(new NetworkModule(context))
                    .build();

    }

    public static NetworkComponent getNetworkComponent(Context context){
        if(networkComponent!=null) {
            return networkComponent;
        }else{
            initNetworkComponent(context);
            return networkComponent;
        }
    }
}

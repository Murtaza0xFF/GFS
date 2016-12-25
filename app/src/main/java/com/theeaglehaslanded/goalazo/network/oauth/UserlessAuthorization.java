package com.theeaglehaslanded.goalazo.network.oauth;

import android.content.Context;

import com.squareup.okhttp.Authenticator;
import com.squareup.okhttp.Credentials;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Proxy;

import rx.Observable;
import rx.Subscriber;


public class UserlessAuthorization {

    String userAgent = com.theeaglehaslanded.goalazo.utils.Credentials.userAgent;
    String clientID = com.theeaglehaslanded.goalazo.utils.Credentials.clientID;
    String redirectURI = "http://127.0.0.1:65010/authorize_callback";
    OkHttpClient client;
    Request request;
    StringBuilder content = null;
    String jsonObject;
    Context context;


    public UserlessAuthorization(Context context){
        this.context = context;
        client = new OkHttpClient();
    }


    public Observable<String> getAccessToken() throws IOException {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                client.setAuthenticator(new Authenticator() {
                    @Override
                    public Request authenticate(Proxy proxy, Response response) throws IOException {
                        String credential = Credentials.basic(clientID, "");
                        return response.request().newBuilder().header("Authorization", credential).build();
                    }

                    @Override
                    public Request authenticateProxy(Proxy proxy, Response response) throws IOException {
                        return null;
                    }
                });

                RequestBody formBody = new FormEncodingBuilder()
                    .add("grant_type", "https://oauth.reddit.com/grants/installed_client")
                    .add("device_id", "DO_NOT_TRACK_THIS_DEVICE")
                    .build();

                request = new Request.Builder()
                    .url("https://www.reddit.com/api/v1/access_token")
                    .post(formBody)
                    .build();

                try {
                    Response response = client.newCall(request).execute();
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);
                    BufferedReader br = new BufferedReader(new InputStreamReader(response.body().byteStream()));
                    content = new StringBuilder();
                    String line;
                    while (null != (line = br.readLine())) {
                        content.append(line);
                        try {
                            jsonObject = new JSONObject(content.toString()).
                                get("access_token").toString();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    subscriber.onNext(jsonObject);
                    subscriber.onCompleted();
                }catch (IOException e){
                    e.printStackTrace();
                }

            }
        });
    }
}

package com.theeaglehaslanded.goalazo.parse;

import android.support.annotation.Nullable;

import com.theeaglehaslanded.goalazo.model.RedditJson;
import com.theeaglehaslanded.goalazo.utils.Credentials;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rx.Observable;
import rx.Subscriber;

public class ParseJson {

    final private ArrayList<String> allowedDomains = new ArrayList<>
        (Arrays.asList("cdn-east.streamable.com", "streamable.com", "cdn.streamable.com"));
    ArrayList<RedditJson> redditJson;

    public ParseJson(){
        redditJson = new ArrayList<>();
    }

    public ParseJson(ArrayList<RedditJson> redditJson){
        this.redditJson = redditJson;
    }

    @Nullable
    public Observable<List<RedditJson>> parseJson(final JSONObject jsonObject, ArrayList<RedditJson> redditOldJson){
        redditJson.clear();
        if(redditOldJson!=null){
            this.redditJson = redditOldJson;
        }
        return Observable.create(new Observable.OnSubscribe<List<RedditJson>>() {
            @Override
            public void call(Subscriber<? super List<RedditJson>> subscriber) {
                try {
                    JSONArray jMod = jsonObject
                        .getJSONObject("data")
                        .getJSONArray("children");
                    int jsonSize = jMod
                        .length();
                    for(int i = 0; i<jsonSize; i++){
                        String domain = jMod
                            .getJSONObject(i)
                            .getJSONObject("data")
                            .get("domain")
                            .toString();

                        if(allowedDomains.contains(domain) || true){

                            String url = jMod
                                .getJSONObject(i)
                                .getJSONObject("data")
                                .get("url")
                                .toString();

                            String thumbnailUrl;
                            try {
                                thumbnailUrl = jMod
                                    .getJSONObject(i)
                                    .getJSONObject("data")
                                    .getJSONObject("media")
                                    .getJSONObject("oembed")
                                    .get("thumbnail_url")
                                    .toString();
                            }catch (JSONException e){
                                thumbnailUrl = "";
                            }


                            String title = jMod
                                .getJSONObject(i)
                                .getJSONObject("data")
                                .get("title")
                                .toString();

                            String modTitle = Credentials.modTitle(title);

                            String name = jMod
                                .getJSONObject(i)
                                .getJSONObject("data")
                                .get("name")
                                .toString();


                            redditJson.add(new RedditJson());
                            redditJson.get(redditJson.size()-1).setDomain(domain);
                            redditJson.get(redditJson.size()-1).setThumbnailURL(thumbnailUrl);
                            redditJson.get(redditJson.size()-1).setTitle(modTitle);
                            redditJson.get(redditJson.size()-1).setURL(url);
                            redditJson.get(redditJson.size()-1).setName(name);
                        }
                    }

                    subscriber.onNext(redditJson);
                    subscriber.onCompleted();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }
}

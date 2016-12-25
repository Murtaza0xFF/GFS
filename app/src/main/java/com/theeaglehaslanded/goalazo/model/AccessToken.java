package com.theeaglehaslanded.goalazo.model;

import io.realm.RealmObject;

/**
 * Created by murtaza on 4/10/15.
 */
public class AccessToken extends RealmObject {

    private String accessToken;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

}

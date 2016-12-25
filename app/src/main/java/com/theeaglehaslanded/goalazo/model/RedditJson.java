package com.theeaglehaslanded.goalazo.model;

import android.os.Parcel;
import android.os.Parcelable;

public class RedditJson implements Parcelable {


    private String domain;
    private String URL;
    private String title;
    private String thumbnailURL;
    private String name;

    public RedditJson() {
    }

    public RedditJson(Parcel input) {
        domain = input.readString();
        URL = input.readString();
        title = input.readString();
        thumbnailURL = input.readString();
        name = input.readString();
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(domain);
        dest.writeString(URL);
        dest.writeString(title);
        dest.writeString(thumbnailURL);
        dest.writeString(name);
    }

    public static final Parcelable.Creator<RedditJson> CREATOR = new Parcelable.Creator<RedditJson>() {
        public RedditJson createFromParcel(Parcel in) {
            return new RedditJson(in);
        }

        public RedditJson[] newArray(int size) {
            return new RedditJson[size];
        }
    };
}

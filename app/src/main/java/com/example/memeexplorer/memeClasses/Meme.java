package com.example.memeexplorer.memeClasses;

import java.util.UUID;

public class Meme {
    private UUID mId;
    private String mLocation;
    private String mTag;

    public Meme(String location, String tag) {
        mLocation = location;
        mTag = tag;
        mId = UUID.randomUUID();
    }
    public Meme(UUID id) {
        mId = id;
    }

    public UUID getId() {
        return mId;
    }

    public void setId(UUID id) {
        mId = id;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String location) {
        mLocation = location;
    }

    public String getTag() {
        return mTag;
    }

    public void setTag(String tag) {
        mTag = tag;
    }
}

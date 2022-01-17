package com.example.memeexplorer.memeClasses;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.UUID;
@Entity
public class Meme {
    @PrimaryKey
    private UUID mId;

    @ColumnInfo(name = "location")
    private String mLocation;

    @ColumnInfo(name = "tag")
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

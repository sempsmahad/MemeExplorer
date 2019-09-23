package com.example.memeexplorer.db;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.memeexplorer.memeClasses.Meme;

import java.util.UUID;

public class MemeCursorWrapper extends CursorWrapper {
    public MemeCursorWrapper(Cursor cursor) {
        super(cursor);
    }
    public Meme getMeme(){
        String uuidString = getString(getColumnIndex(MemeExplorerDatabaseContract.MemeInfoEntry.COLUMN_MEME_ID));
        String location = getString(getColumnIndex(MemeExplorerDatabaseContract.MemeInfoEntry.COLUMN_MEME_LOCATION));
        String tag = getString(getColumnIndex(MemeExplorerDatabaseContract.MemeInfoEntry.COLUMN_MEME_TAG));
        Meme meme = new Meme(UUID.fromString(uuidString));
        meme.setLocation(location);
        meme.setTag(tag);
        return meme;
    }
}

package com.example.memeexplorer.db;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Jim.
 */

public class DatabaseDataWorker {
    private SQLiteDatabase mDb;

    public DatabaseDataWorker(SQLiteDatabase db) {
        mDb = db;
    }

    public void insertMeme(String memeId, String location, String tag) {
        ContentValues values = new ContentValues();
        values.put(MemeExplorerDatabaseContract.MemeInfoEntry.COLUMN_MEME_ID, memeId);
        values.put(MemeExplorerDatabaseContract.MemeInfoEntry.COLUMN_MEME_LOCATION, location);
        values.put(MemeExplorerDatabaseContract.MemeInfoEntry.COLUMN_MEME_TAG, tag);

        long newRowId = mDb.insert(MemeExplorerDatabaseContract.MemeInfoEntry.TABLE_NAME, null, values);
    }

}

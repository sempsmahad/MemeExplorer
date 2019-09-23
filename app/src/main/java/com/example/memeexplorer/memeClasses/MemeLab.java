package com.example.memeexplorer.memeClasses;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.memeexplorer.db.MemeCursorWrapper;
import com.example.memeexplorer.db.MemeExplorerOpenHelper;

import java.util.ArrayList;
import java.util.List;

import static com.example.memeexplorer.db.MemeExplorerDatabaseContract.MemeInfoEntry.*;


public class MemeLab {
    private static MemeLab sMemeLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static MemeLab get(Context context){
        if (sMemeLab == null) {
            sMemeLab = new MemeLab(context);
        }
        return sMemeLab;
    }
    private MemeLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new MemeExplorerOpenHelper(mContext)
                .getReadableDatabase();
    }
    public void addMeme(Meme m) {
        ContentValues values = getContentValues(m);
        mDatabase.insert(TABLE_NAME, null, values);
    }
    public Meme getMeme(String location) {
        MemeCursorWrapper cursor = queryMemes(
                COLUMN_MEME_LOCATION + " = ?",
                new String[]{location}
        );
        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getMeme();
        } finally {
            cursor.close();
        }
    }
    public List<Meme> getMemes(String query) {
        List<Meme> memes = new ArrayList<>();
        MemeCursorWrapper cursor = queryMemes(COLUMN_MEME_TAG + " like ?", new String[]{"%" + query + "%"});
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                memes.add(cursor.getMeme());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return memes;
    }
    public List<Meme> getMemes() {
        List<Meme> memes = new ArrayList<>();
        MemeCursorWrapper cursor = queryMemes(null, null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                memes.add(cursor.getMeme());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return memes;
    }
    private MemeCursorWrapper queryMemes(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                TABLE_NAME,
                null, // Columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                 "_ID DESC"  // orderBy
        );
        return new MemeCursorWrapper(cursor);
    }
    private static ContentValues getContentValues(Meme meme) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_MEME_ID, meme.getId().toString());
        values.put(COLUMN_MEME_LOCATION, meme.getLocation());
        values.put(COLUMN_MEME_TAG, meme.getTag());
        return values;
    }

}

package com.example.memeexplorer.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MemeExplorerOpenHelper extends SQLiteOpenHelper {
    public  static final String DATABASE_NAME = "MemeExplorer.db";
    public static final  int DATABASE_VERSION = 1;
    public MemeExplorerOpenHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(MemeExplorerDatabaseContract.MemeInfoEntry.SQL_CREATE_TABLE);
        sqLiteDatabase.execSQL(MemeExplorerDatabaseContract.MemeInfoEntry.SQL_CREATE_INDEX);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}

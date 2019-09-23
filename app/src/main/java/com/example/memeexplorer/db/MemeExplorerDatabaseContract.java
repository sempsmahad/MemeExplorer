package com.example.memeexplorer.db;


public final class MemeExplorerDatabaseContract {
    public MemeExplorerDatabaseContract() {
    }

    public static final class MemeInfoEntry {
        public static final String TABLE_NAME = "meme_info";
        public static final String COLUMN_MEME_ID = "meme_id";
        public static final String COLUMN_MEME_LOCATION = "meme_location";
        public static final String COLUMN_MEME_TAG = "meme_tag";

        //CREATE TABLE meme_info (meme_id,meme_location,meme_tag)
        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + "( _ID INTEGER PRIMARY KEY, " +
                        COLUMN_MEME_ID + " TEXT UNIQUE NOT NULL, "+
                        COLUMN_MEME_LOCATION + " TEXT UNIQUE NOT NULL, " +
                        COLUMN_MEME_TAG + " TEXT" + ")";
//        CREATE INDEX T_a_ci ON T(a COLLATE NOCASE);
        public static final String SQL_CREATE_INDEX =
                "CREATE INDEX " + TABLE_NAME + "_"+
                        COLUMN_MEME_TAG+"_ci ON " +
                        TABLE_NAME+"("+
                        COLUMN_MEME_TAG+" COLLATE NOCASE)";
    }
}

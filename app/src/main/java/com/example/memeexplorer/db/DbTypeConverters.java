package com.example.memeexplorer.db;

import androidx.room.TypeConverter;

import java.util.UUID;

public class DbTypeConverters {
    @TypeConverter
    private UUID toUUID(String uuid){
        return UUID.fromString(uuid);
    }

    @TypeConverter
    private String fromUUID(UUID uuid){
        return uuid.toString();
    }

}

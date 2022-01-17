package com.example.memeexplorer.utilities;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.os.ResultReceiver;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.memeexplorer.activities.DebugActivity;
import com.google.android.gms.vision.Frame;

import java.io.File;
import java.net.URLConnection;
import java.util.ArrayList;

public class TranslatorService extends IntentService {
    private static final String TAG = "TranslatorService";
    int progress ;

    public TranslatorService() {
        super(TAG);
    }

    public static Intent newIntent (Context context){
        return new Intent(context,TranslatorService.class);
    }
    @SuppressLint("RestrictedApi")
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        ResultReceiver receiver = intent.getParcelableExtra("receiver");
        ArrayList<String> pathsArray = new ArrayListSaverInterface(getApplicationContext()).getPathsArray();
        if (pathsArray != null){
            Log.d(TAG, "onHandleIntent: "+ pathsArray.get(0));
        for (int i = 0; i < pathsArray.size(); i++) {
            progress = (i*100)/pathsArray.size();
            Bundle staf = new Bundle();
            staf.putInt("progress", progress);
            receiver.send(Constants.NEW_PROGRESS, staf);
            Log.i("wired", String.valueOf(pathsArray.size()));
            final String filepath = pathsArray.get(i);
            Frame outputFrame;

            File file = new File(filepath);
            if(file.exists() && isImageFile(filepath)){
                Bitmap btm = DebugActivity.convertPathToBitmap(filepath);
                if (btm != null){
                outputFrame = new Frame.Builder().setBitmap(btm).build();
                DebugActivity.detectText(getApplicationContext(), outputFrame, filepath);
                }
            }
        }
        }
    }
    public static boolean isImageFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("image");
    }


}

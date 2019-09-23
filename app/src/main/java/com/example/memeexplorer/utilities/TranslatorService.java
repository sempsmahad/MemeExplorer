package com.example.memeexplorer.utilities;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.os.ResultReceiver;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.memeexplorer.activities.MainActivity;
import com.google.android.gms.vision.Frame;

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
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        ResultReceiver receiver = intent.getParcelableExtra("receiver");
        ArrayList<String> pathsArray = new ArrayListSaverInterface(getApplicationContext()).getPathsArray();
        for (int i = 0; i < pathsArray.size(); i++) {
            progress = (i*100)/pathsArray.size();
            Bundle staf = new Bundle();
            staf.putInt("progress", progress);
            receiver.send(Constants.NEW_PROGRESS, staf);
            Log.i("wired", String.valueOf(pathsArray.size()));
            final String filepath = pathsArray.get(i);
            Frame outputFrame;
            outputFrame = new Frame.Builder().setBitmap(MainActivity.convertPathToBitmap(filepath)).build();
            MainActivity.detectText(getApplicationContext(), outputFrame, filepath);
        }
    }


}

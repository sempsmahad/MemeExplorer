package com.example.memeexplorer;

import com.example.memeexplorer.utilities.AdController;

public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AdController.initAd(this);

    }
}

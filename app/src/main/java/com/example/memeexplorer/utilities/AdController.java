package com.example.memeexplorer.utilities;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.Display;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.memeexplorer.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class AdController {
    public static int adCounter = 1;
    public static int adDisplayCounter = 4;


    public static void initAd(Context context) {
//        MobileAds.setRequestConfiguration(new RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("ABCDEF0123456")).build());
        MobileAds.initialize(context, initializationStatus -> {
        });
    }

    static AdView gadView;
    public static void loadBannerAd(Context context, AdView adView) {
        gadView = adView;
//        gadView.setAdUnitId(context.getString(R.string.admob_test));
//        adContainer.addView(gadView);
        loadBanner(context);
    }

    static void loadBanner(Context context) {
        AdRequest adRequest =
                new AdRequest.Builder().build();

//        AdSize adSize = getAdSize((Activity) context);
//        gadView.setAdSize(adSize);
        if (AppConfig.ADS_MAIN_BANNER && NetworkCheck.isConnect(context.getApplicationContext())) {

            gadView.loadAd(adRequest);}

    }
    static void loadBanner(Context context, LinearLayout ad_container) {
//         if (AppConfig.ADS_MAIN_BANNER && NetworkCheck.isConnect(context.getApplicationContext())) {
        AdView mAdView = new AdView(context);
        mAdView.setAdSize(AdSize.BANNER);
        mAdView.setAdUnitId(context.getString(R.string.admob_banner_id));

        ad_container.removeAllViews();
        ad_container.addView(mAdView);

        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
//        }
    }

    static AdSize getAdSize(Activity context) {
        Display display = context.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth);
    }

    public static void largeBannerAd(Context context, LinearLayout adContainer) {
        AdView adView = new AdView(context);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.setAdSize(AdSize.LARGE_BANNER);
        adView.setAdUnitId(context.getString(R.string.admob_banner_id));
        adView.loadAd(adRequest);
        adContainer.addView(adView);
    }

    static InterstitialAd mInterstitialAd;

    public static void loadInterAd(Context context) {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(context,context.getString(R.string.admob_interstitial), adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                mInterstitialAd = interstitialAd;
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                mInterstitialAd = null;
            }
        });
    }

    public static void showInterAd(final Activity context, final Intent intent, final int requstCode) {
        if (adCounter == adDisplayCounter && mInterstitialAd != null && NetworkCheck.isConnect(context.getApplicationContext())) {
            adCounter = 1;
            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                @Override
                public void onAdDismissedFullScreenContent() {
                    // Called when fullscreen content is dismissed.
//                    Log.d("TAG", "The ad was dismissed.");
                    loadInterAd(context);
                    startActivity(context, intent, requstCode);
                }

                @Override
                public void onAdFailedToShowFullScreenContent(com.google.android.gms.ads.AdError adError) {
//                     Called when fullscreen content failed to show.
//                    Log.d("TAG", "The ad failed to show.");
                }


                @Override
                public void onAdShowedFullScreenContent() {
                    // Called when fullscreen content is shown.
                    // Make sure to set your reference to null so you don't
                    // show it a second time.
                    mInterstitialAd = null;
//                    Log.d("TAG", "The ad was shown.");
                }
            });
            mInterstitialAd.show((Activity) context);
        } else {
            if (adCounter == adDisplayCounter){
                adCounter = 1;
            }
            startActivity(context, intent, requstCode);
        }
    }

    static void startActivity(Activity context, Intent intent, int requestCode) {
        if (intent != null) {
            context.startActivityForResult(intent, requestCode);
        }
    }

    public static void showInterAd(final Fragment context, final Intent intent, final int requstCode) {
        if (adCounter == adDisplayCounter && mInterstitialAd != null) {
            adCounter = 1;
            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                @Override
                public void onAdDismissedFullScreenContent() {
                    // Called when fullscreen content is dismissed.
//                    Log.d("TAG", "The ad was dismissed.");
                    loadInterAd(context.getActivity());
                    startActivity(context, intent, requstCode);
                }

                @Override
                public void onAdFailedToShowFullScreenContent(com.google.android.gms.ads.AdError adError) {
//                     Called when fullscreen content failed to show.
//                    Log.d("TAG", "The ad failed to show.");
                }


                @Override
                public void onAdShowedFullScreenContent() {
                    // Called when fullscreen content is shown.
                    // Make sure to set your reference to null so you don't
                    // show it a second time.
                    mInterstitialAd = null;
//                    Log.d("TAG", "The ad was shown.");
                }
            });
            mInterstitialAd.show(context.getActivity());
        } else {
            if (adCounter == adDisplayCounter){
                adCounter = 1;
            }
            startActivity(context, intent, requstCode);
        }
    }

    static void startActivity(Fragment context, Intent intent, int requestCode) {
        if (intent != null) {
            context.startActivityForResult(intent, requestCode);
        }
    }

}


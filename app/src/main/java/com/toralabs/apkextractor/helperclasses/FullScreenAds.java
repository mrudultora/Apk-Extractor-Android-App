package com.toralabs.apkextractor.helperclasses;

import android.content.Context;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.toralabs.apkextractor.R;

import static com.facebook.ads.CacheFlag.ALL;

public class FullScreenAds {
    private Context context;
    InterstitialAd interstitialAd;

    public FullScreenAds(Context context) {
        this.context = context;
    }

    public void showFullScreenAd() {
        interstitialAd = new InterstitialAd(context.getApplicationContext(), "");
        InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {

            }

            @Override
            public void onInterstitialDismissed(Ad ad) {

            }

            @Override
            public void onError(Ad ad, AdError adError) {
            }

            @Override
            public void onAdLoaded(Ad ad) {
                if(interstitialAd.isAdLoaded()) {
                    interstitialAd.show();
                }
            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        };
        interstitialAd.loadAd(interstitialAd.buildLoadAdConfig().withAdListener(interstitialAdListener).withCacheFlags(ALL).build());
    }
}

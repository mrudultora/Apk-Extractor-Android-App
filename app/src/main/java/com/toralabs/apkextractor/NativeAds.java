package com.toralabs.apkextractor;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdIconView;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;

import java.util.ArrayList;
import java.util.List;

class NativeAds extends Activity {
    NativeAdLayout nativeAdLayout;
    LinearLayout ad;
    NativeAd nativeAd;
    Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nativeAd = new NativeAd(NativeAds.this,"CAROUSEL_IMG_SQUARE_LINK#310221940080933_314859456283848");
        nativeAd.setAdListener(new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {
            }

            @Override
            public void onError(Ad ad, AdError adError) {
            }

            @Override
            public void onAdLoaded(Ad ad) {
                if (nativeAd == null || nativeAd != ad) {
                    return;
                }
                inflateAd(nativeAd);
            }

            @Override
            public void onAdClicked(Ad ad) {
            }

            @Override
            public void onLoggingImpression(Ad ad) {
            }
        });
        nativeAd.loadAd();
    }
    public NativeAds(Context context){
        this.context=context;
    }
    private void inflateAd(NativeAd nativeAd) {
        nativeAd.unregisterView();
        nativeAdLayout =findViewById(R.id.native_ad_container);
        nativeAdLayout.setVisibility(View.VISIBLE);
        LayoutInflater inflater = LayoutInflater.from(NativeAds.this);
        ad = (LinearLayout) inflater.inflate(R.layout.nativead_layout, nativeAdLayout, false);
        nativeAdLayout.addView(ad);
        LinearLayout adChoicesContainer = findViewById(R.id.ad_choices_container);
        AdOptionsView adOptionsView = new AdOptionsView(NativeAds.this, nativeAd, nativeAdLayout);
        adChoicesContainer.removeAllViews();
        adChoicesContainer.addView(adOptionsView, 0);
        AdIconView nativeAdIcon = ad.findViewById(R.id.native_ad_icon);
        TextView nativeAdTitle = ad.findViewById(R.id.native_ad_title);
        MediaView nativeAdMedia = ad.findViewById(R.id.native_ad_media);
        TextView nativeAdSocialContext = ad.findViewById(R.id.native_ad_social_context);
        TextView nativeAdBody = ad.findViewById(R.id.native_ad_body);
        TextView sponsoredLabel = ad.findViewById(R.id.native_ad_sponsored_label);
        Button nativeAdCallToAction = ad.findViewById(R.id.native_ad_call_to_action);
        nativeAdTitle.setText(nativeAd.getAdvertiserName());
        nativeAdBody.setText(nativeAd.getAdBodyText());
        nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
        nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
        sponsoredLabel.setText(nativeAd.getSponsoredTranslation());
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdCallToAction);
        nativeAd.registerViewForInteraction(
                ad,
                nativeAdMedia,
                nativeAdIcon,
                clickableViews);
    }
}

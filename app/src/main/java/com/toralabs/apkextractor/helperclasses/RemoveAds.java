package com.toralabs.apkextractor.helperclasses;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.toralabs.apkextractor.R;

import java.util.ArrayList;
import java.util.List;

public class RemoveAds implements PurchasesUpdatedListener {
    private Context context;
    BillingClient billingClient;
    Preferences preferences;
    View view;
    CustomSnackBar customSnackBar;
    List<String> skuList = new ArrayList<>();
    private String sku = "remove_ads";

    public RemoveAds(Context context, View view) {
        this.context = context;
        preferences = new Preferences(context);
        skuList.add(sku);
        this.view = view;
        customSnackBar = new CustomSnackBar(context, view);
    }

    public void setupbillingclient() {
        billingClient = BillingClient.newBuilder(context).enablePendingPurchases().setListener(this).build();
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {

                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    loadAllSku();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {

            }
        });
    }

    public void loadAllSku() {
        if (billingClient.isReady()) {
            final SkuDetailsParams params = SkuDetailsParams.newBuilder()
                    .setSkusList(skuList)
                    .setType(BillingClient.SkuType.INAPP).build();

            billingClient.querySkuDetailsAsync(params, new SkuDetailsResponseListener() {
                @Override
                public void onSkuDetailsResponse(@NonNull BillingResult billingResult, @Nullable List<SkuDetails> list) {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        for (int i = 0; i < list.size(); i++) {
                            final SkuDetails skuDetails = (SkuDetails) list.get(i);
                            if (skuDetails.getSku().equals(sku)) {
                                boolean isOwned = false;
                                Purchase.PurchasesResult result = billingClient.queryPurchases(BillingClient.SkuType.INAPP);
                                List<Purchase> purchases = result.getPurchasesList();
                                for (int j = 0; j < purchases.size(); j++) {
                                    if (purchases.get(j).getSku().equals(sku)) {
                                        isOwned = true;
                                        break;
                                    }
                                }
                                if (!isOwned) {
                                    BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                                            .setSkuDetails(skuDetails)
                                            .build();
                                    billingClient.launchBillingFlow((Activity) context, billingFlowParams);
                                } else {
                                    preferences.setPurchasePref(true);
                                    customSnackBar.showSnackBar(context.getResources().getString(R.string.premium_user));
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {

        int responseCode = billingResult.getResponseCode();
        if (responseCode == BillingClient.BillingResponseCode.OK && list != null) {
            handlePurchase(list);
        } else if (responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            customSnackBar.showSnackBar(context.getResources().getString(R.string.premium_user));
            preferences.setPurchasePref(true);
        }
    }

    public void handlePurchase(List<Purchase> list) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getSku().equals(sku)) {
                if (list.get(i).getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                    preferences.setPurchasePref(true);
                    if (!list.get(i).isAcknowledged()) {
                        AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams
                                .newBuilder()
                                .setPurchaseToken(list.get(i).getPurchaseToken())
                                .build();
                        billingClient.acknowledgePurchase(acknowledgePurchaseParams, new AcknowledgePurchaseResponseListener() {
                            @Override
                            public void onAcknowledgePurchaseResponse(@NonNull BillingResult billingResult) {
                                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                                }
                            }
                        });
                    }
                    customSnackBar.showSnackBar(context.getResources().getString(R.string.purchase_done));
                }
            }
        }
    }
}

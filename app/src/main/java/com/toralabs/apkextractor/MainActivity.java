package com.toralabs.apkextractor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PurchasesUpdatedListener {

    ArrayList<AppListModel> list = new ArrayList<>();
    List<ApplicationInfo> packagelist = new ArrayList<>();
    AppListAdapter appListAdapter;
    RecyclerView recyclerView;
    Drawable icon;
    String name, packagename, size;
    TextView txt_load, txt_copyright;
    ProgressBar progressBar;
    EditText et_search;
    ImageView img_search, img_filter, img_more, img_eye;
    File file;
    MenuItem menuItem_mode, menuItem_removeads;
    int flag;
    long longsize;
    BillingClient billingClient;
    List<String> skuList = new ArrayList<>();
    private String sku = "remove_ads";   // important for in-app purchase
    boolean bool;
    LottieAnimationView lottie;
    Preferences preferences;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = new Preferences(MainActivity.this);
        boolean b = preferences.getMode();
        if (b) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);
        txt_load = findViewById(R.id.txt_load);
        progressBar = findViewById(R.id.progressbar);
        et_search = findViewById(R.id.et_search);
        img_search = findViewById(R.id.img_search);
        img_filter = findViewById(R.id.img_filter);
        img_more = findViewById(R.id.img_more);
        img_eye = findViewById(R.id.img_eye);
        lottie = findViewById(R.id.lottie);
        txt_copyright = findViewById(R.id.txt_copyright);

        PackageManager packageManager = getPackageManager();
        packagelist = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        img_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupmore(img_more);
            }
        });
        img_eye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showpopeye(img_eye);
            }
        });
        img_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showpopfilter(img_filter);
            }
        });
        NewThread newThread = new NewThread();
        newThread.start();
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    filter(s.toString());
                }
            }
        });
        invalidateOptionsMenu();
        bool = preferences.getSharedPref();
    }

    public void filter(String s) {
        ArrayList<AppListModel> arrayList = new ArrayList<>();
        if (list.size() == packagelist.size()) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getName().toLowerCase().contains(s.toLowerCase())) {
                    arrayList.add(list.get(i));
                    lottie.setVisibility(View.GONE);
                }
            }
            appListAdapter.filteredList(arrayList);
            if (arrayList.size() == 0) {
                lottie.setVisibility(View.VISIBLE);
            }
        } else {
            Toast.makeText(getApplicationContext(), "Please Wait...", Toast.LENGTH_SHORT).show();
        }
    }

    public void sortSystemApps() {
        ArrayList<AppListModel> systemList = new ArrayList<>();
        if (list.size() == packagelist.size()) {
            for (int i = 0; i < packagelist.size(); i++) {
                if (list.get(i).getFlag() == 0) {
                    systemList.add(list.get(i));
                }
            }
            appListAdapter.filteredList(systemList);
        } else {
            Toast.makeText(getApplicationContext(), "Please Wait...", Toast.LENGTH_SHORT).show();
        }
    }

    public void installedApps() {
        ArrayList<AppListModel> installedList = new ArrayList<>();
        if (list.size() == packagelist.size()) {
            for (int i = 0; i < packagelist.size(); i++) {
                if (list.get(i).getFlag() == 1) {
                    installedList.add(list.get(i));
                }
            }
            appListAdapter.filteredList(installedList);

        } else {
            Toast.makeText(getApplicationContext(), "Please Wait...", Toast.LENGTH_SHORT).show();
        }
    }

    public void allApps() {
        if (list.size() == packagelist.size()) {
            appListAdapter.filteredList(list);
        }
    }

    public void showpopfilter(View view) {
        final PopupMenu popupMenu = new PopupMenu(MainActivity.this, view);
        popupMenu.inflate(R.menu.filter_menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.sortalpha1:
                        if (list.size() == packagelist.size()) {
                            Collections.sort(list, new Comparator<AppListModel>() {
                                @Override
                                public int compare(AppListModel o1, AppListModel o2) {
                                    return o1.getName().compareToIgnoreCase(o2.getName());
                                }
                            });
                            appListAdapter.filteredList(list);
                        } else {
                            Toast.makeText(getApplicationContext(), "Please Wait...", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    case R.id.sortalpha2:
                        if (list.size() == packagelist.size()) {
                            Collections.sort(list, new Comparator<AppListModel>() {
                                @Override
                                public int compare(AppListModel o1, AppListModel o2) {
                                    return o2.getName().compareToIgnoreCase(o1.getName());
                                }
                            });
                            appListAdapter.filteredList(list);
                        } else {
                            Toast.makeText(getApplicationContext(), "Please Wait...", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    case R.id.sortsize1:
                        if (list.size() == packagelist.size()) {
                            Collections.sort(list, new Comparator<AppListModel>() {
                                @Override
                                public int compare(AppListModel o1, AppListModel o2) {
                                    return Integer.valueOf((int) o1.getLongsize()).compareTo((int) o2.getLongsize());
                                }
                            });
                            appListAdapter.filteredList(list);
                        } else {
                            Toast.makeText(getApplicationContext(), "Please Wait...", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    case R.id.sortsize2:
                        if (list.size() == packagelist.size()) {
                            Collections.sort(list, new Comparator<AppListModel>() {
                                @Override
                                public int compare(AppListModel o1, AppListModel o2) {
                                    return Integer.valueOf((int) o2.getLongsize()).compareTo((int) o1.getLongsize());
                                }
                            });
                            appListAdapter.filteredList(list);
                        } else {
                            Toast.makeText(getApplicationContext(), "Please Wait...", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
    }

    public void showpopeye(View view) {
        final PopupMenu popupMenu = new PopupMenu(MainActivity.this, view);
        popupMenu.inflate(R.menu.eye_menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.allapps:
                        allApps();
                        return true;
                    case R.id.systemapps:
                        sortSystemApps();
                        return true;
                    case R.id.installed:
                        installedApps();
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
    }

    public void showPopupmore(View view) {
        final PopupMenu popupMenu = new PopupMenu(MainActivity.this, view);
        popupMenu.inflate(R.menu.menu1);
        menuItem_mode = popupMenu.getMenu().findItem(R.id.mode);
        menuItem_removeads = popupMenu.getMenu().findItem(R.id.removeads);
        if (!preferences.getMode()) {
            menuItem_mode.setTitle(getResources().getString(R.string.darkmode));
        } else {
            menuItem_mode.setTitle(getResources().getString(R.string.lightmode));
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.rate:
                        try {
                            Intent rateintent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.toralabs.apkextractor"));
                            startActivity(rateintent);
                        } catch (Exception e) {
                            Intent rateintent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.toralabs.apkextractor"));
                            startActivity(rateintent);
                        }
                        return true;
                    case R.id.share:
                        Intent shareintent = new Intent(Intent.ACTION_SEND);
                        shareintent.setType("text/plain");
                        shareintent.putExtra(Intent.EXTRA_SUBJECT, "");
                        shareintent.putExtra(Intent.EXTRA_TEXT, "Extract Apk's with this lightning fast Apk Extractor packed with many features." + "\nhttps://play.google.com/store/apps/details?id=com.toralabs.apkextractor");
                        startActivity(Intent.createChooser(shareintent, "Share via"));
                        return true;
                    case R.id.mode:
                        if (!preferences.getMode()) {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                            getDelegate().applyDayNight();
                            recreate();
                            preferences.setMode(true, 1);
                        } else {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                            getDelegate().applyDayNight();
                            recreate();
                            preferences.setMode(false, 0);
                        }
                        return true;
                    case R.id.removeads:
                        if (bool) {
                            Toast.makeText(getApplicationContext(), "You are Already a Premium User and Ads are removed from this App.", Toast.LENGTH_LONG).show();
                        } else {
                            skuList.add(sku);
                            setupbillingclient();
                        }
                        return true;
                    case R.id.contact:
                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setType("message/rfc822");
                        i.putExtra(Intent.EXTRA_EMAIL, new String[]{"toralabs24@gmail.com"});
                        i.putExtra(Intent.EXTRA_SUBJECT, "Feedback for Apk Extractor");
                        startActivity(Intent.createChooser(i, "Mail Us:"));
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
    }

    // to use in app purchase ... (just add new item in Play Console -> Store Presence -> In-app product -> Managed Products with id as remove_ads(same as SKU) ).
    public void setupbillingclient() {
        billingClient = BillingClient.newBuilder(this).enablePendingPurchases().setListener(this).build();
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
                                    billingClient.launchBillingFlow(MainActivity.this, billingFlowParams);
                                }
                            } else {
                                preferences.setSharedPref(true);
                                Toast.makeText(getApplicationContext(), "You are already a Premium User. Ads are removed from this app.", Toast.LENGTH_LONG).show();
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
            Toast.makeText(getApplicationContext(), "You Are Already a Premium User. No need to Pay Again...", Toast.LENGTH_LONG).show();
            preferences.setSharedPref(true);
        }
    }

    public void handlePurchase(List<Purchase> list) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getSku().equals(sku)) {
                if (list.get(i).getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                    preferences.setSharedPref(true);
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
                    Toast.makeText(getApplicationContext(), "Purchase Done Successfully...", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    class NewThread extends Thread {
        @Override
        public void run() {
            super.run();
            if (packagelist.size() > 0) {
                for (int i = 0; i < packagelist.size(); i++) {
                    if ((packagelist.get(i).flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                        flag = 1;
                    } else {
                        flag = 0;
                    }
                    icon = packagelist.get(i).loadIcon(getPackageManager());
                    name = packagelist.get(i).loadLabel(getPackageManager()).toString();
                    packagename = packagelist.get(i).packageName;
                    file = new File(packagelist.get(i).publicSourceDir);
                    longsize = file.length();
                    if (longsize > 1024 && longsize <= 1024 * 1024) {
                        size = (longsize / 1024 + " KB");
                    } else if (longsize > 1024 * 1024 && longsize <= 1024 * 1024 * 1024) {
                        size = (longsize / (1024 * 1024) + " MB");
                    } else {
                        size = (longsize / (1024 * 1024 * 1024) + " GB");
                    }
                    list.add(new AppListModel(icon, name, packagename, size, file, flag, longsize));
                    if (i == packagelist.size() - 1) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Collections.sort(list, new Comparator<AppListModel>() {
                                    @Override
                                    public int compare(AppListModel o1, AppListModel o2) {
                                        return o1.getName().compareToIgnoreCase(o2.getName());
                                    }
                                });
                                LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
                                appListAdapter = new AppListAdapter(MainActivity.this, list);
                                recyclerView.setAdapter(appListAdapter);
                                recyclerView.setLayoutManager(layoutManager);
                                recyclerView.setHasFixedSize(true);
                                recyclerView.setVisibility(View.VISIBLE);
                                txt_load.setVisibility(View.GONE);
                                progressBar.setVisibility(View.GONE);
                                txt_copyright.setVisibility(View.GONE);
                            }
                        });
                    }
                }
            }
        }
    }

}

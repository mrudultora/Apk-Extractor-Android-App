package com.toralabs.apkextractor.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;
import com.toralabs.apkextractor.R;
import com.toralabs.apkextractor.helperclasses.AppListAdapter;
import com.toralabs.apkextractor.helperclasses.AppListModel;
import com.toralabs.apkextractor.helperclasses.CustomSnackBar;
import com.toralabs.apkextractor.helperclasses.ExtractDialog;
import com.toralabs.apkextractor.helperclasses.FullScreenAds;
import com.toralabs.apkextractor.helperclasses.NameSaveModel;
import com.toralabs.apkextractor.helperclasses.Preferences;
import com.toralabs.apkextractor.helperclasses.RemoveAds;
import com.toralabs.apkextractor.helperclasses.ThemeConstant;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AppListAdapter.ItemClickListener, ActionMode.Callback, MenuItem.OnActionExpandListener {
    Preferences preferences;
    RemoveAds removeAds;
    androidx.appcompat.widget.SearchView searchView;
    int counter, totalApps, themeNo, flag, position, count = 0;
    RelativeLayout adContainer;
    ThemeConstant themeConstant;
    RecyclerView recycler_apps;
    TextView txt_noapps, txt_loading, txt_copyright;
    ProgressBar progressBar;
    Drawable icon;
    boolean dark, bool, isInActionMode = false, isExtracted = false, selectAll = false, sys = true, toastDisplay = true;
    AppListAdapter appListAdapter;
    String name, packagename, size, version, uid, targetsdk, minsdk = null, permissions = null, vername;
    List<AppListModel> list = new ArrayList<>();
    List<AppListModel> updatedlist;
    List<NameSaveModel> filesList = new ArrayList<>();
    List<ApplicationInfo> packagelist = new ArrayList<>();
    List<NameSaveModel> requestFilesList = new ArrayList<>();
    List<File> newFilesList = new ArrayList<>();
    List<Integer> pos = new ArrayList<>();
    File file;
    ActionMode mode;
    long longsize;
    CustomSnackBar customSnackBar;
    FullScreenAds fullScreenAds;
    String color, fileName, URL_STORE = "https://play.google.com/store/apps/details?id=com.toralabs.apkextractor";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AudienceNetworkAds.initialize(this);
        preferences = new Preferences(MainActivity.this);
        fullScreenAds = new FullScreenAds(MainActivity.this);
        color = preferences.getCircleColor();
        dark = preferences.getMode();
        themeNo = preferences.getThemeNo();
        themeConstant = new ThemeConstant(themeNo);
        if (themeNo == 0) {
            setTheme(R.style.AppTheme);
        } else {
            setTheme(themeConstant.themeChooser());
        }
        setContentView(R.layout.activity_main);
        recycler_apps = findViewById(R.id.recycler_apps);
        adContainer = findViewById(R.id.ad_banner);
        txt_noapps = findViewById(R.id.txt_noapps);
        txt_loading = findViewById(R.id.txt_loading);
        progressBar = findViewById(R.id.progressBar);
        txt_copyright = findViewById(R.id.txt_copyright);
        txt_copyright.setTextColor(Color.parseColor(color));
        txt_loading.setTextColor(Color.parseColor(color));
        txt_noapps.setTextColor(Color.parseColor(color));
        PackageManager packageManager = getPackageManager();
        packagelist = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        totalApps = packagelist.size();
        invalidateOptionsMenu();
        customSnackBar = new CustomSnackBar(MainActivity.this, recycler_apps);
        NewThread newThread = new NewThread();
        newThread.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        searchView = (androidx.appcompat.widget.SearchView) menu.findItem(R.id.search).getActionView();
        if (!sys)
            menu.findItem(R.id.systemapps).setTitle(getResources().getString(R.string.installed));
        else
            menu.findItem(R.id.systemapps).setTitle(getResources().getString(R.string.systemapps));

        menu.findItem(R.id.search).setOnActionExpandListener(this);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!sys)
            menu.findItem(R.id.systemapps).setTitle(getResources().getString(R.string.installed));
        else
            menu.findItem(R.id.systemapps).setTitle(getResources().getString(R.string.systemapps));
        return true;
    }

    public void showPopupMenu(View view, final int position, final List<AppListModel> updatedList) {
        final PopupMenu popupMenu = new PopupMenu(MainActivity.this, view);
        popupMenu.inflate(R.menu.threedot_menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                fileName = appListAdapter.newFileName(updatedList.get(position).getName(), updatedList.get(position).getVersion(), updatedList.get(position).getVername(), updatedList.get(position).getPackageName());
                switch (item.getItemId()) {
                    case R.id.ext:
                        appListAdapter.extract(position, fileName);
                        break;
                    case R.id.appinfo:
                        try {
                            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(Uri.parse("package:" + updatedList.get(position).getPackageName()));
                            startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                            startActivity(intent);
                        }
                        break;
                    case R.id.saveicon:
                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            saveIcons(position, updatedList);
                        } else {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                        }
                        break;
                    case R.id.viewonplay:
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + updatedList.get(position).getPackageName()));
                            startActivity(intent);
                        } catch (Exception e) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + updatedList.get(position).getPackageName()));
                            startActivity(intent);
                        }
                        break;
                    case R.id.shareapk:
                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            shareApk(position, fileName);
                        } else {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                        }
                        break;
                }
                return true;
            }
        });
        popupMenu.show();
    }

    public void shareApk(int position, String name) {
        appListAdapter.extract(position, name);
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Apk Extractor/" + name + ".apk";
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/vnd.android.package-archive");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(path));
        startActivity(Intent.createChooser(intent, "Share Now"));
    }

    public void saveIcons(int position, List<AppListModel> updatedList) {
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Apk Extractor/");
        if (!dir.exists()) {
            dir.mkdir();
        }
        File iconDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Apk Extractor/", "/App Icons/");
        if (!iconDir.exists()) {
            iconDir.mkdir();
        }
        File newFile = new File((Environment.getExternalStorageDirectory().getAbsolutePath() + "/Apk Extractor/App Icons/"), updatedList.get(position).getName() + ".png");
        Bitmap bitmap = null;
        if (updatedList.get(position).getIcon() instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) updatedList.get(position).getIcon();
            bitmap = bitmapDrawable.getBitmap();
        } else {
            Drawable drawable = updatedList.get(position).getIcon();
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                final Canvas canvas = new Canvas(bitmap);
                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                drawable.draw(canvas);
            }
        }
        try {
            OutputStream outputStream = new FileOutputStream(newFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.savedicon) + " " + updatedList.get(position).getName() + " ✔", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.unablesavedicon) + " " + updatedList.get(position).getName() + " ✘", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.unablesavedicon) + " " + updatedList.get(position).getName() + " ✘", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.rate:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URL_STORE)));
                break;
            case R.id.share:
                Intent i = new Intent(Intent.ACTION_SEND);
                i.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share_msg) + "\n" + URL_STORE);
                i.setType("text/plain");
                startActivity(Intent.createChooser(i, getResources().getString(R.string.share_via)));
                break;
            case R.id.settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                finish();
                break;
            case R.id.contact:
                Intent contact_intent = new Intent(Intent.ACTION_SEND);
                contact_intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"toralabs24@gmail.com"});
                contact_intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback for Apk Extractor App");
                contact_intent.setType("message/rfc822");
                startActivity(Intent.createChooser(contact_intent, getResources().getString(R.string.email_via)));
                break;
            case R.id.removeads:
                if (bool) {
                    customSnackBar.showSnackBar(getResources().getString(R.string.premium_user));
                } else {
                    removeAds = new RemoveAds(MainActivity.this, recycler_apps);
                    removeAds.setupbillingclient();
                }
                break;
            case R.id.systemapps:
                if (sys)
                    sortSystemApps();
                else
                    installedApps();
            case R.id.search:
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        if (!newText.isEmpty()) {
                            filter(newText);
                        }
                        return true;
                    }
                });
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public void filter(String s) {
        ArrayList<AppListModel> arrayList = new ArrayList<>();
        if (list.size() == packagelist.size()) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getName().toLowerCase().contains(s.toLowerCase())) {
                    arrayList.add(list.get(i));
                    txt_noapps.setVisibility(View.GONE);
                }
            }
            appListAdapter.filteredList(arrayList);
            if (arrayList.size() == 0) {
                txt_noapps.setVisibility(View.VISIBLE);
            }
        } else {
            if (toastDisplay) {
                Toast.makeText(getApplicationContext(), "Please Wait...", Toast.LENGTH_SHORT).show();
                toastDisplay = false;
            }
        }
    }

    public void sortSystemApps() {
        ArrayList<AppListModel> systemList = new ArrayList<>();
        if (list.size() == packagelist.size()) {
            sys = false;
            for (int i = 0; i < packagelist.size(); i++) {
                System.out.println("ivalue" + i + "\n");
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
            sys = true;
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
            txt_noapps.setVisibility(View.GONE);
            appListAdapter.filteredList(list);
        }
    }


    @Override
    public void onItemClick(final int position, View view, List<AppListModel> updatedList) {
        this.position = position;
        this.updatedlist = updatedList;
        if (!isInActionMode) {
            if (view.getId() == R.id.imgmore) {
                showPopupMenu(view, position, updatedList);
            } else {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    appListAdapter.onClickItem(position);
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                }
            }
        } else {
            if (updatedList.get(position).isSelected()) {
                updatedList.get(position).setSelected(false);
                if (filesList != null && pos != null) {
                    filesList.remove(filesList.get(pos.indexOf(position)));
                    pos.remove(pos.indexOf(position));
                    counter--;
                }
                setActionTitle(mode, counter);
            } else {
                filesList.add(new NameSaveModel(updatedList.get(position).getName(), updatedList.get(position).getPackageName(), updatedList.get(position).getVersion(), updatedList.get(position).getVername(), updatedList.get(position).getFile()));
                pos.add(position);
                counter++;
                setActionTitle(mode, counter);
                updatedList.get(position).setSelected(true);
            }
            appListAdapter.notifyItemChanged(position, 1);
        }
    }

    @Override
    public void onItemLongClick(int position, View view, List<AppListModel> updatedList) {
        if (!isInActionMode) {
            this.updatedlist = updatedList;
            counter++;
            startActionMode(MainActivity.this);
            setActionTitle(mode, counter);
            updatedList.get(position).setSelected(true);
            filesList.add(new NameSaveModel(updatedList.get(position).getName(), updatedList.get(position).getPackageName(), updatedList.get(position).getVersion(), updatedList.get(position).getVername(), updatedList.get(position).getFile()));
            pos.add(position);
            isInActionMode = true;
            appListAdapter.setIsInAction(true);
            appListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        this.mode = mode;
        mode.getMenuInflater().inflate(R.menu.context_menu, menu);
        setActionTitle(mode, counter);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.extract:
                if (filesList.size() != 0) {
                    requestFilesList = filesList;
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Apk Extractor/");
                        if (!dir.exists()) {
                            dir.mkdir();
                        }
                        extractFunc(filesList);
                    } else {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 3);
                    }
                }
                mode.finish();
                break;
            case R.id.selectall:
                if (!selectAll) {
                    allApps();
                    appListAdapter.notifyDataSetChanged();
                    this.updatedlist = list;
                    pos.clear();
                    selectAll = true;
                    filesList.clear();
                    for (int i = 0; i < packagelist.size(); i++) {
                        list.get(i).setSelected(true);
                        pos.add(i);
                        filesList.add(new NameSaveModel(list.get(i).getName(), list.get(i).getPackageName(), list.get(i).getVersion(), list.get(i).getVername(), list.get(i).getFile()));
                    }
                    counter=packagelist.size();
                } else {
                    pos.clear();
                    filesList.clear();
                    for (int i = 0; i < packagelist.size(); i++) {
                        list.get(i).setSelected(false);
                    }
                    counter = 0;
                    selectAll = false;
                }
                setActionTitle(mode, counter);
                appListAdapter.notifyDataSetChanged();
                break;
        }
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        isInActionMode = false;
        counter = 0;
        appListAdapter.setIsInAction(false);
        if (selectAll) {
            for (int i = 0; i < packagelist.size(); i++) {
                list.get(i).setSelected(false);
            }
            selectAll = false;
        }
        if (!isExtracted) {
            if (pos != null) {
                for (int i = 0; i < pos.size(); i++) {
                    updatedlist.get(pos.get(i)).setSelected(false);
                    Log.d("possize", " " + pos.size());
                    Log.d("updatedlist.setselected", " " + updatedlist.get(pos.get(i)).getName());
                }
            }
            filesList.clear();
            newFilesList.clear();
            pos.clear();
        }
        Log.d("isextracted", isExtracted + " ");
        appListAdapter.notifyDataSetChanged();
        this.mode = null;
    }

    public void setActionTitle(ActionMode mode, int itemCount) {
        mode.setTitle(itemCount + "/" + packagelist.size());
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        allApps();
        return true;
    }

    class NewThread extends Thread {
        @Override
        public void run() {
            list.clear();
            super.run();
            int i = 0;
            PackageManager manager = getPackageManager();
            if (packagelist.size() > 0) {
                for (ApplicationInfo applicationInfo : packagelist) {
                    i++;
                    if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                        flag = 1;
                    } else {
                        flag = 0;
                    }
                    StringBuilder stringBuilder = new StringBuilder();
                    packagename = applicationInfo.packageName;
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            version = String.valueOf((int) manager.getPackageInfo(packagename, 0).getLongVersionCode());
                        } else {
                            version = String.valueOf(manager.getPackageInfo(packagename, 0).versionCode);
                        }
                        vername = manager.getPackageInfo(packagename, 0).versionName;
                        String[] reqper = manager.getPackageInfo(packagename, PackageManager.GET_PERMISSIONS).requestedPermissions;
                        if (reqper != null) {
                            for (String per : reqper) {
                                stringBuilder.append("\n").append(per);
                            }
                        } else {
                            permissions = null;
                        }
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    permissions = stringBuilder.toString();
                    icon = applicationInfo.loadIcon(getPackageManager());
                    name = applicationInfo.loadLabel(getPackageManager()).toString();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        minsdk = String.valueOf(applicationInfo.minSdkVersion);
                    }
                    targetsdk = String.valueOf(applicationInfo.targetSdkVersion);
                    uid = String.valueOf(applicationInfo.uid);
                    file = new File(applicationInfo.publicSourceDir);
                    longsize = file.length();
                    if (longsize > 1024 && longsize <= 1024 * 1024) {
                        size = (longsize / 1024 + " KB");
                    } else if (longsize > 1024 * 1024 && longsize <= 1024 * 1024 * 1024) {
                        size = (longsize / (1024 * 1024) + " MB");
                    } else {
                        size = (longsize / (1024 * 1024 * 1024) + " GB");
                    }
                    list.add(new AppListModel(icon, name, packagename, file, size, flag, version, targetsdk, minsdk, uid, permissions, vername));
                    if (i == packagelist.size() - 1) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
                                appListAdapter = new AppListAdapter(MainActivity.this, list, Color.parseColor(preferences.getCircleColor()), recycler_apps, MainActivity.this);
                                appListAdapter.notifyDataSetChanged();
                                recycler_apps.setAdapter(appListAdapter);
                                recycler_apps.setLayoutManager(layoutManager);
                                recycler_apps.setHasFixedSize(true);
                                recycler_apps.setVisibility(View.VISIBLE);
                                txt_loading.setVisibility(View.GONE);
                                progressBar.setVisibility(View.GONE);
                                txt_copyright.setVisibility(View.GONE);
                            }
                        });
                    }
                }
            }
        }
    }

    public void extractFunc(final List<NameSaveModel> fList) {
        if (!preferences.getPurchasePref()) {
            fullScreenAds.showFullScreenAd();
        }
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Apk Extractor/");
        if (!dir.exists()) {
            dir.mkdir();
        }
        for (int i = 0; i < fList.size(); i++) {
            String saveName = appListAdapter.newFileName(fList.get(i).getName(), fList.get(i).getVerCode(), fList.get(i).getVerName(), fList.get(i).getPackageName());
            newFilesList.add(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Apk Extractor/", saveName + ".apk"));
        }
        final ExtractDialog extractDialog = new ExtractDialog(MainActivity.this, Color.parseColor(color));
        extractDialog.showDialog(true);
        final Handler h = new Handler();
        final Runnable r = new Runnable() {
            @Override
            public void run() {
                if (count == fList.size()) {
                    String s = getResources().getString(R.string.totalextracted) + " " + fList.size() + " " + getResources().getString(R.string.apps);
                    isExtracted = false;
                    count = 0;
                    h.removeCallbacks(this);
                    Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                    for (int i = 0; i < pos.size(); i++) {
                        updatedlist.get(pos.get(i)).setSelected(false);
                        Log.d("updatedlist.setselected", " " + updatedlist.get(pos.get(i)).getName());
                    }
                    filesList.clear();
                    newFilesList.clear();
                    pos.clear();
                    customSnackBar.showSnackBar(s);
                    extractDialog.showDialog(false);
                } else {
                    try {
                        InputStream inputStream = new FileInputStream(fList.get(count).getFile());
                        OutputStream outputStream = new FileOutputStream(newFilesList.get(count));
                        byte[] buf = new byte[9192];
                        int len;
                        while ((len = inputStream.read(buf)) > 0) {
                            outputStream.write(buf, 0, len);
                        }
                        inputStream.close();
                        outputStream.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    count++;
                    h.post(this);
                }
            }
        };
        h.post(r);
        isExtracted = true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 0:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    appListAdapter.onClickItem(position);
                }
                break;
            case 1:
                saveIcons(position, updatedlist);
                break;
            case 2:
                shareApk(position, fileName);
                break;
            case 3:
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.select_again), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (dark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
package com.toralabs.apkextractor.helperclasses;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class Preferences extends Activity {
    Context context;
    SharedPreferences sharedPreferences;

    public Preferences(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences("PREF", MODE_PRIVATE);
    }

    public boolean getPurchasePref() {
        return sharedPreferences.getBoolean("purchase_state", false);
    }

    public void setPurchasePref(boolean bool) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("purchase_state", bool).apply();
    }

    public boolean getMode() {
        return sharedPreferences.getBoolean("mode", false);
    }

    public void setMode(boolean bool) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("mode", bool).apply();
    }

    public int getThemeNo() {
        return sharedPreferences.getInt("theme", 0);
    }

    public void setThemeNo(int i) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("theme", i).apply();
    }

    public String getCircleColor() {
        return sharedPreferences.getString("circlecolor", "#0063B3");
    }

    public void setCircleColor(String s) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("circlecolor", s).apply();
    }

    public String getPath() {
        return sharedPreferences.getString("path", "/storage/emulated/0/Apk Extractor/");
    }

    public void setPath(String s) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("path", s).apply();
    }

    public boolean getAppName() {
        return sharedPreferences.getBoolean("appname", true);
    }

    public void setAppName(boolean b) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("appname", b).apply();
    }

    public boolean getPkgName() {
        return sharedPreferences.getBoolean("pkgname", false);
    }

    public void setPkgName(boolean b) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("pkgname", b).apply();
    }

    public boolean getVerName() {
        return sharedPreferences.getBoolean("vername", false);
    }

    public void setVerName(boolean b) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("vername", b).apply();
    }

    public boolean getVerCode() {
        return sharedPreferences.getBoolean("vercode", false);
    }

    public void setVerCode(boolean b) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("vercode", b).apply();
    }
    public boolean getSwitchState(){
        return sharedPreferences.getBoolean("switch", true);
    }
    public void setSwitchState(boolean b) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("switch", b).apply();
    }
}
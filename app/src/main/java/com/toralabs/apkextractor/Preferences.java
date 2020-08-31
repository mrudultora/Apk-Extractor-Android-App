package com.toralabs.apkextractor;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

class Preferences extends Activity {
    Context context;
    SharedPreferences sharedPreferences;

    public Preferences(Context context) {
        this.context = context;
        this.sharedPreferences= context.getSharedPreferences("PREF", MODE_PRIVATE);
    }

    public boolean getSharedPref() {
        return sharedPreferences.getBoolean("purchase_state", false);
    }

    public void setSharedPref(boolean bool) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("purchase_state", bool).apply();
    }

    public void setMode(boolean b, int i) {
        SharedPreferences.Editor e = sharedPreferences.edit();
        e.putBoolean("bKey", b).apply();
    }

    public boolean getMode() {
        return sharedPreferences.getBoolean("bKey", false);
    }
}

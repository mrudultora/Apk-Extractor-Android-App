package com.toralabs.apkextractor.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.toralabs.apkextractor.BuildConfig;
import com.toralabs.apkextractor.R;
import com.toralabs.apkextractor.helperclasses.CustomSnackBar;
import com.toralabs.apkextractor.helperclasses.Preferences;
import com.toralabs.apkextractor.helperclasses.RemoveAds;
import com.toralabs.apkextractor.helperclasses.ThemeConstant;

import java.util.ArrayList;

import petrov.kristiyan.colorpicker.ColorPicker;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {
    RelativeLayout rel_main, rel_theme, rel_showext, rel_color, rel_savedpath, rel_convention, rel_rate, rel_removeads, rel_feedback, rel_app_version, rel_translate;
    Preferences preferences;
    RemoveAds removeAds;
    TextView text_themename, text_version, text_path;
    ThemeConstant themeConstant;
    SwitchCompat switchbtn;
    boolean bool;
    int themeNo;
    boolean flag = false;
    CustomSnackBar customSnackBar;
    ArrayList<String> colors = new ArrayList<>();
    String URL_STORE = "https://play.google.com/store/apps/details?id=com.toralabs.apkextractor";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = new Preferences(SettingsActivity.this);
        themeNo = preferences.getThemeNo();
        themeConstant = new ThemeConstant(themeNo);
        if (themeNo == 0) {
            setTheme(R.style.AppTheme);
        } else {
            setTheme(themeConstant.themeChooser());
        }
        Drawable unwrappedDrawable = AppCompatResources.getDrawable(this, R.drawable.circle);
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        if (themeNo != 0) {
            DrawableCompat.setTint(wrappedDrawable, Color.parseColor(preferences.getCircleColor()));
        } else {
            DrawableCompat.setTint(wrappedDrawable, Color.parseColor("#0063B3"));
        }
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setTitle(getResources().getString(R.string.settings));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        colors.add("#f44236");
        colors.add("#ea1e63");
        colors.add("#9d27b2");
        colors.add("#673bb7");
        colors.add("#1029AD");
        colors.add("#0063B3");
        colors.add("#04a8f5");
        colors.add("#00bed2");
        colors.add("#009788");
        colors.add("#00D308");
        colors.add("#ff9700");
        colors.add("#FFC000");
        colors.add("#D2E41D");
        colors.add("#fe5722");
        colors.add("#5E4034");

        rel_color = findViewById(R.id.rel_color);
        rel_convention = findViewById(R.id.rel_convention);
        rel_rate = findViewById(R.id.rel_rate);
        rel_removeads = findViewById(R.id.rel_removeads);
        rel_savedpath = findViewById(R.id.rel_savedpath);
        rel_theme = findViewById(R.id.rel_theme);
        rel_feedback = findViewById(R.id.rel_feedback);
        rel_app_version = findViewById(R.id.rel_app_version);
        text_version = findViewById(R.id.text_version);
        switchbtn = findViewById(R.id.switchbtn);
        text_themename = findViewById(R.id.text_themename);
        text_path = findViewById(R.id.text_path);
        rel_translate = findViewById(R.id.rel_translate);
        rel_showext = findViewById(R.id.rel_showext);
        rel_main = findViewById(R.id.rel_main);

        rel_showext.setOnClickListener(this);
        rel_color.setOnClickListener(this);
        rel_convention.setOnClickListener(this);
        rel_rate.setOnClickListener(this);
        rel_removeads.setOnClickListener(this);
        rel_savedpath.setOnClickListener(this);
        rel_theme.setOnClickListener(this);
        rel_feedback.setOnClickListener(this);
        rel_translate.setOnClickListener(this);
        rel_app_version.setOnClickListener(this);
        bool = preferences.getPurchasePref();
        if (preferences.getMode()) {
            flag = true;
            text_themename.setText(getResources().getString(R.string.dark));
        }
        if (preferences.getSwitchState()) {
            switchbtn.setChecked(true);
        } else {
            switchbtn.setChecked(false);
        }
        switchbtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    switchbtn.setChecked(true);
                    preferences.setSwitchState(true);
                } else {
                    switchbtn.setChecked(false);
                    preferences.setSwitchState(false);
                }
            }
        });
        customSnackBar = new CustomSnackBar(SettingsActivity.this, rel_main);
        text_path.setText(preferences.getPath());
        text_version.setText(BuildConfig.VERSION_NAME);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rel_color:
                showColorDialog();
                break;
            case R.id.rel_showext:
                if (switchbtn.isChecked()) {
                    switchbtn.setChecked(false);
                    preferences.setSwitchState(false);
                } else {
                    switchbtn.setChecked(true);
                    preferences.setSwitchState(true);
                }
                break;
            case R.id.rel_convention:
                showNameDialog();
                break;
            case R.id.rel_theme:
                showDialogBox();
                break;
            case R.id.rel_translate:

                break;
            case R.id.rel_removeads:
                if (bool) {
                    customSnackBar.showSnackBar(getResources().getString(R.string.premium_user));
                } else {
                    removeAds = new RemoveAds(SettingsActivity.this, rel_main);
                    removeAds.setupbillingclient();
                }
                break;
            case R.id.rel_rate:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URL_STORE)));
                break;
            case R.id.rel_feedback:
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback for Apk Extractor App");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"toralabs24@gmail.com"});
                emailIntent.setType("message/rfc822");
                startActivity(Intent.createChooser(emailIntent, "Send Feedback Email"));
                break;
            case R.id.rel_app_version:
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.appversionis) + " " + BuildConfig.VERSION_NAME, Toast.LENGTH_LONG).show();
                break;
        }
    }

    public void showColorDialog() {
        final ColorPicker colorPicker = new ColorPicker(SettingsActivity.this);
        colorPicker.setColors(colors).setColumns(5).setDefaultColorButton(Color.parseColor(preferences.getCircleColor())).setRoundColorButton(true).setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
            @Override
            public void onChooseColor(int position, int color) {
                preferences.setThemeNo(position + 1);
                preferences.setCircleColor(colors.get(position));
                recreate();
            }

            @Override
            public void onCancel() {

            }
        }).show();
    }

    public void showNameDialog() {
        final Dialog dialog = new Dialog(SettingsActivity.this);
        Button btn_cancel, btn_ok;
        RelativeLayout rel_appname, rel_pkgname, rel_vername, rel_vercode;
        final CheckBox check_appname, check_pkgname, check_vername, check_vercode;
        dialog.setContentView(R.layout.dialog_naming);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        btn_cancel = dialog.findViewById(R.id.btn_cancel);
        btn_ok = dialog.findViewById(R.id.btn_ok);
        btn_cancel.setTextColor(Color.parseColor(preferences.getCircleColor()));
        btn_ok.setTextColor(Color.parseColor(preferences.getCircleColor()));
        rel_appname = dialog.findViewById(R.id.rel_appname);
        rel_pkgname = dialog.findViewById(R.id.rel_pkgname);
        rel_vername = dialog.findViewById(R.id.rel_vername);
        rel_vercode = dialog.findViewById(R.id.rel_vercode);
        check_appname = dialog.findViewById(R.id.check_appname);
        check_pkgname = dialog.findViewById(R.id.check_pkgname);
        check_vername = dialog.findViewById(R.id.check_vername);
        check_vercode = dialog.findViewById(R.id.check_vercode);

        rel_appname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check_appname.isChecked())
                    check_appname.setChecked(false);
                else
                    check_appname.setChecked(true);
            }
        });
        rel_pkgname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check_pkgname.isChecked())
                    check_pkgname.setChecked(false);
                else
                    check_pkgname.setChecked(true);
            }
        });
        rel_vercode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check_vercode.isChecked())
                    check_vercode.setChecked(false);
                else
                    check_vercode.setChecked(true);
            }
        });
        rel_vername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check_vername.isChecked())
                    check_vername.setChecked(false);
                else
                    check_vername.setChecked(true);
            }
        });
        if (preferences.getAppName()) {
            check_appname.setChecked(true);
        }
        if (preferences.getVerName()) {
            check_vername.setChecked(true);
        }
        if (preferences.getPkgName()) {
            check_pkgname.setChecked(true);
        }
        if (preferences.getVerCode()) {
            check_vercode.setChecked(true);
        }
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check_appname.isChecked())
                    preferences.setAppName(true);
                else
                    preferences.setAppName(false);
                if (check_pkgname.isChecked())
                    preferences.setPkgName(true);
                else
                    preferences.setPkgName(false);
                if (check_vername.isChecked())
                    preferences.setVerName(true);
                else
                    preferences.setVerName(false);
                if (check_vercode.isChecked())
                    preferences.setVerCode(true);
                else
                    preferences.setVerCode(false);
                if (!(check_appname.isChecked() || check_pkgname.isChecked() || check_vercode.isChecked() || check_vername.isChecked())) {
                    preferences.setAppName(true);
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void showDialogBox() {
        final Dialog dialog = new Dialog(SettingsActivity.this);
        Button btn_cancel;
        RadioButton radio1, radio2;
        dialog.setContentView(R.layout.dialog_mode);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        btn_cancel = dialog.findViewById(R.id.btn_cancel);
        radio1 = dialog.findViewById(R.id.radio1);
        radio2 = dialog.findViewById(R.id.radio2);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        if (flag) {
            radio2.setChecked(true);
        } else {
            radio1.setChecked(true);
        }
        radio1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                preferences.setMode(false);
                recreate();
                dialog.dismiss();

            }
        });
        radio2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                preferences.setMode(true);
                recreate();
                dialog.dismiss();

            }
        });
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (flag) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SettingsActivity.this, MainActivity.class));
        finish();
    }

}
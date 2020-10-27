package com.toralabs.apkextractor.helperclasses;

import android.graphics.drawable.Drawable;

import java.io.File;

public class AppListModel {
    private Drawable icon;
    private String name;
    private String packageName;
    private File file;
    private String size;
    private String version;
    private String targetsdk;
    private String minsdk;
    private String uid;
    private int flag;
    private String vername;
    private String permissions;
    private boolean isSelected=false;

    public AppListModel(Drawable icon, String name, String packageName, File file, String size, int flag, String version, String targetsdk, String minsdk, String uid, String permissions,String vername) {
        this.icon = icon;
        this.name = name;
        this.packageName = packageName;
        this.size = size;
        this.version = version;
        this.targetsdk = targetsdk;
        this.minsdk = minsdk;
        this.uid = uid;
        this.flag = flag;
        this.file = file;
        this.permissions = permissions;
        this.vername=vername;
    }

    public Drawable getIcon() {
        return icon;
    }

    public File getFile() {
        return file;
    }

    public String getName() {
        return name;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getSize() {
        return size;
    }

    public int getFlag() {
        return flag;
    }

    public String getVersion() {
        return version;
    }

    public String getTargetsdk() {
        return targetsdk;
    }

    public String getMinsdk() {
        return minsdk;
    }

    public String getUid() {
        return uid;
    }

    public String getPermissions() {
        return permissions;
    }

    public String getVername() {
        return vername;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

}

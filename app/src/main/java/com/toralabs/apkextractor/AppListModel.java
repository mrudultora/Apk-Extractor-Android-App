package com.toralabs.apkextractor;

import android.graphics.drawable.Drawable;

import java.io.File;

public class AppListModel {
    private Drawable icon;
    private String name;
    private String packagename;
    private String size;
    private File file;
    private int flag;
    private long longsize;

    public AppListModel(Drawable icon, String name, String packagename, String size, File file, int flag, long longsize)  {
        this.icon = icon;
        this.name = name;
        this.packagename = packagename;
        this.size = size;
        this.file = file;
        this.flag = flag;
        this.longsize = longsize;
    }

    public Drawable getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }

    public String getPackagename() {
        return packagename;
    }

    public String getSize() {
        return size;
    }

    public File getFile() {
        return file;
    }

    public int getFlag() {
        return flag;
    }

    public long getLongsize() {
        return  longsize;
    }
}

package com.toralabs.apkextractor.helperclasses;

import java.io.File;

public class NameSaveModel {
    private String name;
    private String packageName;
    private String verCode;
    private String verName;
    private File file;

    public NameSaveModel(String name, String packageName, String verCode, String verName, File file) {
        this.name = name;
        this.packageName = packageName;
        this.verCode = verCode;
        this.verName = verName;
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getVerCode() {
        return verCode;
    }

    public String getVerName() {
        return verName;
    }

    public File getFile() {
        return file;
    }
}

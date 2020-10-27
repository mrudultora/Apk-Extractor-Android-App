package com.toralabs.apkextractor.helperclasses;


import com.toralabs.apkextractor.R;

public class ThemeConstant {
    private int constant;

    public ThemeConstant(int constant) {
        this.constant = constant;
    }

    public int themeChooser() {
        switch (constant) {
            case 1:
                constant = R.style.Theme1;
                break;
            case 2:
                constant = R.style.Theme2;
                break;
            case 3:
                constant = R.style.Theme3;
                break;
            case 4:
                constant = R.style.Theme4;
                break;
            case 5:
                constant = R.style.Theme5;
                break;
            case 6:
                constant = R.style.Theme6;
                break;
            case 7:
                constant = R.style.Theme7;
                break;
            case 8:
                constant = R.style.Theme8;
                break;
            case 9:
                constant = R.style.Theme9;
                break;
            case 10:
                constant = R.style.Theme10;
                break;
            case 11:
                constant = R.style.Theme11;
                break;
            case 12:
                constant = R.style.Theme12;
                break;
            case 13:
                constant = R.style.Theme13;
                break;
            case 14:
                constant = R.style.Theme14;
                break;
            case 15:
                constant = R.style.Theme15;
                break;
        }
        return constant;
    }
}

package com.toralabs.apkextractor.helperclasses;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.toralabs.apkextractor.R;

public class ExtractDialog {
    AlertDialog dialog;
    private Context context;
    private int color;

    public ExtractDialog(Context context, int color) {
        this.context = context;
        this.color = color;
    }

    public void showDialog(boolean visiblity) {
        if (visiblity) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            dialog = builder.create();
            dialog.setCancelable(false);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View customView = inflater.inflate(R.layout.dialog_progress, null, false);
            ProgressBar progressBar = customView.findViewById(R.id.progressBar);
            TextView tv_progress = customView.findViewById(R.id.tv_progress);
            tv_progress.setTextColor(color);
            dialog.setView(customView);
            dialog.show();
        } else {
            dialog.dismiss();
        }
    }
}

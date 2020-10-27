package com.toralabs.apkextractor.helperclasses;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.toralabs.apkextractor.R;
import com.toralabs.apkextractor.activities.MainActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ExtractThread extends Thread {
    private File file, newFile;
    private Context context;
    private String path;
    AlertDialog dialog;
    private int color;
    View view;

    public ExtractThread(Context context, File file, File newFile, String path, int color, View view) {
        this.file = file;
        this.newFile = newFile;
        this.context = context;
        this.path = path;
        this.color = color;
        this.view = view;
    }

    @Override
    public void run() {
        super.run();
        try {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showDialog(true);
                }
            });
            InputStream inputStream = new FileInputStream(file);
            OutputStream outputStream = new FileOutputStream(newFile);
            byte[] buf = new byte[9192];
            int len;
            while ((len = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, len);
            }
            inputStream.close();
            outputStream.close();
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showDialog(false);
                    showSnackBar(true);
                    Toast.makeText(context, context.getResources().getString(R.string.extracted) + " ✔", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (final FileNotFoundException e) {
            ((MainActivity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showDialog(false);
                    showSnackBar(false);
                    Log.d("ExceptionApk", e.getMessage());
                    Toast.makeText(context, context.getResources().getString(R.string.unabletoext) + " ✘", Toast.LENGTH_SHORT).show();
                }
            });
            e.printStackTrace();
        } catch (final IOException e) {
            ((MainActivity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showDialog(false);
                    showSnackBar(false);
                    Log.d("ExceptionApk", e.getMessage());
                    Toast.makeText(context, context.getResources().getString(R.string.unabletoext) + " ✘", Toast.LENGTH_SHORT).show();
                }
            });
            e.printStackTrace();
        }
    }

    public void showSnackBar(boolean saved) {
        Snackbar snackbar = Snackbar.make(view, "", BaseTransientBottomBar.LENGTH_LONG);
        View custom = ((Activity) context).getLayoutInflater().inflate(R.layout.snackbar_layout, null);
        snackbar.getView().setBackgroundColor(Color.TRANSPARENT);
        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
        TextView tv_snack = custom.findViewById(R.id.tv_snack);
        Button btn_open = custom.findViewById(R.id.btn_open);
        if (saved) {
            tv_snack.setText(context.getResources().getString(R.string.apksaved) + " ✔");
        } else {
            tv_snack.setText(context.getResources().getString(R.string.unabletoext) + " ✘");
            btn_open.setVisibility(View.GONE);
        }
        btn_open.setTextColor(color);
        btn_open.setText(context.getResources().getString(R.string.shareapk));
        btn_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("application/vnd.android.package-archive");
                intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(path));
                context.startActivity(Intent.createChooser(intent, "Share Now"));
            }
        });
        snackbarLayout.addView(custom, 0);
        snackbar.show();
    }

    public void showDialog(boolean visiblity) {
        if (visiblity) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            dialog = builder.create();
            dialog.setCancelable(false);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View customView = inflater.inflate(R.layout.dialog_progress, null, false);
            TextView tv_progress = customView.findViewById(R.id.tv_progress);
            tv_progress.setTextColor(color);
            dialog.setView(customView);
            dialog.show();
        } else {
            dialog.dismiss();
        }
    }
}

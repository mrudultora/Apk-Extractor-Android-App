package com.toralabs.apkextractor.helperclasses;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.toralabs.apkextractor.R;

public class CustomSnackBar {
    private Context context;
    private View view;

    public CustomSnackBar(Context context, View view) {
        this.context = context;
        this.view = view;
    }

    public void showSnackBar(String string) {
        Snackbar snackbar = Snackbar.make(view, "", BaseTransientBottomBar.LENGTH_LONG);
        View custom = ((Activity) context).getLayoutInflater().inflate(R.layout.snackbar_layout, null);
        snackbar.getView().setBackgroundColor(Color.TRANSPARENT);
        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
        TextView tv_snack = custom.findViewById(R.id.tv_snack);
        tv_snack.setText(string);
        Button btn_open = custom.findViewById(R.id.btn_open);
        btn_open.setVisibility(View.GONE);
        snackbarLayout.addView(custom, 0);
        snackbar.show();
    }
}

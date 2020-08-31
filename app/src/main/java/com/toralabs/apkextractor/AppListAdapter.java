package com.toralabs.apkextractor;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.Viewholder> {
    private Context context;
    private List<AppListModel> list;
    AlertDialog dialog;

    public AppListAdapter(Context context, List<AppListModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false);
        return new Viewholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final Viewholder holder, final int position) {
        holder.img_icon.setImageDrawable(list.get(position).getIcon());
        holder.name.setText(list.get(position).getName());
        holder.packagename.setText(list.get(position).getPackagename());
        holder.size.setText(list.get(position).getSize());
        holder.rel_main.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
        holder.rel_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                extract(position, holder.rel_main);
            }
        });
        holder.img_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean b = extract(position, holder.rel_main);
                if (b) {
                    String path = pathToStore(1, position);
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("application/vnd.android.package-archive");
                    intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(path));
                    context.startActivity(Intent.createChooser(intent, "Share Now"));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        ImageView img_icon, img_share;
        TextView name, packagename, size;
        RelativeLayout rel_main;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            rel_main = itemView.findViewById(R.id.rel_main);
            img_icon = itemView.findViewById(R.id.img_icon);
            img_share = itemView.findViewById(R.id.img_share);
            name = itemView.findViewById(R.id.name);
            packagename = itemView.findViewById(R.id.packagename);
            size = itemView.findViewById(R.id.size);
        }
    }

    private boolean extract(int position, View v) {
        File file = new File(String.valueOf(list.get(position).getFile()));
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            File dir = new File(pathToStore(0, position));
            if (!dir.exists()) {
                dir.mkdir();
            } else {
                showDialog(true);
                File newfile = new File(pathToStore(1, position));
                ExtractThread extractThread = new ExtractThread(file, newfile);
                extractThread.start();
            }
            return true;
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale((MainActivity) context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Snackbar.make(v, "Need the required permission", Snackbar.LENGTH_INDEFINITE)
                        .setActionTextColor(context.getResources().getColor(R.color.grant)).setAction("Grant", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityCompat.requestPermissions((MainActivity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    }
                }).show();
            } else {
                ActivityCompat.requestPermissions((MainActivity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
            return false;
        }
    }

    public void filteredList(ArrayList<AppListModel> arrayList) {
        list = arrayList;
        notifyDataSetChanged();
    }

    public class ExtractThread extends Thread {
        File file, newfile;

        public ExtractThread(File file, File newfile) {
            this.file = file;
            this.newfile = newfile;
        }

        @Override
        public void run() {
            super.run();
            try {
                InputStream inputStream = new FileInputStream(file);
                OutputStream outputStream = new FileOutputStream(newfile);
                byte[] buf = new byte[1024];
                int len;
                while ((len = inputStream.read(buf)) > 0) {
                    outputStream.write(buf, 0, len);
                }
                inputStream.close();
                outputStream.close();
                ((MainActivity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showDialog(false);
                        Toast.makeText(context, "Apk Extracted Successfully.", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (FileNotFoundException e) {
                ((MainActivity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showDialog(false);
                        Toast.makeText(context, "Unable to extract this Apk.", Toast.LENGTH_SHORT).show();
                    }
                });
                e.printStackTrace();
            } catch (IOException e) {
                ((MainActivity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showDialog(false);
                        Toast.makeText(context, "Unable to extract this Apk.", Toast.LENGTH_SHORT).show();
                    }
                });
                e.printStackTrace();
            }
        }

    }

    // This will work till Android Q (Currently, Android R is not yet released.)
    public String pathToStore(int i, int position) {
        if (i == 0) {
            return Environment.getExternalStorageDirectory().getAbsolutePath() + "/Apk Extractor (ToraLabs)/";
        } else {
            return Environment.getExternalStorageDirectory().getAbsolutePath() + "/Apk Extractor (ToraLabs)/" + list.get(position).getName() + ".apk";
        }
    }

    public void showDialog(boolean visiblity) {
        if (visiblity) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            dialog = builder.create();
            dialog.setCancelable(false);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            dialog.setView(inflater.inflate(R.layout.dialog_progress, null, false));
            dialog.show();
        } else {
            dialog.dismiss();
        }
    }

}

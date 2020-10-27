package com.toralabs.apkextractor.helperclasses;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.ads.InterstitialAd;
import com.toralabs.apkextractor.R;

import java.io.File;
import java.util.List;

public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.Viewholder> {
    private Context context;
    private List<AppListModel> list;
    private int color;
    private View view;
    InterstitialAd interstitialAd;
    private ItemClickListener itemClickListener;
    private Preferences preferences;
    private boolean isInAction = false;

    public AppListAdapter(Context context, List<AppListModel> list, int color, View view, ItemClickListener itemClickListener) {
        this.context = context;
        this.list = list;
        this.color = color;
        this.view = view;
        preferences = new Preferences(context);
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_app_layout, parent, false);
        return new Viewholder(v, itemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position, @NonNull List<Object> payloads) {
        if (!payloads.isEmpty()) {
            if (payloads.get(0) instanceof Integer) {
                if (list.get(position).isSelected()) {
                    holder.multi_check.setChecked(true);
                } else {
                    holder.multi_check.setChecked(false);
                }
            }
        } else {
            super.onBindViewHolder(holder, position, payloads);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final Viewholder holder, final int position) {
        holder.img_icon.setImageDrawable(list.get(position).getIcon());
        holder.name.setText(list.get(position).getName());
        holder.imgmore.setVisibility(View.VISIBLE);
        holder.multi_check.setVisibility(View.GONE);
        holder.packagename.setText(list.get(position).getPackageName());
        holder.size.setText(list.get(position).getSize());
        if (list.get(position).isSelected()) {
            holder.multi_check.setChecked(true);
        } else {
            holder.multi_check.setChecked(false);
        }
        if (isInAction) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.multi_check.getLayoutParams();
            holder.imgmore.setVisibility(View.GONE);
            holder.multi_check.setVisibility(View.VISIBLE);
            params.addRule(RelativeLayout.LEFT_OF, R.id.item_linear);
            params.addRule(RelativeLayout.LEFT_OF, R.id.appname);
            holder.multi_check.setLayoutParams(params);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        ImageView img_icon, imgmore;
        TextView name, packagename, size;
        RelativeLayout rel_main;
        CheckBox multi_check;
        LinearLayout item_linear;
        ItemClickListener mItemClickListener;

        public Viewholder(@NonNull View itemView, ItemClickListener itemClickListener) {
            super(itemView);
            this.mItemClickListener = itemClickListener;
            rel_main = itemView.findViewById(R.id.rel_main);
            img_icon = itemView.findViewById(R.id.appicon);
            imgmore = itemView.findViewById(R.id.imgmore);
            multi_check = itemView.findViewById(R.id.check_multi);
            name = itemView.findViewById(R.id.appname);
            packagename = itemView.findViewById(R.id.pkgname);
            size = itemView.findViewById(R.id.txt_size);
            item_linear = itemView.findViewById(R.id.item_linear);
            rel_main.setOnClickListener(this);
            rel_main.setOnLongClickListener(this);
            multi_check.setOnClickListener(this);
            imgmore.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onItemClick(getAdapterPosition(), v, list);
        }

        @Override
        public boolean onLongClick(View v) {
            itemClickListener.onItemLongClick(getAdapterPosition(), v, list);
            return true;
        }
    }

    public void setIsInAction(boolean b) {
        isInAction = b;
    }

    public void filteredList(List<AppListModel> arrayList) {
        list = arrayList;
        notifyDataSetChanged();
    }

    public void extract(int position, String name) {
        File file = new File(String.valueOf(list.get(position).getFile()));
        if (!preferences.getPurchasePref()) {
            FullScreenAds fullScreenAds = new FullScreenAds(context);
            fullScreenAds.showFullScreenAd();
        }
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Apk Extractor/");
        if (!dir.exists()) {
            dir.mkdir();
        }
        File newFile = new File((Environment.getExternalStorageDirectory().getAbsolutePath() + "/Apk Extractor/"), name + ".apk");
        ExtractThread extractThread = new ExtractThread(context, file, newFile, newFile.getPath(), color, view);
        extractThread.start();
    }


    public void onClickItem(final int position) {
        if (preferences.getSwitchState()) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setCancelable(true);
            View customView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_appdetails, null);
            builder.setView(customView);
            ImageView icon = customView.findViewById(R.id.icon);
            icon.setImageDrawable(list.get(position).getIcon());
            TextView txt_name, txt_pkg, txt_version, txt_targetsdk, txt_minsdk, txt_size, txt_uid, txt_permissions, tv_per;
            txt_minsdk = customView.findViewById(R.id.txt_minsdk);
            LinearLayout linear_minsdk = customView.findViewById(R.id.linear_minsdk);
            if (list.get(position).getMinsdk() != null) {
                linear_minsdk.setVisibility(View.VISIBLE);
                txt_minsdk.setText(list.get(position).getMinsdk());
            }
            txt_name = customView.findViewById(R.id.txt_name);
            txt_pkg = customView.findViewById(R.id.txt_pkg);
            txt_version = customView.findViewById(R.id.txt_version);
            txt_targetsdk = customView.findViewById(R.id.txt_targetsdk);
            txt_size = customView.findViewById(R.id.txt_size);
            txt_uid = customView.findViewById(R.id.txt_uid);
            txt_permissions = customView.findViewById(R.id.txt_permissions);
            tv_per = customView.findViewById(R.id.tv_per);
            RelativeLayout rel_head = customView.findViewById(R.id.rel_head);
            txt_name.setText(list.get(position).getName());
            txt_pkg.setText(list.get(position).getPackageName());
            txt_version.setText(list.get(position).getVername());
            txt_targetsdk.setText(list.get(position).getTargetsdk());
            txt_size.setText(list.get(position).getSize());
            txt_uid.setText(list.get(position).getUid());
            txt_permissions.setText(list.get(position).getPermissions());
            Log.d("listper", list.get(position).getPermissions());
            if (list.get(position).getPermissions() != null) {
                tv_per.setVisibility(View.VISIBLE);
                txt_permissions.setVisibility(View.VISIBLE);
            }
            rel_head.setBackgroundColor(color);
            AlertDialog dialog = builder.create();
            dialog.setButton(-2, context.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            dialog.setButton(-1, context.getResources().getString(R.string.extract), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    extract(position, newFileName(list.get(position).getName(), list.get(position).getVersion(), list.get(position).getVername(), list.get(position).getPackageName()));
                    dialog.dismiss();
                }
            });
            dialog.show();
            final Button btn_canel = dialog.getButton(-2);
            final Button btn_extract = dialog.getButton(-1);
            btn_canel.setTextColor(color);
            btn_extract.setTextColor(color);
        } else {
            extract(position, newFileName(list.get(position).getName(), list.get(position).getVersion(), list.get(position).getVername(), list.get(position).getPackageName()));
        }
    }

    public String newFileName(String appname, String vercode, String vername, String pkgname) {
        StringBuilder builder = new StringBuilder();
        if (preferences.getAppName()) {
            builder.append(appname + "_");
        }
        if (preferences.getPkgName()) {
            builder.append(pkgname + "_");
        }
        if (preferences.getVerName()) {
            builder.append(vername + "_");
        }
        if (preferences.getVerCode()) {
            builder.append("V" + vercode + "_");
        }
        int l = builder.toString().length();
        return builder.toString().substring(0, l - 1);
    }

    public interface ItemClickListener {
        void onItemClick(int position, View view, List<AppListModel> updatedList);

        void onItemLongClick(int position, View view, List<AppListModel> updatedList);
    }
}


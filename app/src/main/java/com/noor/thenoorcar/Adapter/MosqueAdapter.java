package com.noor.thenoorcar.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.noor.thenoorcar.Class.MosqueClass;
import com.noor.thenoorcar.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MosqueAdapter extends RecyclerView.Adapter<MosqueAdapter.MyViewHolder> {

    private List<MosqueClass> menuList;
    Activity activity;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView_masjid,textView_jalan,textView_distance;
        ImageView imageView_waze,imageView_google;
        CircleImageView circleImageView;
        public MyViewHolder(View view) {
            super(view);
            textView_masjid =  view.findViewById(R.id.textView_masjid);
            textView_jalan = view.findViewById(R.id.textView_jalan);
            imageView_waze =  view.findViewById(R.id.imageView_waze);
            imageView_google = view.findViewById(R.id.imageView_google);
            textView_distance = view.findViewById(R.id.textView_distance);


        }
    }


    public MosqueAdapter(List<MosqueClass> menuList, Activity activity) {
        this.menuList = menuList;
        this.activity = activity;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_mosque, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final MosqueClass menu = menuList.get(position);

        holder.textView_masjid.setText(menu.getName());
        holder.textView_jalan.setText(menu.getVicinity());
        holder.textView_distance.setText(String.format("%.1f",Double.parseDouble(menu.getDistance())));
        final String strUri = "http://maps.google.com/maps?q=loc:" + menu.getLatitude() + "," + menu.getLongitude() + " (" +menu.getName()+ ")";


       /* Navi navi = new Navi(activity.getApplicationContext());

        holder.textView_distance.setText(String.format("%.1f",Double.parseDouble(menu.getDistance())));

        holder.imageView_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navi.reqNaviToPoi("Penang", "Penang", Double.parseDouble(menu.getLongitude()), Double.parseDouble(menu.getLatitude()));
                *//* PackageManager pm = activity.getPackageManager();
                boolean isInstalled = isPackageInstalled("com.google.android.apps.maps", pm);
                if(isInstalled){
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(strUri));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                    activity.startActivity(intent);
                }else{

                }*//*
            }
        });*/

        holder.imageView_waze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackageManager pm = activity.getPackageManager();
                boolean isInstalled = isPackageInstalled("com.waze", pm);
                if(isInstalled){
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(strUri));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setPackage("com.waze");
                    activity.startActivity(intent);
                }else{

                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return menuList.size();
    }

    private boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}

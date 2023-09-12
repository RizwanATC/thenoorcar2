package com.noor.thenoorcar.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.noor.thenoorcar.Class.AsmaReciterClass;

import com.noor.thenoorcar.Fragment.AsmaFragment;
import com.noor.thenoorcar.Function.PreferenceReciterAsma;
import com.noor.thenoorcar.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AsmaReciterAdapter extends PagerAdapter {

    private List<AsmaReciterClass> menuList;
    Activity activity;
    LayoutInflater layoutInflater;
    AlertDialog.Builder tngBuilder;
    AlertDialog tngInfoDialog;

    @Override
    public int getCount() {
        return menuList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == ((LinearLayout) object);
    }

    public AsmaReciterAdapter(List<AsmaReciterClass> menuList, Activity activity) {
        this.menuList = menuList;
        this.activity = activity;
        layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View itemView = layoutInflater.inflate(R.layout.adapter_reciter_asma, container, false);

        CircleImageView imageView_profile = itemView.findViewById(R.id.imageView_profile);
        TextView textView_name = itemView.findViewById(R.id.textView_name);
        TextView textView_details = itemView.findViewById(R.id.textView_details);
        ImageView imageView_tick = itemView.findViewById(R.id.imageView_tick);

        Picasso.get().load(menuList.get(position).getImageUrl()).into(imageView_profile);
        textView_name.setText(menuList.get(position).getName());
        textView_details.setText(menuList.get(position).getDescription());

        if(menuList.get(position).getSelect()){
            imageView_tick.setVisibility(View.VISIBLE);
        }else {
            imageView_tick.setVisibility(View.GONE);
        }

        imageView_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AsmaFragment.status_play = 0;
                if (AsmaFragment.mediaAsma!=null){
                    AsmaFragment.mediaAsma.stop();
                }

                AsmaFragment.status_play_asma = false;
                if(AsmaFragment.yourCountDownTimer !=null){
                    AsmaFragment.yourCountDownTimer.cancel();
                }

                AsmaFragment.length_time = 0;
                AsmaFragment.count_surah_read = 0;
                Drawable icon_change = activity.getResources().getDrawable(R.drawable.play_asma_icon);
                AsmaFragment.imageView_play.setImageDrawable(icon_change);
                AsmaFragment.position_scroll = -1;
                AsmaFragment.mAdapter.notifyDataSetChanged();

                PreferenceReciterAsma sessionAsma = new PreferenceReciterAsma(activity);
                sessionAsma.createLoginSession(menuList.get(position).getId());
                AsmaFragment.url = menuList.get(position).getFileUrl();

                for (int i = 0 ; i <menuList.size(); i++){
                    menuList.get(i).setSelect(false);
                }
                notifyDataSetChanged();
                menuList.get(position).setSelect(true);
                notifyDataSetChanged();
                Picasso.get().load(menuList.get(position).getImageUrl()).into(AsmaFragment.profile_image);
                AsmaFragment.reciterDialog.dismiss();
                AsmaFragment.getDetails();
            }
        });


        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}


package com.noor.thenoorcar.Adapter;

import static com.noor.thenoorcar.Dashboard.convertDateToTimesssss;
import static com.noor.thenoorcar.Fragment.PrayerTime.findPositionOfToday;
import static com.noor.thenoorcar.Fragment.PrayerTime.recycle_prayer;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.noor.thenoorcar.Class.WaktuSolatClass;
import com.noor.thenoorcar.Function.FontHelper;
import com.noor.thenoorcar.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class WaktuSolatAdapter extends RecyclerView.Adapter<WaktuSolatAdapter.WaktuSolatViewHolder> {
    private List<WaktuSolatClass> menuList;
    private Context context;

    public WaktuSolatAdapter(List<WaktuSolatClass> menuList, Context context) {
        this.menuList = menuList;
        this.context = context;
    }

    @NonNull
    @Override
    public WaktuSolatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_waktu_solat, parent, false);
        return new WaktuSolatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WaktuSolatViewHolder holder, int position) {
        WaktuSolatClass waktuSolat = menuList.get(position);
        // Bind your data to the ViewHolder's views here
        holder.masihi_date.setText(waktuSolat.getDate());
        holder.hijriDateTextView.setText(waktuSolat.getHijri());
        holder.subuh.setText(waktuSolat.getSubuh().toUpperCase());
        holder.zohor.setText(waktuSolat.getZuhur().toUpperCase());
        holder.asar.setText(waktuSolat.getAsar().toUpperCase());
        holder.maghrib.setText(waktuSolat.getMaghrib().toUpperCase());
        holder.isyak.setText(waktuSolat.getIsyak().toUpperCase());
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDate today = LocalDate.now();
            String today_date = convertDateToTimesssss(waktuSolat.getDate());
            String todayAsString = today.toString();

            SimpleDateFormat currentDate = new SimpleDateFormat("hh:mm a");
            Date todayDate = new Date();
            String serverTime = currentDate.format(todayDate);

            String subuh = waktuSolat.getSubuh();
            String zuhur = waktuSolat.getZuhur();
            String asar = waktuSolat.getAsar();
            String maghrib = waktuSolat.getMaghrib();
            String isya = waktuSolat.getIsyak();



            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
            Date timeSubuh, timeZuhur, timeAsar, timeMaghrib, timeIsya, currentTime;

            if(todayAsString.equals(today_date)){
                holder.back_today.setVisibility(View.GONE);
                try {
                    // Parse the input times
                    timeSubuh = timeFormat.parse(subuh);
                    timeZuhur = timeFormat.parse(zuhur);
                    timeAsar = timeFormat.parse(asar);
                    timeMaghrib = timeFormat.parse(maghrib);
                    timeIsya = timeFormat.parse(isya);

                    // Get current time
                    Calendar currentCalendar = Calendar.getInstance();
                    currentTime = timeFormat.parse(timeFormat.format(currentCalendar.getTime()));

                    Typeface typeface = context.getResources().getFont(R.font.avenirblack);
                    if (currentTime.after(timeSubuh) && currentTime.before(timeZuhur)) {
                        holder.zohor.setTypeface(typeface);
                        holder.waktuzohor.setTypeface(typeface);
                    } else if (currentTime.after(timeZuhur) && currentTime.before(timeAsar)) {
                        holder.asar.setTypeface(typeface);
                        holder.waktuasar.setTypeface(typeface);
                    } else if (currentTime.after(timeAsar) && currentTime.before(timeMaghrib)) {
                        holder.maghrib.setTypeface(typeface);
                        holder.waktumaghrib.setTypeface(typeface);
                    } else if (currentTime.after(timeMaghrib) && currentTime.before(timeIsya)) {
                        holder.isyak.setTypeface(typeface);
                        holder.waktuisyak.setTypeface(typeface);
                    } else {
                        holder.subuh.setTypeface(typeface);
                        holder.waktusubuh.setTypeface(typeface);

                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    // Handle the exception here if needed
                }

            }else{
                holder.back_today.setVisibility(View.VISIBLE);


            }


        }
        holder.back_today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation slideIn = AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.slide_in_left);
                recycle_prayer.startAnimation(slideIn);
                findPositionOfToday();
            }
        });




        // Bind other data to other TextViews here
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }

    public class WaktuSolatViewHolder extends RecyclerView.ViewHolder {
        TextView hijriDateTextView,masihi_date,waktusubuh,waktuzohor,waktuasar,waktumaghrib,waktuisyak,
        subuh,zohor,asar,maghrib,isyak;
        RelativeLayout back_today;
        // Add other TextViews for your data here

        public WaktuSolatViewHolder(@NonNull View itemView) {
            super(itemView);
            hijriDateTextView = itemView.findViewById(R.id.tv_date_hijri);
            masihi_date = itemView.findViewById(R.id.date_masihi);
            subuh = itemView.findViewById(R.id.tv_waktu_subuh);
            zohor = itemView.findViewById(R.id.tv_waktu_zohor);
            asar = itemView.findViewById(R.id.tv_waktu_asar);
            maghrib = itemView.findViewById(R.id.tv_waktu_maghrib);
            isyak = itemView.findViewById(R.id.tv_waktu_isyak);

            waktusubuh = itemView.findViewById(R.id.tv_prayer_subuh);
            waktuzohor = itemView.findViewById(R.id.tv_prayer_zohor);
            waktuasar = itemView.findViewById(R.id.tv_prayer_asar);
            waktumaghrib = itemView.findViewById(R.id.tv_prayer_maghrib);
            waktuisyak = itemView.findViewById(R.id.tv_prayer_isyak);
            back_today = itemView.findViewById(R.id.back_today);

            ;







            // Initialize other TextViews here
        }
    }
}

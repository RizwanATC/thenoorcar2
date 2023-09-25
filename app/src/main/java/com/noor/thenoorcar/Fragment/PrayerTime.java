package com.noor.thenoorcar.Fragment;

import static android.content.ContentValues.TAG;
import static com.android.volley.Request.Method.GET;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.noor.thenoorcar.Adapter.WaktuSolatAdapter;
import com.noor.thenoorcar.Function.GpsTracker;
import com.noor.thenoorcar.Function.ScreenUtils;
import com.noor.thenoorcar.Function.SnapHelperOneByOne;
import com.noor.thenoorcar.R;
import com.noor.thenoorcar.URL.Url;
import com.noor.thenoorcar.Class.WaktuSolatClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class PrayerTime extends Fragment {
    public static Context prayerFrgmntCtx;
    private GpsTracker gpsTracker;

    public static RecyclerView recycle_prayer;
    public static WaktuSolatAdapter mAdapter;
    public static List<WaktuSolatClass> menuList;
    public static String today_date;

    public static TextView textView_which_prayer,textView_countdown,textView_location;
    public static String weather = "29", temperatureUnit = " °", weatherDescription, nextDayHijriDate = "";
    private RequestQueue mRequestQueue;
    ConstraintLayout constraintSilver;
    ImageView backBtn,noorLogo,prev,next;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the appropriate layout for this fragment based on screen width
        Point screenSize = ScreenUtils.getScreenSize(getActivity());
        int screenWidth = screenSize.x;
        int screenHeight = screenSize.y;
        int layoutResId = (screenWidth == 1920) ? R.layout.fragment_prayer_time : R.layout.fragment_prayer_time_large;

        View v = inflater.inflate(layoutResId, container, false);

        // Common view setup
        recycle_prayer = v.findViewById(R.id.recycle_prayer);
        constraintSilver = v.findViewById(R.id.constraintSilver);
        backBtn = v.findViewById(R.id.backBtn);
        noorLogo = v.findViewById(R.id.noorLogo);
        prev = v.findViewById(R.id.prev);
        next = v.findViewById(R.id.next);
        textView_which_prayer = v.findViewById(R.id.textView_which_prayer);
        textView_countdown = v.findViewById(R.id.textView_countdown);
        textView_location = v.findViewById(R.id.textView_location);


        mRequestQueue = Volley.newRequestQueue(getActivity());
        SimpleDateFormat currentDate = new SimpleDateFormat("yyyy-MM-dd");
        Date todayDate = new Date();
        today_date = currentDate.format(todayDate);

        LinearSnapHelper linearSnapHelper = new SnapHelperOneByOne();
        linearSnapHelper.attachToRecyclerView(recycle_prayer);

        return v;
    }
    private void fetchDataBasedOnLatLang(double latitude, double longitude) {
        recycle_prayer.setNestedScrollingEnabled(false);
        recycle_prayer.setHasFixedSize(false);
        menuList = new ArrayList<>();
        mAdapter = new WaktuSolatAdapter(menuList, getActivity());
        recycle_prayer.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recycle_prayer.setAdapter(mAdapter);
        final JSONObject jsonData = new JSONObject();
        JsonObjectRequest jsonReq = new JsonObjectRequest(
                GET,
                Url.url_thenoor_app+"solat/prayer_times/by_coordinates?coordinates="+latitude+"%2C%20"+longitude+"&day=360&period=ranged_days&date="+today_date,
                jsonData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Handle the JSON response here
                        try {
                            JSONArray array_waktu_solat = new JSONArray(response.getString("hydra:member"));
                            SimpleDateFormat currentDate = new SimpleDateFormat("HH:mm:ss");
                            Date todayDate = new Date();
                            String serverTime = currentDate.format(todayDate);

                            for (int i = 0 ; i < array_waktu_solat.length(); i++){
                                JSONObject prayerTime_obj = array_waktu_solat.getJSONObject(i);

                                WaktuSolatClass sessionClass = new WaktuSolatClass(
                                        prayerTime_obj.getString("hijriDate"),
                                        convertDateToTime(prayerTime_obj.getString("date")),
                                        convertDateToTime12hour(prayerTime_obj.getString("imsak")),
                                        convertDateToTime12hour(prayerTime_obj.getString("subuh")),
                                        convertDateToTime12hour(prayerTime_obj.getString("syuruk")),
                                        convertDateToTime12hour(prayerTime_obj.getString("zohor")),
                                        convertDateToTime12hour(prayerTime_obj.getString("asar")),
                                        convertDateToTime12hour(prayerTime_obj.getString("maghrib")),
                                        convertDateToTime12hour(prayerTime_obj.getString("isyak")),
                                        weather,
                                        temperatureUnit,
                                        weatherDescription,
                                        nextDayHijriDate
                                );
                                menuList.add(sessionClass);
                                mAdapter.notifyDataSetChanged();
                                if(today_date.equals(convertDateToNormal(prayerTime_obj.getString("date")))){
                                    ((LinearLayoutManager)recycle_prayer.getLayoutManager()).scrollToPositionWithOffset(i,0);
                                }
                            }

                            Log.d(TAG, "onResponse: "+response.toString());
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error here
                        Log.d(TAG, "onErrorResponse: "+error.getMessage());

                    }
                });

        mRequestQueue.add(jsonReq);



    }
    private void fetchJsonData(double latitude ,double longitude) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, Url.url_thenoor_app+"solat/prayer_times/by_coordinates?coordinates="+latitude+"" +
                "%2C%20"+longitude+"&period=today", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Handle the JSON response here
                        try {
                            JSONArray array_waktu_solat = new JSONArray(response.getString("hydra:member"));
                            SimpleDateFormat currentDate = new SimpleDateFormat("HH:mm:ss");
                            Date todayDate = new Date();
                            String serverTime = currentDate.format(todayDate);
                            Context context = getContext();
                            if (context == null) {
                                Log.e("PrayerFragment", "Fragment is not attached to an Activity.");
                                return;
                            }
                            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                            if (alarmManager == null) {
                                Log.e("PrayerFragment", "Failed to get AlarmManager.");
                                return;
                            }


                            for (int i = 0 ; i < array_waktu_solat.length(); i++){
                                JSONObject prayerTime_obj = array_waktu_solat.getJSONObject(i);
                                textView_location.setText(prayerTime_obj.getString("location"));
                                boolean fajr = checktimings(serverTime,convertDate(prayerTime_obj.getString("subuh")));
                                if(fajr){
                                    textView_which_prayer.setText("Subuh");
                                    PrayerTime.textView_which_prayer.setText("Testing");
                                    countdownSubuh(serverTime,prayerTime_obj);
                                }else{
                                    boolean syuruk = checktimings(serverTime,convertDate(prayerTime_obj.getString("syuruk")));
                                    if(syuruk){
                                        textView_which_prayer.setText("Syuruk");
                                    }else{
                                        boolean dhuhr =  checktimings(serverTime,convertDate(prayerTime_obj.getString("zohor")));
                                        if(dhuhr){
                                            textView_which_prayer.setText("Zohor");
                                            countdownZohor(serverTime,prayerTime_obj);

                                        }else{
                                            boolean asr = checktimings(serverTime,convertDate(prayerTime_obj.getString("asar")));
                                            if(asr){
                                                textView_which_prayer.setText("Asar");
                                                countdownAsar(serverTime,prayerTime_obj);
                                            }else{
                                                boolean maghrib = checktimings(serverTime,convertDate(prayerTime_obj.getString("maghrib")));
                                                if(maghrib){
                                                    textView_which_prayer.setText("Maghrib");
                                                    countdownMargrib(serverTime,prayerTime_obj);
                                                }else{
                                                    boolean isha = checktimings(serverTime,convertDate(prayerTime_obj.getString("isyak")));
                                                    if(isha){
                                                        textView_which_prayer.setText("Isyak");
                                                        countdownIsyak(serverTime,prayerTime_obj);
                                                    }else{
                                                        /*getPrayertimeTomorrow(serverTime,latitude,longitude);*/
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }




                            }


                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error here
                        Log.d(TAG, "onErrorResponse: "+error.getMessage());

                    }
                });

        mRequestQueue.add(request);
    }



    @Override
    public void onResume() {
        super.onResume();
        getLocation();
    }

    public void getLocation() {
        gpsTracker = new GpsTracker(getActivity());
        if (gpsTracker.canGetLocation()) {
            Log.d(TAG, "getLocation: " + gpsTracker.getLatitude() + " " + gpsTracker.getLongitude());
            fetchDataBasedOnLatLang(gpsTracker.getLatitude(), gpsTracker.getLongitude());
            fetchJsonData(gpsTracker.getLatitude(), gpsTracker.getLongitude());

        } else {
            gpsTracker.showSettingsAlert();
        }
    }

    private void countdownSubuh(String serverTime, JSONObject objectArr) throws ParseException, JSONException {
        long start_millis = convertTimetoMili(serverTime); //get the start time in milliseconds
        long end_millis = convertTimetoMili(convertDate(objectArr.getString("subuh"))); //get the end time in milliseconds
        long total_millis = (end_millis - start_millis); //total time in milliseconds


        //1000 = 1 second interval
        CountDownTimer cdt = new CountDownTimer(total_millis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long days = TimeUnit.MILLISECONDS.toDays(millisUntilFinished);
                millisUntilFinished -= TimeUnit.DAYS.toMillis(days);

                long hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
                millisUntilFinished -= TimeUnit.HOURS.toMillis(hours);

                long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                millisUntilFinished -= TimeUnit.MINUTES.toMillis(minutes);

                long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);

                if(hours == 0){
                    textView_countdown.setText( "in "+ minutes + " Min " + seconds +" Sec");
                }else if(hours!=0){
                    textView_countdown.setText( "in " + hours + " Hours "+ minutes + " Min " + seconds +" Sec");

                }


            }

            @Override
            public void onFinish() {
                textView_countdown.setText("Prayer Time!");
                getLocation();
            }
        };
        cdt.start();
    }
    public static String convertDateToTimesssss(String date){
        DateFormat df = new SimpleDateFormat("EEEE, dd'th' MMMM yyyy");
        //Date/time pattern of desired output date
        DateFormat outputformat = new SimpleDateFormat("yyyy-MM-dd");
        Date dates = null;
        String output = null;
        try{
            dates= df.parse(date);
            output = outputformat.format(dates);
        }catch(ParseException pe){
            pe.printStackTrace();
        }

        return output;
    }
    private void countdownZohor(String serverTime, JSONObject objectArr) throws ParseException, JSONException {
        long start_millis = convertTimetoMili(serverTime); //get the start time in milliseconds
        long end_millis = convertTimetoMili(convertDate(objectArr.getString("zohor"))); //get the end time in milliseconds
        long total_millis = (end_millis - start_millis); //total time in milliseconds

        //1000 = 1 second interval
        CountDownTimer cdt = new CountDownTimer(total_millis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long days = TimeUnit.MILLISECONDS.toDays(millisUntilFinished);
                millisUntilFinished -= TimeUnit.DAYS.toMillis(days);

                long hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
                millisUntilFinished -= TimeUnit.HOURS.toMillis(hours);

                long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                millisUntilFinished -= TimeUnit.MINUTES.toMillis(minutes);

                long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);

                if(hours == 0){
                    textView_countdown.setText( "in "+ minutes + " Min " + seconds +" Sec");
                }else if(hours!=0){
                    textView_countdown.setText( "in " + hours + " Hours "+ minutes + " Min " + seconds +" Sec");

                }


            }

            @Override
            public void onFinish() {
                textView_countdown.setText("Prayer Time!");
                getLocation();
            }
        };
        cdt.start();
    }
    private void countdownAsar(String serverTime, JSONObject objectArr) throws ParseException, JSONException {
        long start_millis = convertTimetoMili(serverTime); //get the start time in milliseconds
        long end_millis = convertTimetoMili(convertDate(objectArr.getString("asar"))); //get the end time in milliseconds
        long total_millis = (end_millis - start_millis); //total time in milliseconds

        //1000 = 1 second interval
        CountDownTimer cdt = new CountDownTimer(total_millis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long days = TimeUnit.MILLISECONDS.toDays(millisUntilFinished);
                millisUntilFinished -= TimeUnit.DAYS.toMillis(days);

                long hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
                millisUntilFinished -= TimeUnit.HOURS.toMillis(hours);

                long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                millisUntilFinished -= TimeUnit.MINUTES.toMillis(minutes);

                long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);

                if(hours == 0){
                    textView_countdown.setText( "in "+ minutes + " Min " + seconds +" Sec");
                }else if(hours!=0){
                    textView_countdown.setText( "in " + hours + " Hours "+ minutes + " Min " + seconds +" Sec");

                }

            }

            @Override
            public void onFinish() {
                textView_countdown.setText("Prayer Time!");
                getLocation();
            }
        };
        cdt.start();
    }
    private void countdownMargrib(String serverTime, JSONObject objectArr) throws ParseException, JSONException {
        long start_millis = convertTimetoMili(serverTime); //get the start time in milliseconds
        long end_millis = convertTimetoMili(convertDate(objectArr.getString("maghrib"))); //get the end time in milliseconds
        long total_millis = (end_millis - start_millis); //total time in milliseconds

        //1000 = 1 second interval
        CountDownTimer cdt = new CountDownTimer(total_millis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long days = TimeUnit.MILLISECONDS.toDays(millisUntilFinished);
                millisUntilFinished -= TimeUnit.DAYS.toMillis(days);

                long hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
                millisUntilFinished -= TimeUnit.HOURS.toMillis(hours);

                long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                millisUntilFinished -= TimeUnit.MINUTES.toMillis(minutes);

                long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);

                if(hours == 0){
                    textView_countdown.setText( "in "+ minutes + " Min " + seconds +" Sec");
                }else if(hours!=0){
                    textView_countdown.setText( "in " + hours + " Hours "+ minutes + " Min " + seconds +" Sec");

                }

            }

            @Override
            public void onFinish() {
                textView_countdown.setText("Prayer Time!");
                getLocation();
            }
        };
        cdt.start();
    }

    private void countdownIsyak(String serverTime, JSONObject objectArr) throws ParseException, JSONException {
        long start_millis = convertTimetoMili(serverTime); //get the start time in milliseconds
        long end_millis = convertTimetoMili(convertDate(objectArr.getString("isyak"))); //get the end time in milliseconds
        long total_millis = (end_millis - start_millis); //total time in milliseconds

        //1000 = 1 second interval
        CountDownTimer cdt = new CountDownTimer(total_millis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long days = TimeUnit.MILLISECONDS.toDays(millisUntilFinished);
                millisUntilFinished -= TimeUnit.DAYS.toMillis(days);

                long hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
                millisUntilFinished -= TimeUnit.HOURS.toMillis(hours);

                long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                millisUntilFinished -= TimeUnit.MINUTES.toMillis(minutes);

                long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);

                if(hours == 0){
                    textView_countdown.setText( "in "+ minutes + " Min " + seconds +" Sec");
                }else if(hours!=0){
                    textView_countdown.setText( "in " + hours + " Hours "+ minutes + " Min " + seconds +" Sec");

                }


            }

            @Override
            public void onFinish() {
                textView_countdown.setText("Prayer Time!");
                getLocation();
            }
        };
        cdt.start();
    }

    private long convertTimetoMili(String date) throws ParseException {
        SimpleDateFormat spf=new SimpleDateFormat("HH:mm:ss");
        Date newDate=spf.parse(date);
        return newDate.getTime();
    }

    public static String convertDateToTime12hour(String date){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        //Date/time pattern of desired output date
        DateFormat outputformat = new SimpleDateFormat("hh:mm aa");
        Date dates = null;
        String output = null;
        try{
            dates= df.parse(date);
            output = outputformat.format(dates);
        }catch(ParseException pe){
            pe.printStackTrace();
        }

        return output;
    }
    public static String convertDateToTime(String date){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        //Date/time pattern of desired output date
        DateFormat outputformat = new SimpleDateFormat("EEEE, dd'th' MMMM yyyy");
        Date dates = null;
        String output = null;
        try{
            dates= df.parse(date);
            output = outputformat.format(dates);
        }catch(ParseException pe){
            pe.printStackTrace();
        }

        return output;
    }
    private boolean checktimings(String time, String endtime) {
        String pattern = "HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        try {
            Date date1 = sdf.parse(time);
            Date date2 = sdf.parse(endtime);

            if(date1.before(date2)) {
                return true;
            } else {
                return false;
            }
        } catch (ParseException e){
            e.printStackTrace();
        }
        return false;
    }
    private String convertDate(String date) throws ParseException {
        SimpleDateFormat spf=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date newDate=spf.parse(date);
        spf= new SimpleDateFormat("HH:mm:ss");
        date = spf.format(newDate);
        return date;
    }
    public static String convertDateToNormal(String date){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        //Date/time pattern of desired output date
        DateFormat outputformat = new SimpleDateFormat("yyyy-MM-dd");
        Date dates = null;
        String output = null;
        try{
            dates= df.parse(date);
            output = outputformat.format(dates);
        }catch(ParseException pe){
            pe.printStackTrace();
        }

        return output;
    }

    public static void findPositionOfToday() {
        for (int i = 0; i < menuList.size(); i++) {
            if(today_date.equals(convertDateToTimesssss(menuList.get(i).getDate()))){
                ((LinearLayoutManager)recycle_prayer.getLayoutManager()).scrollToPositionWithOffset(i,0);
                break;
            }
        }
    }

}

package com.noor.thenoorcar;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.noor.thenoorcar.Function.GpsTracker;
import com.noor.thenoorcar.Function.ScreenUtils;
import com.noor.thenoorcar.URL.Url;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Dashboard extends AppCompatActivity {
    private GpsTracker gpsTracker;
    private TextView txt_time,txt_prayer,textView_countdown;

    ImageView im_prayer,im_asma,im_location,im_compass,im_radio,im_quran,im_setting;
    private RequestQueue mRequestQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Display display = getWindowManager().getDefaultDisplay();
        Point screenSize = new Point();
        display.getSize(screenSize);
        int screenWidth = screenSize.x;
        int screenHeight = screenSize.y; // If needed

        // Choose the layout based on the screen width
        int layoutResId = (screenWidth == 1920) ? R.layout.activity_dashboard : R.layout.activity_dashboard_large;

        // Set the content view for the activity
        setContentView(layoutResId);
        txt_prayer = findViewById(R.id.txt_prayer);
        txt_time = findViewById(R.id.txt_time);
        textView_countdown = findViewById(R.id.textView_countdown);
        im_prayer = findViewById(R.id.im_prayer);
        im_asma = findViewById(R.id.im_asma);
        im_location = findViewById(R.id.im_location);
        im_compass = findViewById(R.id.im_compass);
        im_radio = findViewById(R.id.im_radio);
        im_quran = findViewById(R.id.im_quran);
        im_setting = findViewById(R.id.im_setting);

        mRequestQueue = Volley.newRequestQueue(this);






        SharedPreferences preferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        im_prayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int intValue = 0;
                editor.putInt("int_key", intValue);
                editor.apply();
                Intent next = new Intent(getApplicationContext(), DashboardMain.class);
                startActivity(next);
            }
        });

        im_asma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int intValue = 1;
                editor.putInt("int_key", intValue);
                editor.apply();
                Intent next = new Intent(getApplicationContext(), DashboardMain.class);
                startActivity(next);
            }
        });
        im_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int intValue = 2;
                editor.putInt("int_key", intValue);
                editor.apply();
                Intent next = new Intent(getApplicationContext(), DashboardMain.class);
                startActivity(next);
            }
        });
        im_compass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int intValue = 3;
                editor.putInt("int_key", intValue);
                editor.apply();
                Intent next = new Intent(getApplicationContext(), DashboardMain.class);
                startActivity(next);
            }
        });
        im_radio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int intValue = 4;
                editor.putInt("int_key", intValue);
                editor.apply();
                Intent next = new Intent(getApplicationContext(), DashboardMain.class);
                startActivity(next);
            }
        });
        im_quran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int intValue = 5;
                editor.putInt("int_key", intValue);
                editor.apply();
                Intent next = new Intent(getApplicationContext(), DashboardMain.class);
                startActivity(next);
            }
        });
        im_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent next = new Intent(getApplicationContext(), Setting.class);
                startActivity(next);
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        getLocation();
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

                            for (int i = 0 ; i < array_waktu_solat.length(); i++){
                                JSONObject prayerTime_obj = array_waktu_solat.getJSONObject(i);

                                boolean fajr = checktimings(serverTime,convertDate(prayerTime_obj.getString("subuh")));
                                if(fajr){
                                    txt_time.setText(convertDateToTime12hour(prayerTime_obj.getString("subuh")).toUpperCase());
                                    txt_prayer.setText("Subuh");
                                    countdownSubuh(serverTime,prayerTime_obj);
                                }else{
                                    boolean syuruk = checktimings(serverTime,convertDate(prayerTime_obj.getString("syuruk")));
                                    if(syuruk){
                                        txt_time.setText(convertDateToTime12hour(prayerTime_obj.getString("syuruk")).toUpperCase());
                                        txt_prayer.setText("Syuruk");
                                    }else{
                                        boolean dhuhr =  checktimings(serverTime,convertDate(prayerTime_obj.getString("zohor")));
                                        if(dhuhr){
                                            txt_time.setText(convertDateToTime12hour(prayerTime_obj.getString("zohor")).toUpperCase());
                                            txt_prayer.setText("Zohor");
                                            countdownZohor(serverTime,prayerTime_obj);
                                        }else{
                                            boolean asr = checktimings(serverTime,convertDate(prayerTime_obj.getString("asar")));
                                            if(asr){
                                                txt_time.setText(convertDateToTime12hour(prayerTime_obj.getString("asar")).toUpperCase());
                                                txt_prayer.setText("Asar");
                                                countdownAsar(serverTime,prayerTime_obj);
                                            }else{
                                                boolean maghrib = checktimings(serverTime,convertDate(prayerTime_obj.getString("maghrib")));
                                                if(maghrib){
                                                    txt_time.setText(convertDateToTime12hour(prayerTime_obj.getString("maghrib")).toUpperCase());
                                                    txt_prayer.setText("Maghrib");
                                                    countdownMargrib(serverTime,prayerTime_obj);
                                                }else{
                                                    boolean isha = checktimings(serverTime,convertDate(prayerTime_obj.getString("isyak")));
                                                    if(isha){
                                                        txt_time.setText(convertDateToTime12hour(prayerTime_obj.getString("isyak")).toUpperCase());
                                                        txt_prayer.setText("Isyak");
                                                        countdownIsyak(serverTime,prayerTime_obj);
                                                    }else{
                                                        /*getPrayertimeTomorrow(serverTime,latitude,longitude);*/
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

//                                }
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
                    textView_countdown.setText( " in "+ minutes + " Min " + seconds +" Sec");
                }else if(hours!=0){
                    textView_countdown.setText( " in " + hours + " Hours "+ minutes + " Min " + seconds +" Sec");

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
                    textView_countdown.setText( " in "+ minutes + " Min " + seconds +" Sec");
                }else if(hours!=0){
                    textView_countdown.setText( " in " + hours + " Hours "+ minutes + " Min " + seconds +" Sec");

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
                    textView_countdown.setText( " in "+ minutes + " Min " + seconds +" Sec");
                }else if(hours!=0){
                    textView_countdown.setText( " in " + hours + " Hours "+ minutes + " Min " + seconds +" Sec");

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
                    textView_countdown.setText( " in "+ minutes + " Min " + seconds +" Sec");
                }else if(hours!=0){
                    textView_countdown.setText( " in " + hours + " Hours "+ minutes + " Min " + seconds +" Sec");

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
                    textView_countdown.setText( " in "+ minutes + " Min " + seconds +" Sec");
                }else if(hours!=0){
                    textView_countdown.setText( " in " + hours + " Hours "+ minutes + " Min " + seconds +" Sec");

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


    public void getLocation() {
        gpsTracker = new GpsTracker(getApplication());
        if (gpsTracker.canGetLocation()) {
            Log.d(TAG, "getLocation: " + gpsTracker.getLatitude() + " " + gpsTracker.getLongitude());
            fetchJsonData(gpsTracker.getLatitude(),gpsTracker.getLongitude());

        } else {
            gpsTracker.showSettingsAlert();
        }
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


}

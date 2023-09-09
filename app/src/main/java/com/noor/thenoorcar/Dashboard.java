package com.noor.thenoorcar;

import static android.content.ContentValues.TAG;
import static com.android.volley.Request.Method.GET;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.noor.thenoorcar.Function.GpsTracker;
import com.noor.thenoorcar.URL.Url;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Dashboard extends AppCompatActivity {
    private GpsTracker gpsTracker;
    private TextView txt_time,txt_prayer,txt_countDown;
    private RequestQueue mRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        txt_prayer = findViewById(R.id.txt_prayer);
        txt_time = findViewById(R.id.txt_time);
        txt_countDown = findViewById(R.id.txt_countDown);
        mRequestQueue = Volley.newRequestQueue(this);


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

                            Log.d(TAG, "onResponse: "+response.toString());
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
                    txt_countDown.setText( " in "+ minutes + " Min " + seconds +" Sec");
                }else if(hours!=0){
                    txt_countDown.setText( " in " + hours + " Hours "+ minutes + " Min " + seconds +" Sec");

                }


            }

            @Override
            public void onFinish() {
                txt_countDown.setText("Prayer Time!");
                getLocation();
            }
        };
        cdt.start();
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
                    txt_countDown.setText( " in "+ minutes + " Min " + seconds +" Sec");
                }else if(hours!=0){
                    txt_countDown.setText( " in " + hours + " Hours "+ minutes + " Min " + seconds +" Sec");

                }


            }

            @Override
            public void onFinish() {
                txt_countDown.setText("Prayer Time!");
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
                    txt_countDown.setText( " in "+ minutes + " Min " + seconds +" Sec");
                }else if(hours!=0){
                    txt_countDown.setText( " in " + hours + " Hours "+ minutes + " Min " + seconds +" Sec");

                }

            }

            @Override
            public void onFinish() {
                txt_countDown.setText("Prayer Time!");
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
                    txt_countDown.setText( " in "+ minutes + " Min " + seconds +" Sec");
                }else if(hours!=0){
                    txt_countDown.setText( " in " + hours + " Hours "+ minutes + " Min " + seconds +" Sec");

                }

            }

            @Override
            public void onFinish() {
                txt_countDown.setText("Prayer Time!");
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
                    txt_countDown.setText( " in "+ minutes + " Min " + seconds +" Sec");
                }else if(hours!=0){
                    txt_countDown.setText( " in " + hours + " Hours "+ minutes + " Min " + seconds +" Sec");

                }


            }

            @Override
            public void onFinish() {
                txt_countDown.setText("Prayer Time!");
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

}

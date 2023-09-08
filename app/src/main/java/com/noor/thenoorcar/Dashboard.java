package com.noor.thenoorcar;

import static android.content.ContentValues.TAG;
import static com.android.volley.Request.Method.GET;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
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

public class Dashboard extends AppCompatActivity {
    private GpsTracker gpsTracker;
    private TextView txt_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        txt_time = findViewById(R.id.txt_time);
        String endpoint = Url.url_thenoor_app + "solat/prayer_times/by_coordinates?coordinates=" + gpsTracker.getLatitude() + "%2C%20" + gpsTracker.getLongitude() + "&period=today";
        JsonObjectRequest jsonReq = new JsonObjectRequest(
                GET,
                endpoint,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            JSONArray array_waktu_solat = new JSONArray(jsonObject.getString("hydra:member"));
                            SimpleDateFormat currentDate = new SimpleDateFormat("HH:mm:ss");
                            Date todayDate = new Date();
                            String serverTime = currentDate.format(todayDate);

                            // Create a LinkedHashMap of prayer times.
                            LinkedHashMap<String, String> prayerTimes = new LinkedHashMap<>();
                            for (int i = 0; i < array_waktu_solat.length(); i++) {
                                JSONObject prayerTime_obj = array_waktu_solat.getJSONObject(i);
                                prayerTimes.put("subuh", prayerTime_obj.getString("subuh"));
                                prayerTimes.put("syuruk", prayerTime_obj.getString("syuruk"));
                                prayerTimes.put("zohor", prayerTime_obj.getString("zohor"));
                                prayerTimes.put("asar", prayerTime_obj.getString("asar"));
                                prayerTimes.put("maghrib", prayerTime_obj.getString("maghrib"));
                                prayerTimes.put("isyak", prayerTime_obj.getString("isyak"));
                            }

                            // Find the next prayer time after serverTime.
                            for (Map.Entry<String, String> entry : prayerTimes.entrySet()) {
                                if (checktimings(serverTime, convertDate(entry.getValue()))) {
                                    String timetest =(convertDateToTime12hour(entry.getValue()).toUpperCase());
                                    txt_time.setText(convertDateToTime12hour(entry.getValue()).toUpperCase());
                                    break;  // Exit the loop once the next prayer time is found.
                                }
                            }
                        } catch (JSONException | ParseException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(Dashboard.this, "Error retrieving data.", Toast.LENGTH_SHORT).show(); // A simple error handling
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        Volley.newRequestQueue(this).add(jsonReq);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLocation();
    }

   /* private void getWaktuSolatBasedOnLatLngToday(double latitude, double longitude) {
        String endpoint = Url.url_thenoor_app + "solat/prayer_times/by_coordinates?coordinates=" + latitude + "%2C%20" + longitude + "&period=today";
        JsonObjectRequest jsonReq = new JsonObjectRequest(
                GET,
                endpoint,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            JSONArray array_waktu_solat = new JSONArray(jsonObject.getString("hydra:member"));
                            SimpleDateFormat currentDate = new SimpleDateFormat("HH:mm:ss");
                            Date todayDate = new Date();
                            String serverTime = currentDate.format(todayDate);

                            // Create a LinkedHashMap of prayer times.
                            LinkedHashMap<String, String> prayerTimes = new LinkedHashMap<>();
                            for (int i = 0; i < array_waktu_solat.length(); i++) {
                                JSONObject prayerTime_obj = array_waktu_solat.getJSONObject(i);
                                prayerTimes.put("subuh", prayerTime_obj.getString("subuh"));
                                prayerTimes.put("syuruk", prayerTime_obj.getString("syuruk"));
                                prayerTimes.put("zohor", prayerTime_obj.getString("zohor"));
                                prayerTimes.put("asar", prayerTime_obj.getString("asar"));
                                prayerTimes.put("maghrib", prayerTime_obj.getString("maghrib"));
                                prayerTimes.put("isyak", prayerTime_obj.getString("isyak"));
                            }

                            // Find the next prayer time after serverTime.
                            for (Map.Entry<String, String> entry : prayerTimes.entrySet()) {
                                if (checktimings(serverTime, convertDate(entry.getValue()))) {
                                    String timetest =(convertDateToTime12hour(entry.getValue()).toUpperCase());
                                    txt_time.setText(convertDateToTime12hour(entry.getValue()).toUpperCase());
                                    break;  // Exit the loop once the next prayer time is found.
                                }
                            }
                        } catch (JSONException | ParseException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(Dashboard.this, "Error retrieving data.", Toast.LENGTH_SHORT).show(); // A simple error handling
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        Volley.newRequestQueue(this).add(jsonReq);
    }*/

    public void getLocation() {
        gpsTracker = new GpsTracker(getApplication());
        if (gpsTracker.canGetLocation()) {
            Log.d(TAG, "getLocation: " + gpsTracker.getLatitude() + " " + gpsTracker.getLongitude());
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

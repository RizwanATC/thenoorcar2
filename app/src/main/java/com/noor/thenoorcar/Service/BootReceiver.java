package com.noor.thenoorcar.Service;

import static android.content.ContentValues.TAG;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.noor.thenoorcar.Function.GpsTracker;
import com.noor.thenoorcar.URL.Url;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BootReceiver extends BroadcastReceiver {

    private RequestQueue mRequestQueue;
    private GpsTracker gpsTracker;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            mRequestQueue = Volley.newRequestQueue(context);
            getLocation(context);
        }
    }

    private void fetchJsonData(Context context, double latitude, double longitude) {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                Url.url_thenoor_app + "solat/prayer_times/by_coordinates?coordinates=" + latitude + "," + longitude + "&period=today",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray array_waktu_solat = new JSONArray(response.getString("hydra:member"));
                            for (int i = 0; i < array_waktu_solat.length(); i++) {
                                JSONObject prayerTime_obj = array_waktu_solat.getJSONObject(i);
                                schedulePrayerAlarm(context, "Fajr", prayerTime_obj.getString("subuh"));
                                schedulePrayerAlarm(context, "Dhuhr", prayerTime_obj.getString("zohor"));
                                schedulePrayerAlarm(context, "Asr", prayerTime_obj.getString("asar"));
                                schedulePrayerAlarm(context, "Maghrib", prayerTime_obj.getString("maghrib"));
                                schedulePrayerAlarm(context, "Isha", prayerTime_obj.getString("isyak"));
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: " + error.getMessage());
                    }
                });

        mRequestQueue.add(request);
    }

    private void schedulePrayerAlarm(Context context, String prayerName, String prayerTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            Date date = sdf.parse(prayerTime);
            if (date != null && date.getTime() > System.currentTimeMillis()) {
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(context, AlarmReceiver.class);
                intent.putExtra("prayer", prayerName);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, prayerName.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                if (alarmManager != null) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, date.getTime(), pendingIntent);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void getLocation(Context context) {
        gpsTracker = new GpsTracker(context);
        if (gpsTracker.canGetLocation()) {
            fetchJsonData(context, gpsTracker.getLatitude(), gpsTracker.getLongitude());
        } else {
            gpsTracker.showSettingsAlert();
        }
    }
}

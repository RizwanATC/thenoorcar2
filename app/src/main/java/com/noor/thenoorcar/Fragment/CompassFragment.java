package com.noor.thenoorcar.Fragment;

import static com.android.volley.Request.Method.GET;
import static com.android.volley.Request.Method.POST;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.noor.thenoorcar.Adapter.MosqueAdapter;
import com.noor.thenoorcar.Class.Compass;
import com.noor.thenoorcar.Class.MosqueClass;
import com.noor.thenoorcar.Common.PreferenceManagerMosqueLocation;
import com.noor.thenoorcar.Dashboard;
import com.noor.thenoorcar.DashboardMain;
import com.noor.thenoorcar.Function.GpsTracker;
import com.noor.thenoorcar.Function.ScreenUtils;
import com.noor.thenoorcar.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CompassFragment extends Fragment {
    public static Fragment newInstance(Bundle args) {
        CompassFragment frag = new CompassFragment();
        frag.setArguments(args);
        return  frag;
    }
    TextView textView2;
    public static TextView textView_compass_status;
    ImageView imageView_qiblat,image_dial_compass_two;
    public static Compass compass;
    private float currentAzimuth;
    TextView textView_degree,textView_location,textView_degree_current;
    String apiKey;
    RecyclerView rv;
    private List<MosqueClass> mosqueList;
    private MosqueAdapter mosqueAdapter;

    private float qiblatDarjah;
    int radius = 5000; //METER
    Context settingCtx;
    public static SensorManager mSensorManager;
    public static Sensor mLight;
    public static AlertDialog.Builder filterBuilderCalibrate;
    public static AlertDialog calibrateDialog;
    TextView textView_title,textView_title_2,textView_v_1,textView_v_2;
    RelativeLayout rotate;
    ImageView icon_menu_back;




    public static SensorEventListener mLightSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            switch (accuracy) {
                case 0:
                    calibrateDialog.show();
                    break;
                case 1:
                    calibrateDialog.show();
                    break;
                case 2:
                    calibrateDialog.dismiss();
                    break;
                case 3:
                    calibrateDialog.dismiss();
                    break;
            }
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Point screenSize = ScreenUtils.getScreenSize(getActivity());
        int screenWidth = screenSize.x;
        int screenHeight = screenSize.y;
        int layoutResId = (screenWidth == 1920) ? R.layout.fragment_compass : R.layout.fragment_compass_large;

        View v = inflater.inflate(layoutResId, container, false);

        LinearLayout my3dLayout = v.findViewById(R.id.my3dLayout);
        my3dLayout.setCameraDistance(8000); // Increase camera distance to enhance the 3D effect
        my3dLayout.setRotationX(50);


        imageView_qiblat = v.findViewById(R.id.image_dial_compass);
        image_dial_compass_two = v.findViewById(R.id.image_dial_compass_two);
        rotate = v.findViewById(R.id.rotate);
        textView_location = v.findViewById(R.id.textView_location_compass);
        textView_degree = v.findViewById(R.id.textView_degree_compass);
        textView_degree_current = v.findViewById(R.id.textView_degree_current);
        icon_menu_back = v.findViewById(R.id.icon_menu_back);
        icon_menu_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Dashboard.class);
                startActivity(intent);

            }
        });

        getLocation();


        return v;
    }


    @Override
    public void onResume() {
        super.onResume();
        PackageManager manager = getActivity().getPackageManager();
        boolean hasCompass = manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_COMPASS);
        if (!hasCompass)
        {

        }else{

            if(compass !=null){
                compass.start();
            }
        }






    }

    @Override
    public void onStop() {
        super.onStop();
        if(compass !=null){
            compass.stop();
        }
    }

    public void getLocationAsk() {
        GpsTracker gpsTracker = new GpsTracker(getActivity());
        if (gpsTracker.canGetLocation()) {

            if(gpsTracker.getLatitude() == 0 && gpsTracker.getLongitude() == 0){
                Toast.makeText(getActivity(),"Unable to get GPS position",Toast.LENGTH_SHORT).show();
            }else{
                calculateQiblat(gpsTracker.getLatitude(),gpsTracker.getLongitude());
                getLocationDetails(gpsTracker.getLatitude(),gpsTracker.getLongitude());
                /*   getListOfMasjid(gpsTracker.getLatitude(),gpsTracker.getLongitude());*/
            }

        } else {
            gpsTracker.showSettingsAlert();
        }
    }

    public void getLocation() {


        PreferenceManagerMosqueLocation sessionMosque = new PreferenceManagerMosqueLocation(getActivity());

        if(sessionMosque.getUserDetails().get(PreferenceManagerMosqueLocation.KEY_ADDRESS) == null){
            GpsTracker gpsTracker = new GpsTracker(getActivity());
            if (gpsTracker.canGetLocation()) {
                if(gpsTracker.getLatitude() == 0 && gpsTracker.getLongitude() == 0){
                    calculateQiblat(3.170820,101.702187);
                    getLocationDetails(3.170820,101.702187);
                    /* getListOfMasjid(3.170820,101.702187);*/
                }else{
                    calculateQiblat(gpsTracker.getLatitude(),gpsTracker.getLongitude());
                    getLocationDetails(gpsTracker.getLatitude(),gpsTracker.getLongitude());
                    /* getListOfMasjid(gpsTracker.getLatitude(),gpsTracker.getLongitude());*/
                }

            } else {
                calculateQiblat(3.170820,101.702187);
                getLocationDetails(3.170820,101.702187);
                /*getListOfMasjid(3.170820,101.702187);*/
            }
        }else{

            String location = sessionMosque.getUserDetails().get(PreferenceManagerMosqueLocation.KEY_ADDRESS);
            String latitde = sessionMosque.getUserDetails().get(PreferenceManagerMosqueLocation.KEY_LATITUDE);
            String longitude = sessionMosque.getUserDetails().get(PreferenceManagerMosqueLocation.KEY_LONGITUDE);
            try {
                JSONObject object = new JSONObject(sessionMosque.getUserDetails().get(PreferenceManagerMosqueLocation.KEY_DETAILS));
                /* textView_location.setText(location);*/
                calculateQiblat(Double.parseDouble(latitde),Double.parseDouble(longitude));
                /* getListOfMasjidCache(object,latitde,longitude);*/
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }



    }

    private void getLocationDetails(double latitude, double longitude){
        SimpleDateFormat currentDates = new SimpleDateFormat("yyyy-MM-dd");
        Date todayDates = new Date();
        String date_send = currentDates.format(todayDates);

        final JSONObject jsonData = new JSONObject();
        JsonObjectRequest jsonReq = new JsonObjectRequest(
                GET,
                "https://latest.services.cloud.thenoor.co/app/solat/prayer_times/by_coordinates?coordinates="+latitude+"%2C%20"+longitude+"&period=today&date="+date_send,
                jsonData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            JSONArray array_waktu_solat = new JSONArray(jsonObject.getString("hydra:member"));
                            SimpleDateFormat currentDate = new SimpleDateFormat("HH:mm:ss");
                            Date todayDate = new Date();
                            String serverTime = currentDate.format(todayDate);
                            for (int i = 0 ; i < array_waktu_solat.length(); i++){
                                JSONObject prayerTime_obj = array_waktu_solat.getJSONObject(i);
                                /*   textView_location.setText(prayerTime_obj.getString("location")+", "+prayerTime_obj.getString("country"));*/
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                return super.getHeaders();
            }
        };
        Volley.newRequestQueue(getActivity()).add(jsonReq);
    }

    private void getKey(double latitude, double longitude){
        StringRequest stringRequest = new StringRequest(POST, "https://www.zeptopay.co/app/app-google-key",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            apiKey = object.getString("GOOGLE_ANDROID_KEY_LINE");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {

        };
        int socketTimeout = 50000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }






    private void setupCompass() {
        compass = new Compass(getActivity());
        compass.stop();
        Compass.CompassListener cl = new Compass.CompassListener() {
            @Override
            public void onNewAzimuth(float azimuth) {
                adjustArrow(azimuth);
            }
        };
        compass.setListener(cl);
    }

    int i = 1;

    private void adjustArrow(float azimuth) {
        Animation animator = new RotateAnimation(-(currentAzimuth)+qiblatDarjah, -azimuth,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        currentAzimuth = azimuth;
        textView_degree_current.setText(String.valueOf( "Current: "+Math.round(currentAzimuth))+"°");


        if(Math.round(currentAzimuth) == Math.round(qiblatDarjah)){

            if(i < 8){
                Vibrator v = (Vibrator) getActivity().getSystemService(Context. VIBRATOR_SERVICE ) ;
                assert v != null;
                // Vibrate for 500 milliseconds
                if (Build.VERSION. SDK_INT >= Build.VERSION_CODES. O ) {
                    v.vibrate(VibrationEffect. createOneShot ( 100 ,
                            VibrationEffect. DEFAULT_AMPLITUDE )) ;
                } else {
                    //deprecated in API 26
                    v.vibrate( 100 ) ;
                }
                textView_degree.setTextColor(Color.parseColor("#FFC500"));
                i++;
            }
        }else{
            i =0;
            textView_degree.setTextColor(Color.parseColor("#A990DD"));
        }



        animator.setDuration(500);
        animator.setRepeatCount(0);
        animator.setFillAfter(true);
        rotate.startAnimation(animator);

    }

    private void calculateQiblat(double latitude, double longitude){
        Location userLoc = new Location("service Provider");
        userLoc.setLongitude(longitude);
        userLoc.setLatitude(latitude);

        Location destinationLoc = new Location("service Provider");
        destinationLoc.setLatitude(21.4225); //kaaba latitude setting
        destinationLoc.setLongitude(39.8262); //kaaba longitude setting

        qiblatDarjah = userLoc.bearingTo(destinationLoc);

        // Adjust the bearing so that north is at 293 degrees
        // adding offset

        // Ensure the adjusted bearing remains within the range of 0 to 360


        if (qiblatDarjah < 0) {
            qiblatDarjah += 361;  // change 361 to 360
        }

        textView_degree.setText(String.valueOf("Qibla: " + Math.round(qiblatDarjah)) + "°");

        rotate.setRotation(Math.round(qiblatDarjah));
        /* image_dial_compass_two.setRotation(Math.round(qiblatDarjah));*/
        setupCompass();
    }

}
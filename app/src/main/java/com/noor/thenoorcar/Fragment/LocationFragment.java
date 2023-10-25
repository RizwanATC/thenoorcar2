package com.noor.thenoorcar.Fragment;

import static com.android.volley.Request.Method.GET;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.noor.thenoorcar.Adapter.MosqueAdapter;
import com.noor.thenoorcar.Class.MosqueClass;
import com.noor.thenoorcar.Dashboard;
import com.noor.thenoorcar.Function.GpsTracker;
import com.noor.thenoorcar.Function.ScreenUtils;
import com.noor.thenoorcar.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class LocationFragment extends Fragment {
    private List<MosqueClass> mosqueList;
    RecyclerView rv;
    private MosqueAdapter mosqueAdapter;
    int radius = 5000;
    ImageView icon_menu_back;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Point screenSize = ScreenUtils.getScreenSize(getActivity());
        int screenWidth = screenSize.x;
        int screenHeight = screenSize.y;
        int layoutResId = (screenWidth == 1920) ? R.layout.fragment_location : R.layout.fragment_location_large;

        View v = inflater.inflate(layoutResId, container, false);
        rv = v.findViewById(R.id.rv);
        icon_menu_back = v.findViewById(R.id.icon_menu_back);
        icon_menu_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Dashboard.class);
                startActivity(intent);

            }
        });
        getLocationAsk();

     return v;
    }

    public void getLocationAsk() {
        GpsTracker gpsTracker = new GpsTracker(getActivity());
        if (gpsTracker.canGetLocation()) {

            if(gpsTracker.getLatitude() == 0 && gpsTracker.getLongitude() == 0){
                Toast.makeText(getActivity(),"Unable to get GPS position",Toast.LENGTH_SHORT).show();
            }else{
                getListOfMasjid(gpsTracker.getLatitude(),gpsTracker.getLongitude());
            }

        } else {
            gpsTracker.showSettingsAlert();
        }
    }

    private void getListOfMasjidCache(JSONObject object, String latitude, String longitude) {

        rv.setNestedScrollingEnabled(false);

        mosqueList = new ArrayList<>();
        mosqueAdapter = new MosqueAdapter(mosqueList,getActivity());
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv.setAdapter(mosqueAdapter);

        try {
            JSONObject resulttt = new JSONObject(object.getString("results"));
            JSONArray results = new JSONArray(resulttt.getString("hydra:member"));

            if(results.length() == 0){
                Toast.makeText(getContext(),"No nearest mosque within 5 KM radius ",Toast.LENGTH_LONG).show();
            }else{
                /*loadingDialog.dismiss();*/
                for (int i = 0; i < results.length(); i++){
                    JSONObject object_result = results.getJSONObject(i);

                    JSONObject obj_geometry = new JSONObject(object_result.getString("geometry"));
                    JSONObject obj_location = new JSONObject(obj_geometry.getString("location"));
                    MosqueClass mosqueClass = new MosqueClass(
                            object_result.getString("name"),
                            object_result.getString("vicinity"),
                            String.valueOf(getKmFromLatLong(
                                    Float.parseFloat(String.valueOf(latitude)),
                                    Float.parseFloat(String.valueOf(longitude)),
                                    Float.parseFloat(obj_location.getString("lat")),
                                    Float.parseFloat(obj_location.getString("lng")))),
                            obj_location.getString("lat"),
                            obj_location.getString("lng")
                    );

                    mosqueList.add(mosqueClass);
                    Collections.sort(mosqueList, new Comparator<MosqueClass>() {
                        @Override
                        public int compare(MosqueClass o1, MosqueClass o2) {
                            return o1.getDistance().compareTo(o2.getDistance());
                        }
                    });
                }
                mosqueAdapter.notifyDataSetChanged();
                radius = 5000;
              /*  PreferenceManagerMosqueLocation sessionMosque = new PreferenceManagerMosqueLocation(getActivity());
                sessionMosque.createLoginSession(textView_location.getText().toString(),
                        String.valueOf(latitude),
                        String.valueOf(longitude),
                        object.toString());*/
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void getListOfMasjid(double latitude, double longitude) {

        rv.setNestedScrollingEnabled(false);

        mosqueList = new ArrayList<>();
        mosqueAdapter = new MosqueAdapter(mosqueList,getActivity());
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv.setAdapter(mosqueAdapter);

        StringRequest stringRequest = new StringRequest(GET, "https://latest.services.cloud.thenoor.co/places/nearby?location="+latitude+"%2C"+longitude+"&rankby=distance&type=mosque",
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            JSONObject resulttt = new JSONObject(object.getString("results"));
                            JSONArray results = new JSONArray(resulttt.getString("hydra:member"));

                            if(results.length() == 0){
                                Toast.makeText(getContext(),"No nearest mosque within 5 KM radius ",Toast.LENGTH_LONG).show();
                            }else{
                               /* loadingDialog.dismiss();*/
                                for (int i = 0; i < results.length(); i++){
                                    JSONObject object_result = results.getJSONObject(i);

                                    JSONObject obj_geometry = new JSONObject(object_result.getString("geometry"));
                                    JSONObject obj_location = new JSONObject(obj_geometry.getString("location"));
                                    MosqueClass mosqueClass = new MosqueClass(
                                            object_result.getString("name"),
                                            object_result.getString("vicinity"),
                                            String.valueOf(getKmFromLatLong(
                                                    Float.parseFloat(String.valueOf(latitude)),
                                                    Float.parseFloat(String.valueOf(longitude)),
                                                    Float.parseFloat(obj_location.getString("lat")),
                                                    Float.parseFloat(obj_location.getString("lng")))),
                                            obj_location.getString("lat"),
                                            obj_location.getString("lng")
                                    );

                                    mosqueList.add(mosqueClass);
                                    Collections.sort(mosqueList, new Comparator<MosqueClass>() {
                                        @Override
                                        public int compare(MosqueClass o1, MosqueClass o2) {
                                            return o1.getDistance().compareTo(o2.getDistance());
                                        }
                                    });
                                }
                                mosqueAdapter.notifyDataSetChanged();
                                radius = 5000;
                                /*PreferenceManagerMosqueLocation sessionMosque = new PreferenceManagerMosqueLocation(getActivity());*/
                              /*  sessionMosque.createLoginSession(textView_location.getText().toString(),
                                        String.valueOf(latitude),
                                        String.valueOf(longitude),
                                        object.toString());*/
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {

        };
        int socketTimeout = 50000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);

    }

    public static double getKmFromLatLong(double lat1, double lon1, double lat2,double lon2) {
        int EARTH_RADIUS_KM = 6371;
        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);
        double deltaLonRad = Math.toRadians(lon2 - lon1);

        double dist_travelled = Math
                .acos(Math.sin(lat1Rad) * Math.sin(lat2Rad) + Math.cos(lat1Rad)
                        * Math.cos(lat2Rad) * Math.cos(deltaLonRad))
                * EARTH_RADIUS_KM;

        return dist_travelled;

    }
}
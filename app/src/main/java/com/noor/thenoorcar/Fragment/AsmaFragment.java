package com.noor.thenoorcar.Fragment;


import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE;
import static com.android.volley.Request.Method.GET;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;


import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.noor.thenoorcar.Adapter.AsmaReciterAdapter;
import com.noor.thenoorcar.Adapter.adapterAsma;
import com.noor.thenoorcar.Class.AsmaClass;
import com.noor.thenoorcar.Class.AsmaReciterClass;
import com.noor.thenoorcar.Dashboard;
import com.noor.thenoorcar.Function.PreferenceLanguage;
import com.noor.thenoorcar.Function.PreferenceReciterAsma;
import com.noor.thenoorcar.Function.ScreenUtils;
import com.noor.thenoorcar.R;
import com.squareup.picasso.Picasso;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class AsmaFragment extends Fragment {


    public static RecyclerView rv;
    public static List<AsmaClass> menuList;
    public static adapterAsma mAdapter;
    public static int position_scroll = -1;
    public static TextView textView_text,textView_meaning,txt_number;
    public static ImageView imageView;
    public static ImageView imageView_play;
    SeekBar seekBar;
    public static MediaPlayer mediaAsma;
    public static int count_surah_read = 0;
    public static CountDownTimer yourCountDownTimer;
    public static int status_play = 0;
    public static boolean status_play_asma = false;
    LinearLayout linear_first,linear_second;
    public static String url = "";
    public static JSONArray time;
    boolean status_scroll = true;
    public static int length_time = 0;
    public static Handler handler_delay_scroll = new Handler();
    public static CircleImageView profile_image;

    public static Activity activity;

    public static AlertDialog.Builder reciterBuilder;

    public static AlertDialog reciterDialog;
    ImageView icon_menu_back;


    private AsmaReciterAdapter asmaReciterAdapter;
    private List<AsmaReciterClass> asmaReciterList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Point screenSize = ScreenUtils.getScreenSize(getActivity());
        int screenWidth = screenSize.x;
        int screenHeight = screenSize.y;
        int layoutResId = (screenWidth == 1920) ? R.layout.fragment_asma : R.layout.fragment_asma_large;

        View v = inflater.inflate(layoutResId, container, false);

        activity = getActivity();



        rv = v.findViewById(R.id.rv);
        textView_text = v.findViewById(R.id.textView_text);
        textView_meaning = v.findViewById(R.id.textView_meaning);
        txt_number = v.findViewById(R.id.txt_number);
        imageView = v.findViewById(R.id.imageView);
        imageView_play = v.findViewById(R.id.imageView_play);
        seekBar = v.findViewById(R.id.seekBar);
        /*linear_first = v.findViewById(R.id.linear_first);
        linear_second = v.findViewById(R.id.linear_second);*/
        profile_image = v.findViewById(R.id.profile_image);

        icon_menu_back = v.findViewById(R.id.icon_menu_back);
        icon_menu_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Dashboard.class);
                startActivity(intent);

            }
        });

        getReciterAsmaByDefault();

        position_scroll = -1;
        rv.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                int position = getCurrentItem();

                position_scroll = position;
                mAdapter.notifyDataSetChanged();

                menuList.get(position).getArabicName();
                textView_text.setText(menuList.get(position).getNameTranscription());
                textView_meaning.setText(menuList.get(position).getName());
                txt_number.setText(menuList.get(position).getNumber());


                for (int i =position+1 ; i < position+2; i++){
                    int id = getActivity().getResources().getIdentifier("asma_icon_"+i, "drawable", getActivity().getPackageName());
                    imageView.setImageResource(id);
                }

                if (newState == SCROLL_STATE_IDLE) {
                    status_scroll = false;
                    handler_delay_scroll.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            status_scroll = true;
                        }
                    }, 3000);

                }else{
                    status_scroll = false;
                    if(handler_delay_scroll != null){handler_delay_scroll.removeCallbacksAndMessages(null);}
                }

            }
        });

        imageView_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(status_play_asma == true){
                    status_play = 2;
                    mediaAsma.pause();
                    if(yourCountDownTimer != null){
                        yourCountDownTimer.cancel();
                    }
                    status_play_asma = false;
                    Drawable icon_change = getActivity().getResources().getDrawable(R.drawable.play_asma_icon);
                    imageView_play.setImageDrawable(icon_change);
                }else {
                    if(status_play == 0){



                        Drawable icon_change = getActivity().getResources().getDrawable(R.drawable.pause_asama_icon);
                        imageView_play.setImageDrawable(icon_change);
                        mediaAsma = new MediaPlayer();
                        try {
                            mediaAsma.setDataSource(url);
                            mediaAsma.prepareAsync();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        mediaAsma.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                mp.start();

                                yourCountDownTimer = new CountDownTimer(mediaAsma.getDuration(),100) {
                                    @Override
                                    public void onTick(long millisUntilFinished) {
                                        count_surah_read++;

                                        if(status_scroll == true){
                                            getTime();
                                        }

                                        float count = count_surah_read * 100;
                                        float total_percent = (count / mediaAsma.getDuration()) * 100;

                                        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                            @Override
                                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                                if(fromUser){
                                                    float total = (((Float.parseFloat(String.valueOf(progress))/100) * mediaAsma.getDuration()) / 1000);
                                                    int mili = (int) (1000 * total);
                                                    mediaAsma.seekTo(mili);
                                                    count_surah_read = (int) (mili/100);
                                                    yourCountDownTimer.cancel();
                                                    yourCountDownTimer.start();
                                                }
                                            }

                                            @Override
                                            public void onStartTrackingTouch(SeekBar seekBar) {
                                            }

                                            @Override
                                            public void onStopTrackingTouch(SeekBar seekBar) {
                                                getTimeSeekto();
                                            }
                                        });

                                        if(total_percent > 100){
                                            seekBar.setProgress(100);
                                            yourCountDownTimer.onFinish();
                                        }else{
                                            seekBar.setProgress((int) total_percent);
                                        }


                                    }
                                    @Override
                                    public void onFinish() {
                                        if(yourCountDownTimer !=null){
                                            yourCountDownTimer.cancel();
                                        }
                                        count_surah_read = 0;
                                        status_play = 0;
                                        status_play_asma = false;
                                        Drawable icon_change = getActivity().getResources().getDrawable(R.drawable.play_asma_icon);
                                        imageView_play.setImageDrawable(icon_change);
                                        seekBar.setProgress(0);


                                        position_scroll = -1;
                                        mAdapter.notifyDataSetChanged();

                                        ((LinearLayoutManager) rv.getLayoutManager()).scrollToPositionWithOffset(0,0);

                                        textView_text.setText("Allah");

                                        Drawable icon = getActivity().getResources().getDrawable(R.drawable.asma_icon_0);
                                        imageView.setImageDrawable(icon);

                                        length_time = 0;
                                    }
                                }.start();


                            }
                        });

                        status_play = 1;
                    }else if(status_play == 2){
                        status_play = 1;
                        mediaAsma.start();
                        yourCountDownTimer.start();
                        Drawable icon_change = getActivity().getResources().getDrawable(R.drawable.pause_asama_icon);
                        imageView_play.setImageDrawable(icon_change);
                    }
                    status_play_asma = true;
                }
            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reciterBuilder = new AlertDialog.Builder(getContext(),R.style.Theme_Thenoorcar);
                LayoutInflater inflater = getLayoutInflater();
                View dialoglayout = inflater.inflate(R.layout.layout_asma_reciter_v2, null);
                reciterBuilder.setView(dialoglayout);
                reciterDialog = reciterBuilder.create();
                reciterDialog.setCancelable(true);

                ViewPager viewPager = dialoglayout.findViewById(R.id.viewPager);
                DotsIndicator dotsIndicator = dialoglayout.findViewById(R.id.dots_indicator);
                getReciterAsma(viewPager,dotsIndicator);
            }
        });

        profile_image.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP){
                    reciterBuilder = new AlertDialog.Builder(getContext(),R.style.Theme_Thenoorcar);
                    LayoutInflater inflater = getLayoutInflater();
                    View dialoglayout = inflater.inflate(R.layout.layout_asma_reciter_v2, null);
                    reciterBuilder.setView(dialoglayout);
                    reciterDialog = reciterBuilder.create();
                    reciterDialog.setCancelable(true);

                    ViewPager viewPager = dialoglayout.findViewById(R.id.viewPager);
                    DotsIndicator dotsIndicator = dialoglayout.findViewById(R.id.dots_indicator);
                    getReciterAsma(viewPager,dotsIndicator);
                    return true;
                }
                return false;
            }
        });

        return v;
    }

    private void getTime(){
        for (int i =length_time; i < length_time+1; i++){
            JSONObject obj = null;
            try {
                obj = time.getJSONObject(length_time);

                if(!obj.getString("endInSeconds").equals("null")){
                    double end_postion = Double.parseDouble(obj.getString("endInSeconds")) * 1000;

                    if(mediaAsma.getCurrentPosition() >= end_postion){
                        length_time = length_time +1;
                    }else{

                        if(menuList.size()!=0){
                            menuList.get(length_time).getArabicName();
                            textView_text.setText(menuList.get(length_time).getNameTranscription());
                            textView_meaning.setText(menuList.get(length_time).getName());
                            txt_number.setText(menuList.get(length_time).getNumber());

                        }


                        for (int ii =length_time+1 ; ii < length_time+2; ii++){
                            int id = getActivity().getResources().getIdentifier("asma_icon_"+ii, "drawable", getActivity().getPackageName());
                            imageView.setImageResource(id);
                        }

                        ((LinearLayoutManager) rv.getLayoutManager()).scrollToPositionWithOffset(length_time,0);
                        position_scroll = length_time;
                        mAdapter.notifyDataSetChanged();
                    }
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void getTimeSeekto(){
        for (int i =0; i < length_time; i++){
            JSONObject obj = null;
            try {
                obj = time.getJSONObject(i);

                if(!obj.getString("endInSeconds").equals("null")){
                    double startInSeconds = Double.parseDouble(obj.getString("startInSeconds")) * 1000;
                    double end_postion = Double.parseDouble(obj.getString("endInSeconds")) * 1000;

                    if(mediaAsma.getCurrentPosition() >= startInSeconds && mediaAsma.getCurrentPosition() <= end_postion){
                        length_time = i;
                        break;
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static void getDetails(){
        menuList = new ArrayList<>();
        mAdapter = new adapterAsma(rv,menuList, activity, new adapterAsma.onClickJobByMonth() {
            @Override
            public void onClick(AsmaClass jobByMonthClass) {
                position_scroll = Integer.parseInt(jobByMonthClass.getNumber())-1;
                mAdapter.notifyDataSetChanged();

                menuList.get(Integer.parseInt(jobByMonthClass.getNumber())-1).getArabicName();
                textView_text.setText(menuList.get(Integer.parseInt(jobByMonthClass.getNumber())-1).getNameTranscription());
                textView_meaning.setText(menuList.get(Integer.parseInt(jobByMonthClass.getNumber())-1).getName());
                txt_number.setText(menuList.get(Integer.parseInt(jobByMonthClass.getNumber())-1).getNumber());


                for (int i =position_scroll+1 ; i < position_scroll+2; i++){
                    int id = activity.getResources().getIdentifier("asma_icon_"+i, "drawable",activity.getPackageName());
                    imageView.setImageResource(id);
                }

            }
        });
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(horizontalLayoutManager);
        rv.setAdapter(mAdapter);

        PreferenceLanguage languageSession = new PreferenceLanguage(activity);
        String code_language = languageSession.getUserDetails().get(PreferenceLanguage.KEY_LANGUAGE);


        PreferenceReciterAsma asmaSession = new PreferenceReciterAsma(activity);
        String id_reciter = asmaSession.getUserDetails().get(PreferenceReciterAsma.KEY_ID);

        final JSONObject jsonData = new JSONObject();
        JsonObjectRequest jsonReq = new JsonObjectRequest(
                GET,
                "https://latest.services.cloud.thenoor.co/app/asmaul_husna?recitalId="+id_reciter+"&locale="+code_language,
                jsonData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            JSONArray array = new JSONArray(jsonObject.getString("hydra:member"));
                            time = array;
                            for (int i =0; i < array.length(); i++){
                                JSONObject obj = array.getJSONObject(i);

                                AsmaClass reciterClass = new AsmaClass(
                                        obj.getString("id"),
                                        obj.getString("number"),
                                        obj.getString("nameTranscription"),
                                        obj.getString("arabicName"),
                                        obj.getString("name"),
                                        -1,
                                        obj.getString("imageUrl")
                                );
                                menuList.add(reciterClass);
                            }
                            mAdapter.notifyDataSetChanged();
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

        new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        Volley.newRequestQueue(activity).add(jsonReq);
    }

    private int getCurrentItem(){
        return ((LinearLayoutManager)rv.getLayoutManager())
                .findFirstVisibleItemPosition();
    }

    @Override
    public void onPause() {
        super.onPause();

        if(status_play_asma == true){
            mediaAsma.stop();
            status_play_asma = false;
            if(yourCountDownTimer !=null){
                yourCountDownTimer.cancel();
            }
            count_surah_read = 0;
            Drawable icon_change = getActivity().getResources().getDrawable(R.drawable.play_asma_icon);
            imageView_play.setImageDrawable(icon_change);
            status_play = 0;
            seekBar.setProgress(0);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(status_play_asma == true){
            mediaAsma.stop();
            status_play_asma = false;
            if(yourCountDownTimer !=null){
                yourCountDownTimer.cancel();
            }
            /*HadithDetailsFragment.count_surah_read = 0;*/
            Drawable icon_change = getActivity().getResources().getDrawable(R.drawable.play_asma_icon);
            imageView_play.setImageDrawable(icon_change);
            status_play = 0;
            seekBar.setProgress(0);
        }
    }

    private void getReciterAsmaByDefault(){

        PreferenceLanguage languageSession = new PreferenceLanguage(getContext());
        String code_language = languageSession.getUserDetails().get(PreferenceLanguage.KEY_LANGUAGE);

        PreferenceReciterAsma session_asma = new PreferenceReciterAsma(getActivity());
        if(session_asma.getUserDetails().get(PreferenceReciterAsma.KEY_ID) == null){
            final JSONObject jsonData = new JSONObject();
            JsonObjectRequest jsonReq = new JsonObjectRequest(
                    GET,
                    "https://latest.services.cloud.thenoor.co/app/asmaul_husna/recital?page=1&itemsPerPage=50&locale="+code_language,
                    jsonData,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            try {
                                JSONArray array = new JSONArray(jsonObject.getString("hydra:member"));
                                for (int i =0; i < 1; i++){
                                    JSONObject obj = array.getJSONObject(i);
                                    session_asma.createLoginSession(obj.getString("id"));
                                    Picasso.get().load(obj.getString("imageUrl")).into(profile_image);
                                    url = obj.getString("fileUrl");
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
                    headers.put("Content-Type", "application/ld+json");
                    return super.getHeaders();
                }
            };

            new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            Volley.newRequestQueue(getActivity()).add(jsonReq);
        }else {

            String reciter_asma = session_asma.getUserDetails().get(PreferenceReciterAsma.KEY_ID);
            final JSONObject jsonData = new JSONObject();
            JsonObjectRequest jsonReq = new JsonObjectRequest(
                    GET,
                    "https://latest.services.cloud.thenoor.co/app/asmaul_husna/recital?page=1&itemsPerPage=50&locale="+code_language,
                    jsonData,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            try {
                                JSONArray array = new JSONArray(jsonObject.getString("hydra:member"));
                                for (int i =0; i < array.length(); i++){
                                    JSONObject obj = array.getJSONObject(i);

                                    if(obj.getString("id").equals(reciter_asma)){
                                        session_asma.createLoginSession(obj.getString("id"));
                                        Picasso.get().load(obj.getString("imageUrl")).into(profile_image);
                                        url = obj.getString("fileUrl");
                                        break;
                                    }

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
                    headers.put("Content-Type", "application/ld+json");
                    return super.getHeaders();
                }
            };

            new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            Volley.newRequestQueue(getActivity()).add(jsonReq);
        }

        getDetails();


    }

    private void getReciterAsma(ViewPager viewPager,DotsIndicator dotsIndicator){
        asmaReciterList = new ArrayList<>();
        PreferenceLanguage languageSession = new PreferenceLanguage(getContext());
        String code_language = languageSession.getUserDetails().get(PreferenceLanguage.KEY_LANGUAGE);
        PreferenceReciterAsma session_asma = new PreferenceReciterAsma(getActivity());
        String reciter_asma_id = session_asma.getUserDetails().get(PreferenceReciterAsma.KEY_ID);
        final JSONObject jsonData = new JSONObject();
        JsonObjectRequest jsonReq = new JsonObjectRequest(
                GET,
                "https://latest.services.cloud.thenoor.co/app/asmaul_husna/recital?page=1&itemsPerPage=50&locale="+code_language,
                jsonData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            JSONArray array = new JSONArray(jsonObject.getString("hydra:member"));
                            for (int i =0; i < array.length(); i++){
                                JSONObject obj = array.getJSONObject(i);

                                if(reciter_asma_id.equals(obj.getString("id"))){
                                    AsmaReciterClass asmaClass = new AsmaReciterClass(
                                            obj.getString("@id"),
                                            obj.getString("@type"),
                                            obj.getString("id"),
                                            obj.getString("name"),
                                            obj.getString("description"),
                                            obj.getString("imageUrl"),
                                            obj.getString("isDefaultRecital"),
                                            obj.getString("fileUrl"),
                                            true
                                    );
                                    asmaReciterList.add(asmaClass);
                                }else {
                                    AsmaReciterClass asmaClass = new AsmaReciterClass(
                                            obj.getString("@id"),
                                            obj.getString("@type"),
                                            obj.getString("id"),
                                            obj.getString("name"),
                                            obj.getString("description"),
                                            obj.getString("imageUrl"),
                                            obj.getString("isDefaultRecital"),
                                            obj.getString("fileUrl"),
                                            false
                                    );
                                    asmaReciterList.add(asmaClass);
                                }

                            }
                            asmaReciterAdapter = new AsmaReciterAdapter(asmaReciterList, getActivity());
                            viewPager.setAdapter(asmaReciterAdapter);
                            dotsIndicator.setViewPager(viewPager);
                            reciterDialog.show();
                            viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                                private int mCurrentSelectedScreen;
                                private int mNextSelectedScreen;
                                private static final float thresholdOffset = 0.5f;
                                private boolean scrollStarted=true, checkDirection=false;
                                ArrayList<Integer> comp_ary=new ArrayList<Integer>();

                                @Override
                                public void onPageSelected(int arg0) {
                                }

                                @Override
                                public void onPageScrolled(int position, float positionOffset,
                                                           int positionOffsetPixels) {


                                }

                                @Override
                                public void onPageScrollStateChanged(int arg0) {
                                    if (!scrollStarted && arg0 == ViewPager.SCROLL_STATE_SETTLING) {
                                        scrollStarted = true;
                                        checkDirection = true;
                                    } else {
                                        scrollStarted = false;
                                    }
                                }
                            });
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
                headers.put("Content-Type", "application/ld+json");
                return super.getHeaders();
            }
        };

        new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        Volley.newRequestQueue(getActivity()).add(jsonReq);
    }
}
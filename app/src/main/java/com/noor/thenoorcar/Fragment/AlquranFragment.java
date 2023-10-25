package com.noor.thenoorcar.Fragment;


import static android.content.ContentValues.TAG;
import static com.android.volley.Request.Method.GET;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.noor.thenoorcar.Adapter.ReciterAdapter;
import com.noor.thenoorcar.Adapter.SurahAdapter;
import com.noor.thenoorcar.Class.LoadingDialog;
import com.noor.thenoorcar.Class.ReciterClass;
import com.noor.thenoorcar.Class.SurahClass;
import com.noor.thenoorcar.Common.PreferenceManagerReciter;
import com.noor.thenoorcar.Dashboard;
import com.noor.thenoorcar.DashboardMain;
import com.noor.thenoorcar.Function.ScreenUtils;
import com.noor.thenoorcar.R;
import com.squareup.picasso.Picasso;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import de.hdodenhof.circleimageview.CircleImageView;
import wseemann.media.FFmpegMediaPlayer;





/**
 * A simple {@link Fragment} subclass.
 */
public class AlquranFragment extends Fragment {

    private TextView textView_title;

    public static Fragment newInstance(Bundle args) {
        AlquranFragment frag = new AlquranFragment();
        frag.setArguments(args);
        return frag;
    }
    Handler handler = new Handler();
    public static int surah_play_now_auto_play = -1;
    public static RecyclerView rv;
    public static List<SurahClass> menuList;
    public static SurahAdapter mAdapter;
    public static SearchView sv;
    public static TextView textView_surah_name;
    public static TextView textView_surah_arabic;
    public static TextView textView_audio_length;
    public static TextView textView_receiter_name;
    public static TextView textView_begining;
    public static LoadingDialog loadingDialog;
    public static String receiter_id = "";
    public static MediaPlayer mPlayer;
    public static FFmpegMediaPlayer mp;
    public static String status_play_surah = "0";
    public static String copy_id_surah = "0";
    public static String current_play_surah_string = "";
    public static int which_current_play = -1;
    public static int current_play_position = -1;
    public static int count_surah_read = 0;
    public static int count_global = 0;
    NestedScrollView ns;
    public static CountDownTimer yourCountDownTimer;
    public static ImageView imageView_forward, imageView_backward, imageView_play_pause;
    public static int surah_play_right_now = -1;
    public static String image_profile_receiter, receiter_name, surah_name, surah_length, surah_arabic;
    public static CircleImageView profile_image;
    public static String mp3;
    //RECITER DIALOG
    AlertDialog.Builder reciterBuilder;
    AlertDialog reciterDialog;
    RecyclerView rv_reciter;
    private List<ReciterClass> reciterList;
    public static ReciterAdapter reciterAdapter;
    public static String surah_play_for_change_reciter = "";

    ImageView imageView_repeated, imageView_shuffle;
    public static String status_repeated = "0";
    public static String status_shuffle = "0";
    public static Context AlquranFragmentCtx;
    public static SeekBar seekBar;
    PreferenceManagerReciter sessionReciter;
    public static SimpleExoPlayer exoPlayer;
    public static Activity activitys;

    public static String status_play_radio;

    LinearLayout linear_player, linear_surah, back_surah, click_me;
    RelativeLayout bg_shade;
    /*LottieAnimationView lottieAnimationView_wave, lottieAnimationView_wave2;*/
    String statusPlayAnimation = "";

    ImageView icon_menu_back;
    private static final int FORWARD_DELAY = 700;
    private static final int BACKWARD_DELAY = 500;
    private static final int MAX_SURAH_INDEX = 113;






    public static LinearLayout linear_box_playlist;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Point screenSize = ScreenUtils.getScreenSize(getActivity());
        int screenWidth = screenSize.x;
        int screenHeight = screenSize.y;
        int layoutResId = (screenWidth == 1920) ? R.layout.fragment_alquran : R.layout.fragment_alquran_large;

        View v = inflater.inflate(layoutResId, container, false);
        activitys = getActivity();



        sessionReciter = new PreferenceManagerReciter(getActivity());
        AlquranFragmentCtx = getActivity();

        //FRAGMENT DECLARE
        seekBar = v.findViewById(R.id.seekBar);
        rv = v.findViewById(R.id.rv);
        sv = v.findViewById(R.id.sv);
        textView_receiter_name = v.findViewById(R.id.textView_receiter_name);
        textView_surah_name = v.findViewById(R.id.textView_surah_name);
        textView_surah_arabic = v.findViewById(R.id.textView_surah_arabic);
        textView_audio_length = v.findViewById(R.id.textView_audio_length);
        textView_begining = v.findViewById(R.id.textView_begining);
        /*bg_shade = v.findViewById(R.id.bg_shade);*/
        imageView_forward = v.findViewById(R.id.imageView_forward);
        imageView_backward = v.findViewById(R.id.imageView_backward);
        imageView_play_pause = v.findViewById(R.id.imageView_play_pause);
        profile_image = v.findViewById(R.id.profile_image);
        imageView_repeated = v.findViewById(R.id.imageView_repeated);
        imageView_shuffle = v.findViewById(R.id.imageView_shuffle);
        linear_player = v.findViewById(R.id.linear_player);
        linear_surah = v.findViewById(R.id.linear_surah);
        icon_menu_back = v.findViewById(R.id.icon_menu_back);
        click_me = v.findViewById(R.id.click_me);


        linear_surah.setVisibility(View.GONE);
       /* bg_shade.setVisibility(View.GONE);*/
        //END DECLARE LOADING
        click_me.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                linear_surah.setVisibility(View.VISIBLE);

            }


        });

        icon_menu_back = v.findViewById(R.id.icon_menu_back);
        icon_menu_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Dashboard.class);
                startActivity(intent);

            }
        });





        loadingDialog = new LoadingDialog(getContext());
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        linear_surah.setVisibility(View.GONE);

        reciterBuilder = new AlertDialog.Builder(getActivity(), R.style.dialogReciterPlayer);
        ViewGroup viewGroup = v.findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.dialog_receiter, viewGroup, false);

        textView_title = dialogView.findViewById(R.id.textView_title);
        rv_reciter = dialogView.findViewById(R.id.rv_reciter_list);


        rv_reciter.setNestedScrollingEnabled(false);
        rv_reciter.setHasFixedSize(false);
        reciterList = new ArrayList<>();
        reciterAdapter = new ReciterAdapter(reciterList, getActivity(), jobByMonthClass -> {

            sessionReciter.createLoginSession(jobByMonthClass.getId(), jobByMonthClass.getName(), jobByMonthClass.getImage_url());
            image_profile_receiter = jobByMonthClass.getImage_url();
            Picasso.get().load(image_profile_receiter).into(profile_image);
            receiter_id = jobByMonthClass.getId(); // RECITER ID;
            textView_receiter_name.setText(jobByMonthClass.getName());
            receiter_name = jobByMonthClass.getName();
            if (mPlayer != null && mPlayer.isPlaying()) {
                mPlayer.stop();
                status_play_surah = "0";
            }
            if (yourCountDownTimer != null) {
                yourCountDownTimer.cancel();
            }
            if (!surah_play_for_change_reciter.equals("")) {
                status_play_surah = "0";
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((LinearLayoutManager) rv.getLayoutManager()).scrollToPositionWithOffset(surah_play_right_now, 0);
                        ImageView imageView_plays = null;
                        LinearLayout linear_selected_surah = null;
                        View views = null;
                        if (rv.findViewHolderForAdapterPosition(surah_play_right_now) == null) {
                        } else {
                            views = rv.findViewHolderForAdapterPosition(surah_play_right_now).itemView;
                            imageView_plays = views.findViewById(R.id.imageView_play);
                            linear_selected_surah = views.findViewById(R.id.linear_selected_surah);
                            Drawable pause_icon = AlquranFragmentCtx.getResources().getDrawable(R.drawable.pause_icon);
                            imageView_plays.setImageDrawable(pause_icon);
                            imageView_plays.setVisibility(View.VISIBLE);
                            linear_selected_surah.setVisibility(View.VISIBLE);
                        }
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                PlaySurah(surah_play_for_change_reciter);
                            }
                        }, 500);
                    }
                }, 700);

            }

            reciterDialog.dismiss();
            reciterAdapter.notifyDataSetChanged();
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mPlayer != null && fromUser) {
                    int duration = mPlayer.getDuration();
                    int newPosition = (int) ((duration / 100.0) * progress);
                    mPlayer.seekTo(newPosition);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Optional: Implement if needed
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Optional: Implement if needed
            }
        });



        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rv_reciter.setLayoutManager(horizontalLayoutManager);
        rv_reciter.setAdapter(reciterAdapter);
        reciterBuilder.setView(dialogView);
        reciterDialog = reciterBuilder.create();
        reciterDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        rv.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                int action = e.getAction();
                switch (action) {
                    case MotionEvent.ACTION_MOVE:
                        rv.getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        sv.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        sv.setMaxWidth(Integer.MAX_VALUE);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                mAdapter.getFilter().filter(query);
                return false;
            }

        });

        imageView_forward.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {

                return true;
            }
        });

        imageView_forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (surah_play_right_now != -1 && surah_play_right_now != MAX_SURAH_INDEX) {
                    status_play_surah = "3";
                    sv.setQuery("", false);
                    sv.clearFocus();

                    new Handler().postDelayed(() -> {
                        ((LinearLayoutManager) rv.getLayoutManager()).scrollToPositionWithOffset(surah_play_right_now + 1, 0);
                        performActionIfViewHolderExists(surah_play_right_now, view -> {
                            view.findViewById(R.id.imageView_play).setVisibility(View.INVISIBLE);
                            view.findViewById(R.id.linear_selected_surah).setVisibility(View.INVISIBLE);
                        });

                        new Handler().postDelayed(() -> {
                            performActionIfViewHolderExists(surah_play_right_now + 1, View::performClick);
                        }, BACKWARD_DELAY);
                    }, FORWARD_DELAY);
                }
            }
        });

        imageView_backward.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {

                return true;
            }
        });

        imageView_backward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (surah_play_right_now > 0) {
                    status_play_surah = "4";
                    sv.setQuery("", false);
                    sv.clearFocus();

                    new Handler().postDelayed(() -> {
                        ((LinearLayoutManager) rv.getLayoutManager()).scrollToPositionWithOffset(surah_play_right_now - 1, 0);

                        new Handler().postDelayed(() -> {
                            performActionIfViewHolderExists(surah_play_right_now - 1, View::performClick);
                        }, BACKWARD_DELAY);
                    }, FORWARD_DELAY);
                }
            }
        });


        imageView_play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable big_icon;


                if (status_play_surah.equals("1")) {
                    if (yourCountDownTimer != null) {
                        yourCountDownTimer.cancel();
                    }
                    mPlayer.pause();
                    status_play_surah = "2";
                    big_icon = AlquranFragmentCtx.getResources().getDrawable(R.drawable.play_iconv2);
                } else if (status_play_surah.equals("2")) {
                    if (yourCountDownTimer != null) {
                        yourCountDownTimer.start();
                    }
                    mPlayer.start();
                    status_play_surah = "1";
                    big_icon = AlquranFragmentCtx.getResources().getDrawable(R.drawable.pause_icon);
                } else { // Assuming "0"
                    PlaySurah("1");
                    current_play_position = 0;

                    big_icon = AlquranFragmentCtx.getResources().getDrawable(R.drawable.pause_icon);
                }
                imageView_play_pause.setImageDrawable(big_icon);

                ((LinearLayoutManager) rv.getLayoutManager()).scrollToPositionWithOffset(surah_play_right_now, 0);
                new Handler().postDelayed(() -> {
                    performActionIfViewHolderExists(surah_play_right_now, view -> {
                        ImageView imageView_plays = view.findViewById(R.id.imageView_play);
                        LinearLayout linear_selected_surah = view.findViewById(R.id.linear_selected_surah);
                        imageView_plays.setImageDrawable(big_icon);
                        imageView_plays.setVisibility(View.VISIBLE);
                        linear_selected_surah.setVisibility(View.VISIBLE);
                    });
                }, FORWARD_DELAY);
            }
        });


        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                reciterDialog.show();
            }
        });

        imageView_repeated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status_shuffle = "0";
                Drawable big_icons = AlquranFragmentCtx.getResources().getDrawable(R.drawable.shuffle);
                imageView_shuffle.setImageDrawable(big_icons);
                if (status_repeated.equals("0")) {
                    status_repeated = "1";
                    Drawable big_icon = AlquranFragmentCtx.getResources().getDrawable(R.drawable.repeat_icon);
                    imageView_repeated.setImageDrawable(big_icon);
                } else {
                    status_repeated = "0";
                    Drawable big_icon = AlquranFragmentCtx.getResources().getDrawable(R.drawable.repeat_icon);
                    imageView_repeated.setImageDrawable(big_icon);
                }
            }
        });

        imageView_shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status_repeated = "0";
                Drawable big_icons = AlquranFragmentCtx.getResources().getDrawable(R.drawable.repeat_icon);
                imageView_repeated.setImageDrawable(big_icons);
                if (status_shuffle.equals("0")) {
                    status_shuffle = "1";
                    Drawable big_icon = AlquranFragmentCtx.getResources().getDrawable(R.drawable.shuffle);
                    imageView_shuffle.setImageDrawable(big_icon);
                } else {
                    status_shuffle = "0";
                    Drawable big_icon = AlquranFragmentCtx.getResources().getDrawable(R.drawable.shuffle);
                    imageView_shuffle.setImageDrawable(big_icon);
                }
            }
        });

        getAllSurah();

        //IF TGH PLAY
        if (receiter_name != null) {
            textView_receiter_name.setText(receiter_name);
        }

        if (surah_name != null) {
            textView_surah_name.setText(surah_name);
        }
        if (surah_arabic != null) {
            textView_surah_arabic.setText(surah_arabic);
        }


        if (image_profile_receiter != null) {
            Picasso.get().load(image_profile_receiter).into(profile_image);
        }

        if (surah_length != null) {
            textView_audio_length.setText(surah_length);
        } else {
            if (mPlayer != null) {
                String durationText = DateUtils.formatElapsedTime(mPlayer.getDuration() / 1000);
                textView_audio_length.setText(durationText);
            }
        }

        //REPEATED ICON
        if (status_repeated.equals("0")) {
            Drawable big_icon = AlquranFragmentCtx.getResources().getDrawable(R.drawable.repeat_icon);
            imageView_repeated.setImageDrawable(big_icon);
        } else {
            Drawable big_icon = AlquranFragmentCtx.getResources().getDrawable(R.drawable.repeat_icon);
            imageView_repeated.setImageDrawable(big_icon);
        }

        //SHUFFLE ICON
        if (status_shuffle.equals("0")) {
            Drawable big_icon = getContext().getResources().getDrawable(R.drawable.shuffle);
            imageView_shuffle.setImageDrawable(big_icon);
        } else {
            Drawable big_icon = AlquranFragmentCtx.getResources().getDrawable(R.drawable.shuffle);
            imageView_shuffle.setImageDrawable(big_icon);
        }

        if (status_play_surah.equals("1")) {
            Drawable big_icon = getContext().getResources().getDrawable(R.drawable.pause_icon);
            imageView_play_pause.setImageDrawable(big_icon);
        } else if (status_play_surah.equals("0") || status_play_surah.equals("2")) {
            Drawable big_icon = AlquranFragmentCtx.getResources().getDrawable(R.drawable.play_iconv2);
            imageView_play_pause.setImageDrawable(big_icon);
        }


        return v;
    }




    @Override
    public void onResume() {
        super.onResume();

        if(mAdapter != null){
            mAdapter.notifyDataSetChanged();
        }


    }




    Runnable updateSeekBar = new Runnable() {
        @Override
        public void run() {
            if (mPlayer != null) {
                try {
                    int mCurrentPosition = mPlayer.getCurrentPosition();
                    textView_begining.setText(milliSecondsToTimer(mCurrentPosition));

                    int duration = mPlayer.getDuration();
                    int progress = (int) ((mCurrentPosition / (float) duration) * 100);
                    seekBar.setProgress(progress);
                } catch (IllegalStateException e) {
                    // handle the exception, e.g., stop updating the UI
                    return;
                }

                handler.postDelayed(this, 1000);
            }
        }
    };

    private void getAllSurah(){
        menuList = new ArrayList<>();
        mAdapter = new SurahAdapter(rv,menuList, getActivity(), new SurahAdapter.onClickJobByMonth() {
            @Override
            public void onClick(SurahClass jobByMonthClass) {
                PlaySurah(jobByMonthClass.getId());
            }
        });
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(AlquranFragmentCtx, LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(horizontalLayoutManager);
        rv.setAdapter(mAdapter);
        final JSONObject jsonData = new JSONObject();
        JsonObjectRequest jsonReq = new JsonObjectRequest(
                GET,
                "https://latest.services.cloud.thenoor.co/app/quran/surahs?page=1&itemsPerPage=115",
                jsonData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {

                        try {
                            JSONArray array = new JSONArray(jsonObject.getString("hydra:member"));
                            for (int i =0; i < array.length(); i++){
                                JSONObject obj = array.getJSONObject(i);

                                SurahClass sessionClass = new SurahClass(
                                        obj.getString("@id"),
                                        obj.getString("@type"),
                                        obj.getString("id"),
                                        obj.getString("number"),
                                        "",
                                        obj.getString("arabicName"),
                                        obj.getString("name"),
                                        obj.getString("englishName"),
                                        obj.getString("haveBismillah"),
                                        "",
                                        obj.getString("totalVerses"),
                                        obj.getString("chineseName"),
                                        obj.getString("tamilName"),
                                        obj.getString("malayName"),
                                        obj.getString("indonesianName"),
                                        obj.getString("turkiName")
                                );
                                menuList.add(sessionClass);
                                mAdapter.notifyDataSetChanged();
                            }

                            if(mPlayer != null && mPlayer.isPlaying()){
                                if(which_current_play!= -1){
                                    ((LinearLayoutManager)rv.getLayoutManager()).scrollToPositionWithOffset(which_current_play,0);

                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            ImageView imageView_plays = null;
                                            LinearLayout linear_selected_surah = null;
                                            View views = null;
                                            Drawable big_icon = AlquranFragmentCtx.getResources().getDrawable(R.drawable.pause_icon);
                                            if(rv.findViewHolderForAdapterPosition(which_current_play) == null){
                                                views = rv.findViewHolderForAdapterPosition(which_current_play).itemView;
                                                imageView_plays = views.findViewById(R.id.imageView_play);
                                                linear_selected_surah = views.findViewById(R.id.linear_selected_surah);
                                                imageView_plays.setImageDrawable(big_icon);
                                                imageView_plays.setVisibility(View.VISIBLE);
                                                linear_selected_surah.setVisibility(View.VISIBLE);
                                            }else{
                                                views = rv.findViewHolderForAdapterPosition(which_current_play).itemView;
                                                imageView_plays = views.findViewById(R.id.imageView_play);
                                                linear_selected_surah = views.findViewById(R.id.linear_selected_surah);
                                                imageView_plays.setImageDrawable(big_icon);
                                                imageView_plays.setVisibility(View.VISIBLE);
                                                linear_selected_surah.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    }, 500);

                                }

                            }

                            HashMap<String, String> user = sessionReciter.getUserDetails();
                            getAllReciter();
                            if(user.get(sessionReciter.KEY_THEME_ID) == null){
                                getFirstReceiter();
                            }else{
                                /*   SurahFragment.reciter_id = user.get(sessionReciter.KEY_THEME_ID);*/
                                receiter_id = user.get(sessionReciter.KEY_THEME_ID);
                                receiter_name = user.get(sessionReciter.KEY_THEME_NAME);
                                image_profile_receiter = user.get(sessionReciter.KEY_THEME_IMAGE);
                                textView_receiter_name.setText(receiter_name);
                                Picasso.get().load(image_profile_receiter).into(profile_image);
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

        new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        Volley.newRequestQueue(AlquranFragmentCtx).add(jsonReq);
    }
    public void PlaySurah(String id) {
        final JSONObject jsonData = new JSONObject();
        JsonObjectRequest jsonReq = new JsonObjectRequest(
                GET,
                "https://latest.services.cloud.thenoor.co/app/quran/recitations/surahs?quranRecitation=" + receiter_id + "&quranSurah=" + id,
                jsonData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        surah_play_for_change_reciter = id;
                        surah_play_right_now = Integer.parseInt(id)-1;
                        surah_play_now_auto_play = Integer.parseInt(id);
                        try {
                            JSONArray array = new JSONArray(jsonObject.getString("hydra:member"));

                            if (array.length() == 0) {
                                loadingDialog.dismiss();
                                Toast.makeText(AlquranFragmentCtx, "Error while playing surah", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            JSONObject obj = array.getJSONObject(0); // Assuming you want the first surah in the list

                            String mp3;
                            if (obj.getString("alternativeContentUrl").equals("null")) {
                                mp3 = obj.getString("contentUrl");
                            } else {
                                mp3 = obj.getString("alternativeContentUrl");
                            }

                            playAudio(mp3);  // Call the method to play the audio

                            JSONObject objs = new JSONObject(obj.getString("quranSurah"));
                            textView_surah_name.setText(objs.getString("name"));
                            current_play_surah_string = objs.getString("name");
                            textView_surah_arabic.setText(objs.getString("arabicName"));
                            Log.d(TAG, "onResponse: "+receiter_id+id);


                            surah_arabic= objs.getString("arabicName");
                            surah_name = objs.getString("name");

                            if(status_play_surah.equals("0")){

                                Drawable big_icon = AlquranFragmentCtx.getResources().getDrawable(R.drawable.pause_icon);
                                imageView_play_pause.setImageDrawable(big_icon);
                                status_play_surah = "1";



                            }else if(status_play_surah.equals("1")){
                                if(copy_id_surah.equals(id)){
                                    loadingDialog.dismiss();
                                    Drawable big_icon = AlquranFragmentCtx.getResources().getDrawable(R.drawable.play_iconv2);
                                    imageView_play_pause.setImageDrawable(big_icon);

                                    if(mPlayer != null){
                                        playAudio(mp3);
                                    }


                                    status_play_surah = "2";
                                    copy_id_surah = id;
                                }else{

                                    Drawable big_icon = AlquranFragmentCtx.getResources().getDrawable(R.drawable.pause_icon);
                                    imageView_play_pause.setImageDrawable(big_icon);
                                    status_play_surah = "1";

                                    copy_id_surah = id;
                                    status_play_surah = "1";

                                }
                            }else if(status_play_surah.equals("2")){
                                if(copy_id_surah.equals(id)){
                                    loadingDialog.dismiss();
                                    Drawable big_icon = AlquranFragmentCtx.getResources().getDrawable(R.drawable.pause_icon);
                                    imageView_play_pause.setImageDrawable(big_icon);

                                    if(mPlayer != null){
                                        playAudio(mp3);
                                    }

                                    status_play_surah = "1";
                                }else{

                                    Drawable big_icon = AlquranFragmentCtx.getResources().getDrawable(R.drawable.pause_icon);
                                    imageView_play_pause.setImageDrawable(big_icon);
                                    status_play_surah = "1";

                                    copy_id_surah = id;
                                    status_play_surah = "1";

                                }

                            }else if(status_play_surah.equals("3")){
                                Drawable big_icon = AlquranFragmentCtx.getResources().getDrawable(R.drawable.pause_icon);
                                imageView_play_pause.setImageDrawable(big_icon);
                                copy_id_surah = id;

                                status_play_surah = "1";


                            }else if(status_play_surah.equals("4")){
                                Drawable big_icon = AlquranFragmentCtx.getResources().getDrawable(R.drawable.pause_icon);
                                imageView_play_pause.setImageDrawable(big_icon);
                                copy_id_surah = id;

                                status_play_surah = "1";
                                if(mPlayer!=null)mPlayer.release();


                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();
                    }
                }
        );

        Volley.newRequestQueue(AlquranFragmentCtx).add(jsonReq);
    }
    private void playAudio(String url) {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
        }

        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(url);
            mPlayer.prepareAsync();
            mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                    seekBar.setMax(100);
                    handler.postDelayed(updateSeekBar, 1000);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                seekBar.setMax(100);

                // Set total duration to TextView
                int duration = mPlayer.getDuration();
                textView_audio_length.setText(milliSecondsToTimer(duration));

                // Start updating the SeekBar and currentTimeTextView
                handler.postDelayed(updateSeekBar, 1000);
            }
        });


    }
    private void getFirstReceiter(){
        final JSONObject jsonData = new JSONObject();
        JsonObjectRequest jsonReq = new JsonObjectRequest(
                GET,
                "https://latest.services.cloud.thenoor.co/app/quran/recitations?page=1&itemsPerPage=100",
                jsonData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            JSONArray array = new JSONArray(jsonObject.getString("hydra:member"));
                            for (int i =0; i < 1; i++){
                                JSONObject obj = array.getJSONObject(i);

                                JSONObject reciter = new JSONObject(obj.getString("reciter"));
                                sessionReciter.createLoginSession(obj.getString("id"),reciter.getString("name"),reciter.getString("contentUrl"));
                                /*  SurahFragment.reciter_id = obj.getString("id");*/
                                receiter_id = obj.getString("id");
                                textView_receiter_name.setText(reciter.getString("name"));
                                /*SettingFragment.textview_default_reciter.setText(reciter.getString("name"));*/
                                receiter_name = reciter.getString("name");
                                image_profile_receiter = reciter.getString("contentUrl");
                                Picasso.get().load(image_profile_receiter).into(profile_image);

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

        new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        Volley.newRequestQueue(AlquranFragmentCtx).add(jsonReq);
    }
    private void getAllReciter(){
        if (reciterList !=null){
            reciterList.clear();
        }
        final JSONObject jsonData = new JSONObject();
        JsonObjectRequest jsonReq = new JsonObjectRequest(
                GET,
                "https://latest.services.cloud.thenoor.co/app/quran/recitations?page=1&itemsPerPage=100",
                jsonData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        loadingDialog.dismiss();
                        try {
                            JSONArray array = new JSONArray(jsonObject.getString("hydra:member"));
                            for (int i =0; i < array.length(); i++){
                                JSONObject obj = array.getJSONObject(i);

                                JSONObject reciter = new JSONObject(obj.getString("reciter"));
                                ReciterClass reciterClass = new ReciterClass(
                                        obj.getString("id"),
                                        reciter.getString("name"),
                                        reciter.getString("contentUrl")
                                );
                                reciterList.add(reciterClass);
                            }
                            reciterAdapter.notifyDataSetChanged();
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
        Volley.newRequestQueue(AlquranFragmentCtx).add(jsonReq);
    }

    public String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String minutesString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int)(milliseconds / (1000*60*60));
        int minutes = (int)(milliseconds % (1000*60*60)) / (1000*60);
        int seconds = (int)((milliseconds % (1000*60*60)) % (1000*60) / 1000);

        // Add hours if there are
        if(hours > 0){
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if(seconds < 10){
            secondsString = "0" + seconds;
        }else{
            secondsString = "" + seconds;
        }

        minutesString = (minutes < 10) ? "0" + minutes : "" + minutes;

        // Return the formatted string
        finalTimerString = finalTimerString + minutesString + ":" + secondsString;

        return finalTimerString;
    }

    private void performActionIfViewHolderExists(int position, Consumer<View> action) {
        RecyclerView.ViewHolder viewHolder = rv.findViewHolderForAdapterPosition(position);
        if (viewHolder != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                action.accept(viewHolder.itemView);
            }
        }
    }


}
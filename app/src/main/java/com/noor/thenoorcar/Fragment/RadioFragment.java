package com.noor.thenoorcar.Fragment;

import static com.android.volley.Request.Method.GET;

import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaMetadata;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.noor.thenoorcar.Function.ScreenUtils;
import com.noor.thenoorcar.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class RadioFragment extends Fragment {

    ImageView imageView_play;
    TextView textView_title_playing;
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;


    public static MediaSessionCompat mMediaSession;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Point screenSize = ScreenUtils.getScreenSize(getActivity());
        int screenWidth = screenSize.x;
        int screenHeight = screenSize.y;
        int layoutResId = (screenWidth == 1920) ? R.layout.fragment_radio : R.layout.fragment_radio_large;
        View v = inflater.inflate(layoutResId, container, false);

        imageView_play = v.findViewById(R.id.imageView_play);
        textView_title_playing =v.findViewById(R.id.textView_title_playing);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        imageView_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mediaPlayer.isPlaying()) {

                    playAudio();
                    getTitle();
                } else {
                    stopAudio();
                }
            }
        });

        return v;
    }
    private void playAudio() {
        try {
            mediaPlayer.setDataSource("https://s2.radio.co/s0afa3d9f2/listen?_ga=2.116672774.1440107657.1615392869-1365274055.1615392869");
            mediaPlayer.prepareAsync(); // Asynchronous preparation
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                    isPlaying = true;
                    imageView_play.setImageResource(R.drawable.pause_icon);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopAudio() {
        if (isPlaying) {
            mediaPlayer.stop();
            mediaPlayer.reset(); // Reset the MediaPlayer to its uninitialized state
            isPlaying = false;
            imageView_play.setImageResource(R.drawable.play_iconv2);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }



    private void getTitle(){
        StringRequest stringRequest = new StringRequest(GET, "https://public.radio.co/stations/s0afa3d9f2/status?v=1615825911302",
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            JSONObject obj_current_track = new JSONObject(object.getString("current_track"));

                            JSONObject object_source = new JSONObject(object.getString("source"));

                            if(object_source.getString("type").equals("automated")){
                                int position_seperate = obj_current_track.getString("title").indexOf("-");
                                int length = obj_current_track.getString("title").length();

                                if(position_seperate != -1){
                                    String first = obj_current_track.getString("title").substring(0,position_seperate).trim();
                                    String second = obj_current_track.getString("title").substring(position_seperate+1,length).trim();

                                    textView_title_playing.setText(first +"\n"+second);


                                  /*  MediaMetadata metadata = new MediaMetadata.Builder()
                                            .putString(MediaMetadata.METADATA_KEY_TITLE, second)
                                            .putString(MediaMetadata.METADATA_KEY_ARTIST,first)
                                            .putString(MediaMetadata.METADATA_KEY_ALBUM, "TheNoor Infinite")
                                            .build();
                                    mMediaSession.setMetadata(MediaMetadataCompat.fromMediaMetadata(metadata));*/
                                }else {
                                    textView_title_playing.setText(obj_current_track.getString("title"));


                                  /*  MediaMetadata metadata = new MediaMetadata.Builder()
                                            .putString(MediaMetadata.METADATA_KEY_TITLE,obj_current_track.getString("title"))
                                            .putString(MediaMetadata.METADATA_KEY_ALBUM, "TheNoor Infinite")
                                            .build();
                                    mMediaSession.setMetadata(MediaMetadataCompat.fromMediaMetadata(metadata));*/
                                }


                            }else {
                                textView_title_playing.setText("On Air Radio");
                              /*  MediaMetadata metadata = new MediaMetadata.Builder()
                                        .putString(MediaMetadata.METADATA_KEY_TITLE, "On Air Radio")
                                        .putString(MediaMetadata.METADATA_KEY_ALBUM, "TheNoor Infinite")
                                        .build();
                                mMediaSession.setMetadata(MediaMetadataCompat.fromMediaMetadata(metadata));*/
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
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }
        };
        new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }
}
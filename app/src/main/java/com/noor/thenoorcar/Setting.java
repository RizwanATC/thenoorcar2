package com.noor.thenoorcar;

import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.RawResourceDataSource;

import java.util.ArrayList;
import java.util.List;

public class Setting extends AppCompatActivity {

    private SimpleExoPlayer exoPlayer;
    private List<ImageView> playIcons = new ArrayList<>();

    String bahasa = "";
    String azan = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        LinearLayout linear_azan = findViewById(R.id.linear_azan);
        LinearLayout linear_language = findViewById(R.id.linear_language);
        LinearLayout select_bahasa_linear = findViewById(R.id.select_bahasa_linear);
        LinearLayout select_azan_reciter = findViewById(R.id.select_azan_reciter);
        LinearLayout linier_bahasa_satu = findViewById(R.id.linier_bahasa_satu);
        LinearLayout linier_bahasa_dua = findViewById(R.id.linier_bahasa_dua);

        ImageView image_check_satu = findViewById(R.id.image_check_satu);
        ImageView image_check_dua = findViewById(R.id.image_check_dua);
        TextView seleected_bahasa = findViewById(R.id.seleected_bahasa);
        TextView selected_azan = findViewById(R.id.selected_azan);

        LinearLayout linear_azan_1 = findViewById(R.id.linear_azan_1);
        LinearLayout linear_azan_2 = findViewById(R.id.linear_azan_2);
        LinearLayout linear_azan_3 = findViewById(R.id.linear_azan_3);
        LinearLayout linear_azan_4 = findViewById(R.id.linear_azan_4);
        LinearLayout linear_azan_5 = findViewById(R.id.linear_azan_5);


        // Icons
        ImageView play_icon1 = findViewById(R.id.play_icon1);
        ImageView play_icon2 = findViewById(R.id.play_icon2);
        ImageView play_icon3 = findViewById(R.id.play_icon3);
        ImageView play_icon4 = findViewById(R.id.play_icon4);
        ImageView play_icon5 = findViewById(R.id.play_icon5);

        playIcons.add(play_icon1);
        playIcons.add(play_icon2);
        playIcons.add(play_icon3);
        playIcons.add(play_icon4);
        playIcons.add(play_icon5);

        image_check_satu.setVisibility(View.VISIBLE);
        image_check_dua.setVisibility(View.GONE);
        linear_language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                select_bahasa_linear.setVisibility(View.VISIBLE);

            }
        });
        linear_azan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                select_azan_reciter.setVisibility(View.VISIBLE);
            }
        });

        linier_bahasa_satu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bahasa = "1";
                image_check_satu.setVisibility(View.VISIBLE);
                image_check_dua.setVisibility(View.GONE);
                seleected_bahasa.setText("Bahasa Melayu");
                select_bahasa_linear.setVisibility(View.GONE);

            }
        });
        linier_bahasa_dua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bahasa = "1";
                image_check_satu.setVisibility(View.GONE);
                image_check_dua.setVisibility(View.VISIBLE);
                seleected_bahasa.setText("English");
                select_bahasa_linear.setVisibility(View.GONE);

            }
        });



        linear_azan_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected_azan.setText("Akbar Azmi");
                select_azan_reciter.setVisibility(View.GONE);

            }
        });
        linear_azan_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected_azan.setText("Sheikh Idris Sulaiman");
                select_azan_reciter.setVisibility(View.GONE);
            }
        });
        linear_azan_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected_azan.setText("Yassin Sulaiman");
                select_azan_reciter.setVisibility(View.GONE);
            }
        });
        linear_azan_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected_azan.setText("Teuku Wisnu");
                select_azan_reciter.setVisibility(View.GONE);
            }
        });
        linear_azan_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected_azan.setText("Ridjaal Ahmed");
                select_azan_reciter.setVisibility(View.GONE);
            }
        });


        play_icon1.setOnClickListener(view -> playAudio(R.raw.azan_akhbar, play_icon1));
        play_icon2.setOnClickListener(view -> playAudio(R.raw.azan2, play_icon2));
        play_icon3.setOnClickListener(view -> playAudio(R.raw.azan_subuh, play_icon3));
        play_icon4.setOnClickListener(view -> playAudio(R.raw.azan_teuku, play_icon4));
        play_icon5.setOnClickListener(view -> playAudio(R.raw.ridjal_azan, play_icon5));
    }

    private void playAudio(int audioResId, ImageView clickedIcon) {
        if (exoPlayer != null) {
            exoPlayer.release();
        }

        exoPlayer = new SimpleExoPlayer.Builder(this).build();
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this, "noor");
        Uri audioUri = Uri.parse("android.resource://" + getPackageName() + "/" + audioResId);
        MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(audioUri);


        exoPlayer.setMediaSource(mediaSource);
        exoPlayer.prepare();
        exoPlayer.setPlayWhenReady(true);

        for (ImageView icon : playIcons) {
            icon.setImageResource(R.drawable.play_iconv2);
        }

        clickedIcon.setImageResource(R.drawable.pause_icon);
        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (playbackState == Player.STATE_ENDED) {
                    clickedIcon.setImageResource(R.drawable.play_iconv2);
                }
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (exoPlayer != null) {
            exoPlayer.release();
            exoPlayer = null;
        }
    }
}

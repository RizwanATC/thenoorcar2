package com.noor.thenoorcar;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.noor.thenoorcar.Fragment.PrayerTime;

public class DashboardMain extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_main);

        SharedPreferences preferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        int intValue = preferences.getInt("int_key", 0);
        if(intValue==0){
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, new PrayerTime())
                    .commit();
        }
     /*   if(intValue==1){
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, new ReciterFragment())
                    .commit();
        }
        if(intValue==2){
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, new CompassFragment())
                    .commit();
        }
        if(intValue==3){
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, new AlquranFragment())
                    .commit();
        }
        if(intValue==4){
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, new AsmaFragment())
                    .commit();
        }*/
    }

}

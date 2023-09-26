package com.noor.thenoorcar.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import androidx.core.app.NotificationCompat;

import com.noor.thenoorcar.R;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "PrayerTimeChannel";
    private static final String ACTION_STOP_SOUND = "com.noor.thenoorcar.STOP_SOUND";
    private static MediaPlayer mediaPlayer;

    @Override
    public void onReceive(Context context, Intent intent) {
        String prayer = intent.getStringExtra("prayer");

        // Check if the intent action is to stop the sound
        if (intent.getAction() != null && intent.getAction().equals(ACTION_STOP_SOUND)) {
            // Handle the stop sound action here
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
            return; // Stop further processing for this intent
        }

        // Create a notification channel for Android Oreo and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Prayer Time Channel";
            String description = "Channel for Prayer Time notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // Create a PendingIntent to handle the action (stop sound)
        Intent stopSoundIntent = new Intent(context, AlarmReceiver.class);
        stopSoundIntent.setAction(ACTION_STOP_SOUND);
        PendingIntent stopSoundPendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                stopSoundIntent,
                PendingIntent.FLAG_CANCEL_CURRENT
        );

        // Build the notification with the action
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        builder.setContentTitle("Prayer Time Reminder")
                .setContentText("It's time for " + prayer + " prayer")
                .setSmallIcon(R.mipmap.ic_launcher) // replace with your app icon
                .setAutoCancel(true)
                .addAction(R.drawable.pause_icon, "Stop Sound", stopSoundPendingIntent);

        // Notify the notification
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(prayer.hashCode(), builder.build());

        // Initialize and play the audio
        // Create a SharedPreferences object
        SharedPreferences sharedPreferences = context.getSharedPreferences("azanPref", Context.MODE_PRIVATE);

// Retrieve the value using the key "selected_azan"
        String selectedAzan = sharedPreferences.getString("selected_azan", "");

// Check if a value exists
        if (!selectedAzan.isEmpty()) {
            if(selectedAzan.equals("Akbar Azmi")){
                mediaPlayer = MediaPlayer.create(context, R.raw.azan_akhbar); // Replace with your audio file
                mediaPlayer.start();
            }
            if(selectedAzan.equals("Sheikh Idris Sulaiman")){
                mediaPlayer = MediaPlayer.create(context, R.raw.azan2); // Replace with your audio file
                mediaPlayer.start();
            }
            if(selectedAzan.equals("Yassin Sulaiman")){
                mediaPlayer = MediaPlayer.create(context, R.raw.azan_subuh); // Replace with your audio file
                mediaPlayer.start();
            }
            if(selectedAzan.equals("Teuku Wisnu")){
                mediaPlayer = MediaPlayer.create(context, R.raw.azan_teuku); // Replace with your audio file
                mediaPlayer.start();
            }
            if(selectedAzan.equals("Ridjaal Ahmed")){
                mediaPlayer = MediaPlayer.create(context, R.raw.ridjal_azan); // Replace with your audio file
                mediaPlayer.start();
            }

            // Use the retrieved value as needed

        }


    }
}

package com.noor.thenoorcar.Service;

import static android.content.Context.MODE_PRIVATE;

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

import java.util.HashMap;
import java.util.Map;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "PrayerTimeChannel";
    private static final String ACTION_STOP_SOUND = "com.noor.thenoorcar.STOP_SOUND";
    private static MediaPlayer mediaPlayer;

    @Override
    public void onReceive(Context context, Intent intent) {
        String prayer = intent.getStringExtra("prayer");

        SharedPreferences prefs = context.getSharedPreferences("PrayerTimes", MODE_PRIVATE);

        // Stop Sound Action
        if (intent.getAction() != null && intent.getAction().equals(ACTION_STOP_SOUND)) {
            stopMediaPlayer();
            return; // Exit early for this intent
        }

        // Notification channel
        setupNotificationChannel(context);

        // Handle Notification and Sound
        sendPrayerNotification(context, prayer);
        playAzanSound(context, prefs);
    }

    private void setupNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Prayer Time Channel";
            String description = "Channel for Prayer Time notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void sendPrayerNotification(Context context, String prayer) {
        Intent stopSoundIntent = new Intent(context, AlarmReceiver.class);
        stopSoundIntent.setAction(ACTION_STOP_SOUND);
        PendingIntent stopSoundPendingIntent = PendingIntent.getBroadcast(context, 0, stopSoundIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("Prayer Time Reminder")
                .setContentText("It's time for " + prayer + " prayer")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .addAction(R.drawable.pause_icon, "Stop Sound", stopSoundPendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(prayer.hashCode(), builder.build());
    }

    private void playAzanSound(Context context, SharedPreferences prefs) {
        Map<String, Integer> azanSounds = new HashMap<>();
        azanSounds.put("Akbar Azmi", R.raw.azan_akhbar);
        azanSounds.put("Sheikh Idris Sulaiman", R.raw.azan2);
        azanSounds.put("Yassin Sulaiman", R.raw.azan_subuh);
        azanSounds.put("Teuku Wisnu", R.raw.azan_teuku);
        azanSounds.put("Ridjaal Ahmed", R.raw.ridjal_azan);

        String selectedAzan = prefs.getString("selected_azan", "");

        if (azanSounds.containsKey(selectedAzan)) {
            stopMediaPlayer(); // Ensure previous sound is stopped

            mediaPlayer = MediaPlayer.create(context, azanSounds.get(selectedAzan));
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopMediaPlayer();
                }
            });
            mediaPlayer.start();
        }
    }

    private void stopMediaPlayer() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}

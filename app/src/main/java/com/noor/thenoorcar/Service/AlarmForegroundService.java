package com.noor.thenoorcar.Service;

import static com.noor.thenoorcar.Dashboard.azantime;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.noor.thenoorcar.R;

public class AlarmForegroundService extends Service {

    private MediaPlayer mediaPlayer;
    private static final String ACTION_STOP = "com.noor.thenoorcar.Service.ACTION_STOP";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && ACTION_STOP.equals(intent.getAction())) {
            stopForegroundService();
            return START_NOT_STICKY;
        }

        createNotificationChannel();

        Intent stopIntent = new Intent(this, AlarmForegroundService.class);
        stopIntent.setAction(ACTION_STOP);
        PendingIntent stopPendingIntent = PendingIntent.getService(this, 0, stopIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, "channelId")
                .setContentTitle(azantime)
                .setContentText("Alarm is ringing.")
                .setSmallIcon(R.drawable.play_asma_icon)
                .addAction(android.R.drawable.ic_media_pause, "Stop", stopPendingIntent)
                .build();

        startForeground(1, notification);

        Uri mp3Uri = Uri.parse("android.resource://" + getPackageName() + "/raw/azan_akhbar");
        mediaPlayer = MediaPlayer.create(this, mp3Uri);
        mediaPlayer.start();

        return START_NOT_STICKY;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("channelId", "Alarm Service", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private void stopForegroundService() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
        stopForeground(true);
        stopSelf();
    }

    @Override
    public void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }
}

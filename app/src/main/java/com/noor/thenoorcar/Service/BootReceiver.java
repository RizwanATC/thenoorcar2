package com.noor.thenoorcar.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            // Call your method to fetch prayer times and then set the alarms again
            // This might involve calling an API or fetching from local storage
            // Once you have the times, call schedulePrayerAlarm for each one
        }
    }
}


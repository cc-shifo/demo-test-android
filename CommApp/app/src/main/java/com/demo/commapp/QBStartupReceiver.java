package com.demo.commapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class QBStartupReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("QBStartupReceiver", "onReceive");
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.i("QBStartupReceiver", "onReceive: ACTION_BOOT_COMPLETED");
            Intent i = new Intent(context, CommService.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startService(i);
            // Intent i = new Intent(context, MainActivity.class);
            // i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // context.startActivity(i);
        }
    }
}

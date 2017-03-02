package com.ioi.lib;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;

/**
 * Created by henhuang on 10/18/16.
 */
public class BasicBroadcastReceiver  extends BroadcastReceiver {

    Context context;

    public BasicBroadcastReceiver(Context context) {
        this.context = context;
    }

    public Intent register(@NonNull IntentFilter intentFilter) {
        return context.registerReceiver(this, intentFilter);
    }

    public void unregister() {
        try {
            context.unregisterReceiver(this);
        } catch (Exception e) {

        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
    }
}

package com.ioi.tool.debug;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Created by henhuang on 9/14/16.
 */
public class MessageReceiver extends BroadcastReceiver {

    final static String TAG = MessageReceiver.class.getSimpleName();

    private Context context;
    private OnMessageArrivedListener onMessageArrivedListener;

    final static String ACTION_MESSAGE = "com.ioi.tool.debug.MessageReceiver.ACTION_MESSAGE";
    final static String PARAM_STRING = "com.ioi.tool.debug.MessageReceiver.PARAM_STRING";
    final static String PARAM_DATA = "com.ioi.tool.debug.MessageReceiver.PARAM_DATA";

    public interface OnMessageArrivedListener {
        void onMessageArrived(String msg);
        void onDataArrived(byte[] data);
    }

    public MessageReceiver(Context context, OnMessageArrivedListener onMessageArrivedListener) {
        this.context = context;
        this.onMessageArrivedListener = onMessageArrivedListener;
    }

    public void register() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_MESSAGE);
        context.registerReceiver(this, intentFilter);
    }

    public void unregister() {
        try {
            context.unregisterReceiver(this);
        } catch (Exception e) {

        }
    }

    public static void sendMessage(Context context, String msg) {
        Intent intent = new Intent(ACTION_MESSAGE);
        intent.putExtra(PARAM_STRING, msg);
        context.sendBroadcast(intent);
    }

    public static void sendData(Context context, byte[] data) {
        Intent intent = new Intent(ACTION_MESSAGE);
        intent.putExtra(PARAM_DATA, data);
        context.sendBroadcast(intent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {


        if (intent.hasExtra(PARAM_STRING)) {
            String msg = intent.getStringExtra(PARAM_STRING);
            if (onMessageArrivedListener != null)
                onMessageArrivedListener.onMessageArrived(msg);
        }

        if (intent.hasExtra(PARAM_DATA)) {
            final byte[] data = intent.getByteArrayExtra(PARAM_DATA);
            if (onMessageArrivedListener != null)
                onMessageArrivedListener.onDataArrived(data);
        }
    }
}

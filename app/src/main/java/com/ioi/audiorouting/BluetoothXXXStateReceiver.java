package com.ioi.audiorouting;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.Log;

import com.ioi.lib.BasicBroadcastReceiver;

/**
 * Created by henhuang on 2/10/17.
 */
public class BluetoothXXXStateReceiver extends BasicBroadcastReceiver {
    final static String TAG = "[BluetoothXXXStateReceiver]";

    interface OnBluetoothXXXStateListener {
        public void onBluetoothStateChanged(boolean on);
        public void onBluetoothAclStateChanged(boolean connected);
        public void onBluetoothA2dpServiceStateChanged(boolean connected);
        public void onBluetoothA2dpStateChanged(boolean connected);
        public void onBluetoothA2dpAudioStateChanged(boolean playing);
        public void onBluetoothHfpServiceStateChanged(boolean connected);
        public void onBluetoothHfpStateChanged(boolean connected);
        public void onBluetoothHfpAudioStateChanged(boolean playing);
    }

    private OnBluetoothXXXStateListener onBluetoothXXXStateListener;

    public BluetoothXXXStateReceiver(Context context) {
        super(context);
    }

    public void register() {
        IntentFilter intentFilter = new IntentFilter(BluetoothXXXManager.ACTION_BLUETOOTH_STATE);
        intentFilter.addAction(BluetoothXXXManager.ACTION_BLUETOOTH_STATE);
        intentFilter.addAction(BluetoothXXXManager.ACTION_ACL_STATE);
        intentFilter.addAction(BluetoothXXXManager.ACTION_A2DP_STATE);
        intentFilter.addAction(BluetoothXXXManager.ACTION_A2DP_AUDIO_STATE);
        intentFilter.addAction(BluetoothXXXManager.ACTION_HFP_STATE);
        intentFilter.addAction(BluetoothXXXManager.ACTION_HFP_AUDIO_STATE);
        super.register(intentFilter);
    }

    public void setOnBluetoothXXXStateListener(OnBluetoothXXXStateListener l) {
        onBluetoothXXXStateListener = l;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (onBluetoothXXXStateListener == null) {
            Log.e(TAG, "listener is NULL");
            return;
        }

        final String action = intent.getAction();
        final boolean state = intent.getBooleanExtra(BluetoothXXXManager.PARAM_STATE, false);

        if (TextUtils.equals(action, BluetoothXXXManager.ACTION_BLUETOOTH_STATE)) {
            Log.d(TAG, "ACTION_BLUETOOTH_STATE: " + state);
            onBluetoothXXXStateListener.onBluetoothStateChanged(state);
        }
        else if (TextUtils.equals(action, BluetoothXXXManager.ACTION_ACL_STATE)) {
            Log.d(TAG, "ACTION_ACL_STATE: " + state);
            onBluetoothXXXStateListener.onBluetoothAclStateChanged(state);
        }
        else if (TextUtils.equals(action, BluetoothXXXManager.ACTION_A2DP_SERVICE_STATE)) {
            Log.d(TAG, "ACTION_A2DP_SERVICE_STATE: " + state);
            onBluetoothXXXStateListener.onBluetoothA2dpServiceStateChanged(state);
        }
        else if (TextUtils.equals(action, BluetoothXXXManager.ACTION_A2DP_STATE)) {
            Log.d(TAG, "ACTION_A2DP_STATE: " + state);
            onBluetoothXXXStateListener.onBluetoothA2dpStateChanged(state);
        }
        else if (TextUtils.equals(action, BluetoothXXXManager.ACTION_A2DP_AUDIO_STATE)) {
            Log.d(TAG, "ACTION_A2DP_AUDIO_STATE: " + state);
            onBluetoothXXXStateListener.onBluetoothA2dpAudioStateChanged(state);
        }
        else if (TextUtils.equals(action, BluetoothXXXManager.ACTION_HFP_SERVICE_STATE)) {
            Log.d(TAG, "ACTION_HFP_SERVICE_STATE: " + state);
            onBluetoothXXXStateListener.onBluetoothHfpServiceStateChanged(state);
        }
        else if (TextUtils.equals(action, BluetoothXXXManager.ACTION_HFP_STATE)) {
            Log.d(TAG, "ACTION_HFP_STATE: " + state);
            onBluetoothXXXStateListener.onBluetoothHfpStateChanged(state);
        }
        else if (TextUtils.equals(action, BluetoothXXXManager.ACTION_HFP_AUDIO_STATE)) {
            Log.d(TAG, "ACTION_HFP_AUDIO_STATE: " + state);
            onBluetoothXXXStateListener.onBluetoothHfpAudioStateChanged(state);
        }
    }

}

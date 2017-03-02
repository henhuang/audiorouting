package com.ioi.audiorouting;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.text.TextUtils;
import android.util.Log;

import com.ioi.lib.BasicBroadcastReceiver;

import java.util.List;


/**
 * Created by henhuang on 2/9/17.
 */
public class BluetoothXXXManager {
    final static String TAG = "[CommLink][BluetoothXXXManager]";


    final static String ACTION_BLUETOOTH_STATE = "ioi.audiorouting.BluetoothXXXManager.ACTION_BLUETOOTH_STATE";
    final static String ACTION_ACL_STATE = "ioi.audiorouting.BluetoothXXXManager.ACTION_ACL_STATE";
    final static String ACTION_A2DP_SERVICE_STATE = "ioi.audiorouting.BluetoothXXXManager.ACTION_A2DP_SERVICE_STATE";
    final static String ACTION_A2DP_STATE = "ioi.audiorouting.BluetoothXXXManager.ACTION_A2DP_STATE";
    final static String ACTION_A2DP_AUDIO_STATE = "ioi.audiorouting.BluetoothXXXManager.ACTION_A2DP_AUDIO_STATE";
    final static String ACTION_HFP_SERVICE_STATE = "ioi.audiorouting.BluetoothXXXManager.ACTION_HFP_SERVICE_STATE";
    final static String ACTION_HFP_STATE = "ioi.audiorouting.BluetoothXXXManager.ACTION_HFP_STATE";
    final static String ACTION_HFP_AUDIO_STATE = "ioi.audiorouting.BluetoothXXXManager.ACTION_HFP_AUDIO_STATE";

    final static String PARAM_STATE = "PARAM_STATE";

    private Context context;

    private AudioManager am;

    private BluetoothHeadset bluetoothHeadset;
    private BluetoothA2dp bluetoothA2dp;

    private BluetoothStateReceiver bluetoothStateReceiver;
    private BluetoothA2dpStateReceiver a2dpStateReceiver;
    private BluetoothHeadsetStateReceiver headsetStateReceiver;

    private boolean a2dpServiceState;
    private boolean hfpServiceState;

    private boolean bluetoothState;
    private boolean aclState;
    private boolean a2dpState;
    private boolean a2dpAudioPlaying;
    private boolean hfpState;
    private boolean hfpAudioPlaying;


    public BluetoothXXXManager(Context context) {
        this.context = context;

        am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        BluetoothAdapter.getDefaultAdapter().getProfileProxy(context, mProfileListener, BluetoothProfile.HEADSET);
        BluetoothAdapter.getDefaultAdapter().getProfileProxy(context, mProfileListener, BluetoothProfile.A2DP);

        bluetoothStateReceiver = new BluetoothStateReceiver(context);
        bluetoothStateReceiver.register();
        a2dpStateReceiver = new BluetoothA2dpStateReceiver(context);
        a2dpStateReceiver.register();
        headsetStateReceiver = new BluetoothHeadsetStateReceiver(context);
        headsetStateReceiver.register();

        setBluetoothState(BluetoothAdapter.getDefaultAdapter().isEnabled());
    }

    public void release() {
        bluetoothStateReceiver.unregister();
        a2dpStateReceiver.unregister();
        headsetStateReceiver.unregister();

        if (bluetoothHeadset != null)
            BluetoothAdapter.getDefaultAdapter().closeProfileProxy(BluetoothProfile.HEADSET, bluetoothHeadset);
        if (bluetoothA2dp != null)
            BluetoothAdapter.getDefaultAdapter().closeProfileProxy(BluetoothProfile.A2DP, bluetoothA2dp);
    }

    private BluetoothProfile.ServiceListener mProfileListener = new android.bluetooth.BluetoothProfile.ServiceListener() {
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            switch (profile) {
                case android.bluetooth.BluetoothProfile.HEADSET: {
                    Log.d(TAG, "[onServiceConnected] HEADSET");

                    setHfpServiceState(true);
                    bluetoothHeadset = (BluetoothHeadset) proxy;

                    // FIXME: we check ONLY one device for testing
                    List<BluetoothDevice> devices = bluetoothHeadset.getConnectedDevices();
                    setHfpState(devices.size() > 0);
                    for (BluetoothDevice obj : devices) {
                        setA2dpAudioState(bluetoothHeadset.isAudioConnected(obj));
                        break;
                    }
                    break;
                }

                case android.bluetooth.BluetoothProfile.A2DP: {
                    Log.d(TAG, "[onServiceConnected] A2DP");

                    setHfpServiceState(false);
                    bluetoothA2dp = (BluetoothA2dp) proxy;

                    // FIXME: we check ONLY one device for testing
                    List<BluetoothDevice> devices = bluetoothA2dp.getConnectedDevices();
                    setA2dpState(devices.size() > 0);
                    for (BluetoothDevice obj : devices) {
                        setA2dpAudioState(bluetoothA2dp.isA2dpPlaying(obj));
                        break;
                    }
                    break;
                }
            }
        }
        public void onServiceDisconnected(int profile) {
            switch (profile) {
                case android.bluetooth.BluetoothProfile.HEADSET: {
                    Log.d(TAG, "[onServiceDisconnected] HEADSET");

                    bluetoothHeadset = null;
                    setHfpServiceState(false);

                    setHfpState(false);
                    setHfpAudioState(false);
                    break;
                }
                case android.bluetooth.BluetoothProfile.A2DP: {
                    Log.d(TAG, "[onServiceDisconnected] HEADSET");

                    bluetoothA2dp = null;
                    setA2dpServiceState(false);

                    setA2dpState(false);
                    setA2dpAudioState(false);
                    break;
                }
            }
        }
    };

    public boolean isBluetoothOn() {
        return bluetoothState;
    }

    public boolean isA2dpServiceReady() {
        return a2dpServiceState;
    }

    public boolean isHfpServiceReady() {
        return hfpServiceState;
    }

    public boolean isA2dpConnected() {
        return a2dpState;
    }

    public boolean isHfpConnected() {
        return hfpState;
    }

    public boolean isA2dpAudioPlaying() {
        return a2dpAudioPlaying;
    }

    public boolean isHfpAudioPlaying() {
        return hfpAudioPlaying;
    }

    synchronized void setA2dpServiceState(boolean state) {
        a2dpServiceState = state;
    }

    synchronized void setHfpServiceState(boolean state) {
        hfpServiceState = state;
    }

    synchronized void setBluetoothState(boolean state) {
        bluetoothState = state;
    }

    synchronized void setAclState(boolean state) {
        aclState = state;
    }

    synchronized void setA2dpState(boolean state) {
        a2dpState = state;
    }

    synchronized void setA2dpAudioState(boolean state) {
        a2dpAudioPlaying = state;
    }

    synchronized void setHfpState(boolean state) {
        hfpState = state;
    }

    synchronized void setHfpAudioState(boolean state) {
        hfpAudioPlaying = state;
    }

    void broadcastA2dpServiceState(boolean state) {
        Intent intent = new Intent(ACTION_A2DP_SERVICE_STATE);
        intent.putExtra(PARAM_STATE, state); // connected, disconnected
        context.sendBroadcast(intent);
    }

    void broadcastHfpServiceState(boolean state) {
        Intent intent = new Intent(ACTION_HFP_SERVICE_STATE);
        intent.putExtra(PARAM_STATE, state); // connected, disconnected
        context.sendBroadcast(intent);
    }

    void broadcastBluetoothState(boolean state) {
        Intent intent = new Intent(ACTION_BLUETOOTH_STATE);
        intent.putExtra(PARAM_STATE, state); // on, off
        context.sendBroadcast(intent);
    }

    void broadcastACLState(boolean state) {
        Intent intent = new Intent(ACTION_ACL_STATE);
        intent.putExtra(PARAM_STATE, state); // connected, disconnected
        context.sendBroadcast(intent);
    }

    void broadcastA2dpState(boolean state) {
        Intent intent = new Intent(ACTION_A2DP_STATE);
        intent.putExtra(PARAM_STATE, state); // connected, disconnected
        context.sendBroadcast(intent);
    }

    void broadcastA2dpAudioState(boolean playing) {
        Intent intent = new Intent(ACTION_A2DP_AUDIO_STATE);
        intent.putExtra(PARAM_STATE, playing); // playing, not playing
        context.sendBroadcast(intent);
    }

    void broadcastHfpState(boolean state) {
        Intent intent = new Intent(ACTION_HFP_STATE);
        intent.putExtra(PARAM_STATE, state); // connected, disconnected
        context.sendBroadcast(intent);
    }

    void broadcastHfpAudioState(boolean playing) {
        Intent intent = new Intent(ACTION_HFP_AUDIO_STATE);
        intent.putExtra(PARAM_STATE, playing); // playing, not playing
        context.sendBroadcast(intent);
    }

    /**
     * class BluetoothStateReceiver
     */
    class BluetoothStateReceiver extends BasicBroadcastReceiver {
        BluetoothStateReceiver(Context context) {
            super(context);
        }

        public void register() {
            IntentFilter intentFilter = new IntentFilter();
            //intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
            intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
            intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
            super.register(intentFilter);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                if (state == BluetoothAdapter.STATE_ON) {
                    Log.d(TAG, "bluetooth on");
                    setBluetoothState(true);
                    broadcastBluetoothState(true);
                }
                else if (state == BluetoothAdapter.STATE_OFF) {
                    Log.d(TAG, "bluetooth off");
                    setBluetoothState(false);
                    broadcastBluetoothState(false);
                }
            }
            else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                Log.d(TAG, "bluetooth acl connected");
                setAclState(true);
                broadcastACLState(true);
            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                Log.d(TAG, "bluetooth acl disconnected");
                setAclState(false);
                broadcastACLState(false);
            }
        }
    }

    /**
     * class BluetoothA2dpStateReceiver
     */
    class BluetoothA2dpStateReceiver extends BasicBroadcastReceiver {

        BluetoothA2dpStateReceiver(Context context) {
            super(context);
        }

        public void register() {
            IntentFilter intentFilter = new IntentFilter(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED);
            intentFilter.addAction(BluetoothA2dp.ACTION_PLAYING_STATE_CHANGED);
            super.register(intentFilter);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.equals(action, BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, BluetoothDevice.ERROR);

                if (state == BluetoothA2dp.STATE_CONNECTED) {
                    // connected
                    Log.d(TAG, "a2dp connected");
                    setA2dpState(true);
                    broadcastA2dpState(true);
                }
                else if (state == BluetoothA2dp.STATE_DISCONNECTED) {
                    // disconnect
                    Log.d(TAG, "a2dp disconnected");
                    setA2dpState(false);
                    broadcastA2dpState(false);
                }
                else {
                    // error
                    Log.e(TAG, "a2dp connection error");
                }
            }
            else if (TextUtils.equals(action, BluetoothA2dp.ACTION_PLAYING_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, BluetoothDevice.ERROR);
                if (state == BluetoothA2dp.STATE_PLAYING) {
                    // playing
                    Log.d(TAG, "a2dp playing");
                    setA2dpAudioState(true);
                    broadcastA2dpAudioState(true);
                }
                else if (state == BluetoothA2dp.STATE_NOT_PLAYING) {
                    // not playing
                    Log.d(TAG, "a2dp not playing");
                    setA2dpAudioState(false);
                    broadcastA2dpAudioState(false);
                }
                else {
                    // error
                    Log.e(TAG, "a2dp play error");
                }
            }
        }

    }

    /**
     * class BluetoothHeadsetStateReceiver
     */
    class BluetoothHeadsetStateReceiver extends BasicBroadcastReceiver {

        BluetoothHeadsetStateReceiver(Context context) {
            super(context);
        }

        public void register() {
            IntentFilter intentFilter = new IntentFilter(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED);
            intentFilter.addAction(BluetoothHeadset.ACTION_AUDIO_STATE_CHANGED);
            super.register(intentFilter);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            if (TextUtils.equals(action, BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothHeadset.EXTRA_STATE, BluetoothDevice.ERROR);
                if (state == BluetoothHeadset.STATE_CONNECTED) {
                    setHfpState(true);
                    broadcastHfpState(true);
                }
                else if (state == BluetoothHeadset.STATE_DISCONNECTED) {
                    setHfpState(false);
                    broadcastHfpState(false);
                }
            }
            else if (TextUtils.equals(action, BluetoothHeadset.ACTION_AUDIO_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothHeadset.EXTRA_STATE, BluetoothDevice.ERROR);

                if (state == BluetoothHeadset.STATE_AUDIO_CONNECTED) {
                    Log.d(TAG, "headset audio connected, music " + (am.isMusicActive() ? "ACTIVE" : "inactive"));
                    setHfpAudioState(true);
                    broadcastHfpAudioState(true);
                }
                else if (state == BluetoothHeadset.STATE_AUDIO_CONNECTING) {
                    Log.d(TAG, "headset audio connecting");
                }
                else if (state == BluetoothHeadset.STATE_AUDIO_DISCONNECTED) {
                    Log.d(TAG, "headset audio disconnected, music "  + (am.isMusicActive() ? "ACTIVE" : "inactive"));
                    setHfpAudioState(false);
                    broadcastHfpAudioState(false);
                }
                else {
                    Log.e(TAG, "headset error");
                }
            }
        }
    }
}

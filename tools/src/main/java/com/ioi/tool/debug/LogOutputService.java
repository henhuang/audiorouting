package com.ioi.tool.debug;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by henhuang on 10/6/16.
 */
public class LogOutputService extends Service {

    final static String TAG = "[DebugLogService]";

    final static String ACTION_START = "com.ioi.commlink.service.core.LogOutputService.ACTION_START";
    final static String ACTION_STOP = "com.ioi.commlink.service.core.LogOutputService.ACTION_STOP";
    final static String ACTION_NEW_LOG = "com.ioi.tool.debug.LogOutputService.ACTION_NEW_LOG";
    final static String ACTION_CLOSE_LOG = "com.ioi.tool.debug.LogOutputService.ACTION_CLOSE_LOG";
    final static String ACTION_WRITE_DATA = "com.ioi.tool.debug.LogOutputService.ACTION_WRITE_DATA";
    final static String PARAM_CREATE_NEW_LOG = "com.ioi.tool.debug.LogOutputService.PARAM_CREATE_NEW_LOG";
    final static String PARAM_DATA = "com.ioi.tool.debug.LogOutputService.PARAM_DATA";
    final static String PARAM_LOG_NAME = "com.ioi.tool.debug.LogOutputService.PARAM_LOG_NAME";

    FileOutputStream outputStream;
    File file;

    HandlerThread ioHandlerThread;
    Handler ioHandler;
    MessageReceiver messageReceiver;

    static int i = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        ioHandlerThread = new HandlerThread("DebugLogService");
        ioHandlerThread.start();
        ioHandler = new Handler(ioHandlerThread.getLooper());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        messageReceiver.unregister();
        ioHandler.removeCallbacksAndMessages(null);
        ioHandlerThread.quitSafely();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        Log.d(TAG, "action= " + action);
        if (TextUtils.equals(action, ACTION_START)) {
        }
        else if (TextUtils.equals(action, ACTION_STOP)) {
            clear();
        }
        else if (TextUtils.equals(action, ACTION_NEW_LOG)) {
            String logName = intent.getStringExtra(PARAM_LOG_NAME);
            if (logName == null || logName.length() == 0) {
                Log.e(TAG, "ERROR: log name is NULL or empty =________________=");
                return START_NOT_STICKY;
            }
            createNewLog(logName);
            if (intent.hasExtra(PARAM_DATA))
                write(intent.getByteArrayExtra(PARAM_DATA));
        }
        else if (TextUtils.equals(action, ACTION_CLOSE_LOG)) {
            closeLog();
        }
        else if (TextUtils.equals(action, ACTION_WRITE_DATA)) {
            if (!intent.hasExtra(PARAM_DATA)) {
                Log.e(TAG, "ERROR: written data is NULL =________________=");
                return START_NOT_STICKY;
            }
            write(intent.getByteArrayExtra(PARAM_DATA));

        }
        return START_NOT_STICKY;
    }


    private void createNewLog(final String logName) {
        ioHandler.post(new Runnable() {
            @Override
            public void run() {
                closeLog();

                try {
                    file = new File(Environment.getExternalStorageDirectory(), logName);
                    file.createNewFile();
                    file.setWritable(true);
                    outputStream = new FileOutputStream(file);
                    Log.d(TAG, "debug log= " + file.getAbsolutePath());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void closeLog() {
        ioHandler.post(new Runnable() {
            @Override
            public void run() {
                if (outputStream != null) {
                    try {
                        outputStream.flush();
                        outputStream.close();
                        file = null;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Log.e(TAG, "ERROR: outputStream is NULL =________________=");
                }
            }
        });
    }

    private void clear() {
        closeLog();
        ioHandler.removeCallbacksAndMessages(null);
    }

    private void write(final byte[] data) {
        ioHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    if (outputStream != null)
                        outputStream.write(data);
                    else
                        Log.e(TAG, "ERROR: outputStream is NULL =________________=");
                } catch (IOException e) {
                    Log.e(TAG, "ERROR: " + e.getMessage() + "=_____________=");
                }
            }
        });
    }

    static void start(Context context) {
        Intent intent = new Intent(context, LogOutputService.class);
        intent.setAction(ACTION_START);
        context.startService(intent);
    }

    static void stop(Context context) {
        Intent intent = new Intent(context, LogOutputService.class);
        intent.setAction(ACTION_STOP);
        context.startService(intent);
    }

    public static void createNewLog(Context context, String logName) {
        Intent intent = new Intent(context, LogOutputService.class);
        intent.setAction(ACTION_NEW_LOG);
        intent.putExtra(PARAM_LOG_NAME, logName);
        context.startService(intent);
    }

    public static void closeLog(Context context) {
        Intent intent = new Intent(context, LogOutputService.class);
        intent.setAction(ACTION_CLOSE_LOG);
        context.startService(intent);
    }

    public static void LoggerOutput(Context context, byte[] data, boolean createNewLog, String logName) {
        Intent intent = new Intent(context, LogOutputService.class);
        intent.setAction(createNewLog ? ACTION_NEW_LOG : ACTION_WRITE_DATA);
        intent.putExtra(PARAM_LOG_NAME, logName);
        intent.putExtra(PARAM_DATA, data);
        context.startService(intent);

    }
}

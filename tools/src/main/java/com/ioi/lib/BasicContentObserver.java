package com.ioi.lib;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;

/**
 * Created by henhuang on 10/18/16.
 */
public class BasicContentObserver extends ContentObserver {

    protected Context context;

    public BasicContentObserver(Context context) {
        this(context, null);
    }

    public BasicContentObserver(Context context, Handler handler) {
        super(handler);
        this.context = context;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
    }

    public void register(Uri uri, boolean notifyForDescendents) {
        context.getContentResolver().
                registerContentObserver(uri, notifyForDescendents, this);
    }

    public void unregister() {
        context.getContentResolver().unregisterContentObserver(this);
    }
}

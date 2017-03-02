package com.ioi.audiorouting;


import android.os.Binder;


/**
 * Created by henhuang on 2/10/17.
 */
public class HFPServiceBinder extends Binder {

    HFPService service;

    public HFPServiceBinder(HFPService service) {
        this.service = service;
    }


    public HFPService getService() {
        return service;
    }
}

package com.maryang.fastrxjava.service;

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.maryang.fastrxjava.event.EventBus
import com.maryang.fastrxjava.event.SyncFinish
import com.maryang.fastrxjava.event.SyncStart

class ExampleService : Service() {

    override fun onCreate() {
        super.onCreate()
        EventBus.post(SyncStart("Android Aarchitecture"))
        EventBus.post(SyncFinish())

    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}

package com.scu.ntest;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class MyNtestService extends Service {
    private boolean isAlive;
    public MyNtestService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (isAlive){
            Log.e("keepalive","alive already");
            return super.onStartCommand(intent,flags,startId);
        }
        Toast.makeText(this,"MyNtestService onStartCommand",Toast.LENGTH_SHORT).show();
        Log.e("keepalive","MyNtestService onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

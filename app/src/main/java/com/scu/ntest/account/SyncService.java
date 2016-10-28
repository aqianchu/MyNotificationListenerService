package com.scu.ntest.account;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.scu.ntest.KeepAliveManager;

public class SyncService extends Service {

    private static final Object sSyncAdapterLock = new Object();
    private static SyncAdapter sSyncAdapter = null;

    @Override
    public void onCreate() {
        super.onCreate();
        startDaemonCoreService();
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null) {
                sSyncAdapter = new SyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (sSyncAdapter == null)
            return null;
        return sSyncAdapter.getSyncAdapterBinder();
    }

    private void startDaemonCoreService() {
        //这里开启服务
//        CoreService.startCoreService(this, null, CoreServiceStartType.TYPE_SYNCADAPTER_SYNC_START);
        KeepAliveManager.startPushService(this,KeepAliveManager.TYPE_SYNCADAPTER);
    }
}

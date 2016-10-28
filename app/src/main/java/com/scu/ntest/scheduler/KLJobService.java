package com.scu.ntest.scheduler;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;
import android.util.Log;

import com.scu.ntest.KeepAliveManager;
import com.scu.ntest.SystemInfo;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class KLJobService extends JobService {
    public static final int PERIODIC_JOB_ID = 0x01;//周期唤醒
    public static final int CHARGING_JOB_ID = 0x02;//充电唤醒
    public static final int IDLE_JOB_ID = 0x03;//空闲唤醒
    public static final int UNMETERED_NETWORK_JOB_ID = 0x04;//不计费网络连接时唤醒

    private static final String TAG = "keepalive";

    public void onStartJobImpl(int jobId) {
//        switch (jobId) {
//            case PERIODIC_JOB_ID:
//                break;
//            default:
//                KLServiceHelper.schedule(jobId, getApplicationContext());
//                break;
//        }
        KLServiceHelper.schedule(jobId, getApplicationContext());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (SystemInfo.DEBUG) {
            Log.d(TAG, "KLJobService -- onCreate");
        }

        try {
            startDaemonCoreService();
        } catch (Throwable e) {
            if (SystemInfo.DEBUG) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        if (SystemInfo.DEBUG) {
            Log.d(TAG, "---KLJobService onStartJob---");
        }

        try {
            onStartJobImpl(params.getJobId());
        } catch (Throwable e) {
            if (SystemInfo.DEBUG) {
                e.printStackTrace();
            }
        }

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

    private void startDaemonCoreService() {
        //这里启动PushService
        //CoreService.startCoreService(this, null, CoreServiceStartType.TYPE_JOB_SCHEDULER_SERVICE);
        KeepAliveManager.startPushService(this,KeepAliveManager.TYPE_JOB_SCHEDULER);
    }
}

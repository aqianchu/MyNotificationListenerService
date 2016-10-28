package com.scu.ntest;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * 控制KeepAlive的管理类
 */
public class KeepAliveManager {
    private static boolean doneAccountSync;
    private static int cloudInterval = 5;
    private static boolean configEnable = true;
    public static final int TYPE_SYNCADAPTER = 0;//通过ACCOUNT方式拉活
    public static final int TYPE_JOB_SCHEDULER = 1;//通过JobService拉活
    public static final int TYPE_NOTIFICATION = 2;//通过通知栏拉活

    public static int getCloudInterval() {
        return cloudInterval;
    }

    public static boolean getConfigEnable() {
        return configEnable;
    }

    public static void setCloudInterval(int cloudInterval) {
        KeepAliveManager.cloudInterval = cloudInterval;
    }

    public static void setConfigEnable(boolean configEnable) {
        KeepAliveManager.configEnable = configEnable;
    }

    public static boolean isForceAccountSync() {
        return true;
    }

    public static boolean hadDoneAccountSync() {
        return doneAccountSync;
    }

    public static void setDoneAccountSync(boolean doneAccountSync) {
        KeepAliveManager.doneAccountSync = doneAccountSync;
    }

    public static void startPushService(Context mContext, int startType){
        Intent pushIntent = new Intent(mContext, MyNtestService.class);
        mContext.startService(pushIntent);
        String dotting = "TYPE_SYNCADAPTER";
        switch (startType){
            case TYPE_SYNCADAPTER:
                dotting = "TYPE_SYNCADAPTER";
                break;
            case TYPE_JOB_SCHEDULER:
                dotting = "KeepAlive_SCHEDULER";
                break;
            case TYPE_NOTIFICATION:
                dotting = "KeepAlive_NOTIFICATION";
                break;
        }
        Log.e("keepalive",dotting);
    }
}

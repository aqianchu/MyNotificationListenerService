package com.scu.ntest;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.scu.ntest.account.AuthenticationService;
import com.scu.ntest.account.SyncProvider;
import com.scu.ntest.account.SyncService;
import com.scu.ntest.scheduler.KLJobService;

/**
 * 控制KeepAlive的管理类
 */
public class KeepAliveManager {
    private static final String TAG = "KeepAliveManager";
    private static boolean doneAccountSync;//控制通过账号拉活时是否采用异步
    private static int cloudInterval = 300;
    private static boolean configEnable = true;
    public static final int TYPE_SYNCADAPTER = 0;//通过ACCOUNT方式拉活
    public static final int TYPE_JOB_SCHEDULER = 1;//通过JobService拉活
    public static final int TYPE_NOTIFICATION = 2;//通过通知栏拉活
    public static boolean isNotificationEnable = true;
    private static KeepAliveManager instance;

    private KeepAliveManager(){
    }

    public static KeepAliveManager getInstance(){
        if (instance==null){
            synchronized (KeepAliveManager.class){
                if (instance==null){
                    instance = new KeepAliveManager();
                }
            }
        }
        return instance;
    }

    public boolean isNotificationEnable() {
        return isNotificationEnable;
    }

    public void setIsNotificationEnable(boolean isNotificationEnable) {
        KeepAliveManager.isNotificationEnable = isNotificationEnable;
    }

    public int getCloudInterval() {
        return cloudInterval;
    }

    /**
     * 在AccountHelper.addAppstoreAccount和KLServiceHelper.enableOrNot这个里面使用，用于控制是否开启拉活
     * @return
     */
    public boolean getConfigEnable() {
        return configEnable;
    }

    public void setCloudInterval(int cloudInterval) {
        KeepAliveManager.cloudInterval = cloudInterval;
    }

    public void setConfigEnable(boolean configEnable) {
        KeepAliveManager.configEnable = configEnable;
    }

    public boolean isForceAccountSync() {
        return true;
    }

    public boolean hadDoneAccountSync() {
        return doneAccountSync;
    }

    public void setDoneAccountSync(boolean doneAccountSync) {
        KeepAliveManager.doneAccountSync = doneAccountSync;
    }

    public void startPushService(Context mContext, int startType){
        if (!configEnable){//没有开启拉活就返回
            return;
        }
        Intent pushIntent = new Intent(mContext, MyNtestService.class);
        mContext.startService(pushIntent);
//        String dotting = "KeepAlive_SYNCADAPTER";
//        switch (startType){
//            case TYPE_SYNCADAPTER:
//                dotting = "KeepAlive_SYNCADAPTER";
//                break;
//            case TYPE_JOB_SCHEDULER:
//                dotting = "KeepAlive_SCHEDULER";
//                break;
//            case TYPE_NOTIFICATION:
//                dotting = "KeepAlive_NOTIFICATION";
//                break;
//        }
//        if (SystemInfo.DEBUG){
//            Log.e(TAG,dotting);
//        }
    }

    public void disableKeepAlive(Context context, boolean enable){
        final PackageManager pm = context.getPackageManager();
        final ComponentName klcompName = new ComponentName(context.getPackageName(), KLJobService.class.getName());
        final ComponentName compName = new ComponentName(context.getPackageName(), AuthenticationService.class.getName());
        ComponentName compName1 = new ComponentName(context.getPackageName(), SyncService.class.getName());
        ComponentName compName2 = new ComponentName(context.getPackageName(), SyncProvider.class.getName());

        if (pm != null) {
            pm.setComponentEnabledSetting(klcompName,  enable ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED : PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);
            pm.setComponentEnabledSetting(compName,
                    enable ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED : PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);
            pm.setComponentEnabledSetting(compName1,
                    enable ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED : PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);
            pm.setComponentEnabledSetting(compName2,
                    enable ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED : PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);
        }
    }
}

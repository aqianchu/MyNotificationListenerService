package com.scu.ntest;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Notification;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class AppStoreNotificationListenerService extends NotificationListenerService {
    public static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";

    private static final String TAG = "keepalive";

    public AppStoreNotificationListenerService() {
    }

    public static final String ACTION_NOTIFICATION_POSTED = "com.scu.ntest.MyNtestService";
    //    public static final String ACTION_NOTIFICATION_REMOVED = "com.qihoo.ntest.ACTION_NOTIFICATION_REMOVED";
    public static final String ACTION_NOTIFICATION_REMOVED = "com.scu.ntest.MyNtestService";
    public static final String PERMISSION_RECIVE_NOTIFICATION_CHANGED = "com.scu.ntest.MyNtestService";
    public static final String EXTRA_STATUSBAR_NOTIFICATION = "com.scu.ntest.MyNtestService";
    public static final String EXTRA_WRAPPER = "com.scu.ntest.MyNtestService";
    private static final Intent sPluginNotificationPosted = new Intent(ACTION_NOTIFICATION_POSTED);
    private static final Intent sPluginNotificationRemoved = new Intent(ACTION_NOTIFICATION_REMOVED);

    @Override
    public void onCreate() {
        Log.e(TAG,"AppStoreNotificationListenerService onCreate");
        super.onCreate();
        try {
            //在这里开启Push服务
            KeepAliveManager.startPushService(this,KeepAliveManager.TYPE_NOTIFICATION);
//            CoreService.startCoreService(this, null, CoreServiceStartType.TYPE_NOTIFICATION_LISTENER_START);
        } catch (Exception e) {
            if (SystemInfo.DEBUG) {
                Log.e(TAG, "onNotificationPosted", e);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG,"AppStoreNotificationListenerService onBind");
        try {
            //这里也得开启Push服务
//            CoreService.startCoreService(this, null, CoreServiceStartType.TYPE_NOTIFICATION_LISTENER_START);
            KeepAliveManager.startPushService(this,KeepAliveManager.TYPE_NOTIFICATION);
        } catch (Exception e) {
            if (SystemInfo.DEBUG) {
                Log.e(TAG, "onNotificationPosted", e);
            }
        }
        return super.onBind(intent);
    }

    private void startService(StatusBarNotification sbn, String action) {
        Log.e(TAG,"AppStoreNotificationListenerService startService");
        try {
            Intent newIntent = new Intent(action);
            newIntent.putExtra(EXTRA_STATUSBAR_NOTIFICATION, sbn);
            try {
                Bundle extras = new Bundle();
                extras.putBinder(EXTRA_WRAPPER, onBind(null));
                newIntent.putExtras(extras);
            } catch (Throwable e) {
            }
            try {
//                PluginTricker.queryPluginIntentAndStart(this, newIntent, null);
            } catch (Throwable e) {
                fixNotificationBug(sbn.getNotification());
                newIntent.putExtra(EXTRA_STATUSBAR_NOTIFICATION, sbn);
//                PluginTricker.queryPluginIntentAndStart(this, newIntent, null);
                if (SystemInfo.DEBUG) {
                    Log.e(TAG, "startService2", e);
                }
            }
        } catch (Throwable e) {
            if (SystemInfo.DEBUG) {
                Log.e(TAG, "startService1", e);
            }
        }
    }

    private void sendBroadcast(StatusBarNotification sbn, String action) {
        Log.e(TAG,"AppStoreNotificationListenerService sendBroadcast");
//        LogUtils.d("NotificationReceiver", "sendBroadcast");

        try {
            Intent newIntent = new Intent(action);
            newIntent.putExtra(EXTRA_STATUSBAR_NOTIFICATION, sbn);
            try {
                Bundle extras = new Bundle();
                extras.putBinder(EXTRA_WRAPPER, onBind(null));
                newIntent.putExtras(extras);
            } catch (Throwable e) {
            }
            try {
                sendBroadcast(newIntent, PERMISSION_RECIVE_NOTIFICATION_CHANGED);
            } catch (Throwable e) {
                fixNotificationBug(sbn.getNotification());
                newIntent.putExtra(EXTRA_STATUSBAR_NOTIFICATION, sbn);
                sendBroadcast(newIntent, PERMISSION_RECIVE_NOTIFICATION_CHANGED);
                if (SystemInfo.DEBUG) {
                    Log.e(TAG, "sendBroadcast2", e);
                }
            }
        } catch (Throwable e) {
            if (SystemInfo.DEBUG) {
                Log.e(TAG, "sendBroadcast1", e);
            }
        }
    }

    private void fixNotificationBug(Notification notification) {
        Log.e(TAG,"AppStoreNotificationListenerService fixNotification");
        try {
            //bugfix:一些手机上，通知中传送有extras的情况下，可能没法跨进程传送
            Bundle value = new Bundle();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (notification.extras != null && notification.extras.size() > 0) {
                    value.putAll(notification.extras);
                }
                notification.extras = value;
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                Field extrasField = Notification.class.getDeclaredField("extras");
                if (!extrasField.isAccessible()) {
                    extrasField.setAccessible(true);
                }
                Object extras = extrasField.get(notification);
                if (extras != null && extras instanceof Bundle) {
                    Bundle oldextras = ((Bundle) extras);
                    value.putAll(oldextras);
                }
                extrasField.set(notification, value);
            }
        } catch (Throwable e) {
            if (SystemInfo.DEBUG) {
                Log.e(TAG, "fixNotificationBug1", e);
            }
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                if (notification.actions != null && notification.actions.length > 0) {
                    Field mExtrasF = Notification.Action.class.getDeclaredField("mExtras");
                    if (mExtrasF != null) {
                        if (!mExtrasF.isAccessible()) {
                            mExtrasF.setAccessible(true);
                        }
                        for (Notification.Action action : notification.actions) {
                            Bundle extras = action.getExtras();
                            Bundle data = new Bundle(extras);
                            if (extras != null && extras.size() > 0) {
                                data.putAll(extras);
                            }
                            mExtrasF.set(action, data);
                        }
                    }

                }
            }
        } catch (Exception e) {
            if (SystemInfo.DEBUG) {
                Log.e(TAG, "fixNotificationBug2", e);
            }
        }
    }


    private void notifyPlugin(StatusBarNotification sbn, Intent intent) {
        sendBroadcast(sbn, intent.getAction());
        startService(sbn, intent.getAction());
    }

    @Override
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void onNotificationPosted(final StatusBarNotification sbn) {
        Log.e(TAG,"AppStoreNotificationListenerService onNotificationPosted");
        try {
//            try {
//                if (NotificationHongbaoHelper.checkNotificationIsHongbao(sbn)) {
//                    NotificationHongbaoHelper.detailStatusBarNotification(getApplicationContext(), sbn);
//                }
//            } catch (Throwable e) {
//                e.printStackTrace();
//            }

//            Log.d("keepalive", "onNotificationPosted");
//            final PeerManager pm = PCDaemonMgr.getInstance().getPeerManager();
//            if ( sbn.getNotification() !=null && sbn.getNotification().tickerText !=null && sbn.getNotification().contentIntent!=null ){
//                String hashCodeString = String.valueOf(sbn.getNotification().contentIntent.hashCode());
//                PCDaemonMgr.getInstance().addNotificationIntent(hashCodeString,sbn.getNotification().contentIntent);
//                byte[] temp = Base64.encode(sbn.getNotification().tickerText.toString().getBytes(), Base64.NO_WRAP);
//                String data = new String(temp, AppEnv.CHARSET);
//                ACSIITextPdu pdu = new ACSIITextPdu(PCCmdConsts.RET_LONG_CONNECT + PCCmdConsts.ACTIOIN_NOTIFICATION_CHANGED + ":"
//                        + sbn.getPackageName() + ":" + data + ":" + sbn.getPostTime() + ":" + hashCodeString,PduBase.TYPE_LONG_CONNECT);
//                pm.sendPduToAllLongSession(pdu);
//                CoreService.startCoreService(this, null, CoreServiceStartType.TYPE_NOTIFICATION_LISTENER_START);
//                notifyPlugin(sbn, sPluginNotificationPosted);
//            }
            KeepAliveManager.startPushService(this,KeepAliveManager.TYPE_NOTIFICATION);
            notifyPlugin(sbn,sPluginNotificationPosted);
        } catch (Exception e) {
            e.printStackTrace();
            if (SystemInfo.DEBUG) {
                Log.e(TAG, "onNotificationPosted", e);
            }
        }
    }

    @Override
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void onNotificationPosted(StatusBarNotification sbn, RankingMap rankingMap) {
        Log.e(TAG,"AppStoreNotificationListenerService onNotificationPosted2");
        super.onNotificationPosted(sbn, rankingMap);
    }

    @Override
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void onNotificationRemoved(StatusBarNotification sbn, RankingMap rankingMap) {
        Log.e(TAG,"AppStoreNotificationListenerService onNotificationRemoved");
        super.onNotificationRemoved(sbn, rankingMap);
    }

    @Override
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void onNotificationRankingUpdate(RankingMap rankingMap) {
        Log.e(TAG,"AppStoreNotificationListenerService onNotificationRankingUpdate");
        super.onNotificationRankingUpdate(rankingMap);
    }

    @Override
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void onInterruptionFilterChanged(int interruptionFilter) {
        Log.e(TAG,"AppStoreNotificationListenerService onInterruptionFilterChanged");
        super.onInterruptionFilterChanged(interruptionFilter);
    }

    @Override
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void onListenerConnected() {
        Log.e(TAG,"AppStoreNotificationListenerService onListenerConnected");
        super.onListenerConnected();
    }

    @Override
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.e(TAG,"AppStoreNotificationListenerService onNotificationRemoved");
//        super.onNotificationRemoved(sbn);
        try {
            //这里也得开启服务
            //CoreService.startCoreService(this, null, CoreServiceStartType.TYPE_NOTIFICATION_LISTENER_START);
            notifyPlugin(sbn, sPluginNotificationRemoved);
        } catch (Exception e) {
            if (SystemInfo.DEBUG) {
                Log.e(TAG, "onNotificationRemoved", e);
            }
        }
    }

    private static List<ComponentName> loadEnabledListeners(ContentResolver cr) {
        List<ComponentName> enabledListeners = new ArrayList<>();
        final String flat = Settings.Secure.getString(cr, ENABLED_NOTIFICATION_LISTENERS);
        if (flat != null && !"".equals(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                try {
                    final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                    if (cn != null) {
                        enabledListeners.add(cn);
                    }
                } catch (Exception e) {
                }
            }
        }
        return enabledListeners;
    }

    private static void saveEnabledListeners(ContentResolver cr, List<ComponentName> enabledListeners) {
        StringBuilder sb = null;
        for (ComponentName cn : enabledListeners) {
            if (sb == null) {
                sb = new StringBuilder();
            } else {
                sb.append(':');
            }
            sb.append(cn.flattenToString());
        }
        Settings.Secure.putString(cr, ENABLED_NOTIFICATION_LISTENERS, sb != null ? sb.toString() : "");
    }

    public static void setNotificationListeners(Context context, ComponentName cn, boolean enable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            if (checkWriteSecureSettingsPermission(context)) {
                if (enable) {
                    PackageManager pm = context.getPackageManager();
                    if (pm.getComponentEnabledSetting(cn) != PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
                        pm.setComponentEnabledSetting(cn, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                    }
                }
                ContentResolver contentResolver = context.getContentResolver();
                List<ComponentName> enabledListeners = loadEnabledListeners(contentResolver);
                if (enable) {
                    if (!enabledListeners.contains(cn)) {
                        enabledListeners.add(cn);
                        saveEnabledListeners(contentResolver, enabledListeners);
                    }
                } else {
                    if (enabledListeners.contains(cn)) {
                        enabledListeners.remove(enabledListeners);
                        saveEnabledListeners(contentResolver, enabledListeners);
                    }
                }

            } else {
                throw new SecurityException(Manifest.permission.WRITE_SECURE_SETTINGS + " not be granted on this devices(SDK=)" + Build.VERSION.SDK_INT);
            }
        } else {
            throw new UnsupportedOperationException("ENABLED_NOTIFICATION_LISTENERS not be supported on this devices(SDK=)" + Build.VERSION.SDK_INT);
        }
    }

    private static boolean checkWriteSecureSettingsPermission(Context context) {
        return context.checkPermission(Manifest.permission.WRITE_SECURE_SETTINGS, android.os.Process.myPid(), android.os.Process.myUid()) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean isNotificationListenersEnabled(Context context) {
        ComponentName cn = new ComponentName(context.getPackageName(), AppStoreNotificationListenerService.class.getName());
        return isNotificationListenersEnabled(context, cn);
    }


    private static boolean isNotificationListenersEnabled(Context context, ComponentName cn) {
        if (context == null || cn == null) {
            return false;
        }
        List<ComponentName> cns = loadEnabledListeners(context.getContentResolver());
        return cns.contains(cn);
    }

    public static boolean setNotificationListenersEnabled(Context context, boolean enable) {
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
                return false;
            }
            if (context == null) {
                return false;
            }
            PackageManager pm = context.getPackageManager();
            ComponentName cn = new ComponentName(context.getPackageName(), AppStoreNotificationListenerService.class.getName());
            if (enable) {
                pm.setComponentEnabledSetting(cn, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
            } else {
                pm.setComponentEnabledSetting(cn, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            }
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void onApplicationCreated(Context context) {
        Log.e(TAG,"onApplicationCreated");
        try {
            //android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS
            ComponentName cn = new ComponentName(context.getPackageName(), AppStoreNotificationListenerService.class.getName());
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
                PackageManager pm = context.getPackageManager();
                pm.setComponentEnabledSetting(cn, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                return;
            }

            if (!isNotificationListenersEnabled(context, cn)) {
                PackageManager pm = context.getPackageManager();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    if (checkWriteSecureSettingsPermission(context)) {
                        pm.setComponentEnabledSetting(cn, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                        setNotificationListeners(context, cn, true);
                    } else {
//                        if (CoreDaemonUtils.isEnable()) {
                        pm.setComponentEnabledSetting(cn, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
//                        }
                    }
                } else {
//                    if (CoreDaemonUtils.isEnable()) {
                    pm.setComponentEnabledSetting(cn, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
//                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

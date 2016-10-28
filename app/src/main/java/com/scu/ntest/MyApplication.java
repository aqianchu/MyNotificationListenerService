package com.scu.ntest;

import android.app.Application;

/**
 * Created by zhangqianchu on 2016/10/27.
 */
public class MyApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
//        KLServiceHelper.init(this);//通过JobService拉活初始化
        AppStoreNotificationListenerService.onApplicationCreated(this);
    }
}

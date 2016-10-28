package com.scu.ntest.account;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class AuthenticationService extends Service {
    private Authenticator mAuthenticator = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mAuthenticator = new Authenticator(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (mAuthenticator == null)
            return null;

        return mAuthenticator.getIBinder();
    }
}

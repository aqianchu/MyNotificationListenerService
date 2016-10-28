package com.scu.ntest.account;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.scu.ntest.KeepAliveManager;

public class SyncProvider extends ContentProvider {
    public static final String AUTHORITY = "com.scu.ntest.account.syncprovider";

    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String path = uri.getPath();
        if (TextUtils.isEmpty(path))
            return null;
//        if (! path.startsWith(CoreIntent.KEY_PROVIDER_START_APP))
//            return null;
//这里开启服务
        //   CoreService.startCoreService(Conte;xtUtils.getApplicationContext(), null, getStartType(selectionArgs));
        KeepAliveManager.startPushService(getContext(),KeepAliveManager.TYPE_SYNCADAPTER);
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

//    private int getStartType(String[] args){
//        int startType = CoreServiceStartType.TYPE_OTHER_START;
//        if (args == null)
//            return startType;
//
//        for(String arg : args){
//            if (TextUtils.isEmpty(arg))
//                continue;
//
//            String segments[] = arg.split("=");
//            if (segments.length != 2)
//                continue;
//
//            if (!segments[0].equalsIgnoreCase(CoreIntent.KEY_START_TYPE))
//                continue;
//
//            startType = Integer.valueOf(segments[1]);
//        }
//        return startType;
//    }
}
package com.scu.ntest.account;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.scu.ntest.KeepAliveManager;
import com.scu.ntest.R;

// 账号帮助类
public class AccountHelper {
    public static final String ACCOUNT_TYPE = "com.scu.ntest.account";

    public static boolean addAppstoreAccount(Context context){
        if (context == null)
            return false;

        boolean autoOpenSetting = KeepAliveManager.getConfigEnable() && (KeepAliveManager.isForceAccountSync() || !KeepAliveManager.hadDoneAccountSync());
        boolean bRet = true;
        try {
            // 添加账号
            Account account = new Account(context.getString(R.string.app_name), ACCOUNT_TYPE);
            AccountManager mAccountManager = AccountManager.get(context);
            if (!isAppstoreAccountExist(mAccountManager, context.getString(R.string.app_name))){
                mAccountManager.addAccountExplicitly(account, "", null);
            }

            //  不自动打开开关 则发送统计
            if (!autoOpenSetting) {
                sendAccountSyncStat(account,context);
            }else{
                //如果同步总开关关闭 则先开总开关
                if (!ContentResolver.getMasterSyncAutomatically()){
                    ContentResolver.setMasterSyncAutomatically(true);
                }

                ContentResolver.setSyncAutomatically(account, SyncProvider.AUTHORITY, true);
                KeepAliveManager.setDoneAccountSync(true);
            }
            ContentResolver.addPeriodicSync(account, SyncProvider.AUTHORITY, new Bundle(), KeepAliveManager.getCloudInterval());
        }catch (Exception e){
            bRet = false;
        }
        return bRet;
    }

    public static boolean setAccountSyncEnable(Context context, boolean enable){
        if (context == null)
            return false;

        Account account = new Account(context.getString(R.string.app_name), ACCOUNT_TYPE);
        if (enable){
            //如果同步总开关关闭 则先开总开关
            if (!ContentResolver.getMasterSyncAutomatically()){
                ContentResolver.setMasterSyncAutomatically(true);
            }

            ContentResolver.setSyncAutomatically(account, SyncProvider.AUTHORITY, true);
        }else{
            ContentResolver.setSyncAutomatically(account, SyncProvider.AUTHORITY, false);
        }
        return true;
    }

    private static boolean isAppstoreAccountExist(AccountManager mAccountManager, String name){
        if (mAccountManager == null || TextUtils.isEmpty(name))
            return false;

        Account[] accounts = mAccountManager.getAccountsByType(ACCOUNT_TYPE);
        if (accounts == null){
            return false;
        }

        boolean bExist = false;
        for (Account account : accounts){
            if (account != null && name.equalsIgnoreCase(account.name)){
                bExist = true;
                break;
            }
        }

        return bExist;
    }

    /**
     * 发送打点统计
     * @param account
     */
    private static void sendAccountSyncStat(Account account, Context mContext){
        boolean bMastOpen = ContentResolver.getMasterSyncAutomatically();
        boolean bAppstoreOpen = ContentResolver.getSyncAutomatically(account, SyncProvider.AUTHORITY);
        int nType = 0;
        if (bMastOpen && bAppstoreOpen){
            return;
        }else if(!bMastOpen && !bAppstoreOpen){
            nType = 3;
        }else if(!bMastOpen){
            nType = 1;
        }else if(!bAppstoreOpen){
            nType = 2;
        }
//        DottingUtil.onEvent(mContext, "TYPE_SYNCADAPTER");
//        DottingUtil.onEvent(Global.mContext, "Bottombar_bottom_menu_ComputerInfo");
//        StatHelper.onEvent(StatFieldConst.EventId.KEEP_ALIVE, StatFieldConst.Preference.KEEP_ALIVE_ACCOUNT_SYNC, String.valueOf(nType));
    }
}

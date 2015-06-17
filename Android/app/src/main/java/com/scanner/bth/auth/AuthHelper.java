package com.scanner.bth.auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.scanner.bth.Constants;
import com.scanner.bth.bluetoothscanner.FlowPickActivity;
import com.scanner.bth.bluetoothscanner.FlowPickFragment;
import com.scanner.bth.db.DbHelper;

/**
 * Created by shaon on 6/10/2015.
 */
public class AuthHelper {

    public static String getUsername(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                Constants.SharedPrefSettings.FILENAME, Context.MODE_PRIVATE);
        return sharedPref.getString(Constants.SharedPrefSettings.USERNAME, null);
    }

    public static void logout(Activity activity) {
        SharedPreferences sharedPref = activity.getSharedPreferences(
                Constants.SharedPrefSettings.FILENAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        String username = sharedPref.getString(Constants.SharedPrefSettings.USERNAME, null);
        editor.putString(Constants.SharedPrefSettings.USERNAME, null);
        editor.commit();

        

        Account account = new Account(username, FlowPickFragment.ACCOUNT_TYPE);
        AccountManager.get(activity).removeAccount(account, null, null);

    }

}

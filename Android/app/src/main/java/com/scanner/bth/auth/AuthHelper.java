package com.scanner.bth.auth;

import android.accounts.Account;
import android.accounts.AccountManager;

import android.content.Context;
import android.content.SharedPreferences;

import com.scanner.bth.Constants;

import com.scanner.bth.bluetoothscanner.FlowPickFragment;

/**
 * Created by shaon on 6/10/2015.
 */
public class AuthHelper {

    public static String getUsername(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                Constants.SharedPrefSettings.FILENAME, Context.MODE_PRIVATE);
        return sharedPref.getString(Constants.SharedPrefSettings.USERNAME, null);
    }

    public static String getSyncEmail(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                Constants.SharedPrefSettings.FILENAME, Context.MODE_PRIVATE);
        return sharedPref.getString(Constants.SharedPrefSettings.SYNC_EMAIL, null);
    }

    public static void setSyncEmail(String email, Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                Constants.SharedPrefSettings.FILENAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Constants.SharedPrefSettings.SYNC_EMAIL, email);
        editor.commit();
    }

    public static void logout(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                Constants.SharedPrefSettings.FILENAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        String username = sharedPref.getString(Constants.SharedPrefSettings.USERNAME, null);
        editor.putString(Constants.SharedPrefSettings.USERNAME, null);
        editor.commit();

        

        Account account = new Account(username, FlowPickFragment.ACCOUNT_TYPE);
        AccountManager.get(context).removeAccount(account, null, null);

    }

}

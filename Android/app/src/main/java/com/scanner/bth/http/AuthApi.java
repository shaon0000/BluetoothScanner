package com.scanner.bth.http;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.scanner.bth.Constants;
import com.scanner.bth.db.AccountDetails;
import com.scanner.bth.db.DbHelper;

import java.util.UUID;

/**
 * Created by shaon on 6/9/2015.
 */
public class AuthApi {

    public String userSignIn(String name, String password, String authTokenType, Context context) {
        // Since this is mainly a test, we won't actually ping a server
        // here to check out name/password

        if ((name.contentEquals("p@c") && password.contentEquals("qwe")) ||
                (name.contentEquals("gtrainor@cannonservices.ca") && password.contentEquals("User"))) {
            SharedPreferences sharedPref = context.getSharedPreferences(Constants.SharedPrefSettings.FILENAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(Constants.SharedPrefSettings.USERNAME, name);
            editor.commit();
            Log.d(AuthApi.class.getSimpleName(), "commited username:" + name);
            boolean isAdmin = name.contentEquals("p@c");
            AccountDetails details = new AccountDetails(isAdmin);
            DbHelper.getInstance().insertAccountDetails(details);
            return UUID.randomUUID().toString();

        } else {
            return null;
        }
    }


}

package com.scanner.bth.http;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.scanner.bth.Constants;
import com.scanner.bth.db.AccountDetails;
import com.scanner.bth.db.DbHelper;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by shaon on 6/9/2015.
 */
public class AuthApi {
    static HashMap<String, String> name_pass_combos = new HashMap<String, String>();
    static HashMap<String, Boolean> is_admin = new HashMap<>();
    static {
        name_pass_combos.put("pfarrell@cannonservices.ca", "Admin");
        is_admin.put("pfarrell@cannonservices.ca", true);
        name_pass_combos.put("gtrainor@cannonservices.ca", "User");
        is_admin.put("gtrainor@cannonservices.ca", false);
        name_pass_combos.put("p@c", "qwe");
        is_admin.put("p@c", true);
        name_pass_combos.put("g@c", "asd");
        is_admin.put("g@c", false);
    }

    static {

    }
    public String userSignIn(String name, String password, String authTokenType, Context context) {
        // Since this is mainly a test, we won't actually ping a server
        // here to check out name/password

        if (name_pass_combos.containsKey(name) && name_pass_combos.get(name).contentEquals(password)) {
            SharedPreferences sharedPref = context.getSharedPreferences(Constants.SharedPrefSettings.FILENAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(Constants.SharedPrefSettings.USERNAME, name);
            editor.commit();
            Log.d(AuthApi.class.getSimpleName(), "commited username:" + name);
            boolean isAdmin = is_admin.get(name);
            AccountDetails details = new AccountDetails(isAdmin);
            DbHelper.getInstance().insertAccountDetails(details);
            return UUID.randomUUID().toString();

        } else {
            return null;
        }
    }


}

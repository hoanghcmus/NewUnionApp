package com.newunion.newunionapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * <p>  Preference Util for storing the login information
 * </p>
 * Created by Nguyen Ngoc Hoang on 7/20/2016.
 *
 * @author Nguyen Ngoc Hoang (www.hoangvnit.com)
 */
public class LoginPreferences {
    private static final String PREFERENCES_NAME = "login_prefs";

    private static final String TOKEN_KEY = "logged_in_token";

    private static SharedPreferences pref(Context context) {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized boolean isLogged(Context context) {
        return !TextUtils.isEmpty(getToken(context));
    }

    public static synchronized void saveToken(Context context, String token) {
        pref(context).edit().putString(TOKEN_KEY, token).apply();
    }

    public static synchronized String getToken(Context context) {
        return pref(context).getString(TOKEN_KEY, "");
    }

    public static void removeToken(Context context) {
        pref(context).edit().remove(TOKEN_KEY).apply();
    }
}

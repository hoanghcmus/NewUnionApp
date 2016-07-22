package com.newunion.newunionapp.utils;

import android.util.Log;

import com.newunion.newunionapp.BuildConfig;

/**
 * <p> Log Utilities
 * </p>
 * Created by Nguyen Ngoc Hoang on 7/20/2016.
 *
 * @author Nguyen Ngoc Hoang (www.hoangvnit.com)
 */
public class NewUnionLog {
    private final static String TAG_PROJECT = "NewUnionApp";

    public static void d(String text) {

        if (BuildConfig.DEBUG)
            Log.d(TAG_PROJECT, getMessage(text));
    }

    public static void e(String text) {
        if (BuildConfig.DEBUG)
            Log.e(TAG_PROJECT, getMessage(text));
    }

    public static void i(String text) {
        if (BuildConfig.DEBUG)
            Log.i(TAG_PROJECT, getMessage(text));
    }

    public static void w(String text) {
        if (BuildConfig.DEBUG)
            Log.w(TAG_PROJECT, getMessage(text));
    }

    public static void v(String text) {
        if (BuildConfig.DEBUG)
            Log.v(TAG_PROJECT, getMessage(text));
    }

    public static void systemOut(String msg) {
        if (BuildConfig.DEBUG) {
            System.out.println(getMessage(msg));
        }
    }

    public static void printStackTrace(Exception e) {
        if (BuildConfig.DEBUG) {
            e.printStackTrace();
        }
    }

    private static String getMessage(String text) {
        return text == null ? "message null" : text;
    }
}

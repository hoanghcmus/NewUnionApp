package com.newunion.newunionapp;

import android.app.Application;

import com.newunion.newunionapp.rest.RestClient;

/**
 * <p> Base Application class
 * </p>
 * Created by Nguyen Ngoc Hoang on 7/20/2016.
 *
 * @author Nguyen Ngoc Hoang (www.hoangvnit.com)
 */
public class NewUnionApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        /**
         * Init Rest API when start application
         */
        RestClient.getInstance().init(this);
    }
}

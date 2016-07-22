package com.newunion.newunionapp.rest;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.newunion.newunionapp.rest.api.UsersService;
import com.newunion.newunionapp.utils.CONSTANTS;
import com.newunion.newunionapp.utils.LoginPreferences;
import com.newunion.newunionapp.utils.NetworkUtils;
import com.newunion.newunionapp.utils.NoNetworkException;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

/**
 * <p> Configurations for Rest APIs
 * </p>
 * Created by Nguyen Ngoc Hoang on 7/20/2016.
 *
 * @author Nguyen Ngoc Hoang (www.hoangvnit.com)
 */
public class RestClient {

    private static final String TAG = "RestClient";

    private static RestClient sInstance = null;

    private OkHttpClient mHttpClient;

    private UsersService mUserService;

    private boolean isInitialized = false;

    public static synchronized RestClient getInstance() {
        if (sInstance == null) {
            sInstance = new RestClient();
        }
        return sInstance;
    }

    private void setupCache(Context context) {
        try {
            File cacheDir = new File(context.getCacheDir(), CONSTANTS.OK_HTTP_CLIENT_CACHE_FILE_NAME);
            Cache cache = new Cache(cacheDir, CONSTANTS.OK_HTTP_CLIENT_CACHE_SIZE);
            mHttpClient.setCache(cache);
            mHttpClient.setReadTimeout(CONSTANTS.OK_HTTP_CLIENT_TIMEOUT, TimeUnit.SECONDS);
            mHttpClient.setConnectTimeout(CONSTANTS.OK_HTTP_CLIENT_TIMEOUT, TimeUnit.SECONDS);
            mHttpClient.setWriteTimeout(CONSTANTS.OK_HTTP_CLIENT_TIMEOUT, TimeUnit.SECONDS);
            mHttpClient.interceptors().add(new NetworkInterceptor(context));
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    public void init(final Context context) {
        if (isInitialized) {
            return;
        }

        mHttpClient = new OkHttpClient();
        setupCache(context);

        Gson gson = new GsonBuilder().setDateFormat(CONSTANTS.GSON_DATE_FORMAT).create();

        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestInterceptor.RequestFacade request) {
                if (LoginPreferences.isLogged(context)) {
                    request.addHeader(CONSTANTS.REQUEST_HEADER_AUTHORIZATION, "m " + LoginPreferences.getToken(context));
                }

                request.addHeader(CONSTANTS.REQUEST_HEADER_CONTENT_TYPE, CONSTANTS.REQUEST_HEADER_CONTENT_TYPE_JSON_VALUE);
            }
        };

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(CONSTANTS.REST_ROOT_URL)
                .setConverter(new GsonConverter(gson))
                .setRequestInterceptor(requestInterceptor)
                .setClient(new OkClient(mHttpClient))
                .build();

        mUserService = restAdapter.create(UsersService.class);

        isInitialized = true;
    }

    public UsersService getUserService() {
        return mUserService;
    }

    class NetworkInterceptor implements Interceptor {

        Context mContext;

        public NetworkInterceptor(Context context) {
            mContext = context;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            if (mContext != null && !NetworkUtils.isNetworkConnected(mContext)) {
                throw new NoNetworkException();
            }
            mContext = null;
            return chain.proceed(chain.request());
        }
    }
}

package com.newunion.newunionapp.utils;

/**
 * <p> App's configuration - constants
 * </p>
 * Created by Nguyen Ngoc Hoang on 7/20/2016.
 *
 * @author Nguyen Ngoc Hoang (www.hoangvnit.com)
 */
public class CONSTANTS {

    public static final String REST_ROOT_URL = "http://54.255.201.10:9000";
    public static final String GSON_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
    public static final String REQUEST_HEADER_CONTENT_TYPE = "Content-Type";
    public static final String REQUEST_HEADER_CONTENT_TYPE_JSON_VALUE = "application/json";
    public static final String REQUEST_HEADER_AUTHORIZATION = "Authorization";

    public static final int OK_HTTP_CLIENT_CACHE_SIZE = 10 * 1024 * 1024;
    public static final int OK_HTTP_CLIENT_TIMEOUT = 30;
    public static final String OK_HTTP_CLIENT_CACHE_FILE_NAME = "responses";

    /***********************************************************************************************
     * CONFIGURATION FOR REAL TIME API
     **********************************************************************************************/

    public static final String STREAM_DATA_PROXY_ADDRESS = "https://streamdata.motwin.net/";
    public static final String REST_API_FETCH_NEW_ADDRESS = REST_ROOT_URL + "/users";
    public static final String STREAM_DATA_TOKEN_HEADER_NAME = "X-Sd-Token";
    public static final String STREAM_DATA_TOKEN_HEADER_VALUE = "YzU0YjBhNWItOGY4NC00N2VmLWIxZmEtMTIzNTNjY2JjZWM1";

}

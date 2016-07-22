package com.newunion.newunionapp.utils;

import java.io.IOException;

/**
 * <p> Exception for network down (No network connection).
 * Use this for intercept in {@link com.newunion.newunionapp.rest.RestClient}
 * </p>
 * Created by Nguyen Ngoc Hoang on 7/20/2016.
 *
 * @author Nguyen Ngoc Hoang (www.hoangvnit.com)
 */
public class NoNetworkException extends IOException {

    public NoNetworkException() {
        super("No network exception.");
    }
}

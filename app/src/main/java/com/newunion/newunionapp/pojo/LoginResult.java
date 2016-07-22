package com.newunion.newunionapp.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * <p> Login model
 * </p>
 * Created by Nguyen Ngoc Hoang on 7/20/2016.
 *
 * @author Nguyen Ngoc Hoang (www.hoangvnit.com)
 */
public class LoginResult implements Parcelable {

    @SerializedName("token")
    private String mToken;

    @SerializedName("user")
    private User mUserInfo;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mToken);
        dest.writeParcelable(this.mUserInfo, flags);
    }

    public LoginResult() {
    }

    public LoginResult(String token, User userInfo) {
        mToken = token;
        mUserInfo = userInfo;
    }

    public String getToken() {
        return mToken;
    }

    public void setToken(String token) {
        mToken = token;
    }

    public User getUserInfo() {
        return mUserInfo;
    }

    public void setUserInfo(User userInfo) {
        mUserInfo = userInfo;
    }

    protected LoginResult(Parcel in) {
        this.mToken = in.readString();
        this.mUserInfo = in.readParcelable(User.class.getClassLoader());
    }

    public static final Creator<LoginResult> CREATOR = new Creator<LoginResult>() {
        @Override
        public LoginResult createFromParcel(Parcel source) {
            return new LoginResult(source);
        }

        @Override
        public LoginResult[] newArray(int size) {
            return new LoginResult[size];
        }
    };
}

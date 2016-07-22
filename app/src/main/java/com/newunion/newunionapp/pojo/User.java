package com.newunion.newunionapp.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.newunion.newunionapp.utils.CONSTANTS;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <p> User model
 * </p>
 * Created by Nguyen Ngoc Hoang on 7/20/2016.
 *
 * @author Nguyen Ngoc Hoang (www.hoangvnit.com)
 */
public class User implements Parcelable, Comparable<User> {

    @SerializedName("name")
    private String mName;

    @SerializedName("email")
    private String mEmail;

    @SerializedName("password")
    private String mPassword;

    @SerializedName("avatar")
    private String mAvatar;

    @SerializedName("created")
    private Date createdDate;

    public User(String name, String email, String password) {
        mName = name;
        mEmail = email;
        mPassword = password;
    }

    public User() {

    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public String getAvatar() {
        return mAvatar;
    }

    public void setAvatar(String avatar) {
        mAvatar = avatar;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mName);
        dest.writeString(this.mEmail);
        dest.writeString(this.mPassword);
        dest.writeString(this.mAvatar);
        dest.writeString(this.createdDate.toString());
    }

    protected User(Parcel in) {
        this.mName = in.readString();
        this.mEmail = in.readString();
        this.mPassword = in.readString();
        this.mAvatar = in.readString();
        DateFormat df = new SimpleDateFormat(CONSTANTS.GSON_DATE_FORMAT);
        try {
            this.createdDate = df.parse(in.readString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int compareTo(User another) {
        int result = createdDate.compareTo(another.getCreatedDate());
        if (result == 0)
            return 0;
        else if (result > 0)
            return -1;
        else return 1;
    }
}

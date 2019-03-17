package com.pdking.convenientmeeting.db;

import android.os.Parcel;
import android.os.Parcelable;

import org.litepal.crud.LitePalSupport;

/**
 * @author liupeidong
 * Created on 2019/3/16 11:28
 */
public class UserToken extends LitePalSupport implements Parcelable {

    @Override
    public String toString() {
        return "UserToken{" +
                "token='" + token + '\'' +
                '}';
    }

    private String token;

    public UserToken(String token) {
        this.token = token;
    }

    public UserToken() {
    }

    protected UserToken(Parcel in) {
        token = in.readString();
    }

    public static final Creator<UserToken> CREATOR = new Creator<UserToken>() {
        @Override
        public UserToken createFromParcel(Parcel in) {
            return new UserToken(in);
        }

        @Override
        public UserToken[] newArray(int size) {
            return new UserToken[size];
        }
    };

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(token);
    }
}

package com.pdking.convenientmeeting.db;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.litepal.crud.LitePalSupport;

/**
 * @author liupeidong
 * Created on 2019/3/16 10:35
 */
public class LoginBean extends LitePalSupport implements Parcelable {
    @SerializedName("status")
    public int status;
    @SerializedName("msg")
    public String msg;
    @SerializedName("data")
    public UserInfo data;

    protected LoginBean(Parcel in) {
        status = in.readInt();
        msg = in.readString();
    }

    public static final Creator<LoginBean> CREATOR = new Creator<LoginBean>() {
        @Override
        public LoginBean createFromParcel(Parcel in) {
            return new LoginBean(in);
        }

        @Override
        public LoginBean[] newArray(int size) {
            return new LoginBean[size];
        }
    };

    @Override
    public String toString() {
        return "LoginBean{" +
                "status=" + status +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(status);
        dest.writeString(msg);
    }
}

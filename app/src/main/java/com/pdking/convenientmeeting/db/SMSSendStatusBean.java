package com.pdking.convenientmeeting.db;

import com.google.gson.annotations.SerializedName;

/**
 * @author liupeidong
 * Created on 2019/3/10 21:24
 */
public class SMSSendStatusBean {
    @SerializedName("status")
    public int status;
    @SerializedName("data")
    public String data;
}

package com.pdking.convenientmeeting.db;

import com.google.gson.annotations.SerializedName;

/**
 * @author liupeidong
 * Created on 2019/3/10 21:36
 */
public class RequestReturnBean {

    @SerializedName("status")
    public int status;
    @SerializedName("msg")
    public String msg;
}

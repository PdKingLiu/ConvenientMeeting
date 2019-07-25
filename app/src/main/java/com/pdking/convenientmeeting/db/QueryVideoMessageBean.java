package com.pdking.convenientmeeting.db;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author liupeidong
 * Created on 2019/5/10 14:52
 */
public class QueryVideoMessageBean {

    @SerializedName("status")
    public int status;
    @SerializedName("data")
    public List<VideoMessageBean> data;

}

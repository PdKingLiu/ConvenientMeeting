package com.pdking.convenientmeeting.db;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author liupeidong
 * Created on 2019/4/11 11:27
 */
public class FileDataListBean {
    @SerializedName("status")
    public int status;
    @SerializedName("data")
    public List<FileData> data;
}

package com.pdking.convenientmeeting.db;

import com.google.gson.annotations.SerializedName;

/**
 * @author liupeidong
 * Created on 2019/4/11 11:28
 */
public class FileData {
    @SerializedName("id")
    public int id;
    @SerializedName("meetingId")
    public int meetingId;
    @SerializedName("fileName")
    public String fileName;
    @SerializedName("fileUrl")
    public String fileUrl;
    @SerializedName("fileSize")
    public int fileSize;
    @SerializedName("uploader")
    public String uploader;
    @SerializedName("uploadTime")
    public long uploadTime;
}

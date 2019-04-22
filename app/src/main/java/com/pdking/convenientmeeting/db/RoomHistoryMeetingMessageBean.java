package com.pdking.convenientmeeting.db;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author liupeidong
 * Created on 2019/4/22 21:36
 */
public class RoomHistoryMeetingMessageBean {
    @SerializedName("status")
    public int status;
    @SerializedName("data")
    public DataBean data;
    public static class DataBean {
        @SerializedName("total")
        public int total;
        @SerializedName("pageNum")
        public int pageNum;
        @SerializedName("pageSize")
        public int pageSize;
        @SerializedName("size")
        public int size;
        @SerializedName("startRow")
        public int startRow;
        @SerializedName("endRow")
        public int endRow;
        @SerializedName("pages")
        public int pages;
        @SerializedName("prePage")
        public int prePage;
        @SerializedName("nextPage")
        public int nextPage;
        @SerializedName("isFirstPage")
        public boolean isFirstPage;
        @SerializedName("isLastPage")
        public boolean isLastPage;
        @SerializedName("hasPreviousPage")
        public boolean hasPreviousPage;
        @SerializedName("hasNextPage")
        public boolean hasNextPage;
        @SerializedName("navigatePages")
        public int navigatePages;
        @SerializedName("navigateFirstPage")
        public int navigateFirstPage;
        @SerializedName("navigateLastPage")
        public int navigateLastPage;
        @SerializedName("list")
        public List<RoomHistoryMeetingMessage> list;
        @SerializedName("navigatepageNums")
        public List<Integer> navigatepageNums;

    }
}

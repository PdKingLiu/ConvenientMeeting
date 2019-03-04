package com.pdking.convenientmeeting.db;

import java.util.List;

/**
 * @author liupeidong
 * Created on 2019/3/2 21:06
 */
public class MeetingRoomBean {

    private int id;

    private String roomName;

    private String machineNumber;

    private int status;

    private List<MeetingBean> meetingLists;

    private List<MeetingBean> recentlyMeetingLists;

    public void setId(int id) {
        this.id = id;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public void setMachineNumber(String machineNumber) {
        this.machineNumber = machineNumber;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setMeetingLists(List<MeetingBean> meetingLists) {
        this.meetingLists = meetingLists;
    }

    public void setRecentlyMeetingLists(List<MeetingBean> recentlyMeetingLists) {
        this.recentlyMeetingLists = recentlyMeetingLists;
    }

    public int getId() {
        return id;
    }

    public String getRoomName() {
        return roomName;
    }

    public String getMachineNumber() {
        return machineNumber;
    }

    public int getStatus() {
        return status;
    }

    public List<MeetingBean> getMeetingLists() {
        return meetingLists;
    }

    public List<MeetingBean> getRecentlyMeetingLists() {
        return recentlyMeetingLists;
    }
}
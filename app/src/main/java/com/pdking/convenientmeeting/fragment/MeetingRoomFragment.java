package com.pdking.convenientmeeting.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pdking.convenientmeeting.R;

public class MeetingRoomFragment extends Fragment {

    private static MeetingRoomFragment meetingRoomFragment;

    public MeetingRoomFragment() {
    }

    public static MeetingRoomFragment newInstance( ) {
        if (meetingRoomFragment == null) {
            meetingRoomFragment = new MeetingRoomFragment();
        }
        return meetingRoomFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_fragment_meeting_room, container, false);
    }

}

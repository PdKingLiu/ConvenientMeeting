package com.pdking.convenientmeeting.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pdking.convenientmeeting.R;

public class MeetingHistoryFragment extends Fragment {

    private static MeetingHistoryFragment meetingHistoryFragment;

    public MeetingHistoryFragment() {
    }

    public static MeetingHistoryFragment newInstance( ) {
        if (meetingHistoryFragment == null) {
            meetingHistoryFragment = new MeetingHistoryFragment();
        }
        return meetingHistoryFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.layout_fragment_meeting_history, container, false);
    }


}

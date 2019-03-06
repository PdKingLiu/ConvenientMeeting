package com.pdking.convenientmeeting.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pdking.convenientmeeting.R;

public class DayMeetingListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "title";

    private String title;

    private TextView textView;


    public DayMeetingListFragment() {
    }

    public static DayMeetingListFragment newInstance(String param1 ) {
        DayMeetingListFragment fragment = new DayMeetingListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_day_meeting_list, container, false);
        textView = view.findViewById(R.id.text);
        textView.setText(title);
        return view;
    }

}

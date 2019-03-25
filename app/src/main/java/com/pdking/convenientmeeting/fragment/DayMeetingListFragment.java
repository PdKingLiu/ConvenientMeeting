package com.pdking.convenientmeeting.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.adapter.MeetingListAdapter;
import com.pdking.convenientmeeting.db.MeetingMessage;

import java.util.ArrayList;
import java.util.List;

public class DayMeetingListFragment extends Fragment {

    private static final String ARG_PARAM = "title";

    private String title;

    private TextView textView;

    private List<MeetingMessage> meetingList;

    private TextView tvHaveNothing;

    private RecyclerView rvMeetingList;

    private MeetingListAdapter adapter;

    public DayMeetingListFragment() {
    }

    public static DayMeetingListFragment newInstance(String param) {
        DayMeetingListFragment fragment = new DayMeetingListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM, param);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(ARG_PARAM);
        }
    }

    public void setList(List<MeetingMessage> meetings) {
        Log.d("Lpp", "setList: ");
        if (meetingList != null) {
            meetingList.addAll(meetings);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (meetingList.size() == 0) {
                        setHaveNothingVisible(true);
                        setMeetingListVisible(false);
                    } else {
                        setHaveNothingVisible(false);
                        setMeetingListVisible(true);
                        adapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("Lpp", "onCreateView: ");
        View view = inflater.inflate(R.layout.layout_day_meeting_list, container, false);
        tvHaveNothing = view.findViewById(R.id.tv_have_nothing);
        rvMeetingList = view.findViewById(R.id.rv_meeting_list);
        tvHaveNothing.setText(title);
        initRecyclerView();
        return view;
    }

    private void initRecyclerView() {
        meetingList = new ArrayList<>();
        adapter = new MeetingListAdapter(meetingList);
        rvMeetingList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvMeetingList.setAdapter(adapter);
        setHaveNothingVisible(true);
        setMeetingListVisible(false);
    }

    public void setHaveNothingVisible(final boolean visible) {
        if (visible) {
            tvHaveNothing.setVisibility(View.VISIBLE);
        } else {
            tvHaveNothing.setVisibility(View.GONE);

        }
    }

    public void setMeetingListVisible(final boolean visible) {
        if (visible) {
            rvMeetingList.setVisibility(View.VISIBLE);
        } else {
            rvMeetingList.setVisibility(View.GONE);
        }
    }

}

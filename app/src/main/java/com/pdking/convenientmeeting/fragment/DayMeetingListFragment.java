package com.pdking.convenientmeeting.fragment;

import android.content.Intent;
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
import com.pdking.convenientmeeting.activity.MeetingDetailsActivity;
import com.pdking.convenientmeeting.adapter.MeetingListAdapter;
import com.pdking.convenientmeeting.db.RoomOfMeetingMessage;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DayMeetingListFragment extends Fragment {

    private static final String ARG_PARAM = "title";
    private static final String ARG_PARAM2 = "number";

    private String title;

    private List<RoomOfMeetingMessage> meetingList;

    private List<RoomOfMeetingMessage> allMeetingList;

    private TextView tvHaveNothing;

    private RecyclerView rvMeetingList;

    private MeetingListAdapter adapter;

    private String number;

    public DayMeetingListFragment() {
    }

    public static DayMeetingListFragment newInstance(String param, String number) {
        DayMeetingListFragment fragment = new DayMeetingListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM, param);
        args.putString(ARG_PARAM2, number);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(ARG_PARAM);
            number = getArguments().getString(ARG_PARAM2);
        }
        meetingList = new ArrayList<>();
        allMeetingList = LitePal.findAll(RoomOfMeetingMessage.class);
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

    private int getRelativeData(long startTime) {
        int today;
        int startDay;
        Calendar cale = Calendar.getInstance();
        cale.setTime(new Date(System.currentTimeMillis()));
        today = cale.get(Calendar.DAY_OF_YEAR);
        cale.setTime(new Date(startTime));
        startDay = cale.get(Calendar.DAY_OF_YEAR);
        return startDay - today;
    }

    private void initRecyclerView() {
        int i = Integer.parseInt(number);
        for (RoomOfMeetingMessage meeting : allMeetingList) {
            int data = getRelativeData(meeting.startTime) + 2;
            if (data == i) {
                meetingList.add(meeting);
            }
        }
        Log.d("Lpp", "initRecyclerView: " + meetingList.size());
        adapter = new MeetingListAdapter(meetingList);
        adapter.setOnItemClickListener(new MeetingListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getContext(), MeetingDetailsActivity.class);
                intent.putExtra("meetingId", meetingList.get(position).meetingId + "");
                startActivity(intent);
            }
        });
        rvMeetingList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvMeetingList.setAdapter(adapter);
        if (meetingList.size() == 0) {
            setHaveNothingVisible(true);
            setMeetingListVisible(false);
        } else {
            setHaveNothingVisible(false);
            setMeetingListVisible(true);
        }
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

    public void notifyDataChanged() {
        allMeetingList = LitePal.findAll(RoomOfMeetingMessage.class);
        meetingList.clear();
        int i = Integer.parseInt(number);
        for (RoomOfMeetingMessage meeting : allMeetingList) {
            int data = getRelativeData(meeting.startTime) + 2;
            if (data == i) {
                meetingList.add(meeting);
            }
        }
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

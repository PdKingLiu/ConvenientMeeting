package com.pdking.convenientmeeting.adapter;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.haozhang.lib.SlantedTextView;
import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.db.OneMeetingRoomMessage;

import java.util.List;

/**
 * @author liupeidong
 * Created on 2019/3/2 20:52
 */
public class MeetingRoomAdapter extends RecyclerView.Adapter<MeetingRoomAdapter.ViewHolder>
        implements View.OnClickListener {

    private List<OneMeetingRoomMessage> roomMessageList;
    private OnItemClickListener itemClickListener;

    public MeetingRoomAdapter(List<OneMeetingRoomMessage> roomMessageList) {
        this.roomMessageList = roomMessageList;
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        if (itemClickListener != null) {
            itemClickListener.onItemClick(v, (Integer) v.getTag());
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout
                .item_meeting_room, viewGroup, false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        OneMeetingRoomMessage oneMeetingRoomMessage = roomMessageList.get(i);
        viewHolder.setDate(oneMeetingRoomMessage, i);
    }

    @Override
    public int getItemCount() {
        return roomMessageList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRoomName;
        TextView tvMeetingSum;
        TextView tvPeopleSum;
        ImageView ivRecentMeeting;
        SlantedTextView stvStatus;
        View line;
        View view;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
            tvRoomName = itemView.findViewById(R.id.tv_room_name);
            tvMeetingSum = itemView.findViewById(R.id.tv_recent_meeting_sum);
            tvPeopleSum = itemView.findViewById(R.id.tv_people_sum);
            stvStatus = itemView.findViewById(R.id.stv_status);
            line = itemView.findViewById(R.id.view_line);
            ivRecentMeeting = itemView.findViewById(R.id.iv_recent_meeting);
        }

        void setDate(OneMeetingRoomMessage oneMeetingRoomMessage, int i) {
            view.setTag(i);
            if (i == roomMessageList.size() - 1) {
                line.setVisibility(View.GONE);
            } else {
                line.setVisibility(View.VISIBLE);
            }
            tvRoomName.setText(oneMeetingRoomMessage.roomNumber);
            switch (oneMeetingRoomMessage.status) {
                case 1:
                    stvStatus.setText("空闲");
                    stvStatus.setSlantedBackgroundColor(view.getContext().getResources().getColor(R.color
                            .pie_green));
                    break;
                case 2:
                    stvStatus.setText("使用中");
                    stvStatus.setSlantedBackgroundColor(view.getContext().getResources().getColor(R.color
                            .pie_orange));
                    break;
                case 3:
                    stvStatus.setText("维修");
                    stvStatus.setSlantedBackgroundColor(Color.LTGRAY);
                    break;
            }
            if (oneMeetingRoomMessage.recentlyMeetings != null) {
                if (oneMeetingRoomMessage.recentlyMeetings.size() != 0) {
                    tvMeetingSum.setText("近五天有" + oneMeetingRoomMessage.recentlyMeetings.size() +
                            "场会议");
                    ivRecentMeeting.setImageResource(R.mipmap.icon_recent_meeting_sum);
                } else {
                    tvMeetingSum.setText("近期暂无会议");
                    ivRecentMeeting.setImageResource(R.mipmap.icon_recent_meeting_sum_0);
                }
            } else {
                tvMeetingSum.setText("近期暂无会议");
                ivRecentMeeting.setImageResource(R.mipmap.icon_recent_meeting_sum_0);
            }
            tvPeopleSum.setText("可容纳" + oneMeetingRoomMessage.content + "人");
        }
    }

}

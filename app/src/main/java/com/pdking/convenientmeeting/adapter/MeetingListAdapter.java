package com.pdking.convenientmeeting.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.db.RoomOfMeetingMessage;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author liupeidong
 * Created on 2019/3/25 16:38
 */
public class MeetingListAdapter extends RecyclerView.Adapter<MeetingListAdapter.ViewHolder>
        implements View.OnClickListener {

    private List<RoomOfMeetingMessage> meetingList;
    private Calendar calendar = Calendar.getInstance();
    private Date dt = new Date();
    private OnItemClickListener onItemClickListener;

    public MeetingListAdapter(List<RoomOfMeetingMessage> meetingList) {
        this.meetingList = meetingList;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout
                .item_meeting_room_meeting, viewGroup, false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        RoomOfMeetingMessage message = meetingList.get(i);
        viewHolder.setData(message, i);
    }

    @Override
    public int getItemCount() {
        return meetingList.size();
    }

    @Override
    public void onClick(View v) {
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(v, (Integer) v.getTag());
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        View viewUpLine;
        TextView tvMeetingName;
        TextView tvMeetingMessage;
        TextView tvMeetingStatus;
        TextView tvMeetingTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
            tvMeetingName = itemView.findViewById(R.id.tv_meeting_name);
            tvMeetingMessage = itemView.findViewById(R.id.tv_meeting_message);
            tvMeetingStatus = itemView.findViewById(R.id.tv_meeting_status);
            tvMeetingTime = itemView.findViewById(R.id.tv_meeting_time);
            viewUpLine = itemView.findViewById(R.id.view_up_line);
        }

        public void setData(RoomOfMeetingMessage data, int i) {
            view.setTag(i);
            tvMeetingName.setText(data.meetingName);
            tvMeetingMessage.setText(data.meetingIntro);
            switch (data.status) {
                case 1:
                    tvMeetingStatus.setText("已完成");
                    break;
                case 2:
                    tvMeetingStatus.setText("正在进行");
                    break;
                case 3:
                    tvMeetingStatus.setText("即将进行");
                    break;
            }
            if (i == 0) {
                viewUpLine.setVisibility(View.VISIBLE);
            }
            dt.setTime(data.startTime);
            calendar.setTime(dt);
//            String startTime = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar
// .MINUTE);
            String startTime = String.format("%d:%02d", calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE));
            dt.setTime(data.endTime);
            calendar.setTime(dt);
            String endTime = String.format("%d:%02d", calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE));
            ;
//            String endTime = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar
// .MINUTE);
            tvMeetingTime.setText(startTime + "  -  " + endTime);
        }
    }

}

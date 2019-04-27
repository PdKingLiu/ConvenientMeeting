package com.pdking.convenientmeeting.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.db.MeetingMessage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author liupeidong
 * Created on 2019/4/24 16:41
 */
public class QueryMeetingAdapter extends RecyclerView.Adapter<QueryMeetingAdapter.ViewHolder>
        implements View.OnClickListener {

    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Context mContext;
    private List<MeetingMessage> list;
    private MeetingRoomAdapter.OnItemClickListener itemClickListener;

    public QueryMeetingAdapter(Context mContext, List<MeetingMessage> list) {
        this.mContext = mContext;
        this.list = list;
    }

    public void setItemClickListener(MeetingRoomAdapter.OnItemClickListener itemClickListener) {
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
                .item_query_meeting_list, viewGroup, false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.setData(list.get(i), i);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        View viewSum;
        TextView tvMeetingName;
        TextView tvMasterName;
        TextView tvDate;
        TextView tvPlace;
        TextView tvTime;
        TextView tvMessage;
        View viewLine;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            viewSum = itemView;
            tvMeetingName = itemView.findViewById(R.id.tv_meeting_name);
            tvMasterName = itemView.findViewById(R.id.tv_meeting_master);
            tvDate = itemView.findViewById(R.id.tv_meeting_date);
            tvPlace = itemView.findViewById(R.id.tv_meeting_place);
            tvTime = itemView.findViewById(R.id.tv_meeting_time);
            tvMessage = itemView.findViewById(R.id.tv_meeting_message);
            viewLine = itemView.findViewById(R.id.view_line);
        }

        public void setData(MeetingMessage meetingMessage, int i) {
            viewSum.setTag(i);
            tvMeetingName.setText(meetingMessage.meetingName);
            tvMasterName.setText(meetingMessage.masterName);
            tvPlace.setText(meetingMessage.roomName);
            tvMessage.setText(meetingMessage.meetingIntro);

            Date date = new Date();
            Date date2 = new Date();
            try {
                date = format.parse(meetingMessage.startTime);
                date2 = format.parse(meetingMessage.endTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            calendar.setTime(date);
            @SuppressLint("DefaultLocale") String time = String.format("%d月%d日",
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH));
            tvDate.setText(time);
            calendar.setTime(date);
            @SuppressLint("DefaultLocale")
            String string = String.format("%d:%02d", calendar.get
                    (Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
            calendar.setTime(date2);
            @SuppressLint("DefaultLocale")
            String string2 = String.format("%d:%02d", calendar.get
                    (Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
            String s = string + " - " + string2;
            tvTime.setText(s);
            if (i == list.size() - 1) {
                viewLine.setVisibility(View.GONE);
            } else {
                viewLine.setVisibility(View.VISIBLE);
            }
        }
    }
}

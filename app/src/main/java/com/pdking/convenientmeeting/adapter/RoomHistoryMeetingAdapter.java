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
import com.pdking.convenientmeeting.db.RoomOfMeetingMessage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author liupeidong
 * Created on 2019/4/22 21:33
 */
public class RoomHistoryMeetingAdapter extends RecyclerView.Adapter<RoomHistoryMeetingAdapter
        .ViewHolder> implements View.OnClickListener {

    private Context mContext;
    private List<RoomOfMeetingMessage> list;
    private OnItemClickListener mListener;

    public RoomHistoryMeetingAdapter(Context mContext, List<RoomOfMeetingMessage> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout
                .item_room_history_meeting, viewGroup, false);
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

    @Override
    public void onClick(View v) {
        if (mListener != null) {
            mListener.onItemClick(v, (int) v.getTag());
        }
    }

    public void setClickListener(OnItemClickListener mListener) {
        this.mListener = mListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        View viewSum;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        private TextView tvMeetingName;
        private TextView tvStatus;
        private TextView tvMeetingTimeLength;
        private TextView tvDate;
        private TextView tvTime;
        private TextView tvIntroduce;
        private View line;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            viewSum = itemView;
            tvMeetingName = itemView.findViewById(R.id.tv_meeting_name);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvMeetingTimeLength = itemView.findViewById(R.id.tv_meeting_time_length);
            tvDate = itemView.findViewById(R.id.tv_meeting_date);
            tvTime = itemView.findViewById(R.id.tv_meeting_time);
            tvIntroduce = itemView.findViewById(R.id.tv_meeting_introduce);
            line = itemView.findViewById(R.id.view_line);
        }

        public void setData(RoomOfMeetingMessage roomHistoryMeetingMessage, int i) {
            viewSum.setTag(i);
            tvMeetingName.setText(roomHistoryMeetingMessage.meetingName);
            switch (roomHistoryMeetingMessage.status) {
                case 1:
                    tvStatus.setText("结束");
                    tvStatus.setBackground(mContext.getResources().getDrawable(R.drawable
                            .shape_mine_meeting_status_finish));
                    break;
                case 2:
                    tvStatus.setText("正在进行");
                    tvStatus.setBackground(mContext.getResources().getDrawable(R.drawable
                            .shape_mine_meeting_status_progress));
                    break;
                case 3:
                    tvStatus.setText("暂未开始");
                    tvStatus.setBackground(mContext.getResources().getDrawable(R.drawable
                            .shape_mine_meeting_status_future));
                    break;
            }
            Date date = new Date(roomHistoryMeetingMessage.startTime);
            Date date2 = new Date(roomHistoryMeetingMessage.endTime);
            int len = (int) ((date2.getTime() - date.getTime()) / 1000 / 60);
            tvMeetingTimeLength.setText(len + "");

            calendar.setTime(date);

            @SuppressLint("DefaultLocale") String time = String.format("%d年%d月%d日",
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH));
            tvDate.setText(time);

            @SuppressLint("DefaultLocale")
            String string = String.format("%d:%02d:00", calendar.get
                    (Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
            calendar.setTime(date2);
            @SuppressLint("DefaultLocale")
            String string2 = String.format("%d:%02d:00", calendar.get
                    (Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
            String s = string + "  -  " + string2;
            tvTime.setText(s);
            tvIntroduce.setText(roomHistoryMeetingMessage.meetingIntro);
            if (i == list.size() - 1) {
                line.setVisibility(View.GONE);
            } else {
                line.setVisibility(View.VISIBLE);
            }
        }
    }
}

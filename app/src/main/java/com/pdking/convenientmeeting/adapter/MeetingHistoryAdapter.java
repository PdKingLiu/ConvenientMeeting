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
import com.pdking.convenientmeeting.db.MeetingBean;
import com.pdking.convenientmeeting.db.MeetingMessage;
import com.pdking.convenientmeeting.db.UserInfo;

import org.litepal.LitePal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author liupeidong
 * Created on 2019/2/27 18:31
 */
public class MeetingHistoryAdapter extends RecyclerView.Adapter<MeetingHistoryAdapter.ViewHolder>
        implements View.OnClickListener {

    private List<MeetingMessage> meetingBeanList;

    private Context mContext;

    private OnItemClickListener mListener;

    private UserInfo userInfo;

    Calendar calendar = Calendar.getInstance();

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public void setClickListener(OnItemClickListener mListener) {
        this.mListener = mListener;
    }

    public MeetingHistoryAdapter(List<MeetingMessage> meetingBeanList, Context mContext) {
        this.meetingBeanList = meetingBeanList;
        this.mContext = mContext;
        userInfo = LitePal.findAll(UserInfo.class).get(0);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout
                .item_meeting_history, viewGroup, false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        MeetingMessage meetingBean = meetingBeanList.get(i);
        viewHolder.setDate(meetingBean, i);
    }

    @Override
    public int getItemCount() {
        return meetingBeanList.size();
    }

    @Override
    public void onClick(View v) {
        if (mListener != null) {
            mListener.onItemClick(v, (int) v.getTag());
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvMeetingName;
        private TextView tvUserKind;
        private TextView tvPeopleNumber;
        private TextView tvUserStatus;
        private TextView tvPlace;
        private TextView tvMeetingTimeLength;
        private TextView tvMaster;
        private TextView tvDate;
        private TextView tvTime;
        private TextView tvIntroduce;
        private View view;
        private View line;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
            tvMeetingName = itemView.findViewById(R.id.tv_meeting_name);
            tvUserKind = itemView.findViewById(R.id.tv_user_kind);
            tvPeopleNumber = itemView.findViewById(R.id.tv_people_number);
            tvUserStatus = itemView.findViewById(R.id.tv_user_status);
            tvPlace = itemView.findViewById(R.id.tv_place);
            tvMeetingTimeLength = itemView.findViewById(R.id.tv_meeting_time_length);
            tvMaster = itemView.findViewById(R.id.tv_meeting_master);
            tvDate = itemView.findViewById(R.id.tv_meeting_date);
            tvTime = itemView.findViewById(R.id.tv_meeting_time);
            tvIntroduce = itemView.findViewById(R.id.tv_meeting_introduce);
            line = itemView.findViewById(R.id.view_line);
        }

        void setDate(MeetingMessage meetingBean, int i) {
            view.setTag(i);
            tvMeetingName.setText(meetingBean.meetingName);
            if (userInfo.userId == meetingBean.masterId) {
                tvUserKind.setText("组织者");
                tvUserKind.setBackground(mContext.getResources().getDrawable(R.drawable
                        .shape_history_meeting_user_kind_master));
            } else {
                tvUserKind.setText("参与者");
                tvUserKind.setBackground(mContext.getResources().getDrawable(R.drawable
                        .shape_history_meeting_user_kind_member));
            }
            tvPeopleNumber.setText(meetingBean.peopleNum + "");
            switch (meetingBean.userStatus) {
                case 1:
                    tvUserStatus.setText("正常");
                    break;
                case 2:
                    tvUserStatus.setText("缺勤");
                    break;
                case 3:
                    tvUserStatus.setText("迟到");
                    break;
                case 4:
                    tvUserStatus.setText("请假");
                    break;
            }
            tvPlace.setText(meetingBean.roomName);
            Date date = new Date();
            Date date2 = new Date();
            try {
                date = format.parse(meetingBean.startTime);
                date2 = format.parse(meetingBean.endTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            int len = (int) ((date2.getTime() - date.getTime()) / 1000 / 60);
            tvMeetingTimeLength.setText(len + "");
            tvMaster.setText(meetingBean.masterName);
            calendar.setTime(date);
            @SuppressLint("DefaultLocale") String time = String.format("%d年%d月%d日",
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH));
            tvDate.setText(time);
            calendar.setTime(date);
            @SuppressLint("DefaultLocale")
            String string = String.format("%d:%02d:00", calendar.get
                    (Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
            calendar.setTime(date2);
            @SuppressLint("DefaultLocale")
            String string2 = String.format("%d:%02d:00", calendar.get
                    (Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
            String s = string + "  -  " + string2;
            tvTime.setText(s);
            tvIntroduce.setText(meetingBean.meetingIntro);
            if (i == meetingBeanList.size() - 1) {
                line.setVisibility(View.GONE);
            } else {
                line.setVisibility(View.VISIBLE);
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}

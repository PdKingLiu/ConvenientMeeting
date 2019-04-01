package com.pdking.convenientmeeting.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haozhang.lib.SlantedTextView;
import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.db.MeetingMessage;
import com.pdking.convenientmeeting.db.MineMeetingBean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author liupeidong
 * Created on 2019/3/4 16:10
 */
public class MeetingMineAdapter extends RecyclerView.Adapter<MeetingMineAdapter.ViewHolder>
        implements View.OnClickListener {

    private List<MeetingMessage> beanList;
    private OnItemClickListener listener;
    private OnMoreClickListener moreListener;
    private Context mContext;

    public MeetingMineAdapter(List<MeetingMessage> beanList, Context mContext) {
        this.beanList = beanList;
        this.mContext = mContext;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_root:
                if (listener != null) {
                    listener.onItemClick(v, (Integer) v.getTag());
                }
                break;
            case R.id.btn_more:
                if (moreListener != null) {
                    moreListener.onMoreClick(v, (Integer) v.getTag());
                }
            default:
                break;
        }

    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnMoreClickListener {
        void onMoreClick(View view, int position);
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setMoreListener(OnMoreClickListener moreListener) {
        this.moreListener = moreListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout
                .item_meeting_mine, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.setData(beanList.get(i), i);
    }

    @Override
    public int getItemCount() {
        return beanList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        View viewBig;
        TextView tvMeetingName;
        TextView tvMeetingStatus;
        TextView tvPeopleSum;
        TextView tvMaster;
        TextView tvPlace;
        TextView tvTimeSum;
        TextView tvTime;
        View line;
        RelativeLayout btnMore;

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


        ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.viewBig = itemView;
            tvMeetingName = itemView.findViewById(R.id.tv_meeting_name);
            tvMeetingStatus = itemView.findViewById(R.id.tv_meeting_status);
            tvPeopleSum = itemView.findViewById(R.id.tv_people_number);
            tvMaster = itemView.findViewById(R.id.tv_meeting_master);
            tvPlace = itemView.findViewById(R.id.tv_place);
            tvTimeSum = itemView.findViewById(R.id.tv_meeting_time_length);
            tvTime = itemView.findViewById(R.id.tv_meeting_time);
            line = itemView.findViewById(R.id.view_line);
            btnMore = itemView.findViewById(R.id.btn_more);
        }

        void setData(MeetingMessage mineMeetingBean, int i) {
            viewBig.setOnClickListener(MeetingMineAdapter.this);
            viewBig.setTag(i);
            btnMore.findViewById(R.id.btn_more).setOnClickListener(MeetingMineAdapter.this);
            btnMore.findViewById(R.id.btn_more).setTag(i);
            tvMeetingName.setText(mineMeetingBean.meetingName);
            switch (mineMeetingBean.status) {
                case 1:
                    tvMeetingStatus.setText("结束");
                    tvMeetingStatus.setBackground(mContext.getResources().getDrawable(R.drawable
                            .shape_mine_meeting_status_finish));
                    break;
                case 2:
                    tvMeetingStatus.setText("正在进行");
                    tvMeetingStatus.setBackground(mContext.getResources().getDrawable(R.drawable
                            .shape_mine_meeting_status_progress));
                    break;
                case 3:
                    tvMeetingStatus.setText("暂未开始");
                    tvMeetingStatus.setBackground(mContext.getResources().getDrawable(R.drawable
                            .shape_mine_meeting_status_future));
                    break;
            }
            tvPeopleSum.setText(mineMeetingBean.peopleNum+"");
            tvMaster.setText(mineMeetingBean.masterName);
            tvPlace.setText(mineMeetingBean.roomName);
            Date date = new Date();
            Date date2 = new Date();
            try {
                date = format.parse(mineMeetingBean.startTime);
                date2 = format.parse(mineMeetingBean.endTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            int len = (int) ((date2.getTime() - date.getTime()) / 1000 / 60);
            tvTimeSum.setText(len + "");
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
            if (i == beanList.size() - 1) {
                line.setVisibility(View.GONE);
            }
        }
    }
}
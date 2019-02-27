package com.pdking.convenientmeeting.adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.db.MeetingBean;

import java.util.List;

/**
 * @author liupeidong
 * Created on 2019/2/27 18:31
 */
public class MeetingHistoryAdapter extends RecyclerView.Adapter<MeetingHistoryAdapter.ViewHolder> implements View.OnClickListener {

    private List<MeetingBean> meetingBeanList;

    private Context mContext;

    private OnItemClickListener mListener;

    public void setClickListener(OnItemClickListener mListener) {
        this.mListener = mListener;
    }

    public MeetingHistoryAdapter(List<MeetingBean> meetingBeanList, Context mContext) {
        this.meetingBeanList = meetingBeanList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout
                .item_meeting_history_meeting, viewGroup, false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        MeetingBean meetingBean = meetingBeanList.get(i);
        viewHolder.setDate(meetingBean,i);
    }

    @Override
    public int getItemCount() {
        return meetingBeanList.size();
    }

    @Override
    public void onClick(View v) {
        mListener.onItemClick(v,(int) v.getTag());
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_meeting_name;
        private TextView tv_user_kind;
        private TextView tv_people_number;
        private TextView tv_user_status;
        private TextView tv_place;
        private TextView tv_meeting_time_length;
        private TextView tv_meeting_master;
        private TextView tv_meeting_data;
        private TextView tv_meeting_time;
        private TextView tv_meeting_introduce;
        private View view;
        private View line;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
            tv_meeting_name = itemView.findViewById(R.id.tv_meeting_name);
            tv_user_kind = itemView.findViewById(R.id.tv_user_kind);
            tv_people_number = itemView.findViewById(R.id.tv_people_number);
            tv_user_status = itemView.findViewById(R.id.tv_user_status);
            tv_place = itemView.findViewById(R.id.tv_place);
            tv_meeting_time_length = itemView.findViewById(R.id.tv_meeting_time_length);
            tv_meeting_master = itemView.findViewById(R.id.tv_meeting_master);
            tv_meeting_data = itemView.findViewById(R.id.tv_meeting_data);
            tv_meeting_time = itemView.findViewById(R.id.tv_meeting_time);
            tv_meeting_introduce = itemView.findViewById(R.id.tv_meeting_introduce);
            line = itemView.findViewById(R.id.view_line);
        }

        void setDate(MeetingBean meetingBean, int i) {
            view.setTag(i);
            tv_meeting_name.setText(meetingBean.getTv_meeting_name());
            tv_user_kind.setText(meetingBean.getTv_user_kind());
            tv_people_number.setText(meetingBean.getTv_people_number());
            tv_user_status.setText(meetingBean.getTv_user_status());
            tv_place.setText(meetingBean.getTv_place());
            tv_meeting_time_length.setText(meetingBean.getTv_meeting_time_length());
            tv_meeting_master.setText(meetingBean.getTv_meeting_master());
            tv_meeting_data.setText(meetingBean.getTv_meeting_data());
            tv_meeting_time.setText(meetingBean.getTv_meeting_time());
            tv_meeting_introduce.setText(meetingBean.getTv_meeting_introduce());
            if (meetingBean.getTv_user_kind().equals("组织者")) {
                tv_user_kind.setBackground(mContext.getResources().getDrawable(R.drawable
                        .shape_history_meeting_user_kind_master));
            } else if (meetingBean.getTv_user_kind().equals("参与者")) {
                tv_user_kind.setBackground(mContext.getResources().getDrawable(R.drawable
                        .shape_history_meeting_user_kind_member));
            }
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

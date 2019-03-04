package com.pdking.convenientmeeting.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.haozhang.lib.SlantedTextView;
import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.db.MeetingRoomBean;

import java.util.List;

/**
 * @author liupeidong
 * Created on 2019/3/2 20:52
 */
public class MeetingRoomAdapter extends RecyclerView.Adapter<MeetingRoomAdapter.ViewHolder>
        implements View.OnClickListener {

    private Context mContext;
    private List<MeetingRoomBean> meetingRoomBeanList;
    private OnItemClickListener itemClickListener;


    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        if (itemClickListener != null) {
            itemClickListener.onItemClick(v, (Integer) v.getTag());
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }


    public MeetingRoomAdapter(Context mContext, List<MeetingRoomBean> meetingRoomBeanList) {
        this.mContext = mContext;
        this.meetingRoomBeanList = meetingRoomBeanList;
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
        MeetingRoomBean meetingRoomBean = meetingRoomBeanList.get(i);
        viewHolder.setDate(meetingRoomBean, i);
    }

    @Override
    public int getItemCount() {
        return meetingRoomBeanList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRoomName;
        TextView tvRank;
        TextView tvCanOrder;
        TextView tvPeopleSum;
        SlantedTextView stvStatus;
        View line;
        View view;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
            tvRoomName = itemView.findViewById(R.id.tv_room_name);
            tvRank = itemView.findViewById(R.id.tv_rank);
            tvCanOrder = itemView.findViewById(R.id.tv_can_order);
            tvPeopleSum = itemView.findViewById(R.id.tv_people_sum);
            stvStatus = itemView.findViewById(R.id.stv_status);
            line = itemView.findViewById(R.id.view_line);
        }

        void setDate(MeetingRoomBean meetingRoomBean, int i) {
            view.setTag(i);
            if (i == meetingRoomBeanList.size() - 1) {
                line.setVisibility(View.GONE);
            } else {
                line.setVisibility(View.VISIBLE);
            }
        }
    }

}

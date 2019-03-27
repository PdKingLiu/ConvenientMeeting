package com.pdking.convenientmeeting.adapter;

import android.content.Context;
import android.graphics.Color;
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
import com.pdking.convenientmeeting.db.OneMeetingRoomMessage;

import java.util.List;

/**
 * @author liupeidong
 * Created on 2019/3/2 20:52
 */
public class MeetingRoomAdapter extends RecyclerView.Adapter<MeetingRoomAdapter.ViewHolder>
        implements View.OnClickListener {

    private Context mContext;
    private List<OneMeetingRoomMessage> roomMessageList;
    private OnItemClickListener itemClickListener;
    static int i = 1;

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


    public MeetingRoomAdapter(Context mContext, List<OneMeetingRoomMessage> roomMessageList) {
        this.mContext = mContext;
        this.roomMessageList = roomMessageList;
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
                    stvStatus.setSlantedBackgroundColor(mContext.getResources().getColor(R.color
                            .pie_green));
                    break;
                case 2:
                    stvStatus.setText("使用中");
                    stvStatus.setSlantedBackgroundColor(Color.RED);
                    break;
                case 3:
                    stvStatus.setText("维修");
                    stvStatus.setSlantedBackgroundColor(Color.LTGRAY);
                    break;
            }
            tvRank.setText("使用榜第 " + i++ + " 名");
            tvPeopleSum.setText("可容纳人数" + oneMeetingRoomMessage.content + "人");
        }
    }

}

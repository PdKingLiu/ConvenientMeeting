package com.pdking.convenientmeeting.adapter;

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
import com.pdking.convenientmeeting.db.MineMeetingBean;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @author liupeidong
 * Created on 2019/3/4 16:10
 */
public class MeetingMineAdapter extends RecyclerView.Adapter<MeetingMineAdapter.ViewHolder>
        implements View.OnClickListener {

    private List<MineMeetingBean> beanList;
    private OnItemClickListener listener;
    private OnMoreClickListener moreListener;
    private Context mContext;

    public MeetingMineAdapter(List<MineMeetingBean> beanList, Context mContext) {
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
        viewHolder.setData(beanList.get(i),i);
    }

    @Override
    public int getItemCount() {
        return beanList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        View viewBig;
        TextView tvMeetingName;
        TextView tvPeopleKind;
        TextView tvPeopleSum;
        TextView tvMaster;
        TextView tvPlace;
        TextView tvTimeSum;
        TextView tvTime;
        TextView tvMessage;
        SlantedTextView stvStatus;
        View line;
        RelativeLayout btnMore;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.viewBig = itemView;
            tvMeetingName = itemView.findViewById(R.id.tv_meeting_name);
            tvPeopleKind = itemView.findViewById(R.id.tv_user_kind);
            tvPeopleSum = itemView.findViewById(R.id.tv_people_number);
            tvMaster = itemView.findViewById(R.id.tv_meeting_master);
            tvPlace = itemView.findViewById(R.id.tv_place);
            tvTimeSum = itemView.findViewById(R.id.tv_meeting_time_length);
            tvTime = itemView.findViewById(R.id.tv_meeting_time);
            tvMessage = itemView.findViewById(R.id.tv_meeting_introduce);
            stvStatus = itemView.findViewById(R.id.stv_status);
            line = itemView.findViewById(R.id.view_line);
            btnMore = itemView.findViewById(R.id.btn_more);
        }

        void setData(MineMeetingBean mineMeetingBean, int i) {
            viewBig.setOnClickListener(MeetingMineAdapter.this);
            viewBig.setTag(i);
            btnMore.findViewById(R.id.btn_more).setOnClickListener(MeetingMineAdapter.this);
            btnMore.findViewById(R.id.btn_more).setTag(i);
        }
    }

}

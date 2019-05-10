package com.pdking.convenientmeeting.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.db.QueryVideoMessageBean;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author liupeidong
 * Created on 2019/5/10 15:23
 */
public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.ViewHolder>
        implements View.OnClickListener {

    private OnItemClickListener listener;

    private List<QueryVideoMessageBean.DataBean> beanList;

    private Context mContext;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public VideoListAdapter(List<QueryVideoMessageBean.DataBean> beanList, Context mContext) {
        this.beanList = beanList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_video_list, viewGroup,
                false);
        view.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        if (listener != null) {
            listener.onItemClick(v, (Integer) v.getTag());
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        View viewLine;
        TextView tvName;
        TextView tvStatus;
        TextView tvPeoples;
        TextView tvTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
            tvName = itemView.findViewById(R.id.tv_video_name);
            tvStatus = itemView.findViewById(R.id.tv_video_status);
            tvPeoples = itemView.findViewById(R.id.tv_video_peoples);
            tvTime = itemView.findViewById(R.id.tv_video_time);
            viewLine = itemView.findViewById(R.id.view_line);
        }

        public void setData(QueryVideoMessageBean.DataBean dataBean, int i) {
            view.setTag(i);
            tvName.setText(dataBean.liveName);
            if (dataBean.status == 1) {
                tvStatus.setText("正在进行");
            } else {
                tvStatus.setText("结束");
            }
            tvPeoples.setText(String.valueOf("在线人数：" + dataBean.onlineNum + "人"));
            long time = dataBean.startTime;
            tvTime.setText(String.valueOf("时间：" + sdf.format(new Date(time))));
            if (i == beanList.size() - 1) {
                viewLine.setVisibility(View.GONE);
            } else {
                viewLine.setVisibility(View.VISIBLE);
            }
        }
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}

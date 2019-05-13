package com.pdking.convenientmeeting.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.db.LiveDetailBean;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @author liupeidong
 * Created on 2019/5/13 13:55
 */
public class LiveMemberAdapter extends RecyclerView.Adapter<LiveMemberAdapter.ViewHolder> {

    private Context mContext;

    private List<LiveDetailBean.DataBean.MeetingMembersBean> list;

    public LiveMemberAdapter(Context mContext, List<LiveDetailBean.DataBean.MeetingMembersBean>
            list) {
        this.mContext = mContext;
        this.list = list;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView circleImageView;
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.civ_icon);
            textView = itemView.findViewById(R.id.tv_name);
        }

        public void setData(LiveDetailBean.DataBean.MeetingMembersBean meetingMembersBean) {
            Glide.with(mContext).load(meetingMembersBean.avatarUrl).into(circleImageView);
            textView.setText(meetingMembersBean.username);
        }
    }

    @NonNull
    @Override
    public LiveMemberAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout
                .item_live_member, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull LiveMemberAdapter.ViewHolder viewHolder, int i) {
        viewHolder.setData(list.get(i));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

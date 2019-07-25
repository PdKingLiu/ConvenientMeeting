package com.pdking.convenientmeeting.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.db.MeetingByIdMessage;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @author liupeidong
 * Created on 2019/5/1 21:37
 */
public class MeetingMemberAdapter extends RecyclerView.Adapter<MeetingMemberAdapter.ViewHolder> {

    private Context mContext;
    private List<MeetingByIdMessage.MemberStatusBean> list;
    private int masterId;

    public MeetingMemberAdapter(Context mContext, List<MeetingByIdMessage.MemberStatusBean> list,
                                int masterId) {
        this.mContext = mContext;
        this.list = list;
        this.masterId = masterId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout
                .item_meeting_member_list, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.setData(list.get(i), i);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView civIcon;
        TextView tvName;
        LinearLayout llUserKind;
        TextView tvUserStatus;
        ImageView ivUserKind;
        View viewLine;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            civIcon = itemView.findViewById(R.id.civ_user_icon);
            tvName = itemView.findViewById(R.id.tv_username);
            llUserKind = itemView.findViewById(R.id.ll_user_kind);
            tvUserStatus = itemView.findViewById(R.id.tv_status);
            ivUserKind = itemView.findViewById(R.id.iv_user_kind);
            viewLine = itemView.findViewById(R.id.view_line);
        }

        public void setData(MeetingByIdMessage.MemberStatusBean memberStatusBean, int i) {
            Glide.with(mContext).load(memberStatusBean.avatarUrl).into(civIcon);
            if (masterId == memberStatusBean.userId) {
                llUserKind.setVisibility(View.VISIBLE);
            } else {
                llUserKind.setVisibility(View.GONE);
            }
            tvName.setText(memberStatusBean.username);
            switch (memberStatusBean.userStatus) {
                case 1:
                    tvUserStatus.setText("正常签到");
                    ivUserKind.setImageDrawable(mContext.getResources().getDrawable(R.mipmap
                            .icon_user_status_normal));
                    break;
                case 2:
                    tvUserStatus.setText("缺勤");
                    ivUserKind.setImageDrawable(mContext.getResources().getDrawable(R.mipmap
                            .icon_user_status_absence));
                    break;
                case 3:
                    tvUserStatus.setText("迟到");
                    ivUserKind.setImageDrawable(mContext.getResources().getDrawable(R.mipmap
                            .icon_user_status_late));
                    break;
                case 4:
                    ivUserKind.setImageDrawable(mContext.getResources().getDrawable(R.mipmap
                            .icon_user_status_leave));
                    tvUserStatus.setText("请假");
                    break;
            }
            if (i == list.size() - 1) {
                viewLine.setVisibility(View.INVISIBLE);
            } else {
                viewLine.setVisibility(View.VISIBLE);
            }
        }
    }


}

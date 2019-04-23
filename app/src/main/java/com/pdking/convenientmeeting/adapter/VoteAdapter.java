package com.pdking.convenientmeeting.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.db.VoteListBean;
import com.pdking.convenientmeeting.db.VoteTest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @author liupeidong
 * Created on 2019/4/17 17:01
 */
public class VoteAdapter extends RecyclerView.Adapter<VoteAdapter.ViewHolder> implements View
        .OnClickListener {

    private List<VoteListBean.VoteBean> voteList;
    private Context mContext;
    private OnItemClickListener itemClickListener;

    public VoteAdapter(List<VoteListBean.VoteBean> voteList, Context mContext) {
        this.voteList = voteList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_vote,
                viewGroup, false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.setData(voteList.get(i), i);
    }

    @Override
    public int getItemCount() {
        return voteList.size();
    }

    @Override
    public void onClick(View v) {
        if (itemClickListener != null) {
            itemClickListener.onItemClick(v, (Integer) v.getTag());
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        View viewSum;
        TextView tvUserName;
        TextView tvTime;
        TextView tvMessage;
        TextView tvStatus;
        Button btnVote;
        CircleImageView civUserIcon;
        View viewLine;
        View viewLineUp;
        Calendar calendar = Calendar.getInstance();
        DateFormat format = new SimpleDateFormat("MM-dd HH:mm");

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            viewSum = itemView;
            tvUserName = itemView.findViewById(R.id.tv_user_name);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvMessage = itemView.findViewById(R.id.tv_message);
            tvStatus = itemView.findViewById(R.id.tv_status);
            btnVote = itemView.findViewById(R.id.btn_vote);
            civUserIcon = itemView.findViewById(R.id.civ_user_icon);
            viewLine = itemView.findViewById(R.id.view_line);
            viewLineUp = itemView.findViewById(R.id.view_line_up);
        }

        public void setData(VoteListBean.VoteBean bean, int i) {
            viewSum.setTag(i);
            Glide.with(mContext).load(bean.userInfo.avatarUrl).into(civUserIcon);
            tvUserName.setText(bean.userInfo.username);
            tvMessage.setText(bean.topic);
            if (calendar.getTime().getTime() > bean.endTime) {
                tvStatus.setText("已结束");
                tvStatus.setBackground(mContext.getResources().getDrawable(R.mipmap.icon_vote_end));
            } else {
                tvStatus.setText("正在进行");
                tvStatus.setBackground(mContext.getResources().getDrawable(R.mipmap
                        .icon_vote_proceed));
            }
            if (bean.userSelectList == null || bean.userSelectList.size() == 0) {
                if (calendar.getTime().getTime() > bean.endTime) {
                    bean.kind = 1;
                    btnVote.setText("投票截止，查看结果");
                } else {
                    bean.kind = 2;
                    btnVote.setText("立即投票");
                }
            } else {
                bean.kind = 3;
                btnVote.setText("已投票，查看结果");
            }
            calendar = Calendar.getInstance();
            long len = calendar.getTime().getTime() - bean.createTime;
            if (len / 1000 / 60 <= 60) {
                if (len <= 0) {
                    tvTime.setText("刚刚");
                } else {
                    tvTime.setText(len / 1000 / 60 + "分钟前");
                }
            } else {
                tvTime.setText(format.format(new Date(bean.createTime)));
            }
            if (i == 0) {
                viewLineUp.setVisibility(View.VISIBLE);
            } else {
                viewLineUp.setVisibility(View.GONE);
            }
        }
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

}

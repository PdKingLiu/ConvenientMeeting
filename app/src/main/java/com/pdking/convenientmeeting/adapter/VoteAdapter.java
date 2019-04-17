package com.pdking.convenientmeeting.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.db.VoteTest;

import java.util.List;

/**
 * @author liupeidong
 * Created on 2019/4/17 17:01
 */
public class VoteAdapter extends RecyclerView.Adapter<VoteAdapter.ViewHolder> implements View
        .OnClickListener {

    private List<VoteTest> voteList;
    private Context mContext;
    private OnItemClickListener itemClickListener;

    public VoteAdapter(List<VoteTest> voteList, Context mContext) {
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            viewSum = itemView;
        }

        public void setData(VoteTest voteTest, int i) {
            viewSum.setTag(i);
        }
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

}

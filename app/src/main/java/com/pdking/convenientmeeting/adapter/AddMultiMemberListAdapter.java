package com.pdking.convenientmeeting.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.db.AllUserBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liupeidong
 * Created on 2019/5/11 17:29
 */
public class AddMultiMemberListAdapter extends RecyclerView.Adapter<AddMultiMemberListAdapter
        .ViewHolder> {

    private List<AllUserBean.DataBean> beanList;

    private List<AllUserBean.DataBean> checkedList = new ArrayList<>();

    public AddMultiMemberListAdapter(List<AllUserBean.DataBean> beanList) {
        this.beanList = beanList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout
                .item_add_multi_member, viewGroup, false);
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

    class ViewHolder extends RecyclerView.ViewHolder implements CompoundButton
            .OnCheckedChangeListener {

        TextView tvName;
        CheckBox cbIsCheck;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cbIsCheck = itemView.findViewById(R.id.cb_is_check);
            tvName = itemView.findViewById(R.id.tv_name);
        }

        public void setData(AllUserBean.DataBean dataBean, int i) {
            tvName.setText(dataBean.username);
            cbIsCheck.setOnCheckedChangeListener(this);
            cbIsCheck.setTag(i);
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (checkedList == null) {
                checkedList = new ArrayList<>();
            }
            AllUserBean.DataBean bean = beanList.get((Integer) buttonView.getTag());
            if (isChecked) {
                if (!checkedList.contains(bean)) {
                    checkedList.add(bean);
                }
            } else {
                if (checkedList.contains(bean)) {
                    checkedList.remove(bean);
                }
            }
        }
    }

    public List<AllUserBean.DataBean> getCheckedList() {
        return checkedList;
    }
}

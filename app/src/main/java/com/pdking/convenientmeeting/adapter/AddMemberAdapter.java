package com.pdking.convenientmeeting.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.db.UserInfo;

import java.util.List;

/**
 * @author liupeidong
 * Created on 2019/5/6 22:51
 */
public class AddMemberAdapter extends ArrayAdapter<UserInfo> {

    private int id;

    public AddMemberAdapter(@NonNull Context context, int resource,@NonNull List<UserInfo> objects) {
        super(context, resource, objects);
        this.id = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_add_member, parent, false);
        TextView tvName = view.findViewById(R.id.tv_name);
        CheckBox cbIsCheck = view.findViewById(R.id.cb_is_check);
        return view;
    }
}

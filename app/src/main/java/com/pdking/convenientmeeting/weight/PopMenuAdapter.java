package com.pdking.convenientmeeting.weight;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pdking.convenientmeeting.R;

import java.util.ArrayList;

/**
 * @author liupeidong
 * Created on 2019/2/25 13:48
 */
public class PopMenuAdapter extends BaseAdapter {


    private Context context;
    private ArrayList<PopMenuItem> items;
    private String TAG = "Lpp";

    public PopMenuAdapter(Context context, ArrayList<PopMenuItem> items) {
        this.context = context;
        this.items = items;
    }


    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public PopMenuItem getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_pop_menu, null);
            ViewHolder holder = new ViewHolder();
            holder.icon = convertView.findViewById(R.id.menu_icon);
            holder.view = convertView.findViewById(R.id.view);
            holder.text = convertView.findViewById(R.id.menu_text);
            convertView.setTag(holder);
        } else if (convertView.getParent() != null) {
            ((ViewGroup) convertView.getParent()).removeView(convertView);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        PopMenuItem item = items.get(position);
        if (item.getResId() == 0) {
            holder.icon.setVisibility(View.GONE);
        } else {
            holder.icon.setImageResource(item.getResId());
        }
        holder.text.setText(item.getText());
        if (position == items.size() - 1) {
            holder.view.setVisibility(View.GONE);
        } else {
            holder.view.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    private class ViewHolder {
        View view;
        ImageView icon;
        TextView text;
    }

}

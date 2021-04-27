package com.pdking.convenientmeeting.weight;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.adapter.AddMultiMemberListAdapter;
import com.pdking.convenientmeeting.db.AllUserBean;

import java.util.List;

/**
 * @author liupeidong
 * Created on 2019/5/11 16:39
 */
public class AddMultiMemberDialog extends Dialog implements View.OnClickListener {

    private RecyclerView recyclerView;
    private Button btnCancel;
    private Button btnAdd;

    private Context context;
    private AddMultiMemberListAdapter adapter;
    private List<AllUserBean.DataBean> beanList;

    private List<AllUserBean.DataBean> checkedList;

    private AddMultiMemberListener listener;

    public AddMultiMemberDialog(Context context, int style, List<AllUserBean.DataBean> beanList,
                                AddMultiMemberListener listener) {
        super(context, style);
        this.beanList = beanList;
        this.listener = listener;
        View view = LayoutInflater.from(context).inflate(R.layout.layout_add_multi_member, null);
        setContentView(view);
        init(view);
        initList();
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }

    private void initList() {
        adapter = new AddMultiMemberListAdapter(beanList);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
    }

    private void init(View view) {
        recyclerView = view.findViewById(R.id.rv_member);
        btnAdd = view.findViewById(R.id.btn_add);
        btnCancel = view.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                this.hide();
                this.dismiss();
                break;
            case R.id.btn_add:
                checkedList = adapter.getCheckedList();
                if (listener != null) {
                    listener.addMemberCallBack(checkedList);
                }
                this.hide();
                this.dismiss();
                break;
        }
    }

    public void setListener(AddMultiMemberListener listener) {
        this.listener = listener;
    }
}

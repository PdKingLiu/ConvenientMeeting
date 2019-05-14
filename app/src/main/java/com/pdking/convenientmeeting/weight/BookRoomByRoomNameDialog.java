package com.pdking.convenientmeeting.weight;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.pdking.convenientmeeting.R;

/**
 * @author liupeidong
 * Created on 2019/5/13 21:09
 */
public class BookRoomByRoomNameDialog extends Dialog  implements View.OnClickListener{

    private View view;
    private EditText edRoomName;
    private Button btnCancel;
    private Button btnAdd;
    private OnClickListener listener;

    public BookRoomByRoomNameDialog(Context context, int style, OnClickListener listener) {
        super(context, style);
        this.view = LayoutInflater.from(context).inflate(R.layout.layout_book_by_room_name, null);
        this.listener = listener;
        setContentView(view);
        init(view);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }

    private void init(View view) {
        edRoomName = view.findViewById(R.id.ed_room_name);
        btnCancel = view.findViewById(R.id.btn_cancel);
        btnAdd = view.findViewById(R.id.btn_add);
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
                String room = edRoomName.getText().toString();
                if (listener != null) {
                    listener.onClick(room);
                }
                this.hide();
                this.dismiss();
                break;
        }
    }

    public interface OnClickListener {
        void onClick(String room);
    }
}

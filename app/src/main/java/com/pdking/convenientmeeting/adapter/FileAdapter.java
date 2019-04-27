package com.pdking.convenientmeeting.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.db.FileData;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author liupeidong
 * Created on 2019/4/11 11:22
 */
public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder> implements View
        .OnClickListener {

    private Activity activity;
    private List<FileData> dataList;
    private OnItemClickListener mListener;

    public FileAdapter(Activity activity, List<FileData> dataList) {
        this.activity = activity;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_file,
                viewGroup, false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.setData(dataList.get(i), i);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public void onClick(View v) {
        if (mListener != null) {
            mListener.onItemClick(v, (int) v.getTag());
        }
    }

    public void setClickListener(OnItemClickListener mListener) {
        this.mListener = mListener;
    }


    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        View view;
        TextView tvFileName;
        TextView tvDate;
        TextView tvFileSize;
        TextView tvMaster;
        ImageView ivFileIcon;
        Calendar calendar = Calendar.getInstance();
        Date date = new Date();

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
            tvFileName = itemView.findViewById(R.id.tv_file_name);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvFileSize = itemView.findViewById(R.id.tv_file_size);
            tvMaster = itemView.findViewById(R.id.tv_master);
            ivFileIcon = itemView.findViewById(R.id.iv_file_icon);
        }

        public void setData(FileData fileData, int i) {
            view.setTag(i);
            date.setTime(fileData.uploadTime);
            tvFileName.setText(fileData.fileName);
            String dat = String.format("%d-%d-%d",
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH));
            tvDate.setText(dat);
            tvMaster.setText(fileData.uploader);
            if (fileData.fileSize < 1000) {
                tvFileSize.setText(fileData.fileSize + "K");
            } else {
                tvFileSize.setText(fileData.fileSize / 1024 + "MB");
            }
            int end = fileData.fileName.lastIndexOf(".");
            if (end == -1) {
                ivFileIcon.setImageResource(R.mipmap.icon_not_konw);
            } else {
                String str = fileData.fileName.substring(end + 1).toLowerCase();
                switch (str) {
                    case "pdf":
                        ivFileIcon.setImageResource(R.mipmap.icon_pdf);
                        break;
                    case "xla":
                    case "xlc":
                    case "xlm":
                    case "xls":
                    case "xlt":
                    case "xlw":
                        ivFileIcon.setImageResource(R.mipmap.icon_excel);
                        break;
                    case "txt":
                        ivFileIcon.setImageResource(R.mipmap.icon_txt);
                        break;
                    case "mp3":
                        ivFileIcon.setImageResource(R.mipmap.icon_music);
                        break;
                    case "mp4":
                        ivFileIcon.setImageResource(R.mipmap.icon_video);
                        break;
                    case "jpe":
                    case "jpeg":
                    case "jpg":
                    case "gif":
                    case "png":
                        ivFileIcon.setImageResource(R.mipmap.icon_picture);
                        break;
                    case "ppt":
                    case "pptx":
                        ivFileIcon.setImageResource(R.mipmap.icon_ppt);
                        break;
                    case "doc":
                    case "docx":
                        ivFileIcon.setImageResource(R.mipmap.icon_word);
                        break;
                    default:
                        ivFileIcon.setImageResource(R.mipmap.icon_not_konw);
                }
            }
        }
    }
}

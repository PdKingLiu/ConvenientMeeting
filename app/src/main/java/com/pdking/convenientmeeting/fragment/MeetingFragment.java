package com.pdking.convenientmeeting.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.pdking.convenientmeeting.R;
/**
 * @author liupeidong
 * Created on 2019/1/30 17:49
 */
public class MeetingFragment extends Fragment {

    private static MeetingFragment INSTANCE = null;

    private View mView;

    public static MeetingFragment getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new MeetingFragment();
        }
        return INSTANCE;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.layout_meetingfragment, container, false);
        return mView;
    }
}

package com.pdking.convenientmeeting.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pdking.convenientmeeting.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class VideoNowFragment extends Fragment {

    private static VideoNowFragment videoNowFragment;

    public static VideoNowFragment newInstance() {
        if (videoNowFragment == null) {
            videoNowFragment = new VideoNowFragment();
        }
        return videoNowFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_fragment_video_now, container, false);
    }

}

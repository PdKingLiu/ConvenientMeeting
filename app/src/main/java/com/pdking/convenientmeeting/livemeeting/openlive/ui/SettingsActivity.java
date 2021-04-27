package com.pdking.convenientmeeting.livemeeting.openlive.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pdking.convenientmeeting.R;
import com.pdking.convenientmeeting.livemeeting.openlive.model.ConstantApp;
import com.pdking.convenientmeeting.utils.SystemUtil;
import com.pdking.convenientmeeting.weight.TitleView;


public class SettingsActivity extends AppCompatActivity implements TitleView.RightClickListener, TitleView.LeftClickListener {

    private VideoProfileAdapter mVideoProfileAdapter;

    private TitleView titleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        SystemUtil.setTitleMode(getWindow());
        titleView = findViewById(R.id.title_view);
        titleView.setRightClickListener(this);
        titleView.setLeftClickListener(this);
        initUi();
    }

    private void initUi() {
        RecyclerView v_profiles = (RecyclerView) findViewById(R.id.profiles);
        v_profiles.setHasFixedSize(true);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        int prefIndex = pref.getInt(ConstantApp.PrefManager.PREF_PROPERTY_PROFILE_IDX, ConstantApp.DEFAULT_PROFILE_IDX);

        mVideoProfileAdapter = new VideoProfileAdapter(this, prefIndex);
        mVideoProfileAdapter.setHasStableIds(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        v_profiles.setLayoutManager(layoutManager);

        v_profiles.setAdapter(mVideoProfileAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.confirm:
                doSaveProfile();
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void doSaveProfile() {
        int profileIndex = mVideoProfileAdapter.getSelected();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(ConstantApp.PrefManager.PREF_PROPERTY_PROFILE_IDX, profileIndex);
        editor.apply();
    }

    @Override
    public void OnRightButtonClick() {
        doSaveProfile();
        onBackPressed();
    }

    @Override
    public void OnLeftButtonClick() {
        finish();
    }
}

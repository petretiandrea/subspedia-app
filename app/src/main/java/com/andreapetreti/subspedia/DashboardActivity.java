package com.andreapetreti.subspedia;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;


import com.andreapetreti.android_utils.ui.BottomNavigationViewHelper;
import com.andreapetreti.subspedia.ui.fragment.SeriesFragment;
import com.andreapetreti.subspedia.ui.fragment.LastSubtitlesFragment;
import com.andreapetreti.subspedia.ui.fragment.TranslatingSeriesFragment;

import java.util.HashMap;
import java.util.Map;

public class DashboardActivity extends AppCompatActivity {

    /**
     * READ WRITE PERMISSION REQUEST CODE
     */
    private static final int RD_PERMISSION = 100;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private static final String TAG_FRAGMENT_FAVORITE = "frag_favorite_series";
    private static final String TAG_FRAGMENT_ALL_SERIES = "frag_all_series";
    private static final String TAG_FRAGMENT_TRANSLATING_SERIES = "frag_trans_series";
    private static final String TAG_FRAGMENT_LAST_SUBS = "frag_last_subs";

    private String mCurrentSwitchFragment;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        switchFragment(TAG_FRAGMENT_ALL_SERIES);
                        return true;
                    case R.id.navigation_favorite:
                        switchFragment(TAG_FRAGMENT_FAVORITE);
                        return true;
                    case R.id.navigation_dashboard:
                        switchFragment(TAG_FRAGMENT_TRANSLATING_SERIES);
                        return true;
                    case R.id.navigation_notifications:
                        switchFragment(TAG_FRAGMENT_LAST_SUBS);
                        return true;
                }
                return false;
            };

    private Map<String, Fragment> mFragments;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        BottomNavigationViewHelper.removeShiftMode(navigation);

        mFragments = new HashMap<>();
        mFragments.put(TAG_FRAGMENT_ALL_SERIES, SeriesFragment.newInstance(false));
        mFragments.put(TAG_FRAGMENT_FAVORITE, SeriesFragment.newInstance(true));
        mFragments.put(TAG_FRAGMENT_TRANSLATING_SERIES, TranslatingSeriesFragment.newInstance());
        mFragments.put(TAG_FRAGMENT_LAST_SUBS, LastSubtitlesFragment.newInstance());

        mCurrentSwitchFragment = (savedInstanceState != null) ? savedInstanceState.getString("current_frag", TAG_FRAGMENT_ALL_SERIES) : TAG_FRAGMENT_ALL_SERIES;
    }

    private void checkAppPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, RD_PERMISSION);
            // if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE))
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RD_PERMISSION:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    switchFragment(mCurrentSwitchFragment);
                else
                    finish();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAppPermission();
        switchFragment(mCurrentSwitchFragment);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("current_frag", mCurrentSwitchFragment);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mCurrentSwitchFragment = savedInstanceState.getString("current_frag");
    }

    private void switchFragment(String tag) {
        mCurrentSwitchFragment = tag;
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, mFragments.get(tag), tag)
                .commit();
    }
}

package com.andreapetreti.subspedia;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;


import com.andreapetreti.android_utils.ui.BottomNavigationViewHelper;
import com.andreapetreti.subspedia.ui.ActivityLoadingBar;
import com.andreapetreti.subspedia.ui.fragment.AllSeriesFragment;
import com.andreapetreti.subspedia.ui.fragment.LastSubtitlesFragment;
import com.andreapetreti.subspedia.ui.fragment.TranslatingSeriesFragment;

import java.util.HashMap;
import java.util.Map;

public class DashboardActivity extends AppCompatActivity implements ActivityLoadingBar {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private static final String TAG_FRAGMENT_FAVORITE = "frag_favorite_series";
    private static final String TAG_FRAGMENT_ALL_SERIES = "frag_all_series";
    private static final String TAG_FRAGMENT_TRANSLATING_SERIES = "frag_trans_series";
    private static final String TAG_FRAGMENT_LAST_SUBS = "frag_last_subs";

    private String mCurrentSwitchFragment;

    private ProgressBar mProgressBar;

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

        mProgressBar = findViewById(R.id.progressBar);

        mFragments = new HashMap<>();
        mFragments.put(TAG_FRAGMENT_ALL_SERIES, AllSeriesFragment.newInstance(false));
        mFragments.put(TAG_FRAGMENT_FAVORITE, AllSeriesFragment.newInstance(true));
        mFragments.put(TAG_FRAGMENT_TRANSLATING_SERIES, TranslatingSeriesFragment.newInstance());
        mFragments.put(TAG_FRAGMENT_LAST_SUBS, LastSubtitlesFragment.newInstance());

        mCurrentSwitchFragment = (savedInstanceState != null) ? savedInstanceState.getString("current_frag", TAG_FRAGMENT_ALL_SERIES) : TAG_FRAGMENT_ALL_SERIES;

        switchFragment(mCurrentSwitchFragment);
    }

    @Override
    protected void onResume() {
        super.onResume();
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

    @Override
    public void showLoading() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        mProgressBar.setVisibility(View.GONE);
    }
}

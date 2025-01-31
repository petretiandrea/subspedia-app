package com.andreapetreti.subspedia;

import android.Manifest;

import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;

import android.app.SearchManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.andreapetreti.subspedia.background.SubtitleWorker;
import com.andreapetreti.subspedia.utils.SubspediaUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


import com.andreapetreti.android_utils.connectivity.ConnectionLiveData;
import com.andreapetreti.subspedia.background.NewSubsWorker;
import com.andreapetreti.subspedia.common.Resource;
import com.andreapetreti.subspedia.ui.fragment.AllSeriesFragment;
import com.andreapetreti.subspedia.ui.fragment.FavoriteSeriesFragment;
import com.andreapetreti.subspedia.ui.fragment.LastSubtitlesFragment;
import com.andreapetreti.subspedia.ui.fragment.TranslatingSeriesFragment;
import com.andreapetreti.subspedia.viewmodel.SeriesViewModel;

import java.util.HashMap;
import java.util.Map;
import com.annimon.stream.Objects;
import java.util.concurrent.TimeUnit;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

public class DashboardActivity extends AppCompatActivity {

    /**
     * READ WRITE PERMISSION REQUEST CODE
     */
    private static final int RW_PERMISSION = 100;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private static final String TAG_FRAGMENT_FAVORITE = "frag_favorite_series";
    private static final String TAG_FRAGMENT_ALL_SERIES = "frag_all_series";
    private static final String TAG_FRAGMENT_TRANSLATING_SERIES = "frag_trans_series";
    private static final String TAG_FRAGMENT_LAST_SUBS = "frag_last_subs";

    private String mCurrentSwitchFragment;

    /* Fix for "Can not perform this action after onSaveInstanceState" */
    private boolean mPermissionsGranted;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = item -> {
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

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private View mConnectionBanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mConnectionBanner = findViewById(R.id.offline_banner);
        mSwipeRefreshLayout = findViewById(R.id.swipe_loading);
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mFragments = new HashMap<>();
        mFragments.put(TAG_FRAGMENT_LAST_SUBS, LastSubtitlesFragment.newInstance());
        mFragments.put(TAG_FRAGMENT_TRANSLATING_SERIES, TranslatingSeriesFragment.newInstance());
        mFragments.put(TAG_FRAGMENT_FAVORITE, FavoriteSeriesFragment.newInstance());
        mFragments.put(TAG_FRAGMENT_ALL_SERIES, AllSeriesFragment.newInstance());

        mCurrentSwitchFragment = (savedInstanceState != null) ?
                savedInstanceState.getString("current_frag", TAG_FRAGMENT_LAST_SUBS) :
                TAG_FRAGMENT_LAST_SUBS;

        // setup periodic worker for check new subtitles
        SubtitleWorker.enableSubtitleNotification(this);
        setupNetworkLiveData();
    }

    /**
     * Force fetch of tv series. Update the local database of tv series.
     */
    private void fetchTVSeries() {
        SeriesViewModel seriesViewModel = ViewModelProviders.of(this).get(SeriesViewModel.class);
        seriesViewModel.getAllSeries().observe(this, listResource -> {
            if(listResource.status == Resource.Status.LOADING) {
                mSwipeRefreshLayout.setRefreshing(true);
            }

            if(listResource.status == Resource.Status.SUCCESS || listResource.status == Resource.Status.ERROR) {
                mSwipeRefreshLayout.setRefreshing(false);
                switchFragment(mCurrentSwitchFragment);
            }
        });
    }

    /**
     * Initialize the observer to network status.
     * - When there is no connections, enable the banner no connection
     * - When there is connection, disable the banner no connection
     */
    private void setupNetworkLiveData() {
        LiveData<Boolean> networkData = new ConnectionLiveData(this);
        networkData.observe(this, connected -> {

            /* Set visibility of connection banner */
            mConnectionBanner.setVisibility(!Objects.isNull(connected) && connected ?
                View.GONE :
                View.VISIBLE);

            fetchTVSeries();
        });
    }

    /**
     * Check the application permission, and ask it if not granted.
     */
    private void checkAppPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, RW_PERMISSION);
        } else mPermissionsGranted = true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RW_PERMISSION:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    mPermissionsGranted = true;
                else
                    finish();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAppPermission();
    }

    @Override
    protected void onPause() {
        super.onPause();
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

    /**
     * Switch to fragment specified on tag.
     * @param tag Tag of fragment to show.
     */
    private void switchFragment(String tag) {
        mCurrentSwitchFragment = tag;
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, mFragments.get(tag), tag)
                .commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.info:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;

            default: return false;
        }
    }
}

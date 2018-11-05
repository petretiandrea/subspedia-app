package com.andreapetreti.subspedia;

import android.Manifest;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;


import com.andreapetreti.android_utils.connectivity.ConnectionLiveData;
import com.andreapetreti.android_utils.ui.BottomNavigationViewHelper;
import com.andreapetreti.subspedia.background.NewSubsWorker;
import com.andreapetreti.subspedia.common.Resource;
import com.andreapetreti.subspedia.ui.fragment.AllSeriesFragment;
import com.andreapetreti.subspedia.ui.fragment.FavoriteSeriesFragment;
import com.andreapetreti.subspedia.ui.fragment.LastSubtitlesFragment;
import com.andreapetreti.subspedia.ui.fragment.TranslatingSeriesFragment;
import com.andreapetreti.subspedia.viewmodel.SeriesViewModel;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

// TODO: handle the initial download of tv series. Before show the fragment show a refreshing swipe
// TODO: or show a loading bar.
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

    private static final String TAG_NO_CONNECTION_FRAGMENT = "no_conn_fragment";

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

    private BottomNavigationView mBottomNavigationView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private View mConnectionBanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ((TextView)findViewById(R.id.toolbar_title)).setText(toolbar.getTitle());
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        mConnectionBanner = findViewById(R.id.offline_banner);
        mSwipeRefreshLayout = findViewById(R.id.swipe_loading);
        mBottomNavigationView = findViewById(R.id.navigation);
        mBottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        BottomNavigationViewHelper.removeShiftMode(mBottomNavigationView);

        mFragments = new HashMap<>();
        mFragments.put(TAG_FRAGMENT_LAST_SUBS, LastSubtitlesFragment.newInstance());
        mFragments.put(TAG_FRAGMENT_TRANSLATING_SERIES, TranslatingSeriesFragment.newInstance());
        mFragments.put(TAG_FRAGMENT_FAVORITE, FavoriteSeriesFragment.newInstance());
        mFragments.put(TAG_FRAGMENT_ALL_SERIES, AllSeriesFragment.newInstance());

        mCurrentSwitchFragment = (savedInstanceState != null) ?
                savedInstanceState.getString("current_frag", TAG_FRAGMENT_LAST_SUBS) :
                TAG_FRAGMENT_LAST_SUBS;

        // setup periodic worker for check new subtitles
        setupNewSubsWorker();
        setupNetworkLiveData();
    }

    /**
     * Force fetch of tv series. Update the local database of tv series.
     */
    private void fetchTVSeries() {
        SeriesViewModel seriesViewModel = ViewModelProviders.of(this).get(SeriesViewModel.class);
        seriesViewModel.getAllSeries().observe(this, listResource -> {

            if(listResource.status == Resource.Status.LOADING) {
                System.out.println("Loading...");
                mSwipeRefreshLayout.setRefreshing(true);
            }

            if(listResource.status == Resource.Status.SUCCESS || listResource.status == Resource.Status.ERROR) {
                System.out.println("Success...");
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
            if(connected) {
                mConnectionBanner.setVisibility(View.GONE);
                //IntStream.range(0, mBottomNavigationView.getMenu().size()).forEach(value -> mBottomNavigationView.getMenu().getItem(value).setEnabled(true));
            } else {
                mConnectionBanner.setVisibility(View.VISIBLE);
                // IntStream.range(0, mBottomNavigationView.getMenu().size()).forEach(value -> mBottomNavigationView.getMenu().getItem(value).setEnabled(false));
            }
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


    /**
     * Initialize the worker for check new subtitle download
     */
    private void setupNewSubsWorker() {
        Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();

        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(NewSubsWorker.class, Constants.PERIOD_SCHEDULE_NOTIFICATION_HOUR, TimeUnit.HOURS)
                .setInputData(new Data.Builder().putLong(Constants.KEY_PERIOD_SCHEDULE_NOTIFICATION, Constants.PERIOD_SCHEDULE_NOTIFICATION_MILLIS).build())
                .setConstraints(constraints)
                .build();

        WorkManager.getInstance().enqueueUniquePeriodicWork("periodic_new_sub", ExistingPeriodicWorkPolicy.KEEP, workRequest);
    }
}

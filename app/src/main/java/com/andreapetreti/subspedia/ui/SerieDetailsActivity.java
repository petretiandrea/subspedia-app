package com.andreapetreti.subspedia.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.andreapetreti.subspedia.model.Subtitle;
import com.annimon.stream.function.Consumer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.andreapetreti.android_utils.PicassoSingleton;
import com.andreapetreti.android_utils.ViewVisibility;
import com.andreapetreti.android_utils.connectivity.ConnectionLiveData;
import com.andreapetreti.subspedia.R;
import com.andreapetreti.subspedia.common.Resource;
import com.andreapetreti.subspedia.model.Serie;
import com.andreapetreti.subspedia.model.SubtitleWithSerie;
import com.andreapetreti.subspedia.ui.adapter.SubtitleListAdapter;
import com.andreapetreti.subspedia.ui.dialog.SubtitleDialog;
import com.andreapetreti.subspedia.viewmodel.SeriesViewModel;
import com.andreapetreti.subspedia.viewmodel.SubtitleViewModel;
import com.annimon.stream.Collectors;
import com.annimon.stream.Objects;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;

/**
 * Activity that shows all subtitles of specific tv serie
 */
public class SerieDetailsActivity extends AppCompatActivity implements Observer<Boolean> {

    /**
     * KEY for bundle, for pass the serie object.
     */
    private static final String KEY_SERIE = "serie";

    /**
     * Current serie showed by the activity.
     */
    private Serie mSerie;

    /**
     * Static method for obtain an intent to this activity. Automatically add the serie to bundle
     * of intent using the proper KEY.
     * @param context Context.
     * @param serie Serie to be passed using intent to this activity.
     * @return A intent that contains the bundle with tv serie.
     */
    public static Intent obtainIntent(Context context, Serie serie) {
        Intent intent = new Intent(context, SerieDetailsActivity.class);
        intent.putExtra(KEY_SERIE, serie);
        return intent;
    }

    /**
     * The {@link PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private Snackbar mSnackbarOffline;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serie_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /* Check for correct intent pass */
        if(getIntent().getExtras() == null) {
            finish();
            return;
        }

        // get the serie from intent.
        mSerie = Objects.requireNonNull(getIntent().getExtras()).getParcelable(KEY_SERIE);

        if(mSerie == null) {
            finish();
            return;
        }

        /* Init the UI */
        toolbar.setTitle(mSerie.getName());
        FloatingActionButton favoriteActionBtn = findViewById(R.id.floatingActionFavorite);

        // Load with picasso the big image of tv serie
        AppCompatImageView extendImage = findViewById(R.id.header);
        PicassoSingleton.getSharedInstance(this)
                .load(mSerie.getLinkBannerImage())
                .into(TargetImageGradient.of(extendImage, R.drawable.gradient_shape));

        // Create the adapter for tabs seasons
        SparseArray<List<SubtitleWithSerie>> subtitles = new SparseArray<>();
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), subtitles);

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        ProgressBar progress = findViewById(R.id.progressBar);

        // set some property of tab layout
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        // retrieve the ViewModel for subtitle and tv serie
        SeriesViewModel seriesViewModel = ViewModelProviders.of(this).get(SeriesViewModel.class);
        SubtitleViewModel subtitleViewModel = ViewModelProviders.of(this).get(SubtitleViewModel.class);

        // Observe the specific serie, and change the icon of action button using the "favorite" status.
        seriesViewModel.getSerie(mSerie.getIdSerie()).observe(this, serie -> {
            mSerie = (serie != null) ? serie : mSerie;
            favoriteActionBtn.setImageDrawable((mSerie.isFavorite()) ?
                    ContextCompat.getDrawable(SerieDetailsActivity.this, R.drawable.ic_star_white) :
                    ContextCompat.getDrawable(SerieDetailsActivity.this, R.drawable.ic_star_border_white));
        });

        mSnackbarOffline = Snackbar.make(findViewById(R.id.main_content), getString(R.string.no_connection), Snackbar.LENGTH_INDEFINITE);

        // Observe for the subtitles of specific tv serie
        subtitleViewModel.getSubtitlesOf(mSerie.getIdSerie()).observe(this, listResource -> {

            // show and hide loading bar when is in loading status and not.
            progress.setVisibility(ViewVisibility.of(() -> listResource.status.equals(Resource.Status.LOADING)));

            // show and hide the tab layout. Show when there is data inside list resource, and hide it if not.
            tabLayout.setVisibility(ViewVisibility.of(() -> Objects.nonNull(listResource.data) && listResource.data.size() > 0));

            /* Show something when the status is success, or show data cached, if available, during loading */
            if(listResource.status == Resource.Status.SUCCESS ||
                    (listResource.status == Resource.Status.LOADING && Objects.nonNull(listResource.data))) {

                int currentSeason = 1;
                List<SubtitleWithSerie> subtitlesTmp = new ArrayList<>();

                tabLayout.removeAllTabs();

                ListIterator<SubtitleWithSerie> it = listResource.data.listIterator();

                while(it.hasNext()) {
                    SubtitleWithSerie subtitle = it.next();

                    if(subtitle.getSubtitle().getSeasonNumber() > currentSeason || !it.hasNext()) {
                        if(!subtitlesTmp.isEmpty()) {
                            String title = String.format(Locale.getDefault(), getString(R.string.season), currentSeason);
                            tabLayout.addTab(tabLayout.newTab().setText(title));
                            subtitles.put(currentSeason - 1, subtitlesTmp);
                            subtitlesTmp = new ArrayList<>();
                            currentSeason++;
                        }
                    }

                    if(subtitle.getSubtitle().getSeasonNumber() == currentSeason)
                        subtitlesTmp.add(subtitle);
                }
                mSectionsPagerAdapter.notifyDataSetChanged();
            }
        });

        // Listener for action button, when pressed change the "favorite" status of serie.
        favoriteActionBtn.setOnClickListener(v -> seriesViewModel.setFavoriteSerie(mSerie.getIdSerie(), !mSerie.isFavorite()));

        setupNetworkStatus();
    }

    /**
     * Setup a live data for network status.
     */
    private void setupNetworkStatus() {
        LiveData<Boolean> networkData = new ConnectionLiveData(this);
        networkData.observe(this, this);
    }

    @Override
    public void onChanged(Boolean connected) {
        if(connected) mSnackbarOffline.dismiss(); else mSnackbarOffline.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SUBTITLES = "subtitles";

        private List<SubtitleWithSerie> mSubtitles;

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(List<SubtitleWithSerie> subtitles) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putSerializable(ARG_SUBTITLES, new ArrayList<>(subtitles));
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getArguments() != null) {
                mSubtitles = (List<SubtitleWithSerie>) getArguments().getSerializable(ARG_SUBTITLES);
            }
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_subtitles, container, false);

            SubtitleListAdapter listAdapter = new SubtitleListAdapter(getActivity(), SubtitleListAdapter.Type.TYPE_SUB);

            RecyclerView recyclerView = rootView.findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(getContext()), DividerItemDecoration.VERTICAL));
            recyclerView.setAdapter(listAdapter);

            listAdapter.setOnItemClickListener((view, adapterPosition) -> SubtitleDialog.newInstance(listAdapter.itemAt(adapterPosition)).show(getFragmentManager(), "aa"));

            listAdapter.setList(mSubtitles);
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm, SparseArray<List<SubtitleWithSerie>> map) {
            super(fm);
            mMap = map;
        }

        private SparseArray<List<SubtitleWithSerie>> mMap;

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(mMap.get(position));
        }

        @Override
        public int getCount() {
            return mMap.size();
        }
    }
}

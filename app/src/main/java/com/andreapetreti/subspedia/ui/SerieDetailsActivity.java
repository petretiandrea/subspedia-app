package com.andreapetreti.subspedia.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.andreapetreti.android_utils.PicassoSingleton;
import com.andreapetreti.android_utils.adapter.ItemClickListener;
import com.andreapetreti.subspedia.AppExecutor;
import com.andreapetreti.subspedia.R;
import com.andreapetreti.subspedia.common.Resource;
import com.andreapetreti.subspedia.database.SerieDao;
import com.andreapetreti.subspedia.database.SubsDatabase;
import com.andreapetreti.subspedia.model.Serie;
import com.andreapetreti.subspedia.model.Subtitle;
import com.andreapetreti.subspedia.model.SubtitleWithSerie;
import com.andreapetreti.subspedia.ui.adapter.SubtitleListAdapter;
import com.andreapetreti.subspedia.ui.dialog.SubtitleDialog;
import com.andreapetreti.subspedia.utils.SubspediaUtils;
import com.andreapetreti.subspedia.viewmodel.SeriesViewModel;
import com.andreapetreti.subspedia.viewmodel.SubtitleViewModel;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Function;
import com.google.gson.JsonObject;
import com.squareup.picasso.Cache;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Activity that shows all subtitles of specific tv serie
 */
public class SerieDetailsActivity extends AppCompatActivity {

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
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

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
        ImageView extendImage = findViewById(R.id.header);
        PicassoSingleton.getSharedInstance(this)
                .load(mSerie.getLinkBannerImage())
                .fit()
                .centerCrop(Gravity.CENTER)
                .into(extendImage);

        // Create the adapter for tabs seasons
        SparseArray<ArrayList<SubtitleWithSerie>> subtitles = new SparseArray<>();
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
            favoriteActionBtn.setImageDrawable(
                    ContextCompat.getDrawable(SerieDetailsActivity.this, mSerie.isFavorite() ? R.drawable.ic_star_white : R.drawable.ic_star_border_white));
        });

        // Observe for the subtitles of specific tv serie
        subtitleViewModel.getSubtitlesOf(mSerie.getIdSerie()).observe(this, listResource -> {

            if(listResource.status == Resource.Status.LOADING) {
                progress.setVisibility(View.VISIBLE);
                tabLayout.setVisibility(View.GONE);
            }

            if(listResource.status == Resource.Status.SUCCESS) {
                int currentSeason = 1;
                if (listResource.data != null) {
                    // TODO: use stream instead of two for loops
                    for (int i = 0; i < listResource.data.size(); i++) {
                        if(listResource.data.get(i).getSubtitle().getSeasonNumber() > (currentSeason - 1)) {
                            // add new season.
                            ArrayList<SubtitleWithSerie> subs = new ArrayList<>();
                            for(SubtitleWithSerie s : listResource.data)
                                if(s.getSubtitle().getSeasonNumber() == currentSeason)
                                    subs.add(s);

                            String title = String.format(Locale.getDefault(), getString(R.string.season), currentSeason);
                            tabLayout.addTab(tabLayout.newTab().setText(title));
                            subtitles.put(currentSeason - 1, subs);
                            currentSeason++;
                        }
                    }
                }
                progress.setVisibility(View.GONE);
                tabLayout.setVisibility(View.VISIBLE);
                mSectionsPagerAdapter.notifyDataSetChanged();
            }
        });

        // Listener for action button, when pressed change the "favorite" status of serie.
        favoriteActionBtn.setOnClickListener(v -> seriesViewModel.setFavoriteSerie(mSerie.getIdSerie(), !mSerie.isFavorite()));
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
        public static PlaceholderFragment newInstance(ArrayList<SubtitleWithSerie> subtitles) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putSerializable(ARG_SUBTITLES, subtitles);
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

        SectionsPagerAdapter(FragmentManager fm, SparseArray<ArrayList<SubtitleWithSerie>> map) {
            super(fm);
            mMap = map;
        }

        private SparseArray<ArrayList<SubtitleWithSerie>> mMap;

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
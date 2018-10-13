package com.andreapetreti.subspedia.ui.fragment;


import android.app.Activity;
import android.app.SearchManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.transition.TransitionManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.andreapetreti.android_utils.ui.LoadingBarMessage;
import com.andreapetreti.subspedia.R;
import com.andreapetreti.subspedia.common.Resource;
import com.andreapetreti.subspedia.model.Serie;
import com.andreapetreti.subspedia.ui.ActivityLoadingBar;
import com.andreapetreti.subspedia.ui.SerieDetailsActivity;
import com.andreapetreti.subspedia.ui.adapter.SerieListAdapter;
import com.andreapetreti.subspedia.viewmodel.SeriesViewModel;

import java.util.List;
import java.util.Optional;

import static android.content.Context.SEARCH_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AllSeriesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AllSeriesFragment extends Fragment {

    private static final String PARAMETER_SHOW_FAVORITE = "com.andreapetreti.subspedia.show_favorite";

    private boolean mShowFavorite;

    public static AllSeriesFragment newInstance(boolean showFavorite) {
        AllSeriesFragment allSeriesFragment = new AllSeriesFragment();

        Bundle bundle = new Bundle();
        bundle.putBoolean(PARAMETER_SHOW_FAVORITE, showFavorite);
        allSeriesFragment.setArguments(bundle);

        return allSeriesFragment;
    }

    public AllSeriesFragment() {
        // Required empty public constructor
    }

    private SerieListAdapter mSerieListAdapter;
    private LoadingBarMessage mLoadingBarMessage;

    private ActivityLoadingBar mActivityLoadingBar;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof ActivityLoadingBar)
            mActivityLoadingBar = (ActivityLoadingBar) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(mActivityLoadingBar != null)
            mActivityLoadingBar.hideLoading();
        mActivityLoadingBar = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null)
            mShowFavorite = getArguments().getBoolean(PARAMETER_SHOW_FAVORITE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_all_series, container, false);

        SwipeRefreshLayout refreshLayout = rootView.findViewById(R.id.swiperefresh);
        mLoadingBarMessage = rootView.findViewById(R.id.progressMessage);
        mLoadingBarMessage.getProgressBar().setIndeterminate(true);
        mSerieListAdapter = new SerieListAdapter(getActivity());

        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerViewSeries);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(mSerieListAdapter);

        SeriesViewModel seriesViewModel = ViewModelProviders.of(this).get(SeriesViewModel.class);

        if(mShowFavorite)
            seriesViewModel.getFavoriteSeries().observe(this, series -> {
                mLoadingBarMessage.setVisibility(View.GONE);
                mActivityLoadingBar.hideLoading();
                if(series != null && series.size() > 0)
                    mSerieListAdapter.setSeries(series);
                //else
                    // TODO: show empty message
            });
        else
            seriesViewModel.getAllSeries().observe(this, listResource -> {

                // with no data and loading status, there is no data to show, start a progress bar
                if(listResource.status == Resource.Status.LOADING) {
                    if(listResource.data == null || listResource.data.isEmpty())
                        mLoadingBarMessage.setVisibility(View.VISIBLE);
                }

                // generally if data are available, set it.
                if(listResource.data != null) {
                    mSerieListAdapter.setSeries(listResource.data);

                    // if data are available, not show the central progress bar.
                    mLoadingBarMessage.setVisibility(View.GONE);

                    if(listResource.status == Resource.Status.LOADING) {
                        if(mActivityLoadingBar != null)
                            mActivityLoadingBar.showLoading();
                    } else if(listResource.status == Resource.Status.SUCCESS) {
                        if(mActivityLoadingBar != null)
                            mActivityLoadingBar.hideLoading();
                        if(refreshLayout.isRefreshing())
                            refreshLayout.setRefreshing(false);
                    }
                }
            });

       // refreshLayout.setOnRefreshListener(seriesViewModel::forceRefresh);

        mSerieListAdapter.setItemClickListener((view, adapterPosition) -> {
            //Intent intent = SerieDetailsActivity.obtainIntent(getActivity(), mSerieListAdapter.itemAt(adapterPosition));
            Intent intent = SerieDetailsActivity.obtainIntent(getActivity(), mSerieListAdapter.itemAt(adapterPosition));
            startActivity(intent);
        });

        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.dashboard_menu, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mSerieListAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mSerieListAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }

}

package com.andreapetreti.subspedia.ui.fragment;

import android.app.SearchManager;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andreapetreti.android_utils.adapter.EmptyRecyclerView;
import com.andreapetreti.android_utils.ui.LoadingBarMessage;
import com.andreapetreti.subspedia.R;
import com.andreapetreti.subspedia.model.Serie;
import com.andreapetreti.subspedia.ui.adapter.SerieListAdapter;
import com.andreapetreti.subspedia.viewmodel.SeriesViewModel;

import static android.content.Context.SEARCH_SERVICE;

public abstract class SeriesFragment extends Fragment {
    /**
     * Adapter for tv series.
     */
    private SerieListAdapter mSerieListAdapter;
    /**
     * Loading bar and message showed when list is loading.
     */
    private SwipeRefreshLayout mRefreshLayout;
    private SeriesViewModel mSeriesViewModel;
    private EmptyRecyclerView recyclerView;

    public SeriesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_series, container, false);

        mRefreshLayout = rootView.findViewById(R.id.swiperefresh);
        mSerieListAdapter = new SerieListAdapter(getActivity());

        recyclerView = rootView.findViewById(R.id.recyclerViewSeries);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(mSerieListAdapter);

        mSeriesViewModel = ViewModelProviders.of(this).get(SeriesViewModel.class);

        mSerieListAdapter.setItemClickListener((view, adapterPosition) -> onItemSerieClick(mSerieListAdapter.itemAt(adapterPosition)));

        setHasOptionsMenu(true);

        onCreateSeriesView(rootView, inflater, container, savedInstanceState);

        return rootView;
    }

    protected SeriesViewModel getSeriesViewModel() {
        return mSeriesViewModel;
    }

    protected SwipeRefreshLayout getSwipeRefreshLayout() {
        return mRefreshLayout;
    }

    protected SerieListAdapter getSerieListAdapter() {
        return mSerieListAdapter;
    }

    protected EmptyRecyclerView getRecyclerView() {
        return recyclerView;
    }

    protected abstract void onItemSerieClick(Serie serie);

    protected abstract void onCreateSeriesView(View rootView, LayoutInflater inflater, ViewGroup container,
                                               Bundle savedInstanceState);

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

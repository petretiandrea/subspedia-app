package com.andreapetreti.subspedia.ui.fragment;

import android.animation.Animator;
import android.app.SearchManager;

import androidx.core.view.MenuItemCompat;
import androidx.lifecycle.ViewModelProviders;

import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;

import com.andreapetreti.android_utils.adapter.EmptyRecyclerView;
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
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                setItemsVisibility(menu, menuItem, false);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                setItemsVisibility(menu, menuItem, true);
                return true;
            }
        });

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
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void setItemsVisibility(Menu menu, MenuItem exception, boolean visible) {
        for (int i=0; i<menu.size(); ++i) {
            MenuItem item = menu.getItem(i);
            if (item != exception) item.setVisible(visible);
        }
    }

    private void circleRevealView(View toBeReveal, View frame) {

        int centerX = (frame.getLeft() + frame.getRight()) / 2;
        int centerY = (frame.getTop() + frame.getBottom()) / 2;
        float startRadius = 0.0f;
        float endRadius = Math.max(frame.getWidth(), frame.getHeight());

        if(Build.VERSION.SDK_INT >= 21) {
            Animator anim = ViewAnimationUtils.createCircularReveal(toBeReveal,
                    centerX,
                    centerY,
                    startRadius,
                    endRadius);
            toBeReveal.setVisibility(View.VISIBLE);
            anim.start();
            return;
        }
        toBeReveal.setVisibility(View.VISIBLE);
    }
}

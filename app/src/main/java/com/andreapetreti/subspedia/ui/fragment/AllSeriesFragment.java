package com.andreapetreti.subspedia.ui.fragment;


import android.app.SearchManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
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
import android.widget.TextView;

import com.andreapetreti.android_utils.adapter.EmptyRecyclerView;
import com.andreapetreti.android_utils.ui.LoadingBarMessage;
import com.andreapetreti.subspedia.R;
import com.andreapetreti.subspedia.common.Resource;
import com.andreapetreti.subspedia.model.Serie;
import com.andreapetreti.subspedia.ui.SerieDetailsActivity;
import com.andreapetreti.subspedia.ui.adapter.SerieListAdapter;
import com.andreapetreti.subspedia.ui.custom.EmptyView;
import com.andreapetreti.subspedia.viewmodel.SeriesViewModel;

import java.util.Objects;

import static android.content.Context.SEARCH_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AllSeriesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AllSeriesFragment extends SeriesFragment {

    private EmptyView mEmptyView;

    public static AllSeriesFragment newInstance() {
        return new AllSeriesFragment();
    }

    public AllSeriesFragment() {
        // Required empty public constructor
    }

    @Override
    protected void onCreateSeriesView(View rootView, LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mEmptyView = Objects.requireNonNull(rootView).findViewById(R.id.emptyView);
        mEmptyView.setTitle(getString(R.string.empty_list_series_title));
        mEmptyView.setContent(getString(R.string.empty_list_series_content));
        setupAllSeries(getSeriesViewModel());
    }

    @Override
    protected void onItemSerieClick(Serie serie) {
        Intent intent = SerieDetailsActivity.obtainIntent(getActivity(), serie);
        startActivity(intent);
    }

    private void setupAllSeries(SeriesViewModel viewModel) {
        getSwipeRefreshLayout().setEnabled(true);
        getSwipeRefreshLayout().setOnRefreshListener(viewModel::refreshAllSeries);
        viewModel.getAllSeries().observe(this, listResource -> {

            // the refreshing status is set true only if is loading
            getSwipeRefreshLayout().setRefreshing(listResource.status.equals(Resource.Status.LOADING));

            if(listResource.status.equals(Resource.Status.SUCCESS) ||
                    listResource.status.equals(Resource.Status.ERROR)) {

                mEmptyView.setVisibility((com.annimon.stream.Objects.isNull(listResource.data) || listResource.data.isEmpty()) ?
                        View.VISIBLE :
                        View.GONE);

                getSerieListAdapter().setSeries(listResource.data);
            }
        });
    }
}

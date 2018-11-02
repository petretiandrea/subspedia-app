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
import com.andreapetreti.subspedia.viewmodel.SeriesViewModel;

import java.util.Objects;

import static android.content.Context.SEARCH_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AllSeriesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AllSeriesFragment extends SeriesFragment {

    private TextView mMessageView;

    public static AllSeriesFragment newInstance() {
        return new AllSeriesFragment();
    }

    public AllSeriesFragment() {
        // Required empty public constructor
    }

    @Override
    protected void onCreateSeriesView(View rootView, LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMessageView = Objects.requireNonNull(rootView).findViewById(R.id.emptyView);
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

            // show and hide loading bar message
            if(listResource.status == Resource.Status.LOADING && (listResource.data == null || listResource.data.isEmpty())) {
                mMessageView.setVisibility(View.GONE);
                getLoadingBarMessage().setVisibility(View.VISIBLE);
            } else if(listResource.status != Resource.Status.LOADING) {
                getLoadingBarMessage().setVisibility(View.GONE);
                getSwipeRefreshLayout().setRefreshing(false);
            }

            // if user swipe to refresh hide the loading bar
            if(getSwipeRefreshLayout().isRefreshing())
                getLoadingBarMessage().setVisibility(View.GONE);


            // set data
            if(listResource.status == Resource.Status.SUCCESS && listResource.data != null) {
                getSerieListAdapter().setSeries(listResource.data);
            } else if(listResource.status == Resource.Status.ERROR) {
                // error
                getSerieListAdapter().setSeries(null);
                mMessageView.setText(getString(R.string.empty_msg_series));
                mMessageView.setVisibility(View.VISIBLE);
            }
        });
    }
}

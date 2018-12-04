package com.andreapetreti.subspedia.ui.fragment;


import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andreapetreti.subspedia.R;
import com.andreapetreti.subspedia.common.Resource;
import com.andreapetreti.subspedia.model.Serie;
import com.andreapetreti.subspedia.ui.SerieDetailsActivity;
import com.andreapetreti.subspedia.ui.custom.EmptyView;
import com.andreapetreti.subspedia.viewmodel.SeriesViewModel;

import java.util.Objects;

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

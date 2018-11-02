package com.andreapetreti.subspedia.ui.fragment;

import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andreapetreti.subspedia.R;
import com.andreapetreti.subspedia.model.Serie;
import com.andreapetreti.subspedia.ui.SerieDetailsActivity;
import com.annimon.stream.Objects;

import java.util.List;

public class FavoriteSeriesFragment extends SeriesFragment {

    public static FavoriteSeriesFragment newInstance() {
        return new FavoriteSeriesFragment();
    }

    public FavoriteSeriesFragment() {}

    @Override
    protected void onCreateSeriesView(View rootView, LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        TextView emptyView = rootView.findViewById(R.id.emptyView);
        emptyView.setText(getString(R.string.empty_msg_favorite));
        getRecyclerView().setEmptyView(emptyView);

        /* Setup favorite series */
        getSwipeRefreshLayout().setEnabled(false);
        getSeriesViewModel().getFavoriteSeries().observe(this, series -> {
            getLoadingBarMessage().setVisibility(View.GONE);
            if(Objects.nonNull(series))
                getSerieListAdapter().setSeries(series);
        });
    }

    @Override
    protected void onItemSerieClick(Serie serie) {
        Intent intent = SerieDetailsActivity.obtainIntent(getActivity(), serie);
        startActivity(intent);
    }
}

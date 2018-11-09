package com.andreapetreti.subspedia.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andreapetreti.subspedia.R;
import com.andreapetreti.subspedia.model.Serie;
import com.andreapetreti.subspedia.ui.SerieDetailsActivity;
import com.andreapetreti.subspedia.ui.custom.EmptyView;
import com.annimon.stream.Objects;
import com.annimon.stream.Optional;
import com.annimon.stream.function.Consumer;

import java.util.List;

public class FavoriteSeriesFragment extends SeriesFragment {

    public static FavoriteSeriesFragment newInstance() {
        return new FavoriteSeriesFragment();
    }

    public FavoriteSeriesFragment() {}

    @Override
    protected void onCreateSeriesView(View rootView, LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        EmptyView emptyView = rootView.findViewById(R.id.emptyView);
        emptyView.setTitle(getString(R.string.empty_list_favorite_series_title));
        emptyView.setContent(getString(R.string.empty_list_favorite_series_content));

        /* Setup favorite series */
        getSwipeRefreshLayout().setEnabled(false);
        getSeriesViewModel().getFavoriteSeries().observe(this, series -> {
            emptyView.setVisibility((Objects.isNull(series) || series.isEmpty()) ?
                    View.VISIBLE :
                    View.GONE);

            Optional.ofNullable(series).ifPresent(getSerieListAdapter()::setSeries);
        });
    }

    @Override
    protected void onItemSerieClick(Serie serie) {
        Intent intent = SerieDetailsActivity.obtainIntent(getActivity(), serie);
        startActivity(intent);
    }
}

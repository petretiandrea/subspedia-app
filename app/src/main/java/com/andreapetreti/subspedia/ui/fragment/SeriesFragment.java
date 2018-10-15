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
import android.support.v7.widget.RecyclerView;
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
import com.andreapetreti.subspedia.ui.SerieDetailsActivity;
import com.andreapetreti.subspedia.ui.adapter.SerieListAdapter;
import com.andreapetreti.subspedia.viewmodel.SeriesViewModel;

import org.w3c.dom.Text;

import static android.content.Context.SEARCH_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SeriesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SeriesFragment extends Fragment {

    private static final String PARAMETER_SHOW_FAVORITE = "com.andreapetreti.subspedia.show_favorite";

    /**
     * Favorite flag for indicate if the fragment show only favorite or not.
     */
    private boolean mShowFavorite;
    /**
     * Adapter for tv series.
     */
    private SerieListAdapter mSerieListAdapter;
    /**
     * Loading bar and message showed when list is loading.
     */
    private LoadingBarMessage mLoadingBarMessage;

    public static SeriesFragment newInstance(boolean showFavorite) {
        SeriesFragment seriesFragment = new SeriesFragment();

        Bundle bundle = new Bundle();
        bundle.putBoolean(PARAMETER_SHOW_FAVORITE, showFavorite);
        seriesFragment.setArguments(bundle);

        return seriesFragment;
    }

    public SeriesFragment() {
        // Required empty public constructor
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
        View rootView = inflater.inflate(R.layout.fragment_series, container, false);

        SwipeRefreshLayout refreshLayout = rootView.findViewById(R.id.swiperefresh);
        mLoadingBarMessage = rootView.findViewById(R.id.progressMessage);
        mLoadingBarMessage.getProgressBar().setIndeterminate(true);
        mSerieListAdapter = new SerieListAdapter(getActivity());

        EmptyRecyclerView recyclerView = rootView.findViewById(R.id.recyclerViewSeries);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(mSerieListAdapter);
        TextView emptyView = rootView.findViewById(R.id.emptyView);
        emptyView.setText(getString(mShowFavorite ? R.string.empty_msg_favorite : R.string.empty));
        recyclerView.setEmptyView(emptyView);

        // TODO: disable refresh if mShowFavorite is true.

        SeriesViewModel seriesViewModel = ViewModelProviders.of(this).get(SeriesViewModel.class);

        if(mShowFavorite)
            seriesViewModel.getFavoriteSeries().observe(this, series -> {
                mLoadingBarMessage.setVisibility(View.GONE);
                if(series != null)
                    mSerieListAdapter.setSeries(series);
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

                    /*if(listResource.status == Resource.Status.LOADING) {
                        if(mActivityLoadingBar != null)
                            mActivityLoadingBar.showLoading();
                    } else if(listResource.status == Resource.Status.SUCCESS) {
                        if(mActivityLoadingBar != null)
                            mActivityLoadingBar.hideLoading();
                        if(refreshLayout.isRefreshing())
                            refreshLayout.setRefreshing(false);
                    }*/
                }
            });

       // refreshLayout.setOnRefreshListener(seriesViewModel::forceRefresh);

        mSerieListAdapter.setItemClickListener((view, adapterPosition) -> {
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

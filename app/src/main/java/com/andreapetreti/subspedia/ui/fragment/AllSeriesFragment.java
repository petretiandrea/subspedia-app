package com.andreapetreti.subspedia.ui.fragment;


import android.app.SearchManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.andreapetreti.android_utils.ui.LoadingBarMessage;
import com.andreapetreti.subspedia.R;
import com.andreapetreti.subspedia.common.Resource;
import com.andreapetreti.subspedia.model.Serie;
import com.andreapetreti.subspedia.ui.SerieDetailsActivity;
import com.andreapetreti.subspedia.ui.adapter.ItemClickListener;
import com.andreapetreti.subspedia.ui.adapter.SerieListAdapter;
import com.andreapetreti.subspedia.viewmodel.SeriesViewModel;
import com.jaeger.library.StatusBarUtil;

import java.util.List;

import static android.content.Context.SEARCH_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AllSeriesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AllSeriesFragment extends Fragment {

    public AllSeriesFragment() {
        // Required empty public constructor
    }

    private SerieListAdapter mSerieListAdapter;
    private LoadingBarMessage mLoadingBarMessage;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment AllSeriesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AllSeriesFragment newInstance() {
        return new AllSeriesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        seriesViewModel.getAllSeries().observe(this, listResource -> {

            if(listResource.status == Resource.Status.LOADING) {
                mLoadingBarMessage.setVisibility(View.VISIBLE);
            }

            if(listResource.status == Resource.Status.SUCCESS) {
                mSerieListAdapter.setSeries(listResource.data);
                mLoadingBarMessage.setVisibility(View.GONE);
                if(refreshLayout.isRefreshing())
                    refreshLayout.setRefreshing(false);
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

package com.andreapetreti.subspedia.ui.fragment;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andreapetreti.android_utils.ui.LoadingBarMessage;
import com.andreapetreti.subspedia.R;
import com.andreapetreti.subspedia.common.Resource;
import com.andreapetreti.subspedia.model.SerieTranslating;
import com.andreapetreti.subspedia.ui.adapter.SerieListAdapter;
import com.andreapetreti.subspedia.viewmodel.SerieTranslatingViewModel;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TranslatingSeriesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TranslatingSeriesFragment extends android.support.v4.app.Fragment {

    private LoadingBarMessage mLoadingBarMessage;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private SerieTranslatingViewModel mTranslatingViewModel;
    private SerieListAdapter mSerieListAdapter;

    public TranslatingSeriesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment TranslatingSeriesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TranslatingSeriesFragment newInstance() {
        return new TranslatingSeriesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_translating_series, container, false);

        mLoadingBarMessage = rootView.findViewById(R.id.progressMessage);
        mLoadingBarMessage.getProgressBar().setIndeterminate(true);

        mSerieListAdapter = new SerieListAdapter(getActivity());

        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerViewTranslating);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(mSerieListAdapter);

        mSwipeRefreshLayout  = rootView.findViewById(R.id.swiperefresh);

        mTranslatingViewModel = ViewModelProviders.of(this).get(SerieTranslatingViewModel.class);
        mSwipeRefreshLayout.setOnRefreshListener(mTranslatingViewModel::refreshTranslatingSeries);
        mTranslatingViewModel.getAllTranslatingSeries().observe(this, listResource -> {

            if(listResource.status == Resource.Status.LOADING && !mSwipeRefreshLayout.isRefreshing())
                mLoadingBarMessage.setVisibility(View.VISIBLE);

            if(listResource.status == Resource.Status.SUCCESS) {
                mSerieListAdapter.setSeries(listResource.data);
                mLoadingBarMessage.setVisibility(View.GONE);
                if(mSwipeRefreshLayout.isRefreshing())
                    mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        return rootView;
    }
}

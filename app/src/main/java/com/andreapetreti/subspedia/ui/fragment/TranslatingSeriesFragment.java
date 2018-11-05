package com.andreapetreti.subspedia.ui.fragment;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andreapetreti.android_utils.adapter.EmptyRecyclerView;
import com.andreapetreti.android_utils.ui.LoadingBarMessage;
import com.andreapetreti.subspedia.R;
import com.andreapetreti.subspedia.common.Resource;
import com.andreapetreti.subspedia.model.SerieTranslating;
import com.andreapetreti.subspedia.ui.SerieDetailsActivity;
import com.andreapetreti.subspedia.ui.adapter.ItemClickListener;
import com.andreapetreti.subspedia.ui.adapter.SerieListAdapter;
import com.andreapetreti.subspedia.viewmodel.SerieTranslatingViewModel;
import com.annimon.stream.Objects;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TranslatingSeriesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TranslatingSeriesFragment extends android.support.v4.app.Fragment {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private SerieListAdapter mSerieListAdapter;
    private View mEmptyView;

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

        mSerieListAdapter = new SerieListAdapter(getActivity());

        mEmptyView = rootView.findViewById(R.id.emptyView);
        EmptyRecyclerView recyclerView = rootView.findViewById(R.id.recyclerViewTranslating);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(mSerieListAdapter);

        mSwipeRefreshLayout  = rootView.findViewById(R.id.swiperefresh);

        SerieTranslatingViewModel translatingViewModel = ViewModelProviders.of(this).get(SerieTranslatingViewModel.class);
        mSwipeRefreshLayout.setOnRefreshListener(translatingViewModel::refreshTranslatingSeries);
        translatingViewModel.getAllTranslatingSeries().observe(this, listResource -> {

            // the refreshing status is set true only if is loading
            mSwipeRefreshLayout.setRefreshing(listResource.status.equals(Resource.Status.LOADING));

            if(listResource.status.equals(Resource.Status.SUCCESS) ||
                    listResource.status.equals(Resource.Status.ERROR)) {

                mEmptyView.setVisibility((Objects.isNull(listResource.data) || listResource.data.isEmpty()) ?
                        View.VISIBLE :
                        View.GONE);

                mSerieListAdapter.setSeries(listResource.data);
            }
        });

        mSerieListAdapter.setItemClickListener(
                (view, adapterPosition) -> startActivity(SerieDetailsActivity.obtainIntent(getActivity(), mSerieListAdapter.itemAt(adapterPosition)))
        );

        return rootView;
    }
}

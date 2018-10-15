package com.andreapetreti.subspedia.ui.fragment;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andreapetreti.android_utils.adapter.ItemClickListener;
import com.andreapetreti.android_utils.ui.LoadingBarMessage;
import com.andreapetreti.subspedia.R;
import com.andreapetreti.subspedia.common.Resource;
import com.andreapetreti.subspedia.model.Subtitle;
import com.andreapetreti.subspedia.ui.adapter.SubtitleListAdapter;
import com.andreapetreti.subspedia.ui.dialog.SubtitleDialog;
import com.andreapetreti.subspedia.viewmodel.SubtitleViewModel;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LastSubtitlesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LastSubtitlesFragment extends Fragment {

    private LoadingBarMessage mLoadingBarMessage;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private SubtitleListAdapter mSubtitleListAdapter;

    private SubtitleViewModel mSubtitleViewModel;

    public LastSubtitlesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static LastSubtitlesFragment newInstance() {
        return new LastSubtitlesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_last_subtitles, container, false);

        mLoadingBarMessage = rootView.findViewById(R.id.progressMessage);
        mLoadingBarMessage.getProgressBar().setIndeterminate(true);

        mSwipeRefreshLayout  = rootView.findViewById(R.id.swiperefresh);
        mSubtitleListAdapter = new SubtitleListAdapter(getActivity(), SubtitleListAdapter.Type.TYPE_LAST_SUB);

        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerViewSubs);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(mSubtitleListAdapter);

        mSubtitleViewModel = ViewModelProviders.of(this).get(SubtitleViewModel.class);
        mSwipeRefreshLayout.setOnRefreshListener(this::initViewModel);

        mSubtitleListAdapter.setOnItemClickListener((view, adapterPosition) ->
                SubtitleDialog.newInstance(mSubtitleListAdapter.itemAt(adapterPosition)).show(getFragmentManager(), "ee2"));

        initViewModel();

        return rootView;
    }

    private void initViewModel() {
        mSubtitleViewModel.getLastSubtitles().removeObservers(getActivity());
        mSubtitleViewModel.getLastSubtitles().observe(getActivity(), listResource -> {
            if(listResource.status == Resource.Status.LOADING && !mSwipeRefreshLayout.isRefreshing())
                mLoadingBarMessage.setVisibility(View.VISIBLE);

            if(listResource.status == Resource.Status.SUCCESS) {
                mSubtitleListAdapter.setList(listResource.data);
                mSubtitleListAdapter.notifyDataSetChanged();
                mLoadingBarMessage.setVisibility(View.GONE);
                if(mSwipeRefreshLayout.isRefreshing())
                    mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}

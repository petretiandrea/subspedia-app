package com.andreapetreti.subspedia.ui.fragment;


import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andreapetreti.android_utils.adapter.EmptyRecyclerView;
import com.andreapetreti.subspedia.R;
import com.andreapetreti.subspedia.common.Resource;
import com.andreapetreti.subspedia.ui.adapter.SubtitleListAdapter;
import com.andreapetreti.subspedia.ui.custom.EmptyView;
import com.andreapetreti.subspedia.ui.dialog.SubtitleDialog;
import com.andreapetreti.subspedia.viewmodel.SubtitleViewModel;
import com.annimon.stream.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LastSubtitlesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LastSubtitlesFragment extends Fragment {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private SubtitleListAdapter mSubtitleListAdapter;

    private SubtitleViewModel mSubtitleViewModel;
    private EmptyView mEmptyView;

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

        mSwipeRefreshLayout  = rootView.findViewById(R.id.swiperefresh);
        mSubtitleListAdapter = new SubtitleListAdapter(getActivity(), SubtitleListAdapter.Type.TYPE_LAST_SUB);

        mEmptyView = rootView.findViewById(R.id.emptyView);
        mEmptyView.setTitle(getString(R.string.empty_last_subtitles_title));
        mEmptyView.setContent(getString(R.string.empty_last_subtitles_content));
        EmptyRecyclerView recyclerView = rootView.findViewById(R.id.recyclerViewSubs);
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

    /** Initialize the view model for latest subtitles */
    private void initViewModel() {
        mSubtitleViewModel.getLastSubtitles().removeObservers(getActivity());
        mSubtitleViewModel.getLastSubtitles().observe(getActivity(), listResource -> {

            // the refreshing status is set true only if is loading
            mSwipeRefreshLayout.setRefreshing(listResource.status.equals(Resource.Status.LOADING));

            if(listResource.status.equals(Resource.Status.SUCCESS) ||
                    listResource.status.equals(Resource.Status.ERROR)) {

                mEmptyView.setVisibility((Objects.isNull(listResource.data) || listResource.data.isEmpty()) ?
                        View.VISIBLE :
                        View.GONE);

                mSubtitleListAdapter.setList(listResource.data);
                mSubtitleListAdapter.notifyDataSetChanged();
            }
        });
    }
}

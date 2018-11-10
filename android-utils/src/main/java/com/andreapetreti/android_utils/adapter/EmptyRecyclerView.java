package com.andreapetreti.android_utils.adapter;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

public class EmptyRecyclerView extends RecyclerView {

    /**
     * Empty View to show when data of adapter is empty.
     */
    private View mEmptyView;


    public EmptyRecyclerView(Context context) {
        super(context);
    }

    public EmptyRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public EmptyRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Check if the adapter is empty, and set to visible the empty view and gone the recycler view.
     */
    private void checkIfEmpty() {
        if(mEmptyView != null && getAdapter() != null) {
            final boolean emptyViewVisible = getAdapter().getItemCount() == 0;
            mEmptyView.setVisibility(emptyViewVisible ? View.VISIBLE : View.GONE);
            setVisibility(emptyViewVisible ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {
        final Adapter oldAdapter = getAdapter();
        if(oldAdapter != null)
            oldAdapter.unregisterAdapterDataObserver(mAdapterDataObserver);
        super.setAdapter(adapter);
        if(adapter != null)
            adapter.registerAdapterDataObserver(mAdapterDataObserver);

        checkIfEmpty();
    }

    /**
     * Set empty view, that will be show when the adapter is empty.
     * @param emptyView Empty view to set.
     */
    public void setEmptyView(View emptyView) {
        mEmptyView = emptyView;
        checkIfEmpty();
    }

    /**
     * Observer for data change inside adapter, it call "checkIfEmpty()"
     */
    private final AdapterDataObserver mAdapterDataObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            checkIfEmpty();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            checkIfEmpty();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            checkIfEmpty();
        }
    };
}

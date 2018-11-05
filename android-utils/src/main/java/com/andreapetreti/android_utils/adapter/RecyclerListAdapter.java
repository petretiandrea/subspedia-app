package com.andreapetreti.android_utils.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.annimon.stream.Objects;
import com.annimon.stream.Optional;
import com.annimon.stream.function.Consumer;

import java.util.ArrayList;
import java.util.List;

public abstract class RecyclerListAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private Optional<List<T>> mTList;
    private LayoutInflater mInflater;

    private ItemClickListener mItemClickListener;

    public RecyclerListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        mTList = Optional.empty();
        mItemClickListener = null;
    }

    protected LayoutInflater getInflater() { return mInflater; }

    public void setList (List<T> list) {
        mTList.ifPresent(List::clear);
        mTList = Optional.ofNullable(list);
    }

    public void setOnItemClickListener(ItemClickListener clickListener) {
        mItemClickListener = clickListener;
    }

    public T itemAt(int position) {
        if(mTList.isPresent())
            return mTList.get().get(position);
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull final VH holder, int position) {
        holder.itemView.setOnClickListener(v -> {
            if(mItemClickListener != null)
                mItemClickListener.onItemClick(holder.itemView, holder.getAdapterPosition());
        });
        onBindVHolder(holder, position);
    }

    protected abstract void onBindVHolder(@NonNull final VH holder, int position);

    @Override
    public int getItemCount() {
        if(mTList.isPresent())
            return mTList.get().size();
        return 0;
    }

}

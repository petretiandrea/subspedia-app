package com.andreapetreti.android_utils.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

public abstract class RecyclerListAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private List<T> mTList;
    private LayoutInflater mInflater;

    private ItemClickListener mItemClickListener;

    public RecyclerListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        mTList = new ArrayList<>();
        mItemClickListener = null;
    }

    protected LayoutInflater getInflater() { return mInflater; }

    public void setList (List<T> list) {
        mTList.clear();
        mTList = list;
    }

    public void setOnItemClickListener(ItemClickListener clickListener) {
        mItemClickListener = clickListener;
    }

    public List<T> getList() {
        return mTList;
    }

    public T itemAt(int position) {
        return mTList.get(position);
    }

    @Override
    public void onBindViewHolder(@NonNull final VH holder, int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mItemClickListener != null)
                    mItemClickListener.onItemClick(holder.itemView, holder.getAdapterPosition());
            }
        });
        onBindVHolder(holder, position);
    }

    protected abstract void onBindVHolder(@NonNull final VH holder, int position);

    @Override
    public int getItemCount() {
        return mTList.size();
    }

}

package com.andreapetreti.android_utils.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public abstract class RecyclerListAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private List<T> mTList;
    private LayoutInflater mInflater;

    public RecyclerListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        mTList = new ArrayList<>();
    }

    protected LayoutInflater getInflater() { return mInflater; }

    public void setList (List<T> list) {
        mTList.clear();
        mTList = list;
    }

    public List<T> getList() {
        return mTList;
    }

    @Override
    public int getItemCount() {
        return mTList.size();
    }
}

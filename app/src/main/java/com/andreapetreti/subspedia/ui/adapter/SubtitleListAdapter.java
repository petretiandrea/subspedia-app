package com.andreapetreti.subspedia.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.andreapetreti.android_utils.adapter.RecyclerListAdapter;
import com.andreapetreti.subspedia.R;
import com.andreapetreti.subspedia.model.Subtitle;
import com.squareup.picasso.Cache;
import com.squareup.picasso.Picasso;

import java.util.Locale;

public class SubtitleListAdapter extends RecyclerListAdapter<Subtitle, SubtitleListAdapter.SubtitleViewHolder> {

    private Picasso mPicasso;

    public SubtitleListAdapter(Context context) {
        super(context);
        mPicasso = new Picasso.Builder(context)
                .memoryCache(Cache.NONE)
                .requestTransformer(Picasso.RequestTransformer.IDENTITY)
                .build();
    }

    @NonNull
    @Override
    public SubtitleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = getInflater().inflate(R.layout.subtitle_details_item, parent, false);
        return new SubtitleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubtitleViewHolder holder, int position) {
        Subtitle sub = getList().get(position);

        mPicasso.load(sub.getSubtitleImage()).fit().centerCrop(Gravity.CENTER).into(holder.mThubSub);
        holder.mTxtTitle.setText(sub.getEpisodeTitle());
        holder.mTxtCaption.setText(String.format(Locale.getDefault(),
                "%dx%d - %s",
                sub.getEpisodeNumber(),
                sub.getSeasonNumber(),
                sub.getDate()));

    }

    class SubtitleViewHolder extends RecyclerView.ViewHolder {

        ImageView mThubSub;
        TextView mTxtTitle;
        TextView mTxtCaption;

        SubtitleViewHolder(View itemView) {
            super(itemView);
            mThubSub = itemView.findViewById(R.id.thubSub);
            mTxtTitle = itemView.findViewById(R.id.txtTitle);
            mTxtCaption = itemView.findViewById(R.id.txtCaption);
        }
    }
}

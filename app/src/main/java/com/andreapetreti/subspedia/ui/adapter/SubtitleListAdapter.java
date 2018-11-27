package com.andreapetreti.subspedia.ui.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.andreapetreti.androidcommonutils.view.adapter.RecyclerListAdapter;
import com.andreapetreti.subspedia.R;
import com.andreapetreti.subspedia.model.SubtitleWithSerie;
import com.andreapetreti.subspedia.utils.PicassoSingleton;
import com.andreapetreti.subspedia.utils.SubspediaUtils;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.Locale;

public class SubtitleListAdapter extends RecyclerListAdapter<SubtitleWithSerie, SubtitleListAdapter.SubtitleViewHolder> {

    public enum Type {
        TYPE_LAST_SUB,
        TYPE_SUB
    }

    private Picasso mPicasso;
    private Type mType;

    public SubtitleListAdapter(Context context, Type type) {
        super(context);
        mPicasso = PicassoSingleton.getInstance(context);
        mType = type;
    }

    @Override
    public int getItemViewType(int position) {
        return mType.ordinal();
    }

    @NonNull
    @Override
    public SubtitleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = getInflater().inflate(R.layout.subtitle_details_item, parent, false);
        return new SubtitleViewHolder(view);
    }

    @Override
    protected void onBindVHolder(@NonNull SubtitleViewHolder holder, int position) {
        SubtitleWithSerie sub = itemAt(position);
        mPicasso.load(sub.getSubtitle().getSubtitleImage())
                .placeholder(R.drawable.placeholder_subtitles)
                .fit()
                .centerCrop(Gravity.CENTER)
                .into(holder.mThubSub);

        if(getItemViewType(position) == Type.TYPE_SUB.ordinal()) {
            holder.mTxtTitle.setText(sub.getSubtitle().getEpisodeTitle());
            holder.mTxtCaption.setText(String.format(Locale.getDefault(),
                    "%dx%d - %s",
                    sub.getSubtitle().getSeasonNumber(),
                    sub.getSubtitle().getEpisodeNumber(),
                    SubspediaUtils.formatToDefaultDate(sub.getSubtitle().getDateObj().orElse(new Date()))));
        } else {
            holder.mTxtTitle.setText(sub.getSerie().getName());
            holder.mTxtCaption.setText(String.format(Locale.getDefault(), "%dx%d - %s",
                    sub.getSubtitle().getSeasonNumber(),
                    sub.getSubtitle().getEpisodeNumber(),
                    sub.getSubtitle().getEpisodeTitle()));
        }
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

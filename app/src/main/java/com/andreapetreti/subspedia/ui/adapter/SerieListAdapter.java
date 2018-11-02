package com.andreapetreti.subspedia.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.andreapetreti.android_utils.PicassoSingleton;
import com.andreapetreti.subspedia.R;
import com.andreapetreti.subspedia.model.Serie;
import com.andreapetreti.subspedia.model.SerieTranslating;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SerieListAdapter extends RecyclerView.Adapter<SerieListAdapter.SerieViewHolder> implements Filterable {

    private static final int TYPE_SERIE = 0;
    private static final int TYPE_TRANSLATING_SERIE = 1;

    private LayoutInflater mInflater;
    private List<? extends Serie> mSerieList;
    private List<? extends Serie> mSerieFilteredList;

    private ItemClickListener mItemClickListener;

    public SerieListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    public Serie itemAt(int position) {
        return mSerieFilteredList.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        if(mSerieFilteredList != null) {
            Serie serie = mSerieFilteredList.get(position);
            if(serie instanceof SerieTranslating)
                return TYPE_TRANSLATING_SERIE;
            else
                return TYPE_SERIE;
        }
        return -1;
    }

    @NonNull
    @Override
    public SerieViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if(viewType == TYPE_SERIE) {
            View rootView = mInflater.inflate(R.layout.serie_item, viewGroup, false);
            return new SerieViewHolder(rootView);
        } else if(viewType == TYPE_TRANSLATING_SERIE) {
            View rootView = mInflater.inflate(R.layout.serie_translating_item, viewGroup, false);
            return  new SerieTranslatingViewHolder(rootView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull SerieViewHolder serieViewHolder, int i) {
        if(mSerieFilteredList != null) {
            // extended for translating series
            Serie serie = mSerieFilteredList.get(i);
            PicassoSingleton.getSharedInstance(mInflater.getContext()).load(serie.getLinkImage()).fit().centerCrop(Gravity.CENTER).into(serieViewHolder.mImageViewLogo);

            if(serieViewHolder instanceof SerieTranslatingViewHolder) {
                SerieTranslating tmp = (SerieTranslating) serie;

                serieViewHolder.txtTitle.setText(String.format(Locale.getDefault(), "%s - %dx%d",
                        tmp.getName(),
                        tmp.getEpisodeNumber(),
                        tmp.getSeasonNumber()));

                serieViewHolder.txtCaption.setText(serie.getStatus());
                ((SerieTranslatingViewHolder) serieViewHolder).mImageStatus.setImageResource(tmp.isRevisionStatus() ?
                                                                                R.drawable.ic_edit_black_24dp :
                                                                                R.drawable.ic_time_24dp);

            } else {
                serieViewHolder.mFavourite.setImageResource(serie.isFavorite() ? R.drawable.ic_star_orange_24dp : R.drawable.ic_star_border_orange_24dp);
                serieViewHolder.txtTitle.setText(serie.getName());
                serieViewHolder.txtCaption.setText(String.format(Locale.getDefault(), "%s - %d",
                        serie.getStatus().toUpperCase(),
                        serie.getYear()));
            }
        } else {
            // show empty message.
        }
    }

    @Override
    public int getItemCount() {
        if(mSerieFilteredList != null)
            return mSerieFilteredList.size();
        return 0;
    }

    public void setSeries(List<? extends Serie> series) {
        mSerieList = series;
        mSerieFilteredList = mSerieList;
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString().toLowerCase();
                FilterResults filterResults = new FilterResults();
                if(charString.isEmpty()) {
                    filterResults.values = mSerieList;
                } else {
                    List<Serie> filter = new ArrayList<>();
                    for(Serie serie : mSerieList)
                        if(serie.getName().toLowerCase().contains(charString))
                            filter.add(serie);
                    filterResults.values = filter;
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mSerieFilteredList = (ArrayList<? extends Serie>) results.values;
                notifyDataSetChanged();
            }
        };
    }


    class SerieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView mImageViewLogo;
        private TextView txtTitle;
        private TextView txtCaption;
        private ImageView mFavourite;

        SerieViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageViewLogo = itemView.findViewById(R.id.imageViewLogo);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtCaption = itemView.findViewById(R.id.txtCaption);
            mFavourite = itemView.findViewById(R.id.favoriteIcon);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(mItemClickListener != null)
                mItemClickListener.onItemClick(v, getAdapterPosition());
        }
    }

    class SerieTranslatingViewHolder extends SerieViewHolder {

        private ImageView mImageStatus;

        SerieTranslatingViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageStatus = itemView.findViewById(R.id.imgStatus);
        }
    }
}

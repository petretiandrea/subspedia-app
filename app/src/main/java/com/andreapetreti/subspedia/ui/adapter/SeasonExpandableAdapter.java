package com.andreapetreti.subspedia.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.andreapetreti.subspedia.R;
import com.andreapetreti.subspedia.model.Subtitle;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.models.ExpandableList;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Locale;

public class SeasonExpandableAdapter extends ExpandableRecyclerViewAdapter<SeasonExpandableAdapter.SeasonViewHolder, SeasonExpandableAdapter.SubtitlesViewHolder> {

    private LayoutInflater mInflater;

    public SeasonExpandableAdapter(Context context, List<? extends ExpandableGroup> groups) {
        super(groups);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public SeasonViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.season_item, parent, false);
        return new SeasonViewHolder(view);
    }

    @Override
    public SubtitlesViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.subtitle_item, parent, false);
        return new SubtitlesViewHolder(view);
    }

    @Override
    public void onBindChildViewHolder(SubtitlesViewHolder holder, int flatPosition, ExpandableGroup group, int childIndex) {
        if(group.getItems().get(childIndex) instanceof Subtitle) {
            Subtitle subtitle = (Subtitle) group.getItems().get(childIndex);
            holder.mSubtitleTitle.setText(String.format(Locale.getDefault(), "%d - %s",
                    subtitle.getEpisodeNumber(),
                    subtitle.getEpisodeTitle()));
        }
    }

    @Override
    public void onBindGroupViewHolder(SeasonViewHolder holder, int flatPosition, ExpandableGroup group) {
        holder.mSeasonTitle.setText(group.getTitle());
    }

    class SeasonViewHolder extends GroupViewHolder {

        TextView mSeasonTitle;
        private ImageView mCollapseExpand;
        private int mRotation;

        SeasonViewHolder(View itemView) {
            super(itemView);
            mSeasonTitle = itemView.findViewById(R.id.txtSeason);
            mCollapseExpand = itemView.findViewById(R.id.btnCollapse);
            mRotation = 0;
        }

        @Override
        public void expand() {
            super.expand();
            toggle();
        }

        @Override
        public void collapse() {
            super.collapse();
            toggle();
        }

        private void toggle() {
            mRotation = (mRotation == 0) ? 180 : 0;
            mCollapseExpand.animate().rotation(mRotation).setDuration(500).start();
        }
    }

    class SubtitlesViewHolder extends ChildViewHolder {

        TextView mSubtitleTitle;

        SubtitlesViewHolder(View itemView) {
            super(itemView);
            mSubtitleTitle = itemView.findViewById(R.id.txtSubtitle);

        }
    }

}

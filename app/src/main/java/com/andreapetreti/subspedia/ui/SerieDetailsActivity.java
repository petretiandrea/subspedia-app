package com.andreapetreti.subspedia.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import com.andreapetreti.android_utils.ui.LoadingBarMessage;
import com.andreapetreti.subspedia.AppExecutor;
import com.andreapetreti.subspedia.R;
import com.andreapetreti.subspedia.common.Resource;
import com.andreapetreti.subspedia.model.Serie;
import com.andreapetreti.subspedia.model.Subtitle;
import com.andreapetreti.subspedia.ui.adapter.SeasonExpandableAdapter;
import com.andreapetreti.subspedia.viewmodel.SubtitleViewModel;
import com.jaeger.library.StatusBarUtil;
import com.squareup.picasso.Cache;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SerieDetailsActivity extends AppCompatActivity {

    private static final String KEY_SERIE = "serie";
    private static final String TAG = SerieDetailsActivity.class.getName();

    private LoadingBarMessage mProgressMessage;
    private RecyclerView mRecyclerView;

    public static Intent obtainIntent(Context context, Serie serie) {
        Intent intent = new Intent(context, SerieDetailsActivity.class);
        intent.putExtra(KEY_SERIE, serie);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serie_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


      //  StatusBarUtil.setTransparent(this);

        /* Check for correct intent pass */
        if(getIntent().getExtras() == null) {
            finish();
            return;
        }

        Serie serie = Objects.requireNonNull(getIntent().getExtras()).getParcelable(KEY_SERIE);

        if(serie == null) {
            finish();
            return;
        }

        /* Init the UI */
        setTitle(serie.getName());
/*        ImageView extendImage = (ImageView) findViewById(R.id.imageView3);
        new Picasso.Builder(this)
                .memoryCache(Cache.NONE)
                .requestTransformer(Picasso.RequestTransformer.IDENTITY)
                .build()
                .load(serie.getLinkBannerImage())
                .fit()
                .centerCrop(Gravity.CENTER)
                .into(extendImage);*/

        List<ExpandableGroup<Subtitle>> groups = new ArrayList<>();

        mProgressMessage = findViewById(R.id.progressMessage);
        mProgressMessage.getProgressBar().setIndeterminate(true);
        mRecyclerView = findViewById(R.id.subtitlesRecyclerView);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        SubtitleViewModel viewModel = new SubtitleViewModel(getApplication());
        viewModel.getSubtitlesOf(serie.getIdSerie()).observe(this, listResource -> {

            if(listResource.status == Resource.Status.LOADING) {
                mProgressMessage.setVisibility(View.VISIBLE);
            }

            if(listResource.status == Resource.Status.SUCCESS) {
                int currentSeason = 1;
                if (listResource.data != null) {
                    for (int i = 0; i < listResource.data.size(); i++) {
                        if(listResource.data.get(i).getSeasonNumber() > (currentSeason - 1)) {
                            // add new season.
                            List<Subtitle> subs = new ArrayList<>();
                            for(Subtitle s : listResource.data)
                                if(s.getSeasonNumber() == currentSeason)
                                    subs.add(s);

                            groups.add(new ExpandableGroup<>(String.format(
                                    Locale.getDefault(),
                                    getString(R.string.season),
                                    currentSeason
                            ), subs));
                            currentSeason++;
                        }
                    }
                }

                mProgressMessage.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
                SeasonExpandableAdapter seasonExpandableAdapter = new SeasonExpandableAdapter(this, groups);
                mRecyclerView.setAdapter(seasonExpandableAdapter);
                seasonExpandableAdapter = null;
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

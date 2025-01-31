package com.andreapetreti.subspedia.model;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class SerieWithSubtitles {

    @Embedded
    private Serie mSerie;

    @Relation(parentColumn = "mIdSerie", entityColumn = "mIdSerie", entity = Subtitle.class)
    private List<Subtitle> mSubtitles;

    public Serie getSerie() {
        return mSerie;
    }

    public void setSerie(Serie serie) {
        mSerie = serie;
    }

    public List<Subtitle> getSubtitles() {
        return mSubtitles;
    }

    public void setSubtitles(List<Subtitle> subtitles) {
        mSubtitles = subtitles;
    }
}

package com.andreapetreti.subspedia.model;

import android.arch.persistence.room.Entity;

import com.google.gson.annotations.SerializedName;

@Entity
public class SerieTranslating extends Serie {

    /**
     * The season number
     */
    @SerializedName("num_stagione")
    private int mSeasonNumber;

    /**
     * The episode number
     */
    @SerializedName("num_episodio")
    private int mEpisodeNumber;

    public int getSeasonNumber() {
        return mSeasonNumber;
    }

    public int getEpisodeNumber() {
        return mEpisodeNumber;
    }

    public void setSeasonNumber(int seasonNumber) {
        mSeasonNumber = seasonNumber;
    }

    public void setEpisodeNumber(int episodeNumber) {
        mEpisodeNumber = episodeNumber;
    }

    @Override
    public String toString() {
        return "SerieTranslating{" +
                "mSeasonNumber=" + mSeasonNumber +
                ", mEpisodeNumber=" + mEpisodeNumber +
                "} " + super.toString();
    }
}

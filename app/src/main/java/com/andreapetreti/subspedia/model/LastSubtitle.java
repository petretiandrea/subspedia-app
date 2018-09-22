package com.andreapetreti.subspedia.model;

import android.arch.persistence.room.Entity;

import com.google.gson.annotations.SerializedName;

@Entity
public class LastSubtitle {

    @SerializedName("id_serie")
    private int mIdSerie;

    @SerializedName("num_stagione")
    private int mSeasonNumber;

    @SerializedName("num_episodio")
    private int mEpisodeNumber;

}

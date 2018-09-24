package com.andreapetreti.subspedia.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.andreapetreti.subspedia.model.Subtitle;

import java.util.List;

@Dao
public interface SubtitlesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(Subtitle subtitle);

    @Query("SELECT * FROM subtitle WHERE mIdSerie = :idSerie AND mEpisodeNumber = :epNumber AND mSeasonNumber = :season")
    LiveData<Subtitle> getSubtitle(int idSerie, int epNumber, int season);

    @Query("SELECT * FROM subtitle WHERE mIdSerie = :idSerie ORDER BY mSeasonNumber ASC")
    LiveData<List<Subtitle>> getSubtitlesOf(int idSerie);

    /*
    @Query("SELECT MAX(mSeasonNumber) FROM subtitle WHERE mIdSerie = :idSerie")
    int getSeaso(int idSerie);*/
}

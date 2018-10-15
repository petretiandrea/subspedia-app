package com.andreapetreti.subspedia.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.andreapetreti.subspedia.model.SerieWithSubtitles;
import com.andreapetreti.subspedia.model.Subtitle;
import com.andreapetreti.subspedia.model.SubtitleWithSerie;

import java.util.List;

@Dao
public interface SubtitlesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(Subtitle subtitle);

    @Delete
    void delete(Subtitle subtitle);

    @Query("SELECT * FROM subtitle WHERE mIdSerie = :idSerie AND mEpisodeNumber = :epNumber AND mSeasonNumber = :season")
    LiveData<Subtitle> getSubtitle(int idSerie, int epNumber, int season);

    @Query("SELECT subtitle.*, serie.mIdSerie AS s_mIdSerie, serie.mName AS s_mName, serie.mIdTVDB AS s_mIdTVDB, serie.mLink AS s_mLink, serie.mStatus AS s_mStatus, serie.mYear AS s_mYear, serie.mFavorite AS s_mFavorite" +
            " FROM subtitle, serie WHERE subtitle.mIdSerie = :idSerie AND serie.mIdSerie = :idSerie ORDER BY mSeasonNumber ASC")
    LiveData<List<SubtitleWithSerie>> getSubtitlesOf(int idSerie);

    @Query("SELECT subtitle.mIdSerie FROM subtitle ORDER BY last_write DESC")
    List<SerieWithSubtitles> getLRUSerieSubtitle();

    /*
    @Query("SELECT MAX(mSeasonNumber) FROM subtitle WHERE mIdSerie = :idSerie")
    int getSeaso(int idSerie);*/
}

package com.andreapetreti.subspedia.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RawQuery;

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

    /**
     * Load from database a list with all subtitles for the specified tv series.
     * The subtitles are ordered by Season.
     * @param idSerie Id of tv series
     * @return Return a live data that contains a list with all subtitles for the specified tv series.
     */
    @Query("SELECT subtitle.*, serie.mIdSerie AS s_mIdSerie, serie.mName AS s_mName, serie.mIdTVDB AS s_mIdTVDB, serie.mLink AS s_mLink, serie.mStatus AS s_mStatus, serie.mYear AS s_mYear, serie.mFavorite AS s_mFavorite" +
            " FROM subtitle, serie WHERE subtitle.mIdSerie = :idSerie AND serie.mIdSerie = :idSerie ORDER BY mSeasonNumber ASC")
    LiveData<List<SubtitleWithSerie>> getSubtitlesOf(int idSerie);

    /**
     * Delete last writed subtitles cached, in this implementation only 5 LRU series are preserved
     */
    @Query("DELETE FROM subtitle WHERE mIdSerie NOT IN (SELECT subtitle.mIdSerie AS preservSerie FROM subtitle GROUP BY subtitle.mIdSerie ORDER BY last_write DESC LIMIT 5)")
    void removeLRUSerieSubtitles();

    /*
    @Query("SELECT MAX(mSeasonNumber) FROM subtitle WHERE mIdSerie = :idSerie")
    int getSeaso(int idSerie);*/
}

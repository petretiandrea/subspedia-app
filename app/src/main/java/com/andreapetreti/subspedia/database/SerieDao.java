package com.andreapetreti.subspedia.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.andreapetreti.subspedia.model.Serie;

import java.util.List;

@Dao
public interface SerieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(Serie serie);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(Serie... serie);

    @Query("SELECT * FROM serie WHERE mIdSerie = :serieID")
    LiveData<Serie> getSerie(int serieID);

    @Query("SELECT * FROM serie WHERE mIdSerie = :serieID")
    Serie getSerieSync(int serieID);

    @Query("SELECT * FROM serie ORDER BY mName ASC")
    LiveData<List<Serie>> getAllSeries();

    @Query("SELECT * FROM serie WHERE mFavorite = 1 ORDER BY mName ASC")
    LiveData<List<Serie>> getFavoriteSeries();

    @Query("SELECT * FROM serie WHERE mFavorite = 1 ORDER BY mName ASC")
    List<Serie> getFavoriteSeriesSync();

    @Query("SELECT * FROM serie ORDER BY mName ASC")
    List<Serie> getAllSeriesSync();

    @Query("UPDATE serie SET mFavorite = :add WHERE mIdSerie = :idSerie")
    void setFavoriteSerie(int idSerie, boolean add);
}

package com.andreapetreti.subspedia.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.andreapetreti.subspedia.model.Serie;

import java.util.List;
import java.util.Optional;

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

    @Query("SELECT * FROM serie ORDER BY mName ASC")
    List<Serie> getAllSeriesSync();

    @Query("UPDATE serie SET mFavorite = :add WHERE mIdSerie = :idSerie")
    void setFavoriteSerie(int idSerie, boolean add);
}
